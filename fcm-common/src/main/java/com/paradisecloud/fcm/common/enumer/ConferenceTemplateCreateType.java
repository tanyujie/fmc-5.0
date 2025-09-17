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
 * <pre>会议模板创建方式</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum ConferenceTemplateCreateType
{
    
    /**
     * 否
     */
    AUTO(1, "自动"),
    
    /**
     * 是
     */
    MANUAL(2, "手动");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, ConferenceTemplateCreateType> MAP = new HashMap<>();
    static
    {
        for (ConferenceTemplateCreateType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    ConferenceTemplateCreateType(int value, String name)
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
    
    public static ConferenceTemplateCreateType convert(int value)
    {
        ConferenceTemplateCreateType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + ConferenceTemplateCreateType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
