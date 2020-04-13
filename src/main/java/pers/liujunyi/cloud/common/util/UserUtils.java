package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pers.liujunyi.cloud.common.restful.ResultInfo;
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

    @Value("${data.user.client}")
    private String userClient;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取当前登录用户信息
     * @param token
     * @param userId
     * @return
     */
    public UserDetails getCurrentUser(String token, Long userId) {
        UserDetails userDetails = null;
        userDetails = this.getUserByToken(token);
        if (userDetails == null && userId != null) {
            userDetails = this.getUserById(userId);
            if (userDetails == null) {
                userDetails = new UserDetails();
                userDetails.setUserId(0L);
                userDetails.setLessee(0L);
            }
        }
        return userDetails;
    }

    /**
     * 根据token 获取用户信息
     * @param token
     * @return
     */
    public UserDetails getUserByToken(String token){
        if (StringUtils.isNotBlank(token)) {
            String requestUrl = userClient + "/ignore/accounts/token?token=" + token;
            String result = restTemplate.getForObject(requestUrl, String.class);
            ResultInfo resultInfo = JSONObject.parseObject(result, ResultInfo.class);
            if (resultInfo.getSuccess()) {
                return JSONObject.parseObject(JSON.toJSONString(resultInfo.getData()), UserDetails.class);
            }
        }
        return null;
    }

    /**
     * 根据id 获取用户信息
     * @param userId
     * @return
     */
    public UserDetails getUserById(Long userId){
        if (userId != null) {
            String requestUrl = userClient + "/ignore/accounts/id?id=" + userId;
            String result = restTemplate.getForObject(requestUrl, String.class);
            ResultInfo resultInfo = JSONObject.parseObject(result, ResultInfo.class);
            if (resultInfo.getSuccess()) {
                return JSONObject.parseObject(JSON.toJSONString(resultInfo.getData()), UserDetails.class);
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
        UserDetails userDetails = this.getUserByToken(token);
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
        UserDetails userDetail = this.getUserByToken(TokenLocalContext.getToken());
        if (userDetail == null) {
            userDetail = this.getUserById(UserContext.currentUserId());
            if (userDetail == null) {
                userDetail = new UserDetails();
                userDetail.setUserId(0L);
                userDetail.setLessee(0L);
            }
        }
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
