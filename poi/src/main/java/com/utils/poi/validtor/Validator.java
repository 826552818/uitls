package com.utils.poi.validtor;

import com.utils.poi.annotation.ExcelLogs;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;

/**
 * 用于验证单元格的数据
 * 
 * @see {@link com.utils.poi.validtor.DefaultValidtor}
 * @author luoshouqiang
 * 
 *         2016年9月12日
 */
public interface Validator {
    
    public boolean valid(Cell cell, Field field, int cellNum, int rowNum, ExcelLogs logs);
}
