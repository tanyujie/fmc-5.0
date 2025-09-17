/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.attendee.model.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.CellInfo;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.mcu.kdc.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.mcu.kdc.model.enumer.McuKdcLayoutTemplates;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcSetChairmanMrTerminalRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcSetSpeakerMrTerminalRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcStartTerminalChooseSeeRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcSetChairmanMrTerminalResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcSetSpeakerMrTerminalResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcStartTerminalChooseSeeResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.CcUpdateMrMosicConfigResponse;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * <pre>会议室默认视图</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-22 18:16
 */
public class DefaultAttendeeOperation extends DefaultViewOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 18:16
     */
    private static final long serialVersionUID = 1L;
    private volatile List<JSONObject> defaultViewDepts = new ArrayList<>();

    private volatile List<AttendeeForMcuKdc> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile int lastAutoPollingIdx = -1;
    private volatile int autoPollingScreenCount = 0;
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    private volatile List<String> autoPollingAttendeeIdList = new ArrayList<>();
    private volatile List<String> autoPollingAttendeeIdListLast = new ArrayList<>();
    private volatile Set<AttendeeForMcuKdc> lastChooseSeeAttendeeList = null;
    private volatile Set<AttendeeForMcuKdc> lastPollingAttendeeList = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(McuKdcConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public void initSplitScreen() {
        int maxImportance = YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
        if (OneSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OneSplitScreen(maxImportance);
        } else if (FourSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new FourSplitScreen(maxImportance);
        } else if (NineSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new NineSplitScreen(maxImportance);
        } else if (SixteenSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new SixteenSplitScreen(maxImportance);
        } else if (TwentyFiveSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new TwentyFiveSplitScreen(maxImportance);
        } else if (OnePlusFiveSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OnePlusFiveSplitScreen(maxImportance);
        } else if (OnePlusSevenSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OnePlusSevenSplitScreen(maxImportance);
        } else {
            this.splitScreen = new AutomaticSplitScreen();
        }
    }

    @Override
    public void operate() {
        initTargetAttendees();
        operateScreen();
    }

    @Override
    public void cancel() {
        if (conferenceContext != null) {
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            logger.info("默认视图已结束");
        }
        runtimeCount = 0;
    }

    private void initTargetAttendees() {
        autoPollingScreenCount = 0;
        autoPollingAttends.clear();
        checkedAttendeeIdSet.clear();
        autoPollingAttendeeIdSet.clear();
        autoPollingAttendeeIdList.clear();

        boolean isViewSelf = YesOrNo.convert(getDefaultViewIsDisplaySelf()) == YesOrNo.YES;

        for (CellScreen cellScreen : getSplitScreen().getCellScreens()) {
            List<AttendeeForMcuKdc> attendeeList = cellScreen.getAttendees();
            if (attendeeList.size() > 0) {
                for (AttendeeForMcuKdc attendee : attendeeList) {
                    if (attendee != null) {
                        checkedAttendeeIdSet.add(attendee.getId());
                    }
                }
            } else {
                autoPollingScreenCount++;
            }
        }

        if (!ObjectUtils.isEmpty(attendees)) {
            for (AttendeeForMcuKdc attendee : attendees) {
                if (attendee != null) {
                    if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                        boolean needAdd = true;
                        if (conferenceContext.getMasterAttendee() != null) {
                            if (attendee.getId().equals(conferenceContext.getMasterAttendee().getId())) {
                                if (isViewSelf) {
                                    autoPollingAttends.add(conferenceContext.getMasterAttendee());
                                    checkedAttendeeIdSet.add(conferenceContext.getMasterAttendee().getId());
                                }
                                needAdd = false;
                            }
                        }
                        if (needAdd) {
                            autoPollingAttends.add(attendee);
                            checkedAttendeeIdSet.add(attendee.getId());
                        }
                    }
                }
            }
        }

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                if (isViewSelf) {
                    autoPollingAttends.add(conferenceContext.getMasterAttendee());
                    checkedAttendeeIdSet.add(conferenceContext.getMasterAttendee().getId());
                }
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

//        if (autoPollingAttends.size() <= autoPollingScreenCount) {
            lastAutoPollingIdx = -1;
//        }
    }

    private void operateScreen() {
        boolean forceUpdateView = isForceUpdateView();
        if (forceUpdateView) {
            setForceUpdateView(false);
//            LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
//            if (LayoutTemplates.AUTO == layoutTemplates) {
//                forceUpdateView = false;
//            }
            if (getSplitScreen().getAutoPollingCellScreenCount() == 0) {
                forceUpdateView = false;
            }
        }
        long lastUpdateTime = getLastUpdateTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - getLastUpdateTime() >= getDefaultViewPollingInterval() * 1000 || forceUpdateView) {
            if (forceUpdateView) {
                if (currentTimeMillis - getLastUpdateTime() + 5000 >= getDefaultViewPollingInterval() * 1000) {
                    return;
                }
            }
            if (!forceUpdateView) {
                setLastUpdateTime(currentTimeMillis);
            }
            Set<AttendeeForMcuKdc> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuKdc> chooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuKdc> cancelPollingAttendeeList = new HashSet<>();
            Set<AttendeeForMcuKdc> pollingAttendeeList = new HashSet<>();
            boolean isBroadcast = YesOrNo.convert(getDefaultViewIsBroadcast()) == YesOrNo.YES;
            boolean isFill = YesOrNo.convert(getDefaultViewIsFill()) == YesOrNo.YES;
            boolean isViewSelf = YesOrNo.convert(getDefaultViewIsDisplaySelf()) == YesOrNo.YES;
            SplitScreen splitScreen = getSplitScreen();
            List<CellScreen> cellScreens = splitScreen.getCellScreens();
            McuKdcLayoutTemplates layoutTemplates = McuKdcLayoutTemplates.convert(splitScreen);
            List<String> uuidList = new ArrayList<>();
            List<CellInfo> members = new ArrayList<>();

            if (McuKdcLayoutTemplates.AUTO == layoutTemplates) {
                isBroadcast = false;
                int attendeeCount = autoPollingAttends.size();
                if (attendeeCount > 9) {
                    layoutTemplates = McuKdcLayoutTemplates.SCREEN_16;
                } else if (attendeeCount > 4) {
                    layoutTemplates = McuKdcLayoutTemplates.SCREEN_9;
                } else if (attendeeCount > 2) {
                    layoutTemplates = McuKdcLayoutTemplates.SCREEN_4;
                } else if (attendeeCount == 2) {
                    if (isBroadcast || isViewSelf) {
                        layoutTemplates = McuKdcLayoutTemplates.SCREEN_2;
                    } else {
                        layoutTemplates = McuKdcLayoutTemplates.SCREEN_1;
                    }
                } else {
                    layoutTemplates = McuKdcLayoutTemplates.SCREEN_1;
                }
                if (layoutTemplates.getNum() != splitScreen.getCellScreens().size()) {
                    splitScreen.getCellScreens().clear();
                    for (int i = 0; i < layoutTemplates.getNum(); i++) {
                        splitScreen.addCellScreen(i, i);
                    }
                }
            }

            if (McuKdcLayoutTemplates.AUTO != layoutTemplates) {
                for (int i = 0; i < layoutTemplates.getNum(); i++) {
                    CellInfo cellInfo = new CellInfo();
                    cellInfo.setChn_idx(i);
                    CellScreen cellScreen = cellScreens.get(i);
                    List<AttendeeForMcuKdc> attendeeList = cellScreen.getAttendees();
                    if (cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.ROUND && !forceUpdateView) {
                        AttendeeForMcuKdc attendeeForMcuKdc = cellScreen.getPollingAttendee();
                        if (attendeeForMcuKdc != null && StringUtils.isNotEmpty(attendeeForMcuKdc.getParticipantUuid()) && attendeeForMcuKdc.isMeetingJoined()) {
                            String uuid = attendeeForMcuKdc.getParticipantUuid();
                            cellInfo.setMember_type(1);
                            cellInfo.setMt_id(uuid);
                            cellInfo.setMt_chn_idx(0);
                            uuidList.add(uuid);
                            pollingAttendeeList.add(attendeeForMcuKdc);
                        } else {
                            if (isFill) {
                                for (int j = 0; j < attendeeList.size() - 1; j++) {
                                    attendeeForMcuKdc = cellScreen.getPollingAttendee();
                                    if (attendeeForMcuKdc != null) {
                                        String uuid = attendeeForMcuKdc.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid) && attendeeForMcuKdc.isMeetingJoined()) {
                                            cellInfo.setMember_type(1);
                                            cellInfo.setMt_id(uuid);
                                            cellInfo.setMt_chn_idx(0);
                                            uuidList.add(uuid);
                                            pollingAttendeeList.add(attendeeForMcuKdc);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (attendeeForMcuKdc != null) {
                                    String uuid = attendeeForMcuKdc.getParticipantUuid();
                                    if (StringUtils.isNotEmpty(uuid)) {
                                        cellInfo.setMember_type(1);
                                        cellInfo.setMt_id(uuid);
                                        cellInfo.setMt_chn_idx(0);
                                        uuidList.add(uuid);
                                        pollingAttendeeList.add(attendeeForMcuKdc);
                                    }
                                }
                            }
                        }
                    } else {
                        if (attendeeList.size() > 0) {
                            if (isFill) {
                                for (AttendeeForMcuKdc attendeeForMcuKdc : attendeeList) {
                                    if (attendeeForMcuKdc != null) {
                                        String uuid = attendeeForMcuKdc.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid) && attendeeForMcuKdc.isMeetingJoined()) {
                                            cellInfo.setMember_type(1);
                                            cellInfo.setMt_id(uuid);
                                            cellInfo.setMt_chn_idx(0);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                AttendeeForMcuKdc attendeeForMcuKdc = attendeeList.get(0);
                                if (attendeeForMcuKdc != null) {
                                    String uuid = attendeeForMcuKdc.getParticipantUuid();
                                    if (StringUtils.isNotEmpty(uuid)) {
                                        cellInfo.setMember_type(1);
                                        cellInfo.setMt_id(uuid);
                                        cellInfo.setMt_chn_idx(0);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                    }
                                }
                            }
                        } else {
                            // 自动指定
                            if (autoPollingAttends.size() <= autoPollingScreenCount) {
                                int index = lastAutoPollingIdx + 1;
                                if (index < autoPollingAttends.size()) {
                                    AttendeeForMcuKdc attendeeForMcuKdc = autoPollingAttends.get(index);
                                    if (attendeeForMcuKdc != null) {
                                        String uuid = attendeeForMcuKdc.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid)) {
                                            if (isFill) {
                                                if (attendeeForMcuKdc.isMeetingJoined()) {
                                                    cellInfo.setMember_type(1);
                                                    cellInfo.setMt_id(uuid);
                                                    cellInfo.setMt_chn_idx(0);
                                                    uuidList.add(uuid);
                                                    chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                                    autoPollingAttendeeIdList.add(attendeeForMcuKdc.getId());
                                                    lastAutoPollingIdx = index;
                                                }
                                            } else {
                                                cellInfo.setMember_type(1);
                                                cellInfo.setMt_id(uuid);
                                                cellInfo.setMt_chn_idx(0);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                                autoPollingAttendeeIdList.add(attendeeForMcuKdc.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
                                    }
                                    if (StringUtils.isEmpty(cellInfo.getMt_id())) {
                                        if (isFill) {
                                            for (int m = index; m < autoPollingAttends.size(); m++) {
                                                AttendeeForMcuKdc attendeeTemp = autoPollingAttends.get(m);
                                                if (attendeeTemp != null) {
                                                    String uuid = attendeeTemp.getParticipantUuid();
                                                    if (StringUtils.isNotEmpty(uuid) && attendeeTemp.isMeetingJoined()) {
                                                        cellInfo.setMember_type(1);
                                                        cellInfo.setMt_id(uuid);
                                                        cellInfo.setMt_chn_idx(0);
                                                        uuidList.add(uuid);
                                                        chooseSeeAttendeeList.add(attendeeTemp);
                                                        autoPollingAttendeeIdSet.add(attendeeTemp.getId());
                                                        autoPollingAttendeeIdList.add(attendeeTemp.getId());
                                                        lastAutoPollingIdx = m;
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            lastAutoPollingIdx = index;
                                        }
                                    }
                                }
                            } else {
                                int index = lastAutoPollingIdx + 1;
                                if (index >= autoPollingAttends.size()) {
                                    index = 0;
                                }
                                for (int m = index; m < index + autoPollingAttends.size(); m++) {
                                    int idxReal = m;
                                    if (m >= autoPollingAttends.size()) {
                                        idxReal = m - autoPollingAttends.size();
                                    }
                                    AttendeeForMcuKdc attendeeForMcuKdc = autoPollingAttends.get(idxReal);
                                    if (attendeeForMcuKdc != null) {
                                        if (!autoPollingAttendeeIdSet.contains(attendeeForMcuKdc.getId())) {
                                            String uuid = attendeeForMcuKdc.getParticipantUuid();
                                            if (StringUtils.isNotEmpty(uuid)) {
                                                if (isFill) {
                                                    if (attendeeForMcuKdc.isMeetingJoined()) {
                                                        cellInfo.setMember_type(1);
                                                        cellInfo.setMt_id(uuid);
                                                        cellInfo.setMt_chn_idx(0);
                                                        uuidList.add(uuid);
                                                        chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                                        autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                                        autoPollingAttendeeIdList.add(attendeeForMcuKdc.getId());
                                                        lastAutoPollingIdx = idxReal;
                                                        break;
                                                    }
                                                } else {
                                                    cellInfo.setMember_type(1);
                                                    cellInfo.setMt_id(uuid);
                                                    cellInfo.setMt_chn_idx(0);
                                                    uuidList.add(uuid);
                                                    chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                                    autoPollingAttendeeIdList.add(attendeeForMcuKdc.getId());
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (StringUtils.isEmpty(cellInfo.getMt_id())) {
                                        if (!isFill) {
                                            lastAutoPollingIdx = idxReal;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (StringUtils.isEmpty(cellInfo.getMt_id())) {
                        cellInfo.setMember_type(1);
                        cellInfo.setMt_id("");
                        cellInfo.setMt_chn_idx(0);
                    }
                    members.add(cellInfo);
                    // 获取取消选看和取消轮询的终端
                    for (AttendeeForMcuKdc attendeeForMcuKdc : attendeeList) {
                        if (attendeeForMcuKdc != null) {
                            if (!chooseSeeAttendeeList.contains(attendeeForMcuKdc)) {
                                if (attendeeForMcuKdc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()
                                    || attendeeForMcuKdc.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelChooseSeeAttendeeList.add(attendeeForMcuKdc);
                                }
                            }
                            if (!pollingAttendeeList.contains(attendeeForMcuKdc)) {
                                if (attendeeForMcuKdc.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                    || attendeeForMcuKdc.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelPollingAttendeeList.add(attendeeForMcuKdc);
                                }
                            }
                        }
                    }
                }
            }

            if (isCancel()) {
                return;
            }
            if (forceUpdateView) {
                boolean updateView = false;
                if (autoPollingAttendeeIdList.size() == autoPollingAttendeeIdListLast.size()) {
                    for (int i = 0; i < autoPollingAttendeeIdList.size(); i++) {
                        if (!autoPollingAttendeeIdList.get(i).equals(autoPollingAttendeeIdListLast.get(i))) {
                            updateView = true;
                            break;
                        }
                    }
                } else {
                    updateView = true;
                }
                if (!updateView) {
                    return;
                }
            }
            boolean success = false;
            AttendeeForMcuKdc masterAttendee = conferenceContext.getMasterAttendee();
            if (isBroadcast) {
                // 广播时，非自动时，主会场和观众看同样分屏
                if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
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
                } else {
                    // 取消主席
                    CcSetChairmanMrTerminalRequest ccSetChairmanMrTerminalRequest = new CcSetChairmanMrTerminalRequest();
                    ccSetChairmanMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    ccSetChairmanMrTerminalRequest.setMt_id("");
                    CcSetChairmanMrTerminalResponse ccSetChairmanMrTerminalResponse = conferenceContext.getConferenceControlApi().setMrChairman(ccSetChairmanMrTerminalRequest);
                    if (ccSetChairmanMrTerminalResponse != null && ccSetChairmanMrTerminalResponse.isSuccess()) {
                    }
                    // 取消发言人
                    CcSetSpeakerMrTerminalRequest ccSetSpeakerMrTerminalRequest = new CcSetSpeakerMrTerminalRequest();
                    ccSetSpeakerMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    ccSetSpeakerMrTerminalRequest.setMt_id("");
                    ccSetSpeakerMrTerminalRequest.setForce_broadcast(1);
                    CcSetSpeakerMrTerminalResponse ccSetSpeakerMrTerminalResponse = conferenceContext.getConferenceControlApi().setMrSpeaker(ccSetSpeakerMrTerminalRequest);
                    if (ccSetSpeakerMrTerminalResponse != null && ccSetSpeakerMrTerminalResponse.isSuccess()) {
                    }
                }
                CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
                ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
                if (McuKdcLayoutTemplates.AUTO != layoutTemplates) {
                    ccUpdateMrMosicConfigRequest.setMode(1);
                    ccUpdateMrMosicConfigRequest.setLayout(Integer.valueOf(layoutTemplates.getCode()));
                } else {
                    ccUpdateMrMosicConfigRequest.setMode(2);
                }
                ccUpdateMrMosicConfigRequest.setVoice_hint(1);
                ccUpdateMrMosicConfigRequest.setBroadcast(1);
                ccUpdateMrMosicConfigRequest.setMembers(members);
                CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicDiyConfig(ccUpdateMrMosicConfigRequest);
                if (ccUpdateMrMosicConfigResponse != null && ccUpdateMrMosicConfigResponse.isSuccess()) {
                    success = true;
                }
            } else {
                // 不广播时，非自动时，观众看主会场，主会场看分屏观众。
                // 主会场
                if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
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

                    // 有主会场时，观众看主会场
                    CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
                    ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
                    if (McuKdcLayoutTemplates.AUTO != layoutTemplates) {
                        ccUpdateMrMosicConfigRequest.setMode(1);
                        ccUpdateMrMosicConfigRequest.setLayout(Integer.valueOf(layoutTemplates.getCode()));
                    } else {
                        ccUpdateMrMosicConfigRequest.setMode(2);
                    }
                    ccUpdateMrMosicConfigRequest.setVoice_hint(1);
                    ccUpdateMrMosicConfigRequest.setBroadcast(0);
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
                } else {
                    // 无主会场时，观众自动
                    // 取消主席
                    CcSetChairmanMrTerminalRequest ccSetChairmanMrTerminalRequest = new CcSetChairmanMrTerminalRequest();
                    ccSetChairmanMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    ccSetChairmanMrTerminalRequest.setMt_id("");
                    CcSetChairmanMrTerminalResponse ccSetChairmanMrTerminalResponse = conferenceContext.getConferenceControlApi().setMrChairman(ccSetChairmanMrTerminalRequest);
                    if (ccSetChairmanMrTerminalResponse != null && ccSetChairmanMrTerminalResponse.isSuccess()) {
                    }
                    // 取消发言人
                    CcSetSpeakerMrTerminalRequest ccSetSpeakerMrTerminalRequest = new CcSetSpeakerMrTerminalRequest();
                    ccSetSpeakerMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    ccSetSpeakerMrTerminalRequest.setMt_id("");
                    ccSetSpeakerMrTerminalRequest.setForce_broadcast(1);
                    CcSetSpeakerMrTerminalResponse ccSetSpeakerMrTerminalResponse = conferenceContext.getConferenceControlApi().setMrSpeaker(ccSetSpeakerMrTerminalRequest);
                    if (ccSetSpeakerMrTerminalResponse != null && ccSetSpeakerMrTerminalResponse.isSuccess()) {
                    }
                    CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = CcUpdateMrMosicConfigRequest.buildDefaultRequest();
                    ccUpdateMrMosicConfigRequest.setConf_id(conferenceContext.getConfId());
                    ccUpdateMrMosicConfigRequest.setMode(2);
                    ccUpdateMrMosicConfigRequest.setVoice_hint(1);
                    ccUpdateMrMosicConfigRequest.setBroadcast(0);
                    CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicDiyConfig(ccUpdateMrMosicConfigRequest);
                    if (ccUpdateMrMosicConfigResponse != null && ccUpdateMrMosicConfigResponse.isSuccess()) {
                        success = true;
                    }
                }
            }
            if (success) {
            } else {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
                }
            }
            if (success) {
                autoPollingAttendeeIdListLast = autoPollingAttendeeIdList;
                autoPollingAttendeeIdList = new ArrayList<>();
                Set<AttendeeForMcuKdc> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    runtimeCount++;

                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("当前已设置为默认视图");
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                    // 推送默认视图状态消息
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, true);

                    logger.info(messageTip.toString());
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
                for (AttendeeForMcuKdc attendeeForMcuKdc : cancelChooseSeeAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuKdc)) {
                            attendeeForMcuKdc.resetUpdateMap();
                        }
                        attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuKdc);
                    }
                }
                for (AttendeeForMcuKdc attendeeForMcuKdc : cancelPollingAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuKdc)) {
                            attendeeForMcuKdc.resetUpdateMap();
                        }
                        attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuKdc);
                    }
                }
                Set<AttendeeForMcuKdc> lastChooseSeeAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuKdc attendeeForMcuKdc : chooseSeeAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        lastChooseSeeAttendeeListNew.add(attendeeForMcuKdc);
                        if (!updateAttendeeList.contains(attendeeForMcuKdc)) {
                            attendeeForMcuKdc.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.YES.getValue());
                            attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        } else {
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuKdc);
                    }
                }
                Set<AttendeeForMcuKdc> lastPollingAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuKdc attendeeForMcuKdc : pollingAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        lastPollingAttendeeListNew.add(attendeeForMcuKdc);
                        if (!updateAttendeeList.contains(attendeeForMcuKdc)) {
                            attendeeForMcuKdc.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.YES.getValue());
                            attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        } else {
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuKdc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuKdc);
                    }
                }

                for (AttendeeForMcuKdc attendeeForMcuKdc : updateAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        if ((lastChooseSeeAttendeeList == null && lastPollingAttendeeList == null)
                                || (!lastChooseSeeAttendeeList.contains(attendeeForMcuKdc) && lastChooseSeeAttendeeListNew.contains(attendeeForMcuKdc))
                                || (!lastPollingAttendeeList.contains(attendeeForMcuKdc) && lastPollingAttendeeListNew.contains(attendeeForMcuKdc))) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuKdc.getDeptId()).getDeptName(), attendeeForMcuKdc.getName())).append("】");
                            } else {
                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                    messageTip.append("主会场正在选看【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuKdc.getDeptId()).getDeptName(), attendeeForMcuKdc.getName())).append("】");
                                }
                            }
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    }
                }
                lastChooseSeeAttendeeList = lastChooseSeeAttendeeListNew;
                lastPollingAttendeeList = lastPollingAttendeeListNew;
            }
        }
    }

    private String getViewName(String deptName, String name) {
        if (deptName.equals(name)) {
            return deptName;
        }
        return name + " / " + deptName;
    }

    private int getCheckedMeetingJoinedCount() {
        int c = 0;
        if (!ObjectUtils.isEmpty(attendees)) {
            for (AttendeeForMcuKdc attendee : attendees) {
                if (attendee.isMeetingJoined() && !(YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.NO
                        && PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf) != PanePlacementSelfPaneMode.SELF
                        && attendee == conferenceContext.getMasterAttendee())) {
                    c++;
                }
            }
        }
        return c;
    }

    /**
     * <p>Get Method   :   defaultViewDepts List<JSONObject></p>
     *
     * @return defaultViewDepts
     */
    public List<JSONObject> getDefaultViewDepts() {
        return defaultViewDepts;
    }

    /**
     * <p>Set Method   :   defaultViewDepts List<JSONObject></p>
     *
     * @param dept
     */
    public void addDefaultViewDept(JSONObject dept) {
        this.defaultViewDepts.add(dept);
    }

    @Override
    public boolean contains(AttendeeForMcuKdc attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }
}
