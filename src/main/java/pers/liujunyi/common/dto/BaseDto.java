package pers.liujunyi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/***
 * BaseDto
 */
@Data
@EntityListeners(AuditingEntityListener.class)
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

    /** 临时数据 */
    private String temporary;

    /** 0: 启动 1：禁用  */
    private Byte status = 0;

    /** 创建时间 */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /** 创建人ID */
    private Long createUserId;

    /** 最后更新时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updateTime;

    /** 最后更新人ID */
    @LastModifiedBy
    private Long updateUserId;
}
