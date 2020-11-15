package com.anhk.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Description: ExcelUtils
 * @Author: Anhk丶
 * @Date: 2020/10/29  21:46
 * @Version: 1.0
 */
public class ExcelUtils {
    /**
     * 根据map结构导出数据
     *
     * @param sheetName 页名
     * @param titleList 表头列表集合
     * @param dataList  数据集合
     */
    public static void exportMapToExcel(String sheetName,
                                        List<Map<String, String>> titleList,
                                        List<Map<String, Object>> dataList) {
        // 创建headerNameList和headerFiledList分别用来存储titleList集合中map集合的key和value
        //对应对象属性的集合
        ArrayList<String> headerFiledList = new ArrayList<>();
        //与对象属性相对应的中文描述
        ArrayList<String> headerNameList = new ArrayList<>();
        Map<Integer, String> tltleMap = new LinkedHashMap<>();
        // 遍历titleList集合，获取其中map集合的name集合和filed集合
        for (int i = 0; i < titleList.size(); i++) {
            // 获取表头集合中的map集合
            Map<String, String> map = titleList.get(i);
            // 通过map.keySet()方法，得到key的值，然后获取value;
            for (String key : map.keySet()) {
                String value = map.get(key);
                headerFiledList.add(key);
                tltleMap.put(i, value);
                headerNameList.add(value);
            }
        }

        Map<String, Map<String, Object>> dataMap = new LinkedHashMap<>();
        // 遍历dataList集合，获取其中map集合
        for (int i = 0; i < dataList.size(); i++) {
            // 获取数据集合中的map集合
            dataMap.put(String.valueOf(i), dataList.get(i));
        }

        //创建工作簿对象
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        //创建页，并定义页名
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        //创建第一行，大标题
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("学生基本信息表");
        //设置样式
        cell.setCellStyle(bigTitle(workbook));
        //合并单元格  起始行  结束行  起始列  结束列
        //CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol)
        workbook.getSheet(sheetName).addMergedRegion(new CellRangeAddress(0, 0, 0, headerNameList.size() - 1));
        //创建第二行，作为表格头，遍历表格头数据，创建单元格并赋值
        row = sheet.createRow(1);
        for (int i = 0; i < headerNameList.size(); i++) {
            // 设置列宽
            sheet.setColumnWidth(i, 1000 * 5);
            // 设置行高
            row.setHeightInPoints(25);
            //创建列
            cell = row.createCell(i);
            //赋值
            cell.setCellValue(headerNameList.get(i));
            //设置样式
            cell.setCellStyle(title(workbook));
        }
        // 表格列标题一行对应的字段的集合
        int dtoRow = 2;// 内容栏 导出数据dtoList的行序号
        for (int j = 0; j < dataList.size(); j++) {
            //创建行
            row = sheet.createRow(dtoRow);
            //行加一，用于下次循环
            dtoRow++;
            //初始化列
            int zdCell = 0;
            //遍历对应对象的属性集合
            for (String s : headerFiledList) {
                String value = "";
                //判断数据集合中，是否含有和对象属性相同的值
                if (!dataList.get(0).containsKey(s)) {
                    continue;
                }
                if (dataList.get(dtoRow - 2).get(s) != null && dataList.get(dtoRow - 2).get(s) != "") {
                    value = dataList.get(dtoRow - 2).get(s).toString();
                }
                try {
                    // 写进excel对象
                    cell = row.createCell(zdCell);
                    cell.setCellValue(value);
                    cell.setCellStyle(text(workbook));
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                zdCell++;
            }
        }
    }

    /**
     * 根据JavaBean对象导出数据
     *
     * @param sheetName   页名
     * @param headerName  表头名称
     * @param headerFiled 与表头名称相对应的对象属性名
     * @param dataList    数据
     * @throws IOException
     */
    public static void exportBeanToExcel(String sheetName,
                                         List<String> headerName,
                                         List<String> headerFiled,
                                         List<Object> dataList, HttpServletResponse response) throws IOException {
        // 生成一个格式化工具
        DecimalFormat df = new DecimalFormat("######0.000");
        //创建工作簿对象
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        //创建页，并定义页名
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        //创建第一行，大标题
        Row row = sheet.createRow(0);
        //合并单元格  起始行  结束行  起始列  结束列
        //CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol)
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerName.size() - 1));
        Cell cell = row.createCell(0);
        cell.setCellValue(sheetName);
        //设置样式
        cell.setCellStyle(bigTitle(workbook));
        //设置所有默认行高
        sheet.setDefaultRowHeightInPoints(25);
        //创建第二行，作为表格头，遍历表格头数据，创建单元格并赋值
        row = sheet.createRow(1);
        for (int i = 0; i < headerName.size(); i++) {
            // 设置列宽
            if (i == headerName.size() - 1) {
                sheet.setColumnWidth(i, 1000 * 6);
            } else {
                sheet.setColumnWidth(i, 1000 * 5);
            }
            // 设置行高
            row.setHeightInPoints(25);
            //创建列
            cell = row.createCell(i);
            //赋值
            cell.setCellValue(headerName.get(i));
            //设置样式
            cell.setCellStyle(title(workbook));
        }

        // 内容栏 导出数据dtoList的行序号
        int dtoRow = 2;
        //总记录的迭代器
        Iterator<Object> labIt = dataList.iterator();
        //遍历总记录
        while (labIt.hasNext()) {
            row = sheet.createRow(dtoRow);
            dtoRow++;
            Object o = labIt.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            //获得JavaBean全部属性
            Class clazz = o.getClass();
            //创建字段集合
            ArrayList<Field> fieldList = new ArrayList<>();
            while (clazz != null) {
                //获取本类的属性字段数组
                Field[] fields = clazz.getDeclaredFields();
                //将字段数组转换为集合
                List<Field> list = Arrays.asList(fields);
                //保存到创建好的字段集合中
                fieldList.addAll(list);
                //获取父类的class对象，再遍历获取父类的属性字段，并保存到字段集合中
                clazz = clazz.getSuperclass();
            }
            //遍历属性，比对
            for (short i = 0; i < fieldList.size(); i++) {
                Field field = fieldList.get(i);
                //属性名
                String fieldName = field.getName();
                //一行列标题字段的集合的迭代器
                Iterator<String> zdIt = headerFiled.iterator();
                int zdCell = 0;
                //遍历要导出的字段集合
                while (zdIt.hasNext()) {
                    //比对JavaBean的属性名，一致就写入，不一致就丢弃
                    if (zdIt.next().equals(fieldName)) {
                        //拿到属性的get方法
                        String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        //拿到JavaBean对象
                        Class tCls = o.getClass();
                        try {
                            //通过JavaBean对象拿到该属性的get方法，从而进行操控
                            Method getMethod = tCls.getMethod(getMethodName, new Class[]{});
                            //操控该对象属性的get方法，从而拿到属性值
                            Object val = getMethod.invoke(o, new Object[]{});
                            //判断是否是日期类型
                            String textVal = null;
                            cell = row.createCell(zdCell);
                            if (val instanceof Date) {
                                //设置单元格格式
                                DataFormat dataFormat = workbook.createDataFormat();
                                CellStyle cellStyle = text(workbook);
                                cellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
                                cell.setCellStyle(cellStyle);
                                //转换日期类型，并写进excel对象
                                cell.setCellValue((Date) val);
                            } else if (val instanceof Integer) {
                                int intVal = (Integer) val;
                                // 写进excel对象
                                cell.setCellValue(intVal);
                                cell.setCellStyle(text(workbook));
                            } else if (val instanceof Float) {
                                Float fVal = (Float) val;
                                BigDecimal b1 = new BigDecimal(df.format(fVal) + "");
                                // style2.setDataFormat(format.getFormat("0.00")); // 两位小数
                                cell.setCellValue(
                                        Float.parseFloat(String.valueOf(b1).substring(0, String.valueOf(b1).indexOf(".") + 3)));
                                cell.setCellStyle(text(workbook));
                            } else if (val instanceof Double) {
                                double dVal = (Double) val;
                                BigDecimal b1 = new BigDecimal(df.format(dVal) + "");
                                // style2.setDataFormat(format.getFormat("0.00"));
                                cell.setCellValue(Double
                                        .parseDouble(String.valueOf(b1).substring(0, String.valueOf(b1).indexOf(".") + 3)));
                                cell.setCellStyle(text(workbook));
                            } else if (val instanceof Long) {// Long
                                long longValue = (Long) val;
                                cell.setCellValue(longValue);
                                cell.setCellStyle(text(workbook));
                            } else {
                                // 其它数据类型都当作字符串简单处理
                                if (val != null) {
                                    textVal = val.toString();
                                } else {
                                    textVal = "";
                                }
                                cell.setCellValue(textVal);
                                cell.setCellStyle(text(workbook));
                            }
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    zdCell++;
                }
            }
        }
        //网络下载
        //创建字节输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //写流
        workbook.write(byteArrayOutputStream);
        //下载
        DownLoadUtil.download(byteArrayOutputStream, sheetName + ".xlsx", response);
    }

    /**
     * 从Excel导入数据
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static List<Object[]> importFromExcel(InputStream inputStream, String fileName) throws IOException {
        //事先创建一个数组，用来存放每行读取到的列值
        List<Object[]> list = new ArrayList<>();
        Workbook workbook = null;
        //获取文件的后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //判断文件类型，并获取工作簿对象
        if (suffix.equals(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (suffix.equals(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        }
        //读取表  第一页
        Sheet sheet = workbook.getSheetAt(0);
        //读取最后一行，看有多少条数据
        int lastRowNum = sheet.getLastRowNum();
        //创建行
        Row row = null;
        //遍历行数（从第二行开始），创建对象
        for (int i = 1; i <= lastRowNum; i++) {
            //获取行对象
            row = sheet.getRow(i);
            //读取最后一列，看有几个对象属性
            int lastCellNum = row.getLastCellNum();
            Object[] params = new Object[lastCellNum];
            //遍历列数，获取每个表格的值
            for (int j = 0; j < lastCellNum; j++) {
                Cell cell = row.getCell(j);
                //根据表格的类型获取具体的值
                Object cellValue = getCellValue(cell);
                params[j] = cellValue;
            }
            list.add(params);
        }
        return list;
    }

    /**
     * 处理Excel中表格的值类型
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        Object o = null;
        //获取表格类型
        CellType cellType = cell.getCellTypeEnum();
        //判断
        switch (cellType) {
            case STRING:
                o = cell.getStringCellValue();
                break;
            case BOOLEAN:
                o = cell.getBooleanCellValue();
                break;
            case NUMERIC:   //在Excel中数字类型和日期类型的类型一致，但是在Java中不一致，因此需要判断
                //判断当前单元格是不是日期类型
                boolean flag = DateUtil.isCellDateFormatted(cell);
                if (flag) {
                    o = cell.getDateCellValue();
                } else {
                    //处理数值类型的转换，默认是double类型
                    long longVal = Math.round(cell.getNumericCellValue());
                    if (Double.parseDouble(longVal + ".0") == cell.getNumericCellValue()) {
                        o = longVal;
                    } else {
                        o = cell.getNumericCellValue();
                    }
                }
                break;
            default:
                break;
        }
        return o;
    }

    /**
     * 大标题的样式
     *
     * @param wb
     * @return
     */
    public static CellStyle bigTitle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        //设置字体样式
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16); //字体大小
        font.setBold(true);//字体加粗
        style.setFont(font);
        //设置边框样式
        style.setAlignment(HorizontalAlignment.CENTER); // 横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 纵向居中
        return style;
    }

    /**
     * 小标题样式
     *
     * @param wb
     * @return
     */
    public static CellStyle title(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        //设置字体样式
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 14); //字体大小
        style.setFont(font);
        //设置边框样式
        style.setAlignment(HorizontalAlignment.CENTER); // 横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 纵向居中
        style.setBorderTop(BorderStyle.THIN); // 上细线
        style.setBorderBottom(BorderStyle.THIN); // 下细线
        style.setBorderLeft(BorderStyle.THIN); // 左细线
        style.setBorderRight(BorderStyle.THIN); // 右细线
        return style;
    }

    /**
     * 文字样式
     *
     * @param wb
     * @return
     */
    public static CellStyle text(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        //设置字体样式
        Font font = wb.createFont();
        font.setFontName("等线");
        font.setFontHeightInPoints((short) 12); //字体大小
        style.setFont(font);
        //设置边框样式
        style.setAlignment(HorizontalAlignment.LEFT); // 横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 纵向居中
        style.setBorderTop(BorderStyle.THIN); // 上细线
        style.setBorderBottom(BorderStyle.THIN); // 下细线
        style.setBorderLeft(BorderStyle.THIN); // 左细线
        style.setBorderRight(BorderStyle.THIN); // 右细线
        return style;
    }


}
