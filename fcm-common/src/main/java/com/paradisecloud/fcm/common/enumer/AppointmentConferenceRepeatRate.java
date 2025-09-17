/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AppointmentConferenceRepeatRate.java
 * Package     : com.paradisecloud.fcm.common.enumer
 * @author lilinhai 
 * @since 2021-05-26 18:34
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.common.enumer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sinhy.enumer.DateTimeFormatPattern;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.DateUtils;

/**  
 * <pre>预约会议重复频率</pre>
 * @author lilinhai
 * @since 2021-05-26 18:34
 * @version V1.0  
 */
public enum AppointmentConferenceRepeatRate
{
    
    /**
     * 自定义
     */
    CUSTOM(1, "自定义") {
        
        @Override
        public boolean isOK(Integer repeatDate)
        {
            return true;
        }
        
        @Override
        public String getDate(Integer repeatDate)
        {
            return null;
        }
    },
    
    /**
     * 每天
     */
    EVERY_DAY(2, "每天") {
        
        @Override
        public boolean isOK(Integer repeatDate)
        {
            return true;
        }
        
        @Override
        public String getDate(Integer repeatDate)
        {
            return DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13, new Date());
        }
    },
    
    /**
     * 每周
     */
    WEEKLY(3, "每周") {
        
        @Override
        public boolean isOK(Integer repeatDate)
        {
            if (repeatDate == null)
            {
                return false;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            
            boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
            
            // 指示一个星期中的某天。
            int w = isFirstSunday ? cal.get(Calendar.DAY_OF_WEEK) - 1 : cal.get(Calendar.DAY_OF_WEEK);
            return w == repeatDate;
        }
        
        @Override
        public String getDate(Integer repeatDate)
        {
            if (repeatDate == null)
            {
                return null;
            }
            Calendar cal = Calendar.getInstance();
            boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.SUNDAY);
            cal.set(Calendar.DAY_OF_WEEK, isFirstSunday ? repeatDate + 1 : repeatDate);
            return DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13, cal.getTime());
        }
    },
    
    /**
     * 每月
     */
    MONTHLY(4, "每月") {
        
        @Override
        public boolean isOK(Integer repeatDate)
        {
            if (repeatDate == null)
            {
                return false;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            
            // 指示一个星期中的某天。
            int w = cal.get(Calendar.DAY_OF_MONTH);
            return w == repeatDate;
        }

        @Override
        public String getDate(Integer repeatDate)
        {
            if (repeatDate == null)
            {
                return null;
            }
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, repeatDate);
            return DateUtils.formatTo(DateTimeFormatPattern.PATTERN_13, cal.getTime());
        }
        
    };
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, AppointmentConferenceRepeatRate> MAP = new HashMap<>();
    static
    {
        for (AppointmentConferenceRepeatRate recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    AppointmentConferenceRepeatRate(int value, String name)
    {
        this.value = value;
        this.name = name;
    }
    
    /**
     * <p>
     * Get Method : value int
     * </p>
     * 
     * @return value
     */
    public int getValue()
    {
        return value;
    }
    
    /**
     * <p>
     * Get Method : name String
     * </p>
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }
    
    public abstract boolean isOK(Integer repeatDate);
    public abstract String getDate(Integer repeatDate);
    
    public static AppointmentConferenceRepeatRate convert(int value)
    {
        AppointmentConferenceRepeatRate t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的重复频率枚举值：" + value);
        }
        return t;
    }
}
