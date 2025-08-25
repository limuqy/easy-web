package io.github.limuqy.easyweb.mybitis.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PageResult<T> {
    private long total;
    private Collection<T> table;
    private String scrollId;

    public PageResult() {

    }

    public PageResult(Collection<T> table, Integer total) {
        this.table = table;
        this.total = total;
    }

    public static <M> PageResult<M> build(Collection<M> data, Number total) {
        PageResult<M> result = new PageResult<>();
        result.setTable(data);
        result.setTotal(total == null ? 0 : total.longValue());
        return result;
    }

    public static <M> PageResult<M> build(Page<M> page) {
        PageResult<M> result = new PageResult<>();
        result.setTable(page.getRecords());
        result.setTotal(page.getTotal());
        result.setScrollId(page.countId());
        return result;
    }

    public static <M> PageResult<M> build(Page<?> page, Class<M> clazz) {
        PageResult<M> result = new PageResult<>();
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
