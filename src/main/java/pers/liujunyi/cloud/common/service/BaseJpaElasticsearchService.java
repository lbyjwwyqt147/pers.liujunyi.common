package pers.liujunyi.cloud.common.service;

import org.springframework.data.domain.Sort;
import pers.liujunyi.cloud.common.repository.elasticsearch.BaseElasticsearchRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/***
 * 文件名称: BaseJpaElasticsearchService.java
 * 文件描述: 基础 Service.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface BaseJpaElasticsearchService<T, PK extends Serializable> extends BaseElasticsearchTemplateService<T, PK> {

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
     * 根据ID获取数据
     * @param var1
     * @return
     */
    T findById(PK var1);

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
     * 批量删除
     * @param ids
     * @return
     */
    Boolean  deleteByIds(List<PK> ids);

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
     * 排除指定ID数据
     * @param ids
     * @return
     */
    List<T> findByIdNotIn(List<PK> ids);

    /**
     * 根据一组ID获取数据 并根据id 顺序排序
     * @param ids
     * @return
     */
    List<T> findByIdInOrderByIdAsc(List<PK> ids);

    /**
     * 同步数据到Elasticsearch中
     */
    void syncDataElasticsearch(BaseElasticsearchRepository elasticsearchRepository);

    /**
     * 同步数据到Mysql中
     */
    void syncDataMysql();
}
