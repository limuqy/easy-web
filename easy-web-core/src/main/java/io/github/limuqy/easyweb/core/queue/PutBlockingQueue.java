package io.github.limuqy.easyweb.core.queue;

import lombok.NonNull;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 重写offer为阻塞操作
 */
public class PutBlockingQueue<T> extends LinkedBlockingQueue<T> {

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