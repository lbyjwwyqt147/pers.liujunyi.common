package pers.liujunyi.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author ljy
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseQuery implements Serializable {

    /** id */
    private Long id;
    /** 当前页码 */
    private Integer pageNumber = 1;
    /** 每页显示记录条数 */
    private Integer pageSize = 10;
    /** 排序字段 */
    private String sortField = "id";
    /** 排序字段方式  DESC  ASC */
    private String sortType = "ASC";

}
