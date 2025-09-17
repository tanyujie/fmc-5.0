package com.paradisecloud.fcm.common.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author johnson liu
 * @date 2021/5/14 14:25
 */
public class DateUtil {
    /**
     * 转换日期对象
     *
     * @param time
     * @return
     */
    public static Date convertDateByString(String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return Date.from(localDateTime.atZone(TimeZone.getTimeZone("UTC").toZoneId()).toInstant());
    }

    /**
     * 将指定格式的日期字符串转为日期
     * @param time
     * @param pattern
     * @return
     */
    public static Date convertDateByString(String time, String pattern) {
        pattern = (pattern == null || pattern.length() == 0) ? "yyyy-MM-dd HH:mm:ss" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将日期转换为指定的格式输出
     * @param date
     * @param pattern
     * @return
     */
    public static String convertDateToString(Date date, String pattern) {
        pattern = (pattern == null || pattern.length() == 0) ? "yyyy-MM-dd HH:mm:ss" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = date.toInstant().atZone(zoneId).toLocalDateTime();
        String format = dateTimeFormatter.format(localDateTime);
        return format;
    }

    /**
     * 将日期转换为指定的格式输出
     * @param date
     * @param pattern
     * @param locale
     * @return
     */
    public static String convertDateToString(Date date, String pattern, Locale locale) {
        pattern = (pattern == null || pattern.length() == 0) ? "yyyy-MM-dd HH:mm:ss" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, locale);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = date.toInstant().atZone(zoneId).toLocalDateTime();
        String format = dateTimeFormatter.format(localDateTime);
        return format;
    }

    /**
     * 转换日期对象
     *
     * @param time
     * @return
     */
    public static Date convertDateByString(String time, String pattern, ZoneId zoneId) {
        pattern = (pattern == null || pattern.length() == 0) ? "yyyy-MM-dd'T'HH:mm:ss" : pattern;
        zoneId = zoneId == null ? ZoneId.of("UTC") : zoneId;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return Date.from(localDateTime.atZone(TimeZone.getTimeZone(zoneId.getId()).toZoneId()).toInstant());
    }

    /**
     * 对日期格式的字符串进行时分秒填充
     * @param date
     * @param isEndTime
     * @return
     */
    public static String fillDateString(String date, boolean isEndTime) {
        if(date==null|| date.equals("")){
            return date;
        }
        StringBuilder stringBuilder = new StringBuilder(date);
        stringBuilder.append(isEndTime ? " 23:59:59" : " 00:00:00");
        return stringBuilder.toString();
    }

    public static String convertDateToString(LocalDate date, String pattern){
        pattern = (pattern == null || pattern.length() == 0) ? "yyyy-MM-dd" : pattern;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        String format = dateTimeFormatter.format(date);
        return format;
    }

    /**
     * 获取两个日期的间隔天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getIntervalDays(String startDate,String endDate){
        LocalDate startLocalDate=LocalDate.parse(startDate);
        LocalDate endLocalDate=LocalDate.parse(endDate);
        return (int)(endLocalDate.toEpochDay()-startLocalDate.toEpochDay());
    }

    public static Date convertLocalDateToDate(LocalDate localDate){
        Date date = Date.from(localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
        return date;
    }

    /**
     * 获取两个时间的差值 毫秒数
     * @param startTime
     * @param endTime
     * @return 相差的毫秒数
     */
    public static long getDurationNum(LocalDateTime startTime,LocalDateTime endTime){
        Duration between = Duration.between(startTime, endTime);
        return between.toMillis();
    }

    /**
     * 清除时间
     *
     * @param date
     * @return
     */
    public static Date clearTime(Date date) {
        String ymd = convertDateToString(date, "yyyy-MM-dd");
        String ymdT = ymd + " 00:00:00";
        return convertDateByString(ymdT, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 对日期进行时分秒填充
     * @param date
     * @param isEndTime
     * @return
     */
    public static Date fillDate(Date date, boolean isEndTime) {
        if(date == null) {
            return date;
        }
        String dateYmd = convertDateToString(date, "yyyy-MM-dd");
        StringBuilder stringBuilder = new StringBuilder(dateYmd);
        stringBuilder.append(isEndTime ? " 23:59:59" : " 00:00:00");
        return convertDateByString(stringBuilder.toString(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取该月的第一天
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 获取该月的最后一天
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 清除日期中的秒
     * @param date
     * @return
     */
    public static Date getDayOfClearSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

}
