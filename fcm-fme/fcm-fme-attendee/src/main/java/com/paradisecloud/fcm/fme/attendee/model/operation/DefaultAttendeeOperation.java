/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.attendee.model.operation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.exception.AttendeeRepeatException;
import com.paradisecloud.fcm.fme.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.core.ConferenceAttendeeImportanceMonitor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.SplitScreenCreaterMap;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.creater.CustomScreenCreater;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.fme.model.busi.operation.DefaultViewOperation;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    public static final int _INT = 10;
    public static final String LOCAL_R_S = "本地录播";
    private volatile List<JSONObject> defaultViewDepts = new ArrayList<>();

    private volatile Thread currentThread;
    private volatile Boolean repeated = Boolean.TRUE;
    private volatile int repeatedCount = 10;
    private HashSet<String> targetAttendeeIdSet = new HashSet<>();


    public void setRepeatedCount(int repeatedCount) {
        this.repeatedCount = repeatedCount;
    }

    public int getRepeatedCount() {
        return repeatedCount;
    }

    public Boolean getRepeated() {
        return repeated;
    }

    public void setRepeated(Boolean repeated) {
        this.repeated = repeated;
    }

    /**
     * 窗格轮询线程
     */
    private volatile CellScreenAttendeePollingThread cellScreenAttendeePollingThread;

    private volatile List<Attendee> targetAttendees = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();


    private ExecutorService opThread = Executors.newSingleThreadExecutor();


    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(ConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public void initSplitScreen() {
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridgesByDept(conferenceContext.getDeptId());

        CustomScreenCreater customScreenCreater = null;
        if(SplitScreenCreaterMap.isCustomLayoutTemplate(defaultViewLayout)){
            customScreenCreater = fbs.get(0).getDataCache().getSplitScreenCreaterMap().get(defaultViewLayout);
        }

        if (Objects.isNull(customScreenCreater)) {
            this.splitScreen = fbs.get(0).getDataCache().getSplitScreenCreaterMap().create(defaultViewLayout, YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES
                    ? AttendeeImportance.BROADCAST.getEndValue()
                    : AttendeeImportance.CHOOSE_SEE.getEndValue());
        } else {
            JSONObject json = customScreenCreater.getLayoutTemplate();
            JSONArray panes = json.getJSONArray("panes");
            if (panes != null && panes.size() > 0) {
                CustomLayout customLayout = new CustomLayout(customScreenCreater.getLayout(), panes.toArray().length, YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES
                        ? AttendeeImportance.BROADCAST.getEndValue()
                        : AttendeeImportance.CHOOSE_SEE.getEndValue());
                this.splitScreen = customLayout;
            }


        }
    }

    @Override
    public void operate() {
        while (currentThread != null) {
            cancel();
            ThreadUtils.sleep(300);
            logger.info("等待上次默认试图结束！");
        }
        currentThread = new Thread(() -> {

            if (conferenceContext.getLastAttendeeOperation() != null && conferenceContext.getLastAttendeeOperation() instanceof PollingAttendeeOpreationImpl) {
                PollingAttendeeOpreationImpl dao = (PollingAttendeeOpreationImpl) conferenceContext.getLastAttendeeOperation();
                dao.cancel();

                // 等待默认视图完全结束
                while (dao.isRunning()) {
                    ThreadUtils.sleep(50);
                }

                logger.info("--轮询已完全结束--开始显示默认视图----" + conferenceContext.getName());
            }

            StringBuilder messageTip = new StringBuilder();
            messageTip.append("当前已设置为默认视图");
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);

            // 推送默认视图状态消息
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, true);
            logger.info(messageTip.toString());
            isCancel = false;

            if (attendees != null) {
                for (Attendee a : attendees) {
                    checkedAttendeeIdSet.add(a.getId());
                }
            }

            if (this.splitScreen instanceof MainPaneSplitScreen) {
                int f;
                if (this.splitScreen instanceof AllEqualSplitScreen) {
                    f = 25;
                } else if (this.splitScreen instanceof OnePlusNSplitScreen) {
                    f = 10;
                } else {
                    f = 6;
                }

                int c = getCheckedMeetingJoinedCount();
                c = c <= 0 ? 1 : (c > f ? f : c);
                this.splitScreen.initCellScreen(c, YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES
                        ? AttendeeImportance.BROADCAST.getEndValue()
                        : AttendeeImportance.CHOOSE_SEE.getEndValue());
            }

            cellScreenAttendeePollingThread = new CellScreenAttendeePollingThread(DefaultAttendeeOperation.this, conferenceContext
                    , defaultViewPollingInterval <= 0 ? 3 : defaultViewPollingInterval);
            if (splitScreen != null) {
                for (CellScreen cellScreen : splitScreen.getCellScreens()) {
                    if (!ObjectUtils.isEmpty(cellScreen.getAttendees())) {
                        if (cellScreen.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.ROUND) {
                            cellScreenAttendeePollingThread.addCellScreen(cellScreen);
                        }
                        for (Attendee a : cellScreen.getAttendees()) {
                            checkedAttendeeIdSet.add(a.getId());
                        }
                    }
                }
            }

            repeatedCount = 10;
            // 启动窗格轮询线程
            cellScreenAttendeePollingThread.start();
            while (true) {
                ThreadUtils.sleep(10);
                if (repeatedCount > 0) {
                    try {
                        if (isCancel || conferenceContext.isEnd()) {
                            throw new PollingCancelException();
                        }

                        if (splitScreen instanceof AutomaticSplitScreen
                            || splitScreen instanceof AllEqualSplitScreen
                            || splitScreen instanceof OnePlusNSplitScreen
                            || splitScreen instanceof StackedSplitScreen
                            || splitScreen instanceof TelepresenceSplitScreen) {
                            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                            CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
                            if ((coSpace.getPanePlacementHighestImportance() != AttendeeImportance.COMMON.getStartValue())
                                    || (!splitScreen.getLayout().equals(coSpace.getDefaultLayout()))
                                    || coSpace.getPanePlacementSelfPaneMode() != null) {

                                CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
                                // 设置窗格最高权重值
                                coSpaceParamBuilder.panePlacementHighestImportance();
                                coSpaceParamBuilder.defaultLayout(splitScreen.getLayout());
                                coSpaceParamBuilder.panePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF.getStringValue());

                                // 更新CoSpace缓存
                                BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);

                                StringBuilder messageTip1 = new StringBuilder();
                                messageTip1.append("当前已设置为" + splitScreen.getLayoutName() + "分屏");
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                            }

                            // 设置所有分会场为自动分屏
                            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);
                            BeanFactory.getBean(IAttendeeService.class).updateAttendeeImportance(conferenceContext, AttendeeImportance.COMMON);
                            Thread.sleep(1 * 1000);
                        } else if (splitScreen instanceof AllEqualSplitScreen
                                && YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.NO
                                && YesOrNo.convert(defaultViewIsFill) == YesOrNo.YES
                                && defaultViewIsDisplaySelf == -1) {
                            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                            CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());

                            if ((!AllEqualSplitScreen.LAYOUT.equals(coSpace.getDefaultLayout())) || coSpace.getPanePlacementSelfPaneMode() != null) {
                                CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();

                                // 设置窗格最高权重值
                                coSpaceParamBuilder.panePlacementHighestImportance();
                                coSpaceParamBuilder.defaultLayout(AllEqualSplitScreen.LAYOUT);
                                coSpaceParamBuilder.panePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF.getStringValue());

                                // 更新CoSpace缓存
                                BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);

                                StringBuilder messageTip1 = new StringBuilder();
                                messageTip1.append("当前已设置为全等分屏");
                                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                            }
                            // 设置所有分会场为全等分屏
                            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, AllEqualSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);
                            BeanFactory.getBean(IAttendeeService.class).updateAttendeeImportance(conferenceContext, AttendeeImportance.COMMON);
                            Thread.sleep(1 * 1000);

                        } else {
                            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                            CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
                            String oldLayout = coSpace.getDefaultLayout();
                            if ((AutomaticSplitScreen.LAYOUT.equals(oldLayout)
                                || AllEqualSplitScreen.LAYOUT.equals(oldLayout)
                                || StackedSplitScreen.LAYOUT.equals(oldLayout)
                                || TelepresenceSplitScreen.LAYOUT.equals(oldLayout)
                                || OnePlusNSplitScreen.LAYOUT.equals(oldLayout))
                                && defaultViewIsDisplaySelf == PanePlacementSelfPaneMode.SELF.getValue()) {
                                Thread.sleep(1000);
                            }
                            initTargetAttendees();
                            clearOperatedAttendees();
                            clearOperatedAttendeesForOthers();
                            // 分屏数作为CountDownLatch数
                            CountDownLatch latch = new CountDownLatch(splitScreen.getCellScreens().size());
                            // 巡检初始化状态
                            init();
                            if (isCancel || conferenceContext.isEnd()) {
                                throw new PollingCancelException();
                            }

                            if (cellScreenAttendeePollingThread.isStart()) {
                                while (!cellScreenAttendeePollingThread.isProcessingThisRound()) {
                                    Thread.sleep(5);
                                }

                                for (Attendee a : cellScreenAttendeePollingThread.getOperatedAttendees()) {
                                    addOperatedAttendee(a);
                                }
                            }
                            // 处理分屏轮询/选看
                            processCellScreenOperation(latch, initConvertTarget(targetAttendees));

                            if (latch.getCount() > 0) {
                                // 阻塞，直到所有窗格完成处理
                                latch.await();
                            }
                            List<Attendee> otherAttendees = new ArrayList<>();
                            for (Attendee attendee : targetAttendees) {
                                if (attendee != null && attendee.isMeetingJoined()) {
                                    if (conferenceContext.getMasterAttendee() == null
                                            || !attendee.getId().equals(conferenceContext.getMasterAttendee().getId())
//                                            || (attendee.getId().equals(conferenceContext.getMasterAttendee().getId()) && PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf) == PanePlacementSelfPaneMode.SELF)
                                    ) {
                                        if (!targetAttendeeIdSet.contains(attendee.getId())) {
//                                            otherAttendees.add(attendee);
                                        }
                                    }
                                }
                            }
                            int importance = splitScreen.getCellScreens().get(splitScreen.getCellScreens().size() - 1).getImportance() - 1;
                            CountDownLatch latch1 = new CountDownLatch(otherAttendees.size());
                            for (int i = 0; i < otherAttendees.size(); i++) {
                                final int iFinal = i;
                                opThread.execute(() -> {
                                    try {
                                        Attendee attendee = otherAttendees.get(iFinal);
                                        if (attendee != null && attendee.isMeetingJoined()) {
                                            attendee.setOtherImportance(true);
                                            updateImportance(importance - iFinal, attendee);
                                            addOperatedAttendeeForOthers(attendee);

                                            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
                                            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
                                            attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                                            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());
                                            attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());

                                            if (attendee.getUpdateMap().size() > 1) {
                                                Map<String, Object> updateMap = new HashMap<>(attendee.getUpdateMap());
                                                updateMap.put("id", attendee.getId());
                                                updateMap.put("onlineStatus", attendee.getOnlineStatus());
                                                updateMap.put("meetingStatus", attendee.getMeetingStatus());
                                                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    latch1.countDown();
                                });
                            }
                            if (latch1.getCount() > 0) {
                                latch1.await();
                            }
                            ConferenceAttendeeImportanceMonitor.getInstance().put(conferenceContext);
                            Thread.sleep(1000);
                        }
                        repeatedCount--;
                        if (conferenceContext.getAttendeeOperation() != DefaultAttendeeOperation.this) {
                            throw new PollingCancelException();
                        }
                    } catch (Throwable e) {
                        if (e instanceof AttendeeRepeatException) {
                            StringBuilder error = new StringBuilder();
                            error.append("显示布局会场列表存在重复参会：\n");
                            for (Attendee attendee : targetAttendees) {
                                error.append("                         ").append(attendee.getDeptId()).append(", ").append(attendee.getId()).append(", ").append(attendee.getName()).append('\n');
                            }
                            logger.error(error.toString());
                        } else {
                            logger.error("终止默认视图", e);
                        }

                        // 销毁窗格轮询线程
                        if (cellScreenAttendeePollingThread != null) {
                            cellScreenAttendeePollingThread.setCancel(true);
                            cellScreenAttendeePollingThread.interrupt();
                        }
                        repeatedCount = 0;
                        setRepeated(false);
                        break;
                    }
                }

            }


            try {
                if (!(nextAttendeeOperation instanceof DefaultAttendeeOperation)) {
                    // 推送默认视图状态消息
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);
                }

                // reset();

                StringBuilder messageTip1 = new StringBuilder();
                messageTip1.append("默认视图已结束");
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                logger.info("默认视图已结束");
            } catch (Throwable e) {
                logger.error("结束默认视图出错", e);
            } finally {
                isCancel = false;
                cellScreenAttendeePollingThread = null;
                currentThread = null;
            }

        });
        currentThread.start();
        ThreadUtils.sleep(300);
    }

    private void reset() {
        try {
            if (nextAttendeeOperation instanceof DefaultAttendeeOperation || conferenceContext.isEnd()) {
                return;
            }
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
            CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
            CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();

            // 设置窗格最高权重值
            coSpaceParamBuilder.panePlacementHighestImportance();

            // 设置是否显示自己
            coSpaceParamBuilder.panePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF.getStringValue());

            // 更新CoSpace缓存
            BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
        } catch (Throwable e) {
            logger.error("reset error", e);
        }
    }


    private List<Attendee> initConvertTarget(List<Attendee> targetAttendees) {
        if (CollectionUtils.isEmpty(targetAttendees)) {
            return null;
        }
        List<Attendee> targets = targetAttendees.stream().filter(attendee -> {
            if (YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.NO
                    && PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf) != PanePlacementSelfPaneMode.SELF
                    && attendee == conferenceContext.getMasterAttendee()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        return targets;

    }

    private Attendee getCellScreenChooseSeeAttendeeCell(List<Attendee> targets, CellScreen cellScreen, boolean isFill) {
        if (CollectionUtils.isEmpty(targets)) {
            return null;
        }
        if (CollectionUtils.isEmpty(cellScreen.getAttendees())) {
            if (isFill) {
                for (int i = 0; i < targets.size(); i++) {
                    if (targets.get(i).isMeetingJoined()) {
                        return targets.remove(i);
                    }
                }
            } else {
                return targets.remove(0);
            }

        }
        return null;

    }

    private Attendee getCellScreenChooseSeeAttendee(int serialNumber, boolean isFill) {
        serialNumber -= 1;
        int i = 0;
        for (Attendee attendee : targetAttendees) {
            if (YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.NO
                    && PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf) != PanePlacementSelfPaneMode.SELF
                    && attendee == conferenceContext.getMasterAttendee()) {
                // 轮询非广播，绕开主会场
                continue;
            }

            if (i > serialNumber) {
                break;
            }

            if (isFill) {
                if (attendee.isMeetingJoined()) {
                    if (i == serialNumber) {
                        return attendee;
                    }
                    i++;
                }
            } else {
                if (i == serialNumber) {
                    return attendee;
                }
                i++;
            }
        }

        return null;
    }

    /**
     * <pre>处理分屏轮询或选看</pre>
     *
     * @param latch
     * @author lilinhai
     * @since 2021-04-13 17:43  void
     */
    private void processCellScreenOperation(CountDownLatch latch, List<Attendee> convertAttends) {
        //  ExecutorService singleThread = Executors.newSingleThreadExecutor();
        for (CellScreen cellScreen0 : splitScreen.getCellScreens()) {
            final CellScreen cellScreen = cellScreen0;
            if (!ObjectUtils.isEmpty(cellScreen0.getAttendees())) {
                if (cellScreen0.getCellScreenAttendeeOperation() == CellScreenAttendeeOperation.CHOOSE_SEE) {
                    // 处理单分屏选看
                    new Thread(() -> {
                        processOneSplitScreenChooseSee(cellScreen);
                        latch.countDown();
                    }).start();

                } else {
                    latch.countDown();
                }
                for (Attendee attendee : cellScreen0.getAttendees()) {
                    targetAttendeeIdSet.add(attendee.getId());
                }
            } else {
                // 跳过固定窗格
                if (cellScreen0.isFixed()) {
                    latch.countDown();
                    continue;
                }
                opThread.execute(() -> {
                    Attendee attendee = getCellScreenChooseSeeAttendeeCell(convertAttends, cellScreen, YesOrNo.convert(defaultViewIsFill) == YesOrNo.YES);
                    if (attendee != null && attendee.isMeetingJoined()) {
                        // 更新权重
                        if (!operatedAttendees.contains(attendee)) {
                            updateImportance(cellScreen.getImportance(), attendee);
                            cellScreen.setLastOperationAttendee(attendee);
                            addOperatedAttendee(attendee);
                            targetAttendeeIdSet.add(attendee.getId());
                        }

                    }
                    latch.countDown();
                });

            }
        }

        // singleThread.shutdown();
    }

    private void addOperatedAttende(int i, Attendee attendee) {
        if (attendee != null && attendee.isMeetingJoined()) {
            // 更新权重
            if (!operatedAttendees.contains(attendee)) {
                updateImportance(splitScreen.getCellScreens().get(0).getImportance() - i, attendee);
                addOperatedAttendee(attendee);
            }

        }
    }

    /**
     * <pre>处理单分屏选看</pre>
     *
     * @param cellScreen void
     * @author lilinhai
     * @since 2021-04-13 16:09
     */
    private void processOneSplitScreenChooseSee(CellScreen cellScreen) {
        for (Attendee attendee : cellScreen.getAttendees()) {
            if (!attendee.isMeetingJoined() && YesOrNo.convert(defaultViewIsFill) == YesOrNo.NO) {
                return;
            }
            if (attendee != conferenceContext.getMasterAttendee() || YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES || PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf) == PanePlacementSelfPaneMode.SELF) {
                // 更新权重
                updateImportance(cellScreen.getImportance(), attendee);
                cellScreen.setLastOperationAttendee(attendee);
                addOperatedAttendee(attendee);
            }
            return;
        }
    }

    /**
     * 更新权重
     *
     * @param importance
     * @param attendee   void
     * @author lilinhai
     * @since 2021-04-13 17:26
     */
    void updateImportance(Integer importance, Attendee attendee) {
        logger.info("Default Attendee"+attendee);
        if (attendee.importanceEqual(importance) || !attendee.isMeetingJoined()) {
            AttendeeImportance attendeeImportance = AttendeeImportance.convert(importance);
            if (attendee.isOtherImportance()) {
                attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
            } else {
                if (attendeeImportance == AttendeeImportance.BROADCAST) {
                    attendee.setBroadcastStatus(BroadcastStatus.YES.getValue());
                    attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.NO.getValue());
                } else if (attendeeImportance == AttendeeImportance.CHOOSE_SEE) {
                    attendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
                    attendee.setBroadcastStatus(BroadcastStatus.NO.getValue());
                }
            }
            attendee.setCallTheRollStatus(AttendeeCallTheRollStatus.NO.getValue());
            attendee.setTalkStatus(AttendeeTalkStatus.NO.getValue());
            attendee.setRoundRobinStatus(AttendeeRoundRobinStatus.NO.getValue());

            if (attendee.getUpdateMap().size() > 1) {
                Map<String, Object> updateMap = new HashMap<>(attendee.getUpdateMap());
                updateMap.put("id", attendee.getId());
                updateMap.put("onlineStatus", attendee.getOnlineStatus());
                updateMap.put("meetingStatus", attendee.getMeetingStatus());
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, updateMap);
            }
            return;
        }
        FmeBridge subFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
        logger.info("Default subFmeBridge"+subFmeBridge);
        if(subFmeBridge==null){
            return;
        }
        // 将级联参会者设为子会议选看
        RestResponse restResponse0 = subFmeBridge.getParticipantInvoker().updateParticipant(attendee.getParticipantUuid(), new ParticipantParamBuilder()
                .importance(importance)
                .build());

        if (!restResponse0.isSuccess()) {
            String s = YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? "广播" : "选看";
            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("将参会者设为“" + s + "”失败: " + restResponse0.getMessage());
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
        }
    }

    @Override
    public void cancel() {
        setRepeatedCount(0);
        try {
            if (splitScreen instanceof AutomaticSplitScreen) {
                if (conferenceContext.getMasterAttendee() != null) {
                    BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext.getMasterAttendee(), OneSplitScreen.LAYOUT);
                    if (!AttendeeImportance.MASTER.is(conferenceContext.getMasterAttendee().getImportance())) {
                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(conferenceContext.getMasterAttendee());
                        if (fmeBridge != null) {
                            fmeBridge.getParticipantInvoker().updateParticipant(conferenceContext.getMasterAttendee().getParticipantUuid(), new ParticipantParamBuilder()
                                    .importance(AttendeeImportance.MASTER.getStartValue())
                                    .build());
                        }
                    }
                    ThreadUtils.sleep(100);
                }
            }
            if (currentThread != null) {
                isCancel = true;
                currentThread.interrupt();
            }
            if (cellScreenAttendeePollingThread != null) {
                cellScreenAttendeePollingThread.setCancel(true);
                cellScreenAttendeePollingThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (currentThread != null) {
                isCancel = true;
                currentThread.interrupt();
            }
            if (cellScreenAttendeePollingThread != null) {
                cellScreenAttendeePollingThread.setCancel(true);
                cellScreenAttendeePollingThread.interrupt();
            }
            currentThread = null;
        }

        logger.info("中断默认视图请求发起");
        currentThread = null;
    }

    private void initTargetAttendees() {
        targetAttendees.clear();
        if (!ObjectUtils.isEmpty(attendees)) {
            targetAttendees.addAll(attendees);
        }
        if (conferenceContext.getAttendeesOps() != null) {
            for (Attendee attendeeTemp : conferenceContext.getAttendeesOps()) {
                if (attendeeTemp != null && attendeeTemp.getId() != null) {
                    if (!checkedAttendeeIdSet.contains(attendeeTemp.getId())) {
                        Attendee attendeeExist = conferenceContext.getAttendeeById(attendeeTemp.getId());
                        if (attendeeExist != null) {
                            targetAttendees.add(attendeeExist);
                        }
                    }
                }
            }
        }

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                targetAttendees.add(conferenceContext.getMasterAttendee());
            }

            if (conferenceContext.getMasterAttendee().getDeptId() != conferenceContext.getDeptId().longValue() && conferenceContext.getMasterAttendeeIdSet().contains(conferenceContext.getMasterAttendee().getId())) {
                List<Attendee> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null) {
                    for (Attendee attendee : new ArrayList<>(as)) {
                        if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                            targetAttendees.add(attendee);
                        }
                    }
                }
            }
        }

        for (Attendee attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
        }

        for (Attendee attendee : conferenceContext.getMasterAttendees()) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
            List<Attendee> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null) {
                for (Attendee a : new ArrayList<>(as)) {
                    if (!checkedAttendeeIdSet.contains(a.getId())) {
                        targetAttendees.add(a);
                    }
                }
            }
        }



        Set<String> idSet = new HashSet<>();

        if(!CollectionUtils.isEmpty(targetAttendees)){
            List<Attendee> arrayList = new ArrayList<>();
            for (Attendee attendee : targetAttendees) {
                if (idSet.add(attendee.getId())) {
                    arrayList.add(attendee);
                }
            }
            targetAttendees=arrayList;
        }

        if(Objects.equals(conferenceContext.getName(), LOCAL_R_S)){
            if(!CollectionUtils.isEmpty(targetAttendees)){
                Collections.reverse(targetAttendees);
            }
        }
        for (Attendee attendee : targetAttendees) {
            attendee.setOtherImportance(false);
        }

    }

    private int getCheckedMeetingJoinedCount() {
        int c = 0;
        if (!ObjectUtils.isEmpty(attendees)) {
            for (Attendee attendee : attendees) {
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
     * <pre>初始化</pre>
     *
     * @author lilinhai
     * @since 2021-04-09 18:00  void
     */
    private void init() {
        updateCospace();

        // 批量会场设置布局（广播，设置所有会场布局，反之只设置主会场布局）
        if (YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES) {
            // 设置所有参会终端的布局，为指定的布局（单分屏或多分频）
            if (SplitScreenCreaterMap.isCustomLayoutTemplate(splitScreen.getLayout()) && StringUtils.isNotEmpty(splitScreen.getLayout())) {
                BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, splitScreen.getLayout(), AttendeeLayoutSetMode.ALL);
            } else {
                BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);
            }
        } else {
            // 设置主会场为指定布局
            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, splitScreen.getLayout(), AttendeeLayoutSetMode.MASTER);
            // 设置所有分会场为单分屏
            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.SUB);

        }
    }

    private void updateCospace() {
        boolean flag = false;
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (!ObjectUtils.isEmpty(cellScreen.getAttendees()) || cellScreen.isFixed()) {
                flag = true;
                break;
            }
        }

        PanePlacementSelfPaneMode m = PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf);

        // 这两种情况要求布局模式必须是blank或self，否则将布局模式强制置为blank
        if (YesOrNo.convert(defaultViewIsFill) == YesOrNo.NO || flag) {
            if (PanePlacementSelfPaneMode.SELF != m && PanePlacementSelfPaneMode.BLANK != m) {
                m = PanePlacementSelfPaneMode.BLANK;
            }
        }

        String coSpaceLayout = YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? splitScreen.getLayout() : OneSplitScreen.LAYOUT;
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
//        if ((coSpace.getPanePlacementHighestImportance() == null || (coSpace.getPanePlacementHighestImportance().intValue() == splitScreen.getCellScreens().get(0).getImportance()))
//                && (m.getStringValue().equals(coSpace.getPanePlacementSelfPaneMode()) || (m.getStringValue().equals("") && coSpace.getPanePlacementSelfPaneMode() == null))
//                && coSpaceLayout.equals(coSpace.getDefaultLayout())) {
//            return;
//        }

        if (coSpace.getPanePlacementHighestImportance() != null) {
            if ((coSpace.getPanePlacementHighestImportance().intValue() == splitScreen.getCellScreens().get(0).getImportance()) && coSpaceLayout.equals(coSpace.getDefaultLayout()) && (m.getStringValue().equals(coSpace.getPanePlacementSelfPaneMode()) || (m.getStringValue().equals("") && coSpace.getPanePlacementSelfPaneMode() == null))) {
                return;
            }
        }


        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();

        // 设置窗格最高权重值
        if (m != PanePlacementSelfPaneMode.OFF) {
            coSpaceParamBuilder.panePlacementHighestImportance(splitScreen.getCellScreens().get(0).getImportance());
        } else {
            coSpaceParamBuilder.panePlacementHighestImportance();
        }


        coSpaceParamBuilder.panePlacementSelfPaneMode(m.getStringValue());

        // 广播设置为指定分屏，否则设置为单分屏(2021-08-12)
        if (SplitScreenCreaterMap.isCustomLayoutTemplate(splitScreen.getLayout())) {
            coSpaceParamBuilder.defaultLayout(YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? "" : OneSplitScreen.LAYOUT);
        } else {
            coSpaceParamBuilder.defaultLayout(YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? splitScreen.getLayout() : OneSplitScreen.LAYOUT);
        }

        // 更新CoSpace缓存
        BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
        logger.error("coSpace已设置为默认布局指定设置！" + Thread.currentThread().getName());
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
    public boolean contains(Attendee attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }

    public boolean isRunning() {
        return currentThread != null;
    }

}
