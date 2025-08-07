package io.github.limuqy.easyweb.core.util;

import io.github.limuqy.easyweb.core.exception.BusinessException;

public class ObjectUtil extends cn.hutool.core.util.ObjectUtil {

    public static void isEmpty(Object object, String message) {
        if (isEmpty(object)) {
            throw new BusinessException(message);
        }
    }

    @SafeVarargs
    public static <T> T getNotEmpty(T... ts) {
        for (T t : ts) {
            if (t instanceof String) {
                if (isNotNull(t)) {
                    return t;
                }
            }
            if (isNotEmpty(t)) {
                return t;
            }
        }
        return null;
    }

    @SafeVarargs
    public static <T> T getNotEmptyElse(T... ts) {
        T t = getNotEmpty(ts);
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

}
