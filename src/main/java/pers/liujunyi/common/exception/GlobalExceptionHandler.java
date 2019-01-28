package pers.liujunyi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pers.liujunyi.common.restful.ResultInfo;
import pers.liujunyi.common.restful.ResultUtil;

import javax.validation.ValidationException;
import java.util.List;

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
     * 参数绑定 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResultInfo handlerBindException(BindException e) {
        StringBuffer errorMsg = new StringBuffer();
        e.getAllErrors().forEach(item -> errorMsg.append(item.getDefaultMessage()).append("."));
        log.error("【参数绑定失败】： ", e);
        log.info(errorMsg.toString());
        return ResultUtil.error(ErrorCodeEnum.PARAMS.getCode(), errorMsg.toString());
    }

    /**
     * validation 进行数据校验 异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultInfo handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuffer errorMsg = new StringBuffer();
        BindingResult result = e.getBindingResult();
        result.getAllErrors().forEach(item -> errorMsg.append(item.getDefaultMessage()).append("."));
        log.error("【参数验证失败】： ", e);
        log.info(errorMsg.toString());
        return ResultUtil.error(ErrorCodeEnum.PARAMS.getCode(), errorMsg.toString());
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultInfo handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("【不支持当前请求方法】：", e);
        return ResultUtil.error(HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public ResultInfo handleValidationException(ValidationException e) {
        log.error("【参数验证失败】", e);
        return  ResultUtil.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultInfo handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("【参数验证失败】", e);
        return  ResultUtil.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    /**
     * 400 - Bad Request
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultInfo handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("【参数解析失败】", e);
        return  ResultUtil.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }



}
