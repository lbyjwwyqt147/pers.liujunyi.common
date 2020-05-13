package pers.liujunyi.cloud.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    /**
     * 不需要保护的资源
     * @param excludeAntMatchers
     * @return
     */
    public static String[] antMatchers(List<String> excludeAntMatchers) {
        List<String> collect =  excludeAntMatchers.stream().map(String::trim).collect(Collectors.toList());
        return collect.toArray(new String[collect.size()]);
    }

    /**
     * 判断是否是base64编码的字符串
     * @param str
     * @return
     */
    public static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }

}
