package pers.liujunyi.common.redis;

/***
 *
 * @FileName: RedisKeys
 * @Company:
 * @author    ljy
 * @Date      2018年05月120日
 * @version   1.0.0
 * @remark:   redis key
 *
 */
public final class RedisKeys {
    private RedisKeys() {}
    // 用户token   key
    public static final String USER_TOKEN_KEY = "security:jwt:user:token";
    // 用户帐号id 字段key
    public static final String USER_TOKEN_FIELD = "account:id:";
    // 用户信息  key
    public static final String USER_KEY = "cloud:user:info";
    // 用户ID信息  key
    public static final String USER_ID_KEY = "cloud:user:ids";
}
