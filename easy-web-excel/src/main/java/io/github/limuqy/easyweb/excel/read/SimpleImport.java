package io.github.limuqy.easyweb.excel.read;

import cn.idev.excel.FastExcel;
import cn.idev.excel.read.listener.ReadListener;
import io.github.limuqy.easyweb.excel.read.listener.BatchImportListener;
import io.github.limuqy.easyweb.excel.read.listener.SimpleImportListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;

public class SimpleImport<T> {
    private final Class<T> clazz;
    private Consumer<List<T>> consumer = (list) -> {
    };
    private final InputStream inputStream;
    private Integer limit = 1000;
    private ReadListener<T> listener;

    private SimpleImport(InputStream inputStream, Class<T> clazz) {
        this.inputStream = inputStream;
        this.clazz = clazz;
    }

    public static <T> SimpleImport<T> build(InputStream inputStream, Class<T> clazz) {
        return new SimpleImport<>(inputStream, clazz);
    }

    public static <T> SimpleImport<T> build(MultipartFile file, Class<T> clazz) {
        try {
            return new SimpleImport<>(file.getInputStream(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> SimpleImport<T> build(File file, Class<T> clazz) {
        try {
            return new SimpleImport<>(Files.newInputStream(file.toPath()), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleImport<T> limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public SimpleImport<T> listener(ReadListener<T> listener) {
        this.listener = listener;
        return this;
    }

    public void doImport(Consumer<List<T>> consumer) {
        this.consumer = consumer;
        if (this.listener == null) {
            this.listener = new SimpleImportListener<>(consumer, limit);
        }
        doImport();
    }

    public void doBatchImport(Consumer<List<T>> consumer) {
        this.consumer = consumer;
        if (this.listener == null) {
            this.listener = new BatchImportListener<>(consumer, limit, 8);
        }
        doImport();
    }

    private void doImport() {
        FastExcel.read(inputStream, clazz, listener).doReadAll();
    }

}
