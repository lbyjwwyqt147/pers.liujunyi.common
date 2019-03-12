package pers.liujunyi.common.service.impl;

import com.alibaba.fastjson.JSONArray;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import pers.liujunyi.common.repository.elasticsearch.BaseElasticsearchRepository;
import pers.liujunyi.common.service.BaseElasticsearchService;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BaseElasticsearchServiceImpl<T, PK extends Serializable> implements BaseElasticsearchService<T, PK> {

    /** 自定义分页数据  */
    protected  Pageable pageable;
    /** 全部所有数据  */
    protected Pageable allPageable  = PageRequest.of(0, 9999999);
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

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
        return this.baseElasticsearchRepository.findByIdIn(ids, this.getPageable(ids.size()));
    }

    @Override
    public List<T> findAllByIdIn(List<PK> ids) {
        return this.baseElasticsearchRepository.findAllByIdIn(ids);
    }

    @Override
    public List<String> prepareSearch(BoolQueryBuilder queryFilter, Pageable pageable, String...indexNames) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(indexNames)
                .withQuery(queryFilter)
                .withPageable(pageable)
                .build();
        List<String> queryList = elasticsearchTemplate.query(searchQuery, response -> {
            SearchHits hits = response.getHits();
            List<String> list = new CopyOnWriteArrayList<>();
            Arrays.stream(hits.getHits()).forEach(h -> {
                String source = h.getSourceAsString();
                list.add(source);
            });
            return list;
        });
        return queryList;
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
