package pers.liujunyi.cloud.common.service;

import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/***
 * 文件名称: BaseService.java
 * 文件描述: 基础 Service.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface BaseService<T, PK extends Serializable> {

    /**
     * 批量插入
     * @param sql sql
     * @param collection  参数数据
     * @return
     */
    int insertBatchSql(String sql, Collection<T> collection);

    /**
     * 查询所有数据
     * @return
     */
    List<T> findAll();

    /**
     * 查询所有数据 并排序
     * @param sort
     * @return
     */
    List<T> findAll(Sort sort);

    /**
     * 根据主键ID 获取数据
     * @param var1
     * @return
     */
    T getOne(PK var1);

    /**
     * 根据主键ID 检查数据是否存在
     * @param id
     * @return
     */
    boolean existsById(PK id);

    /**
     * 根据一组主键ID 批量删除
     * @param ids
     * @return
     */
    Boolean  deleteAllByIdIn(List<PK> ids);

    /**
     * 根据主键ID 单条删除
     * @param id
     * @return
     */
    Boolean  deleteById(PK id);

    /**
     * 单条删除数据  （实体对象作为参数）
     * @param t
     */
    void delete(T t);

    /**
     *  批量删除 (实体对象作为参数)
     * @param var1
     */
    void deleteInBatch(Iterable<T> var1);

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
    List<T> findByIdIn(List<PK> ids);

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
    List<T> findAllByIdIn(List<PK> ids);

    /**
     * 根据一组ID获取数据 并根据id 顺序排序
     * @param ids
     * @return
     */
    List<T> findByIdInOrderByIdAsc(List<PK> ids);

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
     * 批量报错索引数据到 Elasticsearch 中   参数为javaBean
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

}
