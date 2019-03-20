package pers.liujunyi.cloud.common.query.jpa;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**  
 * 逻辑条件表达式 用于复杂条件时使用，如但属性多对应值的OR查询等  
 *
 * @author ljy
 */    
public class LogicalExpression implements ICriterion {
    /** 逻辑表达式中包含的表达式 */
    private ICriterion[] criterion;
    /** 计算符  */
    private Operator operator;
    /** 条件字段名称 */
    private String fieldName;
    /** 条件开始值 */
    private Object startValue;
    /** 条件结束值 */
    private Object endValue;
        
    public LogicalExpression(ICriterion[] ses, Operator operator) {
        this.criterion = ses;    
        this.operator = operator;    
    }

    /**
     * between用构造方法
     * @param fieldName
     * @param startValue
     * @param endValue
     * @param operator
     */
    public LogicalExpression(String fieldName, Object startValue, Object endValue, Operator operator){
        this.fieldName = fieldName;
        this.startValue = startValue;
        this.endValue = endValue;
        this.operator = operator;  
        this.criterion = null;  
    }  
      
    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();    
        if (criterion != null) {
            int criterionSize = this.criterion.length;
            for (int i = 0; i < criterionSize; i++){
                predicates.add((this.criterion[i]).toPredicate(root, query, builder));
            }
        }
        switch (operator) {
            case OR:
                return builder.or(predicates.toArray(new Predicate[predicates.size()]));
            case AND:
                return builder.and(predicates.toArray(new Predicate[predicates.size()]));
            case BETWEEN:
                Expression expression = root.get(fieldName);
                return builder.between(expression, (Comparable) startValue, (Comparable) endValue);
            default:
                return null;
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getStartValue() {
        return startValue;
    }

    public void setStartValue(Object startValue) {
        this.startValue = startValue;
    }

    public Object getEndValue() {
        return endValue;
    }

    public void setEndValue(Object endValue) {
        this.endValue = endValue;
    }
}
