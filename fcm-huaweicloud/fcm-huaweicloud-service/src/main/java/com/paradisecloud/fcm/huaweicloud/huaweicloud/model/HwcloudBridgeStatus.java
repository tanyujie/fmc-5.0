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
package com.paradisecloud.fcm.huaweicloud.huaweicloud.model;

import com.sinhy.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>会议桥业务状态</pre>
 * 
 */
public enum HwcloudBridgeStatus
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

    private static final Map<Integer, HwcloudBridgeStatus> MAP = new HashMap<>();
    static
    {
        for (HwcloudBridgeStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    HwcloudBridgeStatus(int value, String name)
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
    
    public static HwcloudBridgeStatus convert(int value)
    {
        HwcloudBridgeStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + HwcloudBridgeStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
