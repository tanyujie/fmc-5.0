package com.paradisecloud.fcm.mcu.kdc.attendee.model.operation;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.CellInfo;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.kdc.model.enumer.McuKdcLayoutTemplates;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.*;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.sinhy.spring.BeanFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private volatile List<AttendeeForMcuKdc> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    private volatile AttendeeForMcuKdc defaultChooseSeeAttendee = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChangeMasterAttendeeOperation(McuKdcConferenceContext conferenceContext) {
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

        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuKdc attendee : conferenceContext.getMasterAttendees()) {
                if (attendee.isMeetingJoined()) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (AttendeeForMcuKdc attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuKdc> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuKdc attendee : attendees) {
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
            AttendeeForMcuKdc masterAttendee = conferenceContext.getMasterAttendee();
            if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
                // 有主会场时，观众看主会场
                // 设置主席
                CcSetChairmanMrTerminalRequest ccSetChairmanMrTerminalRequest = new CcSetChairmanMrTerminalRequest();
                ccSetChairmanMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                ccSetChairmanMrTerminalRequest.setMt_id(masterAttendee.getParticipantUuid());
                CcSetChairmanMrTerminalResponse ccSetChairmanMrTerminalResponse = conferenceContext.getConferenceControlApi().setMrChairman(ccSetChairmanMrTerminalRequest);
                if (ccSetChairmanMrTerminalResponse != null && ccSetChairmanMrTerminalResponse.isSuccess()) {
                }
                // 设置发言人
                CcSetSpeakerMrTerminalRequest ccSetSpeakerMrTerminalRequest = new CcSetSpeakerMrTerminalRequest();
                ccSetSpeakerMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                ccSetSpeakerMrTerminalRequest.setMt_id(masterAttendee.getParticipantUuid());
                ccSetSpeakerMrTerminalRequest.setForce_broadcast(1);
                CcSetSpeakerMrTerminalResponse ccSetSpeakerMrTerminalResponse = conferenceContext.getConferenceControlApi().setMrSpeaker(ccSetSpeakerMrTerminalRequest);
                if (ccSetSpeakerMrTerminalResponse != null && ccSetSpeakerMrTerminalResponse.isSuccess()) {
                }

                // 设置1分屏
                if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                    CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
                    ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
                    ccUpdateMrMosicConfigRequest.setMode(1);
                    ccUpdateMrMosicConfigRequest.setLayout(Integer.valueOf(McuKdcLayoutTemplates.SCREEN_1.getCode()));
                    ccUpdateMrMosicConfigRequest.setVoice_hint(1);
                    ccUpdateMrMosicConfigRequest.setBroadcast(0);
                    List<CellInfo> members = new ArrayList<>();
                    {
                        CellInfo member = new CellInfo();
                        member.setChn_idx(0);
                        member.setMember_type(1);
                        member.setMt_id(defaultChooseSeeAttendee.getParticipantUuid());
                        member.setMt_chn_idx(0);
                        members.add(member);
                    }
                    ccUpdateMrMosicConfigRequest.setMembers(members);
                    CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicDiyConfig(ccUpdateMrMosicConfigRequest);
                    if (ccUpdateMrMosicConfigResponse != null && ccUpdateMrMosicConfigResponse.isSuccess()) {
                        success = true;
                    }
                    // 设置主会场分屏
                    CcStartTerminalChooseSeeRequest ccStartTerminalChooseSeeRequest = new CcStartTerminalChooseSeeRequest();
                    ccStartTerminalChooseSeeRequest.setConf_id(conferenceContext.getConfId());
                    ccStartTerminalChooseSeeRequest.setMode(1);
                    CcStartTerminalChooseSeeRequest.Src src = new CcStartTerminalChooseSeeRequest.Src();
                    src.setType(2);
                    ccStartTerminalChooseSeeRequest.setSrc(src);
                    CcStartTerminalChooseSeeRequest.Dst dst = new CcStartTerminalChooseSeeRequest.Dst();
                    dst.setMt_id(masterAttendee.getParticipantUuid());
                    ccStartTerminalChooseSeeRequest.setDst(dst);
                    CcStartTerminalChooseSeeResponse ccStartTerminalChooseSeeResponse = conferenceContext.getConferenceControlApi().startTerminalChooseSee(ccStartTerminalChooseSeeRequest);
                    if (ccStartTerminalChooseSeeResponse != null && ccStartTerminalChooseSeeResponse.isSuccess()) {
                    }
                }
            } else {
                // 无主会场时，观众自动
                CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
                ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
                ccUpdateMrMosicConfigRequest.setMode(2);
                ccUpdateMrMosicConfigRequest.setVoice_hint(1);
                ccUpdateMrMosicConfigRequest.setBroadcast(1);
                CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicDiyConfig(ccUpdateMrMosicConfigRequest);
                if (ccUpdateMrMosicConfigResponse != null && ccUpdateMrMosicConfigResponse.isSuccess()) {
                    success = true;
                }
            }
            if (success) {
            } else {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
                }
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
                if (defaultChooseSeeAttendee != null) {
                    if (!updateAttendeeList.contains(defaultChooseSeeAttendee)) {
                        defaultChooseSeeAttendee.resetUpdateMap();
                    }
                    defaultChooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                    updateAttendeeList.add(defaultChooseSeeAttendee);
                }

                for (AttendeeForMcuKdc attendeeForMcuKdc : updateAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
                }
                if (lastUpdateTime == 0) {
                    // 关闭混音
                    CcStopMrMixingRequest ccStopMrMixingRequest = new CcStopMrMixingRequest();
                    ccStopMrMixingRequest.setConf_id(conferenceContext.getConfId());
                    ccStopMrMixingRequest.setMix_id("1");
                    CcStopMrMixingResponse ccStopMrMixingResponse = conferenceContext.getConferenceControlApi().stopMrMixing(ccStopMrMixingRequest);
                    if (ccStopMrMixingResponse != null) {
                    }
                    // 开音
                    CcTerminalForceMuteRequest ccTerminalForceMuteRequest = new CcTerminalForceMuteRequest();
                    ccTerminalForceMuteRequest.setConf_id(conferenceContext.getConfId());
                    ccTerminalForceMuteRequest.setMt_id(masterAttendee.getParticipantUuid());
                    ccTerminalForceMuteRequest.setValue(0);
                    CcTerminalForceMuteResponse ccTerminalForceMuteResponse = conferenceContext.getConferenceControlApi().terminalForceMute(ccTerminalForceMuteRequest);
                    if (ccTerminalForceMuteResponse != null && ccTerminalForceMuteResponse.isSuccess()) {
                    }
                }
            }
        }
    }
}
