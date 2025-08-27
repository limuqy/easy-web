package io.github.limuqy.easyweb.excel.write;

import cn.idev.excel.ExcelWriter;
import cn.idev.excel.write.metadata.WriteSheet;
import io.github.limuqy.easyweb.core.queue.PutBlockingQueue;
import io.github.limuqy.easyweb.core.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 多线程导出
 *
 * @param <T> 导出的Bean类型
 */
@Slf4j
public class BatchExport<T> extends SimpleExport<T> {

    protected Supplier<Long> totalQuery;

    private static final int THREAD_NUM = 8;
    private long timeout = 5;
    private TimeUnit unit = TimeUnit.MINUTES;

    private BatchExport(Class<T> clazz) {
        super(clazz);
    }

    /**
     * @param clazz 实际导出的类
     * @param <T>   实际导出的类型
     * @return this
     */
    public static <T> SimpleExport<T> build(Class<T> clazz) {
        return new BatchExport<>(clazz);
    }

    /**
     * 查询数据总量
     *
     * @param totalQuery 查询获取数据总量
     * @return this
     */
    public SimpleExport<T> total(Supplier<Long> totalQuery) {
        this.totalQuery = totalQuery;
        return this;
    }

    public SimpleExport<T> timeout(long timeout) {
        this.timeout = timeout;
        this.unit = TimeUnit.MINUTES;
        return this;
    }

    public SimpleExport<T> timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    @Override
    protected void write(ExcelWriter excelWriter, WriteSheet writeSheet) throws Exception {
        if (this.totalQuery == null) {
            super.write(excelWriter, writeSheet);
            return;
        }
        Long total = this.totalQuery.get();
        if (total == null) {
            return;
        }
        if (total < limit * 8) {
            super.write(excelWriter, writeSheet);
            return;
        }
        log.debug("预计导出总数：{}，将启用多线程导出。", total);
        ExecutorService executorService = null;
        try {
            PutBlockingQueue<List<T>> writeQueue = new PutBlockingQueue<>(THREAD_NUM * 2);
            executorService = ThreadUtil.blockingVirtualService(THREAD_NUM);
            int pageTotal = (int) Math.ceil(total / (limit * 1.0D));
            List<Future<?>> futures = new ArrayList<>(pageTotal);
            for (int i = 1; i <= pageTotal; i++) {
                int page = i;
                Future<?> future = executorService.submit(() -> {
                    List<T> list = listQuery.apply(page, limit);
                    writeQueue.offer(list);
                });
                futures.add(future);
            }
            for (Future<?> future : futures) {
                future.get(timeout, unit);
                List<T> data = writeQueue.take();
                excelWriter.write(data, writeSheet);
                outputStream.flush();
                data.clear();
            }
        } finally {
            ThreadUtil.closeExecutor(executorService);
        }
    }
}
