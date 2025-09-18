package io.github.limuqy.easyweb.core.batch;

import io.github.limuqy.easyweb.core.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BatchCallTask<T> {

    private static final Logger log = LoggerFactory.getLogger(BatchCallTask.class);

    private final List<Callable<T>> tasks = new ArrayList<>();
    /**
     * 并发量
     */
    private int quantity = 10;
    private long timeout = 1;
    private TimeUnit unit = TimeUnit.HOURS;

    private BatchCallTask() {
    }

    public static <T> BatchCallTask<T> builder() {
        return new BatchCallTask<>();
    }

    public static <T> BatchCallTask<T> builder(T t) {
        return new BatchCallTask<>();
    }

    public static <T> BatchCallTask<T> builder(Class<T> clazz) {
        return new BatchCallTask<>();
    }

    public static <T> BatchCallTask<T> builder(int quantity) {
        BatchCallTask<T> callTask = new BatchCallTask<>();
        return callTask.parallel(quantity);
    }

    /**
     * 设置并行运行的线程数量
     */
    public BatchCallTask<T> parallel(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public BatchCallTask<T> task(Callable<T> callable) {
        tasks.add(ThreadUtil.wrap(callable));
        return this;
    }

    public BatchCallTask<T> timeout(long timeout) {
        this.timeout = timeout;
        this.unit = TimeUnit.MINUTES;
        return this;
    }

    public BatchCallTask<T> timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
        return this;
    }

    public List<T> run() {
        ExecutorService executorService = null;
        try {
            executorService = ThreadUtil.blockingVirtualService(Math.min(quantity, tasks.size()));
            List<Future<T>> futures = executorService.invokeAll(tasks, timeout, unit);
            long millis = System.currentTimeMillis();
            List<T> list = new ArrayList<>();
            for (Future<T> future : futures) {
                list.add(future.get());
            }
            log.debug("批量任务执行完毕，耗时：{}", System.currentTimeMillis() - millis);
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ThreadUtil.closeExecutor(executorService);
        }
    }

}
