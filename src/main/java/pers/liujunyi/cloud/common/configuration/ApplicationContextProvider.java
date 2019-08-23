package pers.liujunyi.cloud.common.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pers.liujunyi.cloud.common.util.ApplicationContextUtils;

/***
 * 文件名称: ApplicationContextProvider.java
 * 文件描述: 获得应用程序上下文ApplicationContext
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年03月27日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Log4j2
@Component
@Order(value = 1)
public class ApplicationContextProvider implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private ApplicationContext applicationContext;

    /**
     * 实现该接口用来初始化应用程序上下文
     * 该接口会在执行完毕@PostConstruct的方法后被执行
     * <p>
     * 接着，会进行Mapper地址扫描并加载，就是RequestMapping中指定的那个路径
     *
     * @param applicationContext 应用程序上下文
     * @throws BeansException beans异常
     */
    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        ApplicationContextUtils.applicationContext = applicationContext;
        log.info("应用程序上下文 ： [{}]", "初始化完成");
    }

    /**
     * 获取applicationContext
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
