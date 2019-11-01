package pers.liujunyi.cloud.common.annotation;



import java.lang.annotation.*;

/**
 * AOP切换数据源注解，默认值dataSource，即默认主数据源
 * @author ljy
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value() default "dataSource";
}
