package com.lingmu.easyweb.mybitis.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.lingmu.easyweb.mybitis.constant.ConditionConst;
import com.lingmu.easyweb.core.function.QSupplier;
import com.lingmu.easyweb.core.util.CollectionUtil;
import com.lingmu.easyweb.core.util.LambdaUtil;
import com.lingmu.easyweb.mybitis.util.PageQueryUtil;
import com.lingmu.easyweb.core.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class QueryRequest {
    private Map<String, Object> data;
    @Schema(title = "条件集")
    private List<QueryParam> params;
    @Schema(title = "排序配置")
    private List<SortParam> sorts;

    public List<QueryParam> getParams() {
        if (params == null) {
            params = new ArrayList<>(10);
        }
        return params;
    }

    public List<SortParam> getSorts() {
        return sorts == null ? sorts = new ArrayList<>() : sorts;
    }

    public QueryParam getQueryParam(String name) {
        QueryParam result = null;
        for (QueryParam param : this.getParams()) {
            if (name.equals(param.getName())) {
                result = param;
                break;
            }
        }
        return result;
    }

    public QueryParam getQueryParamAndRemove(String name) {
        QueryParam result = null;
        int index = -1;
        List<QueryParam> queryParams = this.getParams();
        for (int i = 0; i < queryParams.size(); i++) {
            QueryParam param = queryParams.get(i);
            if (name.equals(param.getName())) {
                result = param;
                index = i;
                break;
            }
        }
        if (index != -1) {
            queryParams.remove(index);
        }
        return result;
    }

    public String getParamValueAndRemove(String name) {
        QueryParam queryParam = getQueryParamAndRemove(name);
        return Objects.isNull(queryParam) ? null : queryParam.getValue();
    }

    public List<String> getParamValuesAndRemove(String name) {
        QueryParam queryParam = getQueryParamAndRemove(name);
        return Objects.isNull(queryParam) ? null : queryParam.getValues();
    }

    public String getParamsValue(String name) {
        List<QueryParam> params = this.getParams();
        String value = null;
        for (QueryParam param : params) {
            if (name.equals(param.getName())) {
                value = param.getValue();
                break;
            }
        }
        return value;
    }

    public List<String> getParamsNames() {
        if (CollectionUtil.isEmpty(params)) {
            return CollectionUtil.newArrayList();
        }
        return params.stream()
                .map(QueryParam::getName)
                .collect(Collectors.toList());
    }

    public void addParameter(QSupplier<?> column, String op) {
        this.addParameter(LambdaUtil.getFieldName(column), StringUtil.valueOf(column.get(), ""), op);
    }

    public <T> void addParameter(SFunction<T, ?> name, String value, String op) {
        addParameter(LambdaUtil.getFieldName(name), value, op);
    }

    public <T> void addParameter(SFunction<T, ?> name, String[] values, String op) {
        addParameter(LambdaUtil.getFieldName(name), values, op);
    }

    public <T> void addParameter(SFunction<T, ?> name, List<String> values, String op) {
        addParameter(LambdaUtil.getFieldName(name), values, op);
    }

    public void addParameter(String name, String value, String op) {
        QueryParam param = new QueryParam();
        param.setName(name);
        param.setValue(value);
        param.setOp(op);
        this.getParams().add(param);
    }

    public void addParameter(String name, String value1, String value2, String op) {
        QueryParam param = new QueryParam();
        param.setName(name);
        param.setValues(Arrays.asList(value1, value2));
        param.setOp(op);
        this.getParams().add(param);
    }

    public void addParameter(QSupplier<?> column) {
        this.addParameter(column, ConditionConst.EQ);
    }

    public <T> void addParameter(SFunction<T, ?> name, String value) {
        this.addParameter(name, value, ConditionConst.EQ);
    }

    public void addParameter(String name, String value) {
        this.addParameter(name, value, ConditionConst.EQ);
    }

    public void addParameter(String name, String[] value, String op) {
        QueryParam param = new QueryParam();
        param.setName(name);
        param.setValues(Arrays.stream(value).collect(Collectors.toList()));
        param.setOp(op);
        this.getParams().add(param);
    }

    public void addParameter(String name, List<String> values, String op) {
        QueryParam param = new QueryParam();
        param.setName(name);
        param.setValues(values);
        param.setOp(op);
        this.getParams().add(param);
    }

    public void addParameter(String name, String[] value) {
        QueryParam param = new QueryParam();
        param.setName(name);
        param.setValues(Arrays.stream(value).collect(Collectors.toList()));
        param.setOp(ConditionConst.IN);
        this.getParams().add(param);
    }

    public <T> void addParameter(SFunction<T, ?> name, List<String> values) {
        addParameter(LambdaUtil.getFieldName(name), values);
    }

    public void addParameter(String name, List<String> values) {
        QueryParam param = new QueryParam();
        param.setName(name);
        param.setValues(values);
        param.setOp(ConditionConst.IN);
        this.getParams().add(param);
    }

    public void addSort(String name, boolean isAsc) {
        if (sorts == null) {
            sorts = new ArrayList<>();
        }
        SortParam param = new SortParam();
        param.setName(name);
        param.setAsc(isAsc);
        this.sorts.add(param);
    }

    public <T> void addSort(SFunction<T, ?> name, boolean value) {
        this.addSort(LambdaUtil.getFieldName(name), value);
    }

    public void addSort(String name) {
        addSort(name, true);
    }

    public void doSimpleWrapper(QueryWrapper<?> wrapper) {
        PageQueryUtil.doSimpleWrapper(this, wrapper);
    }

    public void doSortWrapper(QueryWrapper<?> wrapper) {
        PageQueryUtil.doSortWrapper(this, wrapper);
    }

    public <T> QueryWrapper<T> doSimpleWrapper(Class<T> clazz) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.setEntityClass(clazz);
        doSimpleWrapper(wrapper);
        doSortWrapper(wrapper);
        return wrapper;
    }

    public <T> LambdaQueryWrapper<T> doLambdaWrapper(Class<T> clazz) {
        return doSimpleWrapper(clazz).lambda();
    }
}
