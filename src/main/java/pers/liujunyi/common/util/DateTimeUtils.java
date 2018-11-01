package pers.liujunyi.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/***
 *　DateTimeUtils
 *  时间格式化　工具类
 */
public final class DateTimeUtils {

    private static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";

    private DateTimeUtils() { }

    /**
     * 将当前时间转为字符串格式　yyyy-MM-dd HH:mm:ss
     * @return 2018-10-28 17:07:05
     */
    public static String getCurrentDateTimeAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YMDHMS);
        return LocalDateTime.now().format(formatter);
    }
}
