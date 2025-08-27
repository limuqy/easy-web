package io.github.limuqy.easyweb.core.batch;

import io.github.limuqy.easyweb.core.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class BatchTask {
    private static final Logger log = LoggerFactory.getLogger(BatchTask.class);

    private final List<Runnable> tasks = new ArrayList<>();

    /**
     * 并发量
     */
    private int quantity = 10;
    private long timeout = 1;
    private TimeUnit unit = TimeUnit.HOURS;

    private BatchTask() {
    }

    public static BatchTask builder() {
        return new BatchTask();
    }

    public static BatchTask builder(int quantity) {
        return builder().parallel(quantity);
    }

    /**
     * 设置并行运行的线程数量
     */
    public BatchTask parallel(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public BatchTask task(Runnable runnable) {
        tasks.add(ThreadUtil.wrap(runnable));
        return this;
    }

    public BatchTask timeout(long timeout) {
        this.timeout = timeout;
        this.unit = TimeUnit.MINUTES;
        return this;
    }

    public BatchTask timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    public void run() {
        long millis = System.currentTimeMillis();
        ExecutorService executorService = null;
        try {
            executorService = ThreadUtil.blockingVirtualService(quantity, 100);
            tasks.forEach(executorService::submit);
            if (executorService.awaitTermination(timeout, unit)) {
                log.debug("批量任务执行完毕，耗时：{}", System.currentTimeMillis() - millis);
            } else {
                log.debug("批量任务执行超时，耗时：{}", System.currentTimeMillis() - millis);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ThreadUtil.closeExecutor(executorService);
        }
    }

}
