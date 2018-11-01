package pers.liujunyi.common.util;

/***
 * token 工具类
 */
public final class TokenUtils {

    private TokenUtils() {}

    private static final ThreadLocal<String> LOCAL_TOKEN = new ThreadLocal<>();

    /**
     * 设置token
     * @param token
     */
    public static  void setToken(String token){
        LOCAL_TOKEN.set(token);
    }

    /**
     * 获取token
     * @return
     */
    public static  String getToken(){
        String token = LOCAL_TOKEN.get();
        LOCAL_TOKEN.remove();
        return token;
    }
}
