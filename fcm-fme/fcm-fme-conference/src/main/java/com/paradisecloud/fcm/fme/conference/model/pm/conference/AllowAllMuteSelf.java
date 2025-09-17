/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AllowAllMuteSelf.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:20
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import java.lang.reflect.Method;

import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.parambuilder.CallParamBuilder;

public class AllowAllMuteSelf extends UpdateCall
{
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-18 11:20 
     * @param method 
     */
    public AllowAllMuteSelf(Method method)
    {
        super(method);
    }
 
    public void allowAllMuteSelf(String conferenceId, Boolean enabled)
    {
        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext mainConferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        
        updateCall(contextKey, new CallParamBuilder().allowAllMuteSelf(enabled).build());
        
        mainConferenceContext.setAllowAllMuteSelf(enabled);
        
        // 消息和参会者信息同步到主级会议
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.CONFERENCE_ALLOW_ALL_MUTE_SELF, enabled);
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(mainConferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (enabled ? "允许" : "关闭") + "“所有人静音自己”");
    }
}
