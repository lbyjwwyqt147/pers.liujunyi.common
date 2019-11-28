package pers.liujunyi.cloud.common.vo;

/***
 * redis key
 * @author ljy
 */
public class BaseRedisKeys {
    /** 用户登录toke redis key  */
    public static final String USER_LOGIN_TOKNE = "user:login:token";
    /** 用户 toke redis key  */
    public static final String USER_DETAILS_TOKNE = "user:details:token";
    /** 用户 authentication redis key  */
    public static final String USER_DETAILS_AUTHENTICATION = "user:details:authentication";
    /** 租户 */
    public static final String LESSEE = "tenement";
    /** 租户数据源配置 */
    public static final String LESSEE_DATA_SOURCE = "tenement:dataSource";
    /** userID */
    public static final String USER_ID = "user_id";
    /** 用户详情 */
    public static final String USER_INFO = "user_details";
}
