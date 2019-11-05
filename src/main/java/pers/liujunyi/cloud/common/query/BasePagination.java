package pers.liujunyi.cloud.common.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础页面信息
 * @author ljy
 */
@Data
public class BasePagination implements Serializable {
    private static final long serialVersionUID = 8764751313602049071L;
    /** 当前页码 */
    private Integer pageNumber = 1;
    /** 每页显示记录条数 */
    private Integer pageSize = 10;
}
