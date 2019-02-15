package pers.liujunyi.common.query.jpa;

/***
 * 计算用类 
 * @author ljy
 *
 */
public final class Projections {

    private Projections() {}

    /**
     *  取最大值
     * @param fieldName 字段
     * @return
     */
	public static Projection max(String fieldName){
        return new Projection(fieldName,  ICriterion.Projection.MAX);
    }

    /**
     * 取长度值
     * @param fieldName
     * @return
     */
    public static Projection length(String fieldName){
        return new Projection(fieldName, ICriterion.Projection.LENGTH);
    }

    /**
     * 取最小值
     * @param fieldName
     * @return
     */
    public static Projection min(String fieldName){
        return new Projection(fieldName, ICriterion.Projection.MIN);
    }

    /**
     * 求和
     * @param fieldName
     * @return
     */
    public static Projection sum(String fieldName){
        return new Projection(fieldName, ICriterion.Projection.SUM);
    }

    /**
     *  计算数量
     * @param fieldName
     * @return
     */
    public static Projection count(String fieldName){
        return new Projection(fieldName, ICriterion.Projection.COUNT);
    }

}
