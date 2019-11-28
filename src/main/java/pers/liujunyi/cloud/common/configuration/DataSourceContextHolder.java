package pers.liujunyi.cloud.common.configuration;

/***
 *  动态数据源上下文
 * @author ljy
 */
public class DataSourceContextHolder {

    /**
     * 线程独立
     */
    private static final ThreadLocal<String> DATASOURCECONTEXT = new ThreadLocal<String>();
    /**
     * 默认数据源  dataSource
     */
    public static final String DB_DEFAULT_MYSQL = "dataSource";

    public static  String getCurrentDataSource() {
        return DATASOURCECONTEXT.get();
    }

    public static  void setCurrentDataSource(String dataBase) {
        DATASOURCECONTEXT.set(dataBase);
    }
    public static  void clearCurrentDataSource() {
        DATASOURCECONTEXT.remove();
    }

}
