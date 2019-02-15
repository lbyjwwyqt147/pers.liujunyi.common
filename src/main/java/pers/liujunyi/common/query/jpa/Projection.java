package pers.liujunyi.common.query.jpa;


/**
 * 函数条件构造器
 * @author ljy
 */  
public class Projection {
    /** 函数作用字段  */
    private String fieldName;
    /** 函数类型  */
    private ICriterion.Projection type;

    public Projection(String fieldName, ICriterion.Projection type){
        this.fieldName = fieldName;
        this.type = type;  
    }  
  
    public String getFieldName() {
        return fieldName;
    }  
  
    public ICriterion.Projection getType() {
        return type;  
    }  
      
}  
