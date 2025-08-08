package io.github.limuqy.easyweb.mybitis.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.limuqy.easyweb.mybitis.constant.ConditionConst;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import io.github.limuqy.easyweb.core.util.CollectionUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;
import io.github.limuqy.easyweb.core.util.TypeUtil;
import io.github.limuqy.easyweb.mybitis.base.QueryParam;
import io.github.limuqy.easyweb.mybitis.base.QueryRequest;
import io.github.limuqy.easyweb.mybitis.base.SortParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PageQueryUtil {

    private static final Logger logger = LoggerFactory.getLogger(PageQueryUtil.class);

    private static final String QUERY_PARAM_MESSAGE = "queryParam ==> {} {} {}";

    private PageQueryUtil() {
    }

    public static void doSimpleWrapper(QueryRequest queryRequest, QueryWrapper<?> wrapper) {
        Class<?> clazz = wrapper.getEntityClass();
        List<Field> list = BeanUtil.getEntityClassField(wrapper.getEntityClass());
        for (QueryParam queryParam : queryRequest.getParams()) {
            String fieldName = StringUtil.upper2Underline(queryParam.getName());
            Field field = list.stream()
                    .filter(f -> StringUtil.upper2Underline(f.getName()).equals(fieldName))
                    .findFirst()
                    .orElse(null);
            paramLogger(queryParam);
            String value1 = queryParam.getValue();
            boolean value1NoneBlank = StringUtil.isNotBlank(value1);
            if (StringUtil.isBlank(queryParam.getOp())) {
                queryParam.setOp(ConditionConst.EQ);
            }
            switch (queryParam.getOp()) {
                case ConditionConst.EQ:
                    wrapper.eq(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.NE:
                    wrapper.ne(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.LK:
                    wrapper.like(value1NoneBlank, fieldName, getValue(escapeSpecialChar(value1), field, clazz));
                    break;
                case ConditionConst.LLK:
                    wrapper.likeLeft(value1NoneBlank, fieldName, getValue(escapeSpecialChar(value1), field, clazz));
                    break;
                case ConditionConst.RLK:
                    wrapper.likeRight(value1NoneBlank, fieldName, getValue(escapeSpecialChar(value1), field, clazz));
                    break;
                case ConditionConst.NC:
                    wrapper.notLike(value1NoneBlank, fieldName, getValue(escapeSpecialChar(value1), field, clazz));
                    break;
                case ConditionConst.NEL:
                    wrapper.notLikeLeft(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.NBL:
                    wrapper.notLikeRight(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.IN:
                    wrapper.in(CollectionUtil.isAllNotEmpty(queryParam.getValues()), fieldName, queryParam.getValues().stream().map(v -> getValue(v, field, clazz)).collect(Collectors.toList()));
                    break;
                case ConditionConst.NIN:
                    wrapper.notIn(CollectionUtil.isAllNotEmpty(queryParam.getValues()), fieldName, queryParam.getValues().stream().map(v -> getValue(v, field, clazz)).collect(Collectors.toList()));
                    break;
                case ConditionConst.GT:
                    wrapper.gt(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.GE:
                    wrapper.ge(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.LT:
                    wrapper.lt(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.LE:
                    wrapper.le(value1NoneBlank, fieldName, getValue(value1, field, clazz));
                    break;
                case ConditionConst.BT:
                    if (CollectionUtil.isAllNotEmpty(queryParam.getValues()) && queryParam.getValues().size() == 2) {
                        wrapper.between(fieldName,
                                getValue(queryParam.getValues().get(0), field, clazz),
                                getValue(queryParam.getValues().get(1), field, clazz));
                    }
                    break;
                case ConditionConst.NBT:
                    if (CollectionUtil.isAllNotEmpty(queryParam.getValues()) && queryParam.getValues().size() == 2) {
                        wrapper.notBetween(fieldName,
                                getValue(queryParam.getValues().get(0), field, clazz),
                                getValue(queryParam.getValues().get(1), field, clazz));
                    }
                    break;
                case ConditionConst.NU:
                    wrapper.isNull(fieldName);
                    break;
                case ConditionConst.NN:
                    wrapper.isNotNull(fieldName);
                    break;
                default:
                    break;
            }
        }
    }

    private static void paramLogger(QueryParam queryParam) {
        boolean isValues = ConditionConst.IN.equals(queryParam.getOp()) || ConditionConst.BT.equals(queryParam.getOp()) || ConditionConst.NBT.equals(queryParam.getOp());
        logger.debug(QUERY_PARAM_MESSAGE, queryParam.getName(), queryParam.getOp(), isValues ? queryParam.getValues() : queryParam.getValue());
    }

    public static void doSortWrapper(QueryRequest queryRequest, QueryWrapper<?> wrapper) {
        if (CollectionUtil.isAllEmpty(queryRequest.getSorts())) {
            return;
        }
        List<Field> list = BeanUtil.getEntityClassField(wrapper.getEntityClass());
        for (SortParam sort : queryRequest.getSorts()) {
            String fieldName = StringUtil.upper2Underline(sort.getName());
            Field field = list.stream()
                    .filter(f -> StringUtil.upper2Underline(f.getName()).equals(fieldName))
                    .findFirst()
                    .orElse(null);
            if (field == null) {
                continue;
            }
            wrapper.orderBy(true, sort.isAsc(), fieldName);
        }
    }

    public static Object getValue(String value, Field field, Class<?> clazz) {
        if (StringUtil.isBlank(value)) {
            return null;
        }
        if (field == null) {
            return value;
        }
        if (Collection.class.isAssignableFrom(field.getType())) {
            return TypeUtil.convert(value, cn.hutool.core.util.TypeUtil.getClass(cn.hutool.core.util.TypeUtil.getTypeArgument(field.getGenericType(), 0)));
        }
        return TypeUtil.convert(value, field.getType());
    }

    private static List<String> getValue(List<String> values, Field field, Class<?> clazz) {
        List<String> valueList = new ArrayList<>(10);
        for (String value1 : values) {
            Object value = getValue(value1, field, clazz);
            if (Objects.isNull(value)) {
                continue;
            }
            valueList.add(value1);
        }
        return valueList;
    }

    /**
     * 使用like时，进行value转义
     * @param value like值
     * @return 转义后的值
     */
    public static String escapeSpecialChar(String value) {
        if (StringUtil.isBlank(value)) {
            return value;
        }
        value = value.replace("\\", "\\\\");
        value = value.replace("%", "\\%");
        value = value.replace("_", "\\_");
        value = value.replace("/", "\\/");
        return value;
    }

}
