package com.utils.poi.annotation;

/**
 * <p>
 * <li>Description: 解析错误记录日志</li>
 * <li>@author: LiuDun </li>
 * <li>@date 2018-06-21 15:11:45</li>
 */
public class ExcelLog {
    
    /**
     * <li>rowNum :错误行号 </li>
     */
    private Integer rowNum;
    
    /**
     * <li>colNum :错误列号 </li>
     */
    private Integer colNum;
    
    /**
     * <li>object :错误数据 </li>
     */
    private Object object;
    
    /**
     * <li>log :验证出错原因 </li>
     */
    private String log;
    
    /**
     * row num 的getter方法 
     * @return the rowNum
     */
    public Integer getRowNum() {
        return rowNum;
    }
    
    /**
     * row num 的setter方法
     *
     * @param rowNum the rowNum to set
     */
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
    
    /**
     * object 的getter方法 
     * @return the object
     */
    public Object getObject() {
        return object;
    }
    
    /**
     * object 的setter方法
     *
     * @param object the object to set
     */
    public void setObject(Object object) {
        this.object = object;
    }
    
    /**
     * log 的getter方法 
     * @return the log
     */
    public String getLog() {
        return log;
    }
    
    /**
     * <li> TODO 的构造函数. </li>
     *
     * @param object the object    
     * @param log the log
     */
    public ExcelLog(Object object, String log) {
        super();
        this.object = object;
        this.log = log;
    }
    
    /**
     * <li> TODO 的构造函数. </li>
     *
     * @param log the log
     */
    public ExcelLog(String log) {
        super();
        this.log = log;
    }
    
    /**
     * <li> TODO 的构造函数. </li>
     *
     * @param object the object    
     * @param log the log    
     * @param rowNum the row num
     */
    public ExcelLog(Object object, String log, Integer rowNum) {
        super();
        this.rowNum = rowNum;
        this.object = object;
        this.log = log;
    }
    
    /**
     * log 的setter方法
     *
     * @param log the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }
    
}
