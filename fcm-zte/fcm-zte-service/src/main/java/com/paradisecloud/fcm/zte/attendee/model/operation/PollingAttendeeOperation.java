/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version  V1.0
 */
package com.paradisecloud.fcm.zte.attendee.model.operation;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.zte.attendee.exception.AttendeeRepeatException;
import com.paradisecloud.fcm.zte.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.zte.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.zte.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.MultiViewSelect;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.McuAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.zte.model.enumer.McuZteLayoutTemplates;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.request.*;
import com.zte.m900.response.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * <pre>请加上该类的描述</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-26 15:55
 */
public class PollingAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-26 15:55
     */
    private static final long serialVersionUID = 1L;

    private volatile PollingScheme pollingScheme;
    private volatile List<PollingAttendee> pollingAttendeeList;
    private volatile List<AttendeeForMcuZte> autoPollingAttends = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();
    private volatile int lastAutoPollingIdx = -1;
    private volatile int autoPollingScreenCount = 0;
    private volatile Set<String> autoPollingAttendeeIdSet = new HashSet<>();
    private volatile long runtimeCount = 0;
    /**
     * 暂停
     */
    private volatile boolean isPause;
    private volatile boolean isPollingDownCascade;
    private volatile BaseAttendee lastDownCascadeMcuAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param pollingScheme
     * @author lilinhai
     * @since 2021-02-26 15:56
     */
    public PollingAttendeeOperation(McuZteConferenceContext conferenceContext, PollingScheme pollingScheme) {
        super(conferenceContext);
        this.pollingScheme = pollingScheme;
        initSplitScreen();
        Assert.isTrue(!(this.splitScreen instanceof AutomaticSplitScreen), "轮询操作不支持自动分屏");
    }

    public void initSplitScreen() {
        int maxImportance = this.pollingScheme.getIsBroadcast() == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
        String screenLayout_ = this.pollingScheme.getLayout();
        McuZteLayoutTemplates mcuZteLayoutTemplates = McuZteLayoutTemplates.valueOf(screenLayout_);
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

    /**
     * 解析
     *
     * @return List<PollingAttendee>
     * @author lilinhai
     * @since 2021-04-09 14:01
     */
    private void parse() throws Exception {
        Set<String> idSet = new HashSet<>();
        pollingScheme.removeInvalidAttendee(conferenceContext);
        pollingAttendeeList = new ArrayList<>();
        List<PollingAttendee> pollingAttendeeListTemp = pollingScheme.getPollingStrategy().getStrategy().parse(conferenceContext, pollingScheme.getDeptPollingAttendeesList(), splitScreen);
        for (PollingAttendee pollingAttendee : pollingAttendeeListTemp) {
            BaseAttendee attendee = pollingAttendee.getAttendee() != null ? pollingAttendee.getAttendee() : pollingAttendee.getDownCascadeAttendee();
            if (!idSet.add(attendee.getId())) {
                StringBuilder messageTip1 = new StringBuilder();
                messageTip1.append("轮询列表存在重复参会【" + attendee.getId() + ", " + attendee.getName() + "】，异常终止！");
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
                throw new AttendeeRepeatException(messageTip1.toString());
            }
            if (pollingAttendee.getDownCascadeAttendee() != null) {
                isPollingDownCascade = true;
            }
            boolean needAdd = true;
            if (conferenceContext.getMasterAttendee() != null && attendee.getId().equals(conferenceContext.getMasterAttendee().getId())) {
                if (pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                    needAdd = false;
                    autoPollingScreenCount=autoPollingScreenCount-1;
                }
            }
            if (needAdd) {
                pollingAttendeeList.add(pollingAttendee);
            }
        }
        if (isPollingDownCascade) {
            int maxImportance = this.pollingScheme.getIsBroadcast() == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
            this.splitScreen = new OneSplitScreen(maxImportance);
        }
        idSet.clear();
    }

    /**
     * <p>Get Method   :   isPause boolean</p>
     *
     * @return isPause
     */
    public boolean isPause() {
        return isPause;
    }

    /**
     * <p>Set Method   :   isPause boolean</p>
     *
     * @param isPause
     */
    public void setPause(boolean isPause) {
        this.isPause = isPause;
        synchronized (this) {
            if (isPause) {
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        boolean canceled = isCancel();
        super.cancel();

        if (!canceled) {
            logger.info("----结束轮询----" + conferenceContext.getName());
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("轮询已结束");
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);

            RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
            Object polledListObj = null;
            try {
                polledListObj = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
            } catch (Exception e){}
            if (polledListObj != null) {
                JSONArray polledList = (JSONArray) polledListObj;
                for (Object polledObj : polledList) {
                    AttendeeForMcuZte attendeeForMcuZj = (AttendeeForMcuZte) polledObj;
                    if (attendeeForMcuZj != null) {
                        mqttService.sendPollingAttendMessage(attendeeForMcuZj, conferenceContext, false);
                    }
                }
                redisCache.deleteObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
            }
        }
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() throws Exception {
        if (isCancel() || isPause()) {
            return;
        }
        initTargetAttendees();
        operateScreen();
    }

    private void initTargetAttendees() throws Exception {
        autoPollingScreenCount = getSplitScreen().getCellScreens().size();
        autoPollingAttendeeIdSet.clear();
        autoPollingAttends.clear();
        checkedAttendeeIdSet.clear();
        this.parse();

        boolean isViewSelf = this.pollingScheme.getIsBroadcast() == YesOrNo.NO;

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

        if (pollingAttendeeList.size() <= autoPollingScreenCount) {
            lastAutoPollingIdx = -1;
        }
    }

    private void operateScreen() throws Exception {

        if (ObjectUtils.isEmpty(pollingAttendeeList))
        {
            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "轮询方案参会列表为空，轮询停止！");
            throw new PollingCancelException("轮询方案参会列表为空，轮询停止！");
        }
        long lastUpdateTime = getLastUpdateTime();
        long currentTimeMillis = System.currentTimeMillis();
        int interval = this.pollingScheme.getInterval();
        if (interval < 10) {
            interval = 10;
        }
        if (currentTimeMillis - getLastUpdateTime() >= interval * 1000) {
            setLastUpdateTime(currentTimeMillis);
            Set<AttendeeForMcuZte> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZte> chooseSeeAttendeeList = new HashSet<>();
            boolean isBroadcast = this.pollingScheme.getIsBroadcast() == YesOrNo.YES;
            boolean isFill = this.pollingScheme.getIsFill() == YesOrNo.YES;
            boolean isViewSelf = this.pollingScheme.getIsBroadcast() == YesOrNo.NO;
            BaseAttendee currentDownCascadeMcuAttendee = null;
            McuZteLayoutTemplates layoutTemplates = McuZteLayoutTemplates.convert(getSplitScreen());
         //   McuZteLayoutTemplates mcuZteLayoutTemplates = McuZteLayoutTemplates.valueOf(defaultViewLayout);

            //多画面配置
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



            List<String> uuidList = new ArrayList<>();
            List<MultiViewSelect> cellInfoList = new ArrayList<>();

            for (int i = 0; i < layoutTemplates.getNum(); i++) {
                MultiViewSelect cellInfo = new MultiViewSelect();
                cellInfo.setViewNo(i);
                cellInfo.setTerminalIdentifier("-2");
                // 固定主会场
                if (this.pollingScheme.getIsFixSelf() == YesOrNo.YES&&i==0) {
                    if (conferenceContext.getMasterAttendee() != null) {
                        AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
                        String uuid = masterAttendee.getParticipantUuid();
                        cellInfo.setTerminalIdentifier(uuid);
                        uuidList.add(uuid);
                        chooseSeeAttendeeList.add(masterAttendee);
                        cellInfoList.add(cellInfo);
                    }
                    continue;
                }
                // 自动指定
                if (this.pollingAttendeeList.size() <= autoPollingScreenCount) {
                    int index = lastAutoPollingIdx + 1;
                    if (index < this.pollingAttendeeList.size()) {
                        PollingAttendee pollingAttendee = this.pollingAttendeeList.get(index);
                        AttendeeForMcuZte attendeeForMcuZte = pollingAttendee.getAttendee();
                        if (attendeeForMcuZte != null) {
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendeeForMcuZte.isLocked()) {
                                    continue;
                                }
                            }
                            String uuid = attendeeForMcuZte.getParticipantUuid();

                            if (StringUtils.isNotEmpty(uuid)) {
                                if (isFill) {
                                    if (attendeeForMcuZte.isMeetingJoined()) {
                                        cellInfo.setTerminalIdentifier(uuid);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuZte);
                                        autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                        lastAutoPollingIdx = index;
                                    }
                                } else {
                                    cellInfo.setTerminalIdentifier(uuid);
                                    uuidList.add(uuid);
                                    chooseSeeAttendeeList.add(attendeeForMcuZte);
                                    autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                    lastAutoPollingIdx = index;
                                }
                            }
                        } else {
                            if (pollingAttendee.getDownCascadeAttendee() != null) {
                                BaseAttendee downCascadeAttendeeTemp = pollingAttendee.getDownCascadeAttendee();
                                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendeeTemp.getContextKey());
                                if (downCascadeConferenceContext != null) {
                                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(downCascadeAttendeeTemp.getId());
                                    if (downCascadeAttendee.isMeetingJoined()) {
                                        AttendeeForMcuZte mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendee != null) {
                                            if (mcuAttendee.isMeetingJoined()) {
                                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                if (isBroadcast) {
                                                    lastDownCascadeMcuAttendee = mcuAttendee;
                                                }

                                                String uuid = attendeeForMcuZte.getParticipantUuid();
                                                cellInfo.setTerminalIdentifier(uuid);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(mcuAttendee);
                                                autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (Objects.equals(cellInfo.getTerminalIdentifier(),"-2")) {
                            if (isFill) {
                                for (int m = index; m < this.pollingAttendeeList.size(); m++) {
                                    AttendeeForMcuZte attendeeTemp = this.pollingAttendeeList.get(m).getAttendee();
                                    if (attendeeTemp != null) {
                                        if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                                || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                            if (attendeeTemp.isLocked()) {
                                                continue;
                                            }
                                        }
                                        String uuid = attendeeTemp.getParticipantUuid();

                                        if (StringUtils.isNotEmpty(uuid) && attendeeTemp.isMeetingJoined()) {
                                            cellInfo.setTerminalIdentifier(uuid);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeTemp);
                                            autoPollingAttendeeIdSet.add(attendeeTemp.getId());
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
                    if (index >= this.pollingAttendeeList.size()) {
                        index = 0;
                    }
                    for (int m = index; m < index + this.pollingAttendeeList.size(); m++) {
                        int idxReal = m;
                        if (m >= this.pollingAttendeeList.size()) {
                            idxReal = m - this.pollingAttendeeList.size();
                        }
                        PollingAttendee pollingAttendee = this.pollingAttendeeList.get(idxReal);
                        AttendeeForMcuZte attendeeForMcuZte = pollingAttendee.getAttendee();
                        if (attendeeForMcuZte != null) {
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendeeForMcuZte.isLocked()) {
                                    continue;
                                }
                            }
                            if (!autoPollingAttendeeIdSet.contains(attendeeForMcuZte.getId())) {
                                String uuid = attendeeForMcuZte.getParticipantUuid();

                                if (StringUtils.isNotEmpty(uuid)) {
                                    if (isFill) {
                                        if (attendeeForMcuZte.isMeetingJoined()) {
                                            cellInfo.setTerminalIdentifier(uuid);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeForMcuZte);
                                            autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                            lastAutoPollingIdx = idxReal;
                                            break;
                                        }
                                    } else {
                                        cellInfo.setTerminalIdentifier(uuid);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuZte);
                                        autoPollingAttendeeIdSet.add(attendeeForMcuZte.getId());
                                        lastAutoPollingIdx = idxReal;
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (pollingAttendee.getDownCascadeAttendee() != null) {
                                BaseAttendee downCascadeAttendee = pollingAttendee.getDownCascadeAttendee();
                                if (downCascadeAttendee.isMeetingJoined()) {
                                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendee.getContextKey());
                                    if (downCascadeConferenceContext != null) {
                                        AttendeeForMcuZte mcuAttendeeForMcuZte = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendeeForMcuZte != null) {
                                            if (mcuAttendeeForMcuZte.isMeetingJoined()) {
                                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);

                                                String uuid = attendeeForMcuZte.getParticipantUuid();
                                                cellInfo.setTerminalIdentifier(uuid);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(mcuAttendeeForMcuZte);
                                                autoPollingAttendeeIdSet.add(mcuAttendeeForMcuZte.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (Objects.equals(cellInfo.getTerminalIdentifier(),"-2")) {
                            if (!isFill) {
                                lastAutoPollingIdx = idxReal;
                                break;
                            }
                        }
                    }
                }

                cellInfoList.add(cellInfo);
                // 获取取消选看和取消轮询的终端
                for (PollingAttendee pollingAttendee : this.pollingAttendeeList) {
                    AttendeeForMcuZte attendeeForMcuZte = pollingAttendee.getAttendee();
                    if (attendeeForMcuZte != null) {
                        if (!chooseSeeAttendeeList.contains(attendeeForMcuZte)) {
                            if (attendeeForMcuZte.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                    || attendeeForMcuZte.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                cancelChooseSeeAttendeeList.add(attendeeForMcuZte);
                            }
                        }
                    }
                }
            }
            if (isCancel() || isPause()) {
                return;
            }
            boolean success = false;
            AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
            if (isBroadcast) {


                // 广播时，主会场和观众看同样分屏
                SwitchMultiViewCtrlModeRequest ccSwitchMultiCtrlModeRequest = new SwitchMultiViewCtrlModeRequest();
                ccSwitchMultiCtrlModeRequest.setConferenceIdentifier(conferenceContext.getConfId());
                ccSwitchMultiCtrlModeRequest.setMultiViewMode("manual");
                SwitchMultiViewCtrlModeResponse ccUpdateMrAutoMosicConfigResponse = conferenceContext.getConferenceControlApi().switchMultiCtrlModeRequest(ccSwitchMultiCtrlModeRequest);
                if (ccUpdateMrAutoMosicConfigResponse != null && CommonResponse.STATUS_OK.equals(ccUpdateMrAutoMosicConfigResponse.getStatus())) {
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
                        success = true;
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
                // 不广播时，观众看主会场，主会场看分屏观众。
                // 观众
                if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
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
                if (isPollingDownCascade) {
                    if (lastDownCascadeMcuAttendee != null && lastDownCascadeMcuAttendee != currentDownCascadeMcuAttendee) {
                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastDownCascadeMcuAttendee.getCascadeConferenceId()));
                        if (downCascadeConferenceContext != null) {
                            ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceContext.getId());
                        }
                    }
                    lastDownCascadeMcuAttendee = currentDownCascadeMcuAttendee;
                }
                for (AttendeeForMcuZte attendeeForMcuZte : autoPollingAttends) {
                    if (attendeeForMcuZte != null && StringUtils.isNotEmpty(attendeeForMcuZte.getParticipantUuid())) {
                        if (isBroadcast) {
                            if (uuidList.contains(attendeeForMcuZte.getParticipantUuid())) {
                                continue;
                            }
                        } else {
                            if (masterAttendee != null && attendeeForMcuZte.getParticipantUuid().equals(masterAttendee.getParticipantUuid())) {
                                continue;
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
                Set<AttendeeForMcuZte> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    List<McuAttendeeForMcuZte> mcuAttendees = conferenceContext.getMcuAttendees();
                    for (McuAttendeeForMcuZte mcuAttendeeTemp : mcuAttendees) {
                        if (currentDownCascadeMcuAttendee == null || !mcuAttendeeTemp.getId().equals(currentDownCascadeMcuAttendee.getId())) {
                            BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendeeTemp.getCascadeConferenceId()));
                            if (mcuConferenceContext != null) {
                                try {
                                    ConferenceCascadeHandler.defaultChooseSee(mcuAttendeeTemp.getId());
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                    runtimeCount++;
                    logger.info("----开始轮询----" + conferenceContext.getName());
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, "轮询开始");
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("轮询已开始");
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

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
                        attendeeForMcuZte.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuZte);
                    }
                }
                for (AttendeeForMcuZte attendeeForMcuZte : chooseSeeAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuZte)) {
                            attendeeForMcuZte.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuZte.setBroadcastStatus(BroadcastStatus.YES.getValue());
                        } else {
                            attendeeForMcuZte.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
                        }
                        updateAttendeeList.add(attendeeForMcuZte);
                    }
                }

                List<AttendeeForMcuZte> pollingAttendList = new ArrayList<>();
                for (AttendeeForMcuZte attendeeForMcuZte : updateAttendeeList) {
                    if (attendeeForMcuZte != null) {
                        if (chooseSeeAttendeeList.contains(attendeeForMcuZte)) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播式轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZte.getDeptId()).getDeptName(), attendeeForMcuZte.getName())).append("】");
                            } else {
                                messageTip.append("正在轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZte.getDeptId()).getDeptName(), attendeeForMcuZte.getName())).append("】");
                            }
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            pollingAttendList.add(attendeeForMcuZte);
                        }
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    }
                }

                RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
                Object polledListObj = null;
                try {
                    polledListObj = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
                }catch (Exception e) {}
                if (polledListObj != null) {
                    JSONArray polledList = (JSONArray) polledListObj;
                    Map<String, AttendeeForMcuZte> polledAttendMap = new HashMap<>();
                    Map<String, AttendeeForMcuZte> pollingAttendMap = new HashMap<>();
                    for (Object polledObj : polledList) {
                        AttendeeForMcuZte attendeeForMcuZte = (AttendeeForMcuZte) polledObj;
                        if (attendeeForMcuZte != null) {
                            polledAttendMap.put(attendeeForMcuZte.getId(), attendeeForMcuZte);
                        }
                    }
                    for (AttendeeForMcuZte attendeeForMcuZte : pollingAttendList) {
                        if (!polledAttendMap.containsKey(attendeeForMcuZte.getId())) {
                            mqttService.sendPollingAttendMessage(attendeeForMcuZte, conferenceContext, true);
                        }
                        pollingAttendMap.put(attendeeForMcuZte.getId(), attendeeForMcuZte);
                    }
                    for (AttendeeForMcuZte attendeeForMcuZte : polledAttendMap.values()) {
                        if (!pollingAttendMap.containsKey(attendeeForMcuZte.getId())) {
                            mqttService.sendPollingAttendMessage(attendeeForMcuZte, conferenceContext, false);
                        }
                    }
                } else {
                    for (AttendeeForMcuZte attendeeForMcuZte : pollingAttendList) {
                        mqttService.sendPollingAttendMessage(attendeeForMcuZte, conferenceContext, true);
                    }
                }
                redisCache.setCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling", pollingAttendList);

                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
            }
        }
        
        
        
        

    }

    private String getViewName(String deptName, String name) {
        if (deptName.equals(name)) {
            return deptName;
        }
        return name + " / " + deptName;
    }
}
