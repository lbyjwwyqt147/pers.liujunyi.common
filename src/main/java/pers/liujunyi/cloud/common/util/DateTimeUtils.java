package pers.liujunyi.cloud.common.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/***
 *　DateTimeUtils
 *  时间格式化　工具类
 */
public final class DateTimeUtils {

    private static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    private static final String YMD = "yyyy-MM-dd";

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
     * 获取当前年月日
     * @return 2018-10-28
     */
    public static  Date getCurrentDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YMD);
        LocalDate localDate = LocalDate.now();
        try {
            return sDateFormat.parse(localDate.format(formatter));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取当前年月日
     * @return 2018-10-28
     */
    public static  String getCurrentDateAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YMD);
        LocalDate localDate = LocalDate.now();
        return localDate.format(formatter);
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

    /**
     * 获取过去几年的年份
     * @param number  过去几年值
     * @return
     */
    public static Integer getCurrentBeforeYear(int number) {
        // 取当前日期：
        LocalDate today = LocalDate.now();
        return today.getDayOfYear() - number;
    }

    /**
     * 计算2个日期之间相差的  相差多少年月日
     * 比如：2011-02-02 到  2017-03-02 相差 6年，1个月，0天
     * @param fromDate
     * @param toDate
     * @return
     */
    public static DayCompare dayComparePrecise(Date fromDate,Date toDate){
        Calendar from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);

        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        int year = toYear  -  fromYear;
        int month = toMonth  - fromMonth;
        int day = toDay  - fromDay;
        return DayCompare.builder().day(day).month(month).year(year).build();
    }

    /**
     * 计算2个日期之间相差的  以年、月、日为单位，各自计算结果是多少
     * 比如：2011-02-02 到  2017-03-02
     *                                以年为单位相差为：6年
     *                                以月为单位相差为：73个月
     *                                以日为单位相差为：2220天
     * @param fromDate
     * @param toDate
     * @return
     */
    public static DayCompare dayCompare(Date fromDate,Date toDate){
        Calendar  from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);
        //只要年月
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);

        int year = toYear  -  fromYear;
        int month = toYear *  12  + toMonth  -  (fromYear  *  12  +  fromMonth);
        int day = (int) ((to.getTimeInMillis()  -  from.getTimeInMillis())  /  (24  *  3600  *  1000));
        return DayCompare.builder().day(day).month(month).year(year).build();
    }

    /**
     * 计算2个日期相差多少年
     * 列：2011-02-02  ~  2017-03-02 大约相差 6.1 年
     * @param fromDate
     * @param toDate
     * @return
     */
    public static String yearCompare(Date fromDate,Date toDate){
        DayCompare result = dayComparePrecise(fromDate, toDate);
        double month = result.getMonth();
        double year = result.getYear();
        //返回2位小数，并且四舍五入
        DecimalFormat df = new DecimalFormat("######0.0");
        return df.format(year + month / 12);
    }

    /**
     * 将日期类型格式化为字符串
     *
     * @param toDate
     * @return 2018-10-28 17:07:05
     */
    public static String dateFormatYmdhms(Date toDate){
        SimpleDateFormat format = new SimpleDateFormat(YMDHMS);
        return format.format(toDate);
    }


    /**
     * 将日期类型格式化为字符串
     *
     * @param toDate
     * @return 2017-03-02
     */
    public static String dateFormatYmd(Date toDate){
        SimpleDateFormat format = new SimpleDateFormat(YMD);
        return format.format(toDate);
    }
}
