package pers.liujunyi.cloud.common.query.jpa.annotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 *  公共查询实体类，其中在toSpecWithLogicType方法中利用反射机制，将所有的属性按照注解的规则加入到动态查询条件中
 * @author ljy
 * @param <T>
 */
public abstract class BaseQuery<T>  implements Serializable {
    private static final long serialVersionUID = -5350796097312586226L;
    /** 当前页码 */
    private Integer pageNumber = 1;
    /** 每页显示记录条数 */
    private Integer pageSize = 10;

    /**
     * 将查询转换成Specification
     * @return
     */
    public abstract Specification<T> toSpec();

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
     * 动态查询and连接
     * @return
     */
    protected Specification<T> toSpecWithAnd() {
        return this.toSpecWithLogicType("and");
    }

    /**
     * 动态查询or连接
     * @return
     */
    protected Specification<T> toSpecWithOr() {
        return this.toSpecWithLogicType("or");
    }

    /**
     * logicType or/and
     * @param logicType
     * @return
     */
    private Specification<T> toSpecWithLogicType(String logicType) {
        BaseQuery outerThis = this;
        return (root, criteriaQuery, cb) -> {
            Class clazz = outerThis.getClass();
            //获取查询类Query的所有字段,包括父类字段
            List<Field> fields = getAllFieldsWithRoot(clazz);
            List<Predicate> predicates = new ArrayList<>(fields.size());
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
                field.setAccessible(true);
                try {
                    // nullable
                    Object value = field.get(outerThis);
                    //如果值为null,注解未标注nullable,跳过
                    if (value == null && !qw.nullable()) {
                        continue;
                    }
                    // can be empty
                    if (value != null && String.class.isAssignableFrom(value.getClass())) {
                        String s = (String) value;
                        //如果值为"",且注解未标注emptyable,跳过
                        if (s.trim().equals("") && !qw.emptyable()) {
                            continue;
                        }
                    }
                    //通过注解上func属性,构建路径表达式
                    Path path = root.get(column);
                    switch (qw.func()) {
                        case equal:
                            predicates.add(cb.equal(path, value));
                            break;
                        case term:
                            predicates.add(cb.equal(path, value));
                            break;
                        case equals:
                            predicates.add(cb.equal(path, Arrays.asList(String.valueOf(value).split(","))));
                            break;
                        case like:
                            predicates.add(cb.like(path, "%" + value + "%"));
                            break;
                        case gt:
                            predicates.add(cb.gt(path, (Number) value));
                            break;
                        case lt:
                            predicates.add(cb.lt(path, (Number) value));
                            break;
                        case ge:
                            predicates.add(cb.ge(path, (Number) value));
                            break;
                        case le:
                            predicates.add(cb.le(path, (Number) value));
                            break;
                        case or:
                            Predicate[] predicatesPermissionArr = new Predicate[1];
                            if (StringUtils.isNotBlank(qw.orFieldValue())) {
                                predicatesPermissionArr[0] =  cb.equal(path, field.get(qw.orFieldValue()));
                            } else if (StringUtils.isNotBlank(qw.orLikeFieldValue())) {
                                predicatesPermissionArr[0] =  cb.like(path, "%" + field.get(qw.orFieldValue()) +  "%");
                            } else {
                                predicatesPermissionArr[0] =  cb.equal(path, value);
                            }
                            predicates.add(cb.or(predicatesPermissionArr));
                            break;
                        case notEqual:
                            predicates.add(cb.notEqual(path, value));
                        case notIn:
                            predicates.add(cb.notEqual(path, Arrays.asList(String.valueOf(value).split(","))));
                            break;
                        case notLike:
                            predicates.add(cb.notLike(path, "%" + value + "%"));
                            break;
                        case greaterThan:
                            predicates.add(cb.greaterThan(path, (Comparable) value));
                            break;
                        case greaterThanOrEqualTo:
                            predicates.add(cb.greaterThanOrEqualTo(path, (Comparable) value));
                            break;
                        case lessThan:
                            predicates.add(cb.lessThan(path, (Comparable) value));
                            break;
                        case lessThanOrEqualTo:
                            predicates.add(cb.lessThanOrEqualTo(path, (Comparable) value));
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            Predicate p = null;
            if (logicType == null || logicType.trim().equals("") || logicType.trim().equals("and")) {
                //and连接
                p = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            } else if (logicType.trim().equals("or")) {
                //or连接
                p = cb.or(predicates.toArray(new Predicate[predicates.size()]));
            }
            return p;
        };
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
