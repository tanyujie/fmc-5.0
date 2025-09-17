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
public class TalkAttendeeOperation extends AttendeeOperation {

    private AttendeeForMcuKdc talkAttendee;
    private String talkUuid = null;
    private volatile List<AttendeeForMcuKdc> autoPollingAttends = new ArrayList<>();
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
    public TalkAttendeeOperation(McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc talkAttendee) {
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
            AttendeeForMcuKdc masterAttendee = conferenceContext.getMasterAttendee();
            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                // 设置二分屏
                CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
                ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
                ccUpdateMrMosicConfigRequest.setMode(1);
                ccUpdateMrMosicConfigRequest.setLayout(Integer.valueOf(McuKdcLayoutTemplates.SCREEN_2.getCode()));
                List<CellInfo> members = new ArrayList<>();
                // 主会场
                {
                    CellInfo member = new CellInfo();
                    member.setChn_idx(0);
                    member.setMember_type(1);
                    member.setMt_id(masterAttendee.getParticipantUuid());
                    member.setMt_chn_idx(0);
                    members.add(member);
                }
                // 对话终端
                {
                    CellInfo member = new CellInfo();
                    member.setChn_idx(1);
                    member.setMember_type(1);
                    member.setMt_id(talkUuid);
                    member.setMt_chn_idx(0);
                    members.add(member);
                }
                ccUpdateMrMosicConfigRequest.setMembers(members);
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
                if (StringUtils.isNotEmpty(talkUuid)) {
                    if (talkAttendee != null) {
                        if (!updateAttendeeList.contains(talkAttendee)) {
                            talkAttendee.resetUpdateMap();
                        }
                        talkAttendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
                        updateAttendeeList.add(talkAttendee);
                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场正在对话【").append(talkAttendee.getName()).append("】");
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    }
                }

                for (AttendeeForMcuKdc attendeeForMcuKdc : updateAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                    }
                }

                // 关闭混音
                CcStopMrMixingRequest ccStopMrMixingRequest = new CcStopMrMixingRequest();
                ccStopMrMixingRequest.setConf_id(conferenceContext.getConfId());
                ccStopMrMixingRequest.setMix_id("1");
                CcStopMrMixingResponse ccStopMrMixingResponse = conferenceContext.getConferenceControlApi().stopMrMixing(ccStopMrMixingRequest);
                if (ccStopMrMixingResponse != null) {
                }
                // 打开主会场和对话终端混音
                CcStartMrMixingRequest ccStartMrMixingRequest = new CcStartMrMixingRequest();
                ccStartMrMixingRequest.setConf_id(conferenceContext.getConfId());
                List<CcStartMrMixingRequest.Member> members = new ArrayList<>();
                {
                    CcStartMrMixingRequest.Member member = new CcStartMrMixingRequest.Member();
                    member.setMt_id(masterAttendee.getParticipantUuid());
                    members.add(member);
                }
                {
                    CcStartMrMixingRequest.Member member = new CcStartMrMixingRequest.Member();
                    member.setMt_id(talkUuid);
                    members.add(member);
                }
                ccStartMrMixingRequest.setMembers(members);
                CcStartMrMixingResponse ccStartMrMixingResponse = conferenceContext.getConferenceControlApi().startMrMixing(ccStartMrMixingRequest);
                if (ccStartMrMixingResponse != null && ccStartMrMixingResponse.isSuccess()) {
                }
                if (success) {
                    // 发送提示信息
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, AttendeeMixingStatus.YES.getName());
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("主会场正在对话【").append(getViewName(SysDeptCache.getInstance().get(talkAttendee.getDeptId()).getDeptName() , talkAttendee.getName())).append("】");
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
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
        // 关闭混音
        CcStopMrMixingRequest ccStopMrMixingRequest = new CcStopMrMixingRequest();
        ccStopMrMixingRequest.setConf_id(conferenceContext.getConfId());
        ccStopMrMixingRequest.setMix_id("1");
        CcStopMrMixingResponse ccStopMrMixingResponse = conferenceContext.getConferenceControlApi().stopMrMixing(ccStopMrMixingRequest);
        if (ccStopMrMixingResponse != null) {
        }
    }

}
