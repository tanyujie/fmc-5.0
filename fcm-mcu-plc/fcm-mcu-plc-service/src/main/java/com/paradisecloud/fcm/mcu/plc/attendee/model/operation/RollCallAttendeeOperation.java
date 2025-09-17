package com.paradisecloud.fcm.mcu.plc.attendee.model.operation;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.plc.attendee.utils.McuPlcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.CellInfo;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.model.enumer.McuPlcLayoutTemplates;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.*;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;

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

    private AttendeeForMcuPlc rollCallAttendee;
    private String rollCallUuid = null;
    private volatile List<AttendeeForMcuPlc> autoPollingAttends = new ArrayList<>();
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
    public RollCallAttendeeOperation(McuPlcConferenceContext conferenceContext, AttendeeForMcuPlc rollCallAttendee) {
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
            AttendeeForMcuPlc masterAttendee = conferenceContext.getMasterAttendee();
            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                // 主会场
                CcUpdatePersonalMosicConfigRequest ccUpdatePersonalMosicConfigRequest = new CcUpdatePersonalMosicConfigRequest();
                ccUpdatePersonalMosicConfigRequest.setId(conferenceContext.getConfId());
                ccUpdatePersonalMosicConfigRequest.setParty_id(masterAttendee.getParticipantUuid());
                ccUpdatePersonalMosicConfigRequest.setLayout_type("personal");
                ccUpdatePersonalMosicConfigRequest.setLayout(McuPlcLayoutTemplates.SCREEN_1.getCode());
                List<CellInfo> cellInfoList = new ArrayList<>();
                CellInfo cellInfo = new CellInfo();
                cellInfo.setId("1");
                cellInfo.setForceStatus("forced");
                cellInfo.setForceId(rollCallUuid);
                cellInfo.setSourceId(rollCallUuid);
                cellInfoList.add(cellInfo);
                ccUpdatePersonalMosicConfigRequest.setCellInfoList(cellInfoList);
                CcUpdatePersonalMosicConfigResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().updatePersonalMosicConfig(ccUpdatePersonalMosicConfigRequest);
                if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                    success = true;
                }
            }
            {
                // 观众
                CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequestForGuest = new CcUpdateMrMosicConfigRequest();
                ccUpdateMrMosicConfigRequestForGuest.setId(conferenceContext.getConfId());
                ccUpdateMrMosicConfigRequestForGuest.setLayout(McuPlcLayoutTemplates.SCREEN_1.getCode());
                List<CellInfo> cellInfoListForGuest = new ArrayList<>();
                CellInfo cellInfo = new CellInfo();
                cellInfo.setId("1");
                cellInfo.setForceStatus("forced");
                cellInfo.setForceId(rollCallUuid);
                cellInfo.setSourceId(rollCallUuid);
                cellInfoListForGuest.add(cellInfo);
                ccUpdateMrMosicConfigRequestForGuest.setCellInfoList(cellInfoListForGuest);

                CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequestForGuest);
                if (ccUpdateMrMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrMosicConfigResponse.getStatus())) {
                    success = true;
                }
            }
            {
                // 嘉宾
                CcUpdatePersonalMosicConfigRequest ccUpdatePersonalMosicConfigRequest = new CcUpdatePersonalMosicConfigRequest();
                ccUpdatePersonalMosicConfigRequest.setId(conferenceContext.getConfId());
                ccUpdatePersonalMosicConfigRequest.setParty_id(rollCallUuid);
                ccUpdatePersonalMosicConfigRequest.setLayout_type("personal");
                ccUpdatePersonalMosicConfigRequest.setLayout(McuPlcLayoutTemplates.SCREEN_1.getCode());
                List<CellInfo> cellInfoList = new ArrayList<>();
                CellInfo cellInfo = new CellInfo();
                cellInfo.setId("1");
                cellInfo.setForceStatus("forced");
                cellInfo.setForceId(masterAttendee.getParticipantUuid());
                cellInfo.setSourceId(masterAttendee.getParticipantUuid());
                cellInfoList.add(cellInfo);
                ccUpdatePersonalMosicConfigRequest.setCellInfoList(cellInfoList);
                CcUpdatePersonalMosicConfigResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().updatePersonalMosicConfig(ccUpdatePersonalMosicConfigRequest);
                if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                    success = true;
                }
            }
            if (success) {
                if (lastUpdateTime == 0) {
                    for (AttendeeForMcuPlc attendeeForMcuPlc : autoPollingAttends) {
                        if (attendeeForMcuPlc != null && StringUtils.isNotEmpty(attendeeForMcuPlc.getParticipantUuid())) {
                            if (rollCallUuid.equals(attendeeForMcuPlc.getParticipantUuid())) {
                                continue;
                            }
                            if (masterAttendee != null && attendeeForMcuPlc.getParticipantUuid().contains(masterAttendee.getParticipantUuid())) {
                                continue;
                            }
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
            } else {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
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
                        Set<String> excludeIdSet = new HashSet<>();
                        excludeIdSet.add(masterAttendee.getParticipantUuid());
                        excludeIdSet.add(rollCallUuid);
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
                    {
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
                            success = true;

                            masterAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee.getUpdateMap());
                        }
                    }
                    // 打开点名混音
                    {
                        CcUpdateTerminalAudioAndVideoRequest ccUpdateTerminalAudioAndVideoRequest = new CcUpdateTerminalAudioAndVideoRequest();
                        ccUpdateTerminalAudioAndVideoRequest.setId(conferenceContext.getConfId());
                        ccUpdateTerminalAudioAndVideoRequest.setParty_id(rollCallUuid);
                        ccUpdateTerminalAudioAndVideoRequest.setAudio_mute(false);
                        if (rollCallAttendee.getVideoStatus() == AttendeeVideoStatus.YES.getValue()) {
                            ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(false);
                        } else {
                            ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(true);
                        }
                        CcUpdateTerminalAudioAndVideoResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().updateTerminalAudioAndVideo(ccUpdateTerminalAudioAndVideoRequest);
                        if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {
                            success = true;

                            rollCallAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, rollCallAttendee.getUpdateMap());
                        }
                    }
                }

                for (AttendeeForMcuPlc attendeeForMcuPlc : updateAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        if (attendeeForMcuPlc == rollCallAttendee) {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("正在点名【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuPlc.getDeptId()).getDeptName(), attendeeForMcuPlc.getName())).append("】");
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
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
        // 关闭点名混音
        CcUpdateTerminalAudioAndVideoRequest ccUpdateTerminalAudioAndVideoRequest = new CcUpdateTerminalAudioAndVideoRequest();
        ccUpdateTerminalAudioAndVideoRequest.setId(conferenceContext.getConfId());
        ccUpdateTerminalAudioAndVideoRequest.setParty_id(rollCallUuid);
        ccUpdateTerminalAudioAndVideoRequest.setAudio_mute(true);
        if (rollCallAttendee.getVideoStatus() == AttendeeVideoStatus.YES.getValue()) {
            ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(false);
        } else {
            ccUpdateTerminalAudioAndVideoRequest.setVideo_mute(true);
        }
        CcUpdateTerminalAudioAndVideoResponse ccUpdateTerminalAudioAndVideoResponse = conferenceContext.getConferenceControlApi().updateTerminalAudioAndVideo(ccUpdateTerminalAudioAndVideoRequest);
        if (ccUpdateTerminalAudioAndVideoResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateTerminalAudioAndVideoResponse.getStatus())) {

            rollCallAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, rollCallAttendee.getUpdateMap());
        }
    }

}
