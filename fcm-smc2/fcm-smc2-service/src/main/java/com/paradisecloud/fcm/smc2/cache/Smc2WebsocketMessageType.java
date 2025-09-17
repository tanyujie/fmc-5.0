package com.paradisecloud.fcm.smc2.cache;

import com.sinhy.exception.SystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nj
 * @date 2023/3/20 10:47
 */
public enum Smc2WebsocketMessageType {
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
     * 主会场已变更
     */
    MASTER_CHANGED("masterChanged", "主会场已变更"),
    /**
     * 会议状态变化
     */
    CONFERENCE_STAGE("conference_stage", "会议状态变化"),
    /**
     * 会控变化
     */
    CONFERENCE_CHANGED("conference_changed", "会控变化"),

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
     * 单个与会者新增
     */
    ATTENDEE_ADD("attendeeAdd", "与会者新增"),

    /**
     * 单个与会者删除
     */
    ATTENDEE_DELETE("attendeeDelete", "与会者删除"),

    /**
     * 单个与会者更新
     */
    ATTENDEE_UPDATE("attendeeUpdate", "与会者更新"),

    /**
     * 与会者发言
     */
    ATTENDEE_SPEAKER("attendeeSpeaker", "与会者正在发言"),
    /**
     * 会议已结束
     */
    CONFERENCE_ENDED("conferenceEnded", "会议已结束"),
    /**
     * 参会者同步
     */
    PARTICIPANT_SYNC("participantSync", "参会者同步"),
    /**
     * 默认视图状态
     */
    DEFAULT_VIEW_RUNNING("defaultViewRunning", "默认视图运行状态"),

    /**
     * 轮询开始
     */
    ATTENDEE_POLLING_STARTED("attendeePollingStarted", "轮询开始"),

    /**
     * 轮询暂停
     */
    ATTENDEE_POLLING_PAUSED("attendeePollingPaused", "轮询暂停"),

    /**
     * 轮询结束
     */
    ATTENDEE_POLLING_ENDED("attendeePollingEnded", "轮询结束"),
    /**
     * 会议讨论
     */
    CONFERENCE_DISCUSS("conferenceDiscuss", "会议讨论"),

    /**
     * 会议已启动
     */
    CONFERENCE_STARTED("conferenceStarted", "会议已启动"),
    /**
     * 会议预约信息
     */
    APPOINTMENT_INFO("appointmentInfo", "会议预约信息"),

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
    private static final Map<String, Smc2WebsocketMessageType> MAP = new HashMap<>();
    static
    {
        for (Smc2WebsocketMessageType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }

    Smc2WebsocketMessageType(String value, String name)
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


    public Smc2WebsocketMessage create(Object data)
    {
        return new Smc2WebsocketMessage(value, data);
    }


    public Smc2WebsocketMessage create(Object data, String destination)
    {
        return new Smc2WebsocketMessage(value, data, destination);
    }

    public static Smc2WebsocketMessageType convert(String value)
    {
        Smc2WebsocketMessageType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + Smc2WebsocketMessageType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
