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
import com.paradisecloud.fcm.common.enumer.AttendeeChooseSeeStatus;
import com.paradisecloud.fcm.common.enumer.AttendeeMixingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.tencent.busi.AttendeeImportance;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentBridge;
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
 * <pre>选看与会者操作（主会场看选看者，分会场看主会场）</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-20 16:42
 */
public class ChooseToSeeAttendeeOperation extends AttendeeOperation {

    private final AttendeeTencent chooseSeeAttendee;
    private AttendeeTencent oldChooseSeeAttendee;

    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @param chooseSeeAttendee
     * @author lilinhai
     * @since 2021-02-22 13:47
     */
    public ChooseToSeeAttendeeOperation(TencentConferenceContext conferenceContext, AttendeeTencent chooseSeeAttendee) {
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

        if (chooseSeeAttendee.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
            return;
        }

        AttendeeOperation lastAttendeeOperation = conferenceContext.getLastAttendeeOperation();

        if (lastAttendeeOperation instanceof ChangeMasterAttendeeOperation) {
            ChangeMasterAttendeeOperation old = (ChangeMasterAttendeeOperation) lastAttendeeOperation;
            oldChooseSeeAttendee = old.getDefaultChooseSeeAttendee();
        }

        if (lastAttendeeOperation instanceof ChooseToSeeAttendeeOperation) {
            ChooseToSeeAttendeeOperation old = (ChooseToSeeAttendeeOperation) lastAttendeeOperation;
            oldChooseSeeAttendee = old.chooseSeeAttendee;

        }


        AttendeeTencent masterAttendee = conferenceContext.getMasterAttendee();

        if (masterAttendee == null) {
            throw new CustomException("没有主会场,无法选看");
        }
//        if (chooseSeeAttendee == masterAttendee) {
//            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_ERROR, "主会场不能被选看！");
//            return;
//        }


        if (chooseSeeAttendee.isMeetingJoined()) {

            SmcParitipantsStateRep.ContentDTO masterParticipant = masterAttendee.getSmcParticipant();
            if (masterParticipant == null) {
                return;
            }
            SmcParitipantsStateRep.ContentDTO smcParticipantChoose = chooseSeeAttendee.getSmcParticipant();
            if (smcParticipantChoose == null) {
                return;
            }

            TencentBridge bridgesByDept = conferenceContext.getTencentBridge();
            TencentLayoutClient layout_client = bridgesByDept.getLAYOUT_CLIENT();

            chooseSeeAttendee.setChooseSeeStatus(AttendeeChooseSeeStatus.YES.getValue());

            //广播主席
            //主席设置--
            {

                //应用布局
                ApplyingLayoutRequest applyingLayoutRequest = new ApplyingLayoutRequest();
                applyingLayoutRequest.setLayout_id(conferenceContext.getChairmanLayoutId());
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
                processMapInBatches(attendeeMap, (p) -> {
                    List<ApplyingLayoutRequest.UserListMsopenIdDto> user_list = new ArrayList<>();
                    for (AttendeeTencent value : p.values()) {
                        if (value.isMeetingJoined() && value.getInstanceid() == 9 && (!Objects.equals(value.getId(), masterAttendee.getId()))) {
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


            //选看
            {
                String chooseLayoutId = conferenceContext.getChooseLayoutId();
                if (chooseSeeAttendee != null) {
                    if (Strings.isNotBlank(chooseLayoutId)) {
                        try {
                            QueryMeetingLayoutListAdvancedRequest advancedRequest = new QueryMeetingLayoutListAdvancedRequest();
                            advancedRequest.setInstanceid(1);
                            advancedRequest.setOperatorIdType(1);
                            advancedRequest.setMeetingId(conferenceContext.getMeetingId());
                            advancedRequest.setOperatorId(conferenceContext.getTencentUser());
                            MeetingAdvancedLayoutResponse meetingAdvancedLayoutResponse = layout_client.queryMeetingLayoutsAdvanced(advancedRequest);
                            List<MeetingAdvancedLayoutResponse.LayoutListDTO> layoutList = meetingAdvancedLayoutResponse.getLayoutList();

                            Optional<MeetingAdvancedLayoutResponse.LayoutListDTO> any = layoutList.stream().filter(p -> Objects.equals(p.getLayoutId(), chooseLayoutId)).findAny();
                            if (!any.isPresent()) {
                                AddMeetingLayoutRequest meetingLayoutRequest2 = addLayout(chooseSeeAttendee.getSmcParticipant(), 1);
                                AddMeetingLayoutReponse meetingLayoutReponse2 = layout_client.addMeetingLayout(meetingLayoutRequest2);
                                String layoutId2 = meetingLayoutReponse2.getLayoutList().get(0).getLayoutId();
                                conferenceContext.setChooseLayoutId(layoutId2);
                            } else {
                                ChangeMeetingLayoutRequest changeMeetingLayoutRequest = new ChangeMeetingLayoutRequest();
                                changeMeetingLayoutRequest.setLayoutId(chooseLayoutId);
                                changeMeetingLayoutRequest.setMeetingId(conferenceContext.getMeetingId());
                                changeMeetingLayoutRequest.setUserid(conferenceContext.getTencentUser());
                                changeMeetingLayoutRequest.setInstanceid(1);
                                List<ChangeMeetingLayoutRequest.PageListDTO> pageList = new ArrayList<>();
                                ChangeMeetingLayoutRequest.PageListDTO pageListDTO = new ChangeMeetingLayoutRequest.PageListDTO();
                                pageList.add(pageListDTO);
                                pageListDTO.setLayoutTemplateId("1");
                                List<ChangeMeetingLayoutRequest.PageListDTO.UserSeatListDTO> userSeatList = new ArrayList<>();
                                ChangeMeetingLayoutRequest.PageListDTO.UserSeatListDTO userSeatListDTO = new ChangeMeetingLayoutRequest.PageListDTO.UserSeatListDTO();
                                userSeatListDTO.setGridId("1");
                                userSeatListDTO.setGridType(1);
                                userSeatListDTO.setMsOpenId(chooseSeeAttendee.getMs_open_id());
                                userSeatListDTO.setUsername(chooseSeeAttendee.getNickName());
                                userSeatList.add(userSeatListDTO);
                                pageListDTO.setUserSeatList(userSeatList);
                                changeMeetingLayoutRequest.setPageList(pageList);
                                layout_client.changeMeetingLayout(changeMeetingLayoutRequest);
                            }


                        } catch (WemeetSdkException e) {
                            logger.info(e.getMessage());

                        }
                    } else {
                        try {
                            AddMeetingLayoutRequest meetingLayoutRequest2 = addLayout(chooseSeeAttendee.getSmcParticipant(), 1);
                            AddMeetingLayoutReponse meetingLayoutReponse2 = layout_client.addMeetingLayout(meetingLayoutRequest2);
                            String layoutId2 = meetingLayoutReponse2.getLayoutList().get(0).getLayoutId();
                            conferenceContext.setChooseLayoutId(layoutId2);

                        } catch (WemeetSdkException e) {
                            logger.info(e.getMessage());
                        }
                    }

                    //应用布局
                    ApplyingLayoutRequest applyingLayoutRequest2 = new ApplyingLayoutRequest();
                    applyingLayoutRequest2.setLayout_id(conferenceContext.getChooseLayoutId());
                    applyingLayoutRequest2.setMeetingId(conferenceContext.getMeetingId());
                    applyingLayoutRequest2.setInstanceid(1);
                    applyingLayoutRequest2.setOperatorId(conferenceContext.getTencentUser());
                    applyingLayoutRequest2.setOperatorIdType(1);

                    List<ApplyingLayoutRequest.UserListMsopenIdDto> user_list2 = new ArrayList<>();
                    ApplyingLayoutRequest.UserListMsopenIdDto userListMsopenIdDto2 = new ApplyingLayoutRequest.UserListMsopenIdDto();
                    userListMsopenIdDto2.setInstanceid(masterAttendee.getInstanceid());
                    userListMsopenIdDto2.setMs_open_id(masterAttendee.getMs_open_id());
                    user_list2.add(userListMsopenIdDto2);
                    applyingLayoutRequest2.setUser_list(user_list2);
                    try {
                        layout_client.applyingLayout(applyingLayoutRequest2);
                    } catch (WemeetSdkException e) {
                        logger.info(e.getMessage());
                    }

                }

            }

            //其它会场 静音
            //全体关闭麦克风
            masterAttendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
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
            masterAttendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());

            MuteParticipantRequest request = new MuteParticipantRequest();
            request.setMeetingId(conferenceContext.getMeetingId());
            request.setInstanceid(1);
            request.setMute(false);
            request.setOperatorIdType(4);
            request.setOperatorId(conferenceContext.getMsopenid());
            MuteParticipantRequest.UserDTO usersDTO = new MuteParticipantRequest.UserDTO();
            usersDTO.setUuid(masterParticipant.getUuid());
            usersDTO.setToOperatorId(masterParticipant.getMs_open_id());
            usersDTO.setToOperatorIdType(4);
            usersDTO.setInstanceid(masterParticipant.getInstanceid());
            request.setUser(usersDTO);

            try {
                conferenceCtrlClient.muteParticpant(request);
            } catch (WemeetSdkException e) {
                logger.info(e.getMessage());
                throw new CustomException(e.getMessage());
            }

            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, masterAttendee);

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
                TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldChooseSeeAttendee);

            }
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);

        }

    }


    @Override
    public void cancel() {
        if (chooseSeeAttendee != null) {

            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(chooseSeeAttendee);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, chooseSeeAttendee);


            if (CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
                for (AttendeeTencent attendee : conferenceContext.getAttendees()) {
                    pushMessage(attendee);
                }
            }
            for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                List<AttendeeTencent> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                if (attendees != null) {
                    for (AttendeeTencent attendee : attendees) {
                        pushMessage(attendee);
                    }
                }
            }

            for (AttendeeTencent attendee : conferenceContext.getMasterAttendees()) {
                pushMessage(attendee);
            }

        }
    }

    private void pushMessage(AttendeeTencent attendee) {
        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(attendee);
        if (attendee.getUpdateMap().size() > 1) {
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee);
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
