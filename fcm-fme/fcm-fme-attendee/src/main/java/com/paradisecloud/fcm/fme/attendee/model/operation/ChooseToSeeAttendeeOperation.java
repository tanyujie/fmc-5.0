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

import java.util.HashMap;
import java.util.List;

import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MixingAttendeeProcessor;
import org.springframework.util.Assert;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.model.core.ConferenceAttendeeImportanceMonitor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.utils.FmeAttendeeUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.exception.SystemException;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>选看与会者操作（主会场看选看者，分会场看主会场）</pre>
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version V1.0  
 */
public class ChooseToSeeAttendeeOperation extends AttendeeOperationAdapter
{
    
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 12:48 
     */
    private static final long serialVersionUID = 1L;

    private AttendeeImportance attendeeImportance;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:38 
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    public ChooseToSeeAttendeeOperation(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees)
    {
        super(conferenceContext, splitScreen, attendees);
    }

    @Override
    public void operate()
    {
        attendeeImportance = AttendeeImportance.CHOOSE_SEE;
        clearOperatedAttendees();
        splitScreen.processImportance(attendees, (Attendee attendee, CellScreen cellScreen) -> {
            Assert.isTrue(attendee.isMeetingJoined(), "不能选看非入会终端");
            if (isUpCascadeRollCall()) {
                attendee.setUpCascadeRollCall(true);
            }else {
                attendee.setUpCascadeRollCall(false);
            }
            if (isUpCascadePolling()) {
                if (isUpCascadeBroadcast()) {
                    attendee.setUpCascadeBroadcast(true);
                }
            }
            if (!isUpCascadeBroadcast()) {
                attendee.setUpCascadeBroadcast(false);
            }
            processChooseSee(attendee, cellScreen.getImportance());
        });
        
        initOneSplitScreen();
    }

    /**
     * <pre>取消选看</pre>
     * @author lilinhai
     * @since 2021-02-22 17:19 
     * @param attendee void
     */
    private void doCancel(Attendee attendee)
    {
        if (attendee.isUpCascadeRollCall() || attendee.isUpCascadeBroadcast()) {
            boolean isCurrentContain = conferenceContext.getAttendeeOperation().getAttendees().contains(attendee);
            if (!isCurrentContain || (isCurrentContain && !isUpCascadeRollCall() && !isUpCascadeBroadcast())) {
                attendee.setUpCascadeRollCall(false);
                attendee.setUpCascadeBroadcast(false);
                AttendeeImportance attendeeImportance = AttendeeImportance.convert(attendee.getImportance());
                if (attendeeImportance != null)
                {
                    try
                    {
                        attendee.resetUpdateMap();
                        attendeeImportance.processAttendeeWebsocketMessage(attendee);
                        if (attendee.getUpdateMap().size() > 1) {
                            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
                        }
                    }
                    catch (Throwable e)
                    {
                        logger.error("权重消息处理出错processAttendeeWebsocketMessage", e);
                    }
                }
                // 关闭混音
                new MixingAttendeeProcessor(attendee, true).process();
            }
        }
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (getAttendees().get(0).getDeptId() != fmeAttendee.getCascadeDeptId()
                    && (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId()))
            {
                if (!AttendeeImportance.COMMON.is(fmeAttendee.getImportance()))
                {
                    if (fmeAttendee.isMeetingJoined())
                    {
                        ThreadUtils.sleep(50);
                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                        RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                                .importance(AttendeeImportance.COMMON.getStartValue())
                                .build());
                        if (!restResponse.isSuccess())
                        {
                            throw new SystemException(1003242, "将FME终端还原为主体会议普通参会者失败：" + restResponse.getMessage());
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
     */
    private void processChooseSee(Attendee attendee, int importance)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        if (attendee == mainConferenceContext.getMasterAttendee())
        {
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "主会场不能被选看！");
            return;
        }
        
        FmeBridge subFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
        
        // 将级联参会者设为子会议选看
        if ((attendee.getImportance() != null && attendee.getImportance().intValue() != importance) || attendee.getImportance() == null)
        {
            RestResponse restResponse0 = subFmeBridge.getParticipantInvoker().updateParticipant(attendee.getParticipantUuid(), new ParticipantParamBuilder()
                    .importance(importance)
                    .build());
        
            if (!restResponse0.isSuccess())
            {
                throw new SystemException(1003242, "将参会者设为子会议选看失败：" + restResponse0.getMessage());
            }
        }
        
        addOperatedAttendee(attendee);
        
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId())
            {
                if (!attendeeImportance.is(fmeAttendee.getImportance()))
                {
                    if (fmeAttendee.isMeetingJoined())
                    {
                        ThreadUtils.sleep(50);
                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                        RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                                .importance(attendeeImportance.getEndValue())
                                .build());
                        if (!restResponse.isSuccess())
                        {
                            throw new SystemException(1003242, "将FME终端设为主体会议选看者失败: " + restResponse.getMessage());
                        }
                    }
                }
            }
        });
        try
        {
            attendee.resetUpdateMap();
            attendeeImportance.processAttendeeWebsocketMessage(attendee);
            if (attendee.getUpdateMap().size() > 1) {
                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(attendee.getUpdateMap()));
            }
        }
        catch (Throwable e)
        {
            logger.error("权重消息处理出错processAttendeeWebsocketMessage", e);
        }
        
        // 取消上次相关操作
        for (Attendee lastOperationAttendee : conferenceContext.getLastAttendeeOperation().getOperatedAttendees())
        {
            if (!lastOperationAttendee.getId().equals(attendee.getId())) {
                try {
                    doCancel(lastOperationAttendee);
                } catch (Throwable e) {
                    logger.error("processChooseSee.cancel error", e);
                }
            }
        }
        
        if (isPushOperatedAttendeesToMonitor())
        {
            ConferenceAttendeeImportanceMonitor.getInstance().put(mainConferenceContext);
        }

        if (attendee.isUpCascadeRollCall()) {
            // 开启混音
            new MixingAttendeeProcessor(attendee, false).process();
        }
    }
}
