package com.utils.poi;

import com.utils.poi.annotation.IExcelCellStyle;

import java.lang.reflect.Field;

/**
 * <p>
 * <li>Description: 属性字段信息</li>
 * <li>@author: LiuDun </li>
 * <li>@date 2018-06-22 10:01:43</li>
 */
public class FieldForSortting {
    /**
     * <li>field :类字段信息 </li>
     */
    private Field field;
    
    /**
     * <li>index :排序 </li>
     */
    private int index;
    
    /**
     * 该字段的单元格信息
     */
    private IExcelCellStyle cellStyle;
    
    /**
     * <li> 构造函数. </li>
     *
     * @param field 字段
     */
    public FieldForSortting(Field field) {
        super();
        this.field = field;
    }
    
    /**
     * <li> 构造函数. </li>
     *
     * @param field 字段 
     * @param index 所在序号
     */
    public FieldForSortting(Field field, int index) {
        super();
        this.field = field;
        this.index = index;
    }
    
    /**
     * <li> 构造函数. </li>
     *
     * @param field 字段
     * @param index 所在排序
     * @param cellStyle 单元格样式
     */
    public FieldForSortting(Field field, int index, IExcelCellStyle cellStyle) {
        super();
        this.field = field;
        this.index = index;
        this.cellStyle = cellStyle;
    }
    
    /**
     * field 的getter方法 
     * @return the field
     */
    public Field getField() {
        return field;
    }
    
    /**
     * field 的setter方法
     *
     * @param field the field to set
     */
    public void setField(Field field) {
        this.field = field;
    }
    
    /**
     * index 的getter方法 
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * index 的setter方法
     *
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
    /**
     * cell style 的getter方法 
     * @return 返回 cell style
     */
    public IExcelCellStyle getCellStyle() {
        
        return cellStyle;
    }
    
    /**
     * cell style 的setter方法
     *
     * @param cellStyle the cell style
     */
    public void setCellStyle(IExcelCellStyle cellStyle) {
        
        this.cellStyle = cellStyle;
    }
}
