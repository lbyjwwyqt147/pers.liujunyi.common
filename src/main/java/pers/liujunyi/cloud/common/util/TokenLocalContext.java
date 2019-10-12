package pers.liujunyi.cloud.common.util;

/***
 * 存储token
 * @author ljy
 */
public final class TokenLocalContext {

    private TokenLocalContext() {}

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
        //LOCAL_TOKEN.remove();
        return token;
    }

    /**
     * 移除
     * @return
     */
    public static  void remove(){
        LOCAL_TOKEN.remove();
    }
}
