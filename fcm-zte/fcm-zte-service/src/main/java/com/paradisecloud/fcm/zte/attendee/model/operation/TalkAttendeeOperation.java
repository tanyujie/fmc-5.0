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
public class TalkAttendeeOperation extends AttendeeOperation {

    private AttendeeForMcuZte talkAttendee;
    private String talkUuid = null;
    private volatile List<AttendeeForMcuZte> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param talkAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public TalkAttendeeOperation(McuZteConferenceContext conferenceContext, AttendeeForMcuZte talkAttendee) {
        super(conferenceContext);
        this.talkAttendee = talkAttendee;
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
            if (!talkAttendee.isMeetingJoined()) {
                if (conferenceContext.getAttendeeOperation() == this) {
                    conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
                    this.cancel();
                }
                setLastUpdateTime(0);
                return;
            }
            String uuid = null;
            if (talkAttendee != null && StringUtils.isNotEmpty(talkAttendee.getParticipantUuid())) {
                uuid = talkAttendee.getParticipantUuid();
            }
            if (StringUtils.isNotEmpty(uuid)) {
                if (uuid.equals(talkUuid)) {
                    return;
                }
            } else {
                setLastUpdateTime(0);
                return;
            }
            talkUuid = uuid;

            if (isCancel()) {
                return;
            }
            boolean success = false;
            AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                // 主会场
                //广播主会场
                BoardcastParticipantRequest boardcastParticipantRequest = new BoardcastParticipantRequest();
                boardcastParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                boardcastParticipantRequest.setTerminalIdentifier(masterAttendee.getParticipantUuid());
                conferenceContext.getConferenceControlApi().boardcastParticipant(boardcastParticipantRequest);

                SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                ccSwitchMultiCtrlModeRequest.setMultiViewMode("manual");
                SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
                if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                    //设置多画面
                    SetMultiViewNumRequest setMultiViewNumRequest=new SetMultiViewNumRequest();
                    setMultiViewNumRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    setMultiViewNumRequest.setMultiViewNum(2);
                    setMultiViewNumRequest.setLayout(0);
                    SetMultiViewNumResponse setMultiViewNumResponse = conferenceContext.getConferenceControlApi().setMultiViewNum(setMultiViewNumRequest);

                    MultiViewSelectRequest multiViewSelectRequest=new MultiViewSelectRequest();
                    multiViewSelectRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    multiViewSelectRequest.setViewNo(0);
                    multiViewSelectRequest.setTerminalIdentifier(masterAttendee.getParticipantUuid());
                    MultiViewSelectResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().multiViewSelect(multiViewSelectRequest);
                    if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                        success = true;
                    }

                    MultiViewSelectRequest multiViewSelectRequest2=new MultiViewSelectRequest();
                    multiViewSelectRequest2.setConferenceIdentifier(conferenceContext.getConfId());
                    multiViewSelectRequest2.setViewNo(1);
                    multiViewSelectRequest2.setTerminalIdentifier(talkAttendee.getParticipantUuid());
                    MultiViewSelectResponse ccUpdatePersonalMosicConfigResponse2 = conferenceContext.getConferenceControlApi().multiViewSelect(multiViewSelectRequest2);
                    if (ccUpdatePersonalMosicConfigResponse2 != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse2.getStatus())) {
                        success = true;
                    }


                    //广播源看单画面，其他会场看多画面
                    SelectMultiOrSingleViewRequest selectMultiOrSingleViewRequest=new SelectMultiOrSingleViewRequest();
                    selectMultiOrSingleViewRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    selectMultiOrSingleViewRequest.setCmdType("AllMultiView");
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
                if (StringUtils.isNotEmpty(talkUuid)) {
                    if (talkAttendee != null) {
                        if (!updateAttendeeList.contains(talkAttendee)) {
                            talkAttendee.resetUpdateMap();
                        }
                        talkAttendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
                        updateAttendeeList.add(talkAttendee);
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场正在对话【").append(talkAttendee.getName()).append("】");
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    }
                }

                for (AttendeeForMcuZte attendeeForMcuZte : updateAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    }
                }


                // 全体关闭麦克风
                MuteParticipantRequest muteParticipantRequest = new MuteParticipantRequest();
                muteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());

                MuteParticipantResponse muteParticipantResponse = conferenceContext.getConferenceControlApi().muteParticipant(muteParticipantRequest);
                if (muteParticipantResponse != null && CommonResponse.STATUS_OK.equals(muteParticipantResponse.getResult())) {
                    Set<String> excludeIdSet = new HashSet<>();
                    excludeIdSet.add(masterAttendee.getParticipantUuid());
                    excludeIdSet.add(talkUuid);
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
                // 打开对话人混音
                {
                    CancelMuteParticipantRequest cancelMuteParticipantRequest = new CancelMuteParticipantRequest();
                    cancelMuteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    cancelMuteParticipantRequest.setTerminalIdentifier(talkAttendee.getParticipantUuid());
                    CancelMuteParticipantResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().cancelMuteParticipant(cancelMuteParticipantRequest);
                    if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {

                        masterAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                    }
                }
                if (success) {
                    // 发送提示信息
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, AttendeeMixingStatus.YES.getName());
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("主会场正在对话【").append(getViewName(SysDeptCache.getInstance().get(talkAttendee.getDeptId()).getDeptName() , talkAttendee.getName())).append("】");
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
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

    public String getTalkUuid() {
        return this.talkUuid;
    }

    @Override
    public void cancel() {
        super.cancel();
        MuteParticipantRequest muteParticipantRequest = new MuteParticipantRequest();
        muteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
        muteParticipantRequest.setConferenceIdentifier(talkAttendee.getParticipantUuid());
        MuteParticipantResponse muteParticipantResponse = conferenceContext.getConferenceControlApi().muteParticipant(muteParticipantRequest);
        if (muteParticipantResponse != null && CommonResponse.STATUS_OK.equals(muteParticipantResponse.getStatus())) {

            talkAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, talkAttendee.getUpdateMap());
        }
    }

}
