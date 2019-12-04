package pers.liujunyi.cloud.common.util;

/**
 * 常量信息
 * @author ljy
 */
public class UtilConstant {
    /** 禁用状态 */
    public static final Byte DISABLE_STATUS = 1;
    /** 启用状态 */
    public static final Byte ENABLE_STATUS = 0;
    public static final String DATA_GRID_MESSAGE = "无数据";
    public static final String RESOURCE_ID = "resource_id";
    /** Mongo 事物 */
    public static final String MONGO_DB_MANAGER = "mongoTransactionManager";

    /**
     * 数据状态值
     * @param status
     * @return
     */
    public static String getStatusValue(Byte status) {
        String statusValue = "正常";
        switch (status.byteValue()) {
            case 1:
                statusValue = "禁用";
                break;
            default:
                break;
        }
        return statusValue;
    }

}
