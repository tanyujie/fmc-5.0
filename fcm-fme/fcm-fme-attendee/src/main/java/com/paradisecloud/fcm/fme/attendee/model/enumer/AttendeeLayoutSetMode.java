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
package com.paradisecloud.fcm.fme.attendee.model.enumer;

import java.util.HashMap;
import java.util.Map;

import com.sinhy.exception.SystemException;

/**
 * <pre>参会者布局设置模式</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum AttendeeLayoutSetMode
{
    
    /**
     * 只设置主会场
     */
    MASTER(1, "只设置主会场"),
    
    /**
     * 设置分会场
     */
    SUB(10, "设置分会场"),
    
    /**
     * 设置所有会场
     */
    ALL(100, "设置所有会场");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, AttendeeLayoutSetMode> MAP = new HashMap<>();
    static
    {
        for (AttendeeLayoutSetMode recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    AttendeeLayoutSetMode(int value, String name)
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
    
    public static AttendeeLayoutSetMode convert(int value)
    {
        AttendeeLayoutSetMode t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + AttendeeLayoutSetMode.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
