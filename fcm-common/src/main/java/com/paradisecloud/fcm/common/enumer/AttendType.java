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
 * <pre>入会类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum AttendType
{
    
    /**
     * 直播
     */
    LIVE(10, "直播"),
    
    /**
     * 被叫
     */
    OUT_BOUND(1, "被叫"),
    
    /**
     * 手动主叫
     */
    MANUAL_JOIN(2, "手动主叫"),
    
    /**
     * 自动主叫
     */
    AUTO_JOIN(3, "自动主叫");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, AttendType> MAP = new HashMap<>();
    static
    {
        for (AttendType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    AttendType(int value, String name)
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
    
    public boolean isLive()
    {
        return this == LIVE;
    }
    
    public boolean isJoin()
    {
        return this != LIVE;
    }
    
    public static AttendType convert(Integer value)
    {
        AttendType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + AttendType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
