package pers.liujunyi.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/***
 * 文件名称: CorsConfig.java
 * 文件描述: 跨域设置
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Configuration
public class CorsConfig {

    /**
     *
     * @return
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        /* 允许指定域名使用 */
        corsConfiguration.addAllowedOrigin("*");
        /* 允许任何头 */
        corsConfiguration.addAllowedHeader("*");
        /* 允许任何方法（post、get等） */
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    /**
     *
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
