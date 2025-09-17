/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : EndConference.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:32
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.constant.MqttConfigConstant;
import com.paradisecloud.fcm.common.constant.ResponseInfo;
import com.paradisecloud.fcm.common.constant.TerminalTopic;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.common.task.TaskService;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceAppointmentMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceAppointment;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.conference.listener.MqttForFmePushMessageCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.paradisecloud.fcm.service.eunm.NotifyType;
import com.paradisecloud.fcm.service.interfaces.IAllConferenceService;
import com.paradisecloud.fcm.service.task.CloudSmsLocaltoRemoteTask;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.time.DateUtils;

public class EndConference extends EndConference2
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:32 
     * @param method 
     */
    public EndConference(Method method)
    {
        super(method);
    }
    
    public void endConference(String conferenceId, int endType, int endReasonsType)
    {
        // 会议结束类型
        final String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceEndType conferenceEndType = ConferenceEndType.convert(endType);
        if (conferenceEndType == ConferenceEndType.CASCADE)
        {
            AtomicInteger successCount = new AtomicInteger();
            ConferenceContextCache.getInstance().destroyAllCascadeConferenceContexts(contextKey, (cc)->{
                endConference(cc, successCount, endReasonsType);
            });
        }
//        else
        {
            ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
            new Thread(()->{
                AtomicInteger successCount = new AtomicInteger();
//                ConferenceContext cc = ConferenceContextCache.getInstance().get(contextKey);
                endConference(cc, successCount, endReasonsType);
            }).start();
            BeanFactory.getBean(IAllConferenceService.class).processAfterEndConference(cc);
        }
    }

    /**
     * <pre>结束会议</pre>
     * @author lilinhai
     * @since 2021-03-12 10:18
     * @param cc void
     */
    private void endConference(ConferenceContext cc, AtomicInteger successCount, int endReasonsType)
    {
        if (cc != null)
        {
            long startTime = System.currentTimeMillis();
            try
            {
                FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(cc.getDeptId(), new FmeBridgeAddpterProcessor()
                {
                    public void process(FmeBridge fmeBridge)
                    {
                        FcmThreadPool.exec(() -> {
                            Call call = fmeBridge.getDataCache().getCallByConferenceNumber(cc.getConferenceNumber());
                            if (call != null)
                            {
                                long s = System.currentTimeMillis();
                                fmeBridge.getCallInvoker().deleteCall(call.getId());
                                fmeBridge.getFmeLogger().logInfo("结束call【" + call.getId() + "】耗时: " + (System.currentTimeMillis() - s), true, false);
                                fmeBridge.getCallLegProfileInvoker().deleteCallLegProfile(cc.getCallLegProfile().getId());
                                successCount.incrementAndGet();
                            }
                        });
                    }
                });
            }
            catch (Throwable e)
            {
                logger.error("endConference-error", e);
            }
            finally 
            {
                long s1 = System.currentTimeMillis();
                endConference(cc.getContextKey(), endReasonsType);
                if (cc.getConferenceAppointment() != null)
                {
                    BusiConferenceAppointment conferenceAppointment = BeanFactory.getBean(BusiConferenceAppointmentMapper.class).selectBusiConferenceAppointmentById(cc.getConferenceAppointment().getId());
                    if (conferenceAppointment != null)
                    {
                        conferenceAppointment.setIsHangUp(YesOrNo.YES.getValue());
                        conferenceAppointment.setIsStart(null);
                        conferenceAppointment.setExtendMinutes(null);
                        BeanFactory.getBean(BusiConferenceAppointmentMapper.class).updateBusiConferenceAppointment(conferenceAppointment);
                    }


                }
                
                StringBuilder infoBuilder = new StringBuilder();
                infoBuilder.append("结束会议【").append(cc.getName()).append("】");
                infoBuilder.append(", conferenceNumber: ").append(cc.getConferenceNumber()).append("，耗时：").append(System.currentTimeMillis() - startTime).append(", ").append(System.currentTimeMillis() - s1).append(", ").append(s1 - startTime);

                if (MqttForFmePushMessageCache.getInstance().getMqttForFmePushMessageListener() != null) {
                    String topic = MqttConfigConstant.TOPIC_PREFIX_CONFERENCE + cc.getId();
                    String action = TerminalTopic.END_CONFERENCE;
                    JSONObject jObj = new JSONObject();
                    jObj.put(MqttConfigConstant.CONFERENCE_ID, cc.getId());
                    jObj.put(MqttConfigConstant.CONFERENCENUM, cc.getConferenceNumber());
                    MqttForFmePushMessageCache.getInstance().getMqttForFmePushMessageListener().onPushMessage(ResponseInfo.CODE_200, "会议结束！", topic, action, jObj, "", "");
                }
                logger.info(infoBuilder.toString());


            }
        }
    }
}
