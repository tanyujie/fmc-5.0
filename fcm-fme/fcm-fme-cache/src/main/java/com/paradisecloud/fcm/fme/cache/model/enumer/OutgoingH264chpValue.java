/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ParticipantBulkOperationMode.java
 * Package     : com.paradisecloud.fcm.fme.cache.model.enumer
 * @author sinhy 
 * @since 2021-09-01 09:22
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.cache.model.enumer;

import java.util.HashMap;
import java.util.Map;

import com.sinhy.exception.SystemException;

/**  
 * <pre>OutgoingH264chp选项值</pre>
 * @author sinhy
 * @since 2021-09-01 09:22
 * @version V1.0  
 */
public enum OutgoingH264chpValue
{

    
    /**
     * 打开
     */
    ON("on", "打开"),
    
    /**
     * 关闭
     */
    OFF("off", "关闭");
    
    /**
     * 值
     */
    private String value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<String, OutgoingH264chpValue> MAP = new HashMap<>();
    static
    {
        for (OutgoingH264chpValue recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    OutgoingH264chpValue(String value, String name)
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
    public String getValue()
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
    
    public static OutgoingH264chpValue convert(String value)
    {
        OutgoingH264chpValue t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + OutgoingH264chpValue.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }

}
