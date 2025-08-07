package com.lingmu.easyweb.core.util;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.func.Func1;
import com.lingmu.easyweb.core.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {

    public static List<Field> getEntityClassField(Class<?> clazz) {
        if (clazz == null) {
            return new ArrayList<>();
        }
        List<Field> fieldList = ListUtil.toList(clazz.getDeclaredFields());
        superClassField(clazz, fieldList);
        return fieldList;
    }

    public static void superClassField(Class<?> superclass, List<Field> fieldList) {
        Class<?> superclassSuperclass = superclass.getSuperclass();
        if (superclassSuperclass != null) {
            List<Field> list = ListUtil.toList(superclassSuperclass.getDeclaredFields()).stream()
                    .filter(e -> fieldList.stream().noneMatch(f -> f.getName().equals(e.getName())))
                    .collect(Collectors.toList());
            fieldList.addAll(list);
            superClassField(superclassSuperclass, fieldList);
        }
    }

    /**
     * 获取指定类及其所有超类（父类）的字段列表。
     *
     * @param cls 指定的类
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> cls) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = cls;

        while (currentClass != null) {
            // 添加当前类声明的所有字段
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            // 移至超类
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    /**
     * 给对象字段判空并报错
     *
     * @param beanName 对象名称
     * @param obj      对象
     * @param fields   要判空的字段名（不传判空所有字段）
     */
    @SafeVarargs
    public static <T> void isBeanFieldNotEmpty(String beanName, T obj, Func1<T, ?>... fields) {
        isBeanFieldNotEmpty(beanName, obj, Arrays.stream(fields).map(LambdaUtil::getFieldName).collect(Collectors.toList()));
    }

    /**
     * 给对象字段判空并报错
     *
     * @param beanName 对象名称
     * @param obj      对象
     * @param fields   要判空的字段名（不传判空所有字段）
     */
    public static void isBeanFieldNotEmpty(String beanName, Object obj, String... fields) {
        isBeanFieldNotEmpty(beanName, obj, Arrays.asList(fields));
    }

    /**
     * 给对象字段判空并报错
     *
     * @param beanName 对象名称
     * @param obj      对象
     * @param fields   要判空的字段名（不传判空所有字段）
     */
    public static void isBeanFieldNotEmpty(String beanName, Object obj, List<String> fields) {
        if (Objects.isNull(obj)) {
            return;
        }
        Class<?> clazz = obj.getClass();
        StringBuilder errMsg = new StringBuilder();
        for (Field field : getEntityClassField(clazz)) {
            if (!fields.isEmpty() && !fields.contains(field.getName())) {
                continue;
            }
            Schema property = field.getAnnotation(Schema.class);
            if (Objects.nonNull(property)) {
                String name = field.getName();
                Object value = null;
                try {
                    Method getMethod = clazz.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                    value = getMethod.invoke(obj);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                if (ObjectUtil.isEmpty(value)) {
                    errMsg.append(String.format("%s(%s) field can not empty！", property.title(), name));
                }
            }
        }
        if (errMsg.length() > 0) {
            throw new BusinessException(beanName + "：" + errMsg);
        }
    }

    public static void copyProperties(Object source, Object target, boolean ignoreNullValue) {
        copyProperties(source, target, CopyOptions.create().setIgnoreNullValue(ignoreNullValue));
    }

    public static <T> List<T> copyToListIgnoreId(Collection<?> collection, Class<T> targetType) {
        return copyToList(collection, targetType, CopyOptions.create().setIgnoreProperties("id"));
    }

    public static <T> List<T> copyToListByFieldValueEditor(Collection<?> collection, Class<T> targetType,
                                                           BiFunction<String, Object, Object> fieldValueEditor) {
        return copyToList(collection, targetType, CopyOptions.create().setFieldValueEditor(fieldValueEditor));
    }

}
