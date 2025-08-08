package io.github.limuqy.easyweb.core.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.text.CharSequenceUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class DateUtil extends cn.hutool.core.date.DateUtil {

    public final static String DATE_TIME_FORMAT = DatePattern.NORM_DATETIME_PATTERN;

    public final static String DATE_TIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss.S";
    public final static String MONTH_FORMAT = DatePattern.NORM_MONTH_PATTERN;
    public final static String DATE_FORMAT = DatePattern.NORM_DATE_PATTERN;
    public final static String DAY_FORMAT = "dd";
    public final static String TIME_FORMAT = DatePattern.NORM_TIME_PATTERN;
    public static final String DEFAULT_TIME_ZONE_ID = "Asia/Shanghai";
    public static final String DEFAULT_TIME_ZONE = "+08:00";

    /**
     * 获取当前浏览器请求所属的年月日时间戳
     * 请求头为空时，默认获取系统的时区
     *
     * @return 当前浏览器请求所属的年月日时间戳
     */
    public static long currentBrowserTimeMillis() {
        ZonedDateTime zonedDateTime = LocalDate.now()
                .atStartOfDay(ZoneId.of(DEFAULT_TIME_ZONE_ID));
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 获取当前浏览器请求所属的时区
     * 请求头为空时，默认获取系统的时区
     *
     * @return 当前浏览器请求所属的时区
     */
    public static String getHeaderZoneId() {
        return DEFAULT_TIME_ZONE_ID;
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前的时间戳
     */
    public static Timestamp timestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 获取当前时间戳
     */
    public static Timestamp timestamp(long timeMillis) {
        return new Timestamp(timeMillis);
    }


    /**
     * 获取当前时间戳
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 比较时间戳
     *
     * @return timestamp小于timestamp2返回true
     */
    public static boolean before(Timestamp timestamp, Timestamp timestamp2) {
        if (timestamp == null || timestamp2 == null) {
            return false;
        }
        return timestamp.before(timestamp2);
    }

    /**
     * Date转时间戳
     */
    public static Timestamp dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param instant           instant
     * @param zoneId            时区
     * @param dateTimeFormatter 日期格式
     * @return 字符串日期
     */
    public static String timestampToString(Instant instant, String zoneId, DateTimeFormatter dateTimeFormatter) {
        if (Objects.isNull(instant)) {
            return "";
        }
        if (CharSequenceUtil.isBlank(zoneId)) {
            return instant.toString();
        }
        return LocalDateTime.ofInstant(instant, ZoneId.of(zoneId)).format(dateTimeFormatter);
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param instant instant
     * @param zoneId  时区
     * @return 字符串日期
     */
    public static String timestampToString(Instant instant, String zoneId) {
        return timestampToString(instant, zoneId, DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_FORMAT));
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param instant instant
     * @return 字符串日期
     */
    public static String timestampToString(Instant instant) {
        return timestampToString(instant, getHeaderZoneId());
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param timestamp 时间戳
     * @return 字符串日期
     */
    public static String timestampToString(Timestamp timestamp) {
        return timestampToString(timestamp, getHeaderZoneId());
    }

    public static String timestampToDateString(Timestamp timestamp) {
        return timestampToString(timestamp, getHeaderZoneId(), DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT));
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param timestamp 时间戳
     * @param zoneId    时区
     * @return 字符串日期
     */
    public static String timestampToString(Timestamp timestamp, String zoneId) {
        return timestampToString(timestamp, zoneId, DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_FORMAT));
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param timestamp         时间戳
     * @param dateTimeFormatter 日期格式参数
     * @return 字符串日期
     */
    public static String timestampToString(Timestamp timestamp, DateTimeFormatter dateTimeFormatter) {
        return timestampToString(timestamp, getHeaderZoneId(), dateTimeFormatter);
    }

    /**
     * 时间戳转为当前浏览器请求头上的对应的时区字符串日期
     *
     * @param timestamp         时间戳
     * @param zoneId            时区
     * @param dateTimeFormatter 日期格式参数
     * @return 字符串日期
     */
    public static String timestampToString(Timestamp timestamp, String zoneId, DateTimeFormatter dateTimeFormatter) {
        if (Objects.isNull(timestamp) || timestamp.getTime() <= 0 || Objects.isNull(timestamp.toInstant())) {
            return "";
        }
        return timestampToString(timestamp.toInstant(), zoneId, dateTimeFormatter);
    }

    public static String monthFirstDay(Calendar cal, String pattern) {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DateFormatUtils.format(cal.getTime(), pattern);
    }

    public static String monthLastDay(Calendar cal, String pattern) {
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        return DateFormatUtils.format(cal.getTime(), pattern);
    }

    public static String yearFirstDay(Calendar cal, String pattern) {
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return DateFormatUtils.format(cal.getTime(), pattern);
    }

    public static String yearLastDay(Calendar cal, String pattern) {
        cal.roll(Calendar.DAY_OF_YEAR, -1);
        return DateFormatUtils.format(cal.getTime(), pattern);
    }

    public static Timestamp[] getYearBounds(String tz, String year) {
        if (StringUtil.isEmpty(tz)) {
            //默认时区
            tz = DEFAULT_TIME_ZONE_ID;
        }
        // 创建一个Calendar实例，并设置时区
        TimeZone timeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(timeZone);
        int yearInt = Integer.parseInt(year);
        // 设置年份的起始时间
        calendar.set(yearInt, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp startOfYear = new Timestamp(calendar.getTimeInMillis());
        // 设置年份的终止时间
        calendar.set(yearInt, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Timestamp endOfYear = new Timestamp(calendar.getTimeInMillis());
        // 返回包含起始和终止时间的Timestamp数组
        return new Timestamp[]{startOfYear, endOfYear};
    }

    public static Timestamp formatTimestamp(Timestamp timestamp, String format) {
        String formatTimestamp = format(timestamp, format);
        return new Timestamp(parse(formatTimestamp).getTime());
    }

    public static Timestamp getStartOrEndTime(Boolean isStart) {
        Timestamp timestamp;
        if (isStart) {
            // 获取今天的起始时间戳（午夜00:00:00）
            LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIDNIGHT);
            long startTimestampMillis = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            timestamp = new Timestamp(startTimestampMillis);
        } else {
            // 获取今天的终止时间戳（当天23:59:59.999）
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
            // 注意：由于Timestamp只精确到毫秒，我们可以忽略纳秒部分，或者使用withNano(0)来确保只使用毫秒
            long endTimestampMillis = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            timestamp = new Timestamp(endTimestampMillis);
        }
        return timestamp;
    }

    public static Long convertTimestamp(Long timestamp, boolean isMillis, boolean toMillis) {
        if (timestamp != null) {
            if (isMillis && toMillis) {
                // 已经是毫秒级，无需转换
                return timestamp;
            } else if (!isMillis && !toMillis) {
                // 已经是秒级，无需转换
                return timestamp;
            } else if (isMillis) {
                // 从毫秒级转换为秒级
                return timestamp / 1000;
            } else {
                // 从秒级转换为毫秒级
                return timestamp * 1000;
            }
        }
        return null;
    }

    /**
     * 某个时间戳增加X天（正数则加，负数则减）
     *
     * @param timestamp 时间戳
     * @param day       天数
     * @return 返回新的时间戳
     */
    public static Timestamp addDays(Timestamp timestamp, Integer day) {
        if (timestamp == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        // 增加30天
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp dateStringToTimestamp(String dateStr) {
        if (CharSequenceUtil.isBlank(dateStr)) {
            return null;
        }
        long timestamp = LocalDate.parse(dateStr).atTime(LocalTime.MIN).toEpochSecond(ZoneOffset.of(getHeaderZoneId()));
        return DateUtil.timestamp(timestamp);
    }

    public static Timestamp timeStringToTimestamp(String timeStr) {
        if (StringUtil.isEmpty(timeStr)) {
            return null;
        }
        long unixTimestamp = Long.parseLong(timeStr);
        return new Timestamp(unixTimestamp);
    }

    public static Timestamp dateStrToTimestamp(String dateStr) {
        if (CharSequenceUtil.isBlank(dateStr)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_DEFAULT);

        // 将字符串解析为LocalDateTime
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);

        // 将LocalDateTime转换为ZonedDateTime（默认使用UTC时区）
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC"));

        // 将ZonedDateTime转换为Instant，然后获取时间戳
        long timestamp = zonedDateTime.toInstant().toEpochMilli();
        return DateUtil.timestamp(timestamp);
    }

    /**
     * 获取当前时区的年份
     *
     * @return 当前时区的年份
     */
    public static Integer getCurrentYear() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(getHeaderZoneId()));
        return zonedDateTime.getYear();
    }

    /**
     * 日期转timestamp
     */
    public static Timestamp dateStrParseTimestamp(String dateString) {
        return dateStrParseTimestamp(dateString, DATE_FORMAT);
    }

    /**
     * 日期时间转timestamp
     */
    public static Timestamp dateTimeStrParseTimestamp(String dateString) {
        return dateStrParseTimestamp(dateString, DATE_TIME_FORMAT);
    }

    public static Timestamp dateStrParseTimestamp(String dateString, String format) {
        if (StringUtil.isBlank(dateString) || StringUtil.isBlank(format)) {
            return null;
        }
        ZoneId zoneId = ZoneId.of(getHeaderZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        Instant instant = zonedDateTime.toInstant();
        return Timestamp.from(instant);
    }

    /**
     * 获取时间所处季度
     */
    public static int getQuarter(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH) + 1;
        if (month <= 3) {
            return 1;
        } else if (month <= 6) {
            return 2;
        } else if (month <= 9) {
            return 3;
        } else if (month <= 12) {
            return 4;
        }
        return 0;
    }

    public static int getWeekYear(Date start) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        return c.get(Calendar.WEEK_OF_YEAR);
    }
}
