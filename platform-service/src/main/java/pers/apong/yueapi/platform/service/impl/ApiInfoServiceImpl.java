package pers.apong.yueapi.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.apong.yueapi.common.utils.SignUtils;
import pers.apong.yueapi.platform.common.ErrorCode;
import pers.apong.yueapi.platform.common.UserContext;
import pers.apong.yueapi.platform.exception.BusinessException;
import pers.apong.yueapi.platform.mapper.ApiInfoMapper;
import pers.apong.yueapi.platform.model.domain.ApiInfo;
import pers.apong.yueapi.platform.model.domain.User;
import pers.apong.yueapi.platform.model.domain.UserApiInvoke;
import pers.apong.yueapi.platform.model.enums.ApiInfoStatusEnum;
import pers.apong.yueapi.platform.service.ApiInfoService;
import pers.apong.yueapi.platform.service.UserApiInvokeService;
import pers.apong.yueapi.platform.service.UserService;
import pers.apong.yueapi.platform.utils.EncryptedRequest;
import pers.apong.yueapi.platform.utils.JsonParamsBuilder;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apong
 */
@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo>
        implements ApiInfoService {

    @Resource
    private UserService userService;

    @Resource
    private UserApiInvokeService userApiInvokeService;

    @Override
    public ApiInfo validate(ApiInfo apiInfo, boolean isAdd) {
        if (apiInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = apiInfo.getName();
        String description = apiInfo.getDescription();
        String url = apiInfo.getUrl();
        String method = apiInfo.getMethod();
        // 更新校验
        if (StringUtils.isAnyBlank(name, description, url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 方法必须为可用的Http方法
        try {
            Method.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求方法定义无效");
        }
        String requestParams = apiInfo.getRequestParams();
        // 请求参数必须满足JSON数组类型
        if (StringUtils.isNotBlank(requestParams) && !JSONUtil.isTypeJSONArray(requestParams)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数定义无效");
        }
        String responseParams = apiInfo.getResponseParams();
        // 响应参数必须满足JSON数组类型
        if (StringUtils.isNotBlank(requestParams) && !JSONUtil.isTypeJSONArray(responseParams)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "响应参数定义无效");
        }
        // 接口地址合法
        if (!HttpUtil.isHttp(url) && !HttpUtil.isHttps(url)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "url定义无效");
        }
        String normalizedUrl = URLUtil.normalize(url);
        apiInfo.setUrl(normalizedUrl);
        // 验证接口是否正常
        boolean isWorking = validateApiWorking(url, method, requestParams);
        if (!isWorking) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用测试异常");
        }
        // 接口地址脱敏，获取网关拦截地址
        String gatewayUrl = getGatewayUrl(normalizedUrl, name);
        apiInfo.setGatewayUrl(gatewayUrl);
        if (apiInfo.getId() != null) {
            ApiInfo oldApiInfo = getById(apiInfo.getId());
            if (!oldApiInfo.getUrl().equals(url)) {
                // 是否重复定义接口
                int count = query().eq("url", url).eq("method", method).count();
                if (count > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口重复冲突");
                }
            }
        }
        // 新增校验
        if (isAdd) {
            // todo 拆分新增和更新逻辑，抽离出公共逻辑
            // 是否重复定义接口
            int count = query().eq("url", url).eq("method", method).count();
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口重复冲突");
            }
        }
        return apiInfo;
    }

    /**
     * 接口地址脱敏，获取网关拦截地址
     *
     * @param normalizedUrl
     * @param name
     * @return
     */
    private static String getGatewayUrl(String normalizedUrl, String name) {
        // 1. 匹配 host 和 path 部分
        String regex = "https?://([^/]+)/(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(normalizedUrl);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口上传异常");
        }
        String host = matcher.group(1);
        String path = matcher.group(2);
        // 2. 取出 host，判断是否为内部服务接口
        // todo 优化为配置，上线需修改
        final String INNER_API_HOST = "localhost:8123";
        final String GATEWAY_URI = "http://localhost:8090/";
        if (!INNER_API_HOST.equals(host)) {
            path = String.format("%s/%s", name, path);
        }
        String gatewayUrl = GATEWAY_URI + path;
        return gatewayUrl;
    }

    /**
     * 测试接口是否可以正常使用
     *
     * @param url
     * @param method
     * @param requestParams
     * @return
     */
    @Override
    public boolean validateApiWorking(String url, String method, String requestParams) {
        // 调用接口
        try (HttpResponse httpResponse = this.invokeApi(url, method, requestParams, true)) {
            if (httpResponse == null || !httpResponse.isOk()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public HttpResponse invokeApi(String url, String method, String requestParams, boolean isTest) {
        // 获取请求示例
        JsonParamsBuilder jsonParamsBuilder = new JsonParamsBuilder();
        String requestBody;
        if (isTest) {
            Map<String, Object> requestExample = jsonParamsBuilder.getDefaultMap(requestParams);
            requestBody = JSONUtil.toJsonStr(requestExample);
        } else {
            requestBody = requestParams;
        }
        HttpRequest request = HttpUtil.createRequest(Method.valueOf(method.toUpperCase()), url);
        return request.addHeaders(getHeaderMap(requestBody)).body(requestBody).execute();
    }

    /**
     * 获取身份请求头信息
     *
     * @param body
     * @return
     */
    private Map<String, String> getHeaderMap(String body) {
        User loginUser = UserContext.getLoginUser();
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送私钥
        //hashMap.put("secretKey", secretKey);
        hashMap.put("body", body);
        // 随机性参数
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 签名
        hashMap.put("sign", SignUtils.genSign(body, secretKey));
        return hashMap;
    }

    @Override
    public ApiInfo verifyApiAvailable(String url, String method) {
        ApiInfo apiInfo = query().eq("url", url).eq("method", method).one();
        if (apiInfo == null) {
            return null;
        }
        final int offlineValue = ApiInfoStatusEnum.OFFLINE.getValue();
        Integer status = Optional.ofNullable(apiInfo.getStatus()).orElse(offlineValue);
        if (!ApiInfoStatusEnum.ONLINE.equalsValue(status)) {
            return null;
        }
        return apiInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyApiInfo(Long apiId) {
        // 0. 接口是否存在
        ApiInfo apiInfo = this.getById(apiId);
        if (apiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        User loginUser = UserContext.getLoginUser();
        // 是否已经申请过
        UserApiInvoke userApiInvoke = userApiInvokeService.query()
                .eq("userId", loginUser.getId())
                .eq("apiInfoId", apiId)
                .one();
        final int LOWEST_APPLY_NUM = 10;
        final int FREE_NUM = 100;
        if (userApiInvoke != null) {
            if (userApiInvoke.getLeftNum() > LOWEST_APPLY_NUM) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "还有较多剩余次数，不予申请");
            }
            Integer leftNum = userApiInvoke.getLeftNum();
            // 添加使用次数
            userApiInvoke.setLeftNum(leftNum + FREE_NUM);
        } else {
            // 为用户分配ak / sk
            if (StringUtils.isAnyBlank(loginUser.getAccessKey(), loginUser.getSecretKey())) {
                assignAkSk(loginUser);
            }
            // 新增用户接口调用关系
            userApiInvoke = new UserApiInvoke();
            userApiInvoke.setUserId(loginUser.getId());
            userApiInvoke.setApiInfoId(apiId);
            // todo 根据不同用户分配不同次数
            userApiInvoke.setLeftNum(FREE_NUM);
        }
        System.out.println("test devtools");
        return userApiInvokeService.save(userApiInvoke);
    }

    @Override
    public User assignAkSk(User user) {
        String accessKey = UUID.randomUUID().toString();
        String secretKey = UUID.randomUUID().toString();
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        boolean b = userService.updateById(user);
        if (!b) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "密钥分配异常");
        }
        return user;
    }

    @Override
    public String invokeOnline(Long id, String userRequestParams) {
        // 校验请求参数结构
        userRequestParams = Optional.ofNullable(userRequestParams).orElse("{}");
        if (StringUtils.isNotBlank(userRequestParams) && !JSONUtil.isTypeJSONObject(userRequestParams)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取接口
        ApiInfo apiInfo = this.getById(id);
        if (apiInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 获取用户密钥，创建sdk对象
        User loginUser = UserContext.getLoginUser();
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        EncryptedRequest encryptedRequest = new EncryptedRequest(accessKey, secretKey);
        try {
            return encryptedRequest.get(apiInfo.getGatewayUrl(), userRequestParams);
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }
}




