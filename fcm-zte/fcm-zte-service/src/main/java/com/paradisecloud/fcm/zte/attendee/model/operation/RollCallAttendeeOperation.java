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
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.request.*;
import com.zte.m900.response.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>请加上该类的描述</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-26 15:55
 */
public class RollCallAttendeeOperation extends AttendeeOperation {

    private AttendeeForMcuZte rollCallAttendee;
    private String rollCallUuid = null;
    private volatile List<AttendeeForMcuZte> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param rollCallAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public RollCallAttendeeOperation(McuZteConferenceContext conferenceContext, AttendeeForMcuZte rollCallAttendee) {
        super(conferenceContext);
        this.rollCallAttendee = rollCallAttendee;
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
    }

    private void operateScreen() {
        long lastUpdateTime = getLastUpdateTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - getLastUpdateTime() >= 10000) {
            setLastUpdateTime(currentTimeMillis);
            String uuid = null;
            if (rollCallAttendee != null && StringUtils.isNotEmpty(rollCallAttendee.getParticipantUuid())) {
                uuid = rollCallAttendee.getParticipantUuid();
            }
            if (StringUtils.isNotEmpty(uuid)) {
                if (uuid.equals(rollCallUuid)) {
                    return;
                }
            } else {
                setLastUpdateTime(0);
                return;
            }
            rollCallUuid = uuid;
            if (isCancel()) {
                return;
            }
            boolean success = false;
            AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                //广播主会场
                BoardcastParticipantRequest boardcastParticipantRequest = new BoardcastParticipantRequest();
                boardcastParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                boardcastParticipantRequest.setTerminalIdentifier(rollCallAttendee.getParticipantUuid());
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

                SelectParticipantRequest selectParticipantRequest=new SelectParticipantRequest();
                selectParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                selectParticipantRequest.setSrcTerminalIdentifier(masterAttendee.getParticipantUuid());

                SelectParticipantResponse selectParticipantResponse = conferenceContext.getConferenceControlApi().selectParticipant(selectParticipantRequest);
                if (selectParticipantResponse != null && CommonResponse.STATUS_OK.equals(selectParticipantResponse.getStatus())) {
                    success = true;
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
                if (StringUtils.isNotEmpty(rollCallUuid)) {
                    if (rollCallAttendee != null) {
                        if (!updateAttendeeList.contains(rollCallAttendee)) {
                            rollCallAttendee.resetUpdateMap();
                        }
                        rollCallAttendee.setCallTheRollStatus(AttendeeCallTheRollStatus.YES.getValue());
                        updateAttendeeList.add(rollCallAttendee);
                    }
                }

                if (lastUpdateTime == 0) {
                    // 全体关闭麦克风
                    MuteParticipantRequest muteParticipantRequest = new MuteParticipantRequest();
                    muteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    MuteParticipantResponse muteParticipantResponse = conferenceContext.getConferenceControlApi().muteParticipant(muteParticipantRequest);
                    if (muteParticipantResponse != null && CommonResponse.STATUS_OK.equals(muteParticipantResponse.getStatus())) {
                        Set<String> excludeIdSet = new HashSet<>();
                        excludeIdSet.add(masterAttendee.getParticipantUuid());
                        excludeIdSet.add(rollCallUuid);
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
                    // 打开点名混音
                    {
                        CancelMuteParticipantRequest cancelMuteParticipantRequest = new CancelMuteParticipantRequest();
                        cancelMuteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                        cancelMuteParticipantRequest.setTerminalIdentifier(rollCallAttendee.getParticipantUuid());

                        CancelMuteParticipantResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().cancelMuteParticipant(cancelMuteParticipantRequest);
                        if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {
                            masterAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                        }
                    }
                }

                for (AttendeeForMcuZte attendeeForMcuZte : updateAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        if (attendeeForMcuZte == rollCallAttendee) {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("正在点名【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZte.getDeptId()).getDeptName(), attendeeForMcuZte.getName())).append("】");
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte);
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
                }
            }else {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
                }
            }

        }
    }

    private String getViewName(String deptName, String name) {
        if (deptName.equals(name)) {
            return deptName;
        }
        return name + " / " + deptName;
    }

    @Override
    public void cancel() {
        super.cancel();
        MuteParticipantRequest muteParticipantRequest = new MuteParticipantRequest();
        muteParticipantRequest.setConferenceIdentifier(conferenceContext.getConferenceNumber());
        muteParticipantRequest.setConferenceIdOption("1");
        muteParticipantRequest.setTerminalIdentifier(rollCallAttendee.getParticipantUuid());
        MuteParticipantResponse muteParticipantResponse = conferenceContext.getConferenceControlApi().muteParticipant(muteParticipantRequest);
        if (muteParticipantResponse != null && CommonResponse.STATUS_OK.equals(muteParticipantResponse.getStatus())) {

            rollCallAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, rollCallAttendee);
        }
    }

}
