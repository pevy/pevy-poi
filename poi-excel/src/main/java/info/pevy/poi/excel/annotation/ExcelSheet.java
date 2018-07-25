package info.pevy.poi.excel.annotation;

import org.apache.poi.hssf.util.HSSFColor;

import java.lang.annotation.*;

/**
 * <p>
 * 表信息
 * </p>
 *
 * @author pevy
 * @since 2018/3/22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelSheet {

    /**
     * 表名称
     */
    String name() default "";

    /**
     * 表头/首行的颜色
     */
    HSSFColor.HSSFColorPredefined headColor() default HSSFColor.HSSFColorPredefined.LIGHT_GREEN;
//    short headColor() default HSSFColor.LIGHT_GREEN.index;
}

