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

import com.paradisecloud.fcm.common.message.websocket.WebSocketMessage;
import com.sinhy.exception.SystemException;

/**
 * <pre>Websocket消息类型</pre>
 * 
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum WebsocketMessageType
{
    
    /**
     * 全量数据同步
     */
    ALL("all", "全量数据"),
    
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
     * 与会者双流
     */
    ATTENDEE_DUAL("attendeeDual", "与会者双流更新"),

    /**
     * 当前发言
     */
    CURRENT_SPEAKERS("currentSpeakers", "当前发言者"),
    
    /**
     * 会议室锁定
     */
    CONFERENCE_LOCK("conferenceLock", "会议室锁定"),
    /**
     * 会议更新
     */
    CONFERENCE_CHANGE("conferenceChange", "会议更新"),
    
    /**
     * 允许所有人静音自己
     */
    CONFERENCE_ALLOW_ALL_MUTE_SELF("conferenceAllowAllMuteSelf", "允许所有人静音自己"),
    
    /**
     * 允许辅流控制
     */
    ALLOW_ALL_PRESENTATION_CONTRIBUTION("conferenceAllowAllPresentationContribution", "允许辅流控制"),
    
    /**
     * 新加入用户静音
     */
    JOIN_AUDIO_MUTE_OVERRIDE("conferencejoinAudioMuteOverride", "新加入用户静音"),
    
    /**
     * 会议讨论
     */
    CONFERENCE_DISCUSS("conferenceDiscuss", "会议讨论"),

    /**
     * 会议模式
     */
    CONFERENCE_MODEL("conferenceModel", "会议模式"),

    /**
     * 会议录制
     */
    RECORDED("recorded", "会议录制"),
    
    /**
     * 会议直播
     */
    STREAMING("streaming", "会议直播"),

    /**
     * 会议纪要
     */
    MINUTES("minutes", "会议纪要"),
    
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
     * 会议状态
     */
    CONFERENCE_STAGE("conferenceStage", "会议状态消息"),
    /**
     * 会议已启动
     */
    CONFERENCE_STARTED("conferenceStarted", "会议已启动"),
    
    /**
     * 会议已结束
     */
    CONFERENCE_ENDED("conferenceEnded", "会议已结束"),

    /**
     * 会议已移除
     */
    CONFERENCE_REMOVED("conferenceRemoved", "会议已移除"),

    /**
     * 下级会议已移除
     */
    DOWN_CASCADE_CONFERENCE_ADDED("downCascadeConferenceAdded", "下级会议已添加"),

    /**
     * 下级会议已移除
     */
    DOWN_CASCADE_CONFERENCE_REMOVED("downCascadeConferenceRemoved", "下级会议已移除"),

    /**
     * 会议倒计时
     */
    CONFERENCE_TIME_COUNTDOWN("conferenceTimeCountdown", "会议倒计时"),
    
    /**
     * 会议预约信息
     */
    APPOINTMENT_INFO("appointmentInfo", "会议预约信息"),
    
    /**
     * 主会场已变更
     */
    MASTER_CHANGED("masterChanged", "主会场已变更"),
    
    /**
     * 参会者同步
     */
    PARTICIPANT_SYNC("participantSync", "参会者同步"),
    /**
     * 会议名称变更
     */
    CONFERENCE_NAME_CHANGED("conferenceNameChanged", "会议名称变更"),

    /**
     * 会议中观看直播的终端数
     */
    LIVE_TERMINAL_COUNT("liveTerminalCount", "会议中观看直播的终端数"),
    /**
     * 会监号码
     */
    SMC_MONITOR_NUMBER_REMOVE("smc_monitor_number_remove", "会监号码移除"),
    /**
     * 会监号码
     */
    SMC_MONITOR_NUMBER_ADD("smc_monitor_number_add", "会监号码添加"),
    /**
     * 会议监控
     */
    CONFERENCE_MONITOR_START("conference_monitor_start", "会议监控开始"),
    /**
     * 会议监控
     */
    CONFERENCE_MONITOR_END("conference_monitor_end", "会议监控结束"),

    /**
     * 选看信息
     */
    CHOOSE_LIST("choose_list", "选看信息"),
    
    /**
     * 提示消息
     */
    MESSAGE_TIP("messageTip", "提示消息"),

    /**
     * 提示消息
     */
    MESSAGE_SHOW_TIP("messageShowTip", "提示消息"),


    /**
     * 警告消息
     */
    MESSAGE_WARN("messageWarn", "警告消息"),
    
    /**
     * 错误消息
     */
    MESSAGE_ERROR("messageError", "错误消息"),

    /**
     * 网络检测消息文本
     */
    NET_CHECK_MSG_TEXT("netCheckMsgText", "网络检测消息文本"),

    /**
     * 服务启动
     */
    NET_CHECK_SERVER_STARTED("netCheckServerStarted", "网络检测服务启动"),

    /**
     * 服务停止
     */
    NET_CHECK_SERVER_STOPPED("netCheckServerStopped", "网络检测服务停止")
    ;

    /**
     * 值
     */
    private String value;
    
    /**
     * 名
     */
    private String name;
    
    private static final Map<String, WebsocketMessageType> MAP = new HashMap<>();



    static
    {
        for (WebsocketMessageType recordType : values())
        {
            MAP.put(recordType.value, recordType);
        }
    }
    
    WebsocketMessageType(String value, String name)
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
    
    /**
     * <pre>创建一个消息</pre>
     * @author lilinhai
     * @since 2021-02-04 16:39 
     * @param data
     * @return WebSocketMessage
     */
    public WebSocketMessage create(Object data)
    {
        return new WebSocketMessage(value, data);
    }
    
    /**
     * <pre>创建一个消息</pre>
     * @author lilinhai
     * @since 2021-02-04 16:39 
     * @param data
     * @return WebSocketMessage
     */
    public WebSocketMessage create(Object data, String destination)
    {
        return new WebSocketMessage(value, data, destination);
    }
    
    public static WebsocketMessageType convert(String value)
    {
        WebsocketMessageType t = MAP.get(value);
        if (t == null)
        {
            throw new SystemException("非法的" + WebsocketMessageType.class.getSimpleName() + "枚举值：" + value);
        }
        return t;
    }
}
