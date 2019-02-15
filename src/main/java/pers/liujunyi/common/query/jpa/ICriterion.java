package pers.liujunyi.common.query.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/***
 * 条件接口  
 * 用户提供条件表达式接口   
 * @author ljy
 *
 */
public interface ICriterion {
    enum Operator {
        EQ, NE, LIKE, NOTLIKE, GT, LT, GTE, LTE, AND, OR, BETWEEN, ISNULL, ISNOTNULL, ISEMPTY, ISNOTEMPTY, IS_MEMBER, IS_NOT_MEMBER
    }  
      
    enum MatchMode {
        START, END, ANYWHERE  
    }    
      
    enum Projection {
        MAX, MIN, AVG, LENGTH, SUM, COUNT  
    }

    /**
     *
     * @param root
     * @param query
     * @param builder
     * @return
     */
    Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                          CriteriaBuilder builder);

}
