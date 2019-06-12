package com.zhouyutong.zapplication.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期utils
 *
 * @author liyingjie
 */
public class DateUtils {
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    // 北京时间时区
    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("GMT+8");

    private DateUtils() {
    }

    public static Date addDay(Date date, int day) {
        Preconditions.checkArgument(date != null, "Param date must be not null");
        Preconditions.checkArgument(day > 0, "Param day must gt 0");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    public static Date addHour(Date date, int hour) {
        Preconditions.checkArgument(date != null, "Param date must be not null");
        Preconditions.checkArgument(hour > 0, "Param hour must gt 0");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }

    /**
     * 获取当前时间的毫秒精度
     *
     * @return yyyy-MM-dd HH:mm:ss.SSS格式
     */
    public static String currentTimeMillis() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS_SSS);
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间的秒精度
     *
     * @return yyyy-MM-dd HH:mm:ss格式
     */
    public static String currentTimeSecond() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD_HH_MM_SS);
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间的日精度
     *
     * @return yyyy-MM-dd HH:mm:ss格式
     */
    public static String currentTimeDay() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD);
        return sdf.format(new Date());
    }

    public static String format(Date date, String format) {
        Preconditions.checkArgument(date != null, "Param date must be not null");
        Preconditions.checkArgument(StringUtils.isNotBlank(format), "Param format must be not null or empty");
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 比较两个Date日期大小
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 1表示date1大于date2, 0表示date1和date2的日期完全相等,-1表示date1小于date2
     */
    public static int compare(Date date1, Date date2) {
        Preconditions.checkArgument(date1 != null, "Param date1 must be not null");
        Preconditions.checkArgument(date2 != null, "Param date2 must be not null");

        if (date1.after(date2)) {
            return 1;
        } else if (date1.before(date2)) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 格式化Date日期为 20160415112036 格式
     *
     * @param date 日期
     * @return long
     */
    public static long format2Long(Date date) {
        return format2Long(date, false);
    }

    /**
     * 格式化Date日期为 20160415112036 格式 或 20160415112036007 格式
     *
     * @param date           日期
     * @param hasMillisecond 是否包含毫秒
     * @return long
     */
    public static long format2Long(Date date, boolean hasMillisecond) {
        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.setTime(date);
        String year = String.format("%04d", calendar.get(Calendar.YEAR));
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
        String second = String.format("%02d", calendar.get(Calendar.SECOND));
        String str = year + month + day + hour + minute + second;
        if (hasMillisecond) {
            String millisecond = String.format("%03d", calendar.get(Calendar.MILLISECOND));
            str = str.concat(millisecond);
        }
        return Long.parseLong(str);
    }

    /**
     * 格式化Date日期为 20160415 格式
     *
     * @param date 日期
     * @return int
     */
    public static int format2Int(Date date) {
        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.setTime(date);
        String year = String.format("%04d", calendar.get(Calendar.YEAR));
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(year + month + day);
    }

    /**
     * 当前时间格式化为 20160415112036 格式
     *
     * @return long
     */
    public static long formatNow2Long() {
        return format2Long(new Date());
    }

    /**
     * 当前时间加上指定秒数后格式化为 20160415112036 格式
     *
     * @return long
     */
    public static long formatNowAddSecond2Long(int second) {
        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.add(Calendar.SECOND, second);
        return format2Long(calendar.getTime());
    }

    /**
     * 当前时间加上指定分钟数后格式化为 20160415112036 格式
     *
     * @return long
     */
    public static long formatNowAddMinute2Long(int minute) {
        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.add(Calendar.MINUTE, minute);
        return format2Long(calendar.getTime());
    }

    /**
     * 当前时间加上指定分钟数后格式化为 20160415112036 格式
     *
     * @return long
     */
    public static long formatNowAdHour2Long(int hour) {
        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return format2Long(calendar.getTime());
    }

    /**
     * 数字型可视化时间转换为Date
     *
     * @param format 数字时间
     * @return date
     */
    public static Date parse(long format) {
        String str = String.valueOf(format);
        if (str.length() >= 8) {
            Calendar calendar = Calendar.getInstance(TIMEZONE);
            calendar.clear();
            calendar.set(Calendar.YEAR, Integer.parseInt(str.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(str.substring(4, 6)) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(str.substring(6, 8)));
            if (str.length() >= 14) {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(str.substring(8, 10)));
                calendar.set(Calendar.MINUTE, Integer.parseInt(str.substring(10, 12)));
                calendar.set(Calendar.SECOND, Integer.parseInt(str.substring(12, 14)));
            }
            if (str.length() == 17) {
                calendar.set(Calendar.MILLISECOND, Integer.parseInt(str.substring(14, 17)));
            }
            return calendar.getTime();
        }
        return null;
    }

    /**
     * 获取本周第一天
     *
     * @return
     */
    public static Date getFirstDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        return cal.getTime();
    }

    /**
     * 获取本周最后一天
     *
     * @return
     */
    public static Date getLastDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    public static Date getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date addYears(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addYears(date, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addMonths(date, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addWeeks(date, amount);
    }

    public static Date addDays(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addDays(date, amount);
    }

    public static Date addHours(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addHours(date, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addMinutes(date, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addSeconds(date, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return org.apache.commons.lang3.time.DateUtils.addMilliseconds(date, amount);
    }
}
