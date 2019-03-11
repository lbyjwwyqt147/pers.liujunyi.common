package pers.liujunyi.common.service.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pers.liujunyi.common.repository.elasticsearch.BaseElasticsearchRepository;
import pers.liujunyi.common.service.BaseElasticsearchService;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BaseElasticsearchServiceImpl<T, PK extends Serializable> implements BaseElasticsearchService<T, PK> {

    protected Pageable page = PageRequest.of(0, 9999999);

    protected BaseElasticsearchRepository<T, PK> baseElasticsearchRepository;

    public BaseElasticsearchServiceImpl(final BaseElasticsearchRepository<T, PK> baseElasticsearchRepository) {
        this.baseElasticsearchRepository = baseElasticsearchRepository;
    }

    @Override
    public List<T> findAll() {
        List<T> list = new LinkedList<>();
        Iterable<T> iterable = this.baseElasticsearchRepository.findAll();
        if (iterable != null) {
            list = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<T> findAll(Sort sort) {
        List<T> list = new LinkedList<>();
        Iterable<T> iterable = this.baseElasticsearchRepository.findAll(sort);
        if (iterable != null) {
            list = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public T getOne(PK var1) {
        Optional<T> object = this.baseElasticsearchRepository.findById(var1);
        if (object != null) {
            return object.get();
        }
        return null;
    }

    @Override
    public boolean existsById(PK id) {
        return this.baseElasticsearchRepository.existsById(id);
    }

    @Override
    public Boolean deleteAllByIdIn(List<PK> ids){
        long count = this.baseElasticsearchRepository.deleteByIdIn(ids);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteById(PK id) {
        this.baseElasticsearchRepository.deleteById(id);
        return true;
    }

    @Override
    public void delete(T t) {
        this.baseElasticsearchRepository.delete(t);
    }

    @Override
    public void deleteInBatch(Iterable<T> var1) {
        this.baseElasticsearchRepository.deleteAll(var1);
    }

    @Override
    public List<T> findByIdIn(List<PK> ids) {
        return this.baseElasticsearchRepository.findByIdIn(ids, page);
    }

    @Override
    public List<T> findAllByIdIn(List<PK> ids) {
        return this.baseElasticsearchRepository.findAllByIdIn(ids);
    }
}
