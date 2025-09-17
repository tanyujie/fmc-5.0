package com.paradisecloud.fcm.common.enumer;

import java.util.HashMap;
import java.util.Map;

import com.sinhy.exception.SystemException;

/**
 * 与会者类型状态枚举类
 *
 * @author zt1994 2019/8/21 13:57
 */
public enum ParticipantState
{
    
    /**
     * 初始化
     */
    INITIAL("initial"),
    
    /**
     * 响铃
     */
    RINGING("ringing"),
    
    /**
     * 连接
     */
    CONNECTED("connected"),
    
    /**
     * 密码会议室
     */
    ON_HOLD("onHold"),
    
    /**
     * 未连接
     */
    DISCONNECT("disconnect");
    
    /**
     * 描述
     */
    private final String value;
    
    private static final Map<String, ParticipantState> MAP = new HashMap<>();
    static
    {
        for (ParticipantState recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    ParticipantState(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
    
    public static ParticipantState convert(String value)
    {
        ParticipantState t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + ParticipantState.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
