package pers.liujunyi.cloud.common.restful;

import com.alibaba.fastjson.JSON;
import pers.liujunyi.cloud.common.exception.ErrorCodeEnum;
import pers.liujunyi.cloud.common.util.DateTimeUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/***
 *  对返回给前端的数据进行格式封装处理
 */
public final class ResultUtil {

    private ResultUtil() { }

    /**
     * 返回信息 传入返回具体出参
     * @param  success   boolean
     * @param data
     * @return
     */
    public static ResultInfo info(boolean success, Object data) {
       if (success) {
           return success(data);
       }
       return fail(data);
    }

    /**
     * 返回信息 不返回具体出参
     * @param  success
     * @return
     */
    public static ResultInfo info(boolean success) {
        if (success) {
            return success();
        }
        return fail();
    }

    /**
     * 返回成功，传入返回体具体出參
     * @param data
     * @return
     */
    public static ResultInfo success(Object data) {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.SUCCESS, data);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(true);
        return result;
    }

    /**
     * 返回成功，传入返回体具体出參(包含扩展数据)
     * @param data
     * @param extend 扩展数据
     * @return
     */
    public static ResultInfo success(Object data, Object extend) {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.SUCCESS, data, extend);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(true);
        return result;
    }

    /**
     * 返回成功，不需要返回具体参数
     * @return
     */
    public static ResultInfo success() {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.SUCCESS);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(true);
        return result;
    }

    /**
     * 返回参数认证消息，传入返回体具体出參
     * @param message
     * @return
     */
    public static ResultInfo params(String message) {
        ResultInfo result = new ResultInfo();
        result.setStatus(ErrorCodeEnum.PARAMS.getCode());
        result.setMessage(message);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(false);
        return result;
    }

    /**
     * 返回参数认证消息，传入返回体具体出參(包含扩展数据)
     * @param data
     * @param extend
     * @return
     */
    public static ResultInfo params(String message, Object data, Object extend) {
        ResultInfo result = params(message);
        result.setData(data);
        result.setExtend(extend);
        return result;
    }

    /**
     * 返回失败，传入返回体具体出參
     * @param data
     * @return
     */
    public static ResultInfo fail(Object data) {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.FAIL, data);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(false);
        return result;
    }

    /**
     * 返回失败，传入返回体具体出參(包含扩展数据)
     * @param data
     * @param extend
     * @return
     */
    public static ResultInfo fail(Object data, Object extend) {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.FAIL, data, extend);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(false);
        return result;
    }

    /**
     * 返回失败，不需要返回具体参数
     * @return
     */
    public static ResultInfo fail() {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.FAIL);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义返回信息
     * @param status  状态码
     * @param  message 消息
     * @param data 数据
     * @param success 是否处理成功
     * @return
     */
    public static ResultInfo info(Integer status, String message, Object data, Boolean success) {
        ResultInfo result = new ResultInfo();
        result.setStatus(status);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(success);
        return result;
    }

    /**
     * 自定义返回信息
     * @param errorCodeEnum
     * @param data 数据
     * @return
     */
    public static ResultInfo info(ErrorCodeEnum errorCodeEnum, Object data, Boolean success) {
        ResultInfo result = new ResultInfo(errorCodeEnum, data);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(success);
        return result;
    }

    /**
     * 自定义返回信息
     * @param errorCodeEnum
     * @param data 数据
     * @return
     */
    public static ResultInfo info(ErrorCodeEnum errorCodeEnum, Object data) {
        ResultInfo result = new ResultInfo(errorCodeEnum, data);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        return result;
    }

    /**
     * 自定义错误信息
     * @param code
     * @param message
     * @return
     */
    public static ResultInfo error(Integer code, String message) {
        ResultInfo result = new ResultInfo();
        result.setStatus(code);
        result.setMessage(message);
        result.setSuccess(false);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        return result;
    }

    /**
     * 返回异常信息，在已知的范围内
     * @return
     */
    public static ResultInfo error() {
        ResultInfo result = new ResultInfo(ErrorCodeEnum.ERROR);
        result.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        result.setSuccess(false);
        return result;
    }

    /**
     * 将json输出到前端(参数非json格式)
     * @param response
     * @param obj  任意类型
     */
    public static void writeJavaScript(HttpServletResponse response, Object obj) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        /* 设置浏览器跨域访问 */
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,appId,appKey,systemCode,credential,accessToken,sign");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            PrintWriter out = response.getWriter();
            out.write(JSON.toJSONString(obj));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将数据输出给前端
     * @param response
     * @param errorCodeEnum
     * @param obj
     */
    public static void writeJavaScript(HttpServletResponse response, ErrorCodeEnum errorCodeEnum, Object obj) {
        //自定义的信息方便自己查看
        Map<String, Object> map = new HashMap<>();
        map.put("message", obj);
        ResultInfo restfulVo = info(errorCodeEnum, map);
        restfulVo.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        writeJavaScript(response, restfulVo);
    }

    /**
     * 将数据输出给前端
     * @param response
     * @param errorCodeEnum
     */
    public static void writeJavaScript(HttpServletResponse response, ErrorCodeEnum errorCodeEnum) {
        //自定义的信息方便自己查看
        ResultInfo restfulVo = info(errorCodeEnum, null);
        restfulVo.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        writeJavaScript(response, restfulVo);
    }

    /**
     * 将数据输出给前端
     * @param response
     * @param message
     */
    public static void writeJavaScript(HttpServletResponse response,ErrorCodeEnum errorCodeEnum, String message) {
        //自定义的信息方便自己查看
        ResultInfo restfulVo = info(errorCodeEnum, null);
        restfulVo.setMessage(message);
        restfulVo.setTimestamp(DateTimeUtils.getCurrentDateTimeAsString());
        writeJavaScript(response, restfulVo);
    }

}
