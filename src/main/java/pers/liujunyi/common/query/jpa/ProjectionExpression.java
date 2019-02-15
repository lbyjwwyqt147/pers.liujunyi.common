package pers.liujunyi.common.query.jpa;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 条件表达式
 * @author ljy
 */
public class ProjectionExpression implements ICriterion {

    /** 条件字段名称 */
	private String fieldName;
    /** 条件值 */
    private Object value;
    /** 函数条件类型   */
    private Projection projection;
    /** 基础条件类型  */
    private Operator operator;
    /** like用匹配类型  */
    private MatchMode matchMode;

    protected ProjectionExpression(String fieldName, Operator operator) {
        this.fieldName = fieldName;
        this.operator = operator;
    }

    protected ProjectionExpression(String fieldName, Object value, Operator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
    }

    protected ProjectionExpression(String fieldName, Object value, MatchMode matchMode, Operator operator) {
        this.fieldName = fieldName;
        this.value = value;
        this.operator = operator;
        this.matchMode = matchMode;
    }

    protected ProjectionExpression(String fieldName, Object value, Projection projection, Operator operator) {    
        this.fieldName = fieldName;    
        this.value = value;    
        this.projection = projection;  
        this.operator = operator;  
    }

    protected ProjectionExpression(String fieldName, Object value, Projection projection, Operator operator, MatchMode matchMode) {    
        this.fieldName = fieldName;    
        this.value = value;    
        this.projection = projection;  
        this.operator = operator;  
        this.matchMode = matchMode;  
    }    
    
      
    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
            CriteriaBuilder builder) {    
        Path expression = null;
        //此处是表关联数据，注意仅限一层关联，如user.address，
        //查询user的address集合中，address的name为某个值
        if (fieldName.contains(".")){
            String[] names = StringUtils.split(fieldName, ".");
            //获取该属性的类型，Set？List？Map？
            expression = root.get(names[0]);
            Class clazz = expression.getJavaType();
            if (clazz.equals(Set.class)) {
                SetJoin setJoin = root.joinSet(names[0]);
                expression = setJoin.get(names[1]);
            } else if (clazz.equals(List.class)) {
                ListJoin listJoin = root.joinList(names[0]);
                expression = listJoin.get(names[1]);
            } else if (clazz.equals(Map.class)) {
                MapJoin mapJoin = root.joinMap(names[0]);
                expression = mapJoin.get(names[1]);
            } else {
                //是many to one时
                expression = expression.get(names[1]);
            }
            /*for (int i = 1; i < names.length; i++) {
                expression = expression.get(names[i]);    
            } */
        } else {
            //单表查询
            expression = root.get(fieldName);    
        }    
        switch (projection) {    
            case LENGTH :
                return getBuilder(builder, builder.length(expression), value, matchMode);
            case MAX :
                return getBuilder(builder, builder.max(expression), value, matchMode);
            case SUM :
                return getBuilder(builder, builder.sum(expression), value, matchMode);
            case MIN :
                return getBuilder(builder, builder.min(expression), value, matchMode);
            case AVG :
                return getBuilder(builder, builder.avg(expression), value, matchMode);
            case COUNT :
                return getBuilder(builder, builder.count(expression), value, matchMode);
            default:
                return null;
        }    
    }    
      
    private Predicate getBuilder(CriteriaBuilder builder, Expression expression, Object value, MatchMode matchMode){
        switch (operator) {    
            case EQ:
                return builder.equal(expression, value);
            case NE:
                return builder.notEqual(expression, value);
            case LIKE:
                switch (matchMode){
                    case START :
                        return builder.like((Expression<String>) expression, value + "%");
                    case END :
                        return builder.like((Expression<String>) expression, "%" + value);
                    case ANYWHERE :
                        return builder.like((Expression<String>) expression, "%" + value + "%");
                    default :
                        return builder.like((Expression<String>) expression, "%" + value + "%");
                }
            case NOTLIKE:
                switch (matchMode){
                    case START :
                        return builder.notLike((Expression<String>) expression, value + "%");
                    case END :
                        return builder.notLike((Expression<String>) expression, "%" + value);
                    case ANYWHERE :
                        return builder.notLike((Expression<String>) expression, "%" + value + "%");
                    default :
                        return builder.notLike((Expression<String>) expression, "%" + value + "%");
                }
            case LT:
                return builder.lessThan(expression, (Comparable) value);
            case GT:
                return builder.greaterThan(expression, (Comparable) value);
            case LTE:
                return builder.lessThanOrEqualTo(expression, (Comparable) value);
            case GTE:
                return builder.greaterThanOrEqualTo(expression, (Comparable) value);
            case ISNOTNULL:
                return builder.isNotNull(expression);
            case ISNULL:
                return builder.isNull(expression);
            case ISEMPTY:
                return builder.isEmpty(expression);
            case ISNOTEMPTY:
                return builder.isNotEmpty(expression);
            case IS_MEMBER:
                return builder.isMember(value, expression);
            case IS_NOT_MEMBER:
                return builder.isNotMember(value, expression);
            default:
                return null;
            }
    }  
      
    public String getFieldName() {    
        return fieldName;    
    }    
    public Object getValue() {    
        return value;    
    }    
    public Projection getProjection() {  
        return projection;  
    }  
    public Operator getOperator() {  
        return operator;  
    }  
    public MatchMode getMatchMode() {  
        return matchMode;  
    }  

}
