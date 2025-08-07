package com.lingmu.easyweb.excel.write;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.FastExcel;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.support.ExcelTypeEnum;
import cn.idev.excel.write.builder.ExcelWriterBuilder;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.lingmu.easyweb.core.function.Func2;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleExport<T> {
    private Integer limit = 500;
    private final Class<T> clazz;
    private OutputStream outputStream;
    private Func2<Integer, Integer, List<T>> listQuery;
    private Collection<String> excludeColumnFieldNames;
    private Collection<String> includeColumnFieldNames;
    private List<List<String>> head;

    private final List<Converter<?>> converters = new ArrayList<>();

    private SimpleExport(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * @param clazz 实际导出的类
     * @param <T>   实际导出的类型
     * @param <M>   分页查询的类型
     */
    public static <T, M> SimpleExport<T> build(Class<T> clazz) {
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
     */
    public SimpleExport<T> out(OutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    /**
     * 查询数据
     *
     * @param listQuery 查询获取后续数据
     */
    public SimpleExport<T> query(Func2<Integer, Integer, List<T>> listQuery) {
        this.listQuery = listQuery;
        return this;
    }

    /**
     * 对查询结果集处理
     */
    public SimpleExport<T> include(Collection<String> includeColumnFieldNames) {
        this.includeColumnFieldNames = includeColumnFieldNames;
        return this;
    }

    /**
     * 对查询结果集处理
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
        int pageNum = 1;
        ExcelWriter excelWriter = null;
        WriteSheet writeSheet = FastExcel.writerSheet(0)
                .includeColumnFieldNames(includeColumnFieldNames)
                .excludeColumnFieldNames(excludeColumnFieldNames)
                .orderByIncludeColumn(true)
                .build();
        try {
            ExcelWriterBuilder write = FastExcel.write(outputStream).excelType(ExcelTypeEnum.XLSX);
            if (!converters.isEmpty()) {
                converters.forEach(write::registerConverter);
            }
            if (this.head != null && !this.head.isEmpty()) {
                write.head(this.head);
                // 自动列宽
                write.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
            } else {
                write.head(this.clazz);
            }
            excelWriter = write.build();
            List<T> list;
            do {
                list = listQuery.apply(pageNum, limit);
                excelWriter.write(list, writeSheet);
                pageNum++;
            } while (list.size() >= limit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
                excelWriter.close();
            }
        }
    }

}