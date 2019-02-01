package pers.liujunyi.common.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
     * @return
     */
     List<T> findByIdIn(List<PK> ids);
}
