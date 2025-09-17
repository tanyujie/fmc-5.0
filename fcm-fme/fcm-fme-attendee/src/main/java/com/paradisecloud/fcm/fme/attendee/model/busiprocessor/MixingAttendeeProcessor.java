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

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.fme.cache.model.enumer.ParticipantBulkOperationMode;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.exception.SystemException;

/**  
 * <pre>混音处理器</pre>
 * @author lilinhai
 * @since 2021-02-23 15:22
 * @version V1.0  
 */
public class MixingAttendeeProcessor extends AttendeeBusiProcessor
{
    
    /**
     * 混音参数
     */
    private boolean rxAudioMute;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-09 11:00 
     * @param contextKey
     * @param attendeeId 
     */
    public MixingAttendeeProcessor(String contextKey, String attendeeId, boolean rxAudioMute)
    {
        super(contextKey, attendeeId);
        this.rxAudioMute = rxAudioMute;
    }

    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-23 16:57
     * @param attendee 
     */
    public MixingAttendeeProcessor(Attendee attendee, boolean rxAudioMute)
    {
        super(attendee);
        this.rxAudioMute = rxAudioMute;
    }

    @Override
    public void process()
    {
        if (targetAttendee.isMeetingJoined() && targetAttendee.getMixingStatus() != AttendeeMixingStatus.convert(rxAudioMute).getValue())
        {
            // 开启/关闭混音
            RestResponse restResponse = fmeBridge.getParticipantInvoker().bulkUpdateParticipant(targetAttendee.getCallId()
                    , new ParticipantParamBuilder().rxAudioMute(rxAudioMute).build()
                    , ParticipantBulkOperationMode.SELECTED, targetAttendee.getParticipantUuid());
            
            if (!restResponse.isSuccess())
            {
                throw new SystemException(1004323, (rxAudioMute ? "关闭" : "开启" ) + "混音失败: " + restResponse.getMessage());
            }
        }
    }
    
}
