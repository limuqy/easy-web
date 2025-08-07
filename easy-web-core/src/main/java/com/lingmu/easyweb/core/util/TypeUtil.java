package com.lingmu.easyweb.core.util;

import com.lingmu.easyweb.core.exception.ErrorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class TypeUtil {

    private static final Logger log = LoggerFactory.getLogger(TypeUtil.class);
    private final static String[] FORMATS = new String[]{
            "yy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm",
            "yy-MM-dd",
            "yyyy-MM-dd",
            "yy/MM/dd",
            "yyyy/MM/dd"
    };

    /**
     * 将对象转换成指定类型
     */
    public static <T> T convert(Object value, Class<T> target) {
        if (value == null) {
            return null;
        }
        if (value.getClass().getName().equals(target.getName()) ||
                value.getClass().isAssignableFrom(target) ||
                Arrays.stream(value.getClass().getInterfaces()).anyMatch(item -> item.getName().equals(target.getName()))) {
            return target.cast(value);
        }
        T result = null;
        if (target == Integer.class) {
            result = target.cast(integerConvert(value));
        } else if (target == String.class) {
            result = target.cast(stringConvert(value));
        } else if (target == Date.class) {
            result = target.cast(dateConvert(value));
        } else if (target == Double.class) {
            result = target.cast(doubleConvert(value));
        } else if (target == Float.class) {
            result = target.cast(floatConvert(value));
        } else if (target == Long.class) {
            result = target.cast(longConvert(value));
        } else if (target == Boolean.class) {
            result = target.cast(booleanConvert(value));
        } else if (target == BigDecimal.class) {
            result = target.cast(bigDecimalConvert(value));
        } else if (target == Timestamp.class) {
            result = target.cast(new Timestamp(longConvert(value)));
        }
        return result;
    }

    private static Object bigDecimalConvert(Object value) {
        Class<?> clazz = value.getClass();
        BigDecimal result = null;
        if (clazz == Double.class) {
            result = new BigDecimal(value.toString());
        } else if (clazz == String.class) {
            result = new BigDecimal((String) value);
        } else if (clazz == Float.class) {
            result = BigDecimal.valueOf((Float) value);
        } else if (clazz == Integer.class) {
            result = new BigDecimal((Integer) value);
        } else if (clazz == Long.class) {
            result = new BigDecimal((Long) value);
        } else if (clazz == Short.class) {
            result = new BigDecimal((Short) value);
        }
        return result;
    }


    /**
     * Integer转换
     */
    public static Integer integerConvert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        int result;
        clazz = getBaseType(clazz);
        if (clazz == Integer.class) {
            result = (Integer) value;
        } else if (clazz == Long.class) {
            result = ((Long) value).intValue();
        } else if (clazz == String.class) {
            try {
                result = Integer.parseInt(value.toString());
            } catch (Exception ex) {
                throw new ErrorException("can not convert from %s==>%s to Integer", clazz.getName(), value.toString());
            }
        } else if (clazz == BigDecimal.class) {
            result = ((BigDecimal) value).intValue();
        } else if (clazz == Float.class) {
            result = ((Float) value).intValue();
        } else if (clazz == Double.class) {
            result = ((Double) value).intValue();
        } else {
            result = ((BigInteger) value).intValue();
        }
        return result;
    }

    public static Long longConvert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        long result;
        boolean bok = clazz == Integer.class
                || clazz == String.class
                || clazz == Double.class
                || clazz == BigDecimal.class
                || clazz == BigInteger.class
                || clazz == Float.class
                || clazz == Long.class
                || clazz == java.sql.Date.class
                || clazz == Timestamp.class
                || clazz == Time.class;
        if (!bok) {
            clazz = String.class;
        }
        if (clazz == Long.class) {
            result = (Long) value;
        } else if (clazz == Integer.class) {
            result = ((Integer) value).longValue();
        } else if (clazz == String.class) {
            try {
                result = Long.parseLong(value.toString());
            } catch (Exception ex) {
                try {
                    //可能是日期类型，尝试转换
                    Date date = dateParse(value.toString());
                    result = Objects.nonNull(date) ? date.getTime() / 1000 : 0L;
                } catch (Exception e) {
                    throw new ErrorException("can not convert from %s==>%s to Long", clazz.getName(), value.toString());
                }
            }
        } else if (clazz == BigDecimal.class) {
            result = ((BigDecimal) value).longValue();
        } else if (clazz == Float.class) {
            result = ((Float) value).longValue();
        } else if (clazz == Double.class) {
            result = ((Double) value).longValue();
        } else if (clazz == java.sql.Date.class) {
            result = ((java.sql.Date) value).getTime();
        } else if (clazz == Timestamp.class) {
            result = ((Timestamp) value).getTime();
        } else if (clazz == Time.class) {
            result = ((Time) value).getTime();
        } else {
            result = ((BigInteger) value).longValue();
        }
        return result;
    }

    public static String stringConvert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        String result;
        //字节数组需要做转换
        if (clazz == byte[].class) {
            result = new String((byte[]) value, StandardCharsets.UTF_8);
        } else if ("oracle.sql.CLOB".equals(clazz.getName()) ||
                "oracle.sql.NCLOB".equals(clazz.getName())) {
            result = TypeUtil.clobToString(value);
        } else if ("com.alibaba.druid.proxy.jdbc.ClobProxyImpl".equals(clazz.getName())) {
            result = TypeUtil.druidClobToString(value);
        } else {
            result = String.valueOf(value);
        }
        return result;
    }

    /**
     * druid数据源clob转换
     */
    private static String druidClobToString(Object rawClob) {
        try {
            Object clob = rawClob.getClass().getMethod("getRawClob").invoke(rawClob);
            return TypeUtil.clobToString(clob);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String clobToString(Object value) {
        String content = null;
        try {
            Reader reader = (Reader) value.getClass().getMethod("getCharacterStream").invoke(value);
            char[] buf = new char[1024];
            int size;
            StringBuilder str = new StringBuilder();
            do {
                size = reader.read(buf);
                if (size <= 0) {
                    break;
                }
                str.append(buf, 0, size);
            } while (true);
            content = str.toString();
            reader.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return content;
    }

    public static Double doubleConvert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        Double result = null;
        boolean bok = clazz == Integer.class
                || clazz == String.class
                || clazz == BigInteger.class
                || clazz == Double.class
                || clazz == BigDecimal.class
                || clazz == Float.class
                || clazz == Long.class;
        if (!bok) {
            clazz = String.class;
        }
        if (clazz == Integer.class) {
            result = ((Integer) value).doubleValue();
        } else if (clazz == Long.class) {
            result = ((Long) value).doubleValue();
        } else if (clazz == String.class) {
            try {
                result = Double.parseDouble(value.toString());
            } catch (Exception ex) {
                throw new ErrorException("can not convert from %s==>%s to Double", clazz.getName(), value.toString());
            }
        } else if (clazz == BigDecimal.class) {
            result = ((BigDecimal) value).doubleValue();
        } else if (clazz == Float.class) {
            result = ((Float) value).doubleValue();
        } else if (clazz == BigInteger.class) {
            result = ((BigInteger) value).doubleValue();
        }
        return result;
    }

    public static Float floatConvert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        Float result = null;
        clazz = getBaseType(clazz);
        if (clazz == Integer.class) {
            result = ((Integer) value).floatValue();
        } else if (clazz == Long.class) {
            result = ((Long) value).floatValue();
        } else if (clazz == String.class) {
            try {
                result = Float.parseFloat(value.toString());
            } catch (Exception ex) {
                throw new ErrorException("can not convert from %s==>%s to Float", clazz.getName(), value.toString());
            }
        } else if (clazz == BigDecimal.class) {
            result = ((BigDecimal) value).floatValue();
        } else if (clazz == Float.class) {
            result = ((Float) value);
        } else if (clazz == BigInteger.class) {
            result = ((BigInteger) value).floatValue();
        }
        return result;
    }

    private static Class<?> getBaseType(Class<?> clazz) {
        boolean bok = clazz == Integer.class
                || clazz == String.class
                || clazz == Double.class
                || clazz == BigDecimal.class
                || clazz == BigInteger.class
                || clazz == Float.class
                || clazz == Long.class;
        if (!bok) {
            clazz = String.class;
        }
        return clazz;
    }

    public static Date dateConvert(Object value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        Date result;
        boolean bok = clazz == java.sql.Date.class
                || clazz == Timestamp.class
                || clazz == Time.class
                || clazz == String.class
                || clazz == Long.class;
        if (!bok) {
            throw new ErrorException("can not convert from %s==>%s to Date", clazz.getName(), value.toString());
        }
        if (clazz == java.sql.Date.class) {
            result = new Date(((java.sql.Date) value).getTime());
        } else if (clazz == Long.class) {
            result = new Date((Long) value);
        } else if (clazz == String.class) {
            result = dateParse(value.toString());
        } else if (clazz == Timestamp.class) {
            result = new Date(((Timestamp) value).getTime());
        } else {
            result = new Date(((Time) value).getTime());
        }
        return result;
    }


    /**
     * bool值转换
     */
    private static Object booleanConvert(Object value) {
        boolean result = false;
        if (value instanceof String) {
            result = Boolean.parseBoolean((String) value);
        } else if (value instanceof Integer) {
            result = ((Integer) value) == 1;
        } else if (value instanceof Long) {
            result = ((Long) value) == 1L;
        } else if (value instanceof Byte) {
            result = ((Byte) value).intValue() == 1;
        }
        return result;
    }

    public static Date dateParse(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        try {
            int length = value.length();
            if (StringUtils.isNumeric(value) && length <= 13) {
                return new Date(Long.parseLong(value));
            }
            return DateUtils.parseDate(value, FORMATS);
        } catch (Exception e) {
            log.error("can not convert from String==>{} to Date", value, e);
            return null;
        }
    }

    /**
     * 判断是否是基本类型
     */
    public static boolean isBasicType(Class<?> clazz) {
        return clazz == Integer.class
                || clazz == String.class
                || clazz == Date.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Long.class
                || clazz == Byte.class
                || clazz == Boolean.class
                || clazz == Short.class
                || clazz == BigDecimal.class;
    }
}
