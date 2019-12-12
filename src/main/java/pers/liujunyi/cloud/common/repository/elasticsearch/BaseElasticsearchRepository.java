package pers.liujunyi.cloud.common.repository.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/***
 * 文件名称: BaseElasticsearchRepository.java
 * 文件描述: 基础 ElasticsearchRepository.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@NoRepositoryBean
public interface BaseElasticsearchRepository<T, PK extends Serializable> extends ElasticsearchRepository<T, PK> {

    /**
     * 根据一组ID获取数据
     * @param ids
     * @param pageable
     * @return
     */
     List<T> findByIdIn(List<PK> ids, Pageable pageable);


    /**
     * 根据一组ID获取数据 并根据id 顺序排序
     * @param ids
     * @return
     */
    List<T> findByIdInOrderByIdAsc(List<PK> ids, Pageable pageable);

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
     * 根据ID 批量删除
     * @param ids
     * @return
     */
    long deleteByIdIn(List<PK> ids);

}
