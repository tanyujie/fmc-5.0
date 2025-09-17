/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.ding.model.operation;

import com.paradisecloud.fcm.ding.busi.AttendeeImportance;
import com.paradisecloud.fcm.ding.busi.attende.AttendeeDing;
import com.paradisecloud.fcm.ding.cache.DingConferenceContext;
import com.paradisecloud.fcm.ding.cache.DingWebSocketMessagePusher;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;

import org.apache.commons.collections.CollectionUtils;

import java.util.*;


/**
 * <pre>选看与会者操作（主会场看选看者，分会场看主会场）</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class ChooseToSeeAttendeeOperation extends AttendeeOperation {

    private final AttendeeDing chooseSeeAttendee;
    private AttendeeDing oldChooseSeeAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param chooseSeeAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChooseToSeeAttendeeOperation(DingConferenceContext conferenceContext, AttendeeDing chooseSeeAttendee) {
        super(conferenceContext);
        this.chooseSeeAttendee = chooseSeeAttendee;
    }

    /**
     * 操作方法
     *
     * @author lilinhai
     * @since 2021-02-20 16:39  void
     */
    @Override
    public void operate() {
        operateScreen();
    }

    private void operateScreen() {


    }


    @Override
    public void cancel() {
        if (chooseSeeAttendee != null) {

            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(chooseSeeAttendee);
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);


            if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
                for (AttendeeDing attendee : conferenceContext.getAttendees()) {
                    pushMessage(attendee);
                }
            }
            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                List<AttendeeDing> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                if (attendees != null) {
                    for (AttendeeDing attendee : attendees) {
                        pushMessage(attendee);
                    }
                }
            }

            for (AttendeeDing attendee : conferenceContext.getMasterAttendees()) {
                pushMessage(attendee);
            }

        }
    }

    private void pushMessage(AttendeeDing attendee) {
        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendee);
        if (attendee.getUpdateMap().size() > 1) {
            DingWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee);
        }
    }




}
