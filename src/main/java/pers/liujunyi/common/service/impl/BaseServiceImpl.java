package pers.liujunyi.common.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.common.repository.jpa.BaseRepository;
import pers.liujunyi.common.service.BaseService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/***
 * 文件名称: BaseServiceImpl.java
 * 文件描述: 基础 Service impl
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Log4j2
public class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {

    protected Class <T> tClazz  = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    /** 自定义分页数据  */
    protected  Pageable pageable;
    /** 全部所有数据  */
    protected Pageable allPageable  = PageRequest.of(0, 9999999);

    protected BaseRepository<T, PK> baseRepository;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    public BaseServiceImpl(final BaseRepository<T, PK> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    public int insertBatchSql(String sql, Collection<T> collection) {
        //批量转数组
        SqlParameterSource[] beanSources  = SqlParameterSourceUtils.createBatch(collection.toArray());
        int[] result = this.namedParameterJdbcTemplate.batchUpdate(sql, beanSources);
        return result.length;
    }

    @Override
    public List<T> findAll() {
        return this.baseRepository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return this.baseRepository.findAll(sort);
    }

    @Override
    public T getOne(PK var1) {
        return this.baseRepository.getOne(var1);
    }

    @Override
    public boolean existsById(PK id) {
        return this.baseRepository.existsById(id);
    }

    @Override
    public Boolean deleteAllByIdIn(List<PK> ids) {
        long count = this.baseRepository.deleteByIdIn(ids);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteById(PK id) {
        this.baseRepository.deleteById(id);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(T t) {
        this.baseRepository.delete(t);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteInBatch(Iterable<T> var1) {
        this.baseRepository.deleteInBatch(var1);
    }

    @Override
    public List<T> findByIdIn(List<PK> ids) {
        return this.baseRepository.findByIdIn(ids);
    }

    @Override
    public List<T> findAllByIdIn(List<PK> ids) {
        return this.baseRepository.findAllByIdIn(ids);
    }

    @Override
    public Boolean updateSingleElasticsearchData(Long id, Map<String, Object> sourceMap) {
        boolean success = false;
        if (!CollectionUtils.isEmpty(sourceMap)) {
            Document annotation = this.getDocumentAnnotation();
            String indexName = annotation.indexName().trim();
            String type = annotation.type().trim();
            StringBuffer msg = new StringBuffer();
            msg.append("indexName:").append(indexName).append(" type:").append(type);
            log.info(" 开始 更新 Elasticsearch   " + msg.toString() +"   里面的字段数据 ........... ");
            UpdateRequestBuilder updateRequestBuilder = this.elasticsearchTemplate.getClient().prepareUpdate(annotation.indexName(), annotation.type(), String.valueOf(id));
            updateRequestBuilder.setDoc(sourceMap);
            if (updateRequestBuilder.execute().actionGet() != null) {
                success = true;
                log.info("  更新 Elasticsearch " + msg.toString() + " 里面的字段数据 成功! ");
            } else {
                log.info("  更新 Elasticsearch " + msg.toString() + " 里面的字段数据 失败! ");
            }
        }
        return success;
    }

    @Override
    public void updateBatchElasticsearchData(Map<String, Map<String, Object>> sourceMap) {
        if (!CollectionUtils.isEmpty(sourceMap)) {
            Document annotation = this.getDocumentAnnotation();
            String indexName = annotation.indexName().trim();
            String type = annotation.type().trim();
            StringBuffer msg = new StringBuffer();
            msg.append("indexName:").append(indexName).append(" type:").append(type);
            log.info(" 开始 更新 Elasticsearch   " + msg.toString() +"   里面的字段数据 ........... ");
            Client elasticsearchClient = this.elasticsearchTemplate.getClient();
            BulkRequestBuilder bulkRequest = elasticsearchClient.prepareBulk();
            Iterator<Map.Entry<String, Map<String, Object>>> entries = sourceMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Map<String, Object>> entry = entries.next();
                String id = entry.getKey();
                Map<String, Object> source = entry.getValue();
                bulkRequest.add(elasticsearchClient.prepareUpdate(annotation.indexName(), annotation.type(), id).setDoc(source));
            }
            // 批量执行
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if(bulkResponse.hasFailures()) {
                BulkItemResponse[] items = bulkResponse.getItems();
                for(BulkItemResponse item : items) {
                    log.info(" 更新 Elasticsearch " + msg.toString() + " 里面的字段失败 .. 错误信息：" + item.getFailureMessage());
                }
            } else {
                log.info(" 更新 Elasticsearch " + msg.toString() + " 里面的字段数据 全部执行成功！");
            }
        }
    }

    @Override
    public Boolean saveElasticsearchIndexMapData(Long id, Map<String, Object> sourceMap) {
        return this.saveElasticsearchIndexData(id, sourceMap);
    }

    @Override
    public Boolean saveElasticsearchIndexBeanData(Long id, T t) {
        return this.saveElasticsearchIndexData(id, t);
    }


    @Override
    public void saveBatchElasticsearchIndexBeanData(Map<String, T> source) {
        if (!CollectionUtils.isEmpty(source)) {
            Document annotation = this.getDocumentAnnotation();
            String indexName = annotation.indexName().trim();
            String type = annotation.type().trim();
            StringBuffer msg = new StringBuffer();
            msg.append("indexName:").append(indexName).append(" type:").append(type);
            log.info(" 开始 添加 Elasticsearch   " + msg.toString() +"   数据 ........... ");
            Client elasticsearchClient = this.elasticsearchTemplate.getClient();
            BulkRequestBuilder bulkRequest = elasticsearchClient.prepareBulk();
            Iterator<Map.Entry<String, T>> entries = source.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, T> entry = entries.next();
                String id = entry.getKey();
                T t = entry.getValue();
                bulkRequest.add(elasticsearchClient.prepareIndex(annotation.indexName(), annotation.type(), String.valueOf(id)).setSource(t));
            }
            // 批量执行
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if(bulkResponse.hasFailures()) {
                BulkItemResponse[] items = bulkResponse.getItems();
                for(BulkItemResponse item : items) {
                    log.info(" 添加 Elasticsearch " + msg.toString() + " 数据失败 .. 错误信息：" + item.getFailureMessage());
                }
            } else {
                log.info(" 添加 Elasticsearch " + msg.toString() + " 数据 全部执行成功！");
            }
        }
    }

    @Override
    public Boolean saveElasticsearchIndexJsonData(Long id, String sourceJson) {
        return this.saveElasticsearchIndexData(id, sourceJson);
    }

    @Override
    public Boolean deleteSingleElasticsearchIndex(Long id) {
        boolean success = false;
        if (id != null) {
            Document annotation = this.getDocumentAnnotation();
            String indexName = annotation.indexName().trim();
            String type = annotation.type().trim();
            StringBuffer msg = new StringBuffer();
            msg.append("indexName:").append(indexName).append(" type:").append(type).append(" index:").append(id);
            log.info(" 开始 删除 Elasticsearch   " + msg.toString() +"   数据 ........... ");
            DeleteRequestBuilder deleteRequestBuilder  = this.elasticsearchTemplate.getClient().prepareDelete(annotation.indexName(), annotation.type(), String.valueOf(id));
            if (deleteRequestBuilder.execute().actionGet() != null) {
                success = true;
                log.info("  删除 Elasticsearch " + msg.toString() + " 数据 成功! ");
            } else {
                log.info("  删除 Elasticsearch " + msg.toString() + " 数据 失败! ");
            }
        }
        return success;
    }

    @Override
    public void deleteBatchElasticsearchIndex(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            Document annotation = this.getDocumentAnnotation();
            String indexName = annotation.indexName().trim();
            String type = annotation.type().trim();
            StringBuffer msg = new StringBuffer();
            msg.append("indexName:").append(indexName).append(" type:").append(type).append(" index:").append(StringUtils.join(ids, ","));
            log.info(" 开始 删除 Elasticsearch   " + msg.toString() +"   数据 ........... ");
            Client elasticsearchClient = this.elasticsearchTemplate.getClient();
            BulkRequestBuilder bulkRequest = elasticsearchClient.prepareBulk();
            ids.stream().forEach(item -> {
                bulkRequest.add(elasticsearchClient.prepareDelete(annotation.indexName(), annotation.type(), String.valueOf(item)));
            });
            // 批量执行
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if(bulkResponse.hasFailures()) {
                BulkItemResponse[] items = bulkResponse.getItems();
                for(BulkItemResponse item : items) {
                    log.info(" 删除 Elasticsearch " + msg.toString() + " 数据失败 .. 错误信息：" + item.getFailureMessage());
                }
            } else {
                log.info(" 删除 Elasticsearch " + msg.toString() + " 数据 全部执行成功！");
            }
        }
    }

    /**
     * 添加  Elasticsearch 数据
     * @param id
     * @param object
     * @return
     */
    private Boolean saveElasticsearchIndexData(Long id, Object object) {
        boolean success = false;
        if (object != null) {
            Document annotation = this.getDocumentAnnotation();
            String indexName = annotation.indexName().trim();
            String type = annotation.type().trim();
            StringBuffer msg = new StringBuffer();
            msg.append("indexName:").append(indexName).append(" type:").append(type);
            log.info(" 开始 添加 Elasticsearch   " + msg.toString() +"   数据 ........... ");
            IndexRequestBuilder indexRequestBuilder  = this.elasticsearchTemplate.getClient().prepareIndex(annotation.indexName(), annotation.type(), String.valueOf(id));
            indexRequestBuilder.setSource(object);
            if (indexRequestBuilder.execute().actionGet() != null) {
                success = true;
                log.info("  添加 Elasticsearch " + msg.toString() + " 数据 成功! ");
            } else {
                log.info("  添加 Elasticsearch " + msg.toString() + " 数据 失败! ");
            }
        }
        return success;
    }

    /**
     * 返回分页数据
     * @param pageSize
     * @return
     */
    public Pageable getPageable(int pageSize) {
        return PageRequest.of(0, pageSize);
    }

    /**
     * 获取 @Document
     * @return
     */
    private Document getDocumentAnnotation() {
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
}
