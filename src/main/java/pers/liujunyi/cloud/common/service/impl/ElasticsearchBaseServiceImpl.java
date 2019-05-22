package pers.liujunyi.cloud.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchHelper;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.common.service.ElasticsearchBaseService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/***
 * 文件名称: ElasticsearchBaseServiceImpl.java
 * 文件描述: Elasticsearch 基础 Service 实现类.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Log4j2
public class ElasticsearchBaseServiceImpl<T, PK extends Serializable> implements ElasticsearchBaseService<T, PK> {

    protected Class <T> tClazz  = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @Value("${es.basePath}")
    private String esmapperBasePath;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

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
            Client elasticsearchClient = this.elasticsearchTemplate.getClient();
            UpdateRequestBuilder updateRequestBuilder = elasticsearchClient.prepareUpdate(annotation.indexName(), annotation.type(), String.valueOf(id));
            updateRequestBuilder.setDoc(sourceMap);
            if (updateRequestBuilder.execute().actionGet() != null) {
                success = true;
                log.info("  更新 Elasticsearch " + msg.toString() + " 里面的字段数据 成功! ");
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(" 更新 Elasticsearch " + msg.toString() + " 里面的字段数据 全部执行成功！");
            }
        }
    }

    @Override
    public Boolean updateByQueryElasticsearchData(String dsl, String executeMethod, Map<String, Object> sourceMap) {
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil(esmapperBasePath + dsl);
        Document annotation = this.getDocumentAnnotation();
        String indexName = annotation.indexName().trim();
        String result = clientUtil.updateByQuery(indexName + "/_update_by_query?conflicts=proceed",executeMethod, sourceMap);
        JSONObject json = JSON.parseObject(result);
        if (json.getIntValue("updated") > 0) {
            return true;
        }
        return false;
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
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            Client elasticsearchClient = this.elasticsearchTemplate.getClient();
            DeleteRequestBuilder deleteRequestBuilder  = elasticsearchClient.prepareDelete(annotation.indexName(), annotation.type(), String.valueOf(id));
            if (deleteRequestBuilder.execute().actionGet() != null) {
                success = true;
                log.info("  删除 Elasticsearch " + msg.toString() + " 数据 成功! ");
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info(" 删除 Elasticsearch " + msg.toString() + " 数据 全部执行成功！");
            }
        }
    }

    @Override
    public Boolean deleteByQueryElasticsearchData(String dsl, String executeMethod, Map<String, Object> sourceMap) {
        ClientInterface clientUtil = ElasticSearchHelper.getConfigRestClientUtil(esmapperBasePath + dsl);
        Document annotation = this.getDocumentAnnotation();
        String indexName = annotation.indexName().trim();
        String result = clientUtil.updateByQuery(indexName + "/_delete_by_query?refresh",executeMethod, sourceMap);
        JSONObject json = JSON.parseObject(result);
        if (json.getIntValue("deleted") > 0) {
            return true;
        }
        return false;
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
            Client elasticsearchClient = this.elasticsearchTemplate.getClient();
            IndexRequestBuilder indexRequestBuilder  = elasticsearchClient.prepareIndex(annotation.indexName(), annotation.type(), String.valueOf(id));
            indexRequestBuilder.setSource(object);
            if (indexRequestBuilder.execute().actionGet() != null) {
                success = true;
                log.info("  添加 Elasticsearch " + msg.toString() + " 数据 成功! ");
            } else {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("  添加 Elasticsearch " + msg.toString() + " 数据 失败! ");
            }
        }
        return success;
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
}
