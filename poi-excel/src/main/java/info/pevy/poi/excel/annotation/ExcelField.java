package info.pevy.poi.excel.annotation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.lang.annotation.*;

/**
 * <p>
 * 列属性信息
 * 支持Java对象数据类型：Boolean、String、Short、Integer、Long、Float、Double、Date
 * 支持Excel的Cell类型为：String
 * </p>
 *
 * @author pevy
 * @since 2018/3/22
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelField {

    /**
     * 列名称
     */
    String name() default "";

    /**
     * 列宽 (大于0时生效; 如果不指定列宽，将会自适应调整宽度；)
     */
    int width() default 0;

    /**
     * 水平对齐方式
     */
    HorizontalAlignment align() default HorizontalAlignment.LEFT;
//    short align() default CellStyle.ALIGN_LEFT;

    /**
     * 时间格式化，日期类型时生效
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";
}
