package com.paradisecloud.fcm.mqtt.enums;

import java.util.HashMap;
import java.util.Map;

import com.sinhy.exception.SystemException;

public enum MqttBridgeStatus {

    
    /**
     * 初始化中
     */
    INITIALIZING(1, "初始化中"),
    
    /**
     * 可用
     */
    AVAILABLE(100, "可用"),
    
    /**
     * 不可用
     */
    NOT_AVAILABLE (200, "不可用");
    
    /**
     * 值
     */
    private int value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<Integer, MqttBridgeStatus> MAP = new HashMap<>();
    static
    {
        for (MqttBridgeStatus recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    MqttBridgeStatus(int value, String name)
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
    
    public static MqttBridgeStatus convert(int value)
    {
    	MqttBridgeStatus t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + MqttBridgeStatus.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }

}
