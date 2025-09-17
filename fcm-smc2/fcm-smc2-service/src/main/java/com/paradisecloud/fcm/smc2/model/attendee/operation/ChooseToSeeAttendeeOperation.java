/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeChooseSeeStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>选看与会者操作（主会场看选看者，分会场看主会场）</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class ChooseToSeeAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    private AttendeeSmc2 chooseSeeAttendee;
    private AttendeeSmc2 oldChooseSeeAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-02-22 13:38
     */
    public ChooseToSeeAttendeeOperation(Smc2ConferenceContext conferenceContext, AttendeeSmc2 chooseSeeAttendee) {
        super(conferenceContext);
        this.chooseSeeAttendee = chooseSeeAttendee;
    }

    @Override
    public void operate() {
        if(chooseSeeAttendee.getChooseSeeStatus()== AttendeeChooseSeeStatus.YES.getValue()){
            return;
        }

        AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();

        if(lastAttendeeOperation instanceof  ChangeMasterAttendeeOperation){
            ChangeMasterAttendeeOperation old= (ChangeMasterAttendeeOperation) lastAttendeeOperation;
            oldChooseSeeAttendee=old.getDefaultChooseSeeAttendee();
        }


        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();

        if(masterAttendee==null){
            throw new CustomException("没有主会场,无法选看");
        }
        if (chooseSeeAttendee == masterAttendee)
        {
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "主会场不能被选看！");
            return;
        }

        if(!chooseSeeAttendee.isMeetingJoined()){
            return;
        }
        String id = masterAttendee.getParticipantUuid();
        String uri = masterAttendee.getSmcParticipant().getGeneralParam().getUri();
        String confId = conferenceContext.getSmc2conferenceId();
        //广播主席
        broadcast(uri, confId,0);

        //要操作的会场uri
        //要观看的会场uri
        String videoSourceUri = chooseSeeAttendee.getSmcParticipant().getGeneralParam().getUri();
        //是否锁定视频源
        // int isLock = 0;
        //获取会议相关服务实例
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer errorCode = conferenceServiceEx.setVideoSourceEx(confId, uri, videoSourceUri, 1);
        if(errorCode==0){
            List<String> urls = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
                for (AttendeeSmc2 attendee : conferenceContext.getAttendees()) {
                    if(attendee.isMeetingJoined()){
                        commonPush(urls, attendee);
                    }

                }
            }
            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                List<AttendeeSmc2> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                if (attendees != null) {
                    for (AttendeeSmc2 attendee : attendees) {
                        if(attendee.isMeetingJoined()){
                            commonPush(urls, attendee);
                        }
                    }
                }
            }

            for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
                if(attendee.isMeetingJoined()){
                    commonPush(urls, attendee);
                }
            }
            mute(conferenceContext.getSmc2conferenceId(),urls,1);


            chooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());
            if (isUpCascadeRollCall()) {
                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(chooseSeeAttendee);
            } else if (isUpCascadePolling()) {
                if (isUpCascadeBroadcast()) {
                    AttendeeImportance.ROUND_BROADCAST.processAttendeeWebsocketMessage(chooseSeeAttendee);
                } else {
                    AttendeeImportance.ROUND.processAttendeeWebsocketMessage(chooseSeeAttendee);
                }
            } else {
                AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(chooseSeeAttendee);
            }
            if (oldChooseSeeAttendee != null&&!Objects.equals(oldChooseSeeAttendee.getId(),chooseSeeAttendee.getId())) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldChooseSeeAttendee);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldChooseSeeAttendee);
            }
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);
            return;
        }
        else {
            logger.error("选看失败："+errorCode);
            throw new CustomException("操作失败："+errorCode);
        }
    }

    private void commonPush(List<String> urls, AttendeeSmc2 attendee) {
        urls.add(attendee.getRemoteParty());
        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendee);
        if (attendee.getUpdateMap().size() > 1) {
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee);
        }
    }


    /**
     * 解锁视频源
     * @param uri
     * @param confId
     * @param conferenceServiceEx
     * @param errorCode
     */
    private void unLockVideoSource(String uri, String confId, ConferenceServiceEx conferenceServiceEx, Integer errorCode) {
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
        WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
        //解锁
        item1.setOperaTypeParam(1);
        //会场标识为01033001
        item1.setSiteUri(uri);
        wsCtrlSiteCommParams.add(item1);
        int resultCode = conferenceServiceEx.setVSAttrCtrlEx(confId,wsCtrlSiteCommParams);
        if(resultCode!=0){
            logger.error(uri +"解锁失败："+ errorCode);
        }
    }



    @Override
    public void cancel() {
        if(chooseSeeAttendee!=null){

            //取消混音
            SmcParitipantsStateRep.ContentDTO smcParticipant = chooseSeeAttendee.getSmcParticipant();
            if(smcParticipant!=null){
                List<String> siteUrls=new ArrayList<>();
                siteUrls.add(smcParticipant.getGeneralParam().getUri());
                mute(conferenceContext.getSmc2conferenceId(),siteUrls,1);
            }

            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(chooseSeeAttendee);
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);


            if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
                for (AttendeeSmc2 attendee : conferenceContext.getAttendees()) {
                    pushMessage(attendee);
                }
            }
            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                List<AttendeeSmc2> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                if (attendees != null) {
                    for (AttendeeSmc2 attendee : attendees) {
                        pushMessage(attendee);
                    }
                }
            }

            for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
                pushMessage(attendee);
            }

        }
    }

    private void pushMessage(AttendeeSmc2 attendee) {
        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendee);
        if (attendee.getUpdateMap().size() > 1) {
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee);
        }
    }

    private void mute(String confId,List<String> siteUrls,Integer isMute) {
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode =  conferenceServiceEx.setSitesMuteEx(confId,siteUrls,isMute);
        if (resultCode != 0) {
            logger.error( "开/闭麦操作：" + resultCode+"操作："+isMute.intValue());
        }
    }

}
