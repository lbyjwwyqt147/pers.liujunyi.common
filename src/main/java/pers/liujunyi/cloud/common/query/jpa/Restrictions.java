package pers.liujunyi.cloud.common.query.jpa;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;


/***
 * 条件构造器  
 * 用于创建条件表达式 
 * @author ljy
 *
 */
public class Restrictions {  
  
    /** 
     * 字段 不为空字符串 条件
     * @param fieldName : 匹配字段 
     * @return 
     */  
    public static ProjectionExpression isNotEmpty(String fieldName) {
        return new ProjectionExpression(fieldName, ICriterion.Operator.ISNOTEMPTY);
    }  
  
    /** 
     * 字段 为空字符串 条件
     * @param fieldName : 匹配字段 
     * @return 
     */  
    public static ProjectionExpression isEmpty(String fieldName) {
        return new ProjectionExpression(fieldName, ICriterion.Operator.ISEMPTY);
    }  
  
    /** 
     * 字段 为null 条件
     * @param fieldName : 匹配字段 
     * @return 
     */  
    public static ProjectionExpression isNull(String fieldName) {
        return new ProjectionExpression(fieldName, ICriterion.Operator.ISNULL);
    }  
  
    /** 
     * 字段 不为空null 条件
     * @param fieldName : 匹配字段 
     * @return 
     */  
    public static ProjectionExpression isNotNull(String fieldName) {
        return new ProjectionExpression(fieldName, ICriterion.Operator.ISNOTNULL);
    }  
  
    /** 
     *  字段 等于 条件
     * @param fieldName : 匹配字段  
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression equals(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.EQ);
    }    
      
    /**  
     *  字段 等于 条件 （带函数 sum,count,avg 条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...) 
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression equals(Projection projection, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.EQ);
    }    
        
    /**  
     * 字段 不等于 条件
     * @param fieldName : 匹配字段  
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression notEquals(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.NE);
    }    
      
    /**  
     *  字段 不等于 条件（带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...) 
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression notEquals(Projection projection, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.NE);
    }    
    
    /**  
     * 字段 模糊匹配 条件
     * @param fieldName : 匹配字段  
     * @param value : 匹配值  
     * @return  
     */    
    public static ProjectionExpression like(String fieldName, String value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.LIKE);
    }    
      
    /**  
     * 字段 模糊匹配 条件 （带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...) 
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression like(Projection projection, String value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.LIKE);
    }    
    
    /**  
     *  字段 自定义模式模糊匹配
     * @param fieldName : 匹配字段 
     * @param value : 匹配值 
     * @param matchMode : 匹配方式(MatchMode.START\END\ANYWHERE) 
     * @return  
     */    
    public static ProjectionExpression like(String fieldName, String value,
            ICriterion.MatchMode matchMode) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, matchMode, ICriterion.Operator.LIKE);
    }    
      
    /**  
     *  自定义模式模糊匹配（带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...) 
     * @param value  : 匹配值 
     * @param matchMode : 匹配方式(MatchMode.START\END\ANYWHERE) 
     * @return  
     */    
    public static ProjectionExpression like(Projection projection, String value,    
            ICriterion.MatchMode matchMode) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.LIKE, matchMode);
    }

    /**
     *  字段 自定义模式模糊匹配
     * @param fieldName : 匹配字段
     * @param value : 匹配值
     * @param matchMode : 匹配方式(MatchMode.START\END\ANYWHERE)
     * @return
     */
    public static ProjectionExpression notLike(String fieldName, String value,
                                            ICriterion.MatchMode matchMode) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, matchMode, ICriterion.Operator.NOTLIKE);
    }

    /**
     *  自定义模式模糊匹配（带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...)
     * @param value  : 匹配值
     * @param matchMode : 匹配方式(MatchMode.START\END\ANYWHERE)
     * @return
     */
    public static ProjectionExpression notLike(Projection projection, String value,
                                            ICriterion.MatchMode matchMode) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.NOTLIKE, matchMode);
    }

    /**  
     * 字段 大于 条件
     * @param fieldName : 匹配字段  
     * @param value : 匹配值  
     * @return  
     */    
    public static ProjectionExpression greaterThan(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.GT);
    }    
      
    /**  
     * 字段 大于 条件（（带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...)  
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression greaterThan(Projection projection, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.GT);
    }    
    
    /**  
     * 字段 小于 条件
     * @param fieldName : 匹配字段  
     * @param value : 匹配值  
     * @return  
     */    
    public static ProjectionExpression lessThan(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.LT);
    }    
      
    /**  
     * 字段 小于 条件（（带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...)   
     * @param value : 匹配值  
     * @return  
     */    
    public static ProjectionExpression lessThan(Projection projection, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.LT);
    }    
    
    /**  
     * 字段 小于等于
     * @param fieldName : 匹配字段  
     * @param value : 匹配值  
     * @return  
     */    
    public static ProjectionExpression lessTlhanEqual(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.LTE);
    }    
      
    /**  
     *  字段 小于等于 （带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...)  
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression lessTlhanEqual(Projection projection, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.LTE);
    }    
    
    /**  
     * 字段 大于等于 条件
     * @param fieldName : 匹配字段 
     * @param value : 匹配值 
     * @return  
     */    
    public static ProjectionExpression greaterThanEqual(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.GTE);
    }    
      
    /**  
     * 字段 大于等于  条件  （带函数 sum,count,avg  条件查询）
     * @param projection : Projection查询条件(Projections.MAX\SUM\AVG...)  
     * @param value : 匹配值 
     * @return  
     */   
    public static ProjectionExpression greaterThanEqual(Projection projection, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(projection.getFieldName(), value, projection.getType(), ICriterion.Operator.GTE);
    }

    /**
     * 字段 并且 and 条件
     */
    public static LogicalExpression and(ICriterion... criterions) {
        return new LogicalExpression(criterions, ICriterion.Operator.AND);
    }

      
    /**  
     * 字段 or 或者 条件
     * @param criterions  
     * @return  
     */    
    public static LogicalExpression or(ICriterion... criterions){
        return new LogicalExpression(criterions, ICriterion.Operator.OR);
    }    
      
    /**
     *  字段 or 或者 条件
     * @param fieldName
     * @param value
     * @return
     */
    public static LogicalExpression orIn(Collection fieldName, Object value) {
    	if (!isBlank(value)){
            ProjectionExpression[] ses = new ProjectionExpression[fieldName.size()];
            int i = 0;
            for (Object obj : fieldName){
                ses[i] = new ProjectionExpression(obj.toString(), value, ICriterion.Operator.EQ);
                i++;  
            }  
            return new LogicalExpression(ses, ICriterion.Operator.OR);
    	} else {
            return null;  
    	}
    
    }
    
      
    /**  
     * 字段  Between 区间 条件
     * @param column : 匹配字段 
     * @param1 val1 左区间  
     * @param2 val2 右区间 
     * @return  
     */   
    public static LogicalExpression between(String column, Object val1, Object val2){  
        return new LogicalExpression(column, val1, val2, ICriterion.Operator.BETWEEN);
    }  

      
    /**  
     * 字段  in 包含于   条件
     * @param fieldName : 匹配字段 
     * @param value : 匹配值 
     * @return  
     */    
    public static LogicalExpression in(String fieldName, Collection value) {
        ProjectionExpression[] ses = new ProjectionExpression[value.size()];
        int i = 0;
        for (Object obj : value){
            ses[i] = new ProjectionExpression(fieldName, obj, ICriterion.Operator.EQ);
            i++;    
        }    
        return new LogicalExpression(ses, ICriterion.Operator.OR);
    }  
  
    /** 
     * 字段  not in 不包含于  条件
     * @param fieldName : 匹配字段 
     * @param value : 匹配值 
     * @return 
     */  
    public static LogicalExpression notIn(String fieldName, Collection value) {
        ProjectionExpression[] ses = new ProjectionExpression[value.size()];
        int i = 0;
        for (Object obj : value){
            ses[i] = new ProjectionExpression(fieldName, obj, ICriterion.Operator.NE);
            i++;  
        }  
        return new LogicalExpression(ses, ICriterion.Operator.AND);
    }

    /**
     * 集合包含某个元素
     */
    public static ProjectionExpression hasMember(String fieldName, Object value) {
        if (isBlank(value)) {
            return null;
        }
        return new ProjectionExpression(fieldName, value, ICriterion.Operator.IS_MEMBER);
    }

    /**
     * 集合包含某几个元素，譬如可以查询User类中Set<String> set包含"ABC","bcd"的User集合，
     * 或者查询User中Set<Address>的Address的name为"北京"的所有User集合
     * 集合可以为基本类型或者JavaBean，可以是one to many或者是@ElementCollection
     * @param fieldName
     * 列名
     * @param value
     * 集合
     * @return
     * expresssion
     */
    public static LogicalExpression hasMembers(String fieldName, Object... value) {
        ProjectionExpression[] ses = new ProjectionExpression[value.length];
        int i = 0;
        //集合中对象是基本类型，如Set<Long>，List<String>
        ICriterion.Operator operator = ICriterion.Operator.IS_MEMBER;
        //集合中对象是JavaBean
        if (fieldName.contains(".")) {
            operator = ICriterion.Operator.EQ;
        }
        for (Object obj : value) {
            ses[i] = new ProjectionExpression(fieldName, obj, operator);
            i++;
        }
        return new LogicalExpression(ses, ICriterion.Operator.OR);
    }


    /**
     * 验证 字段值是否为null
     * @param value
     * @return == null 返回true  反之返回false
     */
    private static Boolean isBlank(Object value) {
        if (value == null) {
            return true;
        } else if (value != null && value instanceof String) {
            if (StringUtils.isBlank(String.valueOf(value))) {
                return true;
            }
        }
        return false;
    }
  
}
