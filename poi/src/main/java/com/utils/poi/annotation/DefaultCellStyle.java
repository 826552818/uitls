package com.utils.poi.annotation;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * <p/>
 * <li>Description: 默认的单元格样式 </li>
 * <li>@author: LiuDun </li>
 * <li>Date: 2018/6/22 11:06</li>
 */
public class DefaultCellStyle implements IExcelCellStyle {
    
    /**
     * <li>cellStyle :单元格样式 </li>
     */
    private static CellStyle cellStyle = null;
    
    /**
     * <li>dataFormat :数据格式 </li>
     */
    private static DataFormat dataFormat = null;
    
    /**
     * 创建cell样式实体类
     * @param wb excel工作簿
     * @return
     */
    private CellStyle createCellStyle(Workbook wb) {
        
        if (cellStyle == null) {
            synchronized (DefaultCellStyle.class) {
                if (cellStyle == null) {
                    cellStyle = wb.createCellStyle();
                }
            }
        }
        return cellStyle;
    }
    
    /**
     * 获取数据格式类
     * @param wb
     * @return
     */
    private DataFormat createDataFormat(Workbook wb) {
        
        if (dataFormat == null) {
            synchronized (DefaultCellStyle.class) {
                if (dataFormat == null) {
                    dataFormat = wb.createDataFormat();
                }
            }
        }
        return dataFormat;
        
    }
    
    /**
     * 生成单元格样式接口
     *
     * @param wb the wb 
     * @return 返回 cell style
     */
    @Override
    public CellStyle getCellStyle(Workbook wb) {
        
        CellStyle cellStyle = createCellStyle(wb);
        
        //水平居中
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        
        // 背景色
        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        
        // 设置边框
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 生成一个字体
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setColor(HSSFColor.RED.index);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        // 自动换行
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        
        return cellStyle;
    }
    
    /**
     * 获取格式化类
     *
     * @param wb
     *
     * @return
     */
    @Override
    public DataFormat getDataFormat(Workbook wb) {
        
        return createDataFormat(wb);
    }
}
