package pers.liujunyi.cloud.common.vo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户详情信息
 * @author Administrator
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class UserDetails implements Serializable {

    private static final long serialVersionUID = -437649344203806493L;
    /** 用户id  */
    private Long userId;
    /** 用户帐号 */
    @JsonIgnore
    private String userAccounts;
    /** 用户编号  */
    private String userNumber;
    /** 用户密码 */
    @JsonIgnore
    private String userPassword;
    /** 绑定的手机号 */
    private String mobilePhone;
    /** 状态：0：正常  1：冻结 */
    private Byte userStatus;
    /** 用户类别   0：超级管理员 1：普通管理员  2：员工  3：顾客 */
    private Byte userCategory;
    /** 真实姓名 */
    private String userName;
    /** 昵称 */
    private String userNickName;
    /** 头像 */
    private String portrait;
    /** 租户ID */
    private Long lessee;
    /** 所属机构ID */
    private Long orgId;
    /** 所属机构名称 */
    private String orgName;
    /** token */
    private String token;

}
