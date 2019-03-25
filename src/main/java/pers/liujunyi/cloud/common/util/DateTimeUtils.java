package pers.liujunyi.cloud.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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


    /**
     * 当前时间基础上追加年份
     * @param year
     * @return
     */
    public static Date additionalYear(long year) {
        LocalDateTime localDateTime = LocalDateTime.now().plusYears(year);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return  date;
    }


    /**
     * 获取当前年份
     * @return
     */
    public static Integer getCurrentYear() {
        // 取当前日期：
        LocalDate today = LocalDate.now();
        return today.getDayOfYear();
    }
}
