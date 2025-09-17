/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : CallTheRollOperation.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.operation
 * @author lilinhai
 * @since 2021-02-20 16:42
 * @version  V1.0
 */
package com.paradisecloud.fcm.tencent.model.operation;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.AttendeeCallTheRollStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.tencent.busi.AttendeeImportance;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentBridge;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.client.TencentLayoutClient;
import com.paradisecloud.fcm.tencent.model.reponse.AddMeetingLayoutReponse;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingAdvancedLayoutResponse;
import com.paradisecloud.fcm.tencent.model.request.ModifyConferenceRequest;
import com.paradisecloud.fcm.tencent.model.request.MuteParticipantRequest;
import com.paradisecloud.fcm.tencent.model.request.layout.AddMeetingLayoutRequest;
import com.paradisecloud.fcm.tencent.model.request.layout.ApplyingLayoutRequest;
import com.paradisecloud.fcm.tencent.model.request.layout.ChangeMeetingLayoutRequest;
import com.paradisecloud.fcm.tencent.model.request.layout.QueryMeetingLayoutListAdvancedRequest;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static com.paradisecloud.fcm.tencent.model.operation.AttendeeTencentLayoutProcessor.processMapInBatches;

/**
 * <pre>点名与会者操作</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class CallTheRollAttendeeOperation extends AttendeeOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 12:48
     */
    private static final long serialVersionUID = 1L;
    private AttendeeTencent callTheRollAttendee;



    public CallTheRollAttendeeOperation(TencentConferenceContext conferenceContext, AttendeeTencent attendee) {
        super(conferenceContext);
        this.callTheRollAttendee = attendee;
    }

    @Override
    public void operate() {
        AttendeeTencent masterAttendee = conferenceContext.getMasterAttendee();
        if (masterAttendee == null) {
            throw new CustomException("没有主会场,无法选看");
        }

        if (this.callTheRollAttendee.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
            return;
        }


        SmcParitipantsStateRep.ContentDTO TencentParticipant = callTheRollAttendee.getSmcParticipant();
        TencentBridge bridgesByDept = conferenceContext.getTencentBridge();
        TencentLayoutClient layout_client = bridgesByDept.getLAYOUT_CLIENT();

        //点名
        {
            String callTheRollLayoutId = conferenceContext.getCallTheRollLayoutId();
            if(callTheRollAttendee!=null){
                if(Strings.isNotBlank(callTheRollLayoutId)){
                    try {

                        QueryMeetingLayoutListAdvancedRequest advancedRequest = new QueryMeetingLayoutListAdvancedRequest();
                        advancedRequest.setInstanceid(1);
                        advancedRequest.setOperatorIdType(1);
                        advancedRequest.setMeetingId(conferenceContext.getMeetingId());
                        advancedRequest.setOperatorId(conferenceContext.getTencentUser());
                        MeetingAdvancedLayoutResponse meetingAdvancedLayoutResponse = layout_client.queryMeetingLayoutsAdvanced(advancedRequest);
                        List<MeetingAdvancedLayoutResponse.LayoutListDTO> layoutList = meetingAdvancedLayoutResponse.getLayoutList();

                        Optional<MeetingAdvancedLayoutResponse.LayoutListDTO> any = layoutList.stream().filter(p -> Objects.equals(p.getLayoutId(), callTheRollLayoutId)).findAny();
                        if(!any.isPresent()){
                            AddMeetingLayoutRequest meetingLayoutRequest2 = addLayout(callTheRollAttendee.getSmcParticipant(), 1);
                            AddMeetingLayoutReponse meetingLayoutReponse2 = layout_client.addMeetingLayout(meetingLayoutRequest2);
                            String layoutId2 = meetingLayoutReponse2.getLayoutList().get(0).getLayoutId();
                            conferenceContext.setCallTheRollLayoutId(layoutId2);
                        }else {
                            ChangeMeetingLayoutRequest changeMeetingLayoutRequest = new ChangeMeetingLayoutRequest();
                            changeMeetingLayoutRequest.setLayoutId(callTheRollLayoutId);
                            changeMeetingLayoutRequest.setMeetingId(conferenceContext.getMeetingId());
                            changeMeetingLayoutRequest.setUserid(conferenceContext.getTencentUser());
                            changeMeetingLayoutRequest.setInstanceid(1);
                            List<ChangeMeetingLayoutRequest.PageListDTO> pageList=new ArrayList<>();
                            ChangeMeetingLayoutRequest.PageListDTO pageListDTO = new ChangeMeetingLayoutRequest.PageListDTO();
                            pageList.add(pageListDTO);
                            pageListDTO.setLayoutTemplateId("1");
                            List<ChangeMeetingLayoutRequest.PageListDTO.UserSeatListDTO> userSeatList=new ArrayList<>();
                            ChangeMeetingLayoutRequest.PageListDTO.UserSeatListDTO userSeatListDTO = new ChangeMeetingLayoutRequest.PageListDTO.UserSeatListDTO();
                            userSeatListDTO.setGridId("1");
                            userSeatListDTO.setGridType(1);
                            userSeatListDTO.setMsOpenId(callTheRollAttendee.getMs_open_id());
                            userSeatListDTO.setUsername(callTheRollAttendee.getNickName());
                            userSeatList.add(userSeatListDTO);
                            pageListDTO.setUserSeatList(userSeatList);
                            changeMeetingLayoutRequest.setPageList(pageList);
                            layout_client.changeMeetingLayout(changeMeetingLayoutRequest);
                        }


                    } catch (WemeetSdkException e) {
                        logger.info(e.getMessage());

                    }
                }else {
                    try {
                        AddMeetingLayoutRequest meetingLayoutRequest2 = addLayout(callTheRollAttendee.getSmcParticipant(), 1);
                        AddMeetingLayoutReponse meetingLayoutReponse2 = layout_client.addMeetingLayout(meetingLayoutRequest2);
                        String layoutId2 = meetingLayoutReponse2.getLayoutList().get(0).getLayoutId();
                        conferenceContext.setCallTheRollLayoutId(layoutId2);

                    } catch (WemeetSdkException e) {
                        logger.info(e.getMessage());
                    }
                }

                //应用布局
                ApplyingLayoutRequest applyingLayoutRequest = new ApplyingLayoutRequest();
                applyingLayoutRequest.setLayout_id(conferenceContext.getCallTheRollLayoutId());
                applyingLayoutRequest.setMeetingId(conferenceContext.getMeetingId());
                applyingLayoutRequest.setInstanceid(1);
                applyingLayoutRequest.setOperatorId(conferenceContext.getTencentUser());
                applyingLayoutRequest.setOperatorIdType(1);


                Map<String, AttendeeTencent> attendeeMap = conferenceContext.getAttendeeMap();

                try {
                    layout_client.applyingLayout(applyingLayoutRequest);
                } catch (WemeetSdkException e) {
                    e.printStackTrace();
                }
                processMapInBatches(attendeeMap,  (p) -> {
                    List<ApplyingLayoutRequest.UserListMsopenIdDto> user_list=new ArrayList<>();
                    for (AttendeeTencent value : p.values()) {
                        if(value.isMeetingJoined()&&value.getInstanceid()==9&&(!Objects.equals(value.getId(),callTheRollAttendee.getId()))){
                            ApplyingLayoutRequest.UserListMsopenIdDto userListMsopenIdDto = new ApplyingLayoutRequest.UserListMsopenIdDto();
                            userListMsopenIdDto.setMs_open_id(value.getMs_open_id());
                            userListMsopenIdDto.setInstanceid(value.getInstanceid());
                            user_list.add(userListMsopenIdDto);
                        }
                    }
                    applyingLayoutRequest.setUser_list(user_list);
                    try {
                        layout_client.applyingLayout(applyingLayoutRequest);
                    } catch (WemeetSdkException e) {
                        throw new CustomException("设置主会场失败" + e.getMessage());
                    }

                }, 20);

            }

        }


        //全体关闭麦克风
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        ModifyConferenceRequest modifyConferenceRequest = new ModifyConferenceRequest();
        modifyConferenceRequest.setInstanceid(1);
        modifyConferenceRequest.setOperatorIdType(4);
        modifyConferenceRequest.setOperatorId(conferenceContext.getMsopenid());
        modifyConferenceRequest.setMuteAll(true);
        modifyConferenceRequest.setMeetingId(conferenceContext.getMeetingId());
        try {
            conferenceCtrlClient.modifyConferenceStatus(modifyConferenceRequest);
        } catch (WemeetSdkException e) {
            throw new CustomException("设置主会场失败" + e.getMessage());
        }

        //打开麦克风
        MuteParticipantRequest request=new MuteParticipantRequest();
        SmcParitipantsStateRep.ContentDTO participant = callTheRollAttendee.getSmcParticipant();
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setMute(false);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getMsopenid());
        MuteParticipantRequest.UserDTO usersDTO = new MuteParticipantRequest.UserDTO();
        usersDTO.setUuid(participant.getUuid());
        usersDTO.setToOperatorId(participant.getMs_open_id());
        usersDTO.setToOperatorIdType(4);
        usersDTO.setInstanceid(participant.getInstanceid());
        request.setUser(usersDTO);
        try {
            conferenceCtrlClient.muteParticpant(request);
        } catch (WemeetSdkException e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }


        //打开麦克风
        MuteParticipantRequest request2=new MuteParticipantRequest();
        request2.setMeetingId(conferenceContext.getMeetingId());
        request2.setInstanceid(1);
        request2.setMute(false);
        request2.setOperatorIdType(4);
        request2.setOperatorId(conferenceContext.getMsopenid());
        MuteParticipantRequest.UserDTO usersDTO2 = new MuteParticipantRequest.UserDTO();
        usersDTO2.setUuid(masterAttendee.getSmcParticipant().getUuid());
        usersDTO2.setToOperatorId(masterAttendee.getMs_open_id());
        usersDTO2.setToOperatorIdType(4);
        usersDTO2.setInstanceid(masterAttendee.getInstanceid());
        request2.setUser(usersDTO2);
        try {
            conferenceCtrlClient.muteParticpant(request2);
        } catch (WemeetSdkException e) {
            logger.info(e.getMessage());
            throw new CustomException(e.getMessage());
        }

        //被点名人
        {
            //应用布局
            ApplyingLayoutRequest applyingLayoutRequest2 = new ApplyingLayoutRequest();
            applyingLayoutRequest2.setLayout_id(conferenceContext.getChairmanLayoutId());
            applyingLayoutRequest2.setMeetingId(conferenceContext.getMeetingId());
            applyingLayoutRequest2.setInstanceid(1);
            applyingLayoutRequest2.setOperatorId(conferenceContext.getTencentUser());
            applyingLayoutRequest2.setOperatorIdType(1);

            List<ApplyingLayoutRequest.UserListMsopenIdDto> user_list2 = new ArrayList<>();
            ApplyingLayoutRequest.UserListMsopenIdDto userListMsopenIdDto2 = new ApplyingLayoutRequest.UserListMsopenIdDto();
            userListMsopenIdDto2.setInstanceid(callTheRollAttendee.getInstanceid());
            userListMsopenIdDto2.setMs_open_id(callTheRollAttendee.getMs_open_id());
            user_list2.add(userListMsopenIdDto2);
            applyingLayoutRequest2.setUser_list(user_list2);
            try {
                layout_client.applyingLayout(applyingLayoutRequest2);
            } catch (WemeetSdkException e) {
                logger.info(e.getMessage());
            }
        }


        if (callTheRollAttendee != null) {
            AttendeeImportance.POINT.processAttendeeWebsocketMessage(callTheRollAttendee);
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee);
        }

    }




    @Override
    public void cancel() {
        TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
        if (callTheRollAttendee != null) {
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(callTheRollAttendee);
            callTheRollAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, callTheRollAttendee);
            //取消混音
            SmcParitipantsStateRep.ContentDTO smcParticipant = callTheRollAttendee.getSmcParticipant();
            if (smcParticipant != null) {
                MuteParticipantRequest request=new MuteParticipantRequest();
                SmcParitipantsStateRep.ContentDTO participant = callTheRollAttendee.getSmcParticipant();
                request.setMeetingId(conferenceContext.getMeetingId());
                request.setInstanceid(1);
                request.setMute(true);
                request.setOperatorIdType(4);
                request.setOperatorId(conferenceContext.getMsopenid());
                MuteParticipantRequest.UserDTO usersDTO = new MuteParticipantRequest.UserDTO();
                usersDTO.setUuid(participant.getUuid());
                usersDTO.setToOperatorId(participant.getMs_open_id());
                usersDTO.setToOperatorIdType(4);
                usersDTO.setInstanceid(participant.getInstanceid());
                request.setUser(usersDTO);
                try {
                    conferenceCtrlClient.muteParticpant(request);
                } catch (WemeetSdkException e) {
                    logger.info(e.getMessage());
                    throw new CustomException(e.getMessage());
                }

            }

        }

    }


    private AddMeetingLayoutRequest addLayout(SmcParitipantsStateRep.ContentDTO participant, int order) {
        AddMeetingLayoutRequest request = new AddMeetingLayoutRequest();
        request.setInstanceid(1);
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setUserid(conferenceContext.getTencentUser());
        List<AddMeetingLayoutRequest.LayoutListDTO> layoutList = new ArrayList<>();
        request.setLayoutList(layoutList);
        AddMeetingLayoutRequest.LayoutListDTO layoutListDTO = new AddMeetingLayoutRequest.LayoutListDTO();
        layoutList.add(layoutListDTO);
        List<AddMeetingLayoutRequest.LayoutListDTO.PageListDTO> pageList = new ArrayList<>();
        AddMeetingLayoutRequest.LayoutListDTO.PageListDTO pageListDTO = new AddMeetingLayoutRequest.LayoutListDTO.PageListDTO();
        pageList.add(pageListDTO);
        pageListDTO.setLayoutTemplateId("1");
        List<AddMeetingLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO> userSeatList = new ArrayList<>();
        pageListDTO.setUserSeatList(userSeatList);
        AddMeetingLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO userSeatListDTO = new AddMeetingLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO();
        userSeatList.add(userSeatListDTO);
        userSeatListDTO.setGridId("1");
        userSeatListDTO.setGridType(1);
        userSeatListDTO.setMsOpenId(participant.getMs_open_id());
        userSeatListDTO.setUsername(participant.getNick_name());
        layoutListDTO.setPageList(pageList);
        request.setDefault_layout_order(order);
        return request;
    }
}
