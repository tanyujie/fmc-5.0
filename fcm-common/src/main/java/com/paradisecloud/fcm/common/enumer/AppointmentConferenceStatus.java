/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2020-, All right reserved.
 * Description : <pre>(用一句话描述该文件做什么)</pre>
 * FileName :
 * Package :
 * 
 * @author
 * 
 * @since 2020/12/24 11:18
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.common.enumer;

import java.util.HashMap;
import java.util.Map;

import com.sinhy.exception.SystemException;

/**
 * <pre>是否枚举</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum AppointmentConferenceStatus
{
    
    /**
     * 停用
     */
    DISABLED(2, "停用"),
    
    /**
     * 启用
     */
    ENABLED(1, "启用");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, AppointmentConferenceStatus> MAP = new HashMap<>();
    static
    {
        for (AppointmentConferenceStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    AppointmentConferenceStatus(int value, String name)
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
    
    public static AppointmentConferenceStatus convert(int value)
    {
        AppointmentConferenceStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + AppointmentConferenceStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
