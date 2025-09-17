/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.attendee.model.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.mcu.zj.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrMosicConfigRequest;
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

    private volatile List<AttendeeForMcuZj> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile int lastAutoPollingIdx = -1;
    private volatile int autoPollingScreenCount = 0;
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    private volatile List<String> autoPollingAttendeeIdList = new ArrayList<>();
    private volatile List<String> autoPollingAttendeeIdListLast = new ArrayList<>();
    private volatile Set<AttendeeForMcuZj> lastChooseSeeAttendeeList = null;
    private volatile Set<AttendeeForMcuZj> lastPollingAttendeeList = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(McuZjConferenceContext conferenceContext) {
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
        } else if (OnePlusNineSplitScreen.LAYOUT.equals(defaultViewLayout)) {
            this.splitScreen = new OnePlusNineSplitScreen(maxImportance);
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
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
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
        for (CellScreen cellScreen : getSplitScreen().getCellScreens()) {
            List<AttendeeForMcuZj> attendeeList = cellScreen.getAttendees();
            if (attendeeList.size() > 0) {
                for (AttendeeForMcuZj attendee : attendeeList) {
                    if (attendee != null) {
                        checkedAttendeeIdSet.add(attendee.getId());
                    }
                }
            } else {
                autoPollingScreenCount++;
            }
        }

        if (!ObjectUtils.isEmpty(attendees)) {
            for (AttendeeForMcuZj attendee : attendees) {
                if (attendee != null) {
                    if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                        autoPollingAttends.add(attendee);
                        checkedAttendeeIdSet.add(attendee.getId());
                    }
                }
            }
        }

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                if (getDefaultViewIsDisplaySelf() == 1) {
                    autoPollingAttends.add(conferenceContext.getMasterAttendee());
                }
                checkedAttendeeIdSet.add(conferenceContext.getMasterAttendee().getId());
            }
        }

        for (AttendeeForMcuZj attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        if (conferenceContext.getMasterAttendees() != null) {
            for (AttendeeForMcuZj attendee : conferenceContext.getMasterAttendees()) {
                if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                    autoPollingAttends.add(attendee);
                    checkedAttendeeIdSet.add(attendee.getId());
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeForMcuZj> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeForMcuZj attendee : attendees) {
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
            LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
            if (LayoutTemplates.AUTO == layoutTemplates) {
                forceUpdateView = false;
            }
            if (getSplitScreen().getAutoPollingCellScreenCount() == 0) {
                forceUpdateView = false;
            }
        }
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
            Set<AttendeeForMcuZj> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZj> chooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZj> cancelPollingAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZj> pollingAttendeeList = new HashSet<>();
            CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
            List<CcUpdateMrMosicConfigRequest.ConfigInfo> configInfoList = new ArrayList();
            boolean isBroadcast = YesOrNo.convert(getDefaultViewIsBroadcast()) == YesOrNo.YES;
            boolean isFill = YesOrNo.convert(getDefaultViewIsFill()) == YesOrNo.YES;
            if (isBroadcast) {
                // 广播时，非自动时，主会场和观众看同样分屏
                List<List<String>> rolesLst = new ArrayList<>();
                {
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoSpeaker = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoSpeaker.setLayout_mode(2);//传统分屏
                    LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
                    configInfoSpeaker.setMosic_id(layoutTemplates.getCode());
                    configInfoSpeaker.setRole("speaker");
                    if (LayoutTemplates.AUTO == layoutTemplates) {
                        configInfoSpeaker.setRoles_lst(rolesLst);
                    } else {
                        for (int i = 0; i < layoutTemplates.getNum(); i++) {
                            List<String> userIdList = new ArrayList<>();
                            CellScreen cellScreen = getSplitScreen().getCellScreens().get(i);
                            List<AttendeeForMcuZj> attendeeList = cellScreen.getAttendees();
                            if (cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.ROUND && !forceUpdateView) {
                                AttendeeForMcuZj attendeeForMcuZj = cellScreen.getPollingAttendee();
                                if (attendeeForMcuZj != null && StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId()) && attendeeForMcuZj.isMeetingJoined()) {
                                    String userId = attendeeForMcuZj.getEpUserId();
                                    userIdList.add(userId);
                                    pollingAttendeeList.add(attendeeForMcuZj);
                                } else {
                                    if (isFill) {
                                        for (int j = 0; j < attendeeList.size() - 1; j++) {
                                            attendeeForMcuZj = cellScreen.getPollingAttendee();
                                            if (attendeeForMcuZj != null) {
                                                String userId = attendeeForMcuZj.getEpUserId();
                                                if (StringUtils.isNotEmpty(userId) && attendeeForMcuZj.isMeetingJoined()) {
                                                    userIdList.add(userId);
                                                    pollingAttendeeList.add(attendeeForMcuZj);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        if (attendeeForMcuZj != null) {
                                            if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                pollingAttendeeList.add(attendeeForMcuZj);
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (attendeeList.size() > 0) {
                                    if (isFill) {
                                        for (AttendeeForMcuZj attendeeForMcuZj : attendeeList) {
                                            if (attendeeForMcuZj != null) {
                                                String userId = attendeeForMcuZj.getEpUserId();
                                                if (StringUtils.isNotEmpty(userId) && attendeeForMcuZj.isMeetingJoined()) {
                                                    userIdList.add(userId);
                                                    chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        AttendeeForMcuZj attendeeForMcuZj = attendeeList.get(0);
                                        if (attendeeForMcuZj != null) {
                                            String userId = attendeeForMcuZj.getEpUserId();
                                            if (StringUtils.isNotEmpty(userId)) {
                                                userIdList.add(userId);
                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                            }
                                        }
                                    }
                                } else {
                                    // 自动指定
                                    if (autoPollingAttends.size() <= autoPollingScreenCount) {
                                        int index = lastAutoPollingIdx + 1;
                                        if (index < autoPollingAttends.size()) {
                                            AttendeeForMcuZj attendeeForMcuZj = autoPollingAttends.get(index);
                                            if (attendeeForMcuZj != null) {
                                                if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                    if (isFill) {
                                                        if (attendeeForMcuZj.isMeetingJoined()) {
                                                            userIdList.add(attendeeForMcuZj.getEpUserId());
                                                            chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                            autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                            autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                            lastAutoPollingIdx = index;
                                                        }
                                                    } else {
                                                        userIdList.add(attendeeForMcuZj.getEpUserId());
                                                        chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                        autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                        autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                        lastAutoPollingIdx = index;
                                                    }
                                                }
                                            }
                                            if (userIdList.size() == 0) {
                                                if (isFill) {
                                                    for (int m = index; m < autoPollingAttends.size(); m++) {
                                                        AttendeeForMcuZj attendeeTemp = autoPollingAttends.get(m);
                                                        if (attendeeTemp != null) {
                                                            if (StringUtils.isNotEmpty(attendeeTemp.getEpUserId()) && attendeeTemp.isMeetingJoined()) {
                                                                userIdList.add(attendeeTemp.getEpUserId());
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
                                            AttendeeForMcuZj attendeeForMcuZj = autoPollingAttends.get(idxReal);
                                            if (attendeeForMcuZj != null) {
                                                if (!autoPollingAttendeeIdSet.contains(attendeeForMcuZj.getId())) {
                                                    if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                        if (isFill) {
                                                            if (attendeeForMcuZj.isMeetingJoined()) {
                                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                                autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                                autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                                lastAutoPollingIdx = idxReal;
                                                                break;
                                                            }
                                                        } else {
                                                            userIdList.add(attendeeForMcuZj.getEpUserId());
                                                            chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                            autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                            autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                            lastAutoPollingIdx = idxReal;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            if (userIdList.size() == 0) {
                                                if (!isFill) {
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (userIdList.size() == 0) {
                                userIdList.add("-1");// 留空
                            }
                            rolesLst.add(userIdList);
                            // 获取取消选看和取消轮询的终端
                            for (AttendeeForMcuZj attendeeForMcuZj : attendeeList) {
                                if (attendeeForMcuZj != null) {
                                    if (!chooseSeeAttendeeList.contains(attendeeForMcuZj)) {
                                        if (attendeeForMcuZj.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()
                                            || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                            cancelChooseSeeAttendeeList.add(attendeeForMcuZj);
                                        }
                                    }
                                    if (!pollingAttendeeList.contains(attendeeForMcuZj)) {
                                        if (attendeeForMcuZj.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                            || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                            cancelPollingAttendeeList.add(attendeeForMcuZj);
                                        }
                                    }
                                }
                            }
                        }
                        configInfoSpeaker.setRoles_lst(rolesLst);
                    }
                    if (getDefaultViewIsDisplaySelf() == 1) {
                        configInfoSpeaker.setView_has_self(1);
                    } else {
                        configInfoSpeaker.setView_has_self(0);
                    }
                    Integer pollSecs = getDefaultViewPollingInterval();
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoSpeaker.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoSpeaker);
                }
                {
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoGuest.setLayout_mode(2);//传统分屏
                    LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
                    configInfoGuest.setMosic_id(layoutTemplates.getCode());
                    configInfoGuest.setRole("guest");
                    if (LayoutTemplates.AUTO == layoutTemplates) {
                        configInfoGuest.setRoles_lst(rolesLst);
                    } else {
                        configInfoGuest.setRoles_lst(rolesLst);
                    }
                    if (getDefaultViewIsDisplaySelf() == 1) {
                        configInfoGuest.setView_has_self(1);
                    } else {
                        configInfoGuest.setView_has_self(0);
                    }
                    Integer pollSecs = getDefaultViewPollingInterval();
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoGuest.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoGuest);
                }
            } else {
                // 不广播时，非自动时，观众看主会场，主会场看分屏观众。
                if (!conferenceContext.isSingleView()) {
                    // 主会场
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoSpeaker = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoSpeaker.setLayout_mode(2);//传统分屏
                    LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
                    configInfoSpeaker.setMosic_id(layoutTemplates.getCode());
                    configInfoSpeaker.setRole("speaker");
                    List<List<String>> rolesLst = new ArrayList<>();
                    if (LayoutTemplates.AUTO == layoutTemplates) {
                        configInfoSpeaker.setRoles_lst(rolesLst);
                    } else {
                        for (int i = 0; i < layoutTemplates.getNum(); i++) {
                            List<String> userIdList = new ArrayList<>();
                            CellScreen cellScreen = getSplitScreen().getCellScreens().get(i);
                            List<AttendeeForMcuZj> attendeeList = cellScreen.getAttendees();
                            if (cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.ROUND && !forceUpdateView) {
                                AttendeeForMcuZj attendeeForMcuZj = cellScreen.getPollingAttendee();
                                if (attendeeForMcuZj != null && StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId()) && attendeeForMcuZj.isMeetingJoined()) {
                                    String userId = attendeeForMcuZj.getEpUserId();
                                    userIdList.add(userId);
                                    pollingAttendeeList.add(attendeeForMcuZj);
                                } else {
                                    if (isFill) {
                                        for (int j = 0; j < attendeeList.size() - 1; j++) {
                                            attendeeForMcuZj = cellScreen.getPollingAttendee();
                                            if (attendeeForMcuZj != null) {
                                                String userId = attendeeForMcuZj.getEpUserId();
                                                if (StringUtils.isNotEmpty(userId) && attendeeForMcuZj.isMeetingJoined()) {
                                                    userIdList.add(userId);
                                                    pollingAttendeeList.add(attendeeForMcuZj);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        if (attendeeForMcuZj != null) {
                                            if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                pollingAttendeeList.add(attendeeForMcuZj);
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (attendeeList.size() > 0) {
                                    if (isFill) {
                                        for (AttendeeForMcuZj attendeeForMcuZj : attendeeList) {
                                            if (attendeeForMcuZj != null) {
                                                String userId = attendeeForMcuZj.getEpUserId();
                                                if (StringUtils.isNotEmpty(userId) && attendeeForMcuZj.isMeetingJoined()) {
                                                    userIdList.add(userId);
                                                    chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        AttendeeForMcuZj attendeeForMcuZj = attendeeList.get(0);
                                        if (attendeeForMcuZj != null) {
                                            String userId = attendeeForMcuZj.getEpUserId();
                                            if (StringUtils.isNotEmpty(userId)) {
                                                userIdList.add(userId);
                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                            }
                                        }
                                    }
                                } else {
                                    // 自动指定
                                    if (autoPollingAttends.size() <= autoPollingScreenCount) {
                                        int index = lastAutoPollingIdx + 1;
                                        if (index < autoPollingAttends.size()) {
                                            AttendeeForMcuZj attendeeForMcuZj = autoPollingAttends.get(index);
                                            if (attendeeForMcuZj != null) {
                                                if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                    if (isFill) {
                                                        if (attendeeForMcuZj.isMeetingJoined()) {
                                                            userIdList.add(attendeeForMcuZj.getEpUserId());
                                                            chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                            autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                            autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                            lastAutoPollingIdx = index;
                                                        }
                                                    } else {
                                                        userIdList.add(attendeeForMcuZj.getEpUserId());
                                                        chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                        autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                        autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                        lastAutoPollingIdx = index;
                                                    }
                                                }
                                            }
                                            if (userIdList.size() == 0) {
                                                if (isFill) {
                                                    for (int m = index; m < autoPollingAttends.size(); m++) {
                                                        AttendeeForMcuZj attendeeTemp = autoPollingAttends.get(m);
                                                        if (attendeeTemp != null) {
                                                            if (StringUtils.isNotEmpty(attendeeTemp.getEpUserId()) && attendeeTemp.isMeetingJoined()) {
                                                                userIdList.add(attendeeTemp.getEpUserId());
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
                                            AttendeeForMcuZj attendeeForMcuZj = autoPollingAttends.get(idxReal);
                                            if (attendeeForMcuZj != null) {
                                                if (!autoPollingAttendeeIdSet.contains(attendeeForMcuZj.getId())) {
                                                    if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                                        if (isFill) {
                                                            if (attendeeForMcuZj.isMeetingJoined()) {
                                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                                autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                                autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                                lastAutoPollingIdx = idxReal;
                                                                break;
                                                            }
                                                        } else {
                                                            userIdList.add(attendeeForMcuZj.getEpUserId());
                                                            chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                            autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                            autoPollingAttendeeIdList.add(attendeeForMcuZj.getId());
                                                            lastAutoPollingIdx = idxReal;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            if (userIdList.size() == 0) {
                                                if (!isFill) {
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (userIdList.size() == 0) {
                                userIdList.add("-1");// 留空
                            }
                            rolesLst.add(userIdList);
                            // 获取取消选看和取消轮询的终端
                            for (AttendeeForMcuZj attendeeForMcuZj : attendeeList) {
                                if (attendeeForMcuZj != null) {
                                    if (!chooseSeeAttendeeList.contains(attendeeForMcuZj)) {
                                        if (attendeeForMcuZj.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()
                                            || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                            cancelChooseSeeAttendeeList.add(attendeeForMcuZj);
                                        }
                                    }
                                    if (!pollingAttendeeList.contains(attendeeForMcuZj)) {
                                        if (attendeeForMcuZj.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                            || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                            cancelPollingAttendeeList.add(attendeeForMcuZj);
                                        }
                                    }
                                }
                            }
                        }
                        configInfoSpeaker.setRoles_lst(rolesLst);
                    }
                    if (getDefaultViewIsDisplaySelf() == 1) {
                        configInfoSpeaker.setView_has_self(1);
                    } else {
                        configInfoSpeaker.setView_has_self(0);
                    }
                    Integer pollSecs = getDefaultViewPollingInterval();
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoSpeaker.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoSpeaker);
                }
                if (conferenceContext.getAttendeeOperationForGuest() instanceof DefaultAttendeeOperation) {
                    // 观众
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoGuest.setLayout_mode(2);//传统分屏
                    LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
                    configInfoGuest.setRole("guest");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList = new ArrayList<>();
                    if (conferenceContext.getMasterAttendee() == null) {
                        configInfoGuest.setMosic_id(LayoutTemplates.AUTO.getCode());
                    } else {
                        configInfoGuest.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 一分屏
                        userIdList.add("1010");//主会场
                    }
                    rolesLst.add(userIdList);
                    configInfoGuest.setRoles_lst(rolesLst);
                    if (getDefaultViewIsDisplaySelf() == 1) {
                        configInfoGuest.setView_has_self(1);
                    } else {
                        configInfoGuest.setView_has_self(0);
                    }
                    Integer pollSecs = getDefaultViewPollingInterval();
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoGuest.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoGuest);
                }
            }
            ccUpdateMrMosicConfigRequest.setConfig_info(configInfoList);
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
            boolean success = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequest);
            if (success) {
                autoPollingAttendeeIdListLast = autoPollingAttendeeIdList;
                autoPollingAttendeeIdList = new ArrayList<>();
                Set<AttendeeForMcuZj> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    runtimeCount++;

                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("当前已设置为默认视图");
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                    // 推送默认视图状态消息
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, true);

                    logger.info(messageTip.toString());
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
                for (AttendeeForMcuZj attendeeForMcuZj : cancelChooseSeeAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuZj)) {
                            attendeeForMcuZj.resetUpdateMap();
                        }
                        attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuZj);
                    }
                }
                for (AttendeeForMcuZj attendeeForMcuZj : cancelPollingAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuZj)) {
                            attendeeForMcuZj.resetUpdateMap();
                        }
                        attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuZj);
                    }
                }
                Set<AttendeeForMcuZj> lastChooseSeeAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuZj attendeeForMcuZj : chooseSeeAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        lastChooseSeeAttendeeListNew.add(attendeeForMcuZj);
                        if (!updateAttendeeList.contains(attendeeForMcuZj)) {
                            attendeeForMcuZj.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.YES.getValue());
                        } else {
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuZj);
                    }
                }
                Set<AttendeeForMcuZj> lastPollingAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuZj attendeeForMcuZj : pollingAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        lastPollingAttendeeListNew.add(attendeeForMcuZj);
                        if (!updateAttendeeList.contains(attendeeForMcuZj)) {
                            attendeeForMcuZj.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.YES.getValue());
                        } else {
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuZj.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuZj);
                    }
                }

                for (AttendeeForMcuZj attendeeForMcuZj : updateAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        if ((lastChooseSeeAttendeeList == null && lastPollingAttendeeList == null)
                                || (!lastChooseSeeAttendeeList.contains(attendeeForMcuZj) && lastChooseSeeAttendeeListNew.contains(attendeeForMcuZj))
                                || (!lastPollingAttendeeList.contains(attendeeForMcuZj) && lastPollingAttendeeListNew.contains(attendeeForMcuZj))) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZj.getDeptId()).getDeptName(), attendeeForMcuZj.getName())).append("】");
                            } else {
                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                    messageTip.append("主会场正在选看【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZj.getDeptId()).getDeptName(), attendeeForMcuZj.getName())).append("】");
                                }
                            }
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
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
            for (AttendeeForMcuZj attendee : attendees) {
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
    public boolean contains(AttendeeForMcuZj attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }
}
