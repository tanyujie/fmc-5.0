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
 * <pre>是否是级联会议之主</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum CascadeMainType
{
    
    /**
     * 是
     */
    YES(1, "是"),
    
    /**
     * 否
     */
    NO(2, "否");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, CascadeMainType> MAP = new HashMap<>();
    static
    {
        for (CascadeMainType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    CascadeMainType(int value, String name)
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
    
    public static CascadeMainType convert(int value)
    {
        CascadeMainType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + CascadeMainType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
