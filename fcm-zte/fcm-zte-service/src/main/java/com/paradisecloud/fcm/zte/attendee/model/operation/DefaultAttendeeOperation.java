/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.attendee.model.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.MultiViewSelect;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.zte.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.zte.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.zte.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.zte.model.enumer.McuZteLayoutTemplates;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.request.*;
import com.zte.m900.response.*;
import org.apache.logging.log4j.util.Strings;
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

    private volatile List<AttendeeForMcuZte> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile int lastAutoPollingIdx = -1;
    private volatile int autoPollingScreenCount = 0;
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    private volatile List<String> autoPollingAttendeeIdList = new ArrayList<>();
    private volatile List<String> autoPollingAttendeeIdListLast = new ArrayList<>();
    private volatile Set<AttendeeForMcuZte> lastChooseSeeAttendeeList = null;
    private volatile Set<AttendeeForMcuZte> lastPollingAttendeeList = null;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(McuZteConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public void initSplitScreen() {
        int maxImportance = YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
        if (Strings.isBlank(defaultViewLayout)|| Objects.equals(defaultViewLayout,McuZteLayoutTemplates.AUTOMATIC.getCode())||Objects.equals(defaultViewLayout,McuZteLayoutTemplates.AUTO.getCode())){
            this.splitScreen = new AutomaticSplitScreen();
            return;
        }
        McuZteLayoutTemplates mcuZteLayoutTemplates = McuZteLayoutTemplates.valueOf(defaultViewLayout);
        String screenLayout = McuZteLayoutTemplates.getScreenLayout(mcuZteLayoutTemplates);

        if (OneSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new OneSplitScreen(maxImportance);
        } else if (FourSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new FourSplitScreen(maxImportance);
        } else if (NineSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new NineSplitScreen(maxImportance);
        } else if (SixteenSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new SixteenSplitScreen(maxImportance);
        } else if (OnePlusFiveSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new OnePlusFiveSplitScreen(maxImportance);
        } else if (OnePlusSevenSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new OnePlusSevenSplitScreen(maxImportance);
        } else if (OnePlusNineSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new OnePlusNineSplitScreen(maxImportance);
        } else if (TwoSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new TwoSplitScreen(maxImportance);
        }else if (ThreeSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new ThreeSplitScreen(maxImportance);
        }else if (EightSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new EightSplitScreen(maxImportance);
        }else if (FiveSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new FiveSplitScreen(maxImportance);
        }else if (ThirteenSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new ThirteenSplitScreen(maxImportance);
        }
        else if (SeventeenSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new SeventeenSplitScreen(maxImportance);
        }else if (SevenSplitScreen.LAYOUT.equals(screenLayout)) {
            this.splitScreen = new SevenSplitScreen(maxImportance);
        }
        else {
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
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
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
            List<AttendeeForMcuZte> attendeeList = cellScreen.getAttendees();
            if (attendeeList.size() > 0) {
                for (AttendeeForMcuZte attendee : attendeeList) {
                    if (attendee != null) {
                        checkedAttendeeIdSet.add(attendee.getId());
                    }
                }
            } else {
                autoPollingScreenCount++;
            }
        }

        if (!ObjectUtils.isEmpty(attendees)) {
            for (AttendeeForMcuZte attendee : attendees) {
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

        lastAutoPollingIdx = -1;
    }

    private void operateScreen() {
        boolean forceUpdateView = isForceUpdateView();
        if (forceUpdateView) {
            setForceUpdateView(false);
            McuZteLayoutTemplates layoutTemplates = McuZteLayoutTemplates.convert(getSplitScreen());
            if (McuZteLayoutTemplates.AUTO == layoutTemplates) {
                forceUpdateView = false;
            }
            if (McuZteLayoutTemplates.AUTOMATIC == layoutTemplates) {
                forceUpdateView = false;
            }
            if (getSplitScreen().getAutoPollingCellScreenCount() == 0) {
                forceUpdateView = false;
            }
        }
        long lastUpdateTime = getLastUpdateTime();
        if(lastUpdateTime==0&&forceUpdateView==true){
            forceUpdateView=false;
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
            Set<AttendeeForMcuZte> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZte> chooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZte> cancelPollingAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZte> pollingAttendeeList = new HashSet<>();
            boolean isBroadcast = YesOrNo.convert(getDefaultViewIsBroadcast()) == YesOrNo.YES;
            boolean isFill = YesOrNo.convert(getDefaultViewIsFill()) == YesOrNo.YES;
            boolean isViewSelf = YesOrNo.convert(getDefaultViewIsDisplaySelf()) == YesOrNo.YES;
            SplitScreen splitScreen = getSplitScreen();
            List<CellScreen> cellScreens = splitScreen.getCellScreens();

            List<String> uuidList = new ArrayList<>();
            List<MultiViewSelect> cellInfoList = new ArrayList<>();

            if (this.splitScreen instanceof AutomaticSplitScreen) {
//                isBroadcast = false;
//                int attendeeCount = autoPollingAttends.size();
//                if (attendeeCount > 9) {
//                    layoutTemplates = McuZteLayoutTemplates.SCREEN_16_0;
//                } else if (attendeeCount > 4) {
//                    layoutTemplates = McuZteLayoutTemplates.SCREEN_9_0;
//                } else if (attendeeCount > 2) {
//                    layoutTemplates = McuZteLayoutTemplates.SCREEN_4_0;
//                } else if (attendeeCount == 2) {
//                    if (isBroadcast || isViewSelf) {
//                        layoutTemplates = McuZteLayoutTemplates.SCREEN_2_0;
//                    } else {
//                        layoutTemplates = McuZteLayoutTemplates.SCREEN_1_0;
//                    }
//                } else {
//                    layoutTemplates = McuZteLayoutTemplates.SCREEN_1_0;
//                }
//                if (layoutTemplates.getNum() != splitScreen.getCellScreens().size()) {
//                    splitScreen.getCellScreens().clear();
//                    for (int i = 0; i < layoutTemplates.getNum(); i++) {
//                        splitScreen.addCellScreen(i, i);
//                    }
//                }

                SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                ccSwitchMultiCtrlModeRequest.setMultiViewMode("auto");
                SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
                return;
            }
            //多画面配置
            McuZteLayoutTemplates layoutTemplates = McuZteLayoutTemplates.valueOf(defaultViewLayout);



            if (McuZteLayoutTemplates.AUTO != layoutTemplates) {
                for (int i = 0; i < layoutTemplates.getNum(); i++) {
                    MultiViewSelect cellInfo = new MultiViewSelect();
                    cellInfo.setViewNo(i);
                    cellInfo.setTerminalIdentifier("-2");
                    CellScreen cellScreen = cellScreens.get(i);
                    cellInfo.setMediaType(1);
                    List<AttendeeForMcuZte> attendeeList = cellScreen.getAttendees();
                    if (cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.ROUND && !forceUpdateView) {
                        AttendeeForMcuZte attendeeForMcuZte = cellScreen.getPollingAttendee();
                        if (attendeeForMcuZte != null && StringUtils.isNotEmpty(attendeeForMcuZte.getParticipantUuid()) && attendeeForMcuZte.isMeetingJoined()) {
                            String uuid = attendeeForMcuZte.getParticipantUuid();
                            cellInfo.setTerminalIdentifier(uuid);
                            uuidList.add(uuid);
                            pollingAttendeeList.add(attendeeForMcuZte);
                        } else {
                            if (isFill) {
                                for (int j = 0; j < attendeeList.size() - 1; j++) {
                                    attendeeForMcuZte = cellScreen.getPollingAttendee();
                                    if (attendeeForMcuZte != null) {
                                        String uuid = attendeeForMcuZte.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid) && attendeeForMcuZte.isMeetingJoined()) {
                                            cellInfo.setTerminalIdentifier(uuid);
                                            uuidList.add(uuid);
                                            pollingAttendeeList.add(attendeeForMcuZte);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                if (attendeeForMcuZte != null) {
                                    String uuid = attendeeForMcuZte.getParticipantUuid();
                                    if (StringUtils.isNotEmpty(uuid)) {
                                        cellInfo.setTerminalIdentifier(uuid);
                                        uuidList.add(uuid);
                                        pollingAttendeeList.add(attendeeForMcuZte);
                                    }
                                }
                            }
                        }
                    } else if(cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.CHOOSE_SEE) {
                        if (attendeeList.size() > 0) {
                            if (isFill) {
                                for (AttendeeForMcuZte attendeeForMcuZte : attendeeList) {
                                    if (attendeeForMcuZte != null) {
                                        String uuid = attendeeForMcuZte.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid) && attendeeForMcuZte.isMeetingJoined()) {
                                            cellInfo.setTerminalIdentifier(uuid);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeForMcuZte);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                AttendeeForMcuZte attendeeForMcuZte = attendeeList.get(0);
                                if (attendeeForMcuZte != null) {
                                    String uuid = attendeeForMcuZte.getParticipantUuid();
                                    if (StringUtils.isNotEmpty(uuid)) {
                                        cellInfo.setTerminalIdentifier(uuid);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuZte);
                                    }
                                }
                            }
                        } else {
                            // 自动指定
                            if (autoPollingAttends.size() <= autoPollingScreenCount) {
                                int index = lastAutoPollingIdx + 1;
                                if (index < autoPollingAttends.size()) {
                                    AttendeeForMcuZte attendeeForMcuZte = autoPollingAttends.get(index);
                                    if (attendeeForMcuZte != null) {
                                        String uuid = attendeeForMcuZte.getParticipantUuid();
                                        if (StringUtils.isNotEmpty(uuid)) {
                                            if (isFill) {
                                                if (attendeeForMcuZte.isMeetingJoined()) {
                                                    cellInfo.setTerminalIdentifier(uuid);
                                                    uuidList.add(uuid);
                                                    chooseSeeAttendeeList.add(attendeeForMcuZte);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                                    autoPollingAttendeeIdList.add(attendeeForMcuZte.getId());
                                                    lastAutoPollingIdx = index;
                                                }
                                            } else {
                                                cellInfo.setTerminalIdentifier(uuid);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(attendeeForMcuZte);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                                autoPollingAttendeeIdList.add(attendeeForMcuZte.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
                                    }
                                   // if (StringUtils.isEmpty(cellInfo.getTerminalIdentifier())) {
                                        if (Objects.equals("-2",cellInfo.getTerminalIdentifier())) {
                                        if (isFill) {
                                            for (int m = index; m < autoPollingAttends.size(); m++) {
                                                AttendeeForMcuZte attendeeTemp = autoPollingAttends.get(m);
                                                if (attendeeTemp != null) {
                                                    String uuid = attendeeTemp.getParticipantUuid();
                                                    if (StringUtils.isNotEmpty(uuid) && attendeeTemp.isMeetingJoined()) {
                                                        cellInfo.setTerminalIdentifier(uuid);
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
                                    AttendeeForMcuZte attendeeForMcuZte = autoPollingAttends.get(idxReal);
                                    if (attendeeForMcuZte != null) {
                                        if (!autoPollingAttendeeIdSet.contains(attendeeForMcuZte.getId())) {
                                            String uuid = attendeeForMcuZte.getParticipantUuid();
                                            if (StringUtils.isNotEmpty(uuid)) {
                                                if (isFill) {
                                                    if (attendeeForMcuZte.isMeetingJoined()) {
                                                        cellInfo.setTerminalIdentifier(uuid);
                                                        uuidList.add(uuid);
                                                        chooseSeeAttendeeList.add(attendeeForMcuZte);
                                                        autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                                        autoPollingAttendeeIdList.add(attendeeForMcuZte.getId());
                                                        lastAutoPollingIdx = idxReal;
                                                        break;
                                                    }
                                                } else {
                                                    cellInfo.setTerminalIdentifier(uuid);
                                                    uuidList.add(uuid);
                                                    chooseSeeAttendeeList.add(attendeeForMcuZte);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                                    autoPollingAttendeeIdList.add(attendeeForMcuZte.getId());
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (Objects.equals("-2",cellInfo.getTerminalIdentifier())) {
                                    //if (StringUtils.isEmpty(cellInfo.getTerminalIdentifier())) {
                                        if (!isFill) {
                                            lastAutoPollingIdx = idxReal;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        String presentAttendeeId = conferenceContext.getPresentAttendeeId();
                        if(Strings.isNotBlank(presentAttendeeId)){
                            AttendeeForMcuZte attendeeById = conferenceContext.getAttendeeById(presentAttendeeId);
                            cellInfo.setTerminalIdentifier(attendeeById.getParticipantUuid());
                            cellInfo.setMediaType(2);
                        }

                    }

                    cellInfoList.add(cellInfo);
                    // 获取取消选看和取消轮询的终端
                    for (AttendeeForMcuZte attendeeForMcuZte : attendeeList) {
                        if (attendeeForMcuZte != null) {
                            if (!chooseSeeAttendeeList.contains(attendeeForMcuZte)) {
                                if (attendeeForMcuZte.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()
                                        || attendeeForMcuZte.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelChooseSeeAttendeeList.add(attendeeForMcuZte);
                                }
                            }
                            if (!pollingAttendeeList.contains(attendeeForMcuZte)) {
                                if (attendeeForMcuZte.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                        || attendeeForMcuZte.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelPollingAttendeeList.add(attendeeForMcuZte);
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
            AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
            if (isBroadcast) {

                // 广播时，非自动时，主会场和观众看同样分屏
                SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                ccSwitchMultiCtrlModeRequest.setMultiViewMode("manual");
                SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
                if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                    SetMultiViewNumRequest setMultiViewNumRequest=new SetMultiViewNumRequest();
                    setMultiViewNumRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    setMultiViewNumRequest.setMultiViewNum(layoutTemplates.getNum());

                    if(layoutTemplates!=null){
                        String code = layoutTemplates.getCode();
                        setMultiViewNumRequest.setLayout(Integer.parseInt(code));
                    }else {
                        setMultiViewNumRequest.setLayout(0);
                    }
                    SetMultiViewNumResponse setMultiViewNumResponse = conferenceContext.getConferenceControlApi().setMultiViewNum(setMultiViewNumRequest);
                    if(setMultiViewNumResponse==null||!Objects.equals(setMultiViewNumResponse.getResult(), CommonResponse.STATUS_OK)){
                        logger.info("切换默认布局失败");
                        return;
                    }
                    for (MultiViewSelect multiViewSelect : cellInfoList) {
                        MultiViewSelectRequest multiViewSelectRequest=new MultiViewSelectRequest();
                        multiViewSelectRequest.setConferenceIdentifier(conferenceContext.getConfId());
                        multiViewSelectRequest.setMediaType(multiViewSelect.getMediaType());
                        multiViewSelectRequest.setViewNo(multiViewSelect.getViewNo());
                        multiViewSelectRequest.setTerminalIdentifier(multiViewSelect.getTerminalIdentifier());
                        MultiViewSelectResponse ccUpdatePersonalMosicConfigResponse = conferenceContext.getConferenceControlApi().multiViewSelect(multiViewSelectRequest);
                        if (ccUpdatePersonalMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdatePersonalMosicConfigResponse.getStatus())) {
                            success = true;
                        }
                    }
                }
                if(this.splitScreen instanceof OneSplitScreen){
                    //设置广播源
                    BoardcastParticipantRequest boardcastParticipantRequest = new BoardcastParticipantRequest();
                    boardcastParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    boardcastParticipantRequest.setTerminalIdentifier(cellInfoList.get(0).getTerminalIdentifier());
                    conferenceContext.getConferenceControlApi().boardcastParticipant(boardcastParticipantRequest);

                    SelectParticipantRequest selectParticipantRequest=new SelectParticipantRequest();
                    selectParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    selectParticipantRequest.setDesTerminalIdentifier(cellInfoList.get(0).getTerminalIdentifier());
                    selectParticipantRequest.setSrcTerminalIdentifier(cellInfoList.get(0).getTerminalIdentifier());
                    SelectParticipantResponse selectParticipantResponse = conferenceContext.getConferenceControlApi().selectParticipant(selectParticipantRequest);
                    if (selectParticipantResponse != null && CommonResponse.STATUS_OK.equals(selectParticipantResponse.getStatus())) {
                        success = true;
                    }
                }else {
                    SelectMultiOrSingleViewRequest selectMultiOrSingleViewRequest=new SelectMultiOrSingleViewRequest();
                    selectMultiOrSingleViewRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    selectMultiOrSingleViewRequest.setCmdType("AllMultiView");
                    SelectMultiOrSingleViewResponse selectMultiOrSingleViewResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(selectMultiOrSingleViewRequest);
                    if (selectMultiOrSingleViewResponse != null && CommonResponse.STATUS_OK.equals(selectMultiOrSingleViewResponse.getStatus())) {
                        success = true;
                    }
                }

            } else {
                // 不广播时，非自动时，观众看主会场，主会场看分屏观众。
                // 观众
                //:广播源看多画面，其他会场看
                //单画面
                if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
                    SetMultiViewNumRequest setMultiViewNumRequest=new SetMultiViewNumRequest();
                    setMultiViewNumRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    setMultiViewNumRequest.setMultiViewNum(layoutTemplates.getNum());

                    if(layoutTemplates!=null){
                        String code = layoutTemplates.getCode();
                        setMultiViewNumRequest.setLayout(Integer.parseInt(code));
                    }else {
                        setMultiViewNumRequest.setLayout(0);
                    }
                    SetMultiViewNumResponse setMultiViewNumResponse = conferenceContext.getConferenceControlApi().setMultiViewNum(setMultiViewNumRequest);


                    // 有主会场时，观众看主会场
                    SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                    ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    ccSwitchMultiCtrlModeRequest.setMultiViewMode("manual");
                    SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);


                    if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
                        for (MultiViewSelect multiViewSelect : cellInfoList) {
                            if(Strings.isNotBlank(multiViewSelect.getTerminalIdentifier())){
                                MultiViewSelectRequest multiViewSelectRequest=new MultiViewSelectRequest();
                                multiViewSelectRequest.setConferenceIdentifier(conferenceContext.getConfId());
                                multiViewSelectRequest.setMediaType(multiViewSelect.getMediaType());
                                multiViewSelectRequest.setViewNo(multiViewSelect.getViewNo());
                                multiViewSelectRequest.setTerminalIdentifier(multiViewSelect.getTerminalIdentifier());
                                conferenceContext.getConferenceControlApi().multiViewSelect(multiViewSelectRequest);
                            }
                        }
                    }
                    if(this.splitScreen instanceof OneSplitScreen){
                        //设置广播源
                        BoardcastParticipantRequest boardcastParticipantRequest = new BoardcastParticipantRequest();
                        boardcastParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                        boardcastParticipantRequest.setTerminalIdentifier(cellInfoList.get(0).getTerminalIdentifier());
                        conferenceContext.getConferenceControlApi().boardcastParticipant(boardcastParticipantRequest);

                        SelectParticipantRequest selectParticipantRequest=new SelectParticipantRequest();
                        selectParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                        selectParticipantRequest.setDesTerminalIdentifier(masterAttendee.getParticipantUuid());
                        selectParticipantRequest.setSrcTerminalIdentifier(cellInfoList.get(0).getTerminalIdentifier());
                        SelectParticipantResponse selectParticipantResponse = conferenceContext.getConferenceControlApi().selectParticipant(selectParticipantRequest);
                        if (selectParticipantResponse != null && CommonResponse.STATUS_OK.equals(selectParticipantResponse.getStatus())) {
                            success = true;
                        }
                    }else {
                        SelectMultiOrSingleViewRequest selectMultiOrSingleViewRequest=new SelectMultiOrSingleViewRequest();
                        selectMultiOrSingleViewRequest.setConferenceIdentifier(conferenceContext.getConfId());
                        selectMultiOrSingleViewRequest.setCmdType("BroadcasterMultiViewOtherSingleView");
                        SelectMultiOrSingleViewResponse selectMultiOrSingleViewResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(selectMultiOrSingleViewRequest);
                        if (selectMultiOrSingleViewResponse != null && CommonResponse.STATUS_OK.equals(selectMultiOrSingleViewResponse.getStatus())) {
                            success = true;
                        }
                    }


                } else {
                    // 无主会场时,所有会场看单画面
                    SelectMultiOrSingleViewRequest selectMultiOrSingleViewRequest=new SelectMultiOrSingleViewRequest();
                    selectMultiOrSingleViewRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    selectMultiOrSingleViewRequest.setCmdType("AllSingleView");
                    SelectMultiOrSingleViewResponse selectMultiOrSingleViewResponse = conferenceContext.getConferenceControlApi().updateMrAutoMosicConfig(selectMultiOrSingleViewRequest);
                    if (selectMultiOrSingleViewResponse != null && CommonResponse.STATUS_OK.equals(selectMultiOrSingleViewResponse.getStatus())) {
                        success = true;
                    }
                }
            }
            if (success) {
                if (lastUpdateTime == 0) {
                    for (AttendeeForMcuZte attendeeForMcuZte : autoPollingAttends) {
                        if (attendeeForMcuZte != null && StringUtils.isNotEmpty(attendeeForMcuZte.getParticipantUuid())) {
                            if (isBroadcast) {
                                if (uuidList.contains(attendeeForMcuZte.getParticipantUuid())) {
                                    continue;
                                }
                            } else {
                                if (layoutTemplates != McuZteLayoutTemplates.AUTO) {
                                    if (masterAttendee != null && attendeeForMcuZte.getParticipantUuid().equals(masterAttendee.getParticipantUuid())) {
                                        continue;
                                    }
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
                Set<AttendeeForMcuZte> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    runtimeCount++;

                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("当前已设置为默认视图");
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

                    // 推送默认视图状态消息
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, true);

                    logger.info(messageTip.toString());
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
                for (AttendeeForMcuZte attendeeForMcuZte : cancelChooseSeeAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuZte)) {
                            attendeeForMcuZte.resetUpdateMap();
                        }
                        attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuZte);
                    }
                }
                for (AttendeeForMcuZte attendeeForMcuZte : cancelPollingAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuZte)) {
                            attendeeForMcuZte.resetUpdateMap();
                        }
                        attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                        attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuZte);
                    }
                }
                Set<AttendeeForMcuZte> lastChooseSeeAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuZte attendeeForMcuZte : chooseSeeAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        lastChooseSeeAttendeeListNew.add(attendeeForMcuZte);
                        if (!updateAttendeeList.contains(attendeeForMcuZte)) {
                            attendeeForMcuZte.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.YES.getValue());
                            attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        } else {
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuZte);
                    }
                }
                Set<AttendeeForMcuZte> lastPollingAttendeeListNew = new HashSet<>();
                for (AttendeeForMcuZte attendeeForMcuZte : pollingAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        lastPollingAttendeeListNew.add(attendeeForMcuZte);
                        if (!updateAttendeeList.contains(attendeeForMcuZte)) {
                            attendeeForMcuZte.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.YES.getValue());
                            attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                        } else {
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.NO.getValue());
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                            } else {
                                attendeeForMcuZte.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                            }
                        }
                        updateAttendeeList.add(attendeeForMcuZte);
                    }
                }

                for (AttendeeForMcuZte attendeeForMcuZte : updateAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        if ((lastChooseSeeAttendeeList == null && lastPollingAttendeeList == null)
                                || (!lastChooseSeeAttendeeList.contains(attendeeForMcuZte) && lastChooseSeeAttendeeListNew.contains(attendeeForMcuZte))
                                || (!lastPollingAttendeeList.contains(attendeeForMcuZte) && lastPollingAttendeeListNew.contains(attendeeForMcuZte))) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZte.getDeptId()).getDeptName(), attendeeForMcuZte.getName())).append("】");
                            } else {
                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined()) {
                                    messageTip.append("主会场正在选看【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZte.getDeptId()).getDeptName(), attendeeForMcuZte.getName())).append("】");
                                }
                            }
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                        }
                        if(attendeeForMcuZte.getUpdateMap()!=null&&attendeeForMcuZte.getUpdateMap().size()>1){
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                        }

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
            for (AttendeeForMcuZte attendee : attendees) {
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
    public boolean contains(AttendeeForMcuZte attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }
}
