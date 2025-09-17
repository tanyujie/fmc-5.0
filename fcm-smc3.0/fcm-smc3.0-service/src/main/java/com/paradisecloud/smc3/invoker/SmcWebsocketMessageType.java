package com.paradisecloud.smc3.invoker;

import com.sinhy.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2023/3/20 10:47
 */
public enum SmcWebsocketMessageType {
    /**
     * 会监号码
     */
    SMC_MONITOR_NUMBER_REMOVE("smc_monitor_number_remove", "会监号码移除"),
    /**
     * 会监号码
     */
    SMC_MONITOR_NUMBER_ADD("smc_monitor_number_add", "会监号码添加"),
    /**
     * 广播信息
     */
    BROAD_LIST("broad_list", "广播信息"),
    /**
     * 选看信息
     */
    CHOOSE_LIST("choose_list", "选看信息"),
    /**
     * 轮询信息
     */
    POLLING_LIST("polling_list", "轮询信息"),
    /**
     * 会议状态变化
     */
    CONFERENCE_STAGE("conference_stage", "会议状态变化"),
    /**
     * 会控变化
     */
    CONFERENCE_CHANGED("conference_changed", "会控变化"),
    /**
     * 会场名称修改
     */
    PARTICIPANT_NAME_CHANGED("participant_name_changed", "会场名称修改"),

    /**
     * 会场变化
     */
    PARTICIPANT_CHANGED("participant_changed", "会场变化"),
    /**
     * 会场添加
     */
    PARTICIPANT_ADD("participant_add", "会场添加"),
    /**
     * 会场移除
     */
    PARTICIPANT_REMOVE("participant_remove", "会场移除"),

    /**
     * 实时信息
     */
    REALTIME_INFO("realtime_info", "实时信息"),

    /**
     * 提示消息
     */
    MESSAGE_TIP("messageTip", "提示消息"),

    /**
     * 警告消息
     */
    MESSAGE_WARN("messageWarn", "警告消息"),

    /**
     * 错误消息
     */
    MESSAGE_ERROR("messageError", "错误消息");

    /**
     * 值
     */
    private String value;

    /**
     * 名
     */
    private String name;
    private static final Map<String, SmcWebsocketMessageType> MAP = new HashMap<>();
    static
    {
        for (SmcWebsocketMessageType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    SmcWebsocketMessageType(String value, String name)
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


    public SmcWebsocketMessage create(Object data)
    {
        return new SmcWebsocketMessage(value, data);
    }


    public SmcWebsocketMessage create(Object data, String destination)
    {
        return new SmcWebsocketMessage(value, data, destination);
    }

    public static SmcWebsocketMessageType convert(String value)
    {
        SmcWebsocketMessageType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + SmcWebsocketMessageType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
