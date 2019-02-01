package pers.liujunyi.common.service;

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

}
