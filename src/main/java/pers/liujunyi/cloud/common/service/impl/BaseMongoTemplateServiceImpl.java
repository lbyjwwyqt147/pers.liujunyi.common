package pers.liujunyi.cloud.common.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.common.service.BaseMongoTemplateService;

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

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean updateMongoData(Map<String, Object> queryParam, Map<String, Object> updateParam) {
        Query query = this.queryCondition(queryParam);
        Update update = this.updateData(updateParam);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, tClazz);
        if (updateResult.getModifiedCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateMongoDataByIds(Map<String, Map<String, Object>> sourceMap) {
        AtomicBoolean success = new AtomicBoolean(false);
        if (!CollectionUtils.isEmpty(sourceMap)) {
            Iterator<Map.Entry<String, Map<String, Object>>> entries = sourceMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = entries.next();
                String id = entry.getKey();
                Map<String, Object> source = entry.getValue();
                Query query = new Query(Criteria.where("id").is(id));
                Update update = this.updateData(source);
                UpdateResult updateResult = mongoTemplate.updateMulti(query, update, tClazz);
                if (updateResult.getModifiedCount() > 0) {
                    success.set(true);
                }
            }
        }
        return success.get();
    }

    @Override
    public Boolean updateMongoDataById(PK id, Map<String, Object> sourceMap) {
        AtomicBoolean success = new AtomicBoolean(false);
        if (!CollectionUtils.isEmpty(sourceMap)) {
            Query query = new Query(Criteria.where("id").is(id));
            Update update = this.updateData(sourceMap);
            UpdateResult updateResult = mongoTemplate.updateMulti(query, update, tClazz);
            if (updateResult.getModifiedCount() > 0) {
                success.set(true);
            }
        }
        return success.get();
    }

    @Override
    public Boolean deleteSingleMongoData(PK id) {
        DeleteResult deleteResult =  mongoTemplate.remove(new Query(Criteria.where("id").is(id)), tClazz);
        if (deleteResult.getDeletedCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteBatchMongoData(List<PK> ids) {
        DeleteResult deleteResult =  mongoTemplate.remove(new Query(Criteria.where("id").in(ids)), tClazz);
        if (deleteResult.getDeletedCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteByQueryMongoData(Map<String, Object> queryFilter) {
        Query query = this.queryCondition(queryFilter);
        DeleteResult deleteResult = mongoTemplate.remove(query, tClazz);
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
        return mongoTemplate.find(queryFilter, tClazz);

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
        return mongoTemplate.find(query, tClazz);
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
