package com.leqiwl.novel.common.exception;

import com.leqiwl.novel.common.enums.ApiErrorCodeEnum;
import lombok.NoArgsConstructor;

/**
 * API预设异常
 * <p>
 * 使用预设的错误信息，即ApiErrorCodeEnum，构造异常，方便传递固定错误信息。
 */
@NoArgsConstructor
public class ApiPresetException extends RuntimeException {

    /**
     * 错误消息枚举
     */
    private ApiErrorCodeEnum errorEnum;

    public ApiErrorCodeEnum getErrorEnum() {
        return errorEnum;
    }


    public void setErrorEnum(ApiErrorCodeEnum errorEnum) {
        this.errorEnum = errorEnum;
    }

    public ApiPresetException(ApiErrorCodeEnum errorEnum) {
        super(errorEnum.getStatusName());
        this.errorEnum = errorEnum;
    }

    public ApiPresetException(String message, ApiErrorCodeEnum errorEnum) {
        super(message);
        this.errorEnum = errorEnum;
    }

    public ApiPresetException(String message, Throwable cause, ApiErrorCodeEnum errorEnum) {
        super(message, cause);
        this.errorEnum = errorEnum;
    }

    public ApiPresetException(Throwable cause, ApiErrorCodeEnum errorEnum) {
        super(cause);
        this.errorEnum = errorEnum;
    }
    public ApiPresetException(Throwable cause) {
        super(cause);
    }

    public ApiPresetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ApiErrorCodeEnum errorEnum) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorEnum = errorEnum;
    }

    @Override
    public String toString() {
        return "[errorEnum=" + errorEnum + "]\r\n" + super.toString();
    }
}
