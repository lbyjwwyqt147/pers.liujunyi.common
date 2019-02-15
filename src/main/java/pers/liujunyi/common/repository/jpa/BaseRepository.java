package pers.liujunyi.common.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/***
 * 文件名称: BaseRepository.java
 * 文件描述: 基础 Repository.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@NoRepositoryBean
public interface  BaseRepository<T, PK extends Serializable> extends JpaRepository<T, PK>, JpaSpecificationExecutor<T> {

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
     List<T> findByIdIn(List<PK> ids);

    /**
     * 根据ID 批量删除
     * @param ids
     * @return
     */
    long deleteByIdIn(List<PK> ids);
}