package com.paradisecloud.fcm.ops.cloud.mqtt.enums;

import com.sinhy.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zyz
 *
 */
public enum MqttType {
	
	 //集群
    CLUSTER(100, "集群"),
    
    //单节点
    SINGLE_NODE(1, "单节点");
    
    //值
    private int value;
    
    //名
    private String name;
    
    private static final Map<Integer, MqttType> MAP = new HashMap<>();
   
    static
    {
        for (MqttType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    MqttType(int value, String name)
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
    
    public static MqttType convert(int value)
    {
    	MqttType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + MqttType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
