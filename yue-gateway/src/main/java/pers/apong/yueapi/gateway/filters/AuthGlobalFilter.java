package pers.apong.yueapi.gateway.filters;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pers.apong.yueapi.common.dto.UserApiInvocationDto;
import pers.apong.yueapi.common.client.UserApiClient;
import pers.apong.yueapi.common.utils.SignUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Map;


/**
 * 鉴权全局过滤器
 *
 * @author apong
 */
@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @DubboReference
    private UserApiClient userApiClient;

    private final String CURRENT_SYSTEM_HOST = "http://localhost:8090";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 校验权限，ak sk
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = Optional.ofNullable(headers.getFirst("nonce")).orElse("-1");
        String timestamp = Optional.ofNullable(headers.getFirst("timestamp")).orElse("0");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 请求有效性
        // todo 用 redis 存储随机数，不能重复使用一个随机数
        if (Long.parseLong(nonce) > 10000L) {
            return handleResponseComplete(chain, exchange, HttpStatus.FORBIDDEN);
        }
        // 验证是否过期，5分钟
        final Duration expireTime = Duration.ofMinutes(5);
        Instant requestTime = Instant.ofEpochSecond(Long.parseLong(timestamp));
        if (expireTime.compareTo(Duration.between(requestTime, Instant.now())) <= 0) {
            return handleResponseComplete(chain, exchange, HttpStatus.FORBIDDEN);
        }
        // 业务有效性
        String method = request.getMethodValue();
        String url = CURRENT_SYSTEM_HOST + request.getPath();
        if(StrUtil.isBlank(url)) {
            return handleResponseComplete(chain, exchange, HttpStatus.NOT_FOUND);
        }
        UserApiInvocationDto userApiInvocationDto =
                userApiClient.validate(url, method, accessKey);
        if (userApiInvocationDto == null) {
            return handleResponseComplete(chain, exchange, HttpStatus.FORBIDDEN);
        }
        String errorMessage = userApiInvocationDto.getErrorMessage();
        if (StrUtil.isNotBlank(errorMessage)) {
            return handleResponseComplete(chain, exchange, HttpStatus.FORBIDDEN);
        }
        // 验证签名
        String secretKey = userApiInvocationDto.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (!serverSign.equals(sign)) {
            return handleResponseComplete(chain, exchange, HttpStatus.FORBIDDEN);
        }
        // 调用成功，统计次数
        return handleResponse(exchange, chain,userApiInvocationDto.getApiInfoId(), userApiInvocationDto.getUserId());
    }

    @Override
    public int getOrder() {
        return -2;
    }

    /**
     * 获取响应结果，打印响应日志
     *
     * @param exchange        exchange
     * @param chain           chain
     * @param apiInfoId       接口ID
     * @param userId          用户ID
     * @return 获取响应结果
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long apiInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 取出响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode != HttpStatus.OK) {
                //降级处理返回数据
                return chain.filter(exchange);
            }
            // 解析响应结果，统计接口调用次数
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.map(dataBuffer -> {
                            // 读取body内容
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            //响应数据
                            try {
                                String responseData = new String(content, StandardCharsets.UTF_8);
                                Map<String, Object> responseMap = JSONUtil.toBean(responseData, Map.class);
                                Integer code = (Integer) responseMap.get("code");
                                if (0 == code) {
                                    userApiClient.countInvacation(apiInfoId, userId);
                                }
                                log.info("响应结果：" + responseMap);
                            } catch (Exception e) {
                                log.info("接口响应数据有误");
                            }
                            return bufferFactory.wrap(content);
                        }));
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        } catch (Exception e) {
            log.error("响应处理异常", e);
            return chain.filter(exchange);
        }
    }

    /**
     * 结束响应
     *
     * @param chain
     * @param exchange
     * @param httpStatus
     * @return
     */
    private Mono<Void> handleResponseComplete(GatewayFilterChain chain, ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
