/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai 
 * @since 2021-04-25 14:18
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.operation;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.attendee.model.busiprocessor.MixingAttendeeProcessor;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

/**  
 * <pre>讨论操作 TODO </pre>
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version V1.0  
 */
public class DiscussAttendeeOperation extends AttendeeOperation
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-04-25 14:19 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-25 14:19 
     * @param conferenceContext 
     */
    public DiscussAttendeeOperation(ConferenceContext conferenceContext)
    {
        super(conferenceContext);
    }

    @Override
    public void operate()
    {
        // 全部设为自动分屏
        BeanFactory.getBean(IAttendeeService.class).updateAttendeeImportance(conferenceContext, AttendeeImportance.COMMON);
        
        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        CoSpace cospace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
        
        BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, cospace, new CoSpaceParamBuilder().panePlacementHighestImportance());
        
        // 全体开麦克风
        BeanFactory.getBean(IAttendeeService.class).openMixing(conferenceContext);
        
        conferenceContext.setDiscuss(true);
        
        // 发送提示信息
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());

        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

    @Override
    public void cancel()
    {
        if (!conferenceContext.isEnd())
        {
            conferenceContext.setDiscuss(false);
            
            // 全体关闭麦克风
            BeanFactory.getBean(IAttendeeService.class).closeMixing(conferenceContext, conferenceContext.getMasterAttendee());
            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().isMeetingJoined())
            {
                // 还原主会场权重
                BeanFactory.getBean(IAttendeeService.class).updateAttendeeImportance(conferenceContext, conferenceContext.getMasterAttendee(), AttendeeImportance.MASTER);
                new Thread(()->{
                    ThreadUtils.sleep(20);
                    new MixingAttendeeProcessor(conferenceContext.getConferenceNumber(), conferenceContext.getMasterAttendee().getId(), false).process();
                }).start();
            }
            
            // 发送提示信息
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已退出讨论模式！");
            WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
        }
    }
    
}
