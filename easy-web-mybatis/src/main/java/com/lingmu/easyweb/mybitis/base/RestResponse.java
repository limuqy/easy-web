package com.lingmu.easyweb.mybitis.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingmu.easyweb.core.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collection;

@Getter
@Setter
public class RestResponse<T> {

    public static final Integer OK = 200;
    public static final Integer FAIL = 500;

    private Integer code = OK;

    private String message;

    private T data;

    public static RestResponse<Object> ok() {
        return new RestResponse<>();
    }

    public static <T> RestResponse<T> ok(T data) {
        return new RestResponse<T>().data(data);
    }

    public static <T> RestResponse<PageResult<Collection<T>>> ok(Page<T> page) {
        return PageResult.build(page).response();
    }

    public static <T> RestResponse<PageResult<Collection<T>>> ok(Collection<T> data, Number total) {
        return PageResult.build(data, total).response();
    }

    public static <T> RestResponse<T> fail(String message) {
        return fail(FAIL, message);
    }

    public static <T> RestResponse<T> fail(int code, String message) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static RestResponse<Object> fail(HttpStatus status, String message) {
        return fail(status.value(), message);
    }

    public RestResponse<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public RestResponse<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public RestResponse<T> data(T data) {
        this.data = data;
        return this;
    }

    public RestResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public Boolean checkSuccess() {
        if (code == null) {
            return false;
        }
        return OK.equals(code);
    }

    public Boolean checkFail() {
        return !checkSuccess();
    }

    public void checkFailException() {
        if (checkSuccess()) {
            return;
        }
        throw new BusinessException(this.message);
    }

}
