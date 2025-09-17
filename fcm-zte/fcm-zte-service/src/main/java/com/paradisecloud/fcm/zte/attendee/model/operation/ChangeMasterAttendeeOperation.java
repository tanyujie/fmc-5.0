package com.paradisecloud.fcm.zte.attendee.model.operation;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.zte.attendee.utils.McuZteConferenceContextUtils;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.request.*;
import com.zte.m900.response.CancelMuteParticipantResponse;
import com.zte.m900.response.MuteParticipantResponse;
import com.zte.m900.response.SelectMultiOrSingleViewResponse;
import com.zte.m900.response.SwitchMultiViewCtrlModeResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private volatile List<AttendeeForMcuZte> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    private volatile AttendeeForMcuZte defaultChooseSeeAttendee = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChangeMasterAttendeeOperation(McuZteConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() {
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

        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuZte attendee : conferenceContext.getMasterAttendees()) {
                if (attendee.isMeetingJoined()) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (AttendeeForMcuZte attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuZte> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuZte attendee : attendees) {
                    if (attendee.isMeetingJoined()) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }
    }

    private void operateScreen() {
        long lastUpdateTime = getLastUpdateTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - getLastUpdateTime() >= 10000) {
            setLastUpdateTime(currentTimeMillis);
            if (isCancel()) {
                return;
            }
            boolean success = false;
            // 观众看主会场，主会场看分屏观众。
            AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
            if (masterAttendee != null && masterAttendee.isMeetingJoined()) {

                //广播主会场
                BoardcastParticipantRequest boardcastParticipantRequest = new BoardcastParticipantRequest();
                boardcastParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                boardcastParticipantRequest.setTerminalIdentifier(masterAttendee.getParticipantUuid());
                conferenceContext.getConferenceControlApi().boardcastParticipant(boardcastParticipantRequest);
                // 有主会场时，观众看主会场
                SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                ccSwitchMultiCtrlModeRequest.setMultiViewMode("manual");
                SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
                if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                    //所有会场看单画面
                    SelectMultiOrSingleViewRequest selectMultiOrSingleViewRequest=new SelectMultiOrSingleViewRequest();
                    selectMultiOrSingleViewRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    selectMultiOrSingleViewRequest.setCmdType("AllSingleView");
                    SelectMultiOrSingleViewResponse selectMultiOrSingleViewResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(selectMultiOrSingleViewRequest);
                    if(selectMultiOrSingleViewResponse!=null&& CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())){
                        success=true;
                    }
                }


            } else {
                // 有主会场时，观众看主会场
                SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                ccSwitchMultiCtrlModeRequest.setMultiViewMode("manual");
                SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
                if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                    //所有会场看单画面
                    SelectMultiOrSingleViewRequest selectMultiOrSingleViewRequest=new SelectMultiOrSingleViewRequest();
                    selectMultiOrSingleViewRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    selectMultiOrSingleViewRequest.setCmdType("AllSingleView");
                    SelectMultiOrSingleViewResponse selectMultiOrSingleViewResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(selectMultiOrSingleViewRequest);
                    if(selectMultiOrSingleViewResponse!=null&& CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())){
                        success=true;
                    }
                }
            }
            if (!success) {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
                }
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
                if (defaultChooseSeeAttendee != null) {
                    if (!updateAttendeeList.contains(defaultChooseSeeAttendee)) {
                        defaultChooseSeeAttendee.resetUpdateMap();
                    }
                    defaultChooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                    updateAttendeeList.add(defaultChooseSeeAttendee);
                }

                for (AttendeeForMcuZte attendeeForMcuZte : updateAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
                }
                if (lastUpdateTime == 0) {

                    // 全体关闭麦克风
                    MuteParticipantRequest muteParticipantRequest = new MuteParticipantRequest();
                    muteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    MuteParticipantResponse ccUpdateMrAudioResponse = conferenceContext.getConferenceControlApi().muteParticipant(muteParticipantRequest);
                    if (ccUpdateMrAudioResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAudioResponse.getStatus())) {
                        Set<String> excludeIdSet = new HashSet<>();
                        excludeIdSet.add(masterAttendee.getParticipantUuid());
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
                    {
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
    }
}
