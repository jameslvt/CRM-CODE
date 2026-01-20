package com.crm.common.utils;

import com.crm.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入导出工具类
 * 支持 WPS 和 Microsoft Office
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Slf4j
public class ExcelUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_ROWS = 10000; // 最大导入行数

    /**
     * 导出 Excel
     *
     * @param response  HTTP 响应
     * @param dataList  数据列表
     * @param headers   表头数组
     * @param fieldNames 字段名数组
     * @param fileName  文件名
     */
    public static <T> void exportExcel(HttpServletResponse response,
                                       List<T> dataList,
                                       String[] headers,
                                       String[] fieldNames,
                                       String fileName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("数据");

            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000); // 设置列宽
            }

            // 创建数据行
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                T data = dataList.get(i);

                for (int j = 0; j < fieldNames.length; j++) {
                    Cell cell = row.createCell(j);
                    Object value = getFieldValue(data, fieldNames[j]);
                    setCellValue(cell, value);
                }
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName + ".xlsx");

            // 写入响应流
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }

        } catch (Exception e) {
            log.error("导出 Excel 失败", e);
            throw new BusinessException("导出 Excel 失败: " + e.getMessage());
        }
    }

    /**
     * 导入 Excel
     *
     * @param file  上传的文件
     * @param clazz 目标类
     * @param fieldNames 字段名数组（与 Excel 列对应）
     * @return 数据列表
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> clazz, String[] fieldNames) {
        List<T> dataList = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            // 检查行数限制
            if (rowCount > MAX_ROWS + 1) { // +1 是因为包含表头
                throw new BusinessException("导入数据不能超过 " + MAX_ROWS + " 行");
            }

            // 从第二行开始读取数据（第一行是表头）
            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                T instance = clazz.newInstance();

                for (int j = 0; j < fieldNames.length; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        Object value = getCellValue(cell);
                        setFieldValue(instance, fieldNames[j], value);
                    }
                }

                dataList.add(instance);
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("导入 Excel 失败", e);
            throw new BusinessException("导入 Excel 失败: " + e.getMessage());
        }

        return dataList;
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        return style;
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DATE_TIME_FORMATTER));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 通过反射获取字段值
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            log.warn("获取字段值失败: {}", fieldName, e);
            return null;
        }
    }

    /**
     * 通过反射设置字段值
     */
    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // 类型转换
            if (value != null) {
                Class<?> fieldType = field.getType();
                if (fieldType == String.class) {
                    field.set(obj, value.toString());
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    field.set(obj, ((Number) value).intValue());
                } else if (fieldType == Long.class || fieldType == long.class) {
                    field.set(obj, ((Number) value).longValue());
                } else if (fieldType == Double.class || fieldType == double.class) {
                    field.set(obj, ((Number) value).doubleValue());
                } else if (fieldType == LocalDateTime.class) {
                    if (value instanceof LocalDateTime) {
                        field.set(obj, value);
                    } else if (value instanceof String) {
                        field.set(obj, LocalDateTime.parse((String) value, DATE_TIME_FORMATTER));
                    }
                } else {
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            log.warn("设置字段值失败: {}", fieldName, e);
        }
    }
}
