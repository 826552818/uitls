package com.utils.poi.annotation;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * excel错误日志解析集合
 *
 * @author luoshouqiang
 *         <p>
 *         2016年9月13日
 */
public class ExcelLogs {
    
    private List<ExcelLog> logList = new ArrayList<ExcelLog>();
    
    /**
     *
     */
    public ExcelLogs() {
        
        super();
    }
    
    /**
     * @return the hasError
     */
    public Boolean getHasError() {
        
        return logList.size() > 0;
    }
    
    /**
     * @return the logList
     */
    public List<ExcelLog> getLogList() {
        
        return logList;
    }
    
    public void addLog(ExcelLog log) {
        
        this.logList.add(log);
    }
    
    public List<ExcelLog> getErrorLogList() {
        
        List<ExcelLog> errList = new ArrayList<ExcelLog>();
        for (ExcelLog log : this.logList) {
            if (log != null && StringUtils.isNotBlank(log.getLog())) {
                errList.add(log);
            }
        }
        return errList;
    }
    
    /**
     * @param logList the logList to set
     */
    public void setLogList(List<ExcelLog> logList) {
        
        this.logList = logList;
    }
    
}
