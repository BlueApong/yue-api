package pers.apong.yueapi.platform.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 *
 * @author apong
 */
public enum ApiInfoStatusEnum {

    OFFLINE("下线", 0),
    ONLINE("上线", 1)
    ;

    private final String text;

    private final int value;

    ApiInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 值匹配
     *
     * @param value
     * @return
     */
    public boolean equalsValue(int value) {
        return this.value == value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
