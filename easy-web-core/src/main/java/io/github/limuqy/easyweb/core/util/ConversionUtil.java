package io.github.limuqy.easyweb.core.util;

/**
 * 10进制、64进制互转
 */
public class ConversionUtil {

    private static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+/";
    private static final long scale = chars.length();

    /**
     * 将数字转为94进制
     *
     * @param num Long 型数字
     * @return 94进制字符串
     */
    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();
        int remainder;
        while (num > scale - 1) {
            // 对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转（reverse）字符串
            remainder = (int) (num % scale);
            sb.append(chars.charAt(remainder));
            num = num / scale;
        }

        sb.append(chars.charAt((int) num));
        return sb.reverse().toString();
    }

    /**
     * 94进制字符串转为数字
     *
     * @param str 编码后的94进制字符串
     * @return 解码后的 10 进制字符串
     */
    public static long decode(String str) {
        long num = 0;
        long index;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            // 查找字符的索引位置
            index = chars.indexOf(str.charAt(i));
            // 索引位置代表字符的数值
            num += index * pow(length - i - 1);
        }
        return num;
    }

    private static long pow(long y) {
        long square = 1;
        for (int i = 0; i < y; i++) {
            square *= scale;
        }
        return square;
    }

    public static void main(String[] args) {
        long numId = 1800766735060049921L;
        System.out.println(numId);
        String rowId = encode(numId);
        System.out.println(rowId);
        Long id = decode(rowId);
        System.out.println(id);
    }
}
