package pers.liujunyi.common.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/***
 * 文件名称: ApiVersion.java
 * 文件描述: 自定义api版本号注解
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2018年08月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface ApiVersion {
    /** 版本号 */
    int value();
}
