package pers.liujunyi.cloud.common.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @author ljy
 */
@Component
public class CustomDruidDataSource {

    @Autowired
    private DruidDataSourceProperties druidDataSourceProperties;

    /**
     * 初始化数据源属性
     * @param druidDataSource
     * @throws SQLException
     */
    public DruidDataSource initDruidDataSource(DruidDataSource druidDataSource) throws SQLException {
        //属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：
        //监控统计用的filter:stat
        //日志用的filter:log4j
        //防御sql注入的filter:wall
        druidDataSource.setFilters(druidDataSourceProperties.getFilters());
        //最大连接池数量
        druidDataSource.setMaxActive(druidDataSourceProperties.getMaxActive());
        // 	初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
        druidDataSource.setInitialSize(druidDataSourceProperties.getInitialSize());
        //获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
        druidDataSource.setMaxWait(druidDataSourceProperties.getMaxWait());
        //最小连接池数量
        druidDataSource.setMinIdle(druidDataSourceProperties.getMinIdle());
        //有两个含义：
        //1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。
        //2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidDataSourceProperties.getTimeBetweenEvictionRunsMillis());
        //连接保持空闲而不被驱逐的最小时间
        druidDataSource.setMinEvictableIdleTimeMillis(druidDataSourceProperties.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(druidDataSourceProperties.getValidationQuery());
        //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，
        //(空闲时测试)执行validationQuery检测连接是否有效。
        druidDataSource.setTestWhileIdle(druidDataSourceProperties.getTestWhileIdle());
        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        druidDataSource.setTestOnBorrow(druidDataSourceProperties.getTestOnBorrow());
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        druidDataSource.setTestOnReturn(druidDataSourceProperties.getTestOnReturn());
        //移除泄露的链接
        druidDataSource.setRemoveAbandoned(druidDataSourceProperties.getRemoveAbandoned());
        //泄露连接的定义时间(要超过最大事务的处理时间)
        druidDataSource.setRemoveAbandonedTimeout(druidDataSourceProperties.getRemoveAbandonedTimeoutMillis());
        //移除泄露连接发生是是否记录日志
        druidDataSource.setLogAbandoned(druidDataSourceProperties.getLogAbandoned());
        druidDataSource.setMaxOpenPreparedStatements(druidDataSourceProperties.getMaxOpenPreparedStatements());
        druidDataSource.setPoolPreparedStatements(druidDataSourceProperties.getPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidDataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setValidationQueryTimeout(druidDataSourceProperties.getValidationQueryTimeout());
        druidDataSource.setQueryTimeout(druidDataSourceProperties.getQueryTimeout());
        druidDataSource.setTransactionThresholdMillis(druidDataSourceProperties.getTransactionThresholdMillis());
        druidDataSource.setConnectionProperties(druidDataSourceProperties.getConnectionProperties());
        druidDataSource.setUseGlobalDataSourceStat(druidDataSourceProperties.getUseGlobalDataSourceStat());
        return druidDataSource;
    }

}
