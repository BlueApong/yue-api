package pers.apong.yueapi.platform.model.dto.user_api_invoke;

import pers.apong.yueapi.platform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author apong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserApiInvokeQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 调用者 id
     */
    private Long userId;

    /**
     * 接口 id
     */
    private Long apiInfoId;

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
