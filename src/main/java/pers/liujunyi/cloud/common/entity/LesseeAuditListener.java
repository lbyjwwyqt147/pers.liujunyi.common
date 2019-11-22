package pers.liujunyi.cloud.common.entity;

import pers.liujunyi.cloud.common.util.UserContext;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author ljy
 */

public class LesseeAuditListener {

    @PostPersist
    private void postPersist(BaseEntity entity) {
        //在数据库中存储新实体（在commit或期间flush）。
        // 设置租户ID
        Long lesseeId = UserContext.currentLesseeId();
        if (lesseeId != null) {
            entity.setLessee(Long.valueOf(lesseeId));
        }
    }


    @PostRemove
    private void PostRemove(BaseEntity entity) {
        //从数据库中删除实体（在commit或期间flush）
    }

    @PostUpdate
    private void PostUpdate(BaseEntity entity) {
        //更新数据库中的实体（在commit或期间flush）
    }

}
