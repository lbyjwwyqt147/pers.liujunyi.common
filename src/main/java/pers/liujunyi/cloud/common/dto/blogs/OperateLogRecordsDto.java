package pers.liujunyi.cloud.common.dto.blogs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.liujunyi.cloud.common.dto.BaseDto;

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

    /** 操作人id */
    private Long operateUserId;

    /** 操作人编号 */
    private String operateUserNumber;

    /** 操作人名称 */
    private String operateUserName;

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

    /** 日志内容 */
    private String logContent;

    /** 操作状态 */
    private Byte operateStatus;

}