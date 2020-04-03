package pers.liujunyi.cloud.common.dto.blogs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.liujunyi.cloud.common.dto.BaseDto;

import java.util.Date;

/***
 * 文件名称: OperateLogRecordsDto.java
 * 文件描述: 操作日志记录
 * 公 司:
 * 内容摘要:
 *
 * 完成日期:2019年03月11日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperateLogRecordsDto extends BaseDto {

    private static final long serialVersionUID = -1994799462808121477L;
    /** 操作日志id */
    private String logId;

    /** 操作人id */
    private Long operateUserId;

    /** 操作人编号 */
    private String operateUserNumber;

    /** 操作人名称 */
    private String operateUserName;

    /** 操作人登录帐号 */
    private String operateUserAccount;

    /** 用户IP 地址 */
    private String ipAddress;

    /** 用户类别   0：超级管理员 1：普通管理员 2：内部职工 3：普通用户   */
    private Byte operateUserType;

    /** 操作模块 */
    private String operateModule;

    /** 应用名称 */
    private String applicationName;

    /** 操作类型 1:新增  2:修改 3: 删除 */
    private Byte operateType;

    /** 日志类型 0:正常请求日志  1:异常日志 3: 登录日志  4：登出日志 */
    private Byte logType;

    /** 操作的表名 */
    private String tableName;

    /** 操作方法名 */
    private String operateMethod;

    /** 说明 */
    private String explain;

    /** 参数 */
    private String parameters;

    /** 响应执行时间 */
    private Date responseStartTime;

    /** 响应结束时间 */
    private Date responseEndTime;

    /** 消耗时间(ms) */
    private Long expendTime;

    /** 异常信息 */
    private String errorMessage;

    /** 操作结果 */
    private String resultMessage;

    /** 操作状态 0: 成功  1： 失败 */
    private Byte operateStatus;

    /** 变更数据项  */
    private String changeDataItem;

}
