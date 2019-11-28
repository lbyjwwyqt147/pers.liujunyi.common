package pers.liujunyi.cloud.common.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/***
 * 文件名称: ElasticsearchBaseService.java
 * 文件描述: Elasticsearch 基础 Service.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface BaseElasticsearchTemplateService<T, PK extends Serializable>  {

    /**
     * 单条更新 Elasticsearch 里面的字段数据  参数为Map<String,Object>
     * @param id  id
     * @param sourceMap  资源   key = 需要更新的字段  value = 字段值
     * @return true 成功   false  失败
     */
    Boolean updateSingleElasticsearchData(Long id, Map<String, Object> sourceMap);

    /**
     * 批量更新 Elasticsearch 里面的字段数据  参数为Map<String,Object>
     * @param sourceMap  key = id  value = Map<String, Object> key = 需要更新的字段  value = 字段值
     */
    void updateBatchElasticsearchData(Map<String, Map<String, Object>> sourceMap);

    /**
     * 根据条件 更新 Elasticsearch 里面的字段数据  参数为Map<String,Object>
     * @param dsl esmapper 目录下 mapper文件名
     * @param executeMethod  需要执行的方法
     * @param sourceMap 参数
     * @return
     */
    Boolean updateByQueryElasticsearchData(String dsl, String executeMethod, Map<String, Object> sourceMap);

    /**
     * 保存索引数据到 Elasticsearch 中  参数为Map<String,Object>
     * @param id  索引id
     * @param sourceMap   资源   key = 字段  value = 字段值
     * @return
     */
    Boolean saveElasticsearchIndexMapData(Long id, Map<String, Object> sourceMap);

    /**
     * 保存索引数据到 Elasticsearch 中   参数为javaBean
     * @param id  索引id
     * @param t   资源   javaBean
     * @return
     */
    Boolean saveElasticsearchIndexBeanData(Long id, T t);

    /**
     * 批量保存索引数据到 Elasticsearch 中   参数为javaBean
     * @param source  参数为Map<String,Object>
     */
    void saveBatchElasticsearchIndexBeanData(Map<String, T> source);

    /**
     * 保存索引数据到 Elasticsearch 中  资源  为 json 字符串
     * @param id  索引id
     * @param sourceJson   资源  json 字符串
     * @return
     */
    Boolean saveElasticsearchIndexJsonData(Long id, String sourceJson);


    /**
     * 单条删除 Elasticsearch 中  索引数据
     * @param id 索引id
     * @return
     */
    Boolean deleteSingleElasticsearchIndex(Long id);

    /**
     * 批量删除 Elasticsearch 中  索引数据
     * @param ids  一组 索引id
     */
    void  deleteBatchElasticsearchIndex(List<Long> ids);

    /**
     * 根据条件删除 Elasticsearch 中  索引数据  参数为Map<String,Object>
     * @param dsl esmapper 目录下 mapper文件名
     * @param executeMethod  需要执行的方法
     * @param sourceMap 参数
     */
    Boolean deleteByQueryElasticsearchData(String dsl, String executeMethod, Map<String, Object> sourceMap);
}
