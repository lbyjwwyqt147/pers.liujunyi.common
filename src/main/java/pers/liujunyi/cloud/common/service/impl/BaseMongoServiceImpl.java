package pers.liujunyi.cloud.common.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import pers.liujunyi.cloud.common.repository.mongo.BaseMongoRepository;
import pers.liujunyi.cloud.common.service.BaseMongoService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/***
 * 文件名称: BaseMongoServiceImpl.java
 * 文件描述: Mongo 基础 Service 实现类.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月22日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
/**
 * @author ljy
 */
public class BaseMongoServiceImpl<T, PK extends Serializable> implements BaseMongoService<T, PK> {

    /** 自定义分页数据  */
    protected  Pageable pageable;
    /**  AES 密匙 */
    @Value("${spring.encrypt.secretKey}")
    protected String secretKey;
    @Resource
    protected MongoTemplate mongoDbTemplate;

    protected BaseMongoRepository<T, PK> baseMongoRepository;

    public BaseMongoServiceImpl(final BaseMongoRepository<T, PK> baseMongoRepository) {
        this.baseMongoRepository = baseMongoRepository;
    }

    @Override
    public List<T> findAll() {
        return this.baseMongoRepository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return this.baseMongoRepository.findAll(sort);
    }

    @Override
    public T getOne(PK var1) {
        Optional<T> object = this.baseMongoRepository.findById(var1);
        if (object != null) {
            return object.get();
        }
        return null;
    }

    @Override
    public boolean existsById(PK id) {
        return this.baseMongoRepository.existsById(id);
    }

    @Override
    public Boolean deleteAllByIdIn(List<PK> ids){
        long count = this.baseMongoRepository.deleteByIdIn(ids);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteById(PK id) {
        this.baseMongoRepository.deleteById(id);
        return true;
    }

    @Override
    public void delete(T t) {
        this.baseMongoRepository.delete(t);
    }

    @Override
    public void deleteInBatch(Iterable<T> var1) {
        this.baseMongoRepository.deleteAll(var1);
    }

    @Override
    public Boolean deleteByLessee(PK id) {
        this.baseMongoRepository.deleteByLessee(id);
        return true;
    }

    @Override
    public List<T> findByIdIn(List<PK> ids) {
        return this.baseMongoRepository.findByIdIn(ids);
    }

    @Override
    public List<T> findByIdInOrderByIdAsc(List<PK> ids) {
        return  this.baseMongoRepository.findByIdInOrderByIdAsc(ids);
    }

    @Override
    public List<T> findAllByIdIn(List<PK> ids) {
        return this.baseMongoRepository.findAllByIdIn(ids);
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
