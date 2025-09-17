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
package com.paradisecloud.fcm.fme.attendee.model.enumer;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.enumer.AttendeeCallTheRollStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeChooseSeeStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeRoundRobinStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeTalkStatus;
import com.paradisecloud.fcm.common.enumer.BroadcastStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.fme.attendee.model.operation.PollingAttendeeOpreationImpl;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.system.model.SysDeptCache;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;

/**
 * <pre>与会者权重</pre>
 * 点名:        [8000000, 8999999]
 * 主会场：      7500000
 * 选看：       [6000000, 6999999]
 * 轮询：       [5000000, 5999999]
 * 省FME权重：   7000000 （当地州终端是主会场时，省FME权重为4000000）
 * 普通会场：    20000
 * @author lilinhai
 * @since 2020-12-29 15:35
 * @version V1.0
 */
public enum AttendeeImportance
{
    
    /**
     * 分会场
     */
    COMMON(null, null, "分会场")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
        }
    },
    
    /**
     * 主会场
     */
    MASTER(7500000, 7500000, "主会场")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            // 主会场没有选看，广播和轮询三种状态
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            
            if (!(attendee instanceof FmeAttendee))
            {
                ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != attendee)
                {
                    Attendee oldMasterAttendee = mainConferenceContext.getMasterAttendee();
                    
                    // 主会场的内存数据结构变更
                    mainConferenceContext.setMasterAttendee(attendee);
                    
                    Map<String, Object> data = new HashMap<>();
                    data.put("oldMasterAttendee", oldMasterAttendee);
                    data.put("newMasterAttendee", attendee);
                    WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(mainConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                    
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("主会场已切换至【").append(attendee.getName()).append("】");
                    WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }
        }
    },
    
    /**
     * 广播式轮询
     */
    ROUND_BROADCAST(8600000, 8999999, "广播式轮询")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            if (!(attendee instanceof FmeAttendee))
            {
                BroadcastStatus broadcastStatus = BroadcastStatus.YES;
                if (attendee.isOtherImportance()) {
                    broadcastStatus = BroadcastStatus.NO;
                }
                attendee.setBroadcastStatus(broadcastStatus.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                    if (mainConferenceContext != null)
                    {
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("正在广播式轮询【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName() , attendee.getName())).append("】");
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                        // 发送消息
                        String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLE);
                        if (ConfigConstant.SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLED.equals(showEnable)) {
                            String message = null;
                            AttendeeOperation attendeeOperation = mainConferenceContext.getAttendeeOperation();
                            if (attendeeOperation instanceof PollingAttendeeOpreationImpl) {
                                PollingAttendeeOpreationImpl pollingAttendeeOpreation = (PollingAttendeeOpreationImpl) attendeeOperation;
                                PollingScheme pollingScheme = pollingAttendeeOpreation.getPollingScheme();
                                if ("speakerOnly".equals(pollingScheme.getLayout())) {
                                    message = "【" + attendee.getName() + "】" + "正在被轮询";
                                } else {
                                    message = "正在多画面轮询";
                                }
                            } else if (attendeeOperation instanceof DefaultAttendeeOperation) {
                                message = "正在多画面广播";
                            }
                            if (StringUtils.isNotEmpty(message)) {
                                BeanFactory.getBean(IAttendeeService.class).sendSystemMessage(mainConferenceContext.getId(), message, 5);
                            }
                        }
                    }
                }
            }
        
        }
    },
    
    /**
     * 广播
     */
    BROADCAST(8200000, 8599999, "广播")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            if (!(attendee instanceof FmeAttendee))
            {
                BroadcastStatus broadcastStatus = BroadcastStatus.YES;
                if (attendee.isOtherImportance()) {
                    broadcastStatus = BroadcastStatus.NO;
                }
                attendee.setBroadcastStatus(broadcastStatus.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                if (mainConferenceContext != null)
                {
                    if (attendee.getUpdateMap().size() > 1)
                    {
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("正在广播【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName() , attendee.getName())).append("】");
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                    }
                    // 发送消息
                    String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLE);
                    if (ConfigConstant.SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLED.equals(showEnable)) {
                        String message = null;
                        AttendeeOperation attendeeOperation = mainConferenceContext.getAttendeeOperation();
                        if (attendeeOperation instanceof PollingAttendeeOpreationImpl) {
                            PollingAttendeeOpreationImpl pollingAttendeeOpreation = (PollingAttendeeOpreationImpl) attendeeOperation;
                            PollingScheme pollingScheme = pollingAttendeeOpreation.getPollingScheme();
                            if ("speakerOnly".equals(pollingScheme.getLayout())) {
                                message = "【" + attendee.getName() + "】" + "正在被轮询";
                            } else {
                                message = "正在多画面轮询";
                            }
                        } else if (attendeeOperation instanceof DefaultAttendeeOperation) {
                            message = "正在多画面广播";
                        }
                        if (StringUtils.isNotEmpty(message)) {
                            BeanFactory.getBean(IAttendeeService.class).sendSystemMessage(mainConferenceContext.getId(), message, 5);
                        }
                    }
                }
            }
        }
    },
    
    /**
     * 对话
     */
    TALK(8160000, 81999999, "对话")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            if (!(attendee instanceof FmeAttendee))
            {
                attendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                    if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null && attendee != mainConferenceContext.getMasterAttendee())
                    {
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场正在对话【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName() , attendee.getName())).append("】");
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                    }
                }
            }
        }
    },
    
    /**
     * 点名
     */
    POINT(8000000, 81599999, "点名")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            if (!(attendee instanceof FmeAttendee))
            {
                AttendeeCallTheRollStatus attendeeCallTheRollStatus = AttendeeCallTheRollStatus.YES;
                if (attendee.isOtherImportance()) {
                    attendeeCallTheRollStatus = AttendeeCallTheRollStatus.NO;
                }
                attendee.setCallTheRollStatus(attendeeCallTheRollStatus.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                if (mainConferenceContext != null)
                {
                    if (attendee.getUpdateMap().size() > 1)
                    {
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("正在点名【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName() , attendee.getName())).append("】");
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                    }
                    // 发送消息
                    String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLE);
                    if (ConfigConstant.SHOW_TERMINAL_SYSTEM_MESSAGE_ENABLED.equals(showEnable)) {
                        String message = "【" + attendee.getName() + "】" + "正在被点名";
                        BeanFactory.getBean(IAttendeeService.class).sendSystemMessage(mainConferenceContext.getId(), message, 5);
                    }
                }
            }
        }
    },
    
    /**
     * 选看
     */
    CHOOSE_SEE(6000000, 6999999, "选看")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            if (!(attendee instanceof FmeAttendee))
            {
                AttendeeChooseSeeStatus attendeeChooseSeeStatus = AttendeeChooseSeeStatus.YES;
                ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null && mainConferenceContext.getMasterAttendee().isMeetingJoined()) {
                    attendeeChooseSeeStatus = AttendeeChooseSeeStatus.YES;
                } else {
                    attendeeChooseSeeStatus = AttendeeChooseSeeStatus.NO;
                }
                if (attendee.isOtherImportance()) {
                    attendeeChooseSeeStatus = AttendeeChooseSeeStatus.NO;
                    attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                }
                attendee.setChooseSeeStatus(attendeeChooseSeeStatus.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                if (attendee.isUpCascadeRollCall()) {
                    attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.YES.getValue());
                    attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                } else if (attendee.isUpCascadeBroadcast()) {
                    attendee.setBroadcastStatus(BroadcastStatus.YES.getValue());
                    attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                } else {
                    attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                    attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                }
                if (attendee.getUpdateMap().size() > 1)
                {
                    if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null && mainConferenceContext.getMasterAttendee().isMeetingJoined())
                    {
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场正在选看【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName() , attendee.getName())).append("】");
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                    }
                }
            }
        }
    },
    
    /**
     * 轮询
     */
    ROUND(5000000, 5999999, "轮询")
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            if (!(attendee instanceof FmeAttendee))
            {
                attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
                attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                if (attendee.getUpdateMap().size() > 1)
                {
                    ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(attendee.getContextKey());
                    if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null)
                    {
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("正在轮询【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName() , attendee.getName())).append("】");
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                    }
                }
            }
        }
    },
    
    /**
     * 主FME在子的权重
     */
    UP_FME(7000000, 7000000, "上级FME终端在下级会议中的权重") 
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            
        }
    },
    
    /**
     * 当地州终端是主会场时，省FME权重为4000000
     */
    UP_FME_WHILE_MASTER_IS_SUB(4000000, 4000000, "当地州终端是主会场时，省FME权重为4000000") 
    {
        public void processAttendeeWebsocketMessage(Attendee attendee)
        {
            
        }
    };
    
    /**
     * 起始值
     */
    private Integer startValue;
    
    /**
     * 结束值
     */
    private Integer endValue;
    
    /**
     * 名
     */
    private String name;
    
    AttendeeImportance(Integer startValue, Integer endValue, String name)
    {
        this.startValue = startValue;
        this.endValue = endValue;
        this.name = name;
    }
    
    private static String getViewName(String deptName, String name)
    {
        if (deptName.equals(name))
        {
            return deptName;
        }
        return name + " / " + deptName;
    }
    
    /**
     * <p>
     * Get Method : value int
     * </p>
     * 
     * @return value
     */
    public Integer getStartValue()
    {
        return startValue;
    }
    
    /**
     * <p>Get Method   :   endValue int</p>
     * @return endValue
     */
    public Integer getEndValue()
    {
        return endValue;
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
     * 处理参会者消息
     * @author lilinhai
     * @since 2021-03-09 16:25 
     * @param attendee void
     */
    public abstract void processAttendeeWebsocketMessage(Attendee attendee);
    
    public boolean is(Integer value)
    {
        if (value == null || startValue == null || endValue == null)
        {
            return startValue == value && endValue == value;
        }
        return startValue.intValue() <= value.intValue() && value.intValue() <= endValue.intValue();
    }
    
    public static AttendeeImportance convert(Integer value)
    {
        for (AttendeeImportance attendeeImportance : values())
        {
            if (attendeeImportance.is(value))
            {
                return attendeeImportance;
            }
        }
        return null;
    }
  
}
