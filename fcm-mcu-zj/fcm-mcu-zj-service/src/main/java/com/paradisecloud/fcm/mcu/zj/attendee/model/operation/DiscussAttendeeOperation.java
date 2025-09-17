/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.attendee.model.operation;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrStatusRequest;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.sinhy.spring.BeanFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>讨论操作 TODO </pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-04-25 14:18
 */
public class DiscussAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-04-25 14:19
     */
    private static final long serialVersionUID = 1L;
    private volatile long runtimeCount = 0;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-25 14:19
     */
    public DiscussAttendeeOperation(McuZjConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    @Override
    public void operate() {
        if (conferenceContext.isDiscuss() || isCancel()) {
            return;
        }
        // 全部设为自动分屏
        CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
        List<List<String>> rolesLst = new ArrayList<>();
        List<CcUpdateMrMosicConfigRequest.ConfigInfo> configInfoList = new ArrayList();
        {
            CcUpdateMrMosicConfigRequest.ConfigInfo configInfoSpeaker = new CcUpdateMrMosicConfigRequest.ConfigInfo();
            configInfoSpeaker.setLayout_mode(2);//传统分屏
            configInfoSpeaker.setMosic_id(LayoutTemplates.AUTO.getCode());
            configInfoSpeaker.setRole("speaker");
            configInfoSpeaker.setRoles_lst(rolesLst);
            configInfoSpeaker.setView_has_self(1);
            configInfoSpeaker.setPoll_secs(10);
            configInfoList.add(configInfoSpeaker);
        }
        {
            CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
            configInfoGuest.setLayout_mode(2);//传统分屏
            configInfoGuest.setMosic_id(LayoutTemplates.AUTO.getCode());
            configInfoGuest.setRole("guest");
            configInfoGuest.setRoles_lst(rolesLst);
            configInfoGuest.setView_has_self(1);
            configInfoGuest.setPoll_secs(10);
            configInfoList.add(configInfoGuest);
        }
        ccUpdateMrMosicConfigRequest.setConfig_info(configInfoList);
        boolean success = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequest);
        if (success) {
            Set<AttendeeForMcuZj> updateAttendeeList = new HashSet<>();
            if (runtimeCount == 0) {
                runtimeCount++;
                if (conferenceContext.getMasterAttendee() != null) {
                    AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getMasterAttendee();
                    if (attendeeForMcuZj != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZj.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZj.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZj.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZj.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuZj.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZj);
                            if (!exitInUpdate) {
                                attendeeForMcuZj.resetUpdateMap();
                            }
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuZj.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuZj.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuZj.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuZj);
                            }
                        }
                    }
                }
                for (AttendeeForMcuZj attendeeForMcuZj : conferenceContext.getAttendees()) {
                    if (attendeeForMcuZj != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZj.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZj.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZj.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZj.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuZj.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZj);
                            if (!exitInUpdate) {
                                attendeeForMcuZj.resetUpdateMap();
                            }
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuZj.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuZj.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuZj.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuZj);
                            }
                        }
                    }
                }
                if (conferenceContext.getMasterAttendees() != null) {
                    for (AttendeeForMcuZj attendeeForMcuZj : conferenceContext.getMasterAttendees()) {
                        if (attendeeForMcuZj != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZj.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZj.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZj.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZj.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuZj.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZj);
                                if (!exitInUpdate) {
                                    attendeeForMcuZj.resetUpdateMap();
                                }
                                attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuZj.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuZj.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuZj.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuZj);
                                }
                            }
                        }
                    }
                }
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuZj> attendeeForMcuZjList = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    for (AttendeeForMcuZj attendeeForMcuZj : attendeeForMcuZjList) {
                        if (attendeeForMcuZj != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZj.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZj.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZj.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZj.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuZj.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZj);
                                if (!exitInUpdate) {
                                    attendeeForMcuZj.resetUpdateMap();
                                }
                                attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuZj.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuZj.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuZj.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuZj);
                                }
                            }
                        }
                    }
                }
            }

            for (AttendeeForMcuZj attendeeForMcuZj : updateAttendeeList) {
                if (attendeeForMcuZj != null) {
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                }
            }

            // 全体开麦克风
            CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
            ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_all_guests_mute);
            ccUpdateMrStatusRequest.setMrStatusValue(0);
            success = conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
            if (success) {
                conferenceContext.setDiscuss(true);
                // 发送提示信息
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
            }
        }
    }

    @Override
    public void cancel() {
        if (!conferenceContext.isEnd()) {
            conferenceContext.setDiscuss(false);

            // 全体关闭麦克风
            CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
            ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_all_guests_mute);
            ccUpdateMrStatusRequest.setMrStatusValue(1);
            boolean success = conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
            if (success) {
                // 发送提示信息
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已退出讨论模式！");
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
            }
        }
    }

}
