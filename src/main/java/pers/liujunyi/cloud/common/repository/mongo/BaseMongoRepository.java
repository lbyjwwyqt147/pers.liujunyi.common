package pers.liujunyi.cloud.common.repository.mongo;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/***
 * 文件名称: BaseMongoRepository.java
 * 文件描述: 基础 MongoRepository.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月22日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@NoRepositoryBean
public interface BaseMongoRepository<T, PK extends Serializable> extends MongoRepository<T, PK> {

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
     List<T> findByIdIn(List<PK> ids);

    /**
     * 查询租户下的所有数据
     * @param id  租户id
     * @return
     */
    List<T> findByLessee(PK id);

    /**
     * 查询租户下所有数据 并排序
     * @param sort
     * @return
     */
    List<T> findByLessee(PK id, Sort sort);

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
     * 根据ID 批量删除
     * @param ids
     * @return
     */
    long deleteByIdIn(List<PK> ids);

    /**
     * 根据租户ID 删除数据
     * @param id
     * @return
     */
    long deleteByLessee(PK id);
}
