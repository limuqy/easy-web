package io.github.limuqy.easyweb.core.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.limuqy.easyweb.core.annotation.RowId;
import io.github.limuqy.easyweb.core.annotation.RowIdEntity;
import io.github.limuqy.easyweb.core.config.EasyWebProperties;
import io.github.limuqy.easyweb.core.config.RowIdProperties;
import io.github.limuqy.easyweb.core.exception.RowIdException;
import io.github.limuqy.easyweb.model.mybatis.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RowIdUtil {
    private static final Map<String, SymmetricCrypto> sm4Map = new ConcurrentHashMap<>();
    private static String secret;
    private static Boolean decryptNumber;

    private static String getSecret() {
        if (Objects.isNull(secret)) {
            EasyWebProperties easyWebProperties = SpringUtil.getBean(EasyWebProperties.class);
            if (Objects.nonNull(easyWebProperties) && Objects.nonNull(easyWebProperties.getRowId())) {
                RowIdProperties rowId = easyWebProperties.getRowId();
                secret = rowId.getSecret();
            }
        }
        return secret;
    }

    public static boolean isDisable() {
        return StringUtil.isEmpty(getSecret());
    }

    public static boolean isDecryptNumber() {
        if (Objects.isNull(decryptNumber)) {
            EasyWebProperties easyWebProperties = SpringUtil.getBean(EasyWebProperties.class);
            if (Objects.nonNull(easyWebProperties) && Objects.nonNull(easyWebProperties.getRowId())) {
                RowIdProperties rowId = easyWebProperties.getRowId();
                decryptNumber = rowId.getDecryptNumber();
            }
        }
        return decryptNumber;
    }

    private static SymmetricCrypto getSm4(String tableName) {
        tableName = tableName.toUpperCase();
        SymmetricCrypto symmetricCrypto = sm4Map.get(tableName);
        if (symmetricCrypto == null) {
            byte[] key = MD5.create().digest(getSecret() + tableName, CharsetUtil.CHARSET_UTF_8);
            symmetricCrypto = SmUtil.sm4(key);
            sm4Map.put(tableName, symmetricCrypto);
        }
        return symmetricCrypto;
    }

    private static String getTableName(Class<?> clazz) {
        String tableName = "";
        if (clazz != null && !clazz.equals(Object.class)) {
            TableName clazzAnnotation = clazz.getAnnotation(TableName.class);
            if (Objects.isNull(clazzAnnotation)) {
                RowIdEntity rowIdEntityAnnotation = clazz.getAnnotation(RowIdEntity.class);
                if (Objects.nonNull(rowIdEntityAnnotation)) {
                    tableName = rowIdEntityAnnotation.tableName();
                }
            } else {
                tableName = clazzAnnotation.value();
            }
            if (StringUtil.isEmpty(tableName) && clazz.getSuperclass() != Object.class && clazz.getSuperclass() != BaseEntity.class) {
                tableName = getTableName(clazz.getSuperclass());
            }
            if (StringUtil.isEmpty(tableName)) {
                tableName = clazz.getName();
            }
            return tableName;
        }
        return tableName;
    }

    public static String encryptRowId(Number id) {
        return encryptRowId(id, "");
    }

    public static String encryptRowId(Number id, Class<?> clazz) {
        return encryptRowId(id, getTableName(clazz));
    }

    public static String encryptRowId(Number id, String tableName) {
        if (id == null) {
            return null;
        }
        // 64进制加密
        String text = ConversionUtil.encode(id.longValue());
        return getSm4(tableName).encryptHex(text, CharsetUtil.CHARSET_UTF_8);
    }

    public static Long decryptRowId(String value) {
        return decryptRowId(value, "");
    }

    public static List<Long> decryptRowIdList(List<String> rowIdList, Class<?> clazz) {
        String tableName = getTableName(clazz);
        List<Long> idList = new ArrayList<>();
        rowIdList.forEach(item -> idList.add(decryptRowId(item, tableName)));
        return idList;
    }

    public static Long decryptRowId(String rowId, Class<?> clazz) {
        return decryptRowId(rowId, getTableName(clazz));
    }

    public static Long decryptRowId(String rowId, String tableName) {
        try {
            if (StringUtil.isBlank(rowId)) {
                return null;
            }
            // 这个会导致ID不安全
            if (rowId.length() <= 13 && NumberUtil.isNumber(rowId) && isDecryptNumber()) {
                return Long.parseLong(rowId);
            }
            String decryptStr;
            try {
                decryptStr = getSm4(tableName).decryptStr(rowId, CharsetUtil.CHARSET_UTF_8);
            } catch (Exception e) {
                throw new RowIdException(HttpStatus.NOT_ACCEPTABLE, "Id is illegal");
            }
            // 94进制解密
            return ConversionUtil.decode(decryptStr);
        } catch (Exception e) {
            if (e instanceof RowIdException) {
                throw e;
            }
            throw new RowIdException(HttpStatus.INTERNAL_SERVER_ERROR, "ID decode fail！");
        }
    }

    public static String getEntityTableName(RowId rowId, Class<?> beanClazz) {
        if (StringUtil.isNotEmpty(rowId.tableName())) {
            return rowId.tableName();
        }
        Class<?> clazz;
        if (rowId.value() == BaseEntity.class && beanClazz != null) {
            RowIdEntity annotation = beanClazz.getAnnotation(RowIdEntity.class);
            if (annotation != null && StringUtil.isNotEmpty(annotation.tableName())) {
                return annotation.tableName();
            }
            clazz = annotation != null ? annotation.value() : beanClazz;
        } else {
            clazz = rowId.value();
        }
        return getTableName(clazz);
    }
}
