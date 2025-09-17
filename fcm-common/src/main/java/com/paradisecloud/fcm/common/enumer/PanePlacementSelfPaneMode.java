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
public enum PanePlacementSelfPaneMode
{
    
    /**
     * 不设置
     */
    OFF(-1, "", "不设置"),
    
    /**
     * 显示自己
     */
    SELF(1, "self", "显示自己"),
    
    /**
     * 跳过
     */
    SKIP(2, "skip", "跳过"),
    
    /**
     * 显示空白
     */
    BLANK(3, "blank", "显示空白");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 字符串值
     */
    private String stringValue;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, PanePlacementSelfPaneMode> MAP = new HashMap<>();
    static
    {
        for (PanePlacementSelfPaneMode recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    PanePlacementSelfPaneMode(int value, String stringValue, String name)
    {
        this.value = value;
        this.stringValue = stringValue;
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
     * <p>Get Method   :   stringValue String</p>
     * @return stringValue
     */
    public String getStringValue()
    {
        return stringValue;
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
    
    public static PanePlacementSelfPaneMode convert(int value)
    {
        PanePlacementSelfPaneMode t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + PanePlacementSelfPaneMode.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
