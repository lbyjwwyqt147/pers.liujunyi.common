package pers.liujunyi.cloud.common.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.common.service.BaseMongoTemplateService;
import pers.liujunyi.cloud.common.util.UtilConstant;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 文件名称: BaseMongoTemplateServiceImpl.java
 * 文件描述: mongoTemplate 基础 Service 实现类.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月222日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Log4j2
public class BaseMongoTemplateServiceImpl<T, PK extends Serializable> implements BaseMongoTemplateService<T, PK> {

    protected Class <T> tClazz  = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @Value("${spring.data.mongodb.where-id-field}")
    private String idField;

    @Lazy
    @Resource
    protected MongoTemplate mongoTemplate;

    @Transactional(value = UtilConstant.MONGO_DB_MANAGER, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public Boolean updateMongoData(Map<String, Object> queryParam, Map<String, Object> updateParam) {
        Query query = this.queryCondition(queryParam);
        Update update = this.updateData(updateParam);
        UpdateResult updateResult = this.mongoTemplate.updateMulti(query, update, this.tClazz, this.getDocumentAnnotation().collection());
        if (updateResult.getModifiedCount() > 0) {
            return true;
        }
        return false;
    }

    @Transactional(value = UtilConstant.MONGO_DB_MANAGER, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public Boolean updateMongoDataByIds(Map<Long, Map<String, Object>> sourceMap) {
        AtomicBoolean success = new AtomicBoolean(false);
        if (!CollectionUtils.isEmpty(sourceMap)) {
            Iterator<Map.Entry<Long, Map<String, Object>>> entries = sourceMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Long, Map<String, Object>> entry = entries.next();
                Long id = entry.getKey();
                Map<String, Object> source = entry.getValue();
                Query query = new Query(Criteria.where(idField.trim()).is(id));
                Update update = this.updateData(source);
                UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, this.tClazz,  this.getDocumentAnnotation().collection());
                if (updateResult.getModifiedCount() > 0) {
                    success.set(true);
                }
            }
        }
        return success.get();
    }

    @Transactional(value = UtilConstant.MONGO_DB_MANAGER, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public Boolean updateMongoDataById(PK id, Map<String, Object> sourceMap) {
        AtomicBoolean success = new AtomicBoolean(false);
        if (!CollectionUtils.isEmpty(sourceMap)) {
            Query query = new Query(Criteria.where(idField.trim()).is(id));
            Update update = this.updateData(sourceMap);
            UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, this.tClazz, this.getDocumentAnnotation().collection());
            if (updateResult.getModifiedCount() > 0) {
                success.set(true);
            }
        }
        return success.get();
    }

    @Transactional(value = UtilConstant.MONGO_DB_MANAGER, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public Boolean deleteSingleMongoData(PK id) {
        DeleteResult deleteResult =  this.mongoTemplate.remove(new Query(Criteria.where(idField.trim()).is(id)), this.tClazz, this.getDocumentAnnotation().collection());
        if (deleteResult.getDeletedCount() > 0) {
            return true;
        }
        return false;
    }

    @Transactional(value = UtilConstant.MONGO_DB_MANAGER, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public Boolean deleteBatchMongoData(List<PK> ids) {
        DeleteResult deleteResult =  this.mongoTemplate.remove(new Query(Criteria.where(idField.trim()).in(ids)), this.tClazz, this.getDocumentAnnotation().collection());
        if (deleteResult.getDeletedCount() > 0) {
            return true;
        }
        return false;
    }

    @Transactional(value = UtilConstant.MONGO_DB_MANAGER, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public Boolean deleteByQueryMongoData(Map<String, Object> queryFilter) {
        Query query = this.queryCondition(queryFilter);
        DeleteResult deleteResult = this.mongoTemplate.remove(query, this.tClazz, this.getDocumentAnnotation().collection());
        if (deleteResult.getDeletedCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<T> findConditionFilter(Query queryFilter, Pageable pageable) {
        if (pageable != null) {
            queryFilter.with(pageable);
        }
        return this.mongoTemplate.find(queryFilter, tClazz, this.getDocumentAnnotation().collection());

    }

    @Override
    public List<T> findConditionFilter(Criteria criteria, Map<String, Object> queryFilter, Sort sort) {
        Query query = new Query();
        Iterator<Map.Entry<String, Object>> entries = queryFilter.entrySet().iterator();
        if (criteria != null) {
            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                criteria.and(key).is(value);
            }
        } else {
            criteria = new Criteria();
            int i = 0;
            while (entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (i == 0) {
                    criteria = Criteria.where(key).is(value);
                } else {
                    criteria.and(key).is(value);
                }
                i++;
            }
        }
        query.addCriteria(criteria);
        if (sort != null) {
            query.with(sort);
        }
        return this.mongoTemplate.find(query, tClazz, this.getDocumentAnnotation().collection());
    }

    /**
     * 获取 @Document
     * @return
     */
    protected Document getDocumentAnnotation() {
        Document annotation = null;
        try {
            annotation =  this.tClazz.newInstance().getClass().getAnnotation(Document.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return annotation;
    }

    /**
     * 查询条件
     * @param queryParam
     * @return
     */
    protected Query queryCondition(Map<String, Object> queryParam) {
        Query query = new Query();
        Criteria criteria = null;
        Iterator<Map.Entry<String, Object>> entries = queryParam.entrySet().iterator();
        AtomicInteger i = new AtomicInteger(0);
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (i.get() == 0) {
                criteria = Criteria.where(key).is(value);
            } else {
                criteria.and(key).is(value);
            }
            i.addAndGet(1);
        }
        return query.addCriteria(criteria);
    }


    /**
     * 更新数据
     * @param queryParam
     * @return
     */
    protected Update updateData(Map<String, Object> queryParam) {
        Update update = new Update();
        Iterator<Map.Entry<String, Object>> entries = queryParam.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            update.set(key, value);
        }
        return update;
    }
}
