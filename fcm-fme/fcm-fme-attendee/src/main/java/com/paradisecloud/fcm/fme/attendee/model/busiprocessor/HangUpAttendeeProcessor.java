/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : HangUpAttendeeProcessor.java
 * Package     : com.paradisecloud.fcm.fme.service.model.attendeeprocessor
 * @author lilinhai 
 * @since 2021-02-09 11:27
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.UpFmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.cascade.UpCascade;
import com.sinhy.exception.SystemException;

/**  
 * <pre>与会者挂断处理器</pre>
 * @author lilinhai
 * @since 2021-02-09 11:27
 * @version V1.0  
 */
public class HangUpAttendeeProcessor extends AttendeeBusiProcessor
{
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:27 
     * @param conferenceNumber
     * @param attendeeId 
     */
    public HangUpAttendeeProcessor(String conferenceNumber, String attendeeId)
    {
        super(conferenceNumber, attendeeId);
    }

    @Override
    public void process()
    {
        if (targetAttendee == null)
        {
            throw new SystemException(1002342, "找不到与会者：" + attendeeId);
        }
        
        if (targetAttendee.isMeetingJoined())
        {
            RestResponse rr = fmeBridge.getParticipantInvoker().deleteParticipant(targetAttendee.getParticipantUuid());
            if (!rr.isSuccess())
            {
                throw new SystemException(1002345, "挂断失败：" + rr.getMessage());
            }
            targetAttendee.setHangUp(true);
            targetAttendee.setParticipantUuid(null);
            
            // 需要将对应的UpFmeAttendee一起挂断
            if (targetAttendee instanceof FmeAttendee)
            {
                ConferenceContext subConferenceContext = ConferenceContextCache.getInstance().get(((FmeAttendee) targetAttendee).getContextKey());
                UpCascade upCascade = subConferenceContext.getUpCascade();
                if (upCascade != null)
                {
                    UpFmeAttendee upFmeAttendee = upCascade.get(conferenceContext.getConferenceNumber());
                    if (upFmeAttendee != null)
                    {
                        try
                        {
                            FmeBridge subConferenceContextFmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(upFmeAttendee);
                            rr = subConferenceContextFmeBridge.getParticipantInvoker().deleteParticipant(upFmeAttendee.getParticipantUuid());
                            if (!rr.isSuccess())
                            {
                                throw new SystemException(1002345, "挂断失败：" + rr.getMessage());
                            }
                        }
                        catch (Throwable e)
                        {
                        }
                        finally 
                        {
                            upFmeAttendee.setHangUp(true);
                        }
                    }
                }
            }
            WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + targetAttendee.getName() + "】挂断请求已发起");
        }
    }
    
}
