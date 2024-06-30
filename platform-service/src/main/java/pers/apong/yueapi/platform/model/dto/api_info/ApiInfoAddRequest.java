package pers.apong.yueapi.platform.model.dto.api_info;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口创建请求
 */
@Data
public class ApiInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 真实接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 请求参数
     * {"username": "string", "age":"number"}
     */
    private String requestParams;

    /**
     * 响应参数
     */
    private String responseParams;
}
