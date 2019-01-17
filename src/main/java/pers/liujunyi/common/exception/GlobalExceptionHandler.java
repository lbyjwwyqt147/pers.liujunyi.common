package pers.liujunyi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.liujunyi.common.restful.ResultInfo;
import pers.liujunyi.common.restful.ResultUtil;

/***
 * 异常处理
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 判断错误是否是已定义的已知错误，不是则由未知错误代替，同时记录在log中
     * @param e
     * @return
     */
    @ExceptionHandler(value = DescribeException.class)
    @ResponseBody
    public ResultInfo handlerDescribeException(DescribeException e) {
        log.error("【系统异常】： ", e);
        return ResultUtil.error(ErrorCodeEnum.ERROR.getCode(), e.getMessage());
    }

    /**
     * 判断错误是否是已定义的已知错误，不是则由未知错误代替，同时记录在log中
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultInfo handlerException(Exception e) {
        if (e instanceof DescribeException){
            DescribeException myException = (DescribeException) e;
            return ResultUtil.error(ErrorCodeEnum.ERROR.getCode(), myException.getMessage());
        }
        log.error("【系统异常】： ", e);
        return ResultUtil.error();
    }


    /**
     * validation 进行数据校验 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResultInfo handlerBindException(BindException e) {
        StringBuffer errorMsg = new StringBuffer();
        e.getAllErrors().forEach(item -> errorMsg.append(item.getDefaultMessage()).append("."));
        log.error("【validation 参数校验异常】： ", e);
        return ResultUtil.error(ErrorCodeEnum.PARAMS.getCode(), errorMsg.toString());
    }

}
