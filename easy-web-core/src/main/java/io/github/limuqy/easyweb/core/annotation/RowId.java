package io.github.limuqy.easyweb.core.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.limuqy.easyweb.core.handler.RowIdDeserializeHandler;
import io.github.limuqy.easyweb.core.handler.RowIdSerializerHandler;
import io.github.limuqy.easyweb.model.mybatis.BaseEntity;

import java.lang.annotation.*;

/**
 * 通用自动加密ID字段
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Documented
//让jackson的注解拦截器（com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector）能发现当前注解
@JacksonAnnotationsInside
//指定当前注解修饰的属性/方法使用具体哪个序列化类来序列化
@JsonSerialize(using = RowIdSerializerHandler.class)
@JsonDeserialize(using = RowIdDeserializeHandler.class)
public @interface RowId {
    Class<?> value() default BaseEntity.class;

    String tableName() default "";
}