package pers.liujunyi.common.vo.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/***
 *  ztree 叶子节点
 * @author Administrator
 */
public class ZtreeChildren implements Serializable {

    private static final long serialVersionUID = -1886219187123429716L;
    private List list = new ArrayList();

    public int getSize() {
        return list.size();
    }

    public void addChild(ZTreeNode node) {
        list.add(node);
    }

    /**
     *  拼接孩子节点的JSON字符串
     * @return
     */
    @Override
    public String toString() {
        String result = "[";
        for (Iterator it = list.iterator(); it.hasNext();) {
            result += ((ZTreeNode) it.next()).toString();
            result += ",";
        }
        result = result.substring(0, result.length() - 1);
        result += "]";
        return result;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    /**
     * 孩子节点排序
     */
    public void sortChildren() {
        // 对本层节点进行排序
        // 可根据不同的排序属性，传入不同的比较器，这里传入ID比较器
        Collections.sort(list);
        // 对每个节点的下一层节点进行排序
        for (Iterator it = list.iterator(); it.hasNext();) {
           // ((ZTreeNode) it.next()).sortChildren();
        }
    }
}
