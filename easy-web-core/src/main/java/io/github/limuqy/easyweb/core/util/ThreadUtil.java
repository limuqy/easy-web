package io.github.limuqy.easyweb.core.util;

import io.github.limuqy.easyweb.core.context.AppContext;
import io.github.limuqy.easyweb.core.queue.PutBlockingQueue;
import io.github.limuqy.easyweb.model.core.UserProfile;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    private final static ExecutorService EXECUTOR_SERVICE = virtualExecutor();

    public static ExecutorService virtualExecutor() {
        return blockingVirtualService(Runtime.getRuntime().availableProcessors(), Integer.MAX_VALUE);
    }

    public static void startVirtualThread(Runnable task) {
        EXECUTOR_SERVICE.execute(wrap(task));
    }

    public static void startAsync(Runnable task) {
        EXECUTOR_SERVICE.execute(wrap(task));
    }

    public static void start(Runnable task) {
        try {
            EXECUTOR_SERVICE.submit(wrap(task)).get();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static <T> Callable<T> wrap(final Callable<T> callable) {
        UserProfile userProfile = AppContext.getUserProfile();
        String traceId = TraceIdUtil.getTraceId();
        return () -> {
            if (StringUtil.isNoneBlank(traceId)) {
                TraceIdUtil.setTraceId(traceId);
            }
            AppContext.setUserProfile(userProfile);
            return callable.call();
        };
    }

    public static Runnable wrap(final Runnable runnable) {
        UserProfile userProfile = AppContext.getUserProfile();
        String traceId = TraceIdUtil.getTraceId();
        return () -> {
            if (StringUtil.isNoneBlank(traceId)) {
                TraceIdUtil.setTraceId(traceId);
            }
            AppContext.setUserProfile(userProfile);
            runnable.run();
        };
    }

    public static ExecutorService blockingVirtualService(int corePoolSize, int maxQueue) {
        return new ThreadPoolExecutor(0, corePoolSize, 5L, TimeUnit.MINUTES, new PutBlockingQueue<>(maxQueue));
    }

    public static ExecutorService blockingVirtualService(int maxQueue) {
        return new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 5L, TimeUnit.MINUTES, new PutBlockingQueue<>(maxQueue));
    }

    public static void closeExecutor(ExecutorService executorService) {
        if (Objects.isNull(executorService)) {
            return;
        }
        boolean terminated = executorService.isTerminated();
        if (!terminated) {
            executorService.shutdown();
            boolean interrupted = false;
            while (!terminated) {
                try {
                    terminated = executorService.awaitTermination(2L, TimeUnit.HOURS);
                } catch (InterruptedException e) {
                    if (!interrupted) {
                        executorService.shutdownNow();
                        interrupted = true;
                    }
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
