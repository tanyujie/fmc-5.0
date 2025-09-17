/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : PollingAttendeeOpreation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 17:06
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.attendee.model.operation;


import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.ConferenceOpsModeEnum;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.exception.AttendeeRepeatException;
import com.paradisecloud.fcm.fme.attendee.exception.PollingCancelException;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutService;
import com.paradisecloud.fcm.fme.attendee.model.core.ConferenceAttendeeImportanceMonitor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.attendee.model.enumer.PollingStrategy;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingAttendee;
import com.paradisecloud.fcm.fme.attendee.model.polling.PollingScheme;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.SplitScreenCreaterMap;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.McuAttendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.busi.operation.PollingAttendeeOpreation;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.ConferenceCascadeHandler;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <pre>与会者轮询操作</pre>
 * @author lilinhai
 * @since 2021-02-20 17:06
 * @version V1.0
 */
public class ChairmanPollingOpreationImpl extends PollingAttendeeOpreation
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    public static final int IMPORTANCE_VALUE = 1000;

    private volatile PollingScheme pollingScheme;

    private SingleAttendeePollingOperation singleAttendeePollingOperation;

    private AttendeeOperation attendeeCurrentOperation;

    private Thread currentThread;

    private final AttendeeImportance attendeeImportance;

    private volatile List<PollingAttendee> pollingAttendeeList;
    private volatile boolean isPollingDownCascade;
    private volatile BaseAttendee lastDownCascadeMcuAttendee = null;
    private volatile BaseAttendee currentDownCascadeMcuAttendee = null;
    private volatile long runtimeCount = 0;

    /**
     * 暂停
     */
    private volatile boolean isPause;

    private ExecutorService executorService=new ThreadPoolExecutor(25, 50, 20L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024), new ThreadFactory() {
        private int index;
        @Override
        public Thread newThread(Runnable r)
        {
            index++;
            return new Thread(r, " PollingAttendeeThreadPool[]-Thread-" + index);
        }
    },new ThreadPoolExecutor.DiscardPolicy());

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-25 17:10
     * @param conferenceContext
     * @param pollingScheme
     */
    public ChairmanPollingOpreationImpl(ConferenceContext conferenceContext)
    {
        super(conferenceContext);
        PollingScheme pollingScheme = new PollingScheme();
        pollingScheme.setInterval(10);
        pollingScheme.setLayout(OneSplitScreen.LAYOUT);
        pollingScheme.setIsBroadcast(YesOrNo.NO);
        pollingScheme.setPanePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF);
        pollingScheme.setIsFill(YesOrNo.YES);
        pollingScheme.setIsFixSelf(YesOrNo.NO);
        pollingScheme.setPollingStrategy(PollingStrategy.GLOBAL);

        attendeeImportance = AttendeeImportance.ROUND;
        this.pollingScheme = pollingScheme;
        List<FmeBridge> fbs = FmeBridgeCache.getInstance().getFmeBridgesByDept(conferenceContext.getDeptId());
        this.splitScreen = fbs.get(0).getDataCache().getSplitScreenCreaterMap().create(pollingScheme.getLayout(), attendeeImportance.getEndValue());
    }

    @Override
    public void operate()
    {
      //  conferenceContext.setStartRound(true);
        currentThread = new Thread(()->{

            if (conferenceContext.getLastAttendeeOperation() != null && conferenceContext.getLastAttendeeOperation() instanceof DefaultAttendeeOperation)
            {
                DefaultAttendeeOperation dao = (DefaultAttendeeOperation)conferenceContext.getLastAttendeeOperation();
                dao.cancel();

                // 等待默认视图完全结束
                while (dao.isRunning())
                {
                    ThreadUtils.sleep(50);
                }

                logger.info("--默认视图已完全结束--开始轮询----" + conferenceContext.getName());
            }

            logger.info("----开始轮询----" + conferenceContext.getName());
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_STARTED, "轮询开始");

            StringBuilder messageTip = new StringBuilder();
            messageTip.append("轮询已开始");
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            List<PollingAttendee> attendeeViewGroup = new ArrayList<>();
            conferenceContext.setConferenceMode(ConferenceOpsModeEnum.CHAIRMAN_POLLING.name());
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入主席轮询模式！");
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_MODEL, ConferenceOpsModeEnum.CHAIRMAN_POLLING.name());
            while (true)
            {
                try
                {
                    if (isCancel || conferenceContext.isEnd()||conferenceContext.getAttendeeOperation() != ChairmanPollingOpreationImpl.this) {
                        throw new PollingCancelException("轮询正常结束！");
                    }
                    if (isMasterLeft()) {
                        throw new PollingCancelException("轮询正常结束！");
                    }
                    parse();
                    if (ObjectUtils.isEmpty(pollingAttendeeList)) {
                        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "轮询方案参会列表为空，轮询停止！");
                        throw new PollingCancelException("轮询方案参会列表为空，轮询停止！");
                    }
                    if (pollingScheme.getIsFixSelf() == YesOrNo.YES) {
                        List<PollingAttendee> collect = pollingAttendeeList.stream().filter(p -> !Objects.equals(p.getAttendee(), conferenceContext.getMasterAttendee())).collect(Collectors.toList());
                        pollingAttendeeList = collect;
                    }
                    for (int i = 0; i < pollingAttendeeList.size(); i++) {
                        if (isMasterLeft()) {
                            throw new PollingCancelException("轮询正常结束！");
                        }
                        currentDownCascadeMcuAttendee = null;
                        PollingAttendee pollingAttendee = pollingAttendeeList.get(i);
                        Attendee attendee = pollingAttendee.getAttendee();
                        if (attendee != null) {
                            if (pollingScheme.getIsFill() == YesOrNo.YES && !attendee.isMeetingJoined()) {
                                continue;
                            }
                            if (PollingStrategy.GLOBAL == pollingScheme.getPollingStrategy()
                                    || PollingStrategy.GLOBAL_AND_DEPT_FIRST == pollingScheme.getPollingStrategy()) {
                                if (attendee.isLocked()) {
                                    continue;
                                }
                            }
                            if (runtimeCount == 0) {
                                List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                                for (McuAttendee mcuAttendeeTemp : mcuAttendees) {
                                    BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendeeTemp.getCascadeConferenceId()));
                                    if (mcuConferenceContext != null) {
                                        try {
                                            ConferenceCascadeHandler.defaultChooseSee(mcuAttendeeTemp.getId());
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                runtimeCount++;
                            }
                            if (pollingScheme.getIsFixSelf() == YesOrNo.NO) {

                                if (pollingScheme.getIsBroadcast() == YesOrNo.NO && pollingScheme.getPanePlacementSelfPaneMode() != PanePlacementSelfPaneMode.SELF
                                        && pollingAttendee.getAttendee() == conferenceContext.getMasterAttendee()) {
                                    continue;
                                }
                                if (!attendeeViewGroup.contains(pollingAttendee)) {
                                    attendeeViewGroup.add(pollingAttendee);
                                    if (attendeeViewGroup.size() == splitScreen.getCellScreens().size()) {
                                        singleAttendeePolling(attendeeViewGroup);
                                        attendeeViewGroup.clear();
                                    } else if (pollingAttendeeList.size() < splitScreen.getCellScreens().size()) {
                                        if (i == pollingAttendeeList.size() - 1) {
                                            singleAttendeePolling(attendeeViewGroup);
                                            attendeeViewGroup.clear();
                                        }
                                    }
                                }
                            } else {
                                if (!attendeeViewGroup.contains(pollingAttendee)) {
                                    attendeeViewGroup.add(pollingAttendee);
                                    if (splitScreen.getCellScreens().size() == 1) {
                                        if (attendeeViewGroup.size() == splitScreen.getCellScreens().size()) {
                                            singleAttendeePolling(attendeeViewGroup);
                                            attendeeViewGroup.clear();
                                        } else if (pollingAttendeeList.size() < splitScreen.getCellScreens().size()) {
                                            if (i == pollingAttendeeList.size() - 1) {
                                                singleAttendeePolling(attendeeViewGroup);
                                                attendeeViewGroup.clear();
                                            }
                                        }
                                    } else {
                                        if (attendeeViewGroup.size() + 1 == splitScreen.getCellScreens().size()) {
                                            singleAttendeePolling(attendeeViewGroup);
                                            attendeeViewGroup.clear();
                                        } else if (pollingAttendeeList.size() < splitScreen.getCellScreens().size()) {
                                            if (i == pollingAttendeeList.size() - 1) {
                                                singleAttendeePolling(attendeeViewGroup);
                                                attendeeViewGroup.clear();
                                            }
                                        }
                                    }

                                }
                            }
                        } else {
                            if (pollingAttendee.getDownCascadeAttendee() != null) {
                                BaseAttendee downCascadeAttendeeTemp = pollingAttendee.getDownCascadeAttendee();
                                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(downCascadeAttendeeTemp.getContextKey());
                                if (downCascadeConferenceContext != null) {
                                    BaseAttendee downCascadeAttendee = downCascadeConferenceContext.getAttendeeById(downCascadeAttendeeTemp.getId());
                                    if (downCascadeAttendee.isMeetingJoined()) {
                                        Attendee mcuAttendee = conferenceContext.getAttendeeById(downCascadeConferenceContext.getId());
                                        if (mcuAttendee != null) {
                                            if (mcuAttendee.isMeetingJoined()) {
                                                try {
                                                    boolean isBroadcast = pollingScheme.getIsBroadcast() == YesOrNo.YES;
                                                    ConferenceCascadeHandler.chooseSee(downCascadeConferenceContext.getId(), downCascadeAttendee.getId(), true, isBroadcast, true);
                                                    currentDownCascadeMcuAttendee = mcuAttendee;

                                                    if (runtimeCount == 0) {
                                                        List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                                                        for (McuAttendee mcuAttendeeTemp : mcuAttendees) {
                                                            if (!mcuAttendeeTemp.getId().equals(currentDownCascadeMcuAttendee.getId())) {
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
                                                    }

                                                    if (lastDownCascadeMcuAttendee == currentDownCascadeMcuAttendee) {
                                                        int sleepSeconds = pollingScheme.getInterval();
                                                        if (splitScreen instanceof OneSplitScreen)
                                                        {
                                                            sleepSeconds = pollingAttendee.getInterval() != null ? pollingAttendee.getInterval() : sleepSeconds;
                                                            if (sleepSeconds == 0)
                                                            {
                                                                sleepSeconds = 3;
                                                            }
                                                        }

                                                        // 休眠指定秒数
                                                        if (singleAttendeePollingOperation != null)
                                                        {
                                                            sleep((sleepSeconds * 1000)-500);
                                                        }
                                                        else
                                                        {
                                                            sleep(sleepSeconds * 1000);
                                                        }
                                                    } else {
                                                        PollingAttendee mcuPollingAttendee = new PollingAttendee(mcuAttendee);
                                                        attendeeViewGroup.add(mcuPollingAttendee);
                                                        if (splitScreen.getCellScreens().size() == 1) {
                                                            if (attendeeViewGroup.size() == splitScreen.getCellScreens().size()) {
                                                                singleAttendeePolling(attendeeViewGroup);
                                                                attendeeViewGroup.clear();
                                                            }
                                                        } else {
                                                            if (attendeeViewGroup.size() + 1 == splitScreen.getCellScreens().size()) {
                                                                singleAttendeePolling(attendeeViewGroup);
                                                                attendeeViewGroup.clear();
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (runtimeCount == 0) {
                                List<McuAttendee> mcuAttendees = conferenceContext.getMcuAttendees();
                                for (McuAttendee mcuAttendeeTemp : mcuAttendees) {
                                    BaseConferenceContext mcuConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(mcuAttendeeTemp.getCascadeConferenceId()));
                                    if (mcuConferenceContext != null) {
                                        try {
                                            ConferenceCascadeHandler.defaultChooseSee(mcuAttendeeTemp.getId());
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                runtimeCount++;
                            }
                        }
                    }
                    if (isCancel || conferenceContext.isEnd()||conferenceContext.getAttendeeOperation() != ChairmanPollingOpreationImpl.this) {
                        conferenceContext.setStartRound(false);
                        throw new PollingCancelException("轮询正常结束！");
                    }
                    if (isMasterLeft()) {
                        throw new PollingCancelException("轮询正常结束！");
                    }

                }
                catch (Throwable e)
                {
                    if (e instanceof AttendeeRepeatException)
                    {
                        StringBuilder error = new StringBuilder();
                        error.append("轮询参会列表存在重复参会：\n");
                        for (PollingAttendee pollingAttendee : pollingAttendeeList)
                        {
                            error.append("                         ").append(pollingAttendee.getAttendee().getDeptId()).append(", ");
                            error.append(pollingAttendee.getAttendee().getId()).append(", ").append(pollingAttendee.getAttendee().getName()).append('\n');
                        }
                        logger.error(error.toString());
                    }
                    else
                    {
                        logger.error("轮询出错", e);
                    }
                    conferenceContext.setStartRound(false);
                    break;
                }
            }

            currentThread = null;

            new Thread(() -> {
                try {
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
                    CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
                    CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();

                    // 设置窗格最高权重值
                    coSpaceParamBuilder.panePlacementHighestImportance();

                    // 设置是否显示自己
                    coSpaceParamBuilder.panePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF.getStringValue());
                    RestResponse restResponse = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), coSpaceParamBuilder.build());
                    if (!restResponse.isSuccess())
                    {
                        throw new SystemException(1005435, "修改CoSpace是否显示自己失败！");
                    }

                    // 更新CoSpace缓存
                    BeanFactory.getBean(ICoSpaceService.class).updateCoSpaceCache(fmeBridge, coSpace.getId());


                    if (singleAttendeePollingOperation != null)
                    {
                        singleAttendeePollingOperation.cancel(attendeeCurrentOperation);
                        singleAttendeePollingOperation = null;
                    }
                    conferenceContext.setStartRound(false);
                    logger.info("----结束轮询----" + conferenceContext.getName());
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询结束");

                    StringBuilder messageTip1 = new StringBuilder();
                    messageTip1.append("轮询已结束");
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                } catch (Throwable e) {
                    conferenceContext.setStartRound(false);
                    logger.error("PollingAttendeeOpreationImpl end error",e);
                    e.printStackTrace();
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_ENDED, "轮询错误,已自动结束轮询");
                    cancel();
                }
                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
            }).start();
        });
        currentThread.start();
        ThreadUtils.sleep(100);
    }

    /**
     * <pre>初始化</pre>
     * @author lilinhai
     * @since 2021-04-09 18:00  void
     */
    private void init()
    {
        updateCoSpace();

        // 批量会场设置布局（广播，设置所有会场布局，反之只设置主会场布局）
        if (pollingScheme.getIsBroadcast() == YesOrNo.YES)
        {
            // 设置所有参会终端的布局，为指定的布局（单分屏或多分频）
            //  BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);

            // 设置所有参会终端的布局，为指定的布局（单分屏或多分频）
            if(SplitScreenCreaterMap.isCustomLayoutTemplate(splitScreen.getLayout())&& StringUtils.isNotEmpty(splitScreen.getLayout())){
                BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, splitScreen.getLayout(), AttendeeLayoutSetMode.ALL);
            }else {
                BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);
            }
        }
        else
        {
            // 设置主会场为指定布局
            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, splitScreen.getLayout(), AttendeeLayoutSetMode.MASTER);

            // 设置所有分会场为单分屏
            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, "speakerOnly", AttendeeLayoutSetMode.SUB);
        }
    }

    private void updateCoSpace()
    {
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();

        // 设置窗格最高权重值
        if (pollingScheme.getPanePlacementSelfPaneMode() != PanePlacementSelfPaneMode.OFF)
        {
            coSpaceParamBuilder.panePlacementHighestImportance(splitScreen.getCellScreens().get(0).getImportance());
        }
        else
        {
            coSpaceParamBuilder.panePlacementHighestImportance();
        }

        boolean flag = false;
        for (CellScreen cellScreen : splitScreen.getCellScreens())
        {
            if (!ObjectUtils.isEmpty(cellScreen.getAttendees()) || cellScreen.isFixed())
            {
                flag = true;
                break;
            }
        }

        PanePlacementSelfPaneMode m = pollingScheme.getPanePlacementSelfPaneMode();

        // 这两种情况要求布局模式必须是blank或self，则讲布局模式强制置为blank
        if (pollingScheme.getIsFill() == YesOrNo.NO || flag)
        {
            if (PanePlacementSelfPaneMode.SELF != m && PanePlacementSelfPaneMode.BLANK != m)
            {
                m = PanePlacementSelfPaneMode.BLANK;
            }
        }
        coSpaceParamBuilder.panePlacementSelfPaneMode(m.getStringValue());
        // 广播设置为指定分屏，否则设置为单分屏(2021-08-12)
        // coSpaceParamBuilder.defaultLayout(pollingScheme.getIsBroadcast() == YesOrNo.YES ? splitScreen.getLayout() : OneSplitScreen.LAYOUT);
        if (SplitScreenCreaterMap.isCustomLayoutTemplate(splitScreen.getLayout())) {
            coSpaceParamBuilder.defaultLayout(pollingScheme.getIsBroadcast() == YesOrNo.YES ? "" : OneSplitScreen.LAYOUT);
        } else {
            coSpaceParamBuilder.defaultLayout(pollingScheme.getIsBroadcast() == YesOrNo.YES ? splitScreen.getLayout() : OneSplitScreen.LAYOUT);
        }
        if (pollingScheme.getIsBroadcast() == YesOrNo.NO && pollingScheme.getIsFixSelf() == YesOrNo.YES) {
            // 更新CoSpace缓存
            coSpaceParamBuilder = new CoSpaceParamBuilder();
            m = PanePlacementSelfPaneMode.SELF;
            coSpaceParamBuilder.panePlacementSelfPaneMode(m.getStringValue());
            coSpaceParamBuilder.panePlacementHighestImportance(splitScreen.getCellScreens().get(0).getImportance());
        }
        BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
    }

    private void singleAttendeePolling(List<PollingAttendee> attendeeViewGroup) throws InterruptedException
    {
        if (isCancel || conferenceContext.isEnd())
        {
            throw new PollingCancelException();
        }
        if (isMasterLeft()) {
            throw new PollingCancelException("轮询正常结束！");
        }

        if (isPause)
        {
            synchronized (this)
            {
                if (isPause)
                {
                    this.wait();
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, false);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已恢复运行状态");
                }
            }
        }

        List<Attendee> attendees = new ArrayList<>();
        for (PollingAttendee pollingAttendee : attendeeViewGroup)
        {
            attendees.add(pollingAttendee.getAttendee());
        }

        if (attendees.isEmpty())
        {
            return;
        }

        if (singleAttendeePollingOperation != null)
        {
            List<Attendee> as0 = singleAttendeePollingOperation.getAttendees();
            if (as0.size() == attendees.size())
            {
                boolean isEQ = true;
                for (Attendee attendee : as0)
                {
                    if (!attendees.contains(attendee))
                    {
                        isEQ = false;
                    }
                }
                if (isEQ) {
                    return;
                }
            }
        }


        if (pollingScheme.getIsFixSelf() == YesOrNo.YES) {
            attendees.add(0, conferenceContext.getMasterAttendee());
            if(Objects.equals(splitScreen.getLayout(),OneSplitScreen.LAYOUT)){
                attendees.clear();
                attendees.add(conferenceContext.getMasterAttendee());
            }
        }
        splitScreen.reInitCellScreenImportance(attendeeImportance.getEndValue());
        CountDownLatch latch = new CountDownLatch(attendees.size());
        SingleAttendeePollingOperation o = new SingleAttendeePollingOperation(conferenceContext, splitScreen, attendees, latch, executorService);
        o.operate();

        if (latch.getCount() > 0) {
            // 阻塞，直到所有窗格完成处理
            latch.await();
        }

        clearOperatedAttendees();
        if (isPushOperatedAttendeesToMonitor())
        {
            for (Attendee a : o.getOperatedAttendees())
            {
                addOperatedAttendee(a);
            }
            ConferenceAttendeeImportanceMonitor.getInstance().put(conferenceContext);
        }
//        Thread.sleep(500L);
        init();
        if (isPollingDownCascade) {
            if (lastDownCascadeMcuAttendee != null && lastDownCascadeMcuAttendee != currentDownCascadeMcuAttendee) {
                BaseConferenceContext downCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(lastDownCascadeMcuAttendee.getCascadeConferenceId()));
                if (downCascadeConferenceContext != null) {
                    ConferenceCascadeHandler.defaultChooseSee(downCascadeConferenceContext.getId());
                }
            }
            lastDownCascadeMcuAttendee = currentDownCascadeMcuAttendee;
        }

        int sleepSeconds = pollingScheme.getInterval();
        if (splitScreen instanceof OneSplitScreen)
        {
            sleepSeconds = attendeeViewGroup.get(0).getInterval() != null ? attendeeViewGroup.get(0).getInterval() : sleepSeconds;
            if (sleepSeconds == 0)
            {
                sleepSeconds = 3;
            }
        }

        // 休眠指定秒数
        if (singleAttendeePollingOperation != null)
        {
            sleep((sleepSeconds * 1000)-500);
        }
        else
        {
            sleep(sleepSeconds * 1000);
        }

        singleAttendeePollingOperation = o;
    }

    @Override
    public void cancel()
    {
       // conferenceContext.setStartRound(false);
//        conferenceContext.setConferenceModel(ConferenceOpsModeEnum.DIRECT.name());
//        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭主席轮询模式！");
//        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_MODEL, ConferenceOpsModeEnum.DIRECT.name());
        isCancel = true;
        setPause(false);
        if (currentThread != null)
        {
            currentThread.interrupt();
        }
        executorService.shutdownNow();
    }

    /**
     * <p>Get Method   :   singleAttendeePollingOperation SingleAttendeePollingOperation</p>
     * @return singleAttendeePollingOperation
     */
    public SingleAttendeePollingOperation getSingleAttendeePollingOperation()
    {
        return singleAttendeePollingOperation;
    }

    /**
     * 解析
     * @author lilinhai
     * @since 2021-04-09 14:01
     * @return List<PollingAttendee>
     */
    private void parse()
    {
        Set<String> idSet = new HashSet<>();
        pollingScheme.removeInvalidAttendee(conferenceContext);
        pollingAttendeeList = pollingScheme.getPollingStrategy().getStrategy().parse(conferenceContext, pollingScheme.getDeptPollingAttendeesList(), splitScreen);
        for (PollingAttendee pollingAttendee : pollingAttendeeList)
        {
            if (pollingAttendee.getAttendee() != null) {
                if (!idSet.add(pollingAttendee.getAttendee().getId())) {
                    StringBuilder messageTip1 = new StringBuilder();
                    messageTip1.append("轮询列表存在重复参会【" + pollingAttendee.getAttendee().getId() + ", " + pollingAttendee.getAttendee().getName() + "】，异常终止！");
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip1);
                    throw new AttendeeRepeatException(messageTip1.toString());
                }
            }
            if (pollingAttendee.getDownCascadeAttendee() != null) {
                isPollingDownCascade = true;
            }
        }
        if (isPollingDownCascade) {
            int maxImportance = this.pollingScheme.getIsBroadcast() == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();
            this.splitScreen = new OneSplitScreen(maxImportance);
        }
        idSet.clear();
    }

    /**
     * <p>Get Method   :   pollingScheme PollingScheme</p>
     * @return pollingScheme
     */
    public PollingScheme getPollingScheme()
    {
        return pollingScheme;
    }

    public void update(PollingScheme pollingScheme)
    {
        this.pollingScheme = pollingScheme;
    }

    /**
     * <p>Get Method   :   isPause boolean</p>
     * @return isPause
     */
    @Override
    public boolean isPause()
    {
        return isPause;
    }

    /**
     * <p>Set Method   :   isPause boolean</p>
     * @param isPause
     */
    public void setPause(boolean isPause)
    {
        this.isPause = isPause;
        synchronized (this)
        {
            if (!isPause)
            {
                this.notify();
            }
            else
            {
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_POLLING_PAUSED, true);
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "轮询已进入暂停状态");
            }
        }
    }

    public boolean isRunning()
    {
        return currentThread != null;
    }

    private void sleep(long millis) throws InterruptedException {
        if (millis > 1000) {
            long sleepSeconds = millis / 1000;
            long remainMillis = millis % 1000;
            for (int i = 0; i < sleepSeconds; i++) {
                if (isMasterLeft()) {
                    throw new PollingCancelException("轮询正常结束！");
                }
                Thread.sleep(1000);
            }
            if (isMasterLeft()) {
                throw new PollingCancelException("轮询正常结束！");
            }
            Thread.sleep(remainMillis);
        } else {
            if (isMasterLeft()) {
                throw new PollingCancelException("轮询正常结束！");
            }
            Thread.sleep(millis);
        }
    }

    private boolean isMasterLeft() {
        if (pollingScheme.getIsBroadcast() != YesOrNo.YES) {
            if (conferenceContext.getMasterAttendee() == null || !conferenceContext.getMasterAttendee().isMeetingJoined()) {
                DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
                defaultAttendeeOperation.setDefaultViewIsBroadcast(YesOrNo.YES.getValue());
                defaultAttendeeOperation.setDefaultViewIsFill(YesOrNo.YES.getValue());
                defaultAttendeeOperation.setDefaultViewIsDisplaySelf(2);
                defaultAttendeeOperation.setDefaultViewLayout(AllEqualSplitScreen.LAYOUT);
                defaultAttendeeOperation.setDefaultViewPollingInterval(5);
                defaultAttendeeOperation.initSplitScreen();
                conferenceContext.setDefaultViewOperation(defaultAttendeeOperation);
                AttendeeOperation old = conferenceContext.getAttendeeOperation();
                conferenceContext.setLastAttendeeOperation(old);
                old.cancel(defaultAttendeeOperation);
                conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
                return true;
            }
        }
        return false;
    }
}
