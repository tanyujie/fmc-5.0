/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.plc.attendee.model.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.CellInfo;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.mcu.plc.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.mcu.plc.model.enumer.McuPlcLayoutTemplates;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcUpdateMrAutoMosicConfigRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcUpdatePersonalMosicConfigRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcUpdatePersonalMosicTypeRequest;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdateMrAutoMosicConfigResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdateMrMosicConfigResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdatePersonalMosicConfigResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.CcUpdatePersonalMosicTypeResponse;
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

    private volatile List<AttendeeForMcuPlc> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile int lastAutoPollingIdx = -1;
    private volatile int autoPollingScreenCount = 0;
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    private volatile List<String> autoPollingAttendeeIdList = new ArrayList<>();
    private volatile List<String> autoPollingAttendeeIdListLast = new ArrayList<>();
    private volatile Set<AttendeeForMcuPlc> lastChooseSeeAttendeeList = null;
    private volatile Set<AttendeeForMcuPlc> lastPollingAttendeeList = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(McuPlcConferenceContext conferenceContext) {
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
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
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
            List<AttendeeForMcuPlc> attendeeList = cellScreen.getAttendees();
            if (attendeeList.size() > 0) {
                for (AttendeeForMcuPlc attendee : attendeeList) {
                    if (attendee != null) {
                        checkedAttendeeIdSet.add(attendee.getId());
                    }
                }
            } else {
                autoPollingScreenCount++;
            }
        }

        if (!ObjectUtils.isEmpty(attendees)) {
            for (AttendeeForMcuPlc attendee : attendees) {
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
            Set<AttendeeForMcuPlc> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuPlc> chooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuPlc> cancelPollingAttendeeList = new HashSet<>();
            Set<AttendeeForMcuPlc> pollingAttendeeList = new HashSet<>();
            boolean isBroadcast = YesOrNo.convert(getDefaultViewIsBroadcast()) == YesOrNo.YES;
            boolean isFill = YesOrNo.convert(getDefaultViewIsFill()) == YesOrNo.YES;
            boolean isViewSelf = YesOrNo.convert(getDefaultViewIsDisplaySelf()) == YesOrNo.YES;
            SplitScreen splitScreen = getSplitScreen();
            List<CellScreen> cellScreens = splitScreen.getCellScreens();
            McuPlcLayoutTemplates layoutTemplates = McuPlcLayoutTemplates.convert(splitScreen);
            List<String> uuidList = new ArrayList<>();
            List<CellInfo> cellInfoList = new ArrayList<>();

            if (McuPlcLayoutTemplates.AUTO == layoutTemplates) {
                isBroadcast = false;
                int attendeeCount = autoPollingAttends.size();
                if (attendeeCount > 9) {
                    layoutTemplates = McuPlcLayoutTemplates.SCREEN_16;
                } else if (attendeeCount > 4) {
                    layoutTemplates = McuPlcLayoutTemplates.SCREEN_9;
                } else if (attendeeCount > 2) {
                    layoutTemplates = McuPlcLayoutTemplates.SCREEN_4;
                } else if (attendeeCount == 2) {
                    if (isBroadcast || isViewSelf) {
                        layoutTemplates = McuPlcLayoutTemplates.SCREEN_2;
                    } else {
                        layoutTemplates = McuPlcLayoutTemplates.SCREEN_1;
                    }
                } else {
                    layoutTemplates = McuPlcLayoutTemplates.SCREEN_1;
                }
                if (layoutTemplates.getNum() != splitScreen.getCellScreens().size()) {
                    splitScreen.getCellScreens().clear();
                    for (int i = 0; i < layoutTemplates.getNum(); i++) {
                        splitScreen.addCellScreen(i, i);
                    }
                }
            }

            if (McuPlcLayoutTemplates.AUTO != layoutTemplates) {
                for (int i = 0; i < layoutTemplates.getNum(); i++) {
                    CellInfo cellInfo = new CellInfo();
                    cellInfo.setId(String.valueOf(i + 1));
                    CellScreen cellScreen = cellScreens.get(i);
                    List<AttendeeForMcuPlc> attendeeList = cellScreen.getAttendees();
                    if (cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.ROUND && !forceUpdateView) {
                        AttendeeForMcuPlc attendeeForMcuPlc = cellScreen.getPollingAttendee();
                        if (attendeeForMcuPlc != null && StringUtils.isNotEmpty(attendeeForMcuPlc.getParticipantUuid()) && attendeeForMcuPlc.isMeetingJoined()) {
                            String uuid = attendeeForMcuPlc.getParticipantUuid();
                            cellInfo.setForceStatus("forced");
                            cellInfo.setForceId(uuid);
                            cellInfo.setSourceId(uuid);
                            uuidList.add(uuid);
                            pollingAttendeeList.add(attendeeForMcuPlc);
                        } else {
                            if (isFill) {
                                for (int j = 0; j < attendeeList.size() - 1; j++) {
                                    attendeeForMcuPlc = cellScreen.getPollingAttendee();
                                    if (attendeeForMcuPlc != null) {
                                        String uuid = attendeeForMcuPlc.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid) && attendeeForMcuPlc.isMeetingJoined()) {
                                            cellInfo.setForceStatus("forced");
                                            cellInfo.setForceId(uuid);
                                            cellInfo.setSourceId(uuid);
                                            uuidList.add(uuid);
                                            pollingAttendeeList.add(attendeeForMcuPlc);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (attendeeForMcuPlc != null) {
                                    String uuid = attendeeForMcuPlc.getParticipantUuid();
                                    if (StringUtils.isNotEmpty(uuid)) {
                                        cellInfo.setForceStatus("forced");
                                        cellInfo.setForceId(uuid);
                                        cellInfo.setSourceId(uuid);
                                        uuidList.add(uuid);
                                        pollingAttendeeList.add(attendeeForMcuPlc);
                                    }
                                }
                            }
                        }
                    } else {
                        if (attendeeList.size() > 0) {
                            if (isFill) {
                                for (AttendeeForMcuPlc attendeeForMcuPlc : attendeeList) {
                                    if (attendeeForMcuPlc != null) {
                                        String uuid = attendeeForMcuPlc.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid) && attendeeForMcuPlc.isMeetingJoined()) {
                                            cellInfo.setForceStatus("forced");
                                            cellInfo.setForceId(uuid);
                                            cellInfo.setSourceId(uuid);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                AttendeeForMcuPlc attendeeForMcuPlc = attendeeList.get(0);
                                if (attendeeForMcuPlc != null) {
                                    String uuid = attendeeForMcuPlc.getParticipantUuid();
                                    if (StringUtils.isNotEmpty(uuid)) {
                                        cellInfo.setForceStatus("forced");
                                        cellInfo.setForceId(uuid);
                                        cellInfo.setSourceId(uuid);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                    }
                                }
                            }
                        } else {
                            // 自动指定
                            if (autoPollingAttends.size() <= autoPollingScreenCount) {
                                int index = lastAutoPollingIdx + 1;
                                if (index < autoPollingAttends.size()) {
                                    AttendeeForMcuPlc attendeeForMcuPlc = autoPollingAttends.get(index);
                                    if (attendeeForMcuPlc != null) {
                                        String uuid = attendeeForMcuPlc.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid)) {
                                            if (isFill) {
                                                if (attendeeForMcuPlc.isMeetingJoined()) {
                                                    cellInfo.setForceStatus("forced");
                                                    cellInfo.setForceId(uuid);
                                                    cellInfo.setSourceId(uuid);
                                                    uuidList.add(uuid);
                                                    chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
                                                    autoPollingAttendeeIdList.add(attendeeForMcuPlc.getId());
                                                    lastAutoPollingIdx = index;
                                                }
                                            } else {
                                                cellInfo.setForceStatus("forced");
                                                cellInfo.setForceId(uuid);
                                                cellInfo.setSourceId(uuid);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
                                                autoPollingAttendeeIdList.add(attendeeForMcuPlc.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
                                    }
                                    if (StringUtils.isEmpty(cellInfo.getForceId())) {
                                        if (isFill) {
                                            for (int m = index; m < autoPollingAttends.size(); m++) {
                                                AttendeeForMcuPlc attendeeTemp = autoPollingAttends.get(m);
                                                if (attendeeTemp != null) {
                                                    String uuid = attendeeTemp.getParticipantUuid();
                                                    if (StringUtils.isNotEmpty(uuid) && attendeeTemp.isMeetingJoined()) {
                                                        cellInfo.setForceStatus("forced");
                                                        cellInfo.setForceId(uuid);
                                                        cellInfo.setSourceId(uuid);
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
                                    AttendeeForMcuPlc attendeeForMcuPlc = autoPollingAttends.get(idxReal);
                                    if (attendeeForMcuPlc != null) {
                                        if (!autoPollingAttendeeIdSet.contains(attendeeForMcuPlc.getId())) {
                                            String uuid = attendeeForMcuPlc.getParticipantUuid();
                                            if (StringUtils.isNotEmpty(uuid)) {
                                                if (isFill) {
                                                    if (attendeeForMcuPlc.isMeetingJoined()) {
                                                        cellInfo.setForceStatus("forced");
                                                        cellInfo.setForceId(uuid);
                                                        cellInfo.setSourceId(uuid);
                                                        uuidList.add(uuid);
                                                        chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                                        autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
                                                        autoPollingAttendeeIdList.add(attendeeForMcuPlc.getId());
                                                        lastAutoPollingIdx = idxReal;
                                                        break;
                                                    }
                                                } else {
                                                    cellInfo.setForceStatus("forced");
                                                    cellInfo.setForceId(uuid);
                                                    cellInfo.setSourceId(uuid);
                                                    uuidList.add(uuid);
                                                    chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
                                                    autoPollingAttendeeIdList.add(attendeeForMcuPlc.getId());
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (StringUtils.isEmpty(cellInfo.getForceId())) {
                                        if (!isFill) {
                                            lastAutoPollingIdx = idxReal;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (StringUtils.isEmpty(cellInfo.getForceId())) {
                        cellInfo.setForceStatus("blank");
                        cellInfo.setForceId("-1");// 留空
                        cellInfo.setSourceId("-1");// 留空
                    }
                    cellInfoList.add(cellInfo);
                    // 获取取消选看和取消轮询的终端
                    for (AttendeeForMcuPlc attendeeForMcuPlc : attendeeList) {
                        if (attendeeForMcuPlc != null) {
                            if (!chooseSeeAttendeeList.contains(attendeeForMcuPlc)) {
                                if (attendeeForMcuPlc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()
                                    || attendeeForMcuPlc.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelChooseSeeAttendeeList.add(attendeeForMcuPlc);
                                }
                            }
                            if (!pollingAttendeeList.contains(attendeeForMcuPlc)) {
                                if (attendeeForMcuPlc.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                    || attendeeForMcuPlc.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelPollingAttendeeList.add(attendeeForMcuPlc);
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
            AttendeeForMcuPlc masterAttendee = conferenceContext.getMasterAttendee();
            if (isBroadcast) {
                // 广播时，非自动时，主会场和观众看同样分屏
                CcUpdateMrAutoMosicConfigRequest ccUpdateMrAutoMosicConfigRequest = new CcUpdateMrAutoMosicConfigRequest();
                ccUpdateMrAutoMosicConfigRequest.setId(conferenceContext.getConfId());
                ccUpdateMrAutoMosicConfigRequest.setAuto_layout(false);
                CcUpdateMrAutoMosicConfigResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(ccUpdateMrAutoMosicConfigRequest);
                if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                    CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
                    ccUpdateMrMosicConfigRequest.setId(conferenceContext.getConfId());
                    ccUpdateMrMosicConfigRequest.setLayout(layoutTemplates.getCode());
                    ccUpdateMrMosicConfigRequest.setCellInfoList(cellInfoList);
                    CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequest);
                    if (ccUpdateMrMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrMosicConfigResponse.getStatus())) {
                        success = true;
                        // 主会场
                        if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                            CcUpdatePersonalMosicConfigRequest ccUpdatePersonalMosicConfigRequest = new CcUpdatePersonalMosicConfigRequest();
                            ccUpdatePersonalMosicConfigRequest.setId(conferenceContext.getConfId());
                            ccUpdatePersonalMosicConfigRequest.setParty_id(masterAttendee.getParticipantUuid());
                            ccUpdatePersonalMosicConfigRequest.setLayout_type("personal");
                            ccUpdatePersonalMosicConfigRequest.setLayout(layoutTemplates.getCode());
                            ccUpdatePersonalMosicConfigRequest.setCellInfoList(cellInfoList);
                            CcUpdatePersonalMosicConfigResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().updatePersonalMosicConfig(ccUpdatePersonalMosicConfigRequest);
                            if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                                success = true;
                            }
                        }
                        for (String uuid : uuidList) {
                            CcUpdatePersonalMosicConfigRequest ccUpdatePersonalMosicConfigRequest = new CcUpdatePersonalMosicConfigRequest();
                            ccUpdatePersonalMosicConfigRequest.setId(conferenceContext.getConfId());
                            ccUpdatePersonalMosicConfigRequest.setParty_id(uuid);
                            ccUpdatePersonalMosicConfigRequest.setLayout_type("personal");
                            ccUpdatePersonalMosicConfigRequest.setLayout(layoutTemplates.getCode());
                            ccUpdatePersonalMosicConfigRequest.setCellInfoList(cellInfoList);
                            CcUpdatePersonalMosicConfigResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().updatePersonalMosicConfig(ccUpdatePersonalMosicConfigRequest);
                            if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                                success = true;
                            }
                        }
                    }
                }
            } else {
                // 不广播时，非自动时，观众看主会场，主会场看分屏观众。
                // 观众
                if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
                    // 有主会场时，观众看主会场
                    CcUpdateMrAutoMosicConfigRequest ccUpdateMrAutoMosicConfigRequest = new CcUpdateMrAutoMosicConfigRequest();
                    ccUpdateMrAutoMosicConfigRequest.setId(conferenceContext.getConfId());
                    ccUpdateMrAutoMosicConfigRequest.setAuto_layout(false);
                    CcUpdateMrAutoMosicConfigResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(ccUpdateMrAutoMosicConfigRequest);
                    if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                        CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequestForGuest = new CcUpdateMrMosicConfigRequest();
                        ccUpdateMrMosicConfigRequestForGuest.setId(conferenceContext.getConfId());
                        ccUpdateMrMosicConfigRequestForGuest.setLayout(McuPlcLayoutTemplates.SCREEN_1.getCode());
                        List<CellInfo> cellInfoListForGuest = new ArrayList<>();
                        CellInfo cellInfoForGuest1 = new CellInfo();
                        cellInfoForGuest1.setId("1");
                        cellInfoForGuest1.setForceStatus("forced");
                        cellInfoForGuest1.setForceId(masterAttendee.getParticipantUuid());
                        cellInfoForGuest1.setSourceId(masterAttendee.getParticipantUuid());
                        cellInfoListForGuest.add(cellInfoForGuest1);
                        ccUpdateMrMosicConfigRequestForGuest.setCellInfoList(cellInfoListForGuest);
                        CcUpdateMrMosicConfigResponse ccUpdateMrMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequestForGuest);
                        if (ccUpdateMrMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrMosicConfigResponse.getStatus())) {
                            success = true;
                            // 主会场
                            if (masterAttendee != null && StringUtils.isNotEmpty(masterAttendee.getParticipantUuid())) {
                                CcUpdatePersonalMosicConfigRequest ccUpdatePersonalMosicConfigRequest = new CcUpdatePersonalMosicConfigRequest();
                                ccUpdatePersonalMosicConfigRequest.setId(conferenceContext.getConfId());
                                ccUpdatePersonalMosicConfigRequest.setParty_id(masterAttendee.getParticipantUuid());
                                ccUpdatePersonalMosicConfigRequest.setLayout_type("personal");
                                ccUpdatePersonalMosicConfigRequest.setLayout(layoutTemplates.getCode());
                                ccUpdatePersonalMosicConfigRequest.setCellInfoList(cellInfoList);
                                CcUpdatePersonalMosicConfigResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().updatePersonalMosicConfig(ccUpdatePersonalMosicConfigRequest);
                                if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                                    success = true;
                                }
                            }
                        }
                    }
                } else {
                    // 无主会场时，观众自动
                    CcUpdateMrAutoMosicConfigRequest ccUpdateMrAutoMosicConfigRequest = new CcUpdateMrAutoMosicConfigRequest();
                    ccUpdateMrAutoMosicConfigRequest.setId(conferenceContext.getConfId());
                    ccUpdateMrAutoMosicConfigRequest.setAuto_layout(true);
                    CcUpdateMrAutoMosicConfigResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(ccUpdateMrAutoMosicConfigRequest);
                    if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                        success = true;
                    }
                }
            }
            if (success) {
                if (lastUpdateTime == 0) {
                    for (AttendeeForMcuPlc attendeeForMcuPlc : autoPollingAttends) {
                        if (attendeeForMcuPlc != null && StringUtils.isNotEmpty(attendeeForMcuPlc.getParticipantUuid())) {
                            if (isBroadcast) {
                                if (uuidList.contains(attendeeForMcuPlc.getParticipantUuid())) {
                                    continue;
                                }
                            } else {
                                if (layoutTemplates != McuPlcLayoutTemplates.AUTO) {
                                    if (masterAttendee != null && attendeeForMcuPlc.getParticipantUuid().equals(masterAttendee.getParticipantUuid())) {
                                        continue;
                                    }
                                }
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
                autoPollingAttendeeIdListLast = autoPollingAttendeeIdList;
                autoPollingAttendeeIdList = new ArrayList<>();
                Set<AttendeeForMcuPlc> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    runtimeCount++;

                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("当前已设置为默认视图");
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                    // 推送默认视图状态消息
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, true);

                    logger.info(messageTip.toString());
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
                for (AttendeeForMcuPlc attendeeForMcuPlc : cancelChooseSeeAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuPlc)) {
                            attendeeForMcuPlc.resetUpdateMap();
                        }
                        attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuPlc);
                    }
                }
                for (AttendeeForMcuPlc attendeeForMcuPlc : cancelPollingAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuPlc)) {
                            attendeeForMcuPlc.resetUpdateMap();
                        }
                        attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuPlc);
                    }
                }
                Set<AttendeeForMcuPlc> lastChooseSeeAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuPlc attendeeForMcuPlc : chooseSeeAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        lastChooseSeeAttendeeListNew.add(attendeeForMcuPlc);
                        if (!updateAttendeeList.contains(attendeeForMcuPlc)) {
                            attendeeForMcuPlc.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.YES.getValue());
                            attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        } else {
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuPlc);
                    }
                }
                Set<AttendeeForMcuPlc> lastPollingAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuPlc attendeeForMcuPlc : pollingAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        lastPollingAttendeeListNew.add(attendeeForMcuPlc);
                        if (!updateAttendeeList.contains(attendeeForMcuPlc)) {
                            attendeeForMcuPlc.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.YES.getValue());
                            attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        } else {
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuPlc.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuPlc);
                    }
                }

                for (AttendeeForMcuPlc attendeeForMcuPlc : updateAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        if ((lastChooseSeeAttendeeList == null && lastPollingAttendeeList == null)
                                || (!lastChooseSeeAttendeeList.contains(attendeeForMcuPlc) && lastChooseSeeAttendeeListNew.contains(attendeeForMcuPlc))
                                || (!lastPollingAttendeeList.contains(attendeeForMcuPlc) && lastPollingAttendeeListNew.contains(attendeeForMcuPlc))) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuPlc.getDeptId()).getDeptName(), attendeeForMcuPlc.getName())).append("】");
                            } else {
                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                    messageTip.append("主会场正在选看【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuPlc.getDeptId()).getDeptName(), attendeeForMcuPlc.getName())).append("】");
                                }
                            }
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
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
            for (AttendeeForMcuPlc attendee : attendees) {
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
    public boolean contains(AttendeeForMcuPlc attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }
}
