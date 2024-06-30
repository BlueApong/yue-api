package pers.apong.yueapi.gateway.model;

import lombok.Data;
import org.springframework.util.MultiValueMap;

/**
 * 网络请求体
 */
@Data
public class AccessRecord {
    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 目标服务地址
     */
    private String targetUri;

    /**
     * 远程请求ip
     */
    private String remoteAddress;

    /**
     * GET 请求参数
     */
    private MultiValueMap<String, String> params;

    /**
     * POST请求体 body
     */
    private String body;
}
