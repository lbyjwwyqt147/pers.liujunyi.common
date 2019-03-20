package pers.liujunyi.cloud.common.vo.tree;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/***
 *  ztree 树 构建
 * @author Administrator
 */
public class ZtreeBuilder {

    /**
     * list 数据结构 转为 tree 数据结构
     * @param source
     * @return
     */
    public static List<ZtreeNode> buildListToTree(List<ZtreeNode> source) {
        List<ZtreeNode> roots = findRoots(source);
        List<ZtreeNode> notRoots = (List<ZtreeNode>) CollectionUtils
                .subtract(source, roots);
        for (ZtreeNode root : roots) {
            List<ZtreeNode> children = findChildren(root, notRoots);
            if (!CollectionUtils.isEmpty(children)) {
                root.setIsParent(true);
                root.setIsLeaf(false);
            }
            root.setChildren(children);
        }
        return roots;
    }

    /**
     * root 节点数据
     * @param allNodes
     * @return
     */
    private static List<ZtreeNode> findRoots(List<ZtreeNode> allNodes) {
        List<ZtreeNode> results = new ArrayList<>();
        for (ZtreeNode node : allNodes) {
            boolean isRoot = true;
            for (ZtreeNode comparedOne : allNodes) {
                if (node.getPid().longValue() == comparedOne.getId().longValue()) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                results.add(node);
                // pid = 0 表示根节点
                if (node.getPid().longValue() == 0) {
                    node.setPid(0L);
                } else {
                    node.setPid(node.getId());
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
    private static List<ZtreeNode> findChildren(ZtreeNode root, List<ZtreeNode> allNodes) {
        List<ZtreeNode> children = new ArrayList<>();
        for (ZtreeNode comparedOne : allNodes) {
            if (comparedOne.getPid().longValue() == root.getId().longValue()) {
                children.add(comparedOne);
            }
        }
        List<ZtreeNode> notChildren = (List<ZtreeNode>) CollectionUtils.subtract(allNodes, children);
        for (ZtreeNode child : children) {
            List<ZtreeNode> tmpChildren = findChildren(child, notChildren);
            if (CollectionUtils.isEmpty(tmpChildren)) {
                child.setIsLeaf(true);
            } else {
                child.setIsLeaf(false);
            }
            child.setChildren(tmpChildren);
        }
        if (CollectionUtils.isEmpty(children)) {
            return null;
        }
        return children;
    }
}
