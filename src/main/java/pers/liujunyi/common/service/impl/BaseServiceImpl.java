package pers.liujunyi.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.transaction.annotation.Transactional;
import pers.liujunyi.common.repository.jpa.BaseRepository;
import pers.liujunyi.common.service.BaseService;

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
public class BaseServiceImpl<T, PK extends Serializable> implements BaseService<T, PK> {

    protected Pageable page = PageRequest.of(0, 100000);

    protected BaseRepository<T, PK> baseRepository;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BaseServiceImpl(final BaseRepository<T, PK> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    public int insertBatch(String sql, Collection<T> collection) {
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
    public T getOne(PK var1) {
        return this.baseRepository.getOne(var1);
    }

    @Override
    public boolean existsById(PK id) {
        return this.baseRepository.existsById(id);
    }

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
    public List<T> findByIdIn(List<PK> ids) {
        return this.baseRepository.findByIdIn(ids);
    }
}
