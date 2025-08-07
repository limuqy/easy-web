package io.github.limuqy.easyweb.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HighlightUtil {

    public static final String LEFT_TAG = "<font style='color:red;'>";
    public static final String RIGHT_TAG = "</font>";

    public static String getHighlightText(String keywordText, String str) {
        if (StringUtil.fullWidthToHalfWidth(keywordText).equals(StringUtil.fullWidthToHalfWidth(str))) {
            return LEFT_TAG + str + RIGHT_TAG;
        }
        return getHighlightText(getParticiple(keywordText), str);
    }

    private static List<String> getParticiple(String keywordText) {
        if (keywordText.length() < 3) {
            return Collections.singletonList(keywordText);
        }
        List<String> list = new ArrayList<>();
        int length = keywordText.length();
        for (int i = 0; i < length - 1; i++) {
            for (int j = i + 1; j < i + 3; j++) {
                if (j == length) {
                    break;
                }
                list.add(keywordText.substring(i, j + 1));
            }
        }
        list.addAll(list.stream().map(StringUtil::fullWidthToHalfWidth).collect(Collectors.toList()));
        list.addAll(list.stream().map(StringUtil::halfWidthToFullWidth).collect(Collectors.toList()));
        Comparator<String> comparator = Comparator.comparingInt(String::length);
        if (keywordText.length() % 2 == 1) {
            comparator = comparator.reversed();
        }
        return list.stream()
                .distinct()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public static String getHighlightText(List<String> list, String str) {
        return getHighlightText(list, str, LEFT_TAG, RIGHT_TAG);
    }

    /**
     * @param list     数据源匹配 例如： {"难的","这世界","世界上最", "世界上最难", "最难的职业","意志","坚韧不拔","职业就是","程序员","他们"}
     * @param text     你所要处理的字符串 例如： 世界上最难的职业
     * @param leftTag  左标签字符串 例如：<font style='color:red;'>
     * @param rightTag 右标签字符串 例如：</font>
     */
    public static String getHighlightText(List<String> list, String text, String leftTag, String rightTag) {
        if (StringUtil.isBlank(text)) {
            return text;
        }
        for (String str : list) {
            text = text.replace(str, leftTag + str + rightTag);
        }
        return replace(text, rightTag, leftTag);
    }

    public static String replace(String text) {
        return replace(text, RIGHT_TAG, LEFT_TAG);
    }

    public static String replace(String text, String rightTag, String leftTag) {
        return text.replace(rightTag + leftTag, "");
    }

}

