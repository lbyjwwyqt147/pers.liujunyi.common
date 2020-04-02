package pers.liujunyi.cloud.common.dto.blogs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.liujunyi.cloud.common.dto.BaseDto;

/***
 * 文件名称: ChangeRecordLogDto.java
 * 文件描述: 变更记录日志
 * 公 司:
 * 内容摘要:
 * 完成日期:2019年02月21日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeRecordLogDto  extends BaseDto {


    /** 操作日志id */
    private String logId;

    /** 字段名称 */
    private String fieldName;

    /** 字段描述 */
    private String fieldDescription;

    /** 修改之前值 */
    private String beforeValue;

    /** 修改之后值 */
    private String afterValue;

    /** 变更状态 0：一致   1：不一致  */
    private Byte changeStatus;

}