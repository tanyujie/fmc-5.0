/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ICallegGetterService.java
 * Package     : com.paradisecloud.fcm.fme.attendee.interfaces
 * @author sinhy 
 * @since 2021-09-02 15:16
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>CallLeg获取器</pre>
 * @author sinhy
 * @since 2021-09-02 15:16
 * @version V1.0  
 */
public interface ICallegService
{
    CallLeg getCallLegByParticipantUuid(FmeBridge fmeBridge, Participant participant);
    
    /**
     * 比较布局是否正确
     * @author sinhy
     * @since 2021-08-31 22:46 
     * @param coSpace
     * @param layout
     * @param attendee
     * @return boolean
     */
    CallLeg getCallLeg(Attendee attendee);
}
