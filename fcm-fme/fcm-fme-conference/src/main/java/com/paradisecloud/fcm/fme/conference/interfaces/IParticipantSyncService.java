/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : IMcuNodeDataSyncService.java
 * Package     : com.paradisecloud.service.interfaces
 * @author lilinhai 
 * @since 2020-12-07 13:37
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.interfaces;

import com.paradisecloud.fcm.fme.attendee.model.core.ParticipantServiceNewSynchronizer.ParticipantNewCallbackProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.response.participant.ParticipantsResponse;

/**  
 * <pre>MCU节点数据同步服务</pre>
 * @author lilinhai
 * @since 2020-12-07 13:37
 * @version V1.0  
 */
public interface IParticipantSyncService
{
    
    /**
     * <pre>同步与会者</pre>
     * @author lilinhai
     * @since 2020-12-08 17:28 
     * @param apiUtilNew void
     */
    void syncParticipants(FmeBridge fmeBridge, ParticipantsResponseGetter participantsResponseGetter, ParticipantNewCallbackProcessor participantNewCallbackProcessor);
    
    static interface ParticipantsResponseGetter
    {
        ParticipantsResponse get(FmeBridge fmeBridge, int offset);
    }
}
