package pers.liujunyi.cloud.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/***
 * 抽象　Entity　部分
 */
@Data
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -10498490916283309L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 创建时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createTime;

    /** 创建人ID */
    @CreatedBy
    private Long createUserId;


    /** 最后更新时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updateTime;

    /** 最后更新人ID */
    @LastModifiedBy
    private Long updateUserId;

    /** 版本号  */
    @Version
    private Long dataVersion;

}
