package com.catalpa.common.core.exception;

/**
 * @author zjwu
 * 业务异常类
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -695542791928644578L;
    private int status;
    private Object data;

    public BusinessException(String message) {
        this(message, 400);
    }

    public BusinessException(String message, int status) {
        super(message);
        this.status = 400;
        this.status = status;
    }

    public BusinessException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = 400;
        this.status = status;
    }

    public BusinessException(String message, Throwable cause, Object data) {
        super(message, cause);
        this.status = 400;
        this.data = data;
    }

    public BusinessException(String message, Throwable cause, int status, Object data) {
        super(message, cause);
        this.status = 400;
        this.status = status;
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public int getStatus() {
        return this.status;
    }
}
