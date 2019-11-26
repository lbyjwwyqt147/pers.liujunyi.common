package pers.liujunyi.cloud.common.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.annotation.Transactional;
import pers.liujunyi.cloud.common.repository.jpa.BaseRepository;
import pers.liujunyi.cloud.common.service.BaseService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/***
 * 文件名称: BaseServiceImpl.java
 * 文件描述: 基础 Service impl
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Log4j2
public class BaseServiceImpl<T, PK extends Serializable> extends BaseMongoTemplateServiceImpl<T, PK> implements BaseService<T, PK> {

    /** 自定义分页数据  */
    protected  Pageable pageable;
    /** 全部所有数据  */
    protected Pageable allPageable  = PageRequest.of(0, 9999999);
    /**  AES 密匙 */
    @Value("${spring.encrypt.secretKey}")
    protected String secretKey;

    protected BaseRepository<T, PK> baseRepository;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BaseServiceImpl(final BaseRepository<T, PK> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertBatchSql(String sql, Collection<T> collection) {
        //批量转数组
        SqlParameterSource[] beanSources  = SqlParameterSourceUtils.createBatch(collection.toArray());
        int[] result = this.namedParameterJdbcTemplate.batchUpdate(sql, beanSources);
        return result.length;
    }

    @Override
    public List<T> findAll() {
        return this.baseRepository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return this.baseRepository.findAll(sort);
    }

    @Override
    public T getOne(PK var1) {
        return this.baseRepository.getOne(var1);
    }

    @Override
    public boolean existsById(PK id) {
        return this.baseRepository.existsById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteAllByIdIn(List<PK> ids) {
        long count = this.baseRepository.deleteByIdIn(ids);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteById(PK id) {
        this.baseRepository.deleteById(id);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(T t) {
        this.baseRepository.delete(t);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteInBatch(Iterable<T> var1) {
        this.baseRepository.deleteInBatch(var1);
    }

    @Override
    public Boolean deleteByLessee(PK id) {
        this.baseRepository.deleteByLessee(id);
        return true;
    }

    @Override
    public List<T> findByIdIn(List<PK> ids) {
        return this.baseRepository.findByIdIn(ids);
    }

    @Override
    public List<T> findAllByIdIn(List<PK> ids) {
        return this.baseRepository.findAllByIdIn(ids);
    }

    @Override
    public List<T> findByIdInOrderByIdAsc(List<PK> ids) {
        return this.baseRepository.findByIdInOrderByIdAsc(ids);
    }

    /**
     * 返回分页数据
     * @param pageSize
     * @return
     */
    public Pageable getPageable(int pageSize) {
        return PageRequest.of(0, pageSize);
    }

}
