package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

import java.lang.reflect.Method;

/**
 * @author nj
 * @date 2023/1/31 11:28
 */
public class UpdateConferencePresenter extends ProxyMethod {
    protected UpdateConferencePresenter(Method method) {
        super(method);
    }

    public void updateConferencePresenter(String conferenceId, Boolean enable, Long presenter) {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        if (enable) {
            conferenceContext.setPresenter(presenter);
        } else {
            conferenceContext.setPresenter(0L);
        }

        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }
}
