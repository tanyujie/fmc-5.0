/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : MixingAttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.busiprocessor
 * @author lilinhai 
 * @since 2021-02-23 15:22
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import java.util.HashMap;

import com.paradisecloud.fcm.common.enumer.RaiseHandStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.sinhy.spring.BeanFactory;

/**  
 * <pre>混音处理器</pre>
 * @author lilinhai
 * @since 2021-02-23 15:22
 * @version V1.0  
 */
public class RaiseHandProcessor extends AttendeeBusiProcessor
{
    
    /**
     * 混音参数
     */
    private RaiseHandStatus raiseHandStatus;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:00 
     * @param contextKey
     * @param raiseHandStatus 
     */
    public RaiseHandProcessor(String contextKey, String attendeeId, RaiseHandStatus raiseHandStatus)
    {
        super(contextKey, attendeeId);
        this.raiseHandStatus = raiseHandStatus;
    }

    @Override
    public void process()
    {
        if (targetAttendee.isMeetingJoined())
        {
            synchronized (targetAttendee)
            {
                targetAttendee.resetUpdateMap();
                targetAttendee.setRaiseHandStatus(raiseHandStatus.getValue());
                if (targetAttendee.getUpdateMap().size() > 1)
                {
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, new HashMap<>(targetAttendee.getUpdateMap()));
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                    
                    StringBuilder messageTip = new StringBuilder();
                    messageTip.append("【").append(targetAttendee.getName()).append("】").append(raiseHandStatus.getName());
                    
                    // 消息和参会者信息同步到主级会议
                    WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                }
            }
        }
    }
    
}
