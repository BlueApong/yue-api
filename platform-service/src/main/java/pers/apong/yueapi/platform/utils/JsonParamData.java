package pers.apong.yueapi.platform.utils;

import lombok.Data;

import java.util.List;

@Data
public class JsonParamData {
    /**
     * 参数名
     */
    private String name;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 枚举值
     */
    private List<Object> enumValues;
}
