package pers.liujunyi.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/***
 * BaseVo
 */
@Data
public class BaseVo implements Serializable {

    private static final long serialVersionUID = 4037747476437063554L;

    /** 用户ID */
    private Long id;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private Long createUserId;

    /** 最后更新时间 */
    private Date updateTime;

    /** 最后更新人ID */
    private Long updateUserId;
}
