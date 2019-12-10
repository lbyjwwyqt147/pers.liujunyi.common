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
import pers.liujunyi.cloud.common.repository.jpa.BaseJpaRepository;
import pers.liujunyi.cloud.common.service.BaseJpaService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/***
 * 文件名称: BaseJpaServiceImpl.java
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
public class BaseJpaServiceImpl<T, PK extends Serializable> implements BaseJpaService<T, PK> {

    /** 自定义分页数据  */
    protected  Pageable pageable;
    /**  AES 密匙 */
    @Value("${spring.encrypt.secretKey}")
    protected String secretKey;

    protected BaseJpaRepository<T, PK> baseJpaRepository;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BaseJpaServiceImpl(final BaseJpaRepository<T, PK> baseJpaRepository) {
        this.baseJpaRepository = baseJpaRepository;
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
        return this.baseJpaRepository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return this.baseJpaRepository.findAll(sort);
    }

    @Override
    public T getOne(PK var1) {
        return this.baseJpaRepository.getOne(var1);
    }

    @Override
    public T findById(PK var1) {
        return this.getOne(var1);
    }

    @Override
    public boolean existsById(PK id) {
        return this.baseJpaRepository.existsById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteAllByIdIn(List<PK> ids) {
        long count = this.baseJpaRepository.deleteByIdIn(ids);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteById(PK id) {
       this.baseJpaRepository.deleteById(id);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteByIds(List<PK> ids) {
        long count = this.baseJpaRepository.deleteByIdIn(ids);
        return count > 0 ? true : false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(T t) {
        this.baseJpaRepository.delete(t);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteInBatch(Iterable<T> var1) {
        this.baseJpaRepository.deleteInBatch(var1);
    }

    @Override
    public List<T> findByIdIn(List<PK> ids) {
        return this.baseJpaRepository.findByIdIn(ids);
    }

    @Override
    public List<T> findAllByIdIn(List<PK> ids) {
        return this.baseJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public List<T> findByIdInOrderByIdAsc(List<PK> ids) {
        return this.baseJpaRepository.findByIdInOrderByIdAsc(ids);
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
