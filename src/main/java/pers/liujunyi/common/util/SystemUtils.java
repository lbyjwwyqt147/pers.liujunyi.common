package pers.liujunyi.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/***
 * 工具类
 */
public final class SystemUtils {

    private SystemUtils() {}

    /**
     * 将id字符串转　　List<Long>
     * @param  id
     * @return
     */
    public static List<Long> getIds(String id){
        List<Long> idsList = new LinkedList<>();
        if (StringUtils.isNotBlank(id)){
            String[] idsArray  = id.split(",");
            if (idsArray != null && idsArray.length > 0) {
                for (String ids : idsArray) {
                    idsList.add(Long.parseLong(ids));
                }
            }
        }
        return  idsList;
    }

}
