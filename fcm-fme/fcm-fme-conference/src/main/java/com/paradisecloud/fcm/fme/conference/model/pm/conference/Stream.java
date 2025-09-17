/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Stream.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:24
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.common.exception.CustomException;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;

public class Stream extends UpdateCall
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:24 
     * @param method 
     */
    public Stream(Method method)
    {
        super(method);
    }
    
    public void stream(ConferenceContext mainConferenceContext, Boolean streaming, String streamUrl)
    {
        // 更新直播地址
        if (!ObjectUtils.isEmpty(streamUrl))
        {
            CoSpaceParamBuilder coSpaceParamBuilder = new CoSpaceParamBuilder();
            coSpaceParamBuilder.streamUrl(streamUrl);
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(mainConferenceContext);
            try {
                BeanFactory.getBean(ICoSpaceService.class).updateCoSpace(fmeBridge, fmeBridge.getDataCache().getCoSpaceByConferenceNumber(mainConferenceContext.getConferenceNumber()), coSpaceParamBuilder);
            } catch (Exception e) {
                logger.error("stream == ",streamUrl);
                throw new CustomException("开启直播失败!");
            }
        }
        
        new Thread(() -> {
            ThreadUtils.sleep(3000);
            updateExistParticipantCall(mainConferenceContext.getContextKey(), new CallParamBuilder().streaming(streaming).build());
        }).start();
    }
    
}
