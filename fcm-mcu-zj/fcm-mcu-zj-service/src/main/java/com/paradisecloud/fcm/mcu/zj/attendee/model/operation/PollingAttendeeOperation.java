/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.zj.attendee.model.operation;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.mcu.zj.attendee.exception.AttendeeRepeatException;
import com.paradisecloud.fcm.mcu.zj.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.zj.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.McuAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrMosicConfigRequest;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.spring.BeanFactory;
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
    public PollingAttendeeOperation(McuZjConferenceContext conferenceContext, PollingScheme pollingScheme) {
        super(conferenceContext);
        this.pollingScheme = pollingScheme;
        initSplitScreen();
        Assert.isTrue(!(this.splitScreen instanceof AutomaticSplitScreen), "轮询操作不支持自动分屏");
    }

    public void initSplitScreen() {
        int maxImportance = this.pollingScheme.getIsBroadcast() == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
        String defaultViewLayout = this.pollingScheme.getLayout();
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
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
                throw new AttendeeRepeatException(messageTip1.toString());
            }
            if (pollingAttendee.getDownCascadeAttendee() != null) {
                isPollingDownCascade = true;
            }
            boolean needAdd = true;
            if (conferenceContext.getMasterAttendee() != null && attendee.getId().equals(conferenceContext.getMasterAttendee().getId())) {
                if (pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                    needAdd = false;
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
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        boolean canceled = isCancel();
        super.cancel();

        if (!canceled) {
            if (isPollingDownCascade) {
                if (lastDownCascadeMcuAttendee != null) {
                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastDownCascadeMcuAttendee.getCascadeConferenceId()));
                    if (downCascadeConferenceContext != null) {
                        ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceContext.getId());
                    }
                }
                lastDownCascadeMcuAttendee = null;
            }
            logger.info("----结束轮询----" + conferenceContext.getName());
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("轮询已结束");
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);

            RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
            IMqttService mqttForMcuZjService = BeanFactory.getBean(IMqttService.class);
            Object polledListObj = null;
            try {
                polledListObj = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
            } catch (Exception e){}
            if (polledListObj != null) {
                JSONArray polledList = (JSONArray) polledListObj;
                for (Object polledObj : polledList) {
                    AttendeeForMcuZj attendeeForMcuZj = (AttendeeForMcuZj) polledObj;
                    if (attendeeForMcuZj != null) {
                        mqttForMcuZjService.sendPollingAttendMessage(attendeeForMcuZj, conferenceContext, false);
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
        this.parse();

        if (pollingAttendeeList.size() <= autoPollingScreenCount) {
            lastAutoPollingIdx = -1;
        }
    }

    private void operateScreen() throws Exception {
        if (ObjectUtils.isEmpty(pollingAttendeeList))
        {
            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "轮询方案参会列表为空，轮询停止！");
            throw new PollingCancelException("轮询方案参会列表为空，轮询停止！");
        }
        long currentTimeMillis = System.currentTimeMillis();
        int interval = this.pollingScheme.getInterval();
        if (interval < 10) {
            interval = 10;
        }
        if (currentTimeMillis - getLastUpdateTime() >= interval * 1000) {
            setLastUpdateTime(currentTimeMillis);
            Set<AttendeeForMcuZj> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuZj> chooseSeeAttendeeList = new HashSet<>();
            CcUpdateMrMosicConfigRequest ccUpdateMrMosicConfigRequest = new CcUpdateMrMosicConfigRequest();
            List<CcUpdateMrMosicConfigRequest.ConfigInfo> configInfoList = new ArrayList();
            boolean isBroadcast = this.pollingScheme.getIsBroadcast() == YesOrNo.YES;
            boolean isFill = this.pollingScheme.getIsFill() == YesOrNo.YES;
            boolean isViewSelf = false;
            BaseAttendee currentDownCascadeMcuAttendee = null;
            if (isBroadcast) {
                // 广播时，主会场和观众看同样分屏
                isViewSelf = true;
                List<List<String>> rolesLst = new ArrayList<>();
                {
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoSpeaker = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoSpeaker.setLayout_mode(2);//传统分屏
                    LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
                    configInfoSpeaker.setMosic_id(layoutTemplates.getCode());
                    configInfoSpeaker.setRole("speaker");
                    for (int i = 0; i < layoutTemplates.getNum(); i++) {
                        List<String> userIdList = new ArrayList<>();
                        // 固定主会场
                        if (this.pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                            userIdList.add("1010");//主会场
                            if (conferenceContext.getMasterAttendee() != null) {
                                chooseSeeAttendeeList.add(conferenceContext.getMasterAttendee());
                            }
                            continue;
                        }
                        // 自动指定
                        if (this.pollingAttendeeList.size() <= autoPollingScreenCount) {
                            int index = lastAutoPollingIdx + 1;
                            if (index < this.pollingAttendeeList.size()) {
                                PollingAttendee pollingAttendee = this.pollingAttendeeList.get(index);
                                AttendeeForMcuZj attendeeForMcuZj = pollingAttendee.getAttendee();
                                if (attendeeForMcuZj != null) {
                                    if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                            || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                        if (attendeeForMcuZj.isLocked()) {
                                            continue;
                                        }
                                    }
                                    if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                        if (isFill) {
                                            if (attendeeForMcuZj.isMeetingJoined()) {
                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        } else {
                                            userIdList.add(attendeeForMcuZj.getEpUserId());
                                            chooseSeeAttendeeList.add(attendeeForMcuZj);
                                            autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
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
                                                AttendeeForMcuZj mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                                if (mcuAttendee != null) {
                                                    if (mcuAttendee.isMeetingJoined()) {
                                                        try {
                                                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                            currentDownCascadeMcuAttendee = mcuAttendee;

                                                            userIdList.add(mcuAttendee.getEpUserId());
                                                            chooseSeeAttendeeList.add(mcuAttendee);
                                                            autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                            lastAutoPollingIdx = index;
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (userIdList.size() == 0) {
                                    if (isFill) {
                                        for (int m = index; m < this.pollingAttendeeList.size(); m++) {
                                            PollingAttendee pollingAttendeeTemp = this.pollingAttendeeList.get(m);
                                            AttendeeForMcuZj attendeeTemp = pollingAttendeeTemp.getAttendee();
                                            if (attendeeTemp != null) {
                                                if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                                        || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                                    if (attendeeTemp.isLocked()) {
                                                        continue;
                                                    }
                                                }
                                                if (StringUtils.isNotEmpty(attendeeTemp.getEpUserId()) && attendeeTemp.isMeetingJoined()) {
                                                    userIdList.add(attendeeTemp.getEpUserId());
                                                    chooseSeeAttendeeList.add(attendeeTemp);
                                                    autoPollingAttendeeIdSet.add(attendeeTemp.getId());
                                                    lastAutoPollingIdx = m;
                                                    break;
                                                }
                                            } else {
                                                if (pollingAttendeeTemp.getDownCascadeAttendee() != null) {
                                                    BaseAttendee downCascadeAttendeeTemp = pollingAttendeeTemp.getDownCascadeAttendee();
                                                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendeeTemp.getContextKey());
                                                    if (downCascadeConferenceContext != null) {
                                                        BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(downCascadeAttendeeTemp.getId());
                                                        if (downCascadeAttendee.isMeetingJoined()) {
                                                            AttendeeForMcuZj mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                                            if (mcuAttendee != null) {
                                                                if (mcuAttendee.isMeetingJoined()) {
                                                                    try {
                                                                        ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                                        currentDownCascadeMcuAttendee = mcuAttendee;

                                                                        userIdList.add(mcuAttendee.getEpUserId());
                                                                        chooseSeeAttendeeList.add(mcuAttendee);
                                                                        autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                                        lastAutoPollingIdx = m;
                                                                        break;
                                                                    } catch (Exception e) {
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
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
                                PollingAttendee pollingAttendeeTemp = this.pollingAttendeeList.get(idxReal);
                                AttendeeForMcuZj attendeeForMcuZj = pollingAttendeeTemp.getAttendee();
                                if (attendeeForMcuZj != null) {
                                    if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                            || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                        if (attendeeForMcuZj.isLocked()) {
                                            continue;
                                        }
                                    }
                                    if (!autoPollingAttendeeIdSet.contains(attendeeForMcuZj.getId())) {
                                        if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                            if (isFill) {
                                                if (attendeeForMcuZj.isMeetingJoined()) {
                                                    userIdList.add(attendeeForMcuZj.getEpUserId());
                                                    chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            } else {
                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                lastAutoPollingIdx = idxReal;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if (pollingAttendeeTemp.getDownCascadeAttendee() != null) {
                                        BaseAttendee downCascadeAttendee = pollingAttendeeTemp.getDownCascadeAttendee();
                                        if (downCascadeAttendee.isMeetingJoined()) {
                                            BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendee.getContextKey());
                                            if (downCascadeConferenceContext != null) {
                                                AttendeeForMcuZj mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                                if (mcuAttendee != null) {
                                                    if (mcuAttendee.isMeetingJoined()) {
                                                        try {
                                                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                            currentDownCascadeMcuAttendee = mcuAttendee;

                                                            userIdList.add(mcuAttendee.getEpUserId());
                                                            chooseSeeAttendeeList.add(mcuAttendee);
                                                            autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                            lastAutoPollingIdx = idxReal;
                                                            break;
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
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
                        if (userIdList.size() == 0) {
                            userIdList.add("-1");// 留空
                        }
                        rolesLst.add(userIdList);
                        // 获取取消选看和取消轮询的终端
                        for (PollingAttendee pollingAttendee : this.pollingAttendeeList) {
                            AttendeeForMcuZj attendeeForMcuZj = pollingAttendee.getAttendee();
                            if (attendeeForMcuZj != null) {
                                if (!chooseSeeAttendeeList.contains(attendeeForMcuZj)) {
                                    if (attendeeForMcuZj.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                            || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                        cancelChooseSeeAttendeeList.add(attendeeForMcuZj);
                                    }
                                }
                            }
                        }
                    }
                    configInfoSpeaker.setRoles_lst(rolesLst);
                    if (isViewSelf) {
                        configInfoSpeaker.setView_has_self(1);
                    } else {
                        configInfoSpeaker.setView_has_self(0);
                    }
                    Integer pollSecs = interval;
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
                    if (isViewSelf) {
                        configInfoGuest.setView_has_self(1);
                    } else {
                        configInfoGuest.setView_has_self(0);
                    }
                    Integer pollSecs = interval;
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoGuest.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoGuest);
                }
            } else {
                // 不广播时，观众看主会场，主会场看分屏观众
                {
                    // 主会场
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoSpeaker = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoSpeaker.setLayout_mode(2);//传统分屏
                    LayoutTemplates layoutTemplates = LayoutTemplates.convert(getSplitScreen());
                    configInfoSpeaker.setMosic_id(layoutTemplates.getCode());
                    if (conferenceContext.isSingleView()) {
                        configInfoSpeaker.setRole("guest");
                    } else {
                        configInfoSpeaker.setRole("speaker");
                    }
                    List<List<String>> rolesLst = new ArrayList<>();
                    for (int i = 0; i < layoutTemplates.getNum(); i++) {
                        List<String> userIdList = new ArrayList<>();
                        // 固定主会场
                        if (this.pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                            userIdList.add("1010");//主会场
                            if (conferenceContext.getMasterAttendee() != null) {
                                chooseSeeAttendeeList.add(conferenceContext.getMasterAttendee());
                            }
                            continue;
                        }
                        // 自动指定
                        if (this.pollingAttendeeList.size() <= autoPollingScreenCount) {
                            int index = lastAutoPollingIdx + 1;
                            if (index < this.pollingAttendeeList.size()) {
                                PollingAttendee pollingAttendee = this.pollingAttendeeList.get(index);
                                AttendeeForMcuZj attendeeForMcuZj = pollingAttendee.getAttendee();
                                if (attendeeForMcuZj != null) {
                                    if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                            || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                        if (attendeeForMcuZj.isLocked()) {
                                            continue;
                                        }
                                    }
                                    if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                        if (isFill) {
                                            if (attendeeForMcuZj.isMeetingJoined()) {
                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        } else {
                                            userIdList.add(attendeeForMcuZj.getEpUserId());
                                            chooseSeeAttendeeList.add(attendeeForMcuZj);
                                            autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
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
                                                AttendeeForMcuZj mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                                if (mcuAttendee != null) {
                                                    if (mcuAttendee.isMeetingJoined()) {
                                                        try {
                                                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                            currentDownCascadeMcuAttendee = mcuAttendee;

                                                            userIdList.add(mcuAttendee.getEpUserId());
                                                            chooseSeeAttendeeList.add(mcuAttendee);
                                                            autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                            lastAutoPollingIdx = index;
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (userIdList.size() == 0) {
                                    if (isFill) {
                                        for (int m = index; m < this.pollingAttendeeList.size(); m++) {
                                            PollingAttendee pollingAttendeeTemp = this.pollingAttendeeList.get(m);
                                            AttendeeForMcuZj attendeeTemp = pollingAttendeeTemp.getAttendee();
                                            if (attendeeTemp != null) {
                                                if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                                        || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                                    if (attendeeTemp.isLocked()) {
                                                        continue;
                                                    }
                                                }
                                                if (StringUtils.isNotEmpty(attendeeTemp.getEpUserId()) && attendeeTemp.isMeetingJoined()) {
                                                    userIdList.add(attendeeTemp.getEpUserId());
                                                    chooseSeeAttendeeList.add(attendeeTemp);
                                                    autoPollingAttendeeIdSet.add(attendeeTemp.getId());
                                                    lastAutoPollingIdx = m;
                                                    break;
                                                }
                                            } else {
                                                if (pollingAttendeeTemp.getDownCascadeAttendee() != null) {
                                                    BaseAttendee downCascadeAttendeeTemp = pollingAttendee.getDownCascadeAttendee();
                                                    BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendeeTemp.getContextKey());
                                                    if (downCascadeConferenceContext != null) {
                                                        BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(downCascadeAttendeeTemp.getId());
                                                        if (downCascadeAttendee.isMeetingJoined()) {
                                                            AttendeeForMcuZj mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                                            if (mcuAttendee != null) {
                                                                if (mcuAttendee.isMeetingJoined()) {
                                                                    try {
                                                                        ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                                        currentDownCascadeMcuAttendee = mcuAttendee;

                                                                        userIdList.add(mcuAttendee.getEpUserId());
                                                                        chooseSeeAttendeeList.add(mcuAttendee);
                                                                        autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                                        lastAutoPollingIdx = m;
                                                                        break;
                                                                    } catch (Exception e) {
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
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
                                PollingAttendee pollingAttendeeTemp = this.pollingAttendeeList.get(idxReal);
                                AttendeeForMcuZj attendeeForMcuZj = pollingAttendeeTemp.getAttendee();
                                if (attendeeForMcuZj != null) {
                                    if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                            || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                        if (attendeeForMcuZj.isLocked()) {
                                            continue;
                                        }
                                    }
                                    if (!autoPollingAttendeeIdSet.contains(attendeeForMcuZj.getId())) {
                                        if (StringUtils.isNotEmpty(attendeeForMcuZj.getEpUserId())) {
                                            if (isFill) {
                                                if (attendeeForMcuZj.isMeetingJoined()) {
                                                    userIdList.add(attendeeForMcuZj.getEpUserId());
                                                    chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                    autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                    lastAutoPollingIdx = idxReal;
                                                    break;
                                                }
                                            } else {
                                                userIdList.add(attendeeForMcuZj.getEpUserId());
                                                chooseSeeAttendeeList.add(attendeeForMcuZj);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuZj.getId());
                                                lastAutoPollingIdx = idxReal;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if (pollingAttendeeTemp.getDownCascadeAttendee() != null) {
                                        BaseAttendee downCascadeAttendee = pollingAttendeeTemp.getDownCascadeAttendee();
                                        if (downCascadeAttendee.isMeetingJoined()) {
                                            BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendee.getContextKey());
                                            if (downCascadeConferenceContext != null) {
                                                AttendeeForMcuZj mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                                if (mcuAttendee != null) {
                                                    if (mcuAttendee.isMeetingJoined()) {
                                                        try {
                                                            ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                            currentDownCascadeMcuAttendee = mcuAttendee;

                                                            userIdList.add(mcuAttendee.getEpUserId());
                                                            chooseSeeAttendeeList.add(mcuAttendee);
                                                            autoPollingAttendeeIdSet.add(mcuAttendee.getId());
                                                            lastAutoPollingIdx = idxReal;
                                                            break;
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
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
                        if (userIdList.size() == 0) {
                            userIdList.add("-1");// 留空
                        }
                        rolesLst.add(userIdList);
                        // 获取取消选看和取消轮询的终端
                        for (PollingAttendee pollingAttendee : this.pollingAttendeeList) {
                            AttendeeForMcuZj attendeeForMcuZj = pollingAttendee.getAttendee();
                            if (attendeeForMcuZj != null) {
                                if (!chooseSeeAttendeeList.contains(attendeeForMcuZj)) {
                                    if (attendeeForMcuZj.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                            || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                        cancelChooseSeeAttendeeList.add(attendeeForMcuZj);
                                    }
                                }
                            }
                        }
                    }
                    configInfoSpeaker.setRoles_lst(rolesLst);
                    if (isViewSelf) {
                        configInfoSpeaker.setView_has_self(1);
                    } else {
                        configInfoSpeaker.setView_has_self(0);
                    }
                    Integer pollSecs = interval;
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoSpeaker.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoSpeaker);
                }
                if (false) {
                    // 观众
                    CcUpdateMrMosicConfigRequest.ConfigInfo configInfoGuest = new CcUpdateMrMosicConfigRequest.ConfigInfo();
                    configInfoGuest.setLayout_mode(2);//传统分屏
                    configInfoGuest.setMosic_id(LayoutTemplates.SCREEN_1.getCode());// 一分屏
                    configInfoGuest.setRole("guest");
                    List<List<String>> rolesLst = new ArrayList<>();
                    List<String> userIdList = new ArrayList<>();
                    userIdList.add("1010");//主会场
                    rolesLst.add(userIdList);
                    configInfoGuest.setRoles_lst(rolesLst);
                    if (isViewSelf) {
                        configInfoGuest.setView_has_self(1);
                    } else {
                        configInfoGuest.setView_has_self(0);
                    }
                    Integer pollSecs = interval;
                    if (pollSecs < 10) {
                        pollSecs = 10;
                    }
                    configInfoGuest.setPoll_secs(pollSecs);
                    configInfoList.add(configInfoGuest);
                }
            }
            ccUpdateMrMosicConfigRequest.setConfig_info(configInfoList);
            if (isCancel() || isPause()) {
                return;
            }
            boolean success = conferenceContext.getConferenceControlApi().updateMrMosicConfig(ccUpdateMrMosicConfigRequest);
            if (success) {
                if (isPollingDownCascade) {
                    if (lastDownCascadeMcuAttendee != null && lastDownCascadeMcuAttendee != currentDownCascadeMcuAttendee) {
                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastDownCascadeMcuAttendee.getCascadeConferenceId()));
                        if (downCascadeConferenceContext != null) {
                            ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceContext.getId());
                            AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeById(lastDownCascadeMcuAttendee.getId());
                            if (!chooseSeeAttendeeList.contains(attendeeForMcuZj)) {
                                if (attendeeForMcuZj.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                        || attendeeForMcuZj.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                    cancelChooseSeeAttendeeList.add(attendeeForMcuZj);
                                }
                            }
                        }
                    }
                    lastDownCascadeMcuAttendee = currentDownCascadeMcuAttendee;
                }
                Set<AttendeeForMcuZj> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    List<McuAttendeeForMcuZj> mcuAttendees = conferenceContext.getMcuAttendees();
                    for (McuAttendeeForMcuZj mcuAttendeeTemp : mcuAttendees) {
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
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, "轮询开始");
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("轮询已开始");
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

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
                        attendeeForMcuZj.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuZj);
                    }
                }
                for (AttendeeForMcuZj attendeeForMcuZj : chooseSeeAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuZj)) {
                            attendeeForMcuZj.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuZj.setBroadcastStatus(BroadcastStatus.YES.getValue());
                        } else {
                            attendeeForMcuZj.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
                        }
                        updateAttendeeList.add(attendeeForMcuZj);
                    }
                }

                List<AttendeeForMcuZj> pollingAttendList = new ArrayList<>();
                for (AttendeeForMcuZj attendeeForMcuZj : updateAttendeeList) {
                    if (attendeeForMcuZj != null) {
                        if (chooseSeeAttendeeList.contains(attendeeForMcuZj)) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播式轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZj.getDeptId()).getDeptName(), attendeeForMcuZj.getName())).append("】");
                            } else {
                                messageTip.append("正在轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuZj.getDeptId()).getDeptName(), attendeeForMcuZj.getName())).append("】");
                            }
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            pollingAttendList.add(attendeeForMcuZj);
                        }
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                    }
                }

                RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                IMqttService mqttForMcuZjService = BeanFactory.getBean(IMqttService.class);
                Object polledListObj = null;
                try {
                    polledListObj = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
                }catch (Exception e) {}
                if (polledListObj != null) {
                    JSONArray polledList = (JSONArray) polledListObj;
                    Map<String, AttendeeForMcuZj> polledAttendMap = new HashMap<>();
                    Map<String, AttendeeForMcuZj> pollingAttendMap = new HashMap<>();
                    for (Object polledObj : polledList) {
                        AttendeeForMcuZj attendeeForMcuZj = (AttendeeForMcuZj) polledObj;
                        if (attendeeForMcuZj != null) {
                            polledAttendMap.put(attendeeForMcuZj.getId(), attendeeForMcuZj);
                        }
                    }
                    for (AttendeeForMcuZj attendeeForMcuZj : pollingAttendList) {
                        if (!polledAttendMap.containsKey(attendeeForMcuZj.getId())) {
                            mqttForMcuZjService.sendPollingAttendMessage(attendeeForMcuZj, conferenceContext, true);
                        }
                        pollingAttendMap.put(attendeeForMcuZj.getId(), attendeeForMcuZj);
                    }
                    for (AttendeeForMcuZj attendeeForMcuZj : polledAttendMap.values()) {
                        if (!pollingAttendMap.containsKey(attendeeForMcuZj.getId())) {
                            mqttForMcuZjService.sendPollingAttendMessage(attendeeForMcuZj, conferenceContext, false);
                        }
                    }
                } else {
                    for (AttendeeForMcuZj attendeeForMcuZj : pollingAttendList) {
                        mqttForMcuZjService.sendPollingAttendMessage(attendeeForMcuZj, conferenceContext, true);
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
