package io.github.limuqy.easyweb.core.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.github.limuqy.easyweb.core.annotation.RowId;
import io.github.limuqy.easyweb.core.util.RowIdUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ID加密处理器
 */
public class RowIdSerializerHandler extends JsonSerializer<Object> implements ContextualSerializer {

    private RowId rowId;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (RowIdUtil.isDisable()) {
            gen.writeObject(value);
            return;
        }
        String tableName = RowIdUtil.getEntityTableName(rowId, gen.getCurrentValue().getClass());
        if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            List<String> list = collection.stream()
                    .map(id -> RowIdUtil.encryptRowId(Long.parseLong(String.valueOf(id)), tableName))
                    .collect(Collectors.toList());
            gen.writeArray(list.toArray(new String[0]), 0, list.size());
        } else {
            String valueStr = String.valueOf(value);
            if (StringUtil.isEmpty(valueStr)) {
                gen.writeString("");
            } else {
                gen.writeString(RowIdUtil.encryptRowId(Long.parseLong(valueStr), tableName));
            }
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        RowId rowIdAnnotation = property.getAnnotation(RowId.class);
        if (Objects.nonNull(rowIdAnnotation)) {
            this.rowId = rowIdAnnotation;
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}