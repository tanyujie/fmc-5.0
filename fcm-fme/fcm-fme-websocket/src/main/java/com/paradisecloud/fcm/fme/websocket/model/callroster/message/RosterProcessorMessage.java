/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterUpdateProcessorMessage.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core.roster
 * @author sinhy 
 * @since 2021-09-07 23:35
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callroster.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallegService;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;
import com.paradisecloud.fcm.fme.websocket.model.processormessage.BusiProcessorMessage;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

public abstract class RosterProcessorMessage extends BusiProcessorMessage
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:38 
     * @param rosterUpdate
     * @param fmeBridge 
     */
    protected RosterProcessorMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem, updateItem.getString("participant"));
    }
    
    protected void obtainCallLeg(Participant p, FmeBridge fmeBridge)
    {
        int t = 1;
        int c = 0;
        while (p.getCallLeg() == null && p.is(ParticipantState.CONNECTED) && c < t)
        {
            // 获取CallLeg
            CallLeg callLeg = BeanFactory.getBean(ICallegService.class).getCallLegByParticipantUuid(fmeBridge, p);
            
            // 设置与会者开关麦信息
            p.setCallLeg(callLeg);
            c++;
            if (c < t)
            {
                ThreadUtils.sleep(50); 
            }
        }
    }
}
