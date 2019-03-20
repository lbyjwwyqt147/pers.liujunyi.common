package pers.liujunyi.cloud.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

    /**
     * 32 位 UUID 值
     * @return
     */
    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

}
