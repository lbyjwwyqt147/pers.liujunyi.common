package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.liujunyi.cloud.common.redis.RedisTemplateUtils;
import pers.liujunyi.cloud.common.vo.BaseRedisKeys;
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

    /**
     * 根据token 获取用户信息
     * @param token
     * @return
     */
    public UserDetails getUser(String token){
        if (StringUtils.isNotBlank(token)) {
            String userKey =  BaseRedisKeys.USER_LOGIN_TOKNE;
            Object object = redisTemplateUtils.hget(userKey, token);
            if (object != null){
                UserDetails userDetail = JSON.parseObject(object.toString(), UserDetails.class);
                return userDetail;
            }
        }
        return null;
    }

    /**
     * 根据token　获取用户id
     * @param token
     * @return
     */
    public Long getUserId(String token) {
        UserDetails userDetails = this.getUser(token);
        if (userDetails != null) {
            return userDetails.getUserId();
        }
        return null;
    }

    /**
     * 根据token 获取用户信息
     * @return
     */
    public UserDetails getCurrentUserDetail(){
        UserDetails userDetail = this.getUser(TokenLocalContext.getToken());
        //if (userDetail == null) {
            userDetail = new UserDetails();
            userDetail.setUserId(1L);
            userDetail.setLessee(1L);
       // }
        return userDetail;
    }

    /**
     * 获取当前登录人userId
     * @return
     */
    public Long getPresentLoginUserId(){
        UserDetails userDetail = this.getCurrentUserDetail();
        if (userDetail != null){
            return userDetail.getUserId();
        }
        return null;
    }

    /**
     * 获取当前登录人租户ID
     * @return
     */
    public Long getPresentLoginLesseeId(){
        UserDetails userDetail = this.getCurrentUserDetail();
        if (userDetail != null){
            return userDetail.getLessee();
        }
        return null;
    }

}
