package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.apiservice.interfaces.ICoSpaceService;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.cms.CoSpace;
import com.paradisecloud.fcm.fme.model.parambuilder.CoSpaceParamBuilder;
import com.sinhy.exception.SystemException;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/11/2 10:19
 */
public class UpdateConferenceName extends ProxyMethod {


    protected UpdateConferenceName(Method method) {
        super(method);
    }
    public void updateConferenceName(String conferenceId, String name){

        String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
        ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
        String oldName = conferenceContext.getName();
        if(Objects.equals(oldName,name)){
            return;
        }

        FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByConferenceContext(conferenceContext);
        CoSpace coSpace = fmeBridge.getDataCache().getCoSpaceByConferenceNumber(conferenceContext.getConferenceNumber());
        RestResponse restResponse = fmeBridge.getCoSpaceInvoker().updateCoSpace(coSpace.getId(), new CoSpaceParamBuilder().name(name).build());
        if (!restResponse.isSuccess())
        {
            throw new SystemException(1015237, "修改会议名称失败！");
        }
        // 更新CoSpace缓存
        BeanFactory.getBean(ICoSpaceService.class).updateCoSpaceCache(fmeBridge, coSpace.getId());
        conferenceContext.setName(name);

        //同步到会议
        WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_NAME_CHANGED, name);
        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
    }

}
