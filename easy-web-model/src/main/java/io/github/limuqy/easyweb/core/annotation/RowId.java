package io.github.limuqy.easyweb.core.annotation;

import io.github.limuqy.easyweb.model.mybatis.BaseEntity;

import java.lang.annotation.*;

/**
 * 通用自动加密ID字段
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface RowId {
    Class<?> value() default BaseEntity.class;

    String tableName() default "";
}