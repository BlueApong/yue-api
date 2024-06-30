package pers.apong.yueapi.gateway.filters;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import pers.apong.yueapi.gateway.model.AccessRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

/**
 * 日志全局过滤器
 *
 * @author apong
 */
@Slf4j
public class LogGlobalFilter implements GlobalFilter, Ordered {
    private static final String START_TIME = "startTime";
    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        // 请求路径
        String path = request.getPath().pathWithinApplication().value();
        // 请求方法
        HttpMethod method = request.getMethod();
        // 路由服务地址
        URI targetUri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        // 设置startTime
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        // 获取请求地址
        InetSocketAddress remoteAddress = request.getRemoteAddress();

        AccessRecord accessRecord = new AccessRecord();
        accessRecord.setPath(path);
        accessRecord.setMethod(method.name());
        accessRecord.setTargetUri(targetUri.toString());
        accessRecord.setRemoteAddress(remoteAddress.toString());
        accessRecord.setParams(request.getQueryParams());
        try {
            // 解析，重放POST请求体 JSON
            if (method == HttpMethod.POST) {
                Mono<Void> voidMono = readBody(exchange, chain, accessRecord);
                printRequestInfo(accessRecord);
                return thenPrintResponse(voidMono, exchange, accessRecord);
            }
        } catch (Exception e) {
            log.error("POST body 读取发生异常");
        }
        printRequestInfo(accessRecord);
        return thenPrintResponse(chain.filter(exchange), exchange, accessRecord);
    }

    /**
     * 序号越大，执行优先级越低，回调优先级越高（栈）
     *
     * @return
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    /**
     * 异步打印响应结果
     *
     * @param mono
     * @param exchange
     * @param accessRecord
     * @return
     */
    private Mono<Void> thenPrintResponse(Mono<Void> mono, ServerWebExchange exchange, AccessRecord accessRecord) {
        return mono.then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                log.info("request end, path: {}, method: {}, 转发地址: {}, ip: {}, status: {}, cost: {}",
                        accessRecord.getPath(), accessRecord.getMethod(), accessRecord.getTargetUri(),
                        accessRecord.getRemoteAddress(), exchange.getResponse().getRawStatusCode(),
                        (System.currentTimeMillis() - startTime) + "ms");
            }
        }));
    }

    private Mono<Void> readBody(ServerWebExchange exchange, GatewayFilterChain chain, AccessRecord accessRecord) {
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                DataBufferUtils.retain(buffer);
                return Mono.just(buffer);
            });
            // 重写请求体,因为请求体数据只能被消费一次
            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }
            };
            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            return ServerRequest.create(mutatedExchange, messageReaders)
                    .bodyToMono(String.class)
                    .doOnNext((body) -> accessRecord.setBody(body))
                    .then(chain.filter(mutatedExchange));
        });
    }

    /**
     * 打印请求日志
     *
     * @param accessRecord
     */
    private void printRequestInfo(AccessRecord accessRecord) {
        log.info("request start, path: {}, method: {}, 转发地址: {}, ip: {}, params: {}, body: {}",
                accessRecord.getPath(), accessRecord.getMethod(), accessRecord.getTargetUri(),
                accessRecord.getRemoteAddress(), accessRecord.getParams(), accessRecord.getBody());
    }
}

