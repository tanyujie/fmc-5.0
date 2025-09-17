/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-26 15:55
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.plc.attendee.model.operation;

import com.alibaba.fastjson.JSONArray;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.mcu.plc.attendee.exception.AttendeeRepeatException;
import com.paradisecloud.fcm.mcu.plc.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.mcu.plc.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.enumer.PollingStrategy;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.CellInfo;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.McuAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
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
    private volatile List<AttendeeForMcuPlc> autoPollingAttends = new ArrayList<>();
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
    public PollingAttendeeOperation(McuPlcConferenceContext conferenceContext, PollingScheme pollingScheme) {
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
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
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
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            } else {
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
            }
        }
    }

    @Override
    public void cancel() {
        boolean canceled = isCancel();
        super.cancel();

        if (!canceled) {
            logger.info("----结束轮询----" + conferenceContext.getName());
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("轮询已结束");
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);

            RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
            IMqttService mqttService = BeanFactory.getBean(IMqttService.class);
            Object polledListObj = null;
            try {
                polledListObj = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_polling");
            } catch (Exception e){}
            if (polledListObj != null) {
                JSONArray polledList = (JSONArray) polledListObj;
                for (Object polledObj : polledList) {
                    AttendeeForMcuPlc attendeeForMcuZj = (AttendeeForMcuPlc) polledObj;
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

        if (pollingAttendeeList.size() <= autoPollingScreenCount) {
            lastAutoPollingIdx = -1;
        }
    }

    private void operateScreen() throws Exception {
        if (ObjectUtils.isEmpty(pollingAttendeeList))
        {
            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "轮询方案参会列表为空，轮询停止！");
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
            Set<AttendeeForMcuPlc> cancelChooseSeeAttendeeList = new HashSet<>();
            Set<AttendeeForMcuPlc> chooseSeeAttendeeList = new HashSet<>();
            boolean isBroadcast = this.pollingScheme.getIsBroadcast() == YesOrNo.YES;
            boolean isFill = this.pollingScheme.getIsFill() == YesOrNo.YES;
            boolean isViewSelf = this.pollingScheme.getIsBroadcast() == YesOrNo.NO;
            BaseAttendee currentDownCascadeMcuAttendee = null;
            McuPlcLayoutTemplates layoutTemplates = McuPlcLayoutTemplates.convert(getSplitScreen());
            List<String> uuidList = new ArrayList<>();
            List<CellInfo> cellInfoList = new ArrayList<>();

            for (int i = 0; i < layoutTemplates.getNum(); i++) {
                CellInfo cellInfo = new CellInfo();
                cellInfo.setId(String.valueOf(i + 1));
                // 固定主会场
                if (this.pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                    if (conferenceContext.getMasterAttendee() != null) {
                        AttendeeForMcuPlc masterAttendee = conferenceContext.getMasterAttendee();
                        String uuid = masterAttendee.getParticipantUuid();
                        cellInfo.setForceStatus("forced");
                        cellInfo.setForceId(uuid);
                        cellInfo.setSourceId(uuid);
                        uuidList.add(uuid);
                        chooseSeeAttendeeList.add(masterAttendee);
                    } else {
                        cellInfo.setForceStatus("blank");
                        cellInfo.setForceId("-1");// 留空
                        cellInfo.setSourceId("-1");// 留空
                    }
                    continue;
                }
                // 自动指定
                if (this.pollingAttendeeList.size() <= autoPollingScreenCount) {
                    int index = lastAutoPollingIdx + 1;
                    if (index < this.pollingAttendeeList.size()) {
                        PollingAttendee pollingAttendee = this.pollingAttendeeList.get(index);
                        AttendeeForMcuPlc attendeeForMcuPlc = pollingAttendee.getAttendee();
                        if (attendeeForMcuPlc != null) {
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendeeForMcuPlc.isLocked()) {
                                    continue;
                                }
                            }
                            String uuid = attendeeForMcuPlc.getParticipantUuid();
//                            if (!isViewSelf) {
//                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc)) {
//                                    uuid = null;
//                                }
//                            }
                            if (StringUtils.isNotEmpty(uuid)) {
                                if (isFill) {
                                    if (attendeeForMcuPlc.isMeetingJoined()) {
                                        cellInfo.setForceStatus("forced");
                                        cellInfo.setForceId(uuid);
                                        cellInfo.setSourceId(uuid);
                                        uuidList.add(uuid);
                                        chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                        autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
                                        lastAutoPollingIdx = index;
                                    }
                                } else {
                                    cellInfo.setForceStatus("forced");
                                    cellInfo.setForceId(uuid);
                                    cellInfo.setSourceId(uuid);
                                    uuidList.add(uuid);
                                    chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                    autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
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
                                        AttendeeForMcuPlc mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendee != null) {
                                            if (mcuAttendee.isMeetingJoined()) {
                                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                if (isBroadcast) {
                                                    lastDownCascadeMcuAttendee = mcuAttendee;
                                                }

                                                String uuid = attendeeForMcuPlc.getParticipantUuid();
                                                cellInfo.setForceStatus("forced");
                                                cellInfo.setForceId(uuid);
                                                cellInfo.setSourceId(uuid);
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
                        if (StringUtils.isEmpty(cellInfo.getForceId())) {
                            if (isFill) {
                                for (int m = index; m < this.pollingAttendeeList.size(); m++) {
                                    AttendeeForMcuPlc attendeeTemp = this.pollingAttendeeList.get(m).getAttendee();
                                    if (attendeeTemp != null) {
                                        if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                                || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                            if (attendeeTemp.isLocked()) {
                                                continue;
                                            }
                                        }
                                        String uuid = attendeeTemp.getParticipantUuid();
//                                        if (!isViewSelf) {
//                                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc)) {
//                                                uuid = null;
//                                            }
//                                        }
                                        if (StringUtils.isNotEmpty(uuid) && attendeeTemp.isMeetingJoined()) {
                                            cellInfo.setForceStatus("forced");
                                            cellInfo.setForceId(uuid);
                                            cellInfo.setSourceId(uuid);
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
                        PollingAttendee pollingAttendee = this.pollingAttendeeList.get(index);
                        AttendeeForMcuPlc attendeeForMcuPlc = pollingAttendee.getAttendee();
                        if (attendeeForMcuPlc != null) {
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendeeForMcuPlc.isLocked()) {
                                    continue;
                                }
                            }
                            if (!autoPollingAttendeeIdSet.contains(attendeeForMcuPlc.getId())) {
                                String uuid = attendeeForMcuPlc.getParticipantUuid();
//                                if (!isViewSelf) {
//                                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc)) {
//                                        uuid = null;
//                                    }
//                                }
                                if (StringUtils.isNotEmpty(uuid)) {
                                    if (isFill) {
                                        if (attendeeForMcuPlc.isMeetingJoined()) {
                                            cellInfo.setForceStatus("forced");
                                            cellInfo.setForceId(uuid);
                                            cellInfo.setSourceId(uuid);
                                            uuidList.add(uuid);
                                            chooseSeeAttendeeList.add(attendeeForMcuPlc);
                                            autoPollingAttendeeIdSet.add(attendeeForMcuPlc.getId());
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
                                        AttendeeForMcuPlc mcuAttendeeForMcuPlc = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendeeForMcuPlc != null) {
                                            if (mcuAttendeeForMcuPlc.isMeetingJoined()) {
                                                ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);

                                                String uuid = attendeeForMcuPlc.getParticipantUuid();
                                                cellInfo.setForceStatus("forced");
                                                cellInfo.setForceId(uuid);
                                                cellInfo.setSourceId(uuid);
                                                uuidList.add(uuid);
                                                chooseSeeAttendeeList.add(mcuAttendeeForMcuPlc);
                                                autoPollingAttendeeIdSet.add(mcuAttendeeForMcuPlc.getId());
                                                lastAutoPollingIdx = index;
                                            }
                                        }
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
                if (StringUtils.isEmpty(cellInfo.getForceId())) {
                    cellInfo.setForceStatus("blank");
                    cellInfo.setForceId("-1");// 留空
                    cellInfo.setSourceId("-1");// 留空
                }
                cellInfoList.add(cellInfo);
                // 获取取消选看和取消轮询的终端
                for (PollingAttendee pollingAttendee : this.pollingAttendeeList) {
                    AttendeeForMcuPlc attendeeForMcuPlc = pollingAttendee.getAttendee();
                    if (attendeeForMcuPlc != null) {
                        if (!chooseSeeAttendeeList.contains(attendeeForMcuPlc)) {
                            if (attendeeForMcuPlc.getRoundRobinStatus() == AttendeeRoundRobinStatus.YES.getValue()
                                || attendeeForMcuPlc.getBroadcastStatus() == BroadcastStatus.YES.getValue()) {
                                cancelChooseSeeAttendeeList.add(attendeeForMcuPlc);
                            }
                        }
                    }
                }
            }
            if (isCancel() || isPause()) {
                return;
            }
            boolean success = false;
            AttendeeForMcuPlc masterAttendee = conferenceContext.getMasterAttendee();
            if (isBroadcast) {
                // 广播时，主会场和观众看同样分屏
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
                // 不广播时，观众看主会场，主会场看分屏观众。
                // 观众
                if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
                    // 有主会场时，观众看主会场
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
                if (isPollingDownCascade) {
                    if (lastDownCascadeMcuAttendee != null && lastDownCascadeMcuAttendee != currentDownCascadeMcuAttendee) {
                        BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastDownCascadeMcuAttendee.getCascadeConferenceId()));
                        if (downCascadeConferenceContext != null) {
                            ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceContext.getId());
                        }
                    }
                    lastDownCascadeMcuAttendee = currentDownCascadeMcuAttendee;
                }
                for (AttendeeForMcuPlc attendeeForMcuPlc : autoPollingAttends) {
                    if (attendeeForMcuPlc != null && StringUtils.isNotEmpty(attendeeForMcuPlc.getParticipantUuid())) {
                        if (isBroadcast) {
                            if (uuidList.contains(attendeeForMcuPlc.getParticipantUuid())) {
                                continue;
                            }
                        } else {
                            if (masterAttendee != null && attendeeForMcuPlc.getParticipantUuid().equals(masterAttendee.getParticipantUuid())) {
                                continue;
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
            } else {
                if (lastUpdateTime == 0) {
                    setLastUpdateTime(0);
                }
            }
            if (success) {
                Set<AttendeeForMcuPlc> updateAttendeeList = new HashSet<>();
                if (runtimeCount == 0) {
                    List<McuAttendeeForMcuPlc> mcuAttendees = conferenceContext.getMcuAttendees();
                    for (McuAttendeeForMcuPlc mcuAttendeeTemp : mcuAttendees) {
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
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, "轮询开始");
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("轮询已开始");
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

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
                        attendeeForMcuPlc.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                        updateAttendeeList.add(attendeeForMcuPlc);
                    }
                }
                for (AttendeeForMcuPlc attendeeForMcuPlc : chooseSeeAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        if (!updateAttendeeList.contains(attendeeForMcuPlc)) {
                            attendeeForMcuPlc.resetUpdateMap();
                        }
                        if (isBroadcast) {
                            attendeeForMcuPlc.setBroadcastStatus(BroadcastStatus.YES.getValue());
                        } else {
                            attendeeForMcuPlc.setRoundRobinStatus(AttendeeRoundRobinStatus.YES.getValue());
                        }
                        updateAttendeeList.add(attendeeForMcuPlc);
                    }
                }

                List<AttendeeForMcuPlc> pollingAttendList = new ArrayList<>();
                for (AttendeeForMcuPlc attendeeForMcuPlc : updateAttendeeList) {
                    if (attendeeForMcuPlc != null) {
                        if (chooseSeeAttendeeList.contains(attendeeForMcuPlc)) {
                            StringBuilder messageTip = new StringBuilder();
                            if (isBroadcast) {
                                messageTip.append("正在广播式轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuPlc.getDeptId()).getDeptName(), attendeeForMcuPlc.getName())).append("】");
                            } else {
                                messageTip.append("正在轮询【").append(getViewName(SysDeptCache.getInstance().get(attendeeForMcuPlc.getDeptId()).getDeptName(), attendeeForMcuPlc.getName())).append("】");
                            }
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            pollingAttendList.add(attendeeForMcuPlc);
                        }
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
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
                    Map<String, AttendeeForMcuPlc> polledAttendMap = new HashMap<>();
                    Map<String, AttendeeForMcuPlc> pollingAttendMap = new HashMap<>();
                    for (Object polledObj : polledList) {
                        AttendeeForMcuPlc attendeeForMcuPlc = (AttendeeForMcuPlc) polledObj;
                        if (attendeeForMcuPlc != null) {
                            polledAttendMap.put(attendeeForMcuPlc.getId(), attendeeForMcuPlc);
                        }
                    }
                    for (AttendeeForMcuPlc attendeeForMcuPlc : pollingAttendList) {
                        if (!polledAttendMap.containsKey(attendeeForMcuPlc.getId())) {
                            mqttService.sendPollingAttendMessage(attendeeForMcuPlc, conferenceContext, true);
                        }
                        pollingAttendMap.put(attendeeForMcuPlc.getId(), attendeeForMcuPlc);
                    }
                    for (AttendeeForMcuPlc attendeeForMcuPlc : polledAttendMap.values()) {
                        if (!pollingAttendMap.containsKey(attendeeForMcuPlc.getId())) {
                            mqttService.sendPollingAttendMessage(attendeeForMcuPlc, conferenceContext, false);
                        }
                    }
                } else {
                    for (AttendeeForMcuPlc attendeeForMcuPlc : pollingAttendList) {
                        mqttService.sendPollingAttendMessage(attendeeForMcuPlc, conferenceContext, true);
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
