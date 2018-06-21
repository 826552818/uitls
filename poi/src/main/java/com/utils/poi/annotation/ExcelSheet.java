package com.utils.poi.annotation;

import java.util.Collection;

/**
 * <p>
 * <li>Description: 用来处理sheet和数据</li>
 * <li>@author: LiuDun </li>
 * <li>@date 2018-06-21 15:10:15</li>
 */
public class ExcelSheet<T> {
    /**
     * <li>sheetName :sheet名称 </li>
     */
    private String sheetName;
    
    /**
     * <li>headers :标题集合 </li>
     */
    private String[] headers;
    
    /**
     * <li>dataset :数据集合 </li>
     */
    private Collection<T> dataset;
    
    /**
     * sheet name 的getter方法 
     * @return the sheetName
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * Excel页签名称
     *
     * @param sheetName the sheetName to set
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    /**
     * Excel表头
     *
     * @return the headers
     */
    public String[] getHeaders() {
        return headers;
    }
    
    /**
     * headers 的setter方法
     *
     * @param headers the headers to set
     */
    public void setHeaders(String[] headers) {
        this.headers = headers;
    }
    
    /**
     * Excel数据集合
     *
     * @return the dataset
     */
    public Collection<T> getDataset() {
        return dataset;
    }
    
    /**
     * dataset 的setter方法
     *
     * @param dataset the dataset to set
     */
    public void setDataset(Collection<T> dataset) {
        this.dataset = dataset;
    }
    
}
