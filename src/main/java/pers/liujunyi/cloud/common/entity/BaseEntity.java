package pers.liujunyi.cloud.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pers.liujunyi.cloud.common.annotation.CustomerField;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/***
 * 抽象　Entity　部分
 * @author ljy
 */
@Data
@EntityListeners({AuditingEntityListener.class, TenementAuditListener.class})
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -10498490916283309L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 创建时间 */
    @CustomerField(isLog = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Column(columnDefinition=" timestamp DEFAULT NULL COMMENT '创建时间'")
    private Date createTime;

    /** 创建人ID */
    @CustomerField(isLog = false)
    @CreatedBy
    @Column(columnDefinition="bigint(20) DEFAULT NULL COMMENT '创建人ID'")
    private Long createUserId;


    /** 最后更新时间 */
    @CustomerField(isLog = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(columnDefinition=" timestamp DEFAULT NULL COMMENT '最后更新时间'")
    private Date updateTime;

    /** 最后更新人ID */
    @CustomerField(isLog = false)
    @LastModifiedBy
    @Column(columnDefinition="bigint(20) DEFAULT NULL COMMENT '最后更新人ID'")
    private Long updateUserId;

    /** 租户Id  */
    @CustomerField(isLog = false)
    @Column(columnDefinition="bigint(20) DEFAULT NULL COMMENT '租户Id'")
    private Long tenementId;

    @CustomerField(isLog = false)
    @Column(columnDefinition="bigint(20) DEFAULT '1' COMMENT '乐观锁版本号'")
    private Long dataVersion;

}
