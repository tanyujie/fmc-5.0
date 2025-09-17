/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : PollingStrategy.java
 * Package : com.paradisecloud.fcm.common.enumer
 * 
 * @author sinhy
 * 
 * @since 2021-09-09 11:50
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.mcu.plc.cache.enumer;

import com.paradisecloud.fcm.mcu.plc.cache.model.polingstrategy.*;
import com.sinhy.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>轮询策略</pre>
 * 
 * @author sinhy
 * @since 2021-09-09 11:50
 * @version V1.0
 */
public enum PollingStrategy
{
    
    /**
     * 全局
     */
    GLOBAL(10, "全局", new GlobalPollingStrategy()),
    
    /**
     * 全局+组织架构优先
     */
    GLOBAL_AND_DEPT_FIRST(20, "全局+组织架构优先", new GlobalAndDeptFirstPollingStrategy()),
    
    /**
     * 选定
     */
    SPECIFIED_RANGE(30, "选定", new SpecifiedRangePollingStrategy()),
    
    /**
     * 选定+组织架构优先
     */
    SPECIFIED_RANGE_AND_DEPT_FIRST(40, "选定+组织架构优先", new SpecifiedRangeAndDeptFirstPollingStrategy()),
    
    /**
     * 选定+全局
     */
    SPECIFIED_RANGE_AND_GLOBAL(50, "选定+全局", new SpecifiedRangeAndGlobalPollingStrategy()),
    
    /**
     * 选定+全局+组织架构优先
     */
    SPECIFIED_RANGE_AND_GLOBAL_AND_DEPT_FIRST(60, "选定+全局+组织架构优先", new SpecifiedRangeAndGlobalAndDeptFirstPollingStrategy());
    
    /**
     * 信息码
     */
    private int value;
    
    /**
     * 信息描述
     */
    private String name;
    
    private AttendeePollingStrategy strategy;
    
    private static final Map<Integer, PollingStrategy> MAP = new HashMap<>();
    static
    {
        for (PollingStrategy recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    PollingStrategy(int value, String name, AttendeePollingStrategy strategy)
    {
        this.value = value;
        this.name = name;
        this.strategy = strategy;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public String getName()
    {
        return name;
    }
    
    /**
     * <p>Get Method   :   strategy AttendeePollingStrategy</p>
     * @return strategy
     */
    public AttendeePollingStrategy getStrategy()
    {
        return strategy;
    }

    public static PollingStrategy convert(int value)
    {
        PollingStrategy t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + PollingStrategy.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
