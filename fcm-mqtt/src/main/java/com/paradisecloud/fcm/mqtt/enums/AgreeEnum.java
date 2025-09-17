package com.paradisecloud.fcm.mqtt.enums;

import java.util.HashMap;
import java.util.Map;

import com.sinhy.exception.SystemException;

public enum AgreeEnum {
	 //同意
    AGREE(1, "同意"),
    
    //不同意
   NOT_AGREE(0, "不同意");
    
    //值
    private int value;
    
    //名
    private String name;
    
    private static final Map<Integer, AgreeEnum> MAP = new HashMap<>();
   
    static
    {
        for (AgreeEnum recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    AgreeEnum(int value, String name)
    {
        this.value = value;
        this.name = name;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public String getName()
    {
        return name;
    }
    
    public static AgreeEnum convert(int value)
    {
    	AgreeEnum t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + AgreeEnum.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
