/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.plc.attendee.model.operation;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.plc.attendee.utils.McuPlcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdateMrAudioResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdateMrAutoMosicConfigResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdatePersonalMosicTypeResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdateTerminalAudioAndVideoResponse;
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
    private volatile List<AttendeeForMcuPlc> autoPollingAttends = new ArrayList<>();
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
    public DiscussAttendeeOperation(McuPlcConferenceContext conferenceContext) {
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

        for (AttendeeForMcuPlc attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuPlc attendee : conferenceContext.getMasterAttendees()) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuPlc> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuPlc attendee : attendees) {
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
        CcUpdateMrAutoMosicConfigRequest ccUpdateMrAutoMosicConfigRequest = new CcUpdateMrAutoMosicConfigRequest();
        ccUpdateMrAutoMosicConfigRequest.setId(conferenceContext.getConfId());
        ccUpdateMrAutoMosicConfigRequest.setAuto_layout(true);
        CcUpdateMrAutoMosicConfigResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(ccUpdateMrAutoMosicConfigRequest);
        if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
            success = true;
        }
        if (success) {
            if (!conferenceContext.isDiscuss()) {
                for (AttendeeForMcuPlc attendeeForMcuPlc : autoPollingAttends) {
                    if (StringUtils.isNotEmpty(attendeeForMcuPlc.getParticipantUuid())) {
                        if (attendeeForMcuPlc.isPersonalLayout()) {
                            CcUpdatePersonalMosicTypeRequest ccUpdatePersonalMosicTypeRequest = new CcUpdatePersonalMosicTypeRequest();
                            ccUpdatePersonalMosicTypeRequest.setId(conferenceContext.getConfId());
                            ccUpdatePersonalMosicTypeRequest.setParty_id(attendeeForMcuPlc.getParticipantUuid());
                            ccUpdatePersonalMosicTypeRequest.setLayout_type("conference");
                            CcUpdatePersonalMosicTypeResponse ccUpdatePersonalMosicTypeResponse = conferenceContext.getConferenceControlApi().updatePersonalMosicType(ccUpdatePersonalMosicTypeRequest);
                            if (ccUpdatePersonalMosicTypeResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicTypeResponse.getStatus())) {
                                attendeeForMcuPlc.setPersonalLayout(false);
                            }
                        }
                    }
                }
            }
        }
        if (success) {
            Set<AttendeeForMcuPlc> updateAttendeeList = new HashSet<>();
            if (runtimeCount == 0) {
                runtimeCount++;
                if (conferenceContext.getMasterAttendee() != null) {
                    AttendeeForMcuPlc attendeeForMcuPlc = conferenceContext.getMasterAttendee();
                    if (attendeeForMcuPlc != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuPlc.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuPlc.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuPlc.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuPlc.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuPlc.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuPlc);
                            if (!exitInUpdate) {
                                attendeeForMcuPlc.resetUpdateMap();
                            }
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuPlc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuPlc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuPlc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuPlc);
                            }
                        }
                    }
                }
                for (AttendeeForMcuPlc attendeeForMcuPlc : conferenceContext.getAttendees()) {
                    if (attendeeForMcuPlc != null) {
                        if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuPlc.getRoundRobinStatus()
                                || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuPlc.getChooseSeeStatus()
                                || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuPlc.getCallTheRollStatus()
                                || AttendeeTalkStatus.YES.getValue() == attendeeForMcuPlc.getTalkStatus()
                                || BroadcastStatus.YES.getValue() == attendeeForMcuPlc.getBroadcastStatus()) {
                            boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuPlc);
                            if (!exitInUpdate) {
                                attendeeForMcuPlc.resetUpdateMap();
                            }
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            attendeeForMcuPlc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                            attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            attendeeForMcuPlc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                            attendeeForMcuPlc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                            if (!exitInUpdate) {
                                updateAttendeeList.add(attendeeForMcuPlc);
                            }
                        }
                    }
                }
                if (conferenceContext.getMasterAttendees() != null) {
                    for (AttendeeForMcuPlc attendeeForMcuPlc : conferenceContext.getMasterAttendees()) {
                        if (attendeeForMcuPlc != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuPlc.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuPlc.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuPlc.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuPlc.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuPlc.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuPlc);
                                if (!exitInUpdate) {
                                    attendeeForMcuPlc.resetUpdateMap();
                                }
                                attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuPlc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuPlc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuPlc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuPlc);
                                }
                            }
                        }
                    }
                }
                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuPlc> attendeeForMcuPlcList = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    for (AttendeeForMcuPlc attendeeForMcuPlc : attendeeForMcuPlcList) {
                        if (attendeeForMcuPlc != null) {
                            if (AttendeeRoundRobinStatus.YES.getValue() == attendeeForMcuPlc.getRoundRobinStatus()
                                    || AttendeeChooseSeeStatus.YES.getValue() == attendeeForMcuPlc.getChooseSeeStatus()
                                    || AttendeeCallTheRollStatus.YES.getValue() == attendeeForMcuPlc.getCallTheRollStatus()
                                    || AttendeeTalkStatus.YES.getValue() == attendeeForMcuPlc.getTalkStatus()
                                    || BroadcastStatus.YES.getValue() == attendeeForMcuPlc.getBroadcastStatus()) {
                                boolean exitInUpdate = updateAttendeeList.contains(attendeeForMcuPlc);
                                if (!exitInUpdate) {
                                    attendeeForMcuPlc.resetUpdateMap();
                                }
                                attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                attendeeForMcuPlc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                                attendeeForMcuPlc.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                attendeeForMcuPlc.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                if (!exitInUpdate) {
                                    updateAttendeeList.add(attendeeForMcuPlc);
                                }
                            }
                        }
                    }
                }
            }

            for (AttendeeForMcuPlc attendeeForMcuPlc : updateAttendeeList) {
                if (attendeeForMcuPlc != null) {
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                }
            }

            if (!conferenceContext.isMuteParties()) {
                CcUpdateMrAudioRequest ccUpdateMrAudioRequest = new CcUpdateMrAudioRequest();
                ccUpdateMrAudioRequest.setId(conferenceContext.getConfId());
                ccUpdateMrAudioRequest.setAudio_mute(true);
                CcUpdateMrAudioResponse ccUpdateMrAudioResponse = conferenceContext.getConferenceControlApi().updateMrAudio(ccUpdateMrAudioRequest);
                if (ccUpdateMrAudioResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAudioResponse.getStatus())) {
                }
            }
            // 全体开麦克风
            CcUpdateMrAudioRequest ccUpdateMrAudioRequest = new CcUpdateMrAudioRequest();
            ccUpdateMrAudioRequest.setId(conferenceContext.getConfId());
            ccUpdateMrAudioRequest.setAudio_mute(false);
            CcUpdateMrAudioResponse ccUpdateMrAudioResponse = conferenceContext.getConferenceControlApi().updateMrAudio(ccUpdateMrAudioRequest);
            if (ccUpdateMrAudioResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAudioResponse.getStatus())) {
                conferenceContext.setDiscuss(true);
                // 发送提示信息
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);

                McuPlcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                    if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    }
                });
            }
        }
    }

    @Override
    public void cancel() {
        if (!conferenceContext.isEnd()) {
            conferenceContext.setDiscuss(false);

            AttendeeForMcuPlc masterAttendee = conferenceContext.getMasterAttendee();
            if (conferenceContext.isMuteParties()) {
                CcUpdateMrAudioRequest ccUpdateMrAudioRequest = new CcUpdateMrAudioRequest();
                ccUpdateMrAudioRequest.setId(conferenceContext.getConfId());
                ccUpdateMrAudioRequest.setAudio_mute(false);
                CcUpdateMrAudioResponse ccUpdateMrAudioResponse = conferenceContext.getConferenceControlApi().updateMrAudio(ccUpdateMrAudioRequest);
                if (ccUpdateMrAudioResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAudioResponse.getStatus())) {
                }
            }
            // 全体关闭麦克风
            CcUpdateMrAudioRequest ccUpdateMrAudioRequest = new CcUpdateMrAudioRequest();
            ccUpdateMrAudioRequest.setId(conferenceContext.getConfId());
            ccUpdateMrAudioRequest.setAudio_mute(true);
            CcUpdateMrAudioResponse ccUpdateMrAudioResponse = conferenceContext.getConferenceControlApi().updateMrAudio(ccUpdateMrAudioRequest);
            if (ccUpdateMrAudioResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAudioResponse.getStatus())) {
                // 发送提示信息
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已退出讨论模式！");
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());

                Set<String> excludeIdSet = new HashSet<>();
                if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                    excludeIdSet.add(masterAttendee.getParticipantUuid());
                }
                McuPlcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                    if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                        if (!excludeIdSet.contains(attendee.getId())) {
                            attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                        }
                    }
                });
            }
            // 打开主会场混音
            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                CcUpdateTerminalAudioAndVideoRequest ccUpdateTerminalAudioAndVideoRequest = new CcUpdateTerminalAudioAndVideoRequest();
                ccUpdateTerminalAudioAndVideoRequest.setId(conferenceContext.getConfId());
                ccUpdateTerminalAudioAndVideoRequest.setParty_id(masterAttendee.getParticipantUuid());
                ccUpdateTerminalAudioAndVideoRequest.setAudio_mute(false);
                if (masterAttendee.getVideoStatus() == AttendeeVideoStatus.YES.getValue()) {
                    ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(false);
                } else {
                    ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(true);
                }
                CcUpdateTerminalAudioAndVideoResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().updateTerminalAudioAndVideo(ccUpdateTerminalAudioAndVideoRequest);
                if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {

                    masterAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                }
            }
        }
    }

}
