package pers.liujunyi.cloud.common.query.jpa.annotation;

import java.lang.annotation.*;

/***
 * 自定义注解，用来标识字段
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface QueryCondition {

    // 数据库中字段名,默认为空字符串,则Query类中的字段要与数据库中字段一致
    String column() default "";

    // equal, like, gt, lt...
    MatchType func() default MatchType.equal;

    // or 完全匹配 条件值字段
    String orFieldValue() default "";

    // or like 匹配 条件值字段
    String orLikeFieldValue() default "";

    // object是否可以为null
    boolean nullable() default false;

    // 字符串是否可为空
    boolean emptyable() default false;

    // 范围比较值字段
    String[] rangeField() default "";

}
