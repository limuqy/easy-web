package com.lingmu.easyweb.core.util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

    private static final String CODE_REGEX = "^[A-Za-z`~!@#$%^&*()_+=|;:',<.>/?0-9]{0,63}$";
    private static final String SORT_REGEX = "^[1-9]\\d*$";

    public static String valueOf(Object obj) {
        return valueOf(obj, EMPTY);
    }

    public static String valueOf(Object obj, String defaultValue) {
        if (ObjectUtil.isNull(obj)) {
            return defaultValue;
        }
        return obj.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String camelConvert(String name) {
        return name != null && !name.contains("_") ? upper2Underline(name) : name;
    }

    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String name) {
        return StrUtil.toCamelCase(name);
    }

    /**
     * 驼峰转下划线
     */
    public static String upper2Underline(String name) {
        return StrUtil.toUnderlineCase(name);
    }

    /**
     * 全角转半角
     */
    public static String fullWidthToHalfWidth(String fullWidthStr) {
        if (fullWidthStr == null) {
            return null;
        }
        char[] c = fullWidthStr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 65281 && c[i] <= 65374) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**
     * 半角转全角
     */
    public static String halfWidthToFullWidth(String halfWidthStr) {
        if (halfWidthStr == null) {
            return null;
        }
        char[] c = halfWidthStr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 33 && c[i] <= 126) {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 校验是否包含中文
     *
     * @param str 字符串
     * @return true：包含中文
     */
    public static boolean containsChinese(String str) {
        String regex = "[一-龥]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * 校验是否以英文字母开头，不管是否为大小写字母
     *
     * @param str 字符串
     * @return true：包含中文
     */
    public static boolean isStartWithEn(String str) {
        return isNotBlank(str) && str.matches("^[a-zA-Z].*");
    }

    /**
     * 校验是否以英文字母开头，不管是否为大小写字母
     *
     * @param str 字符串
     * @return true：不包含中文
     */
    public static boolean isNotStartWith(String str) {
        boolean flag = isStartWithEn(str);
        return !flag;
    }

    /**
     * 验证是否为英文字母 、数字和下划线
     *
     * @param str 字符串
     * @return true：是
     */
    public static boolean isGeneral(String str) {
        return isNotBlank(str) && Validator.isGeneral(str);
    }

    /**
     * 验证是否为英文字母 、数字和下划线
     *
     * @param str 字符串
     * @return true：否
     */
    public static boolean isNotGeneral(String str) {
        boolean flag = isGeneral(str);
        return !flag;
    }

    /**
     * 首字母变大写
     */
    public static String getFirstUpperCaseString(String value) {
        if (!value.isEmpty()) {
            char[] chars = value.toCharArray();
            if (chars[0] >= 'a' && chars[0] <= 'z') {
                chars[0] = (char) (chars[0] - 32);
            }
            return new String(chars);
        }
        return value;
    }

    /**
     * 首字母变小写
     */
    public static String getFirstLowerCaseString(String value) {
        if (!value.isEmpty()) {
            char[] chars = value.toCharArray();
            if (chars[0] >= 'A' && chars[0] <= 'Z') {
                chars[0] = (char) (chars[0] + 32);
            }
            return new String(chars);
        }
        return value;
    }


    /**
     * 获取第一个不为空的数据值
     */
    public static String getNotEmpty(String... arr) {
        return ObjectUtil.getNotEmpty(arr);
    }


    /**
     * 检查编码是否符合规则
     *
     * @param text 待校验字符串
     */
    public static boolean checkCodeFormat(String text) {
        if (isBlank(text)) {
            return false;
        }

        Pattern p = Pattern.compile(CODE_REGEX);
        Matcher m = p.matcher(text);
        return m.find();
    }

    /**
     * 检查排序是否符合规则
     *
     * @param text 待校验字符串
     */
    public static boolean checkSortFormat(String text) {
        if (isBlank(text)) {
            return false;
        }

        Pattern p = Pattern.compile(SORT_REGEX);
        Matcher m = p.matcher(text);
        return m.find();
    }

    /**
     * 将文件中的特殊字符转换成全角
     */
    public static String toFullWidth(String value) {
        if (isEmpty(value)) {
            return value;
        }
        return value.replaceAll("\\\\", "＼")
                .replaceAll("/", "／")
                .replaceAll(":", "：")
                .replaceAll("\\*", "＊")
                .replaceAll("\\?", "？")
                .replaceAll("<", "＜")
                .replaceAll(">", "＞")
                .replaceAll("\\|", "｜")
                .replaceAll("\"", "");
    }

    /**
     * 去除字符串中重复的空白换行
     */
    public static String trimFullString(String s) {
        if (isEmpty(s)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isTrim = false;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c <= ' ') {
                if (!isTrim) {
                    sb.append(' ');
                }
                isTrim = true;
            } else {
                isTrim = false;
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

}
