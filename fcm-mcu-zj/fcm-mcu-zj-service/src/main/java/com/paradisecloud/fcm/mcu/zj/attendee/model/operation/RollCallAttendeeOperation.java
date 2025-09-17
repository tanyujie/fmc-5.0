package com.paradisecloud.fcm.mcu.zj.attendee.model.operation;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrEpsStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrStatusRequest;
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

    private AttendeeForMcuZj rollCallAttendee;
    private String rollCallUserId = null;
    private volatile long runtimeCount = 0;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param rollCallAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public RollCallAttendeeOperation(McuZjConferenceContext conferenceContext, AttendeeForMcuZj rollCallAttendee) {
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
        operateScreen();
    }

    private void operateScreen() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - getLastUpdateTime() >= 10000) {
            setLastUpdateTime(currentTimeMillis);
            String userId = null;
            if (rollCallAttendee != null && StringUtils.isNotEmpty(rollCallAttendee.getEpUserId())) {
                userId = rollCallAttendee.getEpUserId();
            }
            if (StringUtils.isNotEmpty(userId)) {
                if (userId.equals(rollCallUserId)) {
                    return;
                }
            } else {
                return;
            }
            rollCallUserId = userId;
            // 设置点名的会场为嘉宾
//            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
//            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_chair);
//            ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{rollCallUserId});
//            ccUpdateMrEpsStatusRequest.setValue(1);
//            boolean result = conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
//            if (!result) {
//                return;
//            }
            CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
            List<CcUpdateMrMosicConfigRequest.ConfigInfo> configInfoList = new ArrayList();
            if (conferenceContext.isSingleView()) {
                {
                    // 观众
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoGuest.setLayout_mode(2);//传统分屏
                    configInfoGuest.setMosic_id(LayoutTemplates.SCREEN_2.getCode());// 二分屏
                    configInfoGuest.setRole("guest");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList1 = new ArrayList<>();
                    userIdList1.add("1010");//主会场
                    rolesLst.add(userIdList1);
                    List<String> userIdList = new ArrayList<>();
                    if (StringUtils.isNotEmpty(rollCallUserId)) {
                        userIdList.add(rollCallUserId);
                    } else {
                        String userIdTemp = "";
                        for (AttendeeForMcuZj attendeeForMcuZj : conferenceContext.getAttendees()) {
                            if (attendeeForMcuZj != null) {
                                if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                    userIdTemp = attendeeForMcuZj.getEpUserId();
                                    break;
                                }
                            }
                        }
                        if (StringUtils.isEmpty(userIdTemp)) {
                            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                                List<AttendeeForMcuZj> attendeeForMcuZjList = conferenceContext.getCascadeAttendeesMap().get(deptId);
                                if (attendeeForMcuZjList != null) {
                                    for (AttendeeForMcuZj attendeeForMcuZj : attendeeForMcuZjList) {
                                        if (attendeeForMcuZj != null) {
                                            if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                userIdTemp = attendeeForMcuZj.getEpUserId();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isEmpty(userIdTemp)) {
                            userIdList.add(userIdTemp);
                        } else {
                            userIdList.add("0");
                        }
                    }
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
                    configInfoSpeaker.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 一分屏
                    configInfoSpeaker.setRole("speaker");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList = new ArrayList<>();
                    if (StringUtils.isNotEmpty(rollCallUserId)) {
                        userIdList.add(rollCallUserId);
                    } else {
                        String userIdTemp = "";
                        for (AttendeeForMcuZj attendeeForMcuZj : conferenceContext.getAttendees()) {
                            if (attendeeForMcuZj != null) {
                                if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                    userIdTemp = attendeeForMcuZj.getEpUserId();
                                    break;
                                }
                            }
                        }
                        if (StringUtils.isEmpty(userIdTemp)) {
                            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                                List<AttendeeForMcuZj> attendeeForMcuZjList = conferenceContext.getCascadeAttendeesMap().get(deptId);
                                if (attendeeForMcuZjList != null) {
                                    for (AttendeeForMcuZj attendeeForMcuZj : attendeeForMcuZjList) {
                                        if (attendeeForMcuZj != null) {
                                            if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                userIdTemp = attendeeForMcuZj.getEpUserId();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isEmpty(userIdTemp)) {
                            userIdList.add(userIdTemp);
                        } else {
                            userIdList.add("0");
                        }
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
                    configInfoGuest.setMosic_id(LayoutTemplates.SCREEN_2.getCode());// 二分屏
                    configInfoGuest.setRole("guest");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList1 = new ArrayList<>();
                    userIdList1.add("1010");//主会场
                    rolesLst.add(userIdList1);
                    List<String> userIdList = new ArrayList<>();
                    if (StringUtils.isNotEmpty(rollCallUserId)) {
                        userIdList.add(rollCallUserId);
                    }
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
//                    configInfoChair.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 一分屏
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
                if (runtimeCount == 0) {
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
                if (StringUtils.isNotEmpty(rollCallUserId)) {
                    if (rollCallAttendee != null) {
                        if (!updateAttendeeList.contains(rollCallAttendee)) {
                            rollCallAttendee.resetUpdateMap();
                        }
                        rollCallAttendee.setCallTheRollStatus(AttendeeCallTheRollStatus.YES.getValue());
                        updateAttendeeList.add(rollCallAttendee);
                    }
                }

                for (AttendeeForMcuZj attendeeForMcuZj : updateAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        if (attendeeForMcuZj == rollCallAttendee) {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("正在点名【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZj.getDeptId()).getDeptName(), attendeeForMcuZj.getName())).append("】");
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
                }

                // 全体关闭麦克风
                CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
                ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_all_guests_mute);
                ccUpdateMrStatusRequest.setMrStatusValue(1);
                success = conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
                if (success) {
                }
                // 打开点名混音
                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                ccUpdateMrEpsStatusRequest.setAction(ccUpdateMrEpsStatusRequest.ACTION_a_rx);
                String[] strings = new String[1];
                strings[0] = rollCallAttendee.getEpUserId();
                ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
                ccUpdateMrEpsStatusRequest.setValue(1);
                success = conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                if (success) {
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
//        if (StringUtils.isNotEmpty(rollCallUserId)) {
//            // 取消嘉宾
//            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
//            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_chair);
//            ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{rollCallUserId});
//            ccUpdateMrEpsStatusRequest.setValue(0);
//            boolean result = conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
//            if (!result) {
//                return;
//            }
//        }
        // 全体关闭麦克风
        CcUpdateMrStatusRequest ccUpdateMrStatusRequest = new CcUpdateMrStatusRequest();
        ccUpdateMrStatusRequest.setMrStatusAction(CcUpdateMrStatusRequest.PARAM_all_guests_mute);
        ccUpdateMrStatusRequest.setMrStatusValue(1);
        boolean success = conferenceContext.getConferenceControlApi().updateMrStatus(ccUpdateMrStatusRequest);
        if (success) {
        }
    }
}
