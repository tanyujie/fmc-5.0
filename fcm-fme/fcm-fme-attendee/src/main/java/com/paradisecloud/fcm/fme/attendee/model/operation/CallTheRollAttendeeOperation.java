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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MixingAttendeeProcessor;
import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeInfo;
import com.paradisecloud.fcm.fme.attendee.model.core.ConferenceAttendeeImportanceMonitor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.utils.FmeAttendeeUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>点名与会者操作</pre>
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version V1.0  
 */
public class CallTheRollAttendeeOperation extends AttendeeOperationAdapter
{
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-02-22 12:48 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-22 13:38 
     * @param conferenceContext
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    public CallTheRollAttendeeOperation(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees)
    {
        super(conferenceContext, splitScreen, attendees);
    }

    @Override
    public void operate()
    {
        clearOperatedAttendees();
        splitScreen.processImportance(attendees, (Attendee attendee, CellScreen cellScreen) -> {
            Assert.isTrue(attendee.isMeetingJoined(), "不能点名非入会终端");
            processCallTheRoll(attendee, cellScreen.getImportance());
        });
        
        this.initOneSplitScreen();
        
        // 确保主会场开启混音
        new MixingAttendeeProcessor(conferenceContext.getMasterAttendee(), false).process();
        
        // 取消除当前点名者外的其他已开麦参会者的混音状态
        FcmThreadPool.exec(()-> {
            cancelMix();
        });
    }
    
    public void cancel()
    {
        for (Attendee attendee : getOperatedAttendees())
        {
            new MixingAttendeeProcessor(attendee, true).process();
        }
    }

    /**
     * <pre>取消点名</pre>
     * @author lilinhai
     * @since 2021-02-22 17:19 
     * @param attendee void
     */
    private void doCancel(Attendee attendee)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        
        // 被点名方的上下文
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (getAttendees().get(0).getDeptId() != fmeAttendee.getCascadeDeptId()
                    && (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId()))
            {
                if (!AttendeeImportance.COMMON.is(fmeAttendee.getImportance()))
                {
                    ThreadUtils.sleep(10);
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                    RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                            .importance(AttendeeImportance.COMMON.getStartValue())
                            .build());
                    if (!restResponse.isSuccess())
                    {
                        throw new SystemException(1003242, "将FME终端还原为普通参权重失败：" + restResponse.getMessage());
                    }
                }
            }
        });
    }
    
    /**
     * <pre>处理点名</pre>
     * @author lilinhai
     * @since 2021-02-22 15:11 
     * @param attendee
     * @param importance void
     */
    private void processCallTheRoll(Attendee attendee, int importance)
    {
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        
        // 将级联参会者设为子会议选看
        if ((attendee.getImportance() != null && attendee.getImportance().intValue() != importance) || attendee.getImportance() == null)
        {
            FmeBridge subFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
            RestResponse restResponse0 = subFmeBridge.getParticipantInvoker().updateParticipant(attendee.getParticipantUuid(), new ParticipantParamBuilder()
                    .importance(importance)
                    .build());
            if (!restResponse0.isSuccess())
            {
                throw new SystemException(1003242, "将参会者设为子会议点名失败1: " + restResponse0.getMessage());
            }
        }
        
        addOperatedAttendee(attendee);
        
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId())
            {
                if (!AttendeeImportance.POINT.is(fmeAttendee.getImportance()))
                {
                    ThreadUtils.sleep(10);
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                    RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                            .importance(AttendeeImportance.POINT.getStartValue())
                            .build());
                    if (!restResponse.isSuccess())
                    {
                        throw new SystemException(1003242, "将FME终端设为主体会议点名者失败: " + restResponse.getMessage());
                    }
                }
            }
        });
        
        if (conferenceContext.getLastAttendeeOperation() instanceof CallTheRollAttendeeOperation)
        {
            // 如果当前点名是主会场，则需要选看上一个被点名者或选看者
            if (attendee == mainConferenceContext.getMasterAttendee())
            {
                AttendeeInfo ai = new AttendeeInfo(mainConferenceContext.getContextKey(), conferenceContext.getLastAttendeeOperation().getOperatedAttendees().get(0).getId());
                SplitScreen ss = new OneSplitScreen(AttendeeImportance.CHOOSE_SEE.getEndValue());
                AttendeeOperation attendeeOperation = new ChooseToSeeAttendeeOperation(mainConferenceContext, ss, Arrays.asList(ai.getAttendee()));
                
                // 不推送
                attendeeOperation.setPushOperatedAttendeesToMonitor(false);
                attendeeOperation.operate();
                
                addOperatedAttendee(ai.getAttendee());
            }
        }
        
        // 取消上次相关操作
        for (Attendee lastOperationAttendee : conferenceContext.getLastAttendeeOperation().getOperatedAttendees())
        {
            try
            {
                doCancel(lastOperationAttendee);
            }
            catch (Throwable e)
            {
                logger.error("CallTheRollAttendeeOperation.cancel error", e);
            }
        }
        
        if (isPushOperatedAttendeesToMonitor())
        {
            ConferenceAttendeeImportanceMonitor.getInstance().put(mainConferenceContext);
        }
        
        // 开启混音
        new MixingAttendeeProcessor(attendee, false).process();
    }
    
    private void cancelMix()
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        List<Attendee> subAttendees = new ArrayList<Attendee>(attendees);
        subAttendees.add(conferenceContext.getMasterAttendee());
        BeanFactory.getBean(IAttendeeService.class).closeMixing(mainConferenceContext, subAttendees.toArray(new Attendee[0]));
    }
}
