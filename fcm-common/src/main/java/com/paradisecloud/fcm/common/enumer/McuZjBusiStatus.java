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
 * <pre>会议桥业务状态</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum McuZjBusiStatus
{
    
    /**
     * 在线
     */
    ONLINE(1, "在线"),
    
    /**
     * 离线
     */
    OFFLINE(2, "离线");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, McuZjBusiStatus> MAP = new HashMap<>();
    static
    {
        for (McuZjBusiStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    McuZjBusiStatus(int value, String name)
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
    
    public static McuZjBusiStatus convert(int value)
    {
        McuZjBusiStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + McuZjBusiStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
