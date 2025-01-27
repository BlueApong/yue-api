package pers.apong.yueapi.platform.model.dto.api_info;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @author apong
 */
@Data
public class ApiInfoInvokeRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;
}
