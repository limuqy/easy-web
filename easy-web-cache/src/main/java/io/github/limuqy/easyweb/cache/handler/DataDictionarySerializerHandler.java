package io.github.limuqy.easyweb.cache.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.github.limuqy.easyweb.cache.annotation.DataDictionary;
import io.github.limuqy.easyweb.cache.util.DictUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据字典序列化处理器
 */
public class DataDictionarySerializerHandler extends JsonSerializer<Object> implements ContextualSerializer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DataDictionary dataDictionary;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) {
        String dictCode = dataDictionary.dictCode();
        if (StringUtil.isNotEmpty(dataDictionary.value())) {
            dictCode = dataDictionary.value();
        }
        try {
            if (value instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) value;
                gen.writeArray(collection.stream().map(String::valueOf).toArray(String[]::new), 0, collection.size());
            } else {
                gen.writeString(StringUtil.valueOf(value, null));
            }
            JsonStreamContext outputContext = gen.getOutputContext();
            String meaningFieldName = outputContext.getCurrentName() + dataDictionary.suffix();

            Object dictMeaning = DictUtil.getDictMeaning(value, dataDictionary);
            if (value instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) dictMeaning;
                List<String> list = collection.stream().map(String::valueOf).collect(Collectors.toList());
                gen.writeFieldName(meaningFieldName);
                gen.writeArray(list.toArray(new String[0]), 0, list.size());
            } else {
                gen.writeString(StringUtil.valueOf(value, null));
                gen.writeStringField(meaningFieldName, StringUtil.valueOf(dictMeaning, null));
            }
        } catch (Exception e) {
            log.error("值列表转换失败：{}:{}", dictCode, value, e);
        }
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        DataDictionary annotation = property.getAnnotation(DataDictionary.class);
        if (Objects.nonNull(annotation)) {
            this.dataDictionary = annotation;
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
