package pers.liujunyi.cloud.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.SessionSynchronization;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/***
 * 文件名称: MongoUtils
 * 文件描述: 
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/4/27 10:31
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Component
public class MongoUtils {

    public void setSessionSynchronizationForTransactionBegin() {

        mongoTemplate.setSessionSynchronization(SessionSynchronization.ALWAYS);

    }

    public void setSessionSynchronizationForTransactionCompletion() {

        mongoTemplate.setSessionSynchronization(SessionSynchronization.ON_ACTUAL_TRANSACTION);

    }

    @Autowired
    private MongoTemplate mongoTemplate;

}
