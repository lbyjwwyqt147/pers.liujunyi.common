package pers.liujunyi.cloud.common.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
public interface BaseMongoTemplateService<T, PK extends Serializable>  {


    /**
     * 根据条件修改数据
     * @param queryParam  条件
     * @param updateParam  需要修改的数据
     * @return
     */
    Boolean updateMongoData(Map<String, Object> queryParam, Map<String, Object> updateParam);


    /**
     * 单条删除 Elasticsearch 中  索引数据
     * @param id
     * @return
     */
    Boolean deleteSingleMongoData(PK id);

    /**
     * 批量删除 Elasticsearch 中  索引数据
     * @param ids  一组 id
     */
    Boolean  deleteBatchMongoData(List<PK> ids);

    /**
     * 根据条件删除 Mongo 中的数据  参数为Map<String,Object>
     * @param queryFilter 参数
     */
    Boolean deleteByQueryMongoData(Map<String, Object> queryFilter);

    /**
     * 自定义条件查询
     * @param pageable    分页参数
     * @param queryFilter 过滤条件
     * @return
     */
    List<T> findConditionFilter(Query queryFilter, Pageable pageable);

    /**
     * 自定义条件查询
     * @param criteria
     * @param sort  排序
     * @param queryFilter 过滤条件
     * @return
     */
    List<T> findConditionFilter(Criteria criteria, Map<String, Object> queryFilter, Sort sort);
}
