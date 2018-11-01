package pers.liujunyi.common.paging;

/***
 * 计算用类 
 * @author ljy
 *
 */
public final class Projections {

    private Projections() {}

    /**
     * Max
     * @param col
     * @return
     */
	public static Projection Max(String col){
        return new Projection(col,  ICriterion.Projection.MAX);
    }

    /**
     * Length
     * @param col
     * @return
     */
    public static Projection Length(String col){  
        return new Projection(col, ICriterion.Projection.LENGTH);  
    }

    /**
     * Min
     * @param col
     * @return
     */
    public static Projection Min(String col){  
        return new Projection(col, ICriterion.Projection.MIN);  
    }

    /**
     * Sum
     * @param col
     * @return
     */
    public static Projection Sum(String col){  
        return new Projection(col, ICriterion.Projection.SUM);  
    }  
}
