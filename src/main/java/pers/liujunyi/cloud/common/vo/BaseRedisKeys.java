package pers.liujunyi.cloud.common.vo;

/***
 * redis key
 * @author ljy
 */
public class BaseRedisKeys {
    /** 用户登录toke redis key  */
    public static final String USER_LOGIN_TOKNE = "user:login";
    /** 用户 toke redis key  */
    public static final String USER_DETAILS_TOKNE = "user:details";
    /** 用户 authentication redis key  */
    public static final String USER_AUTHORITIES_TOKEN = "user:authorities";
    /** 租户 */
    public static final String LESSEE = "tenement";
    /** 用户ID */
    public static final String SUBSCRIBER = "subscriber";
    /** 租户数据源配置 */
    public static final String LESSEE_DATA_SOURCE = "tenement:dataSource";
    /** userID */
    public static final String USER_ID = "user:ids";
    /** 用户详情 */
    public static final String USER_INFO = "user:particulars";
}
