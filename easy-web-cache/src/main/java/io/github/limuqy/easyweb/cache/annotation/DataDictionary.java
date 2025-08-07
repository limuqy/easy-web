package io.github.limuqy.easyweb.cache.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.limuqy.easyweb.cache.handler.DataDictionarySerializerHandler;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@JacksonAnnotationsInside
//指定当前注解修饰的属性/方法使用具体哪个序列化类来序列化
@JsonSerialize(using = DataDictionarySerializerHandler.class)
public @interface DataDictionary {
    /**
     * 字典编码
     */
    String value() default "";

    /**
     * 字典编码
     */
    String dictCode() default "";

    /**
     * 字段名后缀
     */
    String suffix() default "Meaning";

    /**
     * 分割形字段， 如code1;code2;code3
     */
    String split() default "";
}
