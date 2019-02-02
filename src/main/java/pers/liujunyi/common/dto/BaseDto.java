package pers.liujunyi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/***
 * BaseDto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto implements Serializable {

    private static final long serialVersionUID = -5375298588993640910L;

    /** ID */
    private Long id;

    /** 凭证 */
    private String credential;

    /** 标记 1：代表新增 2：代表修改 */
    private Byte mark;

    /** 历史数据 */
    private String history;

    /** 0: 启动 1：禁用  */
    private Byte status = 0;
}
