/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RecallProcessor.java
 * Package     : com.paradisecloud.fcm.fme.service.model.attendeeprocessor
 * @author lilinhai 
 * @since 2021-02-09 11:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.system.model.SysDeptCache;
import com.sinhy.exception.SystemException;

/**  
 * <pre>参会者重呼处理器</pre>
 * @author lilinhai
 * @since 2021-02-09 11:00
 * @version V1.0  
 */
public class RecallAttendeeProcessor extends CallAttendeeProcessor
{

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:00 
     * @param contextKey
     * @param attendeeId 
     */
    public RecallAttendeeProcessor(String contextKey, String attendeeId)
    {
        super(contextKey, attendeeId);
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
            return;
        }
        
        if (targetAttendee instanceof FmeAttendee)
        {
            FmeAttendee fa = (FmeAttendee) targetAttendee;
            ConferenceContext cc = ConferenceContextCache.getInstance().get(fa.getContextKey());
            if (cc == null)
            {
                throw new SystemException(1009898, "【"+ SysDeptCache.getInstance().get(fa.getCascadeDeptId()).getDeptName() + "】会议已结束，无法呼叫");
            }
        }
        
        // 还原主动挂断为false
        targetAttendee.setHangUp(false);
        
        // 清空与会者UUID
        targetAttendee.setParticipantUuid(null);
        super.process();
    }
    
}
