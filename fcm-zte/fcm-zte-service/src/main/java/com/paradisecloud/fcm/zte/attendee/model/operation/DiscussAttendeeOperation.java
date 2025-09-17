/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.attendee.model.operation;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.zte.attendee.utils.McuZteConferenceContextUtils;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.request.CancelMuteParticipantRequest;
import com.zte.m900.request.MuteParticipantRequest;
import com.zte.m900.request.SwitchMultiViewCtrlModeRequest;
import com.zte.m900.response.CancelMuteParticipantResponse;
import com.zte.m900.response.MuteParticipantResponse;
import com.zte.m900.response.SwitchMultiViewCtrlModeResponse;

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
    private volatile List<AttendeeForMcuZte> autoPollingAttends = new ArrayList<>();
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
    public DiscussAttendeeOperation(McuZteConferenceContext conferenceContext) {
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

        for (AttendeeForMcuZte attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuZte attendee : conferenceContext.getMasterAttendees()) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuZte> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuZte attendee : attendees) {
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
        SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
        ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
        ccSwitchMultiCtrlModeRequest.setMultiViewMode("auto");
        SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
        if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
            success=true;
        }

        if (success) {
            Set<AttendeeForMcuZte> updateAttendeeList = new HashSet<>();
            if (runtimeCount == 0) {
                runtimeCount++;
                if (conferenceContext.getMasterAttendee() != null) {
                    AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getMasterAttendee();
                    if (attendeeForMcuZte != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZte.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZte.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZte.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZte.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuZte.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZte);
                            if (!exitInUpdate) {
                                attendeeForMcuZte.resetUpdateMap();
                            }
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuZte.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuZte.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuZte.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuZte);
                            }
                        }
                    }
                }
                for (AttendeeForMcuZte attendeeForMcuZte : conferenceContext.getAttendees()) {
                    if (attendeeForMcuZte != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZte.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZte.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZte.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZte.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuZte.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZte);
                            if (!exitInUpdate) {
                                attendeeForMcuZte.resetUpdateMap();
                            }
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuZte.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuZte.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuZte.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuZte);
                            }
                        }
                    }
                }
                if (conferenceContext.getMasterAttendees() != null) {
                    for (AttendeeForMcuZte attendeeForMcuZte : conferenceContext.getMasterAttendees()) {
                        if (attendeeForMcuZte != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZte.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZte.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZte.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZte.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuZte.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZte);
                                if (!exitInUpdate) {
                                    attendeeForMcuZte.resetUpdateMap();
                                }
                                attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuZte.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuZte.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuZte.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuZte);
                                }
                            }
                        }
                    }
                }
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuZte> attendeeForMcuZteList = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    for (AttendeeForMcuZte attendeeForMcuZte : attendeeForMcuZteList) {
                        if (attendeeForMcuZte != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuZte.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuZte.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuZte.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuZte.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuZte.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuZte);
                                if (!exitInUpdate) {
                                    attendeeForMcuZte.resetUpdateMap();
                                }
                                attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuZte.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuZte.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuZte.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuZte);
                                }
                            }
                        }
                    }
                }
            }

            for (AttendeeForMcuZte attendeeForMcuZte : updateAttendeeList) {
                if (attendeeForMcuZte != null) {
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                }
            }

            // 全体开麦克风
            CancelMuteParticipantRequest cancelMuteParticipantRequest = new CancelMuteParticipantRequest();
            cancelMuteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
            CancelMuteParticipantResponse cancelMuteParticipant = conferenceContext.getConferenceControlApi().cancelMuteParticipant(cancelMuteParticipantRequest);
            if (cancelMuteParticipant != null && CommonResponse.STATUS_OK.equals(cancelMuteParticipant.getStatus())) {
                conferenceContext.setDiscuss(true);
                // 发送提示信息
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);

                McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                    if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                });
            }
        }
    }

    @Override
    public void cancel() {
        if (!conferenceContext.isEnd()) {
            conferenceContext.setDiscuss(false);

            AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();

            // 全体关闭麦克风
            MuteParticipantRequest muteParticipantRequest = new MuteParticipantRequest();
            muteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());

            MuteParticipantResponse muteParticipantResponse = conferenceContext.getConferenceControlApi().muteParticipant(muteParticipantRequest);
            if (muteParticipantResponse != null && CommonResponse.STATUS_OK.equals(muteParticipantResponse.getStatus())) {
                // 发送提示信息
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已退出讨论模式！");
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());

                Set<String> excludeIdSet = new HashSet<>();
                if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                    excludeIdSet.add(masterAttendee.getParticipantUuid());
                }
                McuZteConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                    if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                        if (!excludeIdSet.contains(attendee.getId())) {
                            attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                        }
                    }
                });
            }
            // 打开主会场混音
            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                CancelMuteParticipantRequest cancelMuteParticipantRequest = new CancelMuteParticipantRequest();
                cancelMuteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                cancelMuteParticipantRequest.setTerminalIdentifier(masterAttendee.getParticipantUuid());
                CancelMuteParticipantResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().cancelMuteParticipant(cancelMuteParticipantRequest);
                if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {

                    masterAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                }
            }
        }
    }

}
