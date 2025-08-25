package io.github.limuqy.easyweb.core.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.github.limuqy.easyweb.core.annotation.RowId;
import io.github.limuqy.easyweb.core.exception.RowIdException;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import io.github.limuqy.easyweb.core.util.RowIdUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;
import io.github.limuqy.easyweb.core.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ID解密处理器
 */
@Slf4j
public class RowIdDeserializeHandler extends JsonDeserializer<Object> implements ContextualDeserializer {
    private RowId rowId;

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        Object currentValue = jsonParser.getCurrentValue();
        if (Objects.isNull(currentValue)) {
            currentValue = jsonParser.getParsingContext().getParent().getCurrentValue();
        }
        Class<?> beanClazz = currentValue.getClass();
        String currentName = jsonParser.getCurrentName();
        try {
            Field field = BeanUtil.getEntityClassField(beanClazz).stream()
                    .filter(f -> currentName.equals(f.getName()))
                    .findFirst().orElse(null);
            String text = jsonParser.getText();
            Class<?> fieldClazz = field != null ? field.getType() : null;
            if (RowIdUtil.isDisable()) {
                return TypeUtil.convert(text, fieldClazz);
            }

            String tableName = RowIdUtil.getEntityTableName(rowId, beanClazz);
            if ("[".equals(text)) {
                List<?> list = jsonParser.readValueAs(List.class);
                if (Objects.isNull(field)) {
                    return list.stream()
                            .map(id -> RowIdUtil.decryptRowId(String.valueOf(id), tableName))
                            .collect(Collectors.toList());
                }
                Class<?> typeClass = getTypeClass(field.getGenericType());
                return list.stream()
                        .map(id -> getValue(typeClass, RowIdUtil.decryptRowId(String.valueOf(id), tableName)))
                        .collect(Collectors.toList());
            }
            Long id = RowIdUtil.decryptRowId(text, tableName);
            return getValue(fieldClazz, id);
        } catch (Exception e) {
            log.error("RowId参数处理！", e);
            if (e instanceof RowIdException) {
                RowIdException rowIdException = (RowIdException) e;
                rowIdException.mappingErr(getParentPath(jsonParser.getParsingContext()), beanClazz, currentName);
                throw e;
            }
            throw new RowIdException(HttpStatus.BAD_REQUEST);
        }

    }

    private Object getValue(Class<?> type, Long id) {
        try {
            if (Objects.isNull(type)) {
                return id;
            }
            if (type.isAssignableFrom(String.class)) {
                return String.valueOf(id);
            } else if (type.isAssignableFrom(Integer.class)) {
                return id.intValue();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public Class<?> getTypeClass(Type type) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalStateException("Type must be a parameterized type");
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        // 获取泛型的具体类型  这里是单泛型
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length < 1) {
            throw new IllegalStateException("Number of type arguments must be 1");
        }
        return (Class<?>) actualTypeArguments[0];
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty property) throws JsonMappingException {
        if (property == null) {
            return this;
        }
        RowId rowIdAnnotation = property.getAnnotation(RowId.class);
        if (Objects.nonNull(rowIdAnnotation)) {
            this.rowId = rowIdAnnotation;
            return this;
        }
        return deserializationContext.findContextualValueDeserializer(property.getType(), property);
    }

    private String getParentPath(JsonStreamContext jsc) {
        if (jsc.getParent() != null) {
            String parentPath = getParentPath(jsc.getParent());
            if (StringUtil.isNotEmpty(parentPath)) {
                return String.format("%s.%s", parentPath, jsc.getCurrentName());
            }
        }
        return jsc.getCurrentName();
    }
}
