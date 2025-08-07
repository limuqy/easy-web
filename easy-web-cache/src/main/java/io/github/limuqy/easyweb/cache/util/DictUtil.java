package io.github.limuqy.easyweb.cache.util;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.limuqy.easyweb.model.cache.DictValueEntity;
import io.github.limuqy.easyweb.cache.template.CacheTemplate;
import io.github.limuqy.easyweb.core.util.CollectionUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据字典工具
 */
@Component
public class DictUtil {

    private static CacheTemplate cacheTemplate;
    private static final ThreadLocal<Map<String, Map<String, DictValueEntity>>> COMPLETE_DICT_MAP_THREAD_LOCAL = new ThreadLocal<>();
    public static final String MODULE_DICTIONARY = "dictionary";

    private DictUtil(CacheTemplate cacheTemplate) {
        DictUtil.cacheTemplate = cacheTemplate;
    }

    public static String getMeaning(String dictCode, String code) {
        if (StringUtil.isBlank(dictCode) || StringUtil.isBlank(code)) {
            return code;
        }
        String meaning = null;
        Map<String, DictValueEntity> dictionaryMap = getDictionaryMap(dictCode);
        if (CollectionUtil.isNotEmpty(dictionaryMap)) {
            DictValueEntity dictValue = dictionaryMap.get(code);
            if (Objects.nonNull(dictValue)) {
                meaning = dictValue.getValue();
            }
        }
        if (meaning == null) {
            meaning = code;
        }
        return meaning;
    }

    public static String getMeaning(String dictCode, String code, String split) {
        if (StringUtil.isEmpty(split)) {
            return getMeaning(dictCode, code);
        }
        return Stream.of(code.split(split)).map(c -> getMeaning(dictCode, c)).collect(Collectors.joining(split));
    }

    public static String getMeaningCode(String dictCode, String meaning) {
        if (StringUtil.isEmpty(meaning) || StringUtil.isEmpty(dictCode)) {
            return meaning;
        }
        String meaningCode = meaning;
        Map<String, DictValueEntity> dictionaryMap = getDictionaryMap(dictCode);
        if (CollectionUtil.isEmpty(dictionaryMap.values())) {
            meaningCode = meaning;
        } else {
            for (DictValueEntity map : dictionaryMap.values()) {
                if (meaning.equals(map.getValue())) {
                    meaningCode = map.getCode();
                    break;
                }
            }
        }
        return meaningCode;
    }

    public static Map<String, String> getDictionaryValueMap(String dictCode) {
        Map<String, DictValueEntity> dictionaryMap = getDictionaryMap(dictCode);
        Map<String, String> values = new HashMap<>();
        if (CollectionUtil.isEmpty(dictionaryMap)) {
            return new HashMap<>();
        }
        dictionaryMap.values().forEach(valueMap -> values.put(valueMap.getCode(), valueMap.getValue()));
        return values;
    }


    public static Map<String, DictValueEntity> getDictionaryMap(String dictCode) {
        Map<String, DictValueEntity> map = new LinkedHashMap<>();
        if (StringUtil.isEmpty(dictCode)) {
            return map;
        }
        return getCacheMap(dictCode);
    }

    public static void clear() {
        COMPLETE_DICT_MAP_THREAD_LOCAL.remove();
    }

    private static Map<String, DictValueEntity> getCacheMap(String dictCode) {
        Map<String, Map<String, DictValueEntity>> dictMap = COMPLETE_DICT_MAP_THREAD_LOCAL.get();
        if (dictMap == null) {
            dictMap = new HashMap<>();
            COMPLETE_DICT_MAP_THREAD_LOCAL.set(dictMap);
        }
        if (dictMap.containsKey(dictCode)) {
            return dictMap.get(dictCode);
        }
        Map<String, DictValueEntity> map = getMap(dictCode);
        dictMap.put(dictCode, map);
        return map;
    }

    private static Map<String, DictValueEntity> getMap(String dictCode) {
        Map<String, DictValueEntity> map = cacheTemplate.get(MODULE_DICTIONARY, dictCode, new TypeReference<Map<String, DictValueEntity>>() {
        });
        if (CollectionUtil.isEmpty(map)) {
            map = new HashMap<>();
        }
        return map;
    }

}
