package pers.liujunyi.cloud.common.configuration;

/***
 *  动态数据源上下文
 * @author ljy
 */
public class DataSourceContextHolder {

    /**
     * 线程独立
     */
    private static  ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    /**
     * 默认数据源  dataSource
     */
    public static final String DB_DEFAULT__MySQL = "dataSource";

    public static  String getDataBaseType() {
        return contextHolder.get();
    }

    public static  void setDataBaseType(String dataBase) {
        contextHolder.set(dataBase);
    }
    public static  void clearDataBaseType() {
        contextHolder.remove();
    }

}
