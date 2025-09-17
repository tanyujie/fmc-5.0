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
import com.paradisecloud.fcm.fme.attendee.interfaces.IAttendeeService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.sinhy.spring.BeanFactory;

public class RosterProcessorUnknowMessage extends RosterProcessorMessage
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:46 
     * @param fmeBridge
     */
    public RosterProcessorUnknowMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        fmeBridge.getFmeLogger().logWebsocketInfo("rosterUpdate对象无法识别的updateType属性: " + updateItem.getString("updateType"), true);
        Participant p = fmeBridge.getDataCache().deleteParticipantByUuid(itemId);
        if (p != null)
        {
            p.getStatus().setState(ParticipantState.DISCONNECT.getValue());
            BeanFactory.getBean(IAttendeeFieldService.class).updateByParticipant(fmeBridge, p);
        }
        else
        {
            BeanFactory.getBean(IAttendeeService.class).callAttendeeFailedNotice(itemId, "可能是终端信令还未彻底断开，请过一会再试！");
        }
    }
    
}
