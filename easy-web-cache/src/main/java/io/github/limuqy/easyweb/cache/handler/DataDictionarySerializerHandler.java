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
            JsonStreamContext outputContext = gen.getOutputContext();
            String meaningFieldName = outputContext.getCurrentName() + dataDictionary.suffix();
            String split = dataDictionary.split();
            if (value instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) value;
                List<String> list = collection.stream().map(String::valueOf).collect(Collectors.toList());
                gen.writeArray(list.toArray(new String[0]), 0, collection.size());
                String finalDictCode = dictCode;
                List<String> meanings = list.stream().map(code -> DictUtil.getMeaning(finalDictCode, code)).collect(Collectors.toList());
                if (StringUtil.isEmpty(split)) {
                    gen.writeFieldName(meaningFieldName);
                    gen.writeArray(meanings.toArray(new String[0]), 0, meanings.size());
                    return;
                }
                gen.writeStringField(meaningFieldName, meanings.stream().map(s -> StringUtil.valueOf(s, "")).collect(Collectors.joining(split)));
            } else {
                gen.writeString(String.valueOf(value));
                gen.writeStringField(meaningFieldName, DictUtil.getMeaning(dictCode, String.valueOf(value), split));
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
