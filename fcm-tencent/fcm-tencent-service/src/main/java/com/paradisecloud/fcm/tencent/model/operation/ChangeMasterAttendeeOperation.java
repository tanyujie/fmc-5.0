package com.paradisecloud.fcm.tencent.model.operation;


import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.tencent.busi.AttendeeImportance;
import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.client.TencentLayoutClient;
import com.paradisecloud.fcm.tencent.model.reponse.AddMeetingLayoutReponse;
import com.paradisecloud.fcm.tencent.model.reponse.MeetingAdvancedLayoutResponse;
import com.paradisecloud.fcm.tencent.model.request.ModifyConferenceRequest;
import com.paradisecloud.fcm.tencent.model.request.MuteParticipantRequest;
import com.paradisecloud.fcm.tencent.model.request.layout.*;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.BaseResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.paradisecloud.fcm.tencent.model.operation.AttendeeTencentLayoutProcessor.processMapInBatches;

/**
 * @author nj
 * @date 2023/5/16 14:30
 */
public class ChangeMasterAttendeeOperation extends AttendeeOperation {

    private final Logger logger = LoggerFactory.getLogger(ChangeMasterAttendeeOperation.class);

    private volatile AttendeeTencent defaultChooseSeeAttendee = null;
    private AttendeeTencent targetAttendee;
    private AttendeeTencent oldMasterAttendee;

    protected ChangeMasterAttendeeOperation(TencentConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public ChangeMasterAttendeeOperation(TencentConferenceContext conferenceContext, AttendeeTencent master) {
        super(conferenceContext);
        this.targetAttendee = master;
        this.oldMasterAttendee=conferenceContext.getMasterAttendee();
    }


    @Override
    public void operate() {
        initTargetAttendees();

        changMasterProcess();
    }

    private void initTargetAttendees() {
        if (targetAttendee == null) {
            return;
        }
        for (AttendeeTencent attendee : new ArrayList<>(conferenceContext.getAttendees())) {
            if (attendee != null) {
                if (attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getId(), targetAttendee.getId())
                        && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
                    defaultChooseSeeAttendee = attendee;
                    return;
                }
            }
        }

        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeTencent> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeTencent attendee : attendees) {
                    if (attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getId(), targetAttendee.getId())
                            && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
                        defaultChooseSeeAttendee = attendee;
                        return;
                    }
                }
            }
        }

        for (AttendeeTencent attendee : conferenceContext.getMasterAttendees()) {
            if (attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getId(), targetAttendee.getId())
                    && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
                defaultChooseSeeAttendee = attendee;
                return;
            }
        }

        AttendeeTencent attendee = conferenceContext.getMasterAttendee();
        if (attendee != null && !Objects.equals(attendee.getId(), targetAttendee.getId())
                && attendee.isMeetingJoined() && attendee.getInstanceid() == 9 && !Objects.equals(attendee.getName(), "会议监控") && !Objects.equals("SMC-MONITOR", attendee.getName())) {
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
        if (conferenceContext == null || conferenceContext.isEnd()||targetAttendee==null) {
            return;
        }
        Integer instanceid = targetAttendee.getInstanceid();
        if(instanceid==null||instanceid!=9){
            throw new CustomException("暂不支持的终端类型");
        }
        //主席
        SmcParitipantsStateRep.ContentDTO participant = targetAttendee.getSmcParticipant();
        if (participant != null) {
            if (participant.getState().getOnline()) {
                //设置主席
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
                    conferenceContext.setMasterAttendee(targetAttendee);
                } catch (WemeetSdkException e) {
                    throw new CustomException("设置主会场失败" + e.getMessage());
                }

                MuteParticipantRequest request=new MuteParticipantRequest();

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

                //设置布局
                TencentLayoutClient layout_client = conferenceContext.getTencentBridge().getLAYOUT_CLIENT();
                //主席设置--
                {
                    String chairmanLayoutId = conferenceContext.getChairmanLayoutId();
                    if(Strings.isNotBlank(chairmanLayoutId)){


                            try {
                                QueryMeetingLayoutListAdvancedRequest advancedRequest = new QueryMeetingLayoutListAdvancedRequest();
                                advancedRequest.setInstanceid(1);
                                advancedRequest.setOperatorIdType(1);
                                advancedRequest.setMeetingId(conferenceContext.getMeetingId());
                                advancedRequest.setOperatorId(conferenceContext.getTencentUser());
                                MeetingAdvancedLayoutResponse meetingAdvancedLayoutResponse = layout_client.queryMeetingLayoutsAdvanced(advancedRequest);
                                List<MeetingAdvancedLayoutResponse.LayoutListDTO> layoutList = meetingAdvancedLayoutResponse.getLayoutList();

                                Optional<MeetingAdvancedLayoutResponse.LayoutListDTO> any = layoutList.stream().filter(p -> Objects.equals(p.getLayoutId(), chairmanLayoutId)).findAny();
                                if(!any.isPresent()){
                                    AddMeetingLayoutRequest meetingLayoutRequest = addLayout(participant, 1);
                                    AddMeetingLayoutReponse meetingLayoutReponse = layout_client.addMeetingLayout(meetingLayoutRequest);
                                    String layoutId = meetingLayoutReponse.getLayoutList().get(0).getLayoutId();
                                    conferenceContext.setChairmanLayoutId(layoutId);
                                }else {
                                    ChangeMeetingLayoutRequest changeMeetingLayoutRequest = new ChangeMeetingLayoutRequest();
                                    changeMeetingLayoutRequest.setLayoutId(chairmanLayoutId);
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
                                    userSeatListDTO.setMsOpenId(participant.getMs_open_id());
                                    userSeatListDTO.setUsername(participant.getNick_name());
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
                            AddMeetingLayoutRequest meetingLayoutRequest = addLayout(participant, 1);
                            AddMeetingLayoutReponse meetingLayoutReponse = layout_client.addMeetingLayout(meetingLayoutRequest);
                            String layoutId = meetingLayoutReponse.getLayoutList().get(0).getLayoutId();
                            conferenceContext.setChairmanLayoutId(layoutId);
                        } catch (WemeetSdkException e) {
                            logger.info(e.getMessage());
                        }
                    }
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
                    processMapInBatches(attendeeMap,  (p) -> {
                        List<ApplyingLayoutRequest.UserListMsopenIdDto> user_list=new ArrayList<>();
                        for (AttendeeTencent value : p.values()) {
                            if(value.isMeetingJoined()&&value.getInstanceid()==9&&(!Objects.equals(value.getId(),targetAttendee.getId()))){
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
                    if(defaultChooseSeeAttendee!=null){
                        if(Strings.isNotBlank(chooseLayoutId)){
                            try {

                                QueryMeetingLayoutListAdvancedRequest advancedRequest = new QueryMeetingLayoutListAdvancedRequest();
                                advancedRequest.setInstanceid(1);
                                advancedRequest.setOperatorIdType(1);
                                advancedRequest.setMeetingId(conferenceContext.getMeetingId());
                                advancedRequest.setOperatorId(conferenceContext.getTencentUser());
                                MeetingAdvancedLayoutResponse meetingAdvancedLayoutResponse = layout_client.queryMeetingLayoutsAdvanced(advancedRequest);
                                List<MeetingAdvancedLayoutResponse.LayoutListDTO> layoutList = meetingAdvancedLayoutResponse.getLayoutList();

                                Optional<MeetingAdvancedLayoutResponse.LayoutListDTO> any = layoutList.stream().filter(p -> Objects.equals(p.getLayoutId(), chooseLayoutId)).findAny();
                                if(!any.isPresent()){
                                    AddMeetingLayoutRequest meetingLayoutRequest2 = addLayout(defaultChooseSeeAttendee.getSmcParticipant(), 1);
                                    AddMeetingLayoutReponse meetingLayoutReponse2 = layout_client.addMeetingLayout(meetingLayoutRequest2);
                                    String layoutId2 = meetingLayoutReponse2.getLayoutList().get(0).getLayoutId();
                                    conferenceContext.setChooseLayoutId(layoutId2);
                                }else {
                                    ChangeMeetingLayoutRequest changeMeetingLayoutRequest = new ChangeMeetingLayoutRequest();
                                    changeMeetingLayoutRequest.setLayoutId(chooseLayoutId);
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
                                    userSeatListDTO.setMsOpenId(defaultChooseSeeAttendee.getMs_open_id());
                                    userSeatListDTO.setUsername(defaultChooseSeeAttendee.getNickName());
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
                                AddMeetingLayoutRequest meetingLayoutRequest2 = addLayout(defaultChooseSeeAttendee.getSmcParticipant(), 1);
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
                        userListMsopenIdDto2.setInstanceid(targetAttendee.getInstanceid());
                        userListMsopenIdDto2.setMs_open_id(targetAttendee.getMs_open_id());
                        user_list2.add(userListMsopenIdDto2);
                        applyingLayoutRequest2.setUser_list(user_list2);
                        try {
                           layout_client.applyingLayout(applyingLayoutRequest2);
                        } catch (WemeetSdkException e) {
                            logger.info(e.getMessage());
                        }

                    }

                }


                    if (oldMasterAttendee != null && oldMasterAttendee != targetAttendee) {
                        AttendeeImportance.COMMON.processAttendeeWebsocketMessage(oldMasterAttendee);
                        TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, oldMasterAttendee.getUpdateMap());

                    }

                    if (oldMasterAttendee != targetAttendee) {
                        Map<String, Object> data = new HashMap<>(2);
                        if(oldMasterAttendee!=null){
                            data.put("oldMasterAttendee", oldMasterAttendee);
                        }
                        data.put("newMasterAttendee", targetAttendee);
                        TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                        if (defaultChooseSeeAttendee != null) {
                            TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
                        }

                        StringBuilder messageTip = new StringBuilder();
                        messageTip.append("主会场已切换至【").append(targetAttendee.getName()).append("】");
                        TencentWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                    }

                     if(defaultChooseSeeAttendee!=null){
                         AttendeeImportance.CHOOSE_SEE.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
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

    private AddMeetingAdancedLayoutRequest addAdvancedLayout(SmcParitipantsStateRep.ContentDTO participant) {
        AddMeetingAdancedLayoutRequest request = new AddMeetingAdancedLayoutRequest();
        request.setMeetingId(conferenceContext.getMeetingId());
        request.setInstanceid(1);
        request.setOperatorIdType(4);
        request.setOperatorId(conferenceContext.getTencentUser());
        List<AddMeetingAdancedLayoutRequest.LayoutListDTO> layoutList = new ArrayList<>();
        request.setLayoutList(layoutList);
        AddMeetingAdancedLayoutRequest.LayoutListDTO layoutListDTO = new AddMeetingAdancedLayoutRequest.LayoutListDTO();
        layoutList.add(layoutListDTO);
        List<AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO> pageList = new ArrayList<>();
        AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO pageListDTO = new AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO();
        pageList.add(pageListDTO);
        pageListDTO.setLayoutTemplateId("1");
        List<AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO> userSeatList = new ArrayList<>();
        pageListDTO.setUserSeatList(userSeatList);
        AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO userSeatListDTO = new AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO();
        userSeatList.add(userSeatListDTO);
        userSeatListDTO.setGridId("1");
        userSeatListDTO.setGridType(1);
        //   userSeatListDTO.setVideoType(3);
        List<AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO.UserListDTO> userList = new ArrayList<>();
        AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO.UserListDTO userListDTO = new AddMeetingAdancedLayoutRequest.LayoutListDTO.PageListDTO.UserSeatListDTO.UserListDTO();
        userListDTO.setMs_open_id(participant.getMs_open_id());
        userList.add(userListDTO);
        //    userSeatListDTO.setUserList(userList);
        layoutListDTO.setPageList(pageList);
        return request;
    }

    @Override
    public void cancel() {
        if (defaultChooseSeeAttendee != null) {
            AttendeeImportance.COMMON.processAttendeeWebsocketMessage(defaultChooseSeeAttendee);
            TencentWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, defaultChooseSeeAttendee);
        }
    }

    public AttendeeTencent getDefaultChooseSeeAttendee() {
        return defaultChooseSeeAttendee;
    }

    public void setDefaultChooseSeeAttendee(AttendeeTencent defaultChooseSeeAttendee) {
        this.defaultChooseSeeAttendee = defaultChooseSeeAttendee;
    }
}
