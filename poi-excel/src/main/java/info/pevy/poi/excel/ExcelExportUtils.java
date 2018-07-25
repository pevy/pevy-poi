package info.pevy.poi.excel;

import info.pevy.poi.excel.annotation.ExcelField;
import info.pevy.poi.excel.annotation.ExcelSheet;
import info.pevy.poi.excel.page.Pagination;
import info.pevy.poi.excel.reflection.FieldReflectionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Excel导出工具
 * </p>
 *
 * @author pevy
 * @since 2018/3/22
 */
@Slf4j
public class ExcelExportUtils {

    private static final int MAX_SHEET_NUM = 1000;

    /**
     * 导出Excel字节数据
     *
     * @param sheetDataListArr 数据，可变参数，如多个参数则代表导出多张Sheet
     * @return 表格字节数组
     */
    public static byte[] exportToBytes(Class sheetClazz, List<?>... sheetDataListArr) {
        // workbook
        Workbook workbook = exportWorkbook(sheetClazz, sheetDataListArr);

        ByteArrayOutputStream byteArrayOutputStream = null;
        byte[] result;
        try {
            // workbook 2 ByteArrayOutputStream
            byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);

            // flush
            byteArrayOutputStream.flush();

            result = byteArrayOutputStream.toByteArray();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 导出Excel对象
     *
     * @param sheetDataListArr Excel数据
     */
    private static Workbook exportWorkbook(Class sheetClazz, List<?>... sheetDataListArr) {
        // book （HSSFWorkbook=2003/xls、XSSFWorkbook=2007/xlsx）
        Workbook workbook = new HSSFWorkbook();

        // data array valid
        if (sheetDataListArr == null || sheetDataListArr.length == 0) {
            log.warn(">>>>>>>>>>> xxl-excel error, data array can not be empty.");
            makeSheetTitle(workbook, sheetClazz);
            return workbook;
        }

        // sheet
        Pagination<?> listPageUtil;
        for (List<?> dataList : sheetDataListArr) {
            try {
                listPageUtil = new Pagination<>(dataList, 50000);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                makeSheetTitle(workbook, sheetClazz);
                break;
            }
            int totalPage = listPageUtil.getTotalPage();
            List<?> currentListData;
            for (int currentPage = 1; currentPage <= totalPage; currentPage++) {
                currentListData = listPageUtil.getPagedList(currentPage);
                if (currentListData != null) {
                    makeSheetData(workbook, makeSheetTitle(workbook, sheetClazz), currentListData);
                }
            }
        }

        return workbook;
    }

    /**
     * 创建Sheet表头
     */
    private static Sheet makeSheetTitle(Workbook workbook, Class<?> sheetClazz) {
        ExcelSheet excelSheet = sheetClazz.getAnnotation(ExcelSheet.class);
        String sheetName = sheetClazz.getSimpleName();
        if (excelSheet.name().trim().length() > 0) {
            sheetName = excelSheet.name().trim();
        }
        Sheet existSheet = workbook.getSheet(sheetName);
        if (existSheet != null) {
            for (int i = 2; i <= MAX_SHEET_NUM; i++) {
                String newSheetName = sheetName.concat(String.valueOf(i));  // avoid sheetName repetition
                existSheet = workbook.getSheet(newSheetName);
                if (existSheet == null) {
                    sheetName = newSheetName;
                    break;
                }
            }
        }

        Sheet sheet = workbook.createSheet(sheetName);

        // sheet field
        List<Field> fields = new ArrayList<>();
        if (sheetClazz.getDeclaredFields() != null && sheetClazz.getDeclaredFields().length > 0) {
            for (Field field : sheetClazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.getAnnotation(ExcelField.class) == null) {
                    continue;
                }
                fields.add(field);
            }
        }

        Row headRow = sheet.createRow(0);
        Field field;
        ExcelField excelField;
        String fieldName;
//        int headColorIndex = excelSheet.headColor();
        int headColorIndex = excelSheet.headColor().getIndex();
        for (int i = 0; i < fields.size(); i++) {
            field = fields.get(i);
            excelField = field.getAnnotation(ExcelField.class);

            fieldName = field.getName();
            if (excelField.name().trim().length() > 0) {
                fieldName = excelField.name().trim();
            }

            CellStyle headStyle = workbook.createCellStyle();
            headStyle.setAlignment(excelField.align());
            if (headColorIndex > -1) {
                headStyle.setFillForegroundColor((short) headColorIndex);
                headStyle.setFillBackgroundColor((short) headColorIndex);
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            }

            // head-field data
            Cell cellX = headRow.createCell(i, Cell.CELL_TYPE_STRING);
            cellX.setCellStyle(headStyle);
            cellX.setCellValue(String.valueOf(fieldName));
        }

        return sheet;
    }

    /**
     * 创建Sheet表数据
     */
    private static void makeSheetData(Workbook workbook, Sheet sheet, List<?> sheetDataList) {
        // data
        if (sheetDataList == null || sheetDataList.size() == 0) {
            log.warn(">>>>>>>>>>> xxl-excel error, data can not be empty.");
            return;
        }

        // sheet
        Class<?> sheetClass = sheetDataList.get(0).getClass();
        ExcelSheet excelSheet = sheetClass.getAnnotation(ExcelSheet.class);

        int headColorIndex = -1;
        if (excelSheet != null) {
//            headColorIndex = excelSheet.headColor();
            headColorIndex = excelSheet.headColor().getIndex();
        }

        // sheet field
        List<Field> fields = new ArrayList<>();
        if (sheetClass.getDeclaredFields() != null && sheetClass.getDeclaredFields().length > 0) {
            for (Field field : sheetClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.getAnnotation(ExcelField.class) == null) {
                    continue;
                }
                fields.add(field);
            }
        }

        if (fields.size() == 0) {
            log.warn(">>>>>>>>>>> xxl-excel error, data field can not be empty.");
            return;
        }

        // sheet header row
        CellStyle[] fieldDataStyleArr = new CellStyle[fields.size()];
        int[] fieldWidthArr = new int[fields.size()];
        for (int i = 0; i < fields.size(); i++) {

            // field
            Field field = fields.get(i);
            ExcelField excelField = field.getAnnotation(ExcelField.class);

            int fieldWidth = 0;
            HorizontalAlignment align = null;
//            Short align = null;
            if (excelField != null) {
                fieldWidth = excelField.width();
                align = excelField.align();
            }

            // field width
            fieldWidthArr[i] = fieldWidth;

            // head-style、field-data-style
            CellStyle fieldDataStyle = workbook.createCellStyle();
            if (align != null) {
                fieldDataStyle.setAlignment(align);
            }
            fieldDataStyleArr[i] = fieldDataStyle;

            CellStyle headStyle = workbook.createCellStyle();
            headStyle.cloneStyleFrom(fieldDataStyle);
            if (headColorIndex > -1) {
                headStyle.setFillForegroundColor((short) headColorIndex);
                headStyle.setFillBackgroundColor((short) headColorIndex);
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                headStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            }
        }

        // sheet data rows
        for (int dataIndex = 0; dataIndex < sheetDataList.size(); dataIndex++) {
            int rowIndex = dataIndex + 1;
            Object rowData = sheetDataList.get(dataIndex);

            Row rowX = sheet.createRow(rowIndex);

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(rowData);

                    String fieldValueString = FieldReflectionParser.formatValue(field, fieldValue);

//                    Cell cellX = rowX.createCell(i, CellType.STRING);
                    Cell cellX = rowX.createCell(i, Cell.CELL_TYPE_STRING);
                    cellX.setCellValue(fieldValueString);
                    cellX.setCellStyle(fieldDataStyleArr[i]);
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        }

        // sheet finally
        for (int i = 0; i < fields.size(); i++) {
            int fieldWidth = fieldWidthArr[i];
            if (fieldWidth > 0) {
                sheet.setColumnWidth(i, fieldWidth);
            } else {
                sheet.autoSizeColumn((short) i);
            }
        }
    }
}
