/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : RosterProcessorUpdateMessage.java
 * Package     : com.paradisecloud.fcm.fme.websocket.model.roster.message
 * @author sinhy 
 * @since 2021-09-07 23:48
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.model.callinfo.message;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.sinhy.spring.BeanFactory;

import java.util.Collection;

public class CallInfoProcessorUpdateMessage extends CallInfoProcessorMessage
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-07 23:46 
     * @param fmeBridge
     * @param updateItem 
     */
    public CallInfoProcessorUpdateMessage(FmeBridge fmeBridge, JSONObject updateItem)
    {
        super(fmeBridge, updateItem);
    }

    @Override
    protected void process0()
    {
        Call call = fmeBridge.getDataCache().getCallByUuid(itemId);
        if (call == null)
        {
            fmeBridge.getFmeLogger().logWebsocketInfo("根据UUID在所有内存中找不到Call对象：" + itemId, true, true);
            return;
        }
        
        compareAndUpdate(call);
        
        String cn = fmeBridge.getDataCache().getConferenceNumberByCallId(call.getId());
        ConferenceContext mainConferenceContext = null;
        Collection<ConferenceContext> conferenceContextList = ConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(cn);
        if (conferenceContextList != null && conferenceContextList.size() > 0) {
            for (ConferenceContext conferenceContextTemp : conferenceContextList) {
                if (call.getCoSpace().equals(conferenceContextTemp.getCoSpaceId())) {
                    mainConferenceContext = conferenceContextTemp;
                    break;
                }
            }
        }
        if (mainConferenceContext != null)
        {
            if (call.getRecording() != null && call.getRecording())
            {
                if (!mainConferenceContext.isRecorded())
                {
                    mainConferenceContext.setRecorded(true);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.RECORDED, true);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启录制");
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }
            else
            {
                if (mainConferenceContext.isRecorded())
                {
                    mainConferenceContext.setRecorded(false);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.RECORDED, false);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭录制");
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }
            
            if (call.getStreaming() != null && call.getStreaming())
            {
                if (!mainConferenceContext.isStreaming())
                {
                    mainConferenceContext.setStreaming(true);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.STREAMING, true);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }
            else
            {
                if (mainConferenceContext.isStreaming())
                {
                    mainConferenceContext.setStreaming(false);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.STREAMING, false);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }
            if (call.getLock() != null && call.getLocked()){
                if (!mainConferenceContext.isLocked())
                {
                    mainConferenceContext.setLocked(true);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.CONFERENCE_LOCK, true);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已锁定");
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }else {
                if (mainConferenceContext.isLocked())
                {
                    mainConferenceContext.setLocked(false);

                    // 向所有客户端通知会议的录制状态
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.CONFERENCE_LOCK, false);
                    WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已解除锁定");
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(mainConferenceContext);
                }
            }
        }
        
        fmeBridge.getFmeLogger().logInfo("Call lightweight update：" + itemId, true, false);
    }
    
    private void compareAndUpdate(Call call)
    {
        call.setCallInfo(updateItem);
        
        if (updateItem.containsKey("name"))
        {
            call.setName(updateItem.getString("name"));
        }
        
        if (updateItem.containsKey("participants"))
        {
            call.setNumParticipantsLocal(updateItem.getInteger("participants"));
        }
        
        if (updateItem.containsKey("distributedInstances"))
        {
            call.setNumDistributedInstances(updateItem.getInteger("distributedInstances"));
        }
        
        if (updateItem.containsKey("recording"))
        {
            call.setRecording("active".equals(updateItem.getString("recording")));
            call.setRecordingStatus(call.getRecording());
        }
        
        if (updateItem.containsKey("endpointRecording"))
        {
            call.setEndpointRecording("active".equals(updateItem.getString("endpointRecording")));
        }
        
        if (updateItem.containsKey("streaming"))
        {
            call.setStreaming("active".equals(updateItem.getString("streaming")));
            call.setStreamingStatus(call.getStreaming());
        }
        
        if (updateItem.containsKey("lockState"))
        {
            call.setLocked("locked".equals(updateItem.getString("lockState")));
        }
        
        if (updateItem.containsKey("callType"))
        {
            call.setCallType(updateItem.getString("callType"));
        }
        
        if (updateItem.containsKey("callCorrelator"))
        {
            call.setCallCorrelator(updateItem.getString("callCorrelator"));
        }
        
        if (updateItem.containsKey("joinAudioMuteOverride"))
        {
            call.setJoinAudioMuteOverride(updateItem.getBoolean("joinAudioMuteOverride"));
        }
    }
}
