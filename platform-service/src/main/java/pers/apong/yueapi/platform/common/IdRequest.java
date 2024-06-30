package pers.apong.yueapi.platform.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * id请求
 *
 * @author apong
 */
@Data
public class IdRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 多个 id
     */
    private List<Long> ids;

    private static final long serialVersionUID = 1L;
}
