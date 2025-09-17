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

/**
 * <pre>会议号段类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum ConferenceNumberSectionType
{

    /**
     * 随机号码
     */
    COMMON(0, "通用号段"),

    /**
     * 固定号段
     */
    FIXED(1, "固定号段");

    /**
     * 值
     */
    private int value;

    /**
     * 名
     */
    private String name;

    private static final Map<Integer, ConferenceNumberSectionType> MAP = new HashMap<>();
    static
    {
        for (ConferenceNumberSectionType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    ConferenceNumberSectionType(int value, String name)
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
    
    public static ConferenceNumberSectionType convert(Integer value)
    {
        if (value == null) {
            return COMMON;
        }
        ConferenceNumberSectionType t = MAP.get(value);
        if (t == null)
        {
            t = COMMON;
        }
        return t;
    }
}
