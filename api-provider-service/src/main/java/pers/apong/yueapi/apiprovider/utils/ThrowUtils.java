package pers.apong.yueapi.apiprovider.utils;

import pers.apong.yueapi.apiprovider.common.ErrorCode;
import pers.apong.yueapi.apiprovider.exception.BusinessException;

/**
 * 异常工具类
 */
public class ThrowUtils {
    public static void throwIf(boolean b, ErrorCode code, String message) {
        if (!b) {
            return;
        }
        throw new BusinessException(code, message);
    }
}
