/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.attendee.model.operation;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.mcu.kdc.attendee.exception.AttendeeRepeatException;
import com.paradisecloud.fcm.mcu.kdc.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.kdc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.CellInfo;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.McuAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.AttendeeOperation;
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
    private volatile List<AttendeeForMcuKdc> autoPollingAttends = new ArrayList<>();
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
    public PollingAttendeeOperation(McuKdcConferenceContext conferenceContext, PollingScheme pollingScheme) {
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
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
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
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        boolean canceled = isCancel();
        super.cancel();

        if (!canceled) {
            logger.info("----结束轮询----" + conferenceContext.getName());
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("轮询已结束");
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);

            RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
            Object polledListObj = null;
            try {
                polledListObj = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
            } catch (Exception e){}
            if (polledListObj != null) {
                JSONArray polledList = (JSONArray) polledListObj;
                for (Object polledObj : polledList) {
                    AttendeeForMcuKdc attendeeForMcuZj = (AttendeeForMcuKdc) polledObj;
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

        if (pollingAttendeeList.size() <= autoPollingScreenCount) {
            lastAutoPollingIdx = -1;
        }
    }

    private void operateScreen() throws Exception {
        if (ObjectUtils.isEmpty(pollingAttendeeList))
        {
            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "轮询方案参会列表为空，轮询停止！");
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
            Set<AttendeeForMcuKdc> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuKdc> chooseSeeAttendeeList = new HashSet<>();
            boolean isBroadcast = this.pollingScheme.getIsBroadcast() == YesOrNo.YES;
            boolean isFill = this.pollingScheme.getIsFill() == YesOrNo.YES;
            boolean isViewSelf = this.pollingScheme.getIsBroadcast() == YesOrNo.NO;
            BaseAttendee currentDownCascadeMcuAttendee = null;
            McuKdcLayoutTemplates layoutTemplates = McuKdcLayoutTemplates.convert(getSplitScreen());
            List<String> uuidList = new ArrayList<>();
            List<CellInfo> members = new ArrayList<>();

            for (int i = 0; i < layoutTemplates.getNum(); i++) {
                CellInfo cellInfo = new CellInfo();
                cellInfo.setChn_idx(i);
                // 固定主会场
                if (this.pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                    if (conferenceContext.getMasterAttendee() != null) {
                        AttendeeForMcuKdc masterAttendee = conferenceContext.getMasterAttendee();
                        String uuid = masterAttendee.getParticipantUuid();
                        cellInfo.setMember_type(1);
                        cellInfo.setMt_id(uuid);
                        cellInfo.setMt_chn_idx(0);
                        uuidList.add(uuid);
                        chooseSeeAttendeeList.add(masterAttendee);
                    } else {
                        cellInfo.setMember_type(1);
                        cellInfo.setMt_id("");
                        cellInfo.setMt_chn_idx(0);
                    }
                    continue;
                }
                // 自动指定
                if (this.pollingAttendeeList.size() <= autoPollingScreenCount) {
                    int index = lastAutoPollingIdx + 1;
                    if (index < this.pollingAttendeeList.size()) {
                        PollingAttendee pollingAttendee = this.pollingAttendeeList.get(index);
                        AttendeeForMcuKdc attendeeForMcuKdc = pollingAttendee.getAttendee();
                        if (attendeeForMcuKdc != null) {
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendeeForMcuKdc.isLocked()) {
                                    continue;
                                }
                            }
                            String uuid = attendeeForMcuKdc.getParticipantUuid();
//                            if (!isViewSelf) {
//                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc)) {
//                                    uuid = null;
//                                }
//                            }
                            if (StringUtils.isNotEmpty(uuid)) {
                                if (isFill) {
                                    if (attendeeForMcuKdc.isMeetingJoined()) {
                                        cellInfo.setMember_type(1);
                                        cellInfo.setMt_id(uuid);
                                        cellInfo.setMt_chn_idx(0);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                        chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                        autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                        lastAutoPollingIdx = index;
                                    }
                                } else {
                                    cellInfo.setMember_type(1);
                                    cellInfo.setMt_id(uuid);
                                    cellInfo.setMt_chn_idx(0);
                                    uuidList.add(uuid);
                                    chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                    autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
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
                                        AttendeeForMcuKdc mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendee != null) {
                                            if (mcuAttendee.isMeetingJoined()) {
                                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                if (isBroadcast) {
                                                    lastDownCascadeMcuAttendee = downCascadeAttendee;
                                                }

                                                String uuid = mcuAttendee.getParticipantUuid();
                                                cellInfo.setMember_type(1);
                                                cellInfo.setMt_id(uuid);
                                                cellInfo.setMt_chn_idx(0);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isEmpty(cellInfo.getMt_id())) {
                            if (isFill) {
                                for (int m = index; m < this.pollingAttendeeList.size(); m++) {
                                    AttendeeForMcuKdc attendeeTemp = this.pollingAttendeeList.get(m).getAttendee();
                                    if (attendeeTemp != null) {
                                        if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                                || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                                if (attendeeTemp.isLocked()) {
                                                    continue;
                                                }
                                            }
                                        }
                                        String uuid = attendeeTemp.getParticipantUuid();
//                                        if (!isViewSelf) {
//                                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc)) {
//                                                uuid = null;
//                                            }
//                                        }
                                        if (StringUtils.isNotEmpty(uuid) && attendeeTemp.isMeetingJoined()) {
                                            cellInfo.setMember_type(1);
                                            cellInfo.setMt_id(uuid);
                                            cellInfo.setMt_chn_idx(0);
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
                        AttendeeForMcuKdc attendeeForMcuKdc = pollingAttendee.getAttendee();
                        if (attendeeForMcuKdc != null) {
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendeeForMcuKdc.isLocked()) {
                                    continue;
                                }
                            }
                            if (!autoPollingAttendeeIdSet.contains(attendeeForMcuKdc.getId())) {
                                String uuid = attendeeForMcuKdc.getParticipantUuid();
//                                if (!isViewSelf) {
//                                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc)) {
//                                        uuid = null;
//                                    }
//                                }
                                if (StringUtils.isNotEmpty(uuid)) {
                                    if (isFill) {
                                        if (attendeeForMcuKdc.isMeetingJoined()) {
                                            cellInfo.setMember_type(1);
                                            cellInfo.setMt_id(uuid);
                                            cellInfo.setMt_chn_idx(0);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                            autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
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
                                        AttendeeForMcuKdc mcuAttendeeForMcuPlc = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendeeForMcuPlc != null) {
                                            if (mcuAttendeeForMcuPlc.isMeetingJoined()) {
                                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);

                                                String uuid = mcuAttendeeForMcuPlc.getParticipantUuid();
                                                cellInfo.setMember_type(1);
                                                cellInfo.setMt_id(uuid);
                                                cellInfo.setMt_chn_idx(0);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(attendeeForMcuKdc);
                                                autoPollingAttendeeIdSet.add(attendeeForMcuKdc.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
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
                if (StringUtils.isEmpty(cellInfo.getMt_id())) {
                    cellInfo.setMember_type(1);
                    cellInfo.setMt_id("");
                    cellInfo.setMt_chn_idx(0);
                }
                members.add(cellInfo);
                // 获取取消选看和取消轮询的终端
                for (PollingAttendee pollingAttendee : this.pollingAttendeeList) {
                    AttendeeForMcuKdc attendeeForMcuKdc = pollingAttendee.getAttendee();
                    if (attendeeForMcuKdc != null) {
                        if (!chooseSeeAttendeeList.contains(attendeeForMcuKdc)) {
                            if (attendeeForMcuKdc.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                || attendeeForMcuKdc.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                cancelChooseSeeAttendeeList.add(attendeeForMcuKdc);
                            }
                        }
                    }
                }
            }
            if (isCancel() || isPause()) {
                return;
            }
            boolean success = false;
            AttendeeForMcuKdc masterAttendee = conferenceContext.getMasterAttendee();
            if (isBroadcast) {
                // 广播时，主会场和观众看同样分屏
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
                // 不广播时，观众看主会场，主会场看分屏观众。
                // 观众
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
                    ccUpdateMrMosicConfigRequest.setBroadcast(1);
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
            }
            if (success) {
            } else {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
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
                Set<AttendeeForMcuKdc> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    List<McuAttendeeForMcuKdc> mcuAttendees = conferenceContext.getMcuAttendees();
                    for (McuAttendeeForMcuKdc mcuAttendeeTemp : mcuAttendees) {
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
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, "轮询开始");
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("轮询已开始");
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

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
                        attendeeForMcuKdc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuKdc);
                    }
                }
                for (AttendeeForMcuKdc attendeeForMcuKdc : chooseSeeAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuKdc)) {
                            attendeeForMcuKdc.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuKdc.setBroadcastStatus(BroadcastStatus.YES.getValue());
                        } else {
                            attendeeForMcuKdc.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
                        }
                        updateAttendeeList.add(attendeeForMcuKdc);
                    }
                }

                List<AttendeeForMcuKdc> pollingAttendList = new ArrayList<>();
                for (AttendeeForMcuKdc attendeeForMcuKdc : updateAttendeeList) {
                    if (attendeeForMcuKdc != null) {
                        if (chooseSeeAttendeeList.contains(attendeeForMcuKdc)) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播式轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuKdc.getDeptId()).getDeptName(), attendeeForMcuKdc.getName())).append("】");
                            } else {
                                messageTip.append("正在轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuKdc.getDeptId()).getDeptName(), attendeeForMcuKdc.getName())).append("】");
                            }
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            pollingAttendList.add(attendeeForMcuKdc);
                        }
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
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
                    Map<String, AttendeeForMcuKdc> polledAttendMap = new HashMap<>();
                    Map<String, AttendeeForMcuKdc> pollingAttendMap = new HashMap<>();
                    for (Object polledObj : polledList) {
                        AttendeeForMcuKdc attendeeForMcuKdc = (AttendeeForMcuKdc) polledObj;
                        if (attendeeForMcuKdc != null) {
                            polledAttendMap.put(attendeeForMcuKdc.getId(), attendeeForMcuKdc);
                        }
                    }
                    for (AttendeeForMcuKdc attendeeForMcuKdc : pollingAttendList) {
                        if (!polledAttendMap.containsKey(attendeeForMcuKdc.getId())) {
                            mqttService.sendPollingAttendMessage(attendeeForMcuKdc, conferenceContext, true);
                        }
                        pollingAttendMap.put(attendeeForMcuKdc.getId(), attendeeForMcuKdc);
                    }
                    for (AttendeeForMcuKdc attendeeForMcuKdc : polledAttendMap.values()) {
                        if (!pollingAttendMap.containsKey(attendeeForMcuKdc.getId())) {
                            mqttService.sendPollingAttendMessage(attendeeForMcuKdc, conferenceContext, false);
                        }
                    }
                } else {
                    for (AttendeeForMcuKdc attendeeForMcuKdc : pollingAttendList) {
                        mqttService.sendPollingAttendMessage(attendeeForMcuKdc, conferenceContext, true);
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
