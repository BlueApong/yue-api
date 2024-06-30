package pers.apong.yueapi.platform.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除请求
 *
 * @author apong
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 多个id
     */
    private List<Long> ids;

    private static final long serialVersionUID = 1L;
}
