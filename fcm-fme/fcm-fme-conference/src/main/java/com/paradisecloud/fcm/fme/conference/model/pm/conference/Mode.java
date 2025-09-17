/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : Discuss.java
 * Package     : com.paradisecloud.fcm.fme.conference.model.pm.conference
 * @author sinhy 
 * @since 2021-09-18 11:25
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.conference.model.pm.conference;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ConferenceOpsModeEnum;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.fme.attendee.model.operation.*;
import com.paradisecloud.fcm.fme.cache.ConferenceContextCache;
import com.paradisecloud.fcm.fme.cache.WebSocketMessagePusher;
import com.paradisecloud.fcm.fme.conference.interfaces.IDefaultAttendeeOperationPackageService;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.operation.AttendeeOperation;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class Mode extends ProxyMethod
{

    /**
     * <pre>构造方法</pre>
     * @author sinhy
     * @since 2021-09-18 11:25
     * @param method
     */
    public Mode(Method method)
    {
        super(method);
    }
    
    public void mode(String conferenceId, String modeName)
    {
        new Thread(() -> {
            String contextKey = EncryptIdUtil.parasToContextKey(conferenceId);
            ConferenceContext conferenceContext = ConferenceContextCache.getInstance().getMainConferenceContext(contextKey);
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            }
            IDefaultAttendeeOperationPackageService defaultAttendeeOperationPackageService = BeanFactory.getBean(IDefaultAttendeeOperationPackageService.class);

            AttendeeOperation dao = null;
            if (Objects.equals(modeName, ConferenceOpsModeEnum.DISCUSS.name())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("defaultViewIsBroadcast", 1);
                jsonObject.put("defaultViewIsDisplaySelf", -1);
                jsonObject.put("defaultViewIsFill", 1);
                jsonObject.put("pollingInterval", 10);
                jsonObject.put("defaultViewLayout", "automatic");
                jsonObject.put("defaultViewCellScreens", new ArrayList<>());
                defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceId, jsonObject);

                // 设置布局则设置为高级模式
                conferenceContext.setConferenceMode(modeName);
                // 发送提示信息
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_MODEL, modeName);
            } else if (Objects.equals(modeName, ConferenceOpsModeEnum.LECTURE.name())) {
                dao = new LectureAttendeeOperation(conferenceContext);
            } else if (Objects.equals(modeName, ConferenceOpsModeEnum.DIRECT.name())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("defaultViewIsBroadcast", 1);
                jsonObject.put("defaultViewIsDisplaySelf", -1);
                jsonObject.put("defaultViewIsFill", 1);
                jsonObject.put("pollingInterval", 10);
                jsonObject.put("defaultViewLayout", "allEqual");
                jsonObject.put("defaultViewCellScreens", new ArrayList<>());
                defaultAttendeeOperationPackageService.updateDefaultViewConfigInfo(conferenceId, jsonObject);

                // 设置布局则设置为高级模式
                conferenceContext.setConferenceMode(modeName);
                // 发送提示信息
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入普通模式！");
                WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_MODEL, modeName);
            } else if (Objects.equals(modeName, ConferenceOpsModeEnum.CHAIRMAN_POLLING.name())) {
                dao = new ChairmanPollingOpreationImpl(conferenceContext);
            } else if (Objects.equals(modeName, ConferenceOpsModeEnum.BROAD_POLLING.name())) {
                dao = new BroadPollingOpreationImpl(conferenceContext);
            } else {
                dao = conferenceContext.getDefaultViewOperation();
            }

            if (dao != null) {
                conferenceContext.setAttendeeOperation(dao);
                conferenceContext.getLastAttendeeOperation().cancel(dao);
            }
        }).start();


    }
}
