package pers.apong.yueapi.platform.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 */
@Data
public class ApiInfoVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 唯一key，用作sdk调用方法唯一名称
     */
    private String calledKey;

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
     * 网关拦截虚拟接口地址
     */
    private String gatewayUrl;

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

    /**
     * 开启状态：0-关闭，1-开启
     */
    private Integer status;

    /**
     * 接口创建人
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 请求示例
     */
    private String requestExample;

    private static final long serialVersionUID = 1L;
}
