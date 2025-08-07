package io.github.limuqy.easyweb.excel.read;

import cn.idev.excel.FastExcel;
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

    public void doImport(Consumer<List<T>> consumer) {
        this.consumer = consumer;
        doImport();
    }

    private void doImport() {
        SimpleImportListener<T> importListener = new SimpleImportListener<>(consumer);
        FastExcel.read(inputStream, clazz, importListener).doReadAll();
    }

}
