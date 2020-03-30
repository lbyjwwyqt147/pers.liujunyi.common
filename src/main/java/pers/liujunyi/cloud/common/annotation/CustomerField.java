package pers.liujunyi.cloud.common.annotation;

import java.lang.annotation.*;

/***
 * 文件名称: CustomerField
 * 文件描述: 
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/3/30 20:26
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CustomerField {

    /** 字段的描述注解 */
    String desc() default "";
    /** 是否记录日志  true 记录  false  不记录  */
    boolean isLog() default true;
}
