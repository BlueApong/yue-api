package pers.apong.yueapi.platform.utils;

import cn.hutool.json.JSONUtil;

import java.util.*;

/**
 * 根据请求参数（json约定）生成参数map
 */
public class JsonParamsBuilder {
    private final Map<String, Object> JSON_JAVA_MAP = new HashMap<String, Object>() {{
            put("string", "");
            put("number", 0);
            put("boolean", true);
            put("Array", new Object[] {});
            // 枚举类需要填写 enumValue: [male, female]，默认选中第一个
            put("enum", 0);
    }};

    /**
     * 根据请求参数获取默认请求体
     *
     * @param jsonParamsArray
     * @return
     */
    public Map<String, Object> getDefaultMap(String jsonParamsArray) {
        if (!JSONUtil.isTypeJSONArray(jsonParamsArray)) {
            return null;
        }
        List<JsonParamData> jsonParamDataList = JSONUtil.toList(jsonParamsArray, JsonParamData.class);
        Map<String, Object> paramsMap = new HashMap<>(jsonParamDataList.size());
        for (JsonParamData jsonParam : jsonParamDataList) {
            String paramName = jsonParam.getName();
            String paramType = jsonParam.getType();
            Object defaultValue = JSON_JAVA_MAP.get(paramType);
            if ("enum".equals(paramType)) {
                List<Object> enumValues = jsonParam.getEnumValues();
                paramsMap.put(paramName, enumValues.get((Integer) defaultValue));
            } else {
                paramsMap.put(paramName, defaultValue);
            }
        }
        return paramsMap;
    }
}
