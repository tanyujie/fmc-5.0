/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : SetAttendeeLayout2.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.pm.layout
 * @author sinhy
 * @since 2021-09-17 21:28
 * @version  V1.0
 */
package com.paradisecloud.fcm.fme.attendee.model.pm.layout;

import java.lang.reflect.Method;
import java.util.Objects;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.fme.cache.model.SplitScreenCreaterMap;
import com.paradisecloud.fcm.fme.model.busi.attendee.LiveBroadcastAttendee;
import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.fme.attendee.interfaces.ICallegService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.cms.callleg.CallLeg;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.sinhy.exception.SystemException;
import com.sinhy.proxy.ProxyMethod;
import com.sinhy.spring.BeanFactory;

public class SetAttendeeLayout2 extends ProxyMethod {

    /**
     * <pre>构造方法</pre>
     *
     * @param method
     * @author sinhy
     * @since 2021-09-17 21:28
     */
    protected SetAttendeeLayout2(Method method) {
        super(method);
    }

    public void setAttendeeLayout(Attendee a, String layout) {
        if (a != null && a.isMeetingJoined() && !a.getFixedSettings().getChosenLayout().isFixed()) {
            CallLeg callLeg = BeanFactory.getBean(ICallegService.class).getCallLeg(a);
            if (callLeg != null && !layoutEquals(callLeg, layout)) {
                setAttendeeLayout(a, layout, callLeg);
            }
//            if (callLeg != null) {
//                setAttendeeLayout(a, layout, callLeg);
//            }
        }
    }

    protected boolean layoutEquals(CallLeg callLeg, String layout) {
        Boolean flag = SplitScreenCreaterMap.isCustomLayoutTemplate(layout);
        if (flag) {
            if (StringUtils.isNotEmpty(layout)) {
                return Objects.equals(callLeg.getConfiguration().getChosenLayout(), "automatic") && Objects.equals(callLeg.getConfiguration().getLayoutTemplate(), layout);
            } else {
                return Objects.equals(callLeg.getConfiguration().getChosenLayout(), layout) && Objects.equals(callLeg.getConfiguration().getDefaultLayout(), layout);
            }
        } else  {
            if (callLeg.getConfiguration().getChosenLayout() == null && callLeg.getConfiguration().getDefaultLayout() == null) {
                return ObjectUtils.isEmpty(layout);
            } else if (callLeg.getConfiguration().getChosenLayout() != null && callLeg.getConfiguration().getDefaultLayout() != null) {
                return callLeg.getConfiguration().getChosenLayout().equals(layout) && callLeg.getConfiguration().getDefaultLayout().equals(layout);
            } else {
                return false;
            }
        }
    }

    protected void setAttendeeLayout(Attendee a, String layout, CallLeg callLeg) {
        try {
            FmeBridge fmeBridge = FmeBridgeCache.getInstance().getFmeBridgeByMeetingJoinedAttendee(a);
            ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
            Boolean flag = SplitScreenCreaterMap.isCustomLayoutTemplate(layout);
            if (flag) {
                participantParamBuilder.layoutTemplate(layout);
                if (StringUtils.isNotEmpty(layout)) {
                    participantParamBuilder.defaultLayout("").chosenLayout("automatic");
                } else {
                    participantParamBuilder.defaultLayout(layout).chosenLayout(layout);
                }
            } else {
                participantParamBuilder.defaultLayout(layout).chosenLayout(layout);
            }
            if (a instanceof LiveBroadcastAttendee) {
                participantParamBuilder.rxVideoMute(true);
                participantParamBuilder.rxAudioMute(true);
            }
            RestResponse rr = fmeBridge.getCallLegInvoker().updateCallLeg(callLeg.getId(), participantParamBuilder.build());
            if (!rr.isSuccess()) {
                throw new SystemException(1009894, "修改参会者【" + a.getName() + ", " + a.getParticipantUuid() + "】布局失败：" + rr.getMessage());
            }
            if (flag) {
//                callLeg.getConfiguration().setDefaultLayout(layout);
//                callLeg.getConfiguration().setChosenLayout(layout);
                if (StringUtils.isNotEmpty(layout)) {
                    callLeg.getConfiguration().setChosenLayout("automatic");
                    callLeg.getConfiguration().setLayoutTemplate(layout);
                }
            }
            callLeg.getConfiguration().setDefaultLayout(layout);
            callLeg.getConfiguration().setChosenLayout(layout);
        } catch (Throwable e) {
            logger.error("setAttendeeLayout(Attendee a, String layout) error", e);
        }
    }
}
