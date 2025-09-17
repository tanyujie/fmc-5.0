/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.attendee.model.operation;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.utils.McuKdcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcStopMrMixingResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcUpdateMrMosicConfigResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcVoiceMotivateResponse;
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
    private volatile List<AttendeeForMcuKdc> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-25 14:19
     */
    public DiscussAttendeeOperation(McuKdcConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    @Override
    public void operate() {
        if (conferenceContext.isDiscuss() || isCancel()) {
            return;
        }
        initTargetAttendees();
        operateScreen();
    }

    private void initTargetAttendees() {
        autoPollingAttendeeIdSet.clear();
        autoPollingAttends.clear();
        checkedAttendeeIdSet.clear();

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                autoPollingAttends.add(conferenceContext.getMasterAttendee());
                checkedAttendeeIdSet.add(conferenceContext.getMasterAttendee().getId());
            }
        }

        for (AttendeeForMcuKdc attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuKdc attendee : conferenceContext.getMasterAttendees()) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuKdc> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuKdc attendee : attendees) {
                    if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                        autoPollingAttends.add(attendee);
                        checkedAttendeeIdSet.add(attendee.getId());
                    }
                }
            }
        }
    }

    private void operateScreen() {
        boolean success = false;
        // 全部设为自动分屏
        CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
        ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
        ccUpdateMrMosicConfigRequest.setMode(2);
        ccUpdateMrMosicConfigRequest.setVoice_hint(1);
        ccUpdateMrMosicConfigRequest.setBroadcast(1);
        CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicDiyConfig(ccUpdateMrMosicConfigRequest);
        if (ccUpdateMrMosicConfigResponse != null && ccUpdateMrMosicConfigResponse.isSuccess()) {
            success = true;
        }
        if (success) {
            Set<AttendeeForMcuKdc> updateAttendeeList = new HashSet<>();
            if (runtimeCount == 0) {
                runtimeCount++;
                if (conferenceContext.getMasterAttendee() != null) {
                    AttendeeForMcuKdc attendeeForMcuKdc = conferenceContext.getMasterAttendee();
                    if (attendeeForMcuKdc != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuKdc.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuKdc.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuKdc.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuKdc.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuKdc.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuKdc);
                            if (!exitInUpdate) {
                                attendeeForMcuKdc.resetUpdateMap();
                            }
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuKdc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuKdc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuKdc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuKdc);
                            }
                        }
                    }
                }
                for (AttendeeForMcuKdc attendeeForMcuKdc : conferenceContext.getAttendees()) {
                    if (attendeeForMcuKdc != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuKdc.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuKdc.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuKdc.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuKdc.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuKdc.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuKdc);
                            if (!exitInUpdate) {
                                attendeeForMcuKdc.resetUpdateMap();
                            }
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuKdc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuKdc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuKdc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuKdc);
                            }
                        }
                    }
                }
                if (conferenceContext.getMasterAttendees() != null) {
                    for (AttendeeForMcuKdc attendeeForMcuKdc : conferenceContext.getMasterAttendees()) {
                        if (attendeeForMcuKdc != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuKdc.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuKdc.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuKdc.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuKdc.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuKdc.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuKdc);
                                if (!exitInUpdate) {
                                    attendeeForMcuKdc.resetUpdateMap();
                                }
                                attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuKdc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuKdc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuKdc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuKdc);
                                }
                            }
                        }
                    }
                }
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuKdc> attendeeForMcuKdcList = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    for (AttendeeForMcuKdc attendeeForMcuKdc : attendeeForMcuKdcList) {
                        if (attendeeForMcuKdc != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuKdc.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuKdc.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuKdc.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuKdc.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuKdc.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuKdc);
                                if (!exitInUpdate) {
                                    attendeeForMcuKdc.resetUpdateMap();
                                }
                                attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuKdc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuKdc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuKdc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuKdc);
                                }
                            }
                        }
                    }
                }
            }

            for (AttendeeForMcuKdc attendeeForMcuKdc : updateAttendeeList) {
                if (attendeeForMcuKdc != null) {
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                }
            }

            // 关闭混音
            if (!conferenceContext.isMuteParties()) {
                CcStopMrMixingRequest ccStopMrMixingRequest = new CcStopMrMixingRequest();
                ccStopMrMixingRequest.setConf_id(conferenceContext.getConfId());
                ccStopMrMixingRequest.setMix_id("1");
                CcStopMrMixingResponse ccStopMrMixingResponse = conferenceContext.getConferenceControlApi().stopMrMixing(ccStopMrMixingRequest);
                if (ccStopMrMixingResponse != null && ccStopMrMixingResponse.isSuccess()) {
                    conferenceContext.setMuteParties(true);
                }
            }
            // 开启语音激励
            CcVoiceMotivateRequest ccVoiceMotivateRequest = new CcVoiceMotivateRequest();
            ccVoiceMotivateRequest.setConf_id(conferenceContext.getConfId());
            ccVoiceMotivateRequest.setState(1);
            ccVoiceMotivateRequest.setVacinterval(3);
            CcVoiceMotivateResponse ccVoiceMotivateResponse = conferenceContext.getConferenceControlApi().voiceMotivate(ccVoiceMotivateRequest);
            if (ccVoiceMotivateResponse != null && ccVoiceMotivateResponse.isSuccess()) {
                conferenceContext.setDiscuss(true);
                // 发送提示信息
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);

                McuKdcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                    if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                });
            }
        }
    }

    @Override
    public void cancel() {
        if (!conferenceContext.isEnd()) {
            conferenceContext.setDiscuss(false);
            // 关闭语音激励
            CcVoiceMotivateRequest ccVoiceMotivateRequest = new CcVoiceMotivateRequest();
            ccVoiceMotivateRequest.setConf_id(conferenceContext.getConfId());
            ccVoiceMotivateRequest.setState(0);
            ccVoiceMotivateRequest.setVacinterval(3);
            CcVoiceMotivateResponse ccVoiceMotivateResponse = conferenceContext.getConferenceControlApi().voiceMotivate(ccVoiceMotivateRequest);
            if (ccVoiceMotivateResponse != null && ccVoiceMotivateResponse.isSuccess()) {
            }
        }
    }

}
