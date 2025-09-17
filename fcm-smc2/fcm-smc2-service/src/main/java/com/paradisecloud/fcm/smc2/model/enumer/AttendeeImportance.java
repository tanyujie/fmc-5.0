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
package com.paradisecloud.fcm.smc2.model.enumer;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContextCache;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.system.model.SysDeptCache;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>与会者权重</pre>
 * 点名:        [8000000, 8999999]
 * 主会场：      7500000
 * 选看：       [6000000, 6999999]
 * 轮询：       [5000000, 5999999]
 * 省FME权重：   7000000 （当地州终端是主会场时，省FME权重为4000000）
 * 普通会场：    20000
 *
 * @author lilinhai
 * @version V1.0
 * @since 2020-12-29 15:25
 */
public enum AttendeeImportance {
    /**
     * 分会场
     */
    COMMON(null, null, "分会场") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
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
    MASTER(7500000, 7500000, "主会场") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            // 主会场没有选看，广播和轮询三种状态
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
            if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != attendee) {
                AttendeeSmc2 oldMasterAttendee = mainConferenceContext.getMasterAttendee();

                // 主会场的内存数据结构变更
                mainConferenceContext.setMasterAttendee(attendee);

                Map<String, Object> data = new HashMap<>();
                data.put("oldMasterAttendee", oldMasterAttendee);
                data.put("newMasterAttendee", attendee);
                Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(mainConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

                StringBuilder messageTip = new StringBuilder();
                messageTip.append("主会场已切换至【").append(attendee.getName()).append("】");
                Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

            }
        }
    },

    /**
     * 广播式轮询
     */
    ROUND_BROADCAST(8600000, 8999999, "广播式轮询") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            attendee.setBroadcastStatus(BroadcastStatus.YES.getValue());
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            if (attendee.getUpdateMap().size() > 1) {
                Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
                if (mainConferenceContext != null) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("正在广播式轮询【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName(), attendee.getName())).append("】");
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                }
            }
        }
    },

    /**
     * 广播
     */
    BROADCAST(8200000, 8599999, "广播") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            attendee.setBroadcastStatus(BroadcastStatus.YES.getValue());
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            if (attendee.getUpdateMap().size() > 1) {
                Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
                if (mainConferenceContext != null) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("正在广播【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName(), attendee.getName())).append("】");
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                }
            }
        }
    },

    /**
     * 对话
     */
    TALK(8160000, 81999999, "对话") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            attendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            if (attendee.getUpdateMap().size() > 1) {
                Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
                if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null && attendee != mainConferenceContext.getMasterAttendee()) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("主会场正在对话【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName(), attendee.getName())).append("】");
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                }
            }
        }
    },

    /**
     * 点名
     */
    POINT(8000000, 81599999, "点名") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.YES.getValue());
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            if (attendee.getUpdateMap().size() > 1) {
                Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
                if (mainConferenceContext != null) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("正在点名【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName(), attendee.getName())).append("】");
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                }
            }
        }
    },

    /**
     * 选看
     */
    CHOOSE_SEE(6000000, 6999999, "选看") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            if (attendee.getUpdateMap().size() > 1) {
                Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
                if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("主会场正在选看【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName(), attendee.getName())).append("】");
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                }
            }
        }
    },

    /**
     * 轮询
     */
    ROUND(5000000, 5999999, "轮询") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            if (attendee.getUpdateMap().size() > 1) {
                Smc2ConferenceContext mainConferenceContext = Smc2ConferenceContextCache.getInstance().get(attendee.getContextKey());
                if (mainConferenceContext != null && mainConferenceContext.getMasterAttendee() != null) {
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("正在轮询【").append(getViewName(SysDeptCache.getInstance().get(attendee.getDeptId()).getDeptName(), attendee.getName())).append("】");
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());

                }
            }
        }
    },

    /**
     * 主FME在子的权重
     */
    UP_FME(7000000, 7000000, "上级FME终端在下级会议中的权重") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {

        }
    },

    /**
     * 当地州终端是主会场时，省FME权重为4000000
     */
    UP_FME_WHILE_MASTER_IS_SUB(4000000, 4000000, "当地州终端是主会场时，省FME权重为4000000") {
        @Override
        public void processAttendeeWebsocketMessage(AttendeeSmc2 attendee) {

        }
    };

    /**
     * 起始值
     */
    private final Integer startValue;

    /**
     * 结束值
     */
    private final Integer endValue;

    /**
     * 名
     */
    private final String name;

    AttendeeImportance(Integer startValue, Integer endValue, String name) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.name = name;
    }

    private static String getViewName(String deptName, String name) {
        if (deptName.equals(name)) {
            return deptName;
        }
        return name + " / " + deptName;
    }

    public static AttendeeImportance convert(Integer value) {
        for (AttendeeImportance attendeeImportance : values()) {
            if (attendeeImportance.is(value)) {
                return attendeeImportance;
            }
        }
        return null;
    }

    /**
     * <p>
     * Get Method : value int
     * </p>
     *
     * @return value
     */
    public Integer getStartValue() {
        return startValue;
    }

    /**
     * <p>Get Method   :   endValue int</p>
     *
     * @return endValue
     */
    public Integer getEndValue() {
        return endValue;
    }

    /**
     * <p>
     * Get Method : name String
     * </p>
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 处理参会者消息
     *
     * @param attendee void
     * @author lilinhai
     * @since 2021-03-09 16:25
     */
    public abstract void processAttendeeWebsocketMessage(AttendeeSmc2 attendee);

    public boolean is(Integer value) {
        if (value == null || startValue == null || endValue == null) {
            return startValue == value && endValue == value;
        }
        return startValue.intValue() <= value.intValue() && value.intValue() <= endValue.intValue();
    }

}
