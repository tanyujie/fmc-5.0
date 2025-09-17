/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateProcessorAddMessage.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.roster.message
 * @author sinhy 
 * @since 2021-09-07 23:45
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callroster.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeFieldService;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.ParticipantInfo;
import com.sinhy.spring.BeanFactory;

public class RosterProcessorAddMessage extends RosterProcessorMessage
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:46 
     * @param fmeBridge
     * @param updateItem 
     */
    public RosterProcessorAddMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        new ParticipantServiceNewSynchronizer(fmeBridge, updateItem, (fb, p) -> {
            if (p.is(ParticipantState.CONNECTED) || p.is(ParticipantState.DISCONNECT))
            {
                obtainCallLeg(p, fb);
                
                // 更新会议上下文信息，并发送到前端显示
                BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(fb, p);
                ParticipantMessageQueue.getInstance().put(new ParticipantInfo(p, fb));
            }
        }).sync();
    }
    
}
