package io.github.limuqy.easyweb.excel.read.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.util.ListUtils;
import io.github.limuqy.easyweb.core.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class BatchImportListener<T> extends SimpleImportListener<T> {
    private long timeout = 30;
    private TimeUnit unit = TimeUnit.MINUTES;

    private final ExecutorService executorService;

    public BatchImportListener(Consumer<List<T>> consumer, Integer limit, Integer quantity) {
        super(consumer, limit);
        this.executorService = ThreadUtil.blockingVirtualService(quantity, quantity * 2);
    }

    public BatchImportListener<T> timeout(long timeout) {
        this.timeout = timeout;
        this.unit = TimeUnit.MINUTES;
        return this;
    }

    public BatchImportListener<T> timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到limit了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= limit) {
            List<T> list = cachedDataList;
            executorService.execute(ThreadUtil.wrap(() -> consumer.accept(list)));
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(limit);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cachedDataList.isEmpty()) {
            executorService.execute(ThreadUtil.wrap(() -> consumer.accept(cachedDataList)));
        }
        try {
            ThreadUtil.closeExecutor(executorService, timeout, unit);
        } catch (Exception e) {
            log.error("Failed to close executor service", e);
            Thread.currentThread().interrupt();
        }
    }

}
