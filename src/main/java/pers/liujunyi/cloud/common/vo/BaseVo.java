package pers.liujunyi.cloud.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/***
 * BaseVo
 * @author ljy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseVo implements Serializable {

    private static final long serialVersionUID = 4037747476437063554L;

    /** ID */
    private Long id;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    private Long createUserId;

    /** 最后更新时间 */
    private Date updateTime;

    /** 最后更新人ID */
    private Long updateUserId;

    /** 版本号  */
    private Long dataVersion;
}
