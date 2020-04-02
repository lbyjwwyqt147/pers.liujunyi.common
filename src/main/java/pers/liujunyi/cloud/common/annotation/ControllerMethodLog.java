package pers.liujunyi.cloud.common.annotation;

import java.lang.annotation.*;

/***
 * 文件名称: MethodLog
 * 文件描述: 日志切面注解
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/3/31 13:36
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerMethodLog {

    /** 描述  */
    String desc() default "";
    /** 操作类型 1:新增  2:修改 3: 删除 */
    byte operType() default 1;
    /** 操作模块 */
    String operModule() default "";
    /** 日志类型 0:正常请求日志  1:异常日志 3: 登录日志  4：登出日志 */
    byte logType() default 0;
    /** service bean 名称*/
    String serviceClass() default "";
    /** 查询数据的方法名称 */
    String findDataMethod() default "findById";
    /** 业务实体类 名称*/
    String entityBeanClass() default "";
    /** 是否为批量类型操作 */
    boolean paramIsArray() default false;
    /** 查询详情的参数类型 例如： Long */
    String parameterType() default "Long";
    /** 需要的查询字段 例如：id */
    String parameterKey() default "id";
}
