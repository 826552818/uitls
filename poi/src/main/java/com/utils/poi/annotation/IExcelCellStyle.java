package com.utils.poi.annotation;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * <p/>
 * <li>Description: excel单元格样式接口 </li>
 * <li>@author: LiuDun </li>
 * <li>Date: 2018/6/22 10:54</li>
 */
public interface IExcelCellStyle {
    
    /**
     * 生成单元格样式接口
     *
     * @param wb sheet工作簿
     * @return 返回 cell style
     */
    public CellStyle getCellStyle(Workbook wb);
    
    /**
     * 获取格式化类
     * @param wb
     * @return
     */
    public DataFormat getDataFormat(Workbook wb);
    
}
