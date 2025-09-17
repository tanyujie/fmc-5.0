package com.paradisecloud.fcm.fme.attendee.model.busiprocessor;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.attendee.model.core.AttendeeSettingsInitializer;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.SelfCallAttendee;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

public class CallStreamerProcessor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private ConferenceContext conferenceContext;
    private String uri;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-02-09 11:00
     * @param conferenceNumber
     * @param uri
     */
    public CallStreamerProcessor(String contextKey, String uri)
    {
        conferenceContext = ConferenceContextCache.getInstance().get(contextKey);
        this.uri = uri;
    }

    public void process() {
        if (conferenceContext == null || conferenceContext.isEnd()) {
            throw new CustomException("开启直播失败!");
        }
        try {
            Attendee targetAttendee = new SelfCallAttendee();
            targetAttendee.setRemoteParty(uri);
            targetAttendee.setName("直播服务");
            FmeBridgeCache.getInstance().doRandomFmeBridgeBusiness(conferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
            {
                public void process(FmeBridge fmeBridge)
                {
                    Call call = BeanFactory.getBean(ICallService.class).createCall(fmeBridge, conferenceContext.getConferenceNumber(), conferenceContext.getName());
                    AttendeeSettingsInitializer attendeeSettingsInitializer = new AttendeeSettingsInitializer(conferenceContext, targetAttendee);
                    String id = fmeBridge.getParticipantInvoker().createParticipant(call.getId(), attendeeSettingsInitializer.getParticipantParamBuilder().build());
                    if (!ObjectUtils.isEmpty(id))
                    {
                        logger.info("终端【" + targetAttendee.getRemoteParty() + "】根据UUID已绑定FME与会者UUID: " + id);
                    } else {
                        throw new CustomException("开启直播失败!");
                    }
                }
            });
        } catch (Exception e) {
            logger.error("呼叫直播与会者发生异常-doCall：" + uri, e);
            throw new CustomException("开启直播失败!");
        }
    }
}
