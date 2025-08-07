package io.github.limuqy.easyweb.core.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String msg;

    public BusinessException(Throwable cause) {
        super(cause);
        this.msg = "系统异常";
    }

    public BusinessException(String message, Object... params) {
        super(String.format(message, params));
        this.msg = String.format(message, params);
    }
}
