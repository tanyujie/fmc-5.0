/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : ChangeMasterAttendeeProcessor.java
 * Package : com.paradisecloud.fcm.fme.service.model.attendeeprocessor
 * 
 * @author lilinhai
 * 
 * @since 2021-02-09 11:33
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import java.util.HashMap;
import java.util.Map;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.ConferenceOpsModeEnum;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeLayoutService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeLayoutSetMode;
import com.paradisecloud.fcm.fme.attendee.utils.FmeAttendeeUtils;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.InheritSplitScreen;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.OneSplitScreen;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.exception.SystemException;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>变更主会场处理器</pre>
 * 
 * @author lilinhai
 * @since 2021-02-09 11:33
 * @version V1.0
 */
@Slf4j
public class MasterChangeAttendeeProcessor extends AttendeeBusiProcessor
{
    
    /**
     * 老主会场
     */
    private Attendee oldMasterAttendee;
    
    private ConferenceContext mainConferenceContext;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2021-02-09 11:33
     * @param contextKey
     * @param attendeeId
     */
    public MasterChangeAttendeeProcessor(String contextKey, String attendeeId)
    {
        super(contextKey, attendeeId);
        
        // 老主会场
        mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(conferenceContext);
        oldMasterAttendee = mainConferenceContext.getMasterAttendee();
    }
    
    @Override
    public void process()
    {
        if (targetAttendee == oldMasterAttendee)
        {
            throw new SystemException(1003241, "当前主会场已是目标参会者，无需重复变更！");
        }
        
        if (targetAttendee.isMeetingJoined())
        {
            RestResponse restResponse0 = fmeBridge.getParticipantInvoker()
                    .updateParticipant(targetAttendee.getParticipantUuid(), new ParticipantParamBuilder().importance(AttendeeImportance.MASTER.getStartValue()).build());
            if (!restResponse0.isSuccess())
            {
                throw new SystemException(1003242, "将目标参会者设置为主会场失败: " + restResponse0.getMessage());
            }
            
            new MixingAttendeeProcessor(targetAttendee, false).process();
        }
        
        FmeAttendeeUtils.processUpFmeAttendee(conferenceContext, mainConferenceContext, (mainFmeAttendee) -> {
            if (mainFmeAttendee.isMeetingJoined())
            {
                ThreadUtils.sleep(50);
                FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(mainFmeAttendee);
                RestResponse restResponse = fmeBridge.getParticipantInvoker()
                        .updateParticipant(mainFmeAttendee.getParticipantUuid(), new ParticipantParamBuilder().importance(AttendeeImportance.UP_FME_WHILE_MASTER_IS_SUB.getStartValue()).build());
                if (!restResponse.isSuccess())
                {
                    throw new SystemException(1003242, "恢复主FME在下级会议的参会者权重失败: " + restResponse.getMessage());
                }
            }
        });
        
        FmeAttendeeUtils.processFmeAttendee(conferenceContext, mainConferenceContext, (fmeAttendee) -> {
            if (fmeAttendee.isMeetingJoined())
            {
                ThreadUtils.sleep(50);
                FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                RestResponse restResponse = fmeBridge.getParticipantInvoker()
                        .updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder().importance(AttendeeImportance.MASTER.getStartValue()).build());
                if (!restResponse.isSuccess())
                {
                    throw new SystemException(1003242, "将级联参会者的FME终端设为主体会议主会场失败: " + restResponse.getMessage());
                }
            }
        });
        
        restoreOldMasterImportance();
        
        CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
        
        // 设置窗格最高权重值
        coSpaceParamBuilder.panePlacementHighestImportance();
        
        coSpaceParamBuilder.panePlacementSelfPaneMode(PanePlacementSelfPaneMode.OFF.getStringValue());
        
        // 广播设置为指定分屏，否则设置为单分屏(2021-08-12)
        coSpaceParamBuilder.defaultLayout(OneSplitScreen.LAYOUT);
        
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
        
        // 更新CoSpace缓存
        BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, coSpace, coSpaceParamBuilder);
        BeanFactory.getBean(IAttendeeLayoutService.class).setAttendeeLayout(conferenceContext, InheritSplitScreen.LAYOUT, AttendeeLayoutSetMode.ALL);
        if (!targetAttendee.isMeetingJoined())
        {
            mainConferenceContext.setMasterAttendee(targetAttendee);
            
            Map<String, Object> data = new HashMap<>();
            data.put("oldMasterAttendee", oldMasterAttendee);
            data.put("newMasterAttendee", targetAttendee);
            WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(mainConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
            
            StringBuilder messageTip = new StringBuilder();
            messageTip.append("主会场已切换至【").append(targetAttendee.getName()).append("】");
            WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
            
            if (mainConferenceContext.getAttendeeOperation() != null)
            {
                mainConferenceContext.getAttendeeOperation().cancel(null);

            }
        }
        else
        {
            mainConferenceContext.setMasterAttendee(targetAttendee);
            // 全部改为单分屏，然后设置默认选看：2021-08-13
            BeanFactory.getBean(IAttendeeService.class).defaultChooseSee(mainConferenceContext);
        }
        logger.info("模式更改");
        conferenceContext.setConferenceMode(ConferenceOpsModeEnum.DIRECT.name());
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_MODEL, ConferenceOpsModeEnum.DIRECT.name());
        logger.info("模式更改为："+conferenceContext.getConferenceMode());
    }
    
    /**
     * <pre>还原老主会场的权重值</pre>
     * 
     * @author lilinhai
     * @since 2021-02-19 13:49 void
     */
    private void restoreOldMasterImportance()
    {
        if (oldMasterAttendee == null)
        {
            return;
        }
        
        // 获取老的主会场的会议上下文
        ConferenceContext oldMasterConferenceContext = ConferenceContextCache.getInstance().get(oldMasterAttendee.getContextKey());
        if (oldMasterConferenceContext != null)
        {
            // 主FME在下级会议的参会者
            FmeAttendeeUtils.processUpFmeAttendee(oldMasterConferenceContext, mainConferenceContext, (upFmeAttendee) -> {
                if (upFmeAttendee.isMeetingJoined())
                {
                    if (targetAttendee.getDeptId() != oldMasterAttendee.getDeptId())
                    {
                        ThreadUtils.sleep(50);
                        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(upFmeAttendee);
                        RestResponse restResponse = fmeBridge.getParticipantInvoker()
                                .updateParticipant(upFmeAttendee.getParticipantUuid(), new ParticipantParamBuilder().importance(AttendeeImportance.UP_FME.getStartValue()).build());
                        if (!restResponse.isSuccess())
                        {
                            throw new SystemException(1003242, "恢复主FME在下级会议的参会者权重失败: " + restResponse.getMessage());
                        }
                    }
                }
            });
            
            FmeAttendeeUtils.processFmeAttendee(oldMasterConferenceContext, mainConferenceContext, (fmeAttendee) -> {
                if (fmeAttendee.isMeetingJoined() && targetAttendee.getDeptId() != fmeAttendee.getCascadeDeptId())
                {
                    ThreadUtils.sleep(50);
                    FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(fmeAttendee);
                    RestResponse restResponse = fmeBridge.getParticipantInvoker()
                            .updateParticipant(fmeAttendee.getParticipantUuid(), new ParticipantParamBuilder().importance(AttendeeImportance.COMMON.getStartValue()).build());
                    if (!restResponse.isSuccess())
                    {
                        throw new SystemException(1003242, "将上个子会议的主会场FME终端设置普通会场失败: " + restResponse.getMessage());
                    }
                }
            });
            
            // 老主会场所属的会议桥
            if (oldMasterAttendee.isMeetingJoined())
            {
                ThreadUtils.sleep(50);
                FmeBridge oldMasterFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(oldMasterAttendee);
                RestResponse restResponse = oldMasterFmeBridge.getParticipantInvoker()
                        .updateParticipant(oldMasterAttendee.getParticipantUuid(), new ParticipantParamBuilder().importance(AttendeeImportance.COMMON.getStartValue()).build());
                
                if (!restResponse.isSuccess())
                {
                    throw new SystemException(1003242, "将上个级联参会者主会场还原为子会议普通会场失败: " + restResponse.getMessage());
                }
                
                new MixingAttendeeProcessor(oldMasterAttendee, true).process();
            }
        }
    }
    
}
