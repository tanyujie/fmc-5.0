/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : UpdateCall.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:00
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.http.NameValuePair;

import com.paradisecloud.fcm.fme.attendee.interfaces.ICallService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.bridgeprocessor.FmeBridgeAddpterProcessor;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.Call;
import com.sinhy.model.GenericValue;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

public class UpdateCall extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:00 
     * @param method 
     */
    protected UpdateCall(Method method)
    {
        super(method);
    }
    
    public void updateCall(String contextKey, List<NameValuePair> nameValuePairs)
    {
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        FmeBridgeCache.getInstance().doTraverseFmeBridgeBusiness(mainConferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                Call call = fmeBridge.getDataCache().getCallByConferenceNumber(mainConferenceContext.getConferenceNumber());
                if (call == null)
                {
                    return;
                }
                
                fmeBridge.getCallInvoker().updateCall(call.getId(), nameValuePairs);
            }
        });
    }
    
    public Boolean updateExistParticipantCall(String contextKey, List<NameValuePair> nameValuePairs)
    {
        GenericValue<Boolean> bv = new GenericValue<>();
        bv.setValue(false);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        FmeBridgeCache.getInstance().doBreakFmeBridgeBusiness(mainConferenceContext.getDeptId(), new FmeBridgeAddpterProcessor()
        {
            public void process(FmeBridge fmeBridge)
            {
                Call call = fmeBridge.getDataCache().getCallByConferenceNumber(mainConferenceContext.getConferenceNumber());
                if (call == null)
                {
                    return;
                }
                
                BeanFactory.getBean(ICallService.class).syncCall(fmeBridge, call.getId());
                call = fmeBridge.getDataCache().getCallByConferenceNumber(mainConferenceContext.getConferenceNumber());
                if (call.getNumParticipantsLocal() != null && call.getNumParticipantsLocal() > 0)
                {
                    fmeBridge.getCallInvoker().updateCall(call.getId(), nameValuePairs);
                    bv.setValue(true);
                    setBreak(true);
                }
            }
        });
        return bv.getValue();
    }
}
