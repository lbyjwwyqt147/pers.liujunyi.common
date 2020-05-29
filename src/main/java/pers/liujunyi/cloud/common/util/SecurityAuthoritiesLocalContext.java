package pers.liujunyi.cloud.common.util;

/***
 * 存储当前用户角色权限
 * @author ljy
 */
public final class SecurityAuthoritiesLocalContext {

    private SecurityAuthoritiesLocalContext() {}

    private static final ThreadLocal<String[]> AUTHORITIES = new ThreadLocal<>();

    /**
     * 设置Authorities
     * @param authorities
     */
    public static  void setAuthorities(String[] authorities){
        AUTHORITIES.set(authorities);
    }

    /**
     * 获取Authorities
     * @return
     */
    public static  String[] getAuthorities(){
        String[] authorities = AUTHORITIES.get();
        //AUTHORITIES.remove();
        return authorities;
    }

    /**
     * 移除
     * @return
     */
    public static  void remove(){
        AUTHORITIES.remove();
    }
}
