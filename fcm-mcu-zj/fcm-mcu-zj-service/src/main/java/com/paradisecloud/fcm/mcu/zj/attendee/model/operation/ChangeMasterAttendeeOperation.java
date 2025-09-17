package com.paradisecloud.fcm.mcu.zj.attendee.model.operation;

import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.sinhy.spring.BeanFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private volatile long runtimeCount = 0;
    private volatile AttendeeForMcuZj defaultChooseSeeAttendee = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChangeMasterAttendeeOperation(McuZjConferenceContext conferenceContext) {
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
        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuZj attendee : conferenceContext.getMasterAttendees()) {
                if (attendee.isMeetingJoined()) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined()) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuZj> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuZj attendee : attendees) {
                    if (attendee.isMeetingJoined()) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }
    }

    private void operateScreen() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - getLastUpdateTime() >= 10000) {
            setLastUpdateTime(currentTimeMillis);
            CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
            List<CcUpdateMrMosicConfigRequest.ConfigInfo> configInfoList = new ArrayList();
            if (conferenceContext.isSingleView()) {
                {
                    // 观众
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoGuest.setLayout_mode(2);//传统分屏
                    configInfoGuest.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 自动
                    configInfoGuest.setRole("guest");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList = new ArrayList<>();
                    userIdList.add("1010");//主会场
                    rolesLst.add(userIdList);
                    configInfoGuest.setRoles_lst(rolesLst);
                    configInfoGuest.setView_has_self(0);
                    configInfoGuest.setPoll_secs(10);
                    configInfoList.add(configInfoGuest);
                }
            } else {
                {
                    // 主会场
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoSpeaker = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoSpeaker.setLayout_mode(2);//传统分屏
                    configInfoSpeaker.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 1分屏
                    configInfoSpeaker.setRole("speaker");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList = new ArrayList<>();
                    if (defaultChooseSeeAttendee != null) {
                        userIdList.add(defaultChooseSeeAttendee.getEpUserId());
                    } else {
                        userIdList.add("0");//自动指定
                    }
                    rolesLst.add(userIdList);
                    configInfoSpeaker.setRoles_lst(rolesLst);
                    configInfoSpeaker.setView_has_self(0);
                    configInfoSpeaker.setPoll_secs(10);
                    configInfoList.add(configInfoSpeaker);
                }
                {
                    // 观众
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoGuest.setLayout_mode(2);//传统分屏
                    configInfoGuest.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 自动
                    configInfoGuest.setRole("guest");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList = new ArrayList<>();
                    userIdList.add("1010");//主会场
                    rolesLst.add(userIdList);
                    configInfoGuest.setRoles_lst(rolesLst);
                    configInfoGuest.setView_has_self(0);
                    configInfoGuest.setPoll_secs(10);
                    configInfoList.add(configInfoGuest);
                }
//                {
//                    // 嘉宾
//                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoChair = new CcUpdateMrMosicConfigRequest.ConfigInfo();
//                    configInfoChair.setLayout_mode(2);//传统分屏
//                    configInfoChair.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 自动
//                    configInfoChair.setRole("chair");
//                    List<List<String>> rolesLst = new ArrayList<>();
//                    List<String> userIdList = new ArrayList<>();
//                    userIdList.add("1010");//主会场
//                    rolesLst.add(userIdList);
//                    configInfoChair.setRoles_lst(rolesLst);
//                    configInfoChair.setView_has_self(0);
//                    configInfoChair.setPoll_secs(10);
//                    configInfoList.add(configInfoChair);
//                }
            }
            ccUpdateMrMosicConfigRequest.setConfig_info(configInfoList);
            if (isCancel()) {
                return;
            }
            boolean success = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequest);
            if (success) {
                Set<AttendeeForMcuZj> updateAttendeeList = new HashSet<>();
                if (runtimeCount <= 3) {
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
                if (defaultChooseSeeAttendee != null) {
                    if (!updateAttendeeList.contains(defaultChooseSeeAttendee)) {
                        defaultChooseSeeAttendee.resetUpdateMap();
                    }
                    defaultChooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                    updateAttendeeList.add(defaultChooseSeeAttendee);
                }

                for (AttendeeForMcuZj attendeeForMcuZj : updateAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
                }
            }
        }
    }
}
