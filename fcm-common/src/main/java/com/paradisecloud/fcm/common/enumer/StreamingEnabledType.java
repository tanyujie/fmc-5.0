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
 * <pre>直播开关枚举</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum StreamingEnabledType
{

    /**
     * 否
     */
    NO(2, "否"),

    /**
     * 是
     */
    YES(1, "是"),

    /**
     * 是
     */
    CLOUDS(3, "云直播"),

    /**
     * 是
     */
    THIRD_PARTY(4, "第三方直播");

    /**
     * 值
     */
    private int value;

    /**
     * 名
     */
    private String name;

    private static final Map<Integer, StreamingEnabledType> MAP = new HashMap<>();
    static
    {
        for (StreamingEnabledType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    StreamingEnabledType(int value, String name)
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
    
    public static StreamingEnabledType convert(Integer value)
    {
        if (value == null) {
            return StreamingEnabledType.NO;
        }
        if (StreamingEnabledType.YES.getValue() == value.intValue()) {
            return StreamingEnabledType.YES;
        } else if (StreamingEnabledType.CLOUDS.getValue() == value.intValue()) {
            return StreamingEnabledType.CLOUDS;
        } else if (StreamingEnabledType.THIRD_PARTY.getValue() == value.intValue()) {
            return StreamingEnabledType.THIRD_PARTY;
        }
        return StreamingEnabledType.NO;
    }
}
