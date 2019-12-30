package pers.liujunyi.cloud.common.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author ljy
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.druid")
@RefreshScope
public class DruidDataSourceProperties {

    private Integer initialSize;
    private Integer maxActive;
    private Integer maxWait;
    private Integer minIdle;
    private Long timeBetweenEvictionRunsMillis;
    private Long minEvictableIdleTimeMillis;
    private Integer maxOpenPreparedStatements;
    private Boolean poolPreparedStatements;
    private Integer maxPoolPreparedStatementPerConnectionSize;
    private String validationQuery;
    private Integer validationQueryTimeout;
    private Integer queryTimeout;
    private Long transactionThresholdMillis;
    private Boolean testWhileIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private Boolean removeAbandoned;
    private Integer removeAbandonedTimeoutMillis;
    private Boolean logAbandoned;
    private String filters;
    private String connectionProperties;
    private Boolean useGlobalDataSourceStat;
}
