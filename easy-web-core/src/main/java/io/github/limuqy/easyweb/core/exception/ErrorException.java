package io.github.limuqy.easyweb.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorException extends RuntimeException {
    private String msg;

    public ErrorException(Throwable cause, String message, Object... params) {
        super(String.format(message, params), cause);
        this.msg = String.format(message, params);
    }

    public ErrorException(String message, Object... params) {
        super(String.format(message, params));
        this.msg = String.format(message, params);
    }

}
