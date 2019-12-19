package pers.liujunyi.cloud.common.util;

/***
 * 存储当前用户角色权限
 * @author ljy
 */
public final class SecurityLocalContext {

    private SecurityLocalContext() {}

    private static final ThreadLocal<String[]> AUTHORITIES = new ThreadLocal<>();

    /**
     * 设置Authorities
     * @param token
     */
    public static  void setAuthorities(String[] token){
        AUTHORITIES.set(token);
    }

    /**
     * 获取Authorities
     * @return
     */
    public static  String[] getAuthorities(){
        String[] token = AUTHORITIES.get();
        //LOCAL_TOKEN.remove();
        return token;
    }

    /**
     * 移除
     * @return
     */
    public static  void remove(){
        AUTHORITIES.remove();
    }
}
