package io.github.limuqy.easyweb.core.annotation;

import io.github.limuqy.easyweb.model.mybatis.BaseEntity;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RowIdEntity {
    Class<?> value() default BaseEntity.class;

    String tableName() default "";
}
