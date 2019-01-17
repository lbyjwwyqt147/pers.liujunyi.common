package pers.liujunyi.common.restful;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.liujunyi.common.exception.ErrorCodeEnum;

import java.io.Serializable;

/***
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResultInfo implements Serializable {

    private static final long serialVersionUID = -4450312255234324795L;

    /** 状态码 */
    private String status;
    /** 描述信息 */
    private String message;
    /** 数据集 */
    private Object data;
    /** 扩展数据 */
    private Object extend;
    /** 时间 */
    private String timestamp;
    /** 是否处理成功 */
    private Boolean success = true;
    @JSONField(serialize = false)
    @JsonIgnore
    private ErrorCodeEnum errorCodeEnum;

    public ResultInfo(){

    }

    public ResultInfo(ErrorCodeEnum  errorCodeEnum){
        this.status = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getMessage();
    }

    public ResultInfo(ErrorCodeEnum  errorCodeEnum, Object data, Object extend){
        this.status = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getMessage();
        this.data = data;
        this.extend = extend;
    }

    public ResultInfo(ErrorCodeEnum  errorCodeEnum, Object data){
        this.status = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getMessage();
        this.data = data;
    }
}
