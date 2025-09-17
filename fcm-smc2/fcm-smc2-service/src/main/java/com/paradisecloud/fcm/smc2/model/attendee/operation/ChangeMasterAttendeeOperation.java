package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.paradisecloud.com.fcm.smc.modle.ConferenceState;
import com.paradisecloud.com.fcm.smc.modle.ParticipantRspDto;
import com.paradisecloud.com.fcm.smc.modle.response.SmcParitipantsStateRep;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.smc2.model.enumer.Smc2ErrorCode;
import com.paradisecloud.fcm.smc2.model.notice.OngoingConfNotificationExMessage;
import com.sinhy.utils.ThreadUtils;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;

import java.util.*;

/**
 * @author nj
 * @date 2023/5/16 14:30
 */
public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private volatile AttendeeSmc2 defaultChooseSeeAttendee = null;
    private AttendeeSmc2 targetAttendee;
    private AttendeeSmc2 oldMasterAttendee;
    private List<AttendeeSmc2> otherAttendee=new ArrayList<>();
    protected ChangeMasterAttendeeOperation(Smc2ConferenceContext conferenceContext) {
        super(conferenceContext);
        this.oldMasterAttendee = conferenceContext.getMasterAttendee();
    }


    public ChangeMasterAttendeeOperation(Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendeeSmc2) {
        super(conferenceContext);
        this.oldMasterAttendee = conferenceContext.getMasterAttendee();
        this.targetAttendee = attendeeSmc2;
    }
    public AttendeeSmc2 getDefaultChooseSeeAttendee() {
        return defaultChooseSeeAttendee;
    }

    public void setDefaultChooseSeeAttendee(AttendeeSmc2 defaultChooseSeeAttendee) {
        this.defaultChooseSeeAttendee = defaultChooseSeeAttendee;
    }
    @Override
    public void operate() {

        initTargetAttendees();
        changMasterProcess();

    }


    private void initTargetAttendees() {
        if(targetAttendee==null){
            return;
        }
        for (AttendeeSmc2 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                        && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc2> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc2 attendee : attendees) {
                    if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                            && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }

        for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
            if (attendee.isMeetingJoined() && !Objects.equals(attendee.getId(), targetAttendee.getId())
                    && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
                defaultChooseSeeAttendee = attendee;
                return;
            }
        }

        AttendeeSmc2 attendee = conferenceContext.getMasterAttendee();
        if (attendee != null && !Objects.equals(attendee.getId(), targetAttendee.getId())
                && attendee.isMeetingJoined() && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getSmcParticipant().getGeneralParam().getName())) {
            defaultChooseSeeAttendee = attendee;
            return;
        }
    }

    /**
     * 会议中同一时段只能存在一个主席会场。
     * 若被指定会场已是主席，重复设置会失败。
     * 若会议中已经设置了其他会场为主席会场，必须先通过releaseConfChairEx接口释放原来的主席，才能设置新的主席。
     * 如果是智真三屏会场，只能设置中屏为主席。
     */
    private void changMasterProcess() {
        if (targetAttendee == null) {
            return;
        }
        if (conferenceContext == null || conferenceContext.isEnd()) {
            return;
        }
        String confId = conferenceContext.getSmc2conferenceId();
       // cancelPoll();
        //查询主席
        AttendeeSmc2 masterAttendeeOld = conferenceContext.getMasterAttendee();
        if (targetAttendee == conferenceContext.getMasterAttendee()) {
            oldMasterAttendee = conferenceContext.getMasterAttendee();
        }
        if (masterAttendeeOld != null) {

            //释放原来的主席
            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
            Integer resultCode = conferenceServiceEx.releaseConfChairEx(confId);
            if (resultCode == 0) {
                List<String> uri=new ArrayList<>();
                uri.add(masterAttendeeOld.getRemoteParty());
                mute(confId,uri,1);

                ConferenceState conferenceState = conferenceContext.getDetailConference().getConferenceState();
                conferenceContext.setMasterParticipant(null);
                String broadcastId = conferenceState.getBroadcastId();
                if(Objects.equals(broadcastId, masterAttendeeOld.getSmcParticipant().getId())){
                    conferenceState.setBroadcastId("");
                }
                Threads.sleep(1000);
            }
        }

        AttendeeSmc2 attendee = conferenceContext.getAttendeeById(targetAttendee.getId());
        SmcParitipantsStateRep.ContentDTO participant = attendee.getSmcParticipant();
        if (participant != null) {
            if (participant.getState().getOnline()) {
                //设置主席
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                Integer resultCode = conferenceServiceEx.requestConfChairEx(confId, participant.getGeneralParam().getUri());
                if (resultCode == 0) {
                    doLockOrUnlockVideoSource(participant.getGeneralParam().getUri(), confId, conferenceServiceEx, 1);
                    //广播
                    broadcast(participant.getGeneralParam().getUri(), confId);
                    //全体关闭麦克风
                    List<String> urls=new ArrayList<>();
                    List<AttendeeSmc2> attendees = conferenceContext.getAttendees();
                    for (AttendeeSmc2 attendeeSmc2 : attendees) {
                        urls.add(attendeeSmc2.getRemoteParty());

                    }
                    List<AttendeeSmc2> masterAttendees = conferenceContext.getMasterAttendees();
                    for (AttendeeSmc2 masterAttendee : masterAttendees) {
                        urls.add(masterAttendee.getRemoteParty());

                    }
                    Map<Long, List<AttendeeSmc2>> cascadeAttendeesMap = conferenceContext.getCascadeAttendeesMap();
                    for (List<AttendeeSmc2> value : cascadeAttendeesMap.values()) {
                        for (AttendeeSmc2 attendeeSmc2 : value) {
                            urls.add(attendeeSmc2.getRemoteParty());
                        }

                    }
                    mute(confId,urls,1);
                    urls.clear();

                    if (oldMasterAttendee != null && oldMasterAttendee != targetAttendee) {
                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldMasterAttendee);
                        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldMasterAttendee.getUpdateMap());

                    }

                    if (oldMasterAttendee != targetAttendee) {
                        Map<String, Object> data = new HashMap<>(2);
                        data.put("oldMasterAttendee", oldMasterAttendee);
                        data.put("newMasterAttendee", targetAttendee);
                        Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                        if (defaultChooseSeeAttendee != null) {
                            Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
                        }

                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场已切换至【").append(targetAttendee.getName()).append("】");
                        Smc2WebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    }
                    setMaster(attendee);
                    if(defaultChooseSeeAttendee!=null){
                        //选看
                        Integer errorCode = conferenceServiceEx.setVideoSourceEx(confId, participant.getGeneralParam().getUri(), defaultChooseSeeAttendee.getRemoteParty(), 0);
                        if (errorCode != 0) {
                            logger.error("主席设置选看失败" + errorCode);
                        } else {
                            AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
                        }
                    }
                    doLockOrUnlockVideoSource(participant.getGeneralParam().getUri(), confId, conferenceServiceEx, 0);
                    urls.add(targetAttendee.getRemoteParty());
                    mute(confId,urls,0);
                } else {
                    logger.info("设置主席失败："+Smc2ErrorCode.getReasonByCode(resultCode) + resultCode);
                    if(resultCode == Smc2ErrorCode.INVALID_OPERATION_CHAIR.getCode()){
                        while (true){
                            try {
                                changMasterProcess();
                                if(conferenceContext.getMasterAttendee()!=null){
                                    break;
                                }
                            } catch (Exception e) {
                                break;
                            }
                        }

                    }else {
                        throw new CustomException("设置主席失败："+Smc2ErrorCode.getReasonByCode(resultCode) + resultCode);
                    }

                }

            }
        }


    }

    private void setMaster(AttendeeSmc2 participant) {
        conferenceContext.getDetailConference().getConferenceState().setChairmanId(targetAttendee.getParticipantUuid());
        ParticipantRspDto participantRspDto = OngoingConfNotificationExMessage.setMasterParticipant(conferenceContext, participant);
        conferenceContext.getDetailConference().getConferenceState().setChairman(participantRspDto);
        conferenceContext.setMasterAttendee(targetAttendee);
    }

    private void broadcast(String uri, String confId) {
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode = conferenceServiceEx.setBroadcastSiteEx(confId, uri, 0);
        if (resultCode != 0) {
            logger.error(uri + "广播主席失败：" + resultCode);
        }
    }

    /**
     * 指定会场闭音
     * @param confId
     * @param siteUrls
     * @param isMute  0 不闭  1闭音
     */
    private void mute(String confId,List<String> siteUrls,Integer isMute) {
        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        Integer resultCode =  conferenceServiceEx.setSitesMuteEx(confId,siteUrls,isMute);
        if (resultCode != 0) {
            logger.error( "开/闭麦操作：" + resultCode+"操作："+isMute.intValue());
        }
    }

    /**
     * 解锁视频源
     *
     * @param uri
     * @param confId
     * @param conferenceServiceEx
     * @param lock                1解锁 0 锁定
     */
    private void doLockOrUnlockVideoSource(String uri, String confId, ConferenceServiceEx conferenceServiceEx, int lock) {
        List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParams = new ArrayList<>();
        WSCtrlSiteCommParamEx item1 = new WSCtrlSiteCommParamEx();
        //解锁
        item1.setOperaTypeParam(lock);
        item1.setSiteUri(uri);
        wsCtrlSiteCommParams.add(item1);
        int resultCode = conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParams);
        if (resultCode != 0) {
            logger.error(uri + "解锁失败：" + resultCode);
        }
    }

    @Override
    public void cancel() {
        if (targetAttendee != null) {
            String confId = conferenceContext.getSmc2conferenceId();
            AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
            if(masterAttendee!=null){
                List<String> uri=new ArrayList<>();
                uri.add(masterAttendee.getSmcParticipant().getGeneralParam().getUri());
                mute(confId,uri,1);
                //取消广播主席
                ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
                conferenceServiceEx.setBroadcastSiteEx(confId, masterAttendee.getSmcParticipant().getGeneralParam().getUri(), 1);
            }
            List<String> uri=new ArrayList<>();
            uri.add(targetAttendee.getSmcParticipant().getGeneralParam().getUri());
            mute(confId,uri,1);
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(targetAttendee);
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, targetAttendee.getUpdateMap());

            //释放原来的主席
//            ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
//            Integer resultCode = conferenceServiceEx.releaseConfChairEx(confId);
//            if(resultCode==0){
//                conferenceContext.setMasterParticipant(null);
//            }
            Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, targetAttendee);
            if (defaultChooseSeeAttendee != null) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
                Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
            }
            ThreadUtils.sleep(900);
        }


    }
}
