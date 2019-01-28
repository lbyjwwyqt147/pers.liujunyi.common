package pers.liujunyi.common.service;

import pers.liujunyi.common.restful.ResultInfo;

import java.io.Serializable;
import java.util.List;

/***
 * 文件名称: BaseService.java
 * 文件描述: 基础 Service.
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年01月17日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface BaseService<T, ID extends Serializable> {

    /**
     * 根据一组ID 批量删除
     * @param ids
     * @return
     */
    Boolean  deleteAllByIdIn(List<ID> ids);

    /**
     * 根据ID单条删除
     * @param id
     * @return
     */
    Boolean  deleteById(ID id);

    /**
     * 根据一组ID获取数据
     * @param ids
     * @return
     */
    List<T> findByIdIn(List<ID> ids);

}
