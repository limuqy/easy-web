package io.github.limuqy.easyweb.excel.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import io.github.limuqy.easyweb.core.util.DateUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
public class TimestampStringConverter implements Converter<Timestamp> {

    /**
     * 时区ID
     */
    private String zoneId;
    /**
     * 字符串日期格式
     */
    private DateTimeFormatter dateTimeFormatter;

    public TimestampStringConverter() {
    }

    public TimestampStringConverter(String zoneId) {
        this.zoneId = zoneId;
    }

    public TimestampStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public TimestampStringConverter(String zoneId, DateTimeFormatter dateTimeFormatter) {
        this.zoneId = zoneId;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return Timestamp.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Timestamp value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        DateTimeFormatter ofPattern = Optional.ofNullable(this.dateTimeFormatter).orElse(DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_FORMAT));
        String localZoneId = Optional.ofNullable(this.zoneId).orElse(DateUtil.DEFAULT_TIME_ZONE);
        return new WriteCellData<>(dateTime(value, localZoneId, ofPattern));
    }

    private String dateTime(Instant instant, String zoneId, DateTimeFormatter dateTimeFormatter) {
        if (Objects.isNull(instant)) {
            return null;
        }
        if (StringUtil.isBlank(zoneId)) {
            return instant.toString();
        }
        return LocalDateTime.ofInstant(instant, ZoneId.of(zoneId)).format(dateTimeFormatter);
    }

    private String dateTime(Timestamp timestamp, String zoneId, DateTimeFormatter dateTimeFormatter) {
        return dateTime(timestamp.toInstant(), zoneId, dateTimeFormatter);
    }
}