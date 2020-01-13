package pers.liujunyi.cloud.common.util;

import pers.liujunyi.cloud.common.vo.user.UserDetails;

/***
 * 设置登录人信息
 * @author ljy
 */
public class CurrentUserHolder {

    private static final ThreadLocal<UserDetails> CURRENT_USER_HOLDER = new ThreadLocal<>();

    public static UserDetails getUser() {
        return CURRENT_USER_HOLDER.get();
    }

    public static void setUser(UserDetails user) {
        CURRENT_USER_HOLDER.set(user);
    }

    public static  void remove(){
        CURRENT_USER_HOLDER.remove();
    }
}
