package org.allen.ins.underwriting.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    /**
     * 异常状态码
     */
    private final static Integer HTTP_INTERNAL_ERROR = 500;

    private Integer code;

    public BusinessException() {
        super();
        this.code = HTTP_INTERNAL_ERROR; // 默认500状态码
    }

    public BusinessException(String message) {
        super(message);
        this.code = HTTP_INTERNAL_ERROR;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = HTTP_INTERNAL_ERROR;
    }

    public BusinessException(Throwable cause) {
        super(cause);
        this.code = HTTP_INTERNAL_ERROR;
    }

    protected BusinessException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = HTTP_INTERNAL_ERROR;
    }

    /**
     * 自定义状态码+异常信息（最常用，比如400参数错、404资源错）
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 自定义状态码+异常信息+根因（排查问题用）
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
