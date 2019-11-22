package pers.liujunyi.cloud.common.query.mongo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pers.liujunyi.cloud.common.query.jpa.annotation.AggregationType;
import pers.liujunyi.cloud.common.query.jpa.annotation.QueryCondition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/***
 *  公共查询实体类，其中在toSpecWithLogicType方法中利用反射机制，将所有的属性按照注解的规则加入到动态查询条件中
 * @author ljy
 * @param
 */
public abstract class BaseMongoQuery implements Serializable {
    private static final long serialVersionUID = -741728191209491619L;
    /** 当前页码 */
    private Integer pageNumber = 1;
    /** 每页显示记录条数 */
    private Integer pageSize = 10;

    /**
     * 聚合函数
     * @param fieldName
     * @param type
     * @return
     */
   public Aggregation aggregationBuilders(String fieldName, AggregationType type) {
        Aggregation termQueryBuilder = null;
        switch (type) {
            case count:
                termQueryBuilder = Aggregation.newAggregation(Aggregation.match(this.toCriteria()), Aggregation.group("id").count().as("count"));
                break;
            case avg:
                termQueryBuilder = Aggregation.newAggregation(Aggregation.match(this.toCriteria()), Aggregation.group("id").avg(fieldName).as("count"));
                break;
            case sum:
                termQueryBuilder = Aggregation.newAggregation(Aggregation.match(this.toCriteria()), Aggregation.group("id").sum(fieldName).as("count"));
                break;
            case min:
                termQueryBuilder = Aggregation.newAggregation(Aggregation.match(this.toCriteria()), Aggregation.group("id").min(fieldName).as("count"));
                break;
            case max:
                termQueryBuilder = Aggregation.newAggregation(Aggregation.match(this.toCriteria()), Aggregation.group("id").max(fieldName).as("count"));
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
    public Query toSpecPageable(Pageable pageable) {
        return this.toSpec(pageable);
    }


    /**
     * 聚合函数
     * @param fieldName 字段名称
     * @param type  聚合函数类型
     * @return
     */
   /* public Query toAggregationBuilders(String fieldName, AggregationType type) {
        return this.toSpec(null, null, aggregationBuilders(fieldName, type));
    }*/


    /**
     * 构建查询条件
     *
     * @param pageable  分页
     * @return SearchQuery
     */
    private Query toSpec(Pageable pageable) {
        // 条件过滤
        Query queryFilter = new Query();
        queryFilter.addCriteria(this.toCriteria() );
        if (pageable != null ) {
            queryFilter.with(pageable);
        }
        return queryFilter;

    }

    /**
     * 构建查询条件
     *
     * @return SearchQuery
     */
    private Criteria toCriteria() {
        BaseMongoQuery outerThis = this;
        Class clazz = outerThis.getClass();
        //获取查询类Query的所有字段,包括父类字段
        List<Field> fields = getAllFieldsWithRoot(clazz);
        Criteria criteria = new Criteria();
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
                        value = String.valueOf(value).trim().replace(" ","");
                    }
                }
                Pattern pattern = null;
                //通过注解上func属性,构建条件表达式
                switch (qw.func()) {
                    case equal:
                        criteria.and(column).is(value);
                        break;
                    case equals:
                        criteria.and(column).in(Arrays.asList(String.valueOf(value).split(",")));
                        break;
                    case term:
                        criteria.and(column).is(value);
                        break;
                    case terms:
                        criteria.and(column).in(Arrays.asList(String.valueOf(value).split(",")));
                        break;
                    case match:
                        criteria.and(column).is(value);
                        break;
                    case multiMatch:
                        criteria.and(column).is(value);
                        break;
                    case prefix:
                        //左匹配
                        pattern = Pattern.compile("^"+value+".*$", Pattern.CASE_INSENSITIVE);
                        criteria.and(column).regex(pattern);
                        break;
                    case wildcard:
                        //模糊匹配
                        pattern = Pattern.compile("^.*"+value+".*$", Pattern.CASE_INSENSITIVE);
                        criteria.and(column).regex(pattern);
                        break;
                    case like:
                        //模糊匹配
                        pattern = Pattern.compile("^.*"+value+".*$", Pattern.CASE_INSENSITIVE);
                        criteria.and(column).regex(pattern);
                        break;
                    case or:
                        if (StringUtils.isNotBlank(qw.orFieldValue())) {
                            criteria.orOperator(Criteria.where(column)
                                    .is(qw.orFieldValue()));
                        } else if (StringUtils.isNotBlank(qw.orLikeFieldValue())) {
                            //模糊匹配
                            pattern = Pattern.compile("^.*"+qw.orLikeFieldValue()+".*$", Pattern.CASE_INSENSITIVE);
                            criteria.orOperator(Criteria.where(column)
                                    .regex(pattern));
                        } else {
                            criteria.orOperator(Criteria.where(column)
                                    .is(value));
                        }
                        break;
                    case gt:
                        criteria.and(column).gt(value);
                        break;
                    case lt:
                        criteria.and(column).lt(value);
                        break;
                    case ge:
                        criteria.and(column).gte(value);
                        break;
                    case le:
                        criteria.and(column).lte(value);
                        break;
                    case leRange:
                        criteria.and(column).lte(field.get(rangeField[0]));
                        criteria.and(column).lte(field.get(rangeField[1]));
                        break;
                    case notEqual:
                        criteria.and(column).ne(value);
                        break;
                    case notIn:
                        criteria.and(column).nin(Arrays.asList(String.valueOf(value).split(",")));
                        break;
                    case notLike:
                        criteria.and(column).ne(value);
                        break;
                    case greaterThan:
                        criteria.and(column).gt(value);
                        break;
                    case greaterThanOrEqualTo:
                        criteria.and(column).gte(value);
                        break;
                    case lessThan:
                        criteria.and(column).lt(value);
                        break;
                    case lessThanOrEqualTo:
                        criteria.and(column).lte(value);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return criteria;

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
