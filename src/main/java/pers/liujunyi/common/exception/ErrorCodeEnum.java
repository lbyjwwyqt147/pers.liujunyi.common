package pers.liujunyi.common.exception;

/***
 * 异常代码 值
 */

public enum ErrorCodeEnum {

    SUCCESS("ok.", "0"),
    FAIL("fail.", "-1"),
    EXIST("数据已经存在.","10"),
    PARAMS("参数错误.","501"),
    ERROR("服务器遇到错误，无法完成请求,请联系管理员.","500"),
    DELETE("删除成功.","3"),
    AUTHORITY("无访问权限,请联系管理员.","401"),
    LOGIN_WITHOUT("登录超时,请重新登录.","504"),
    LOGIN_FAIL("登录失败.","-2"),
    LOGIN_INCORRECT("登录账户或者密码错误.","-3"),
    USER_LOCK("你登陆的用户已被锁定,请联系管理员.","-5"),
    TOKEN_INVALID("无效的用户token.","530"),
    NO_TOKEN("要访问此资源，需要完全身份验证,缺少请求头参数,Authorization传递是token值所以参数是必须的.","401");

    private String message ;
    private String code ;

    ErrorCodeEnum(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
