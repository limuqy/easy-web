package io.github.limuqy.easyweb.excel.read.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.util.ListUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Setter
public class SimpleImportListener<T> implements ReadListener<T> {
    /**
     * 每隔1000条存储数据库，然后清理list ，方便内存回收
     */
    protected Integer limit = 1000;
    /**
     * 缓存的数据
     */
    protected List<T> cachedDataList;

    protected Consumer<List<T>> consumer;

    public SimpleImportListener(Consumer<List<T>> consumer, Integer limit) {
        this.consumer = consumer;
        if (limit != null) {
            this.limit = limit;
        }
        cachedDataList = ListUtils.newArrayListWithExpectedSize(this.limit);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= limit) {
            List<T> list = cachedDataList;
            consumer.accept(list);
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(limit);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cachedDataList.isEmpty()) {
            consumer.accept(cachedDataList);
        }
    }

}
