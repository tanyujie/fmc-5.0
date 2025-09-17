/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>讨论操作 TODO </pre>
 * @author lilinhai
 * @since 2021-04-25 14:18
 * @version V1.0
 */
public class DiscussAttendeeOperation extends AttendeeOperation
{

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-04-25 14:19
     */
    private static final long serialVersionUID = 1L;

    private volatile List<AttendeeSmc2> targetAttendees = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();

    private AttendeeOperation lastAttendeeOperation;

    /**
     * <pre>构造方法</pre>
     * @author lilinhai
     * @since 2021-04-25 14:19
     * @param conferenceContext
     */
    public DiscussAttendeeOperation(Smc2ConferenceContext conferenceContext)
    {
        super(conferenceContext);
    }

    public AttendeeOperation getLastAttendeeOperation() {
        return lastAttendeeOperation;
    }

    public void setLastAttendeeOperation(AttendeeOperation lastAttendeeOperation) {
        this.lastAttendeeOperation = lastAttendeeOperation;
    }

    private void initTargetAttendees() {
        targetAttendees.clear();
        if (!ObjectUtils.isEmpty(attendees)) {
            targetAttendees.addAll(attendees);
        }

        if (conferenceContext.getMasterAttendee() != null) {
            if (!checkedAttendeeIdSet.contains(conferenceContext.getMasterAttendee().getId())) {
                targetAttendees.add(conferenceContext.getMasterAttendee());
            }

            if (conferenceContext.getMasterAttendee().getDeptId() != conferenceContext.getDeptId().longValue() && conferenceContext.getMasterAttendeeIdSet().contains(conferenceContext.getMasterAttendee().getId())) {
                List<AttendeeSmc2> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null) {
                    for (AttendeeSmc2 attendee : new ArrayList<>(as)) {
                        if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                            targetAttendees.add(attendee);
                        }
                    }
                }
            }
        }

        for (AttendeeSmc2 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
        }

        for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
            List<AttendeeSmc2> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null) {
                for (AttendeeSmc2 a : new ArrayList<>(as)) {
                    if (!checkedAttendeeIdSet.contains(a.getId())) {
                        targetAttendees.add(a);
                    }
                }
            }
        }



        Set<String> idSet = new HashSet<>();

        if(!CollectionUtils.isEmpty(targetAttendees)){
            List<AttendeeSmc2> arrayList = new ArrayList<>();
            for (AttendeeSmc2 attendee : targetAttendees) {
                if (idSet.add(attendee.getId())) {
                    arrayList.add(attendee);
                }
            }
            targetAttendees=arrayList;
        }

    }

    @Override
    public void operate()
    {
        if(conferenceContext.isDiscuss()){
            return;
        }
        AttendeeOperation clastAttendeeOperation = conferenceContext.getLastAttendeeOperation();
         lastAttendeeOperation=clastAttendeeOperation;
        initTargetAttendees();


        String confId = conferenceContext.getSmc2conferenceId();

        // 全体开麦克风
        List<String> list = new ArrayList<String>();
        for (AttendeeSmc2 targetAttendee : targetAttendees) {
            if(targetAttendee.isMeetingJoined()){
                list.add(targetAttendee.getSmcParticipant().getGeneralParam().getUri());
            }
        }

        //是否闭音。
        //0：不闭音
        //1：闭音
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(confId, list, 0);
        if (resultCode != 0) {
            throw new CustomException("全体静音错误：" + resultCode);
        }

        Integer resultCode2 = conferenceServiceEx.setAudioSwitchEx(confId, 50, 1);
        if (resultCode2 != 0) {
            throw new CustomException("声控切换错误" + resultCode2);
        }
        conferenceContext.setDiscuss(true);
        // 发送提示信息
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());

    }

    @Override
    public void cancel()
    {
        if (!conferenceContext.isEnd())
        {
            conferenceContext.setDiscuss(false);
            // 全体关闭麦克风
            String confId = conferenceContext.getSmc2conferenceId();

            List<String> list = new ArrayList<String>();
            for (AttendeeSmc2 targetAttendee : targetAttendees) {
                if(targetAttendee.isMeetingJoined()){
                    list.add(targetAttendee.getSmcParticipant().getGeneralParam().getUri());
                }
            }
            //是否闭音。
            //0：不闭音
            //1：闭音
            ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
            Integer resultCode = conferenceServiceEx.setSitesMuteEx(confId, list, 1);
            if (resultCode != 0) {
                throw new CustomException("全体静音错误：" + resultCode);
            }

            Integer resultCode2 = conferenceServiceEx.setAudioSwitchEx(confId, 50, 0);
            if (resultCode2 != 0) {
                throw new CustomException("声控切换错误" + resultCode2);
            }
            // 发送提示信息
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已退出讨论模式！");
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
        }

    }

}
