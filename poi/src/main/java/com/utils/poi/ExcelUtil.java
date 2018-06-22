package com.utils.poi;

import com.utils.poi.annotation.DefaultCellStyle;
import com.utils.poi.annotation.ExcelCell;
import com.utils.poi.annotation.ExcelSheet;
import com.utils.poi.annotation.IExcelCellStyle;
import com.utils.poi.exception.UtilSystemException;
import com.utils.poi.log.ExcelLog;
import com.utils.poi.log.ExcelLogs;
import com.utils.poi.validtor.DefaultValidator;
import com.utils.poi.validtor.SelfValidator;
import com.utils.poi.validtor.Validator;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

/**
 * <p>
 * <li>Description: The <code>ExcelUtil</code> 与 {@link ExcelCell}搭配使用</li>
 * <li>@author: LiuDun </li>
 * <li>@date 2018-06-22 09:57:46</li>
 */
public class ExcelUtil {
    
    /**
     * <li>LG :日志信息 </li>
     */
    private static Logger LG = LoggerFactory.getLogger(ExcelUtil.class);
    
    /**
     * <li>validator :校验类校验类 </li>
     */
    private static Validator validator = null;
    
    /**
     * <li>datePattern :日期格式话内容 </li>
     */
    private static String datePattern = "yyyy/MM";
    
    /**
     * 当数据量大于5000条的时候 将数据刷入到系统中
     */
    private static int flushRow = 5000;
    
    /**
     * 单个sheet最多行数量
     */
    private static int maxRowNumber = 50000;
    
    /**
     * <li> 无参的构造方法 默认校验方式. </li>
     */
    public ExcelUtil() {
        validator = new DefaultValidator();
    }
    
    /**
     * <li> 有参的构造方法 自定义校验方法. </li>
     *
     * @param myValidator the my validator
     */
    public ExcelUtil(Validator myValidator) {
        validator = myValidator;
    }
    
    /**
     * 获取单元格值
     *
     * @param cell the cell   
     * @return cell value
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
    
    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于单个sheet
     *
     * @param <T>    泛型类    
     * @param tip tip信息    
     * @param headers 表格属性列名数组   
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的            javabean属性的数据类型有基本数据类型及String,Date,String[],Double[]   
     * @param out 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中  
     * @throws UtilSystemException the util system exception
     */
    public static <T> void exportExcel(List<String> tip, String[] headers, Collection<T> dataset, OutputStream out)
            throws UtilSystemException {
        exportExcel(tip, headers, dataset, out, datePattern);
    }
    
    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于单个sheet
     *
     * @param <T>    泛型类  
     * @param tip tip信息  
     * @param headers 表格属性列名数组   
     * @param dataset 需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的            javabean属性的数据类型有基本数据类型及String,Date,String[],Double[]   
     * @param out 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中   
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyy-MM"  
     * @throws UtilSystemException the util system exception
     */
    public static <T> void exportExcel(List<String> tip, String[] headers, Collection<T> dataset, OutputStream out,
            String pattern) throws UtilSystemException {
        // 声明一个工作薄
        Workbook workbook = new SXSSFWorkbook(flushRow);
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        
        write2Sheet(sheet, headers, dataset, pattern);
        try {
            workbook.write(out);
        } catch (IOException e) {
            LG.error("导出excel异常", e);
            throw new UtilSystemException(e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
            }
        }
    }
    
    public static <T> void exportExcel(Workbook workbook, ExcelSheet<T> excelSheet, OutputStream out, String pattern)
            throws UtilSystemException {
        
        String sheetName = excelSheet.getSheetName();
        
        int sheetTotal = workbook.getNumberOfSheets();
        // 生成一个表格
        Sheet sheet = null;
        if (sheetTotal == 0) {
            if (StringUtils.isNotEmpty(sheetName)) {
                sheet = workbook.createSheet(sheetName);
            } else {
                sheet = workbook.createSheet();
            }
        } else {
            sheet = workbook.getSheetAt(sheetTotal - 1);
        }
        
        write2Sheet(sheet, excelSheet.getHeaders(), excelSheet.getDataset(), pattern);
        try {
            workbook.write(out);
        } catch (IOException e) {
            LG.error("导出excel异常", e);
            throw new UtilSystemException(e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * <li>Description: 根据二维数组导出数据 </li>
     *
     * @param datalist 二维数组  
     * @param out 输出流
     */
    public static void exportExcel(String[][] datalist, OutputStream out) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            // 声明一个工作薄
            // 生成一个表格
            HSSFSheet sheet = workbook.createSheet();
            
            for (int i = 0; i < datalist.length; i++) {
                String[] r = datalist[i];
                HSSFRow row = sheet.createRow(i);
                for (int j = 0; j < r.length; j++) {
                    HSSFCell cell = row.createCell(j);
                    // cell max length 32767
                    if (r[j].length() > 32767) {
                        r[j] = "--此字段过长(超过32767),已被截断--" + r[j];
                        r[j] = r[j].substring(0, 32766);
                    }
                    cell.setCellValue(r[j]);
                }
            }
            // 自动列宽
            if (datalist.length > 0) {
                int colcount = datalist[0].length;
                for (int i = 0; i < colcount; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            workbook.write(out);
        } catch (IOException e) {
            LG.error(e.toString(), e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于多个sheet
     *
     * @param <T>    泛型类    
     * @param sheets {@link ExcelSheet}的集合  
     * @param out 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中 
     * @throws UtilSystemException the util system exception
     */
    public static <T> void exportExcel(List<ExcelSheet<T>> sheets, OutputStream out) throws UtilSystemException {
        exportExcel(sheets, out, datePattern);
    }
    
    /**
     * 利用JAVA的反射机制，将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上<br>
     * 用于多个sheet
     *
     * @param <T>    泛型类    
     * @param sheets {@link ExcelSheet}的集合   
     * @param out 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中   
     * @param pattern 如果有时间数据，设定输出格式。默认为"yyy-MM-dd"  
     * @throws UtilSystemException the util system exception
     */
    public static <T> void exportExcel(List<ExcelSheet<T>> sheets, OutputStream out, String pattern)
            throws UtilSystemException {
        if (CollectionUtils.isEmpty(sheets)) {
            return;
        }
        // 声明一个工作薄
        Workbook workbook = new SXSSFWorkbook();
        for (ExcelSheet<T> sheet : sheets) {
            // 生成一个表格
            Sheet wbSheet = workbook.createSheet(sheet.getSheetName());
            write2Sheet(wbSheet, sheet.getHeaders(), sheet.getDataset(), pattern);
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            LG.error("导出excel异常", e);
            throw new UtilSystemException(e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
            }
        }
    }
    
    /**
     * 每个sheet的写入
     *
     * @param <T>    泛型类    
     * @param headers 表头   
     * @param dataset 数据集合   
     * @param pattern 日期格式  
     * @throws UtilSystemException the util system exception
     */
    private static <T> int write2Sheet(Sheet sheet, String[] headers, Collection<T> dataset, String pattern)
            throws UtilSystemException {
        
        //标题上面是否存在一些备注
        Row row = null;
        Cell cell = null;
        XSSFRichTextString text = new XSSFRichTextString();
        
        int rowNumber = sheet.getLastRowNum();
        if (rowNumber == 0) {//说明是新的sheet判断是否需要添加标题行
            // 产生表格标题行
            if (headers != null && headers.length > 0) {
                row = sheet.createRow(rowNumber);
                for (int i = 0; i < headers.length; i++) {
                    cell = row.createCell(i);
                    text.setString(headers[i]);
                    cell.setCellValue(text);
                }
            }
        }
        
        // 遍历集合数据，产生数据行
        Iterator<T> it = dataset.iterator();
        while (it.hasNext()) {
            rowNumber++;
            row = sheet.createRow(rowNumber);
            T t = (T) it.next();
            try {
                if (t instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) t;
                    int cellNum = 0;
                    for (String k : headers) {
                        if (!map.containsKey(k)) {
                            LG.error("Map 中 不存在 key [" + k + "]");
                            continue;
                        }
                        Object value = map.get(k);
                        cell = row.createCell(cellNum);
                        cell.setCellValue(String.valueOf(value));
                        cellNum++;
                    }
                } else {
                    List<FieldForSortting> fields = sortFieldByAnno(t.getClass());
                    
                    Workbook wb = sheet.getWorkbook();
                    
                    CellStyle cellStyle = null;
                    DataFormat format = null;
                    
                    int cellNum = 0;
                    for (int i = 0; i < fields.size(); i++) {
                        cell = row.createCell(cellNum);
                        FieldForSortting fieldForSortting = fields.get(i);
                        Field field = fieldForSortting.getField();
                        field.setAccessible(true);
                        Object value = field.get(t);
                        String textValue = null;
                        cellStyle = fieldForSortting.getCellStyle().getCellStyle(wb);
                        format = fieldForSortting.getCellStyle().getDataFormat(wb);
                        if (value instanceof Integer) {
                            cellStyle.setDataFormat((short) 1);
                            int intValue = (Integer) value;
                            cell.setCellValue(intValue);
                            cell.setCellStyle(cellStyle);
                        } else if (value instanceof Float) {
                            float fValue = (Float) value;
                            cell.setCellValue(fValue);
                        } else if (value instanceof Double) {
                            cellStyle.setDataFormat((short) 2);
                            double dValue = (Double) value;
                            cell.setCellValue(dValue);
                            cell.setCellStyle(cellStyle);
                        } else if (value instanceof Long) {
                            long longValue = (Long) value;
                            cell.setCellValue(longValue);
                        } else if (value instanceof Boolean) {
                            boolean bValue = (Boolean) value;
                            cell.setCellValue(bValue);
                        } else if (value instanceof Date) {
                            cellStyle.setDataFormat(format.getFormat(pattern));
                            cell.setCellValue((Date) value);
                            cell.setCellStyle(cellStyle);
                        } else if (value instanceof String[]) {
                            String[] strArr = (String[]) value;
                            for (int j = 0; j < strArr.length; j++) {
                                String str = strArr[j];
                                cell.setCellValue(str);
                                if (j != strArr.length - 1) {
                                    cellNum++;
                                    cell = row.createCell(cellNum);
                                }
                            }
                        } else if (value instanceof Double[]) {
                            Double[] douArr = (Double[]) value;
                            for (int j = 0; j < douArr.length; j++) {
                                Double val = douArr[j];
                                // 资料不为空则set Value
                                if (val != null) {
                                    cell.setCellValue(val);
                                }
                                
                                if (j != douArr.length - 1) {
                                    cellNum++;
                                    cell = row.createCell(cellNum);
                                }
                            }
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            String empty = StringUtils.EMPTY;
                            ExcelCell anno = field.getAnnotation(ExcelCell.class);
                            if (anno != null) {
                                empty = anno.defaultValue();
                            }
                            textValue = value == null ? empty : value.toString();
                        }
                        if (textValue != null) {
                            //HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            
                            text.setString(textValue);
                            
                            cell.setCellValue(text);
                        }
                        
                        cellNum++;
                    }
                }
            } catch (Exception e) {
                LG.error("导出excel异常", e);
                throw new UtilSystemException(e.getMessage());
            }
        }
        // 设定自动宽度
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        return rowNumber;
    }
    
    /**
     * 把Excel的数据封装成voList
     *
     * @param <T>     泛型类  
     * @param clazz 解析的clazz类  
     * @param inputFile 输入的文件集合  
     * @param logs 错误log集合   
     * @param arrayCount 如果vo中有数组类型,那就按照index顺序,把数组应该有几个值写上.   
     * @return voList
     * @throws UtilSystemException the util system exception
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> importExcel(Class<T> clazz, File inputFile, ExcelLogs logs, Integer... arrayCount)
            throws UtilSystemException {
        if (!ExcelMapper.class.isAssignableFrom(clazz)) {
            throw new UtilSystemException("解析的类必须要实现ExcelMapper接口");
        }
        Workbook workBook = null;
        LG.info("开始解析excel文件,文件名为:" + inputFile.getName());
        try {
            workBook = WorkbookFactory.create(inputFile);
        } catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
            LG.error("解析文件" + inputFile.getName() + "文件格式错误", e);
            ExcelLog log = new ExcelLog(inputFile.getName() + "不支持的excel文件格式");
            logs.addLog(log);
            // throw new
            // SystemException(BaseErrorCode.FILE_READ_ERROR,"不支持当前的excel文件格式");
        }
        List<T> list = new ArrayList<T>();
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        try {
            Map<String, Integer> titleMap = new HashMap<>();
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // 解析表头
                if (row.getRowNum() == 0) {
                    if (clazz == Map.class) {
                        // 解析map用的key,就是excel标题行
                        Iterator<Cell> cellIterator = row.cellIterator();
                        Integer index = 0;
                        while (cellIterator.hasNext()) {
                            String value = cellIterator.next().getStringCellValue();
                            titleMap.put(value, index);
                            index++;
                        }
                    }
                    continue;
                }
                // 整行都空，就跳过
                boolean allRowIsNull = true;
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Object cellValue = getCellValue(cellIterator.next());
                    if (cellValue != null) {
                        allRowIsNull = false;
                        break;
                    }
                }
                if (allRowIsNull) {
                    // LG.warn("Excel row " + row.getRowNum()
                    // + " all row value is null!");
                    continue;
                }
                T t = null;
                StringBuilder log = new StringBuilder();
                if (clazz == Map.class) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    for (String k : titleMap.keySet()) {
                        Integer index = titleMap.get(k);
                        String value = row.getCell(index).getStringCellValue();
                        map.put(k, value);
                    }
                    list.add((T) map);
                    
                } else {
                    t = clazz.newInstance();
                    int arrayIndex = 0;// 标识当前第几个数组了
                    int cellIndex = 0;// 标识当前读到这一行的第几个cell了
                    List<FieldForSortting> fields = sortFieldByAnno(clazz);
                    for (FieldForSortting ffs : fields) {
                        Field field = ffs.getField();
                        field.setAccessible(true);
                        // 处理field为数组的情况
                        if (field.getType().isArray()) {
                            parseArrayCell(logs, row, t, field, arrayIndex, cellIndex, arrayCount);
                        } else {
                            Cell cell = row.getCell(cellIndex);
                            Object value = getCellValue(cell);
                            if (validateCell(cell, field, cellIndex + 1, row.getRowNum() + 1, logs)) {
                                try {
                                    setValue(field, value, t, cell);
                                } catch (ClassCastException ex) {
                                    // 单独处理用户填写的数据格式不对的情况
                                    // throw new SystemException(
                                    // BaseErrorCode.SYSTEM,
                                    // inputFile.getName()
                                    // + (cellIndex + 1) + "列"
                                    // + (row.getRowNum() + 1)
                                    // + "行的数据格式不正确，错误的值为" + value);
                                    ExcelLog errorLog = new ExcelLog(inputFile.getName() + (cellIndex + 1) + "列"
                                            + (row.getRowNum() + 1) + "行的数据格式不正确，错误的值为" + value);
                                    logs.addLog(errorLog);
                                }
                            }
                            
                            cellIndex++;
                        }
                    }
                    String checkMsg = null;
                    if (t instanceof SelfValidator) {
                        checkMsg = ((SelfValidator) t).selfCheck();
                    }
                    if (StringUtils.isBlank(checkMsg)) {
                        list.add(t);
                    } else {
                        ExcelLog excelLog = new ExcelLog(checkMsg);
                        logs.addLog(excelLog);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(MessageFormat.format("不能实例化类:{0}", clazz.getSimpleName()), e);
        } finally {
            try {
                workBook.close();
            } catch (IOException e) {
            }
        }
        LG.info("解析文件" + inputFile.getName() + "完成");
        return list;
    }
    
    /**
     * value 的setter方法
     *
     * @param <T>    the type parameter   
     * @param field the field   
     * @param value the value   
     * @param t the t   
     * @param cell the cell   
     * @throws IllegalArgumentException the illegal argument exception   
     * @throws IllegalAccessException the illegal access exception
     */
    private static <T> void setValue(Field field, Object value, T t, Cell cell)
            throws IllegalArgumentException, IllegalAccessException {
        if (value == null) {
            return;
        }
        Class<?> clazz = field.getType();
        if (clazz == Integer.class || clazz == int.class) {
            double doubleValue = (Double) value;
            field.set(t, (int) doubleValue);
        } else if (clazz == Double.class || clazz == double.class) {
            field.set(t, (Double) value);
        } else if (clazz == Long.class || clazz == long.class) {
            double doubleValue = (Double) value;
            field.set(t, (long) doubleValue);
        } else if (clazz == Date.class) {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isValidExcelDate((Double) value)) {
                Date dateValue = DateUtil.getJavaDate((Double) value);
                field.set(t, dateValue);
            } else {
                throw new ClassCastException("时间格式不正确");
            }
        } else {
            if (value instanceof Double) {
                DecimalFormat df = new DecimalFormat("#");
                field.set(t, df.format(value));
            } else {
                field.set(t, String.valueOf(value));
            }
            
        }
        
    }
    
    /**
     * 解析数组单元
     *
     * @param <T>    泛型类  
     * @param logs 日志信息类  
     * @param row 一行数据信息  
     * @param t 实体类  
     * @param field 类字段信息  
     * @param arrayIndex 数组信息  
     * @param cellIndex cell开始的编号  
     * @param arrayCount 数组数量  
     * @throws IllegalAccessException the illegal access exception
     */
    private static <T> void parseArrayCell(ExcelLogs logs, Row row, T t, Field field, int arrayIndex, int cellIndex,
            Integer... arrayCount) throws IllegalAccessException {
        Integer count = arrayCount[arrayIndex];
        Object[] value = null;
        if (field.getType().equals(String[].class)) {
            value = new String[count];
        } else {
            // 目前只支持String[]和Double[]
            value = new Double[count];
        }
        for (int i = 0; i < count; i++) {
            Cell cell = row.getCell(cellIndex);
            if (validateCell(cell, field, cellIndex + 1, row.getRowNum() + 1, logs)) {
                field.set(t, value);
            }
            
            cellIndex++;
        }
        
        arrayIndex++;
    }
    
    /**
     * <li>Description: cell值校验方法 </li>
     *
     * @param cell cell值  
     * @param field 组装类字段信息  
     * @param cellNum 列号  
     * @param rowNum 行号  
     * @param logs 日志信息  
     * @return 是否验证通过
     */
    private static boolean validateCell(Cell cell, Field field, int cellNum, int rowNum, ExcelLogs logs) {
        
        return validator.valid(cell, field, cellNum, rowNum, logs);
    }
    
    /**
     * 根据annotation的seq排序后的栏位
     *
     * @param clazz 类字段列参数信息  
     * @return
     */
    private static List<FieldForSortting> sortFieldByAnno(Class<?> clazz) {
        Field[] fieldsArr = clazz.getDeclaredFields();
        List<FieldForSortting> fields = new ArrayList<FieldForSortting>();
        List<FieldForSortting> annoNullFields = new ArrayList<FieldForSortting>();
        for (Field field : fieldsArr) {
            ExcelCell ec = field.getAnnotation(ExcelCell.class);
            if (ec == null) {
                // 没有ExcelCell Annotation 视为不汇入
                continue;
            }
            int id = ec.index();
            
            IExcelCellStyle excelCellStyle = null;
            try {
                Class cellStyle = ec.defaultCellStyle();
                excelCellStyle = (IExcelCellStyle) cellStyle.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LG.debug("单元格实例化错误,使用默认的单元格样式");
                excelCellStyle = new DefaultCellStyle();
            }
            
            fields.add(new FieldForSortting(field, id, excelCellStyle));
        }
        fields.addAll(annoNullFields);
        sortByProperties(fields, true, false, "index");
        return fields;
    }
    
    /**
     * <li>Description: 根据属性值进行排序 </li>
     *
     * @param list 待排序集合信息  
     * @param isNullHigh 确定空值 是否大于任何值  true 空值大于任何值  false 空值小于任何值  
     * @param isReversed 是否逆序  
     * @param props 比较的字段名称
     */
    @SuppressWarnings("unchecked")
    private static void sortByProperties(List<? extends Object> list, boolean isNullHigh, boolean isReversed,
            String... props) {
        if (CollectionUtils.isNotEmpty(list)) {
            Comparator<?> typeComp = ComparableComparator.getInstance();
            if (isNullHigh) {
                typeComp = ComparatorUtils.nullHighComparator(typeComp);
            } else {
                typeComp = ComparatorUtils.nullLowComparator(typeComp);
            }
            if (isReversed) {
                typeComp = ComparatorUtils.reversedComparator(typeComp);
            }
            
            List<Object> sortCols = new ArrayList<Object>();
            
            if (props != null) {
                for (String prop : props) {
                    sortCols.add(new BeanComparator(prop, typeComp));
                }
            }
            if (sortCols.size() > 0) {
                Comparator<Object> sortChain = new ComparatorChain(sortCols);
                Collections.sort(list, sortChain);
            }
        }
    }
    
}
