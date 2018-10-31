package pers.liujunyi.common.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/***
 * 抽象　Entity　部分
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -10498490916283309L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 用户ID */
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

}
