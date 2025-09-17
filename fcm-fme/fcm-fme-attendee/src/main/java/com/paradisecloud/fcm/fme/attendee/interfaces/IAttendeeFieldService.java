/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IAttendeeFieldDataUpdateService.java
 * Package     : com.paradisecloud.fcm.fme.attendee.interfaces
 * @author sinhy 
 * @since 2021-09-08 15:43
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.interfaces;

import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.participant.Participant;

/**  
 * <pre>参会者业务字段数据更新服务</pre>
 * @author sinhy
 * @since 2021-09-08 15:43
 * @version V1.0  
 */
public interface IAttendeeFieldService
{
    
    void updateByParticipant(FmeBridge fmeBridge, Participant participant);
    void updateByParticipant(FmeBridge fmeBridge, ConferenceContext conferenceContext, Participant participant, Attendee a);
}
