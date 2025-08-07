package com.lingmu.easyweb.mybitis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lingmu.easyweb.core.util.BeanUtil;
import com.lingmu.easyweb.mybitis.base.PageRequest;
import com.lingmu.easyweb.mybitis.base.PageResult;
import com.lingmu.easyweb.mybitis.base.QueryRequest;

import java.util.List;

public interface BaseService<T> extends IService<T> {

    /**
     * 通用条件分页查询
     *
     * @param queryRequest 条件
     */
    default Page<T> pageQuery(PageRequest queryRequest) {
        return page(queryRequest.getPage(this.getEntityClass()), queryRequest.doSimpleWrapper(this.getEntityClass()));
    }

    /**
     * 通用条件分页查询
     *
     * @param queryRequest 条件
     */
    default <E> Page<E> pageQuery(PageRequest queryRequest, Class<E> clazz) {
        return PageResult.copyToPage(pageQuery(queryRequest), clazz);
    }

    /**
     * 通用条件查询
     *
     * @param queryRequest 条件
     */
    default List<T> query(QueryRequest queryRequest) {
        return list(queryRequest.doSimpleWrapper(this.getEntityClass()));
    }

    /**
     * 通用条件查询
     *
     * @param queryRequest 条件
     */
    default <E> List<E> query(QueryRequest queryRequest, Class<E> clazz) {
        return BeanUtil.copyToList(query(queryRequest), clazz);
    }

    /**
     * 通用条件获取条件构造器
     *
     * @param queryRequest 条件
     */
    default QueryWrapper<T> queryWrapper(QueryRequest queryRequest) {
        return queryRequest.doSimpleWrapper(this.getEntityClass());
    }

}
