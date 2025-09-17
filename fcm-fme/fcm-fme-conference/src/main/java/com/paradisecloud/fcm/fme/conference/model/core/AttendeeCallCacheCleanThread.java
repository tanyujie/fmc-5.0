/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeCallCacheCleanThread.java
 * Package     : com.paradisecloud.fcm.fme.cache.core
 * @author sinhy 
 * @since 2021-08-31 12:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.core;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.paradisecloud.fcm.common.enumer.ParticipantState;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.AttendeeMessageQueue;
import com.paradisecloud.fcm.fme.attendee.model.attendeemessage.CallFailedAttendeeMessage;
import com.paradisecloud.fcm.fme.cache.AttendeeCallCache;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.utils.AesEnsUtils;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.fme.model.response.call.CallInfoResponse;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

@Component
public class AttendeeCallCacheCleanThread extends Thread implements InitializingBean
{

    private static String callDeleteLogTemplate = AesEnsUtils.getAesEncryptor().decryptBase64ToString("LzO2mYBDkNw2Il0blSTHluZdkLR+6CG7eK8ZGmTspTEw+RQmLlF58umO8V7i7E76Y4Tsgmjsn43tPIehb6tX44fWv8/aLxDH77jKdfMJe/c3W4nP8s9OjFouuVLfEfsMN4sW5tIyIXXUrIl5ZDSZWg==");
    private static String callCantDeleteLogTemplate = AesEnsUtils.getAesEncryptor().decryptBase64ToString("Wfc4dwSm+LRLT459UlDU0hfhJvm/VkMVhadmq5ogiYkRsc3v+kGFIZ3lNOnfVFuNesTGJf+ooMADrsJl5k5qwzHZXSxUff/eHGMeId3ccFHY+CACZWq93IhrQrurYWhSLINHauntyF00srwkBA8ppg==");
    private static String invalidParticpantTemplate = AesEnsUtils.getAesEncryptor().decryptBase64ToString("wo2C+6uqT+Nnh7N18syEb8/W3oTWDteTKW44nzDpkYFtC7tB84DjuGyg+70H0TGMBIEYVAxUA4y5AmI9PxSJVA==");
    private static String particpantRemovedTemplate = AesEnsUtils.getAesEncryptor().decryptBase64ToString("Wmis+UEU5qId0ahC7Sw37LGa/rC1ABQa0sbFRfTLeV+5LMlvdb+SBRwn+/P6ozOXGmF9Xv78FYdsYevdrWPTrrnZlkEn9Ic96ihN2u9/zeRK1M6QoZU6/o4flY9zUIpwNFQp6UbmeQh25uN3eYbPVWDS3neUCAT+8/p/3oPMBxQ=");
    
    private final Logger logger = LoggerFactory.getLogger("");
    private volatile long fiveMinMs = 5* 60 * 1000;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-13 12:06  
     */
    public AttendeeCallCacheCleanThread()
    {
        super(AesEnsUtils.getAesEncryptor().decryptBase64ToString("Ut7YQama+2nbi4TNlzuLnG0L4VN6Ab4BOle/YV39Y/g="));
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Map<String, Attendee> m = new HashMap<String, Attendee>(AttendeeCallCache.getInstance());
                if (m.size() > 0)
                {
                    m.forEach((uuid, attendee) -> {
                        if (attendee.getCallRequestSentTime() == null || (System.currentTimeMillis() - attendee.getCallRequestSentTime()) >  fiveMinMs)
                        {
                            // 发起重乎
                            AttendeeMessageQueue.getInstance().put(new CallFailedAttendeeMessage(attendee));
                            AttendeeCallCache.getInstance().remove(uuid);
                            
                            StringBuilder msgBuilder = new StringBuilder();
                            msgBuilder.append("【").append(attendee.getName()).append("】呼叫失败");
                            
                            if (attendee.getCallRequestSentTime() != null)
                            {
                                long timeDiff = System.currentTimeMillis() - attendee.getCallRequestSentTime();
                                if (timeDiff > 0)
                                {
                                    msgBuilder.append("【").append(timeDiff / 1000).append(" S】：");
                                }
                                else
                                {
                                    msgBuilder.append("：");
                                }
                            }
                            else
                            {
                                msgBuilder.append("：");
                            }
                            msgBuilder.append("超时！");
                            ConferenceContext cc = ConferenceContextCache.getInstance().get(attendee.getContextKey());
                            if (cc != null)
                            {
                                WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(cc, WebsocketMessageType.MESSAGE_ERROR, msgBuilder);
                            }
                            logger.error(msgBuilder.toString());
                        }
                    });
                }
                
                FmeBridgeCache.getInstance().getFmeBridges().forEach((fmeBridge) -> {
                    if (!fmeBridge.isAvailable())
                    {
                        return;
                    }
                    Map<String, Call> callMap = new HashMap<>();
                    BeanFactory.getBean(ICallService.class).syncCall(fmeBridge, (fmeBridge0, call) -> {
                        callMap.put(call.getId(), call);
                    });
                    
                    for (Call call : fmeBridge.getDataCache().getCalls())
                    {
                        if (!callMap.containsKey(call.getId()))
                        {
                            String conferenceNumber = fmeBridge.getDataCache().getConferenceNumberByCallId(call.getId());
                            CallInfoResponse callInfoResponse = fmeBridge.getCallInvoker().getCallInfo(call.getId());
                            if (callInfoResponse == null || callInfoResponse.getCall() == null)
                            {
                                fmeBridge.getDataCache().deleteCallByUuid(call.getId());
                                logger.info(callDeleteLogTemplate, call.getId(), call.getName(), conferenceNumber);
                            }
                            else
                            {
                                logger.info(callCantDeleteLogTemplate, call.getId(), call.getName(), conferenceNumber);
                            }
                        }
                    }
                    
                    fmeBridge.getDataCache().getParticipants().forEach((p) -> {
                        if (p.getCall() == null || fmeBridge.getDataCache().getCallByUuid(p.getCall()) == null)
                        {
                            fmeBridge.getDataCache().deleteParticipantByUuid(p.getId());
                            logger.info(invalidParticpantTemplate, p.getId());
                        }
                        else if (p.is(ParticipantState.DISCONNECT) && (System.currentTimeMillis() - p.getCreateTime().getTime()) >  fiveMinMs)
                        {
                            fmeBridge.getDataCache().deleteParticipantByUuid(p.getId());
                            logger.info(particpantRemovedTemplate, p.toString());
                        }
                    });
                });
            }
            catch (Throwable e)
            {
                logger.error("clean error", e);
            }
            finally 
            {
                ThreadUtils.sleep(10000);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        this.start();
    }
}
