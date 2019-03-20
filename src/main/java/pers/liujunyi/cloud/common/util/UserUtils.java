package pers.liujunyi.cloud.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.liujunyi.cloud.common.redis.RedisTemplateUtils;
import pers.liujunyi.cloud.common.vo.user.UserDetails;

/***
 *
 * @FileName: UserUtils
 * @Company:
 * @author    ljy
 * @Date      2018年05月120日
 * @version   1.0.0
 * @remark:   用户信息操作工具类
 *
 */
@Component
public class UserUtils {
    @Autowired
    private RedisTemplateUtils redisTemplateUtils;

   /* @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;*/

    /**
     * 根据token 获取用户信息
     * @param token
     * @return
     */
    public UserDetails getUser(String token){
       /* String userKey =  RedisKeys.USER_KEY;
        Object object = redisUtil.hget(userKey, token);
        if (object != null){
            UserDetail userDetail = JSON.parseObject(object.toString(), UserDetail.class);
            return userDetail;
        }*/
        return null;
    }

    /**
     * 根据token　获取用户id
     * @param token
     * @return
     */
    public Long getUserId(String token) {
       /* String userIdKey =  RedisKeys.USER_ID_KEY;
        Object object = redisUtil.hget(userIdKey, token);
        return object != null ? Long.valueOf(object.toString()) : null;*/
       return null;
    }

    /**
     * 根据token 获取用户信息
     * @return
     */
    public UserDetails getUserDetail(){
      /*  String token = TokenUtils.getToken();
        String userKey =  RedisKeys.USER_KEY;
        Object object = redisUtil.hget(userKey, token);
        if (object != null){
            UserDetail userDetail = JSON.parseObject(object.toString(), UserDetail.class);
            return userDetail;
        }*/
        UserDetails userDetail = new UserDetails();
        userDetail.setUserId(1L);
        return userDetail;
    }

    /**
     * 获取当前登录人userId
     * @return
     */
    public Long getPresentLoginUserId(){
        UserDetails userDetail = this.getUserDetail();
        if (userDetail != null){
            return userDetail.getUserId();
        }
        return null;
    }

    /**
     * 获取用户token值
     * @param request
     * @return
     */
    /*public String getUserToken(HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        final String authToken = token.substring(tokenHead.length());
        return authToken;
    }*/
}
