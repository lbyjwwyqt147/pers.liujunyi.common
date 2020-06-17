package pers.liujunyi.cloud.common.query.elasticsearch;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import pers.liujunyi.cloud.common.query.jpa.annotation.AggregationType;
import pers.liujunyi.cloud.common.query.jpa.annotation.QueryCondition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 *  公共查询实体类，其中在toSpecWithLogicType方法中利用反射机制，将所有的属性按照注解的规则加入到动态查询条件中
 * @author ljy
 * @param
 */
public abstract class BaseEsQuery implements Serializable {
    private static final long serialVersionUID = -741728191209491619L;
    /** 当前页码 */
    private Integer pageNumber = 1;
    /** 每页显示记录条数 */
    private Integer pageSize = 10;

    public AbstractAggregationBuilder aggregationBuilders(String fieldName, AggregationType type) {
        AbstractAggregationBuilder termQueryBuilder = null;
        switch (type) {
            case count:
                termQueryBuilder = AggregationBuilders.count(fieldName);
                break;
            case avg:
                termQueryBuilder = AggregationBuilders.avg(fieldName);
                break;
            case sum:
                termQueryBuilder = AggregationBuilders.sum(fieldName);
                break;
            case min:
                termQueryBuilder = AggregationBuilders.min(fieldName);
                break;
            case max:
                termQueryBuilder = AggregationBuilders.max(fieldName);
                break;
            default:
                break;
        }
        return termQueryBuilder;
    }

    /**
     * JPA分页查询类
     * @return
     */
    public Pageable toPageable() {
        return PageRequest.of(pageNumber - 1, pageSize);
    }

    /**
     * JPA分页查询类,带排序条件
     * @param sort
     * @return
     */
    public Pageable toPageable(Sort sort) {
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

    /**
     * JPA分页查询类,带排序条件
     * @param direction 排序方式
     * @param properties  排序字段
     * @return
     */
    public Pageable toPageable(Sort.Direction direction, String... properties) {
        return PageRequest.of(pageNumber - 1, pageSize, direction, properties);
    }


    /**
     * 动态查询and连接
     * @param pageable 分页
     * @return
     */
    public NativeSearchQuery toSpecPageable(Pageable pageable) {
        return this.toSpec(pageable, null,null);
    }

    /**
     * 动态查询and连接
     * @param sortBuilder 排序
     * @return
     */
    public NativeSearchQuery toSpecSortPageable(SortBuilder sortBuilder) {
        return this.toSpec(this.toPageable(), sortBuilder, null);
    }


    /**
     * 聚合函数
     * @param fieldName 字段名称
     * @param type  聚合函数类型
     * @return
     */
    public NativeSearchQuery toAggregationBuilders(String fieldName, AggregationType type) {
        return this.toSpec(null, null, aggregationBuilders(fieldName, type));
    }


    /**
     *
     * term是代表完全匹配，也就是精确查询，搜索前不会再对搜索词进行分词，所以我们的搜索词必须是文档分词集合中的一个
     *
     * TermsBuilder:构造聚合函数
     *
     * AggregationBuilders:创建聚合函数工具类
     *
     * BoolQueryBuilder:拼装连接(查询)条件
     *
     * QueryBuilders:简单的静态工厂”导入静态”使用。主要作用是查询条件(关系),如区间\精确\多值等条件
     *
     * NativeSearchQueryBuilder:将连接条件和聚合函数等组合
     *
     * SearchQuery:生成查询
     *
     * 使用QueryBuilder
     *    termQuery("key", obj) 完全匹配
     *    termsQuery("key", obj1, obj2..)   一次匹配多个值
     *    matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
     *    multiMatchQuery("text", "field1", "field2"..);  匹配多个字段, field有通配符忒行
     *    fuzzyQuery("key"，value)  模糊查询
     *    matchAllQuery();         匹配所有文件

     *    must 相当于 与 & =
     *    must not 相当于 非 ~   ！=
     *    should 相当于 或  |   or
     *    filter  过滤
     *
     * @param pageable  分页
     * @param sortBuilder  指定排序字段
     * @param aggregationBuilder 包含聚合函数查询
     * @return SearchQuery
     */
    private NativeSearchQuery toSpec(Pageable pageable, SortBuilder sortBuilder, AbstractAggregationBuilder aggregationBuilder) {
        BaseEsQuery outerThis = this;
        Class clazz = outerThis.getClass();
        //获取查询类Query的所有字段,包括父类字段
        List<Field> fields = getAllFieldsWithRoot(clazz);
        // 条件过滤
        BoolQueryBuilder queryFilter = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryOr = QueryBuilders.boolQuery();
        for (Field field : fields) {
            //获取字段上的@QueryCondition 注解
            QueryCondition qw = field.getAnnotation(QueryCondition.class);
            if (qw == null) {
                continue;
            }
            // 获取字段名
            String column = qw.column().trim();
            //如果主注解上colume为默认值"",则以field为准
            if (column.equals("")) {
                column = field.getName();
            }
            String[] rangeField = qw.rangeField();
            field.setAccessible(true);
            try {
                // nullable
                Object value = field.get(outerThis);
                //如果值为null,注解未标注nullable,跳过
                if (value == null && !qw.nullable()) {
                    continue;
                }
                if (String.class.isAssignableFrom(value.getClass()) ) {
                    String val = (String) value;
                    if (val.trim().equals("")) {
                        continue;
                    }
                }
                // can be empty
                if (value != null && String.class.isAssignableFrom(value.getClass())) {
                    String s = (String) value;
                    //如果值为"",且注解未标注emptyable,跳过
                    if (s.trim().equals("") && !qw.emptyable()) {
                        continue;
                    }
                    if (value instanceof String) {
                        //  QueryParser.escape(String.valueOf(value)).trim().replace(" ","") 对特殊字符的处理, 如果对特殊字符进行处理  则查询条件携带了特殊字符 ES 无法匹配数据
                        value = QueryParser.escape(String.valueOf(value)).trim().replace(" ","");
                    }
                }
                //通过注解上func属性,构建条件表达式
                switch (qw.func()) {
                    case equal:
                        queryFilter.must(QueryBuilders.termQuery(column, value));
                        break;
                    case equals:
                        queryFilter.must(QueryBuilders.termsQuery(column, Arrays.asList(String.valueOf(value).split(","))));
                        break;
                    case term:
                        queryFilter.must(QueryBuilders.termQuery(column, value));
                        break;
                    case terms:
                        queryFilter.must(QueryBuilders.termsQuery(column, Arrays.asList(String.valueOf(value).split(","))));
                        break;
                    case match:
                        queryFilter.must(QueryBuilders.matchQuery(column, value));
                        break;
                    case multiMatch:
                        queryFilter.must(QueryBuilders.multiMatchQuery(column, String.valueOf(value).split(",")));
                        break;
                    case prefix:
                        queryFilter.must(QueryBuilders.prefixQuery(column, String.valueOf(value)));
                        break;
                    case wildcard:
                        queryFilter.must(QueryBuilders.wildcardQuery(column, String.valueOf(value)));
                        break;
                    case like:
                        queryFilter.must(QueryBuilders.matchPhraseQuery(column, value));
                        break;
                    case or:
                        if (StringUtils.isNotBlank(qw.orFieldValue())) {
                            boolQueryOr.should(QueryBuilders.termQuery(column, field.get(qw.orFieldValue())));
                        } else if (StringUtils.isNotBlank(qw.orLikeFieldValue())) {
                            boolQueryOr.should(QueryBuilders.fuzzyQuery(column, field.get(qw.orFieldValue())));
                        } else {
                            boolQueryOr.should(QueryBuilders.termQuery(column, value));
                        }
                        queryFilter.must(boolQueryOr);
                        break;
                    case gt:
                        queryFilter.must(new RangeQueryBuilder(column).gt(value));
                        break;
                    case lt:
                        queryFilter.must(new RangeQueryBuilder(column).lt(value));
                        break;
                    case ge:
                        queryFilter.must(new RangeQueryBuilder(column).gte(value));
                        break;
                    case le:
                        queryFilter.must(new RangeQueryBuilder(column).lte(value));
                        break;
                    case leRange:
                        queryFilter.must(new RangeQueryBuilder(column).from(field.get(rangeField[0])).to(field.get(rangeField[1])));
                        break;
                    case notEqual:
                        queryFilter.mustNot(QueryBuilders.termQuery(column, value));
                        break;
                    case notIn:
                        queryFilter.mustNot(QueryBuilders.termQuery(column, Arrays.asList(String.valueOf(value).split(","))));
                        break;
                    case notLike:
                        queryFilter.mustNot(QueryBuilders.fuzzyQuery(column, value));
                        break;
                    case greaterThan:
                        queryFilter.must(new RangeQueryBuilder(column).gt(value));
                        break;
                    case greaterThanOrEqualTo:
                        queryFilter.must(new RangeQueryBuilder(column).gte(value));
                        break;
                    case lessThan:
                        queryFilter.must(new RangeQueryBuilder(column).lt(value));
                        break;
                    case lessThanOrEqualTo:
                        queryFilter.must(new RangeQueryBuilder(column).lte(value));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        NativeSearchQuery searchQuery  = null;
        if (pageable == null && aggregationBuilder == null && sortBuilder == null) {
            searchQuery =  new NativeSearchQueryBuilder().withQuery(queryFilter).build();
        } else if (pageable != null && sortBuilder == null){
            searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
                    .withQuery(queryFilter).build();
        } else if (pageable != null && sortBuilder != null){
            searchQuery = new NativeSearchQueryBuilder().withPageable(pageable)
                    .withQuery(queryFilter).withSort(sortBuilder).build();
        } else if (aggregationBuilder != null ) {
            searchQuery =  new NativeSearchQueryBuilder().withQuery(queryFilter).addAggregation(aggregationBuilder).build();
        }
        return searchQuery;

    }

    /**
     * 获取类clazz的所有Field，包括其父类的Field
     * @param clazz
     * @return
     */
    private List<Field> getAllFieldsWithRoot(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        //获取本类所有字段
        Field[] dFields = clazz.getDeclaredFields();
        if (null != dFields && dFields.length > 0) {
            fieldList.addAll(Arrays.asList(dFields));
        }
        // 若父类是Object，则直接返回当前Field列表
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == Object.class) {
            return Arrays.asList(dFields);
        }
        // 递归查询父类的field列表
        List<Field> superFields = getAllFieldsWithRoot(superClass);
        if (null != superFields && !superFields.isEmpty()) {
            // !fieldList.contains(field) 不重复字段
            superFields.stream().
                    filter(field -> !fieldList.contains(field)).
                    forEach(field -> fieldList.add(field));
        }
        return fieldList;
    }


    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
