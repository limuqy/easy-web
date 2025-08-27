package io.github.limuqy.easyweb.excel.write;

import cn.hutool.core.bean.DynaBean;
import cn.idev.excel.converters.Converter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.github.limuqy.easyweb.core.function.Func2;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import io.github.limuqy.easyweb.core.util.LambdaUtil;
import io.github.limuqy.easyweb.core.util.URLUtil;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查询导出
 *
 * @param <T> 导出的Bean类型
 * @param <M> 查询的Bean类型
 */
public class QueryExport<T, M> {
    private final QueryWrapper<M> wrapper;
    private Func2<Page<M>, QueryWrapper<M>, List<M>> listQuery;
    private Function<List<M>, List<T>> mapFun;
    private Function<List<T>, List<T>> applyFun;
    private Function<QueryWrapper<M>, Long> totalQuery;
    private final SimpleExport<T> simpleExport;

    private static final String SUFFIX_XLSX = ".xlsx";

    private QueryExport(QueryWrapper<M> wrapper, Class<T> clazz, SimpleExport<T> export) {
        this.wrapper = wrapper;
        this.simpleExport = export;
        mapFun = (List<M> list) -> {
            if (clazz == wrapper.getEntityClass()) {
                return list.stream().map(clazz::cast).collect(Collectors.toList());
            }
            return BeanUtil.copyToList(list, clazz);
        };
        totalQuery = Db::count;
        listQuery = Db::list;
    }

    /**
     * 单线程导出
     *
     * @param wrapper 条件构造器
     * @param <T>     实际导出的类型
     */
    public static <T> QueryExport<T, T> build(QueryWrapper<T> wrapper) {
        return new QueryExport<>(wrapper, wrapper.getEntityClass(), SimpleExport.build(wrapper.getEntityClass()));
    }

    /**
     * 单线程导出
     *
     * @param wrapper 条件构造器
     * @param clazz   实际导出的类
     * @param <T>     实际导出的类型
     * @param <M>     分页查询的类型
     */
    public static <T, M> QueryExport<T, M> build(QueryWrapper<M> wrapper, Class<T> clazz) {
        return new QueryExport<>(wrapper, clazz, SimpleExport.build(clazz));
    }

    /**
     * 大批量导出时使用多线程
     *
     * @param wrapper 条件构造器
     * @param <T>     实际导出的类型
     */
    public static <T> QueryExport<T, T> buildBatch(QueryWrapper<T> wrapper) {
        return new QueryExport<>(wrapper, wrapper.getEntityClass(), BatchExport.build(wrapper.getEntityClass()));
    }

    /**
     * 大批量导出时使用多线程
     *
     * @param wrapper 条件构造器
     * @param clazz   实际导出的类
     * @param <T>     实际导出的类型
     * @param <M>     分页查询的类型
     */
    public static <T, M> QueryExport<T, M> buildBatch(QueryWrapper<M> wrapper, Class<T> clazz) {
        return new QueryExport<>(wrapper, clazz, BatchExport.build(clazz));
    }

    public QueryExport<T, M> limit(Integer limit) {
        simpleExport.limit(limit);
        return this;
    }

    /**
     * 设置输出流
     *
     * @param outputStream 输出流
     */
    public QueryExport<T, M> out(OutputStream outputStream) {
        simpleExport.out(outputStream);
        return this;
    }

    /**
     * 设置响应
     *
     * @param servletResponse 必须传入 javax.servlet.http.HttpServletResponse或者jakarta.servlet.http.HttpServletResponse
     * @param fileName        下载文件名称（GET请求有效）
     * @return this
     */
    public QueryExport<T, M> out(Object servletResponse, String fileName) {
        return out(processResponse(fileName, servletResponse));
    }

    /**
     * 设置响应
     *
     * @param servletResponse 必须传入 javax.servlet.http.HttpServletResponse或者jakarta.servlet.http.HttpServletResponse
     * @return this
     */
    public QueryExport<T, M> out(Object servletResponse) {
        return out(servletResponse, String.format("export_%s.xlsx", System.currentTimeMillis()));
    }

    public QueryExport<T, M> converter(Converter<?> converter) {
        simpleExport.converter(converter);
        return this;
    }

    /**
     * 查询数据
     *
     * @param listQuery 查询获取后续数据
     */
    public QueryExport<T, M> query(Func2<Page<M>, QueryWrapper<M>, List<M>> listQuery) {
        this.listQuery = listQuery;
        return this;
    }

    /**
     * 查询数据总量
     *
     * @param totalQuery 查询获取数据总量
     * @return this
     */
    public QueryExport<T, M> total(Function<QueryWrapper<M>, Long> totalQuery) {
        this.totalQuery = totalQuery;
        return this;
    }

    /**
     * 类型转换，用于查询类型和导出类型不一致时，或者需要对查询结果集处理
     *
     * @param mapFun 转换方法
     */
    public QueryExport<T, M> map(Function<List<M>, List<T>> mapFun) {
        this.mapFun = mapFun;
        return this;
    }

    /**
     * 对查询结果集处理
     *
     * @param applyFun 处理方法
     */
    public QueryExport<T, M> apply(Function<List<T>, List<T>> applyFun) {
        this.applyFun = applyFun;
        return this;
    }

    /**
     * 对查询结果集处理
     */
    public QueryExport<T, M> include(Collection<String> includeColumnFieldNames) {
        simpleExport.include(includeColumnFieldNames);
        return this;
    }

    /**
     * 对查询结果集处理
     */
    public QueryExport<T, M> exclude(Collection<String> excludeColumnFieldNames) {
        simpleExport.exclude(excludeColumnFieldNames);
        return this;
    }

    /**
     * 对查询结果集处理
     */
    @SafeVarargs
    public final QueryExport<T, M> include(SFunction<T, ?>... fields) {
        simpleExport.include(Arrays.stream(fields).map(LambdaUtil::getFieldName).collect(Collectors.toList()));
        return this;
    }

    /**
     * 对查询结果集处理
     */
    @SafeVarargs
    public final QueryExport<T, M> exclude(SFunction<T, ?>... fields) {
        simpleExport.exclude(Arrays.stream(fields).map(LambdaUtil::getFieldName).collect(Collectors.toList()));
        return this;
    }

    public QueryExport<T, M> head(List<List<String>> head) {
        simpleExport.head(head);
        return this;
    }

    public QueryExport<T, M> addHead(List<String> head) {
        simpleExport.addHead(head);
        return this;
    }

    /**
     * 执行导出
     */
    public void doExport() {
        if (simpleExport instanceof BatchExport) {
            BatchExport<T> batchExport = (BatchExport<T>) simpleExport;
            batchExport.total(() -> totalQuery.apply(wrapper));
        }
        simpleExport.query((pageNum, pageSize) -> {
            Page<M> page = Page.of(pageNum, pageSize);
            page.setSearchCount(false);
            List<M> list = listQuery.apply(page, wrapper);
            List<T> res = mapFun.apply(list);
            if (applyFun != null) {
                res = applyFun.apply(res);
            }
            return res;
        }).doExport();
    }

    /**
     * 公共处理response
     *
     * @param fileName        导出文件名称
     * @param servletResponse http响应
     */
    public static OutputStream processResponse(String fileName, Object servletResponse) {
        fileName = fileName.endsWith(SUFFIX_XLSX) ? fileName : fileName + SUFFIX_XLSX;
        // 这里URLEncoder.encode可以防止中文乱码
        String fileTitle = URLUtil.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        DynaBean response = DynaBean.create(servletResponse);
        response.invoke("setContentType", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.invoke("setCharacterEncoding", "UTF-8");
        response.invoke("setHeader", "Access-Control-Expose-Headers", "Content-Disposition");
        response.invoke("setHeader", "content-disposition", "attachment; filename*=utf-8''" + fileTitle);
        // 设置二进制传输文件
        response.invoke("setHeader", "Content-Transfer-Encoding", "binary");
        response.invoke("setHeader", "Pragma", "public");
        response.invoke("setHeader", "Cache-Control", "public");
        return (OutputStream) response.invoke("getOutputStream");
    }
}
