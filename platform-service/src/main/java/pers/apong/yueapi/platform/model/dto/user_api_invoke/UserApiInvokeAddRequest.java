package pers.apong.yueapi.platform.model.dto.user_api_invoke;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author apong
 */
@Data
public class UserApiInvokeAddRequest implements Serializable {

    private static final long serialVersionUID = 408502116946147283L;

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
}
