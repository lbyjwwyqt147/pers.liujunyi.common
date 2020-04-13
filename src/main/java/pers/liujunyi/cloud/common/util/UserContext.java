package pers.liujunyi.cloud.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.liujunyi.cloud.common.vo.BaseRedisKeys;
import pers.liujunyi.cloud.common.vo.user.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/***
 *
 * @author ljy
 */
public class UserContext {

    /**
     * 获取当前用户ID
     * @return
     */
    public static Long currentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object userId = request.getAttribute(BaseRedisKeys.USER_ID);
        if (userId != null) {
            return Long.valueOf(userId.toString());
        } else {
            String uid = request.getHeader(BaseRedisKeys.USER_CUR_ID);
            if (StringUtils.isNotBlank(uid)) {
                return Long.valueOf(uid);
            }
        }
        return null;
    }

    /**
     * 获取当前租户ID
     * @return
     */
    public static Long currentTenementId() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object lesseeId = request.getAttribute(BaseRedisKeys.LESSEE);
        if (lesseeId != null) {
            return Long.valueOf(lesseeId.toString());
        } else {
            String lessee = request.getHeader(BaseRedisKeys.LESSEE);
            if (StringUtils.isNotBlank(lessee)) {
                return Long.valueOf(lessee);
            }
        }
        return null;
    }


    /**
     * 获取当前用户信息
     * @return
     */
    public static UserDetails currentUser() {
        Method mh = ReflectionUtils.findMethod(ApplicationContextUtils.getBean(UserUtils.class).getClass(), "getCurrentUserDetail");
        Object obj = ReflectionUtils.invokeMethod(mh,  ApplicationContextUtils.getBean(UserUtils.class));
        return (UserDetails) obj;
    }


}
