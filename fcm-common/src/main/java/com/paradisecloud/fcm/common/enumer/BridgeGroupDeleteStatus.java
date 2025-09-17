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
 * <pre>会议桥组删除状态</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum BridgeGroupDeleteStatus
{
    
    /**
     * 启用的
     */
    DELETED(1, "删除"),
    
    /**
     * 未删除
     */
    NOT_DELETED(0, "未删除");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, BridgeGroupDeleteStatus> MAP = new HashMap<>();
    static
    {
        for (BridgeGroupDeleteStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    BridgeGroupDeleteStatus(int value, String name)
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
    
    public static BridgeGroupDeleteStatus convert(int value)
    {
        BridgeGroupDeleteStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + BridgeGroupDeleteStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
