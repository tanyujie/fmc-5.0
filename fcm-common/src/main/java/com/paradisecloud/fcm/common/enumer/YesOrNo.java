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
 * <pre>是否枚举</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum YesOrNo
{
    
    /**
     * 否
     */
    NO(2, "否"),
    
    /**
     * 是
     */
    YES(1, "是");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, YesOrNo> MAP = new HashMap<>();
    static
    {
        for (YesOrNo recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    YesOrNo(int value, String name)
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
    
    public Boolean getBoolean()
    {
        return this == YES;
    }
    
    public String getBooleanString()
    {
        return String.valueOf(this == YES);
    }
    
    public static YesOrNo convert(Integer value)
    {
        if (value == null) {
            return YesOrNo.NO;
        }
        if (YesOrNo.YES.getValue() == value.intValue()) {
            return YesOrNo.YES;
        }
        return YesOrNo.NO;
    }
}
