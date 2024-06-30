package pers.apong.yueapi.platform.model.dto.user_api_invoke;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author apong
 */
@Data
public class UserApiInvokeUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;

    /**
     * 使用状态：0-正常，1-禁用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
