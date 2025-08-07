package com.lingmu.easyweb.core.util;

import com.lingmu.easyweb.core.context.AppContext;
import com.lingmu.easyweb.core.context.UserProfile;
import lombok.NonNull;

import java.util.Objects;
import java.util.concurrent.*;

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
        return () -> {
            AppContext.setUserProfile(userProfile);
            return callable.call();
        };
    }

    public static Runnable wrap(final Runnable runnable) {
        UserProfile userProfile = AppContext.getUserProfile();
        return () -> {
            AppContext.setUserProfile(userProfile);
            runnable.run();
        };
    }

    public static ExecutorService blockingVirtualService(int corePoolSize, int maxQueue) {
        return new ThreadPoolExecutor(0, corePoolSize, 1L, TimeUnit.MINUTES, new PutBlockingQueue<>(maxQueue));
    }

    /**
     * 重写offer为阻塞操作
     */
    private static class PutBlockingQueue<T> extends LinkedBlockingQueue<T> {

        public PutBlockingQueue(int size) {
            super(size);
        }

        @Override
        public boolean offer(@NonNull T t) {
            try {
                put(t);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
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
