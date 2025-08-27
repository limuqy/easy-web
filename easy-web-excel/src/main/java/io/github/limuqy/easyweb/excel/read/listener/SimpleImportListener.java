package io.github.limuqy.easyweb.excel.read.listener;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.util.ListUtils;
import io.github.limuqy.easyweb.core.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Slf4j
public class SimpleImportListener<T> implements ReadListener<T> {

    /**
     * 每隔1000条存储数据库，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;

    private static final int THREAD_NUM = 8;
    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private final Consumer<List<T>> consumer;

    private final ExecutorService executorService;

    public SimpleImportListener(Consumer<List<T>> consumer) {
        this.consumer = consumer;
        this.executorService = ThreadUtil.blockingVirtualService(THREAD_NUM, THREAD_NUM * 2);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            List<T> list = cachedDataList;
            executorService.execute(ThreadUtil.wrap(() -> consumer.accept(list)));
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cachedDataList.isEmpty()) {
            executorService.execute(ThreadUtil.wrap(() -> consumer.accept(cachedDataList)));
        }
        ThreadUtil.closeExecutor(executorService);
    }

}
