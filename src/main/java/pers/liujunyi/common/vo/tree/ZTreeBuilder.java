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
            root.setChildren(findChildren(root, notRoots));
        }
        return roots;
    }

    private static List<ZTreeNode> findRoots(List<ZTreeNode> allNodes) {
        List<ZTreeNode> results = new ArrayList<>();
        for (ZTreeNode node : allNodes) {
            boolean isRoot = true;
            for (ZTreeNode comparedOne : allNodes) {
                if (node.getPid() == comparedOne.getId()) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                results.add(node);
                node.setPid(node.getId());
            }
        }
        return results;
    }

    private static List<ZTreeNode> findChildren(ZTreeNode root, List<ZTreeNode> allNodes) {
        List<ZTreeNode> children = new ArrayList<>();

        for (ZTreeNode comparedOne : allNodes) {
            if (comparedOne.getPid() == root.getId()) {
                children.add(comparedOne);
            }
        }
        List<ZTreeNode> notChildren = (List<ZTreeNode>) CollectionUtils.subtract(allNodes, children);
        for (ZTreeNode child : children) {
            List<ZTreeNode> tmpChildren = findChildren(child, notChildren);
            if (tmpChildren == null || tmpChildren.size() < 1) {
                child.setParent(true);
            } else {
                child.setParent(false);
            }
            child.setChildren(tmpChildren);
        }
        return children;
    }
}
