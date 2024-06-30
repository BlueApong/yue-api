package pers.apong.yueapi.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 *
 */
@Data
public class UserDto implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 接口调用 accesskey
     */
    private String accessKey;

    /**
     * 接口调用 secretKey
     */
    private String secretKey;

    private static final long serialVersionUID = 1L;
}
