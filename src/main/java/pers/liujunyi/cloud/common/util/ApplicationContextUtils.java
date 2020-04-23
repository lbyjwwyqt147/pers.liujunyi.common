package pers.liujunyi.cloud.common.util;

import org.springframework.context.ApplicationContext;

/***
 * 文件名称: ApplicationContextUtils.java
 * 文件描述: 下文ApplicationContext 工具类
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年03月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public class ApplicationContextUtils {

    public static ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     * @param name 参数传入要获取的实例的类名 首字母小写，这是默认的
     * @return
     */
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean.
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 判断某个bean是不是存在
     */
    public static boolean has(String name) {
        return applicationContext.containsBean(name);
    }
}
