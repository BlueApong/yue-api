package pers.apong.yueapi.platform.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户已申请接口信息
 */
@Data
public class UserAppliedApiVO implements Serializable {
    /**
     * 接口id
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
     * 接口剩余次数
     */
    private Integer leftNum;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}
