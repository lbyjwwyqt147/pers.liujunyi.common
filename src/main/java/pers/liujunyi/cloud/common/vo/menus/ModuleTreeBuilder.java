package pers.liujunyi.cloud.common.vo.menus;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/***
 *  菜单 tree 树 构建
 * @author Administrator
 */
public class ModuleTreeBuilder {

    /**
     * list 数据结构 转为 tree 数据结构
     * @param source
     * @return
     */
    public static List<ModuleVo> buildListToTree(List<ModuleVo> source) {
        List<ModuleVo> roots = findRoots(source);
        List<ModuleVo> notRoots = (List<ModuleVo>) CollectionUtils
                .subtract(source, roots);
        for (ModuleVo root : roots) {
            List<ModuleVo> children = findChildren(root, notRoots);
            root.setChildren(children);
        }
        return roots;
    }

    /**
     * root 节点数据
     * @param allNodes
     * @return
     */
    private static List<ModuleVo> findRoots(List<ModuleVo> allNodes) {
        List<ModuleVo> results = new ArrayList<>();
        for (ModuleVo node : allNodes) {
            boolean isRoot = true;
            for (ModuleVo comparedOne : allNodes) {
                if (node.getModulePid().longValue() == comparedOne.getId().longValue()) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                results.add(node);
                // pid = 0 表示根节点
                if (node.getModulePid().longValue() == 0) {
                    node.setModulePid(0L);
                } else {
                    node.setModulePid(node.getId());
                }
            }
        }
        return results;
    }

    /**
     * children 节点数据
     * @param allNodes
     * @return
     */
    private static List<ModuleVo> findChildren(ModuleVo root, List<ModuleVo> allNodes) {
        List<ModuleVo> children = new ArrayList<>();
        for (ModuleVo comparedOne : allNodes) {
            if (comparedOne.getModulePid().longValue() == root.getId().longValue()) {
                children.add(comparedOne);
            }
        }
        List<ModuleVo> notChildren = (List<ModuleVo>) CollectionUtils.subtract(allNodes, children);
        for (ModuleVo child : children) {
            List<ModuleVo> tmpChildren = findChildren(child, notChildren);
            child.setChildren(tmpChildren);
        }
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }
        return children;
    }
}
