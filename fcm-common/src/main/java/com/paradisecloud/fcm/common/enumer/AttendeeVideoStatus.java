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
 * <pre>与会者混音状态</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum AttendeeVideoStatus
{
    
    /**
     * 未混音
     */
    NO(2, "关闭"),
    
    /**
     * 混音
     */
    YES(1, "开启");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, AttendeeVideoStatus> MAP = new HashMap<>();
    static
    {
        for (AttendeeVideoStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    AttendeeVideoStatus(int value, String name)
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
    
    public static AttendeeVideoStatus convert(int value)
    {
        AttendeeVideoStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + AttendeeVideoStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
    
    public static AttendeeVideoStatus convert(boolean value)
    {
        AttendeeVideoStatus t = MAP.get(value ? NO.value : YES.value);
        if (t == null)
        {
            throw new SystemException("非法的" + AttendeeVideoStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
