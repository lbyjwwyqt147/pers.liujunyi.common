package pers.liujunyi.common.vo.tree;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/***
 *  ztree 树 构建
 * @author Administrator
 */
public class ZTreeBuilder {

    public static List<ZTreeNode> buildListToTree(List<ZTreeNode> dirs) {
        List<ZTreeNode> roots = findRoots(dirs);
        List<ZTreeNode> notRoots = (List<ZTreeNode>) CollectionUtils
                .subtract(dirs, roots);
        for (ZTreeNode root : roots) {
            List<ZTreeNode> children = findChildren(root, notRoots);
            if (!CollectionUtils.isEmpty(children)) {
                root.setIsParent(true);
            }
            root.setChildren(children);
        }
        return roots;
    }

    private static List<ZTreeNode> findRoots(List<ZTreeNode> allNodes) {
        List<ZTreeNode> results = new ArrayList<>();
        for (ZTreeNode node : allNodes) {
            boolean isRoot = true;
            for (ZTreeNode comparedOne : allNodes) {
                if (node.getPid().longValue() == comparedOne.getId().longValue()) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                results.add(node);
                // pid = 0 表示根节点
                if (node.getPid() == 0) {
                    node.setPid(0L);
                } else {
                    node.setPid(node.getId());
                }
            }
        }
        return results;
    }

    private static List<ZTreeNode> findChildren(ZTreeNode root, List<ZTreeNode> allNodes) {
        List<ZTreeNode> children = new ArrayList<>();
        for (ZTreeNode comparedOne : allNodes) {
            if (comparedOne.getPid().longValue() == root.getId().longValue()) {
                children.add(comparedOne);
            }
        }
        List<ZTreeNode> notChildren = (List<ZTreeNode>) CollectionUtils.subtract(allNodes, children);
        for (ZTreeNode child : children) {
            List<ZTreeNode> tmpChildren = findChildren(child, notChildren);
            if (CollectionUtils.isEmpty(tmpChildren)) {
                child.setIsLeaf(true);
            } else {
                child.setIsLeaf(true);
            }
            child.setChildren(tmpChildren);
        }
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }
        return children;
    }
}
