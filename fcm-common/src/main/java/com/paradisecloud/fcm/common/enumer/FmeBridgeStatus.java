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
public enum FmeBridgeStatus
{
    
    /**
     * 初始化中
     */
    INITIALIZING(1, "初始化中"),
    
    /**
     * 可用
     */
    AVAILABLE(100, "可用"),
    
    /**
     * 不可用
     */
    NOT_AVAILABLE (200, "不可用");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, FmeBridgeStatus> MAP = new HashMap<>();
    static
    {
        for (FmeBridgeStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    FmeBridgeStatus(int value, String name)
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
    
    public static FmeBridgeStatus convert(int value)
    {
        FmeBridgeStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + FmeBridgeStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
