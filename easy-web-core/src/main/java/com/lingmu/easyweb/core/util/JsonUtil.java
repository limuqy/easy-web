package com.lingmu.easyweb.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static String toJSONString(Object obj) {
        if (obj == null) {
            return "";
        }
        ObjectMapper objectMapper = getObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        ObjectMapper objectMapper = getObjectMapper();
        T result;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            if (clazz == String.class) {
                return clazz.cast(json);
            }
            result = objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static <T> T parseObject(String json, Type type, Class<T> clazz) {
        ObjectMapper objectMapper = getObjectMapper();
        T result;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            if (clazz == String.class) {
                return clazz.cast(json);
            }
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType javaType = typeFactory.constructType(GenericTypeResolver.resolveType(type, clazz));
            result = objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static <T> T parseObject(String json, TypeReference<T> type) {
        ObjectMapper objectMapper = getObjectMapper();
        T result;
        if (StringUtil.isEmpty(json)) {
            return null;
        }
        try {
            result = objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        JavaType listType = TypeFactory.defaultInstance().constructParametricType(List.class, clazz);
        ObjectMapper objectMapper = getObjectMapper();
        List<T> result;
        if (StringUtil.isEmpty(json)) {
            return CollectionUtil.newEmptyArrayList();
        }
        try {
            result = objectMapper.readValue(json, listType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static boolean isArray(String json) {
        if (StringUtil.isEmpty(json)) {
            return false;
        }
        ObjectMapper objectMapper = getObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.isArray();
        } catch (JsonProcessingException e) {
            log.error("判断字符串是否为JSON数组失败", e);
            return false;
        }
    }

    public static boolean isNotArray(String json) {
        return !isArray(json);
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = null;
        try {
            objectMapper = SpringUtil.getBean(ObjectMapper.class);
        } catch (Exception e) {
            log.debug("SpringBean ObjectMapper获取失败！", e);
        }
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return objectMapper;
    }

}
