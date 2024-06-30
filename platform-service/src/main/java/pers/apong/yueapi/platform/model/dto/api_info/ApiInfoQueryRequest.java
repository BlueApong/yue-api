package pers.apong.yueapi.platform.model.dto.api_info;

import pers.apong.yueapi.platform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口查询请求
 *
 * @author apong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiInfoQueryRequest extends PageRequest implements Serializable {
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
     * 网关拦截虚拟接口地址
     */
    private String gatewayUrl;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 开启状态：0-关闭，1-开启
     */
    private Integer status;

    /**
     * 接口创建人
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
