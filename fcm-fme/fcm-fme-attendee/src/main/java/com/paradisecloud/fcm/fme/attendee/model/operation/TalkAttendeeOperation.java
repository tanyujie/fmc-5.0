/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TalkAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author sinhy 
 * @since 2021-12-01 10:16
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.InheritSplitScreen;
import org.springframework.util.Assert;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MixingAttendeeProcessor;
import com.paradisecloud.fcm.fme.attendee.model.core.ConferenceAttendeeImportanceMonitor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.attendee.utils.FmeAttendeeUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.CellScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.SplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.AutomaticSplitScreen;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

/**
 * 参会者对话操作
 * @author sinhy
 * @since 2021-12-01 10:16
 * @version V1.0  
 */
public class TalkAttendeeOperation extends AttendeeOperationAdapter
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-01 10:16 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-12-01 10:22 
     * @param conferenceContext
     * @param splitScreen
     * @param attendees 
     */
    public TalkAttendeeOperation(ConferenceContext conferenceContext, SplitScreen splitScreen, List<Attendee> attendees)
    {
        super(conferenceContext, splitScreen, attendees);
    }
    
    @Override
    public void operate()
    {
        clearOperatedAttendees();
        
        // 设置会场布局为自动布局
        this.initSplitScreen(AutomaticSplitScreen.LAYOUT);
        // 取消上次相关操作
        BeanFactory.getBean(IAttendeeService.class).updateAttendeeImportance(conferenceContext, AttendeeImportance.COMMON, conferenceContext.getMasterAttendee());
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
        splitScreen.processImportance(attendees, (Attendee attendee, CellScreen cellScreen) -> {
            Assert.isTrue(attendee.isMeetingJoined(), "不能对话非入会终端");
            
            if (attendee == conferenceContext.getMasterAttendee())
            {
                logger.error("主会场不能和自己对话");
                return;
            }
            processTalk(attendee, cellScreen.getImportance());
            
            // 设置为指定分屏
//            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(attendee, splitScreen.getLayout());
        });
        
        // 设置主会场为指定单分屏布局
//        BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, splitScreen.getLayout(), AttendeeLayoutSetMode.MASTER);
        

        
        if (isPushOperatedAttendeesToMonitor())
        {
            ConferenceAttendeeImportanceMonitor.getInstance().put(conferenceContext);
        }
        
        // 取消除当前对话者外的其他已开麦参会者的混音状态
        FcmThreadPool.exec(() -> {
            cancelMix();
        });
    }
    
    @Override
    public void cancel()
    {
        for (Attendee attendee : getOperatedAttendees())
        {
            if (attendee != conferenceContext.getMasterAttendee())
            {
                new MixingAttendeeProcessor(attendee, true).process();
            }
        }
    }
    
    /**
     * <pre>处理对话</pre>
     * @author lilinhai
     * @since 2021-02-22 15:11 
     * @param attendee
     * @param importance void
     */
    private void processTalk(Attendee attendee, int importance)
    {
        ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(attendee.getContextKey());
        
        FmeBridge subFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(attendee);
        
        // 将级联参会者设为子会议选看
        if ((attendee.getImportance() != null && attendee.getImportance().intValue() != importance) || attendee.getImportance() == null)
        {
            RestResponse restResponse0 = subFmeBridge.getParticipantInvoker().updateParticipant(attendee.getParticipantUuid(), new ParticipantParamBuilder()
                    .importance(importance)
                    .build());
            if (!restResponse0.isSuccess())
            {
                throw new SystemException(1003242, "将参会者设为对话失败1: " + restResponse0.getMessage());
            }
        }
        
        // 将级联参会者设为子会议选看
        if ((subConferenceContext.getMasterAttendee().getImportance() != null && subConferenceContext.getMasterAttendee().getImportance().intValue() != importance) || subConferenceContext.getMasterAttendee().getImportance() == null)
        {
            RestResponse restResponse0 = subFmeBridge.getParticipantInvoker().updateParticipant(subConferenceContext.getMasterAttendee().getParticipantUuid(), new ParticipantParamBuilder()
                    .importance(importance)
                    .build());
            if (!restResponse0.isSuccess())
            {
                throw new SystemException(1003242, "将主会场设为对话失败1: " + restResponse0.getMessage());
            }
        }
        
        addOperatedAttendee(attendee);
        addOperatedAttendee(subConferenceContext.getMasterAttendee());
        
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        FmeAttendeeUtils.processFmeAttendee(subConferenceContext, conferenceContext, (fmeAttendee) -> {
            if (mainConferenceContext.getMasterAttendee() == null || mainConferenceContext.getMasterAttendee().getDeptId() != fmeAttendee.getCascadeDeptId())
            {
                if (!AttendeeImportance.CHOOSE_SEE.is(fmeAttendee.getImportance()))
                {
                    ThreadUtils.sleep(10);
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                    RestResponse restResponse = fmeBridge.getParticipantInvoker().updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder()
                            .importance(AttendeeImportance.CHOOSE_SEE.getStartValue())
                            .build());
                    if (!restResponse.isSuccess())
                    {
                        throw new SystemException(1003242, "将FME终端设为主体会议点名者失败: " + restResponse.getMessage());
                    }
                }
            }
        });
        
        
        // 开启混音
        new MixingAttendeeProcessor(attendee, false).process();
        
        // 开启混音
        new MixingAttendeeProcessor(subConferenceContext.getMasterAttendee(), false).process();
    }
    
    /**
     * <pre>取消对话</pre>
     * @author lilinhai
     * @since 2021-02-22 17:19 
     * @param attendee void
     */
    private void doCancel(Attendee attendee)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        if(!Objects.equals(mainConferenceContext.getMasterAttendee().getId(),attendee.getId())){
//            BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(attendee,AutomaticSplitScreen.LAYOUT);
        }
        // 被对话方的上下文
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
                        throw new SystemException(1003242, "将对话参会终端还原为普通参权重失败：" + restResponse.getMessage());
                    }
                }
            }
        });
    }
    
    private void cancelMix()
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        List<Attendee> subAttendees = new ArrayList<Attendee>(attendees);
        subAttendees.add(conferenceContext.getMasterAttendee());
        BeanFactory.getBean(IAttendeeService.class).closeMixing(mainConferenceContext, subAttendees.toArray(new Attendee[0]));
    }
}
