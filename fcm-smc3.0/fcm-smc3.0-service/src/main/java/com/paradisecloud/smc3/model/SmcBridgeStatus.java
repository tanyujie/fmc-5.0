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
package com.paradisecloud.smc3.model;

import com.sinhy.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>会议桥业务状态</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum SmcBridgeStatus
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

    private static final Map<Integer, SmcBridgeStatus> MAP = new HashMap<>();
    static
    {
        for (SmcBridgeStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    SmcBridgeStatus(int value, String name)
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
    
    public static SmcBridgeStatus convert(int value)
    {
        SmcBridgeStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + SmcBridgeStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
