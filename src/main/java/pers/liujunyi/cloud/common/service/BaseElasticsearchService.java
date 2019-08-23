package pers.liujunyi.cloud.common.service;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/***
 * 文件名称: BaseElasticsearchService.java
 * 文件描述: 基础 Elasticsearch Service.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface BaseElasticsearchService<T, PK extends Serializable> {

    /**
     * 查询所有数据
     * @return
     */
    List<T> findAll();

    /**
     * 查询租户下的所有数据
     * @param id  租户id
     * @return
     */
    List<T> findAllByLessee(PK id);

    /**
     * 查询所有数据 并排序
     * @param sort
     * @return
     */
    List<T> findAllByLessee(PK id, Sort sort);

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
     * 根据租户ID 删除数据
     * @param id
     * @return
     */
    Boolean deleteByLessee(PK id);

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
    List<T> findByIdIn(List<PK> ids);

    /**
     * 根据一组ID获取数据 并根据id 顺序排序
     * @param ids
     * @return
     */
    List<T> findByIdInOrderByIdAsc(List<PK> ids);

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
    List<T> findAllByIdIn(List<PK> ids);

    /**
     * 多索引查询
     * @param indexNames  多个索引名称
     * @param pageable    分页参数
     * @param queryFilter 过滤条件
     * @return
     */
    List<String> prepareSearch(BoolQueryBuilder queryFilter, Pageable pageable, String...indexNames);
}
