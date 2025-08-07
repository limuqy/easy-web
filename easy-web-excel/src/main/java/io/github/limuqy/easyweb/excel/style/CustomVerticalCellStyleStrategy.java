package io.github.limuqy.easyweb.excel.style;


import cn.idev.excel.metadata.Head;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import cn.idev.excel.write.metadata.style.WriteFont;
import cn.idev.excel.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

public class CustomVerticalCellStyleStrategy extends AbstractVerticalCellStyleStrategy {

    /**
     * 重写定义表头样式的方法
     *
     * @param head 表头
     * @return 表头样式
     */
    @Override
    protected WriteCellStyle headCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
        WriteFont writeFont = new WriteFont();
        writeFont.setColor(IndexedColors.BLACK.getIndex());
        writeFont.setBold(false);
        writeFont.setFontHeightInPoints((short) 15);
        writeCellStyle.setWriteFont(writeFont);
        return writeCellStyle;
    }

    /**
     * 重写定义内容部分样式的方法
     *
     * @param head 表头
     * @return 内容部分样式
     */
    @Override
    protected WriteCellStyle contentCellStyle(Head head) {
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        writeCellStyle.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return writeCellStyle;
    }


}

