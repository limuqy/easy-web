package com.lingmu.easyweb.mybitis.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingmu.easyweb.core.util.BeanUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PageResult<T extends Collection<?>> {
    private long total;
    private T table;
    private String scrollId;

    public PageResult() {

    }

    public PageResult(T table, Integer total) {
        this.table = table;
        this.total = total;
    }

    public static <T> PageResult<Collection<T>> build(Collection<T> data, Number total) {
        PageResult<Collection<T>> result = new PageResult<>();
        result.setTable(data);
        result.setTotal(total == null ? 0 : total.longValue());
        return result;
    }

    public static <T> PageResult<Collection<T>> build(Page<T> page) {
        PageResult<Collection<T>> result = new PageResult<>();
        result.setTable(page.getRecords());
        result.setTotal(page.getTotal());
        result.setScrollId(page.countId());
        return result;
    }

    public static <T> PageResult<Collection<T>> build(Page<?> page, Class<T> clazz) {
        PageResult<Collection<T>> result = new PageResult<>();
        result.setTable(BeanUtil.copyToList(page.getRecords(), clazz));
        result.setTotal(page.getTotal());
        result.setScrollId(page.countId());
        return result;
    }

    public RestResponse<PageResult<T>> response() {
        return RestResponse.ok(this);
    }

    public static <T> Page<T> copyToPage(Page<?> page, Class<T> clazz) {
        Page<T> res = new Page<>();
        res.setRecords(BeanUtil.copyToList(page.getRecords(), clazz));
        res.setTotal(page.getTotal());
        return res;
    }
}
