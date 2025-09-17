/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DiscussAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai 
 * @since 2021-04-25 14:18
 * @version  V1.0
 */ 
package com.paradisecloud.smc3.busi.operation;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.smc3.busi.Smc3ConferenceContext;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.model.ConstAPI;
import com.paradisecloud.smc3.model.ParticipantStatusDto;
import com.paradisecloud.smc3.model.response.SmcParitipantsStateRep;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

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

    private volatile List<AttendeeSmc3> targetAttendees = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();

    private AttendeeOperation lastAttendeeOperation;
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-04-25 14:19 
     * @param conferenceContext 
     */
    public DiscussAttendeeOperation(Smc3ConferenceContext conferenceContext)
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
                List<AttendeeSmc3> as = conferenceContext.getCascadeAttendeesMap().get(conferenceContext.getMasterAttendee().getDeptId());
                if (as != null) {
                    for (AttendeeSmc3 attendee : new ArrayList<>(as)) {
                        if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                            targetAttendees.add(attendee);
                        }
                    }
                }
            }
        }

        for (AttendeeSmc3 attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
        }

        for (AttendeeSmc3 attendee : conferenceContext.getMasterAttendees()) {
            if (!checkedAttendeeIdSet.contains(attendee.getId())) {
                targetAttendees.add(attendee);
            }
            List<AttendeeSmc3> as = conferenceContext.getCascadeAttendeesMap().get(attendee.getDeptId());
            if (as != null) {
                for (AttendeeSmc3 a : new ArrayList<>(as)) {
                    if (!checkedAttendeeIdSet.contains(a.getId())) {
                        targetAttendees.add(a);
                    }
                }
            }
        }



        Set<String> idSet = new HashSet<>();

        if(!CollectionUtils.isEmpty(targetAttendees)){
            List<AttendeeSmc3> arrayList = new ArrayList<>();
            for (AttendeeSmc3 attendee : targetAttendees) {
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
        // 全体开麦克风
        List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
        for (AttendeeSmc3 targetAttendee : targetAttendees) {
            ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
            participantStatusDto.setIsMute(false);
            participantStatusDto.setId(targetAttendee.getSmcParticipant().getGeneralParam().getId());
        }
        Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
        String conferenceId = conferenceContext.getSmc3conferenceId();

        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

        conferenceContext.setDiscuss(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mode", "FREE_TALK");
        if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
            bridgesByDept.getSmcConferencesInvoker().conferencesControlCascade(conferenceId, jsonObject.toJSONString(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }else {
            bridgesByDept.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        }

        // 发送提示信息
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已进入讨论模式！");
        Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());

    }

    @Override
    public void cancel()
    {
        if (!conferenceContext.isEnd())
        {
            conferenceContext.setDiscuss(false);
            // 全体关闭麦克风
            // 全体开麦克风
            Smc3Bridge bridgesByDept = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            String smc3conferenceId = conferenceContext.getSmc3conferenceId();
            List<ParticipantStatusDto> participantStatusList=new ArrayList<>();
            for (AttendeeSmc3 targetAttendee : targetAttendees) {
                SmcParitipantsStateRep.ContentDTO teleParticipant = targetAttendee.getSmcParticipant();
                if(teleParticipant!=null){
                    ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                    participantStatusDto.setIsMute(true);
                    participantStatusDto.setId(teleParticipant.getGeneralParam().getId());
                    participantStatusList.add(participantStatusDto);

                }
            }
            if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }else {
                bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }

            participantStatusList=new ArrayList<>();
            AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
            if(masterAttendee!=null){
                SmcParitipantsStateRep.ContentDTO teleParticipant = masterAttendee.getSmcParticipant();
                if(teleParticipant!=null){
                    ParticipantStatusDto participantStatusDto = new ParticipantStatusDto();
                    participantStatusDto.setIsMute(false);
                    participantStatusDto.setId(teleParticipant.getGeneralParam().getId());
                    participantStatusList.add(participantStatusDto);

                    if(Objects.equals(ConstAPI.CASCADE,conferenceContext.getCategory())){
                        bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnlyCascade(smc3conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }else {
                        bridgesByDept.getSmcParticipantsInvoker().PATCHParticipantsOnly(smc3conferenceId,participantStatusList, bridgesByDept.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
                    }
                }
            }

            // 发送提示信息
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已退出讨论模式！");
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_DISCUSS, conferenceContext.isDiscuss());
        }

    }
    
}
