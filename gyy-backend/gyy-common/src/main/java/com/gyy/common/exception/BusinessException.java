package com.gyy.common.exception;

import com.gyy.common.result.ErrorCode;

/**
 * 业务异常：service 层抛出，由 GlobalExceptionHandler 统一转为 Result。
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
