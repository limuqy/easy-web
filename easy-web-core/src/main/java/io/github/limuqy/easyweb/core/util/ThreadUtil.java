package io.github.limuqy.easyweb.core.util;

import io.github.limuqy.easyweb.core.context.AppContext;
import io.github.limuqy.easyweb.core.queue.PutBlockingQueue;
import io.github.limuqy.easyweb.model.core.UserProfile;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class ThreadUtil extends cn.hutool.core.thread.ThreadUtil {

    private final static ExecutorService EXECUTOR_SERVICE = virtualExecutor();

    public static ExecutorService virtualExecutor() {
        return blockingVirtualService(Runtime.getRuntime().availableProcessors());
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
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (MapUtil.isNotEmpty(contextMap)) {
                MDC.setContextMap(contextMap);
            }
            AppContext.setUserProfile(userProfile);
            return callable.call();
        };
    }

    public static Runnable wrap(final Runnable runnable) {
        UserProfile userProfile = AppContext.getUserProfile();
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (MapUtil.isNotEmpty(contextMap)) {
                MDC.setContextMap(contextMap);
            }
            AppContext.setUserProfile(userProfile);
            runnable.run();
        };
    }

    public static ExecutorService blockingVirtualService(int corePoolSize, int maxQueue) {
        return new ThreadPoolExecutor(corePoolSize, corePoolSize, 5L, TimeUnit.MINUTES, new PutBlockingQueue<>(maxQueue));
    }

    public static ExecutorService blockingVirtualService(int corePoolSize) {
        return blockingVirtualService(corePoolSize, Short.MAX_VALUE);
    }

    public static ExecutorService blockingVirtualService() {
        return blockingVirtualService(Runtime.getRuntime().availableProcessors());
    }

    public static void closeExecutor(ExecutorService executorService) {
        closeExecutor(executorService, 2L, TimeUnit.HOURS);
    }

    public static void closeExecutor(ExecutorService executorService, Long timeout, TimeUnit unit) {
        if (Objects.isNull(executorService)) {
            return;
        }
        boolean terminated = executorService.isTerminated();
        if (!terminated) {
            executorService.shutdown();
            boolean interrupted = false;
            while (!terminated) {
                try {
                    terminated = executorService.awaitTermination(timeout, unit);
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
