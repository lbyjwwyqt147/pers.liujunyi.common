package pers.liujunyi.cloud.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/***
 * 工具类
 * @author ljy
 */
public final class SystemUtils {

    private SystemUtils() {}

    /**
     * 将id字符串转　　List<Long>
     * @param  id
     * @return
     */
    public static List<Long> idToLong(String id){
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
     * 将字符串转　　List<String>
     * @param  data
     * @return
     */
    public static List<String> stringToList(String data){
        if (StringUtils.isNotBlank(data)){
            return Arrays.asList(data.split(","));
        }
        return null;
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

    /**
     * 不需要保护的资源
     * @param excludeAntMatchers
     * @return
     */
    public static String[] antMatchers(String excludeAntMatchers) {
        String[] tempAntMatchers = excludeAntMatchers.trim().split(",");
        int length =  tempAntMatchers.length;
        String[] matchers = new String[length];
        for (int i = 0; i < length; i++) {
            matchers[i] = tempAntMatchers[i].trim();
        }
        return matchers;
    }
}
