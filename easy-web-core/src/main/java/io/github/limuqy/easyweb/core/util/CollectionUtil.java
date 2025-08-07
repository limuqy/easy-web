package io.github.limuqy.easyweb.core.util;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionUtil extends CollUtil {

    public static boolean isAllEmpty(Collection<?> collection) {
        return isEmpty(collection) || IterUtil.isAllNull(collection);
    }

    public static boolean isAllNotEmpty(Collection<?> collection) {
        return !isAllEmpty(collection);
    }

    public static boolean isEmpty(String[] values) {
        return values == null || values.length == 0;
    }

    public static boolean isNotEmpty(String[] values) {
        return !isEmpty(values);
    }

    /**
     * 从传入的集合中随机获取requireSize大小的集合
     *
     * @param list        集合
     * @param requireSize 所需大小
     * @param <T>         泛型
     */
    public static <T> List<T> getRandomList(List<T> list, int requireSize) {
        if (list == null || list.isEmpty() || requireSize <= 0) {
            return newArrayList();
        }

        int size = list.size();
        if (requireSize >= size) {
            // 如果请求的数量大于或等于集合大小，直接返回集合的副本
            Collections.shuffle(new ArrayList<>(list));
            return list;
        }

        // 创建一个包含集合索引的列表
        List<Integer> indices = IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toList());

        // 直接打乱索引列表
        Collections.shuffle(indices, ThreadLocalRandom.current());

        // 使用打乱的索引从原始集合中选取元素
        return indices.stream()
                .limit(requireSize)
                .map(list::get)
                .collect(Collectors.toList());
    }


    public static <T> List<T> newEmptyArrayList() {
        return new ArrayList<>(0);
    }

    @SafeVarargs
    public static <T> ArrayList<T> combineNonNull(T... values) {
        return removeNull(newArrayList(values));
    }


}
