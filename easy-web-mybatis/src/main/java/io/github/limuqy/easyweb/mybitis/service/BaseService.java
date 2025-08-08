package io.github.limuqy.easyweb.mybitis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import io.github.limuqy.easyweb.mybitis.base.PageRequest;
import io.github.limuqy.easyweb.mybitis.base.PageResult;
import io.github.limuqy.easyweb.mybitis.base.QueryRequest;

import java.util.List;

public interface BaseService<T> extends IService<T> {

    /**
     * 通用条件分页查询
     *
     * @param queryRequest 条件
     * @return 分页Page数据
     */
    default Page<T> pageQuery(PageRequest queryRequest) {
        return page(queryRequest.getPage(this.getEntityClass()), queryRequest.doSimpleWrapper(this.getEntityClass()));
    }

    /**
     * 通用条件分页查询
     *
     * @param queryRequest 条件
     * @param clazz 需要转换的对象类型
     * @return 分页Page数据
     * @param <E> 转换的对象类
     */
    default <E> Page<E> pageQuery(PageRequest queryRequest, Class<E> clazz) {
        return PageResult.copyToPage(pageQuery(queryRequest), clazz);
    }

    /**
     * 通用条件查询
     *
     * @param queryRequest 条件
     * @return 分页List数据
     */
    default List<T> query(QueryRequest queryRequest) {
        return list(queryRequest.doSimpleWrapper(this.getEntityClass()));
    }

    /**
     * 通用条件查询
     *
     * @param queryRequest 条件
     * @param clazz 需要转换的对象类型
     * @return 分页List数据
     * @param <E> 转换的对象类
     */
    default <E> List<E> query(QueryRequest queryRequest, Class<E> clazz) {
        return BeanUtil.copyToList(query(queryRequest), clazz);
    }

    /**
     * 通用条件获取条件构造器
     *
     * @param queryRequest 条件
     * @return 条件构造器
     */
    default QueryWrapper<T> queryWrapper(QueryRequest queryRequest) {
        return queryRequest.doSimpleWrapper(this.getEntityClass());
    }

}
