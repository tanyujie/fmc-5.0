/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.attendee.model.operation;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.utils.FmeAttendeeUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.ParticipantBulkOperationMode;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;

/**
 * <pre>单个参会者轮询</pre>
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version V1.0
 */
public class SingleAttendeePollingOperation extends AttendeeOperation
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;

    private CountDownLatch latch;
    private ExecutorService executorService;
    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-22 13:38
     * @param conferenceContext
     * @param splitScreen
     * @param attendees
     * @param latch
     */
    public SingleAttendeePollingOperation(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees, CountDownLatch latch,ExecutorService executorService)
    {
        super(conferenceContext, splitScreen, attendees);
        this.latch = latch;
        this.executorService=executorService;
    }

    @Override
    public void operate()
    {
        clearOperatedAttendees();
        splitScreen.processImportance(attendees, (Attendee attendee0, CellScreen cellScreen0) -> {
            final Attendee attendee = attendee0;
            final CellScreen cellScreen = cellScreen0;
//            executorService.execute(() -> {
//                try
//                {
//                    processChooseSee(attendee, cellScreen.getImportance());
//                }catch(Throwable e){
//                    logger.error("SingleAttendeePollingOperation processChooseSee error", e);
//                }
//                finally
//                {
//                    latch.countDown();
//                }
//            });
            try
            {
                processChooseSee(attendee, cellScreen.getImportance());
            }catch(Throwable e){
                logger.error("SingleAttendeePollingOperation processChooseSee error", e);
            }
            finally
            {
                latch.countDown();
            }
        });
    }

    @Override
    public void cancel(AttendeeOperation attendeeCurrentOperation)
    {
        super.cancel(attendeeCurrentOperation);
        if (!attendees.isEmpty())
        {
            CountDownLatch latch = new CountDownLatch(attendees.size());
            for (Attendee attendee : attendees)
            {
                FcmThreadPool.exec(() -> {
                    try
                    {
                        doCancel(attendee, attendeeCurrentOperation);
                    }
                    catch (Throwable e)
                    {
                        logger.error("SingleAttendeePollingOperation.cancel error", e);
                    }
                    finally
                    {
                        latch.countDown();
                    }
                });
            }

            if (latch.getCount() > 0)
            {
                try
                {
                    latch.await();
                }
                catch (InterruptedException e)
                {
                    logger.error("SingleAttendeePollingOperation.cancel error", e);
                }
            }
        }
    }

    /**
     * <pre>取消选看</pre>
     * @author lilinhai
     * @since 2021-02-22 17:19
     * @param attendee void
     * @param attendeeCurrentOperation
     */
    private void doCancel(Attendee attendee, AttendeeOperation attendeeCurrentOperation)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (attendeeCurrentOperation == null || attendeeCurrentOperation.getAttendees().get(0).getDeptId() != fmeAttendee.getCascadeDeptId()
                    && (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId()))
            {
                if (fmeAttendee.isMeetingJoined())
                {
                    if (!AttendeeImportance.COMMON.is(fmeAttendee.getImportance()))
                    {
                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                        RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                                .importance(AttendeeImportance.COMMON.getStartValue())
                                .build());
                        if (!restResponse.isSuccess())
                        {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("默认视图取消FME参会者【" + attendee.getName() + " - " + fmeAttendee.getImportance() + "】权重失败：" + restResponse.getMessage());
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                        }
                    }
                }
            }
        });
    }

    /**
     * <pre>处理选看</pre>
     * @author lilinhai
     * @since 2021-02-22 15:11
     * @param attendee
     * @param importance void
     * @param result
     */
    private void processChooseSee(Attendee attendee, int importance)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());

        if (attendee.isMeetingJoined())
        {
            FmeBridge subFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);

            // 将级联参会者设为子会议选看
            RestResponse restResponse0 = subFmeBridge.getParticipantInvoker().bulkUpdateParticipant(attendee.getCallId()
                    , new ParticipantParamBuilder().importance(importance).build()
                    , ParticipantBulkOperationMode.SELECTED, attendee.getParticipantUuid());
            if(conferenceContext.getMasterAttendee()!=null){
                if(Objects.equals(conferenceContext.getMasterAttendee().getParticipantUuid(),attendee.getParticipantUuid())){
                    if(importance==AttendeeImportance.ROUND_BROADCAST.getEndValue()){
                        if (subFmeBridge != null)
                        {
                            subFmeBridge.getParticipantInvoker().updateParticipant(conferenceContext.getMasterAttendee().getParticipantUuid(), new ParticipantParamBuilder()
                                    .importance(AttendeeImportance.ROUND_BROADCAST.getEndValue())
                                    .build());
                        }
                    }

                }
            }

            if (!restResponse0.isSuccess())
            {
                StringBuilder messageTip = new StringBuilder();
                messageTip.append("默认视图设置参会者【" + attendee.getName() + " - " + importance + "】权重失败：" + restResponse0.getMessage());
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
            }
            addOperatedAttendee(attendee);
        }

        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId())
            {
                if (!AttendeeImportance.ROUND.is(fmeAttendee.getImportance()))
                {
                    if (fmeAttendee.isMeetingJoined())
                    {
                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                        RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                                .importance(AttendeeImportance.ROUND.getStartValue())
                                .build());
                        if (!restResponse.isSuccess())
                        {
                            StringBuilder messageTip = new StringBuilder();
                            messageTip.append("默认视图设置FME参会者【" + attendee.getName() + " - " + importance + "】权重失败：" + restResponse.getMessage());
                            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, messageTip);
                        }
                    }
                }
            }
        });
    }
}
