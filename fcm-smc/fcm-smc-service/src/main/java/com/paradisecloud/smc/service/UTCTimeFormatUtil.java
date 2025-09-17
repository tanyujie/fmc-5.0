package com.paradisecloud.smc.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author nj
 * @date 2023/3/7 9:59
 */
public class UTCTimeFormatUtil {

    public static Date localToUTC(String localTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date localDate= null;

        localDate = sdf.parse(localTime);

        long localTimeInMillis=localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate=new Date(calendar.getTimeInMillis());
        return utcDate;
    }

    /**
     * utc时间转成local时间
     * @param utcTime
     * @return
     */
    public static Date utcToLocal(String utcTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;

        utcDate = sdf.parse(utcTime);
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate;
    }

    public static String convertToUtc(String inputTime) {
        TimeZone inputTz = TimeZone.getDefault();
        TimeZone utcTz = TimeZone.getTimeZone("UTC");

        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputSdf.setTimeZone(inputTz);

        SimpleDateFormat utcSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcSdf.setTimeZone(utcTz);

        Date fromDate = null;
        try {
            fromDate = inputSdf.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return utcSdf.format(fromDate);
    }

    public static String convertToUtc(String inputTime,String pattern) {
        TimeZone inputTz = TimeZone.getDefault();
        TimeZone utcTz = TimeZone.getTimeZone("UTC");

        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputSdf.setTimeZone(inputTz);

        SimpleDateFormat utcSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcSdf.setTimeZone(utcTz);

        Date fromDate = null;
        try {
            fromDate = inputSdf.parse(inputTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return utcSdf.format(fromDate);
    }




    public static String getRegion(long zone ) {
    //格式为时区大于0："+00:00","+08:00",时区小于0："-06:00"
     StringBuilder stringBuilder =new StringBuilder();
        if (zone >0) {
                        if (zone>10) {
                            stringBuilder.append("+").append(zone).append(":00");
                         }else {
                           stringBuilder.append("+").append("0").append(zone).append(":00");
                        }
        }else {
            if (zone<-10) {
                stringBuilder.append(zone).append(":00");
             }else {
               stringBuilder.append("-0").append(Math.abs(zone)).append(":00");
            }
        }
        return "GMT"+stringBuilder.toString();
    }

    public static String changeUTCTimeStr(String date,long zone) {
         DateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         format.setTimeZone(TimeZone.getTimeZone(getRegion(zone)));
         try {
             Date dateTest = format.parse(date);
             format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
             return format.format(dateTest)+" UTC";
         }catch (Exception e){
         }
        return "";
    }






}
