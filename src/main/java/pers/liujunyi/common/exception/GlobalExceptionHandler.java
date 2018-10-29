package pers.liujunyi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.liujunyi.common.restful.RestfulVo;
import pers.liujunyi.common.restful.ResultUtil;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 判断错误是否是已定义的已知错误，不是则由未知错误代替，同时记录在log中
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public RestfulVo exceptionGet(Exception e) {
        if (e instanceof DescribeException){
            DescribeException myException = (DescribeException) e;
            return ResultUtil.error(ErrorCodeEnum.ERROR.getCode(), myException.getMessage());
        }
        log.error("【系统异常】{}", e);
        return ResultUtil.error();
    }



}
