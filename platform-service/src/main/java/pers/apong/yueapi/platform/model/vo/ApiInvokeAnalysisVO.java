package pers.apong.yueapi.platform.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口调用数据分析
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiInvokeAnalysisVO extends ApiInfoVO{
    /**
     * 调用次数
     */
    private Integer totalNum;
}
