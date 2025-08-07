package com.lingmu.easyweb.core.util;

import java.net.InetAddress;
import java.util.regex.Pattern;

public class URLUtil extends cn.hutool.core.util.URLUtil {

    /**
     * 校验ip地址是否合法
     */
    private static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean isValidIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }
        return IP_PATTERN.matcher(ipAddress).matches() && validateWithInetAddress(ipAddress);
    }

    private static boolean validateWithInetAddress(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.getHostAddress().equals(ipAddress);
        } catch (Exception e) {
            return false;
        }
    }

}
