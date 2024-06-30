package pers.apong.yueapi.platform.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 *
 * @TableName api_info
 */
@TableName(value ="api_info")
@Data
public class ApiInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 请求方法
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
     * 是否删除(0-未删, 1-已删)
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
