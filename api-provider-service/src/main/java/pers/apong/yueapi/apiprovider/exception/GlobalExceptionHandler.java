package pers.apong.yueapi.apiprovider.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.apong.yueapi.apiprovider.common.BaseResponse;
import pers.apong.yueapi.apiprovider.common.ErrorCode;
import pers.apong.yueapi.apiprovider.common.Result;


/**
 * 全局异常处理器
 *
 * @author apong
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return Result.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
