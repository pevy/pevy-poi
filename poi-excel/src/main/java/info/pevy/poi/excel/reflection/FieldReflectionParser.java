package info.pevy.poi.excel.reflection;

import info.pevy.poi.excel.annotation.ExcelField;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * api request field, reflect util
 * </p>
 *
 * @author pevy
 * @since 2018/3/22
 */
public final class FieldReflectionParser {

    /**
     * 参数解析 （支持：Byte、Boolean、String、Short、Integer、Long、Float、Double、Date）
     */
    public static Object parseValue(Field field, String value) {
        Class<?> fieldType = field.getType();

        ExcelField excelField = field.getAnnotation(ExcelField.class);
        if (value == null || value.trim().length() == 0)
            return null;
        value = value.trim();

        if (Byte.class.equals(fieldType) || Byte.TYPE.equals(fieldType)) {
            return parseByte(value);
        } else if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return parseBoolean(value);
        } else if (Character.class.equals(fieldType) || Character.TYPE.equals(fieldType)) {
            return value.toCharArray()[0];
        } else if (String.class.equals(fieldType)) {
            return value;
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return parseShort(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return parseInt(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return parseLong(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return parseFloat(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return parseDouble(value);
        } else if (Date.class.equals(fieldType)) {
            return parseDate(value, excelField);
        } else {
            throw new RuntimeException("request illegal type, type must be Integer not int Long not long etc, type=" + fieldType);
        }
    }

    /**
     * 参数格式化为String
     */
    public static String formatValue(Field field, Object value) {
        ExcelField excelField = field.getAnnotation(ExcelField.class);
        if (value == null) {
            return null;
        }
        Class<?> fieldType = field.getType();
        if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (String.class.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Date.class.equals(fieldType)) {
            return new SimpleDateFormat(excelField != null ? excelField.dateFormat() : "yyyy-MM-dd HH:mm:ss").format(value);
        } else {
            return String.valueOf(value);
        }
    }

    private static Byte parseByte(String value) {
        try {
            value = value.replaceAll("　", "");
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseByte but input illegal input=" + value, e);
        }
    }

    private static Boolean parseBoolean(String value) {
        value = value.replaceAll("　", "");
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            throw new RuntimeException("parseBoolean but input illegal input=" + value);
        }
    }

    private static Integer parseInt(String value) {
        try {
            value = value.replaceAll("　", "");
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseInt but input illegal input=" + value, e);
        }
    }

    private static Short parseShort(String value) {
        try {
            value = value.replaceAll("　", "");
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseShort but input illegal input=" + value, e);
        }
    }

    private static Long parseLong(String value) {
        try {
            value = value.replaceAll("　", "");
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseLong but input illegal input=" + value, e);
        }
    }

    private static Float parseFloat(String value) {
        try {
            value = value.replaceAll("　", "");
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseFloat but input illegal input=" + value, e);
        }
    }

    private static Double parseDouble(String value) {
        try {
            value = value.replaceAll("　", "");
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseDouble but input illegal input=" + value, e);
        }
    }

    private static Date parseDate(String value, ExcelField excelField) {
        try {
            return new SimpleDateFormat(excelField != null ? excelField.dateFormat() : "yyyy-MM-dd HH:mm:ss").parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("parseDate but input illegal input=" + value, e);
        }
    }
}