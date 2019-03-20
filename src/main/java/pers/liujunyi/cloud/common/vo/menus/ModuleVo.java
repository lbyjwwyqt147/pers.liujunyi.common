package pers.liujunyi.cloud.common.vo.menus;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单vo
 * @author ljy
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ModuleVo implements Serializable {

    private static final long serialVersionUID = -6821793952585755897L;

    /** 菜单id  */
    private Long id;
    /** 菜单代码 */
    private String moduleCode;
    /** 菜单名称 */
    private String moduleName;
    /** 菜单类型 1：菜单 2：页面  3：按钮  */
    private Byte moduleType;
    /** 父级ID  */
    private Long modulePid;
    /** 图标 */
    private String menuIcon;
    /** 打开url */
    private String menuOpenUrl;
    /** 授权标记  */
    private String authorizedSigns;
    /** 状态  0:正常  1：禁用 */
    private Byte status;
    /** 功能按钮组  1:保存  2:删除  3：修改状态值  4：查询  5：导入  6：导出    10：同步  */
    private List<String> functionButtonGroup;

    /** 孩子节点  */
    private List<ModuleVo> children;
}
