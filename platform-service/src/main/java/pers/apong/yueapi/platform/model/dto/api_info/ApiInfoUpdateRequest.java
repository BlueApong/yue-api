package pers.apong.yueapi.platform.model.dto.api_info;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author apong
 */
@Data
public class ApiInfoUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

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
     */
    private String requestParams;

    /**
     * 响应参数
     */
    private String responseParams;

    private static final long serialVersionUID = 1L;
}
