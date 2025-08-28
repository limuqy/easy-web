package io.github.limuqy.easyweb.excel.write;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.support.ExcelTypeEnum;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.metadata.WriteSheet;
import io.github.limuqy.easyweb.core.function.Func2;
import io.github.limuqy.easyweb.core.util.BeanUtil;
import io.github.limuqy.easyweb.core.util.CollectionUtil;
import io.github.limuqy.easyweb.excel.converter.TimestampStringConverter;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简单导出
 *
 * @param <T> 导出的Bean类型
 */
public class SimpleExport<T> {
    protected Integer limit = 500;
    protected final Class<T> clazz;
    protected OutputStream outputStream;
    protected Func2<Integer, Integer, List<T>> listQuery;
    protected Collection<String> excludeColumnFieldNames;
    protected Collection<String> includeColumnFieldNames;
    protected List<List<String>> head;

    protected final List<Converter<?>> converters = new ArrayList<>();

    protected SimpleExport(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * @param clazz 实际导出的类
     * @param <T>   实际导出的类型
     * @return this
     */
    public static <T> SimpleExport<T> build(Class<T> clazz) {
        return new SimpleExport<>(clazz);
    }

    public SimpleExport<T> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 设置输出流
     *
     * @param outputStream 输出流
     * @return this
     */
    public SimpleExport<T> out(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    /**
     * 查询数据
     *
     * @param listQuery 查询获取后续数据
     * @return this
     */
    public SimpleExport<T> query(Func2<Integer, Integer, List<T>> listQuery) {
        this.listQuery = listQuery;
        return this;
    }

    /**
     * 设置导出对象包含列
     *
     * @param includeColumnFieldNames 包含列的字段名称
     * @return this
     */
    public SimpleExport<T> include(Collection<String> includeColumnFieldNames) {
        this.includeColumnFieldNames = includeColumnFieldNames;
        return this;
    }

    /**
     * 设置导出对象排除列
     *
     * @param excludeColumnFieldNames 排除列的字段名称
     * @return this
     */
    public SimpleExport<T> exclude(Collection<String> excludeColumnFieldNames) {
        this.excludeColumnFieldNames = excludeColumnFieldNames;
        return this;
    }

    public SimpleExport<T> head(List<List<String>> head) {
        this.head = head;
        return this;
    }

    public SimpleExport<T> addHead(List<String> head) {
        this.head.addAll(head.stream().map(Collections::singletonList).collect(Collectors.toList()));
        return this;
    }

    public SimpleExport<T> converter(Converter<?> converter) {
        this.converters.add(converter);
        return this;
    }

    /**
     * 执行导出
     */
    public void doExport() {
        ExcelWriter excelWriter = null;
        if (CollectionUtil.isEmpty(includeColumnFieldNames)) {
            includeColumnFieldNames = BeanUtil.getAllFields(this.clazz).stream()
                    .filter(e -> e.isAnnotationPresent(ExcelProperty.class))
                    .map(Field::getName)
                    .collect(Collectors.toList());
        }
        WriteSheet writeSheet = FastExcel.writerSheet(0)
                .includeColumnFieldNames(includeColumnFieldNames)
                .excludeColumnFieldNames(excludeColumnFieldNames)
                .orderByIncludeColumn(true)
                .build();
        try {
            ExcelWriterBuilder write = FastExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX);
            if (!converters.isEmpty()) {
                converters.forEach(write::registerConverter);
            } else {
                write.registerConverter(new TimestampStringConverter());
            }
            if (this.head != null && !this.head.isEmpty()) {
                write.head(this.head);
            } else {
                write.head(this.clazz);
            }
            excelWriter = write.build();
            write(excelWriter, writeSheet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    protected void write(ExcelWriter excelWriter, WriteSheet writeSheet) throws Exception {
        int pageNum = 1;
        List<T> list;
        do {
            list = listQuery.apply(pageNum, limit);
            excelWriter.write(list, writeSheet);
            pageNum++;
        } while (list.size() >= limit);
    }

}