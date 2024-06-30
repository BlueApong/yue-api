package pers.apong.yueapi.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户调用接口信息
 *
 */
@Data
public class UserApiInvocationDto implements Serializable {
    /**
     * 接口 id
     */
    private Long apiInfoId;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户调用接口密钥
     */
    private String secretKey;

    /**
     * 接口验证失败错误信息
     */
    private String errorMessage;

    public static UserApiInvocationDto ofError(String errorMessage) {
        UserApiInvocationDto userApiInvocationDto = new UserApiInvocationDto();
        userApiInvocationDto.setErrorMessage(errorMessage);
        return userApiInvocationDto;
    }

    private static final long serialVersionUID = 1L;
}
