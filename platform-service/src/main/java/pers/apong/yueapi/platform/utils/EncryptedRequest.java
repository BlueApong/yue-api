package pers.apong.yueapi.platform.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import pers.apong.yueapi.common.utils.SignUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 携带加密用户信息的请求
 */
public class EncryptedRequest {
    /**
     * 公钥
     */
    private final String accessKey;

    /**
     * 私钥
     */
    private final String secretKey;

    /**
     * 路径请求前缀
     */
    private String prefix = "";

    public EncryptedRequest(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * GET 请求
     *
     * @param url
     * @param params
     * @return
     */
    public String get(String url, String params) throws UnsupportedEncodingException {
        return request(joinQueryParams(url, params), Method.GET, params);
    }

    private String joinQueryParams(String url, String params) {
        if (StrUtil.isBlank(params)) {
            return url;
        }
        if (!JSONUtil.isTypeJSONObject(params)) {
            return url;
        }
        Map<String, Object> paramMap = JSONUtil.toBean(params, Map.class);
        if (paramMap.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        String query = URLUtil.buildQuery(paramMap, StandardCharsets.UTF_8);
        sb.append(query);
        return sb.toString();
    }

    /**
     * POST 请求
     *
     * @param url
     * @param body
     * @return
     */
    public String post(String url, String body) throws UnsupportedEncodingException {
        return request(url, Method.POST, body);
    }

    /**
     * POST 请求
     *
     * @param url
     * @param method
     * @param body
     * @return
     */
    public String request(String url, Method method, String body) throws UnsupportedEncodingException {
        HttpRequest request = HttpUtil.createRequest(method, this.prefix + url);
        HttpResponse httpResponse = request.addHeaders(getHeaderMap(body)).body(body).execute();
        String result = httpResponse.body();
        httpResponse.close();
        return result;
    }

    /**
     * 获取身份加密信息请求头
     *
     * @param body
     * @return
     */
    private Map<String, String> getHeaderMap(String body) throws UnsupportedEncodingException {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        String encodedBody = URLEncoder.encode(body, "utf-8");
        hashMap.put("body", encodedBody);
        // 随机性参数
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 签名
        hashMap.put("sign", SignUtils.genSign(encodedBody, secretKey));
        return hashMap;
    }
}
