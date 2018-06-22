package com.utils.poi.validtor;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import com.utils.poi.annotation.ExcelCell;
import com.utils.poi.log.ExcelLog;
import com.utils.poi.log.ExcelLogs;

/**
 * Validator的默认实现
 * 
 * @author luoshouqiang
 *
 *         2016年9月12日
 */
public class DefaultValidator implements Validator {
    
    /**
     * 用来验证excel与Vo中的类型是否一致 <br>
     * Map<栏位类型,只能是哪些Cell类型>
     */
    private static Map<Class<?>, Integer[]> validateMap = new HashMap<Class<?>, Integer[]>();
    
    static {
        validateMap.put(String[].class, new Integer[] { Cell.CELL_TYPE_STRING });
        validateMap.put(Double[].class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(String.class, new Integer[] { Cell.CELL_TYPE_STRING });
        validateMap.put(Double.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(Date.class, new Integer[] { Cell.CELL_TYPE_NUMERIC, Cell.CELL_TYPE_STRING });
        validateMap.put(Integer.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(int.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(Float.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(float.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(Long.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(long.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(double.class, new Integer[] { Cell.CELL_TYPE_NUMERIC });
        validateMap.put(Boolean.class, new Integer[] { Cell.CELL_TYPE_BOOLEAN });
    }
    
    @Override
    public boolean valid(Cell cell, Field field, int cellNum, int rowNum, ExcelLogs logs) {
        Integer[] integers = validateMap.get(field.getType());
        
        if (integers == null) {
            String msg = MessageFormat
                    .format("[{0}]行[{1}]列不支持这个单元格式 [{2}]", rowNum, cellNum, field.getType().getSimpleName());
            ExcelLog errorLog = new ExcelLog(getCellValue(cell), msg);
            logs.addLog(errorLog);
            return false;
        }
        
        ExcelCell annoCell = field.getAnnotation(ExcelCell.class);
        // 非空验证
        if (!nullCheck(cell, annoCell, cellNum, rowNum, logs)) {
            return false;
        }
        // 时间验证
        if (!dateValidate(cell, annoCell, cellNum, rowNum, logs)) {
            return false;
        }
        // 如果類型不在指定範圍內,並且沒有默認值
        // 类型符合验证,但值不在要求范围内的
        // String in
        if (!stringInValid(cell, annoCell, cellNum, rowNum, logs)) {
            return false;
        }
        // 数字型
        if (!compareNumber(cell, annoCell, cellNum, rowNum, logs)) {
            return false;
        }
        // 正则
        if (!validateRegex(cell, annoCell, cellNum, rowNum, logs)) {
            return false;
        }
        return true;
        
    }
    
    private boolean stringInValid(Cell cell, ExcelCell annoCell, int cellNum, int rowNum, ExcelLogs logs) {
        
        if (annoCell.valid().in().length != 0 && cell != null) {
            String[] in = annoCell.valid().in();
            Object obj = getCellValue(cell);
            String cellValue = String.valueOf(obj);
            boolean isIn = false;
            for (String str : in) {
                if (str.equals(cellValue)) {
                    isIn = true;
                }
            }
            if (!isIn) {
                String msg = MessageFormat.format("[{0}]行[{1}]列的值必须在 {2}中", rowNum, cellNum, Arrays.toString(in));
                logs.addLog(new ExcelLog(cellValue, msg));
                return false;
            }
        }
        return true;
    }
    
    private boolean dateValidate(Cell cell, ExcelCell annoCell, int cellNum, int rowNum, ExcelLogs logs) {
        try {
            if (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                Double value = cell.getNumericCellValue();
                if (!DateUtil.isValidExcelDate(value)) {
                    return false;
                }
                
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            String msg = MessageFormat
                    .format("[{0}]行[{1}]值不是正确的时间格式，错误的值为[{2}]", rowNum, cellNum, cell.getStringCellValue());
            logs.addLog(new ExcelLog(msg));
            return false;
        }
        return true;
    }
    
    private boolean nullCheck(Cell cell, ExcelCell annoCell, int cellNum, int rowNum, ExcelLogs logs) {
        if (annoCell != null && annoCell.valid().allowNull() == false) {
            
            if (cell == null
                    || (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isBlank(cell.getStringCellValue()))
                    || (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && cell.getNumericCellValue() == 0.0D)) {
                String msg = MessageFormat.format("[{0}]行[{1}]列单元 不能为空", rowNum, cellNum);
                logs.addLog(new ExcelLog(msg));
                return false;
            }
            
        }
        return true;
        
    }
    
    /**
     * 正则验证
     * 
     * @param cell
     * @param columnName
     * @param result
     * @param annoCell
     * @return
     */
    private boolean validateRegex(Cell cell, ExcelCell annoCell, int cellNum, int rowNum, ExcelLogs logs) {
        String regex = annoCell.valid().regex();
        if (cell != null && !StringUtils.isEmpty(regex) && cell.getCellType() == Cell.CELL_TYPE_STRING) {
            String cellValue = cell.getStringCellValue();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cellValue);
            if (!matcher.matches()) {
                String msg = MessageFormat.format("[{0}]行[{1}]列单元值必须要符合格式[{2}]", rowNum, cellNum, regex);
                logs.addLog(new ExcelLog(msg));
                return false;
            }
        }
        return true;
    }
    
    /**
     * 数字验证
     * 
     * @param cell
     * @param columnName
     * @param result
     * @param annoCell
     * @return
     */
    private boolean compareNumber(Cell cell, ExcelCell annoCell, int cellNum, int rowNum, ExcelLogs logs) {
        String msg = "";
        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            double cellValue = cell.getNumericCellValue();
            Double compartorValue = annoCell.valid().lt();
            // 小于
            if (!Double.isNaN(compartorValue)) {
                if (!(cellValue < compartorValue)) {
                    msg = MessageFormat.format("[{0}]行[{1}]值列单元值必须要小于[{2}]", rowNum, cellNum, compartorValue);
                    logs.addLog(new ExcelLog(msg));
                    return false;
                }
            }
            
            // 大于
            compartorValue = annoCell.valid().gt();
            if (!Double.isNaN(compartorValue)) {
                if (!(cellValue > compartorValue)) {
                    msg = MessageFormat.format("[{0}]行[{1}]值列单元值必须要大于[{2}]", rowNum, cellNum, compartorValue);
                    logs.addLog(new ExcelLog(msg));
                    return false;
                }
            }
            
            // 小于等于
            compartorValue = annoCell.valid().le();
            if (!Double.isNaN(compartorValue)) {
                if (!(cellValue <= compartorValue)) {
                    msg = MessageFormat.format("[{0}]行[{1}]值列单元值必须要小于或者等于[{2}]", rowNum, cellNum, compartorValue);
                    logs.addLog(new ExcelLog(msg));
                    return false;
                }
            }
            // 大于等于
            compartorValue = annoCell.valid().ge();
            if (!Double.isNaN(compartorValue)) {
                if (!(cellValue >= compartorValue)) {
                    msg = MessageFormat.format("[{0}]行[{1}]值列单元值必须要大于或者等于[{2}]", rowNum, cellNum, compartorValue);
                    logs.addLog(new ExcelLog(msg));
                    return false;
                }
            }
            
        }
        return true;
    }
    
    /**
     * 获取单元格值
     *
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null
                || (cell.getCellType() == Cell.CELL_TYPE_STRING && StringUtils.isBlank(cell.getStringCellValue()))) {
            return null;
        }
        int cellType = cell.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_BLANK:
                return null;
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return null;
        }
    }
    
}
