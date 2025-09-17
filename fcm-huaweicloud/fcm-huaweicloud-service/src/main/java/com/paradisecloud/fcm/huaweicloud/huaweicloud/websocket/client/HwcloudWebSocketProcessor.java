package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sdk.meeting.v1.model.PicInfoNotify;
import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.fme.model.busi.layout.ImportanceProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeCountingStatistics;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.AttendeeImportance;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.CropDirAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContextCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.event.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.*;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.ConfDynamicInfoNotifyMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.ErrorCode;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.InviteResultNotifyMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.message.ParticipantsNotifyMessage;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.impls.BusiHwcloudConferenceServiceImpl;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiHwcloudConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudHistoryConferenceService;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess.AttendeeMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess.SelfCallAttendeeHwcloudProcessor;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.utils.AttendeeUtils;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.interfaces.IHwcloudWebSocketService;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author nj
 * @date 2022/8/22 9:42
 */
public class HwcloudWebSocketProcessor {

    public static final String COHOST = "1";
    private final Logger logger = LoggerFactory.getLogger(HwcloudWebSocketProcessor.class);
    private final HwcloudMeetingWebsocketClient websocketClient;
    private final HwcloudMeetingBridge hwcloudMeetingBridge;

    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public HwcloudWebSocketProcessor(HwcloudMeetingWebsocketClient websocketClient, HwcloudMeetingBridge hwcloudMeetingBridge, IHwcloudWebSocketService webSocketService) {
        this.websocketClient = websocketClient;
        this.hwcloudMeetingBridge = hwcloudMeetingBridge;
    }


    public HwcloudMeetingBridge getSmcBridge() {
        return hwcloudMeetingBridge;
    }


    public void processMessage(String message) {

        logger.info("======>==================HWCLOUD MESSAGE============>>>>>>>>>>>>>>>>>>>>>>>>>>>>========================================");
        logger.info(message);
        logger.info("<message><<<<<<<<<<<<<<<<<<<=HWCLOUD MESSAGE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");


        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject updateItem = JSONObject.parseObject(message);
                if (updateItem != null) {
//                    String msgID = (String) jsonObj.get("msgID");
//                    HwcloudMeetingProcessorMessageQueue.getInstance().put(new HwcloudMeetingMessage(jsonObj, msgID));

                    HwcloudConferenceContext hwcloudConferenceContext=null;
                    try {
                        String action = (String) updateItem.get("action");
                        String confID = (String) updateItem.get("confID");
                        String msgID = (String) updateItem.get("msgID");
                        Integer msgMode = (Integer) updateItem.get("msgMode");
                        if(Strings.isNotBlank(confID)){
                             hwcloudConferenceContext = HwcloudConferenceContextCache.getInstance().get(confID);
                        }

                        SubscriptionTypeEnum subscriptionType = SubscriptionTypeEnum.valueOf(action);
                        switch (subscriptionType) {
                            case CustomMultiPicNotify:
                                break;
                            case ConfBasicInfoNotify:
                                // 处理会议基本信息订阅的情况
                                ConfBasicInfoNotify confBasicInfoNotify = JSONObject.parseObject(updateItem.toJSONString(), ConfBasicInfoNotify.class);
                                if (confBasicInfoNotify != null&&hwcloudConferenceContext!=null) {

                                    Integer callInRestriction = confBasicInfoNotify.getCallInRestriction();
                                    hwcloudConferenceContext.setCallInRestriction(callInRestriction);
                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put("callInRestriction",callInRestriction);
                                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, updateMap);

                                }
                                break;
                            case ConfDynamicInfoNotify:
                                // 处理会议状态信息订阅的情况
                                ConfDynamicInfoNotifyMessage confDynamicInfoNotifyMessage = JSONObject.parseObject(updateItem.toJSONString(), ConfDynamicInfoNotifyMessage.class);
                                if (confDynamicInfoNotifyMessage != null&&hwcloudConferenceContext!=null) {
                                    hwcloudConferenceContext.setLocked(confDynamicInfoNotifyMessage.getLock() == 1);
                                    hwcloudConferenceContext.setAllowUnmuteBySelf(confDynamicInfoNotifyMessage.getCanUnmute() == 1);
                                    hwcloudConferenceContext.setAllowChat(confDynamicInfoNotifyMessage.getForbiddenChat());
                                    hwcloudConferenceContext.setLockPresenterStatus(confDynamicInfoNotifyMessage.getLockSharing() == 1);
                                    hwcloudConferenceContext.setMultiPicInfoNotify(confDynamicInfoNotifyMessage.getMultiPic());

                                    Integer vas = confDynamicInfoNotifyMessage.getVas();
                                    hwcloudConferenceContext.setVas(vas);



//                                    MultiPicInfoNotify multiPic = confDynamicInfoNotifyMessage.getMultiPic();
//                                    MultiPicInfoReq multiPicInfoReq=new MultiPicInfoReq();
//                                    multiPicInfoReq.setSwitchTime(multiPic.getPeriod());
//                                    MultiPicInfoReq.MultiPicInfoDTO multiPicInfo=new MultiPicInfoReq.MultiPicInfoDTO();
//                                    multiPicInfoReq.setMultiPicInfo(multiPicInfo);
//                                    multiPicInfo.setPicNum(multiPic.getPicNum());
//                                    multiPicInfo.setSubPicList();
//                                    hwcloudConferenceContext.setMultiPicInfoReq();
                                    hwcloudConferenceContext.setLockSharing(confDynamicInfoNotifyMessage.getLockSharing());
                                    MultiPicInfoNotify multiPic = confDynamicInfoNotifyMessage.getMultiPic();
                                    if(multiPic!=null){

                                        PresetMultiPicReqDto presetMultiPicReqDto = new PresetMultiPicReqDto();
                                        presetMultiPicReqDto.setPicNum(multiPic.getPicNum());
                                        presetMultiPicReqDto.setSwitchTime(multiPic.getPeriod());

                                        presetMultiPicReqDto.setAutoEffect(true);
                                        List<PresetMultiPicReqDto.PresetMultiPicRollsDTO> subPicPollInfoList=new ArrayList<>();
                                        presetMultiPicReqDto.setSubPicPollInfoList(subPicPollInfoList);


                                        List<PicInfoNotify> picInfos = multiPic.getPicInfos();
                                        if(CollectionUtils.isNotEmpty(picInfos)){
                                            for (PicInfoNotify picInfo : picInfos) {
                                                PresetMultiPicReqDto.PresetMultiPicRollsDTO presetMultiPicRollsDTO = new PresetMultiPicReqDto.PresetMultiPicRollsDTO();


                                            }
                                        }

                                    }






                                    String state = confDynamicInfoNotifyMessage.getState();
                                    if(Objects.equals(state, ConferenceStateEnum.Destroyed.name())){
                                        BeanFactory.getBean(IBusiHwcloudConferenceService.class).endConference(hwcloudConferenceContext.getId());
                                    }
                                    String mode = confDynamicInfoNotifyMessage.getMode();
                                    if(Objects.equals(ConferenceModeEnum.FIXED.name(),mode)){

                                        if(vas==1){
                                            hwcloudConferenceContext.setMultiPicBroadcastStatus(false);
                                        }
                                        if(vas==0){
                                            hwcloudConferenceContext.setMultiPicBroadcastStatus(true);
                                        }

                                    }else {
                                        hwcloudConferenceContext.setMultiPicBroadcastStatus(false);
                                    }

                                    Integer callInRestriction = confDynamicInfoNotifyMessage.getCallInRestriction();
                                    hwcloudConferenceContext.setCallInRestriction(callInRestriction);


                                    Map<String, Object> updateMap = new HashMap<>();
                                    updateMap.put("locked", confDynamicInfoNotifyMessage.getLock() == 1);
                                    updateMap.put("allowUnmuteBySelf", confDynamicInfoNotifyMessage.getCanUnmute() == 1);
                                    updateMap.put("allowChat", confDynamicInfoNotifyMessage.getForbiddenChat());
                                    updateMap.put("lockPresenterStatus", confDynamicInfoNotifyMessage.getLockSharing() == 1);
                                    updateMap.put("multiPicInfoNotify", confDynamicInfoNotifyMessage.getMultiPic());
                                    updateMap.put("vas", confDynamicInfoNotifyMessage.getVas());
                                    updateMap.put("lockSharing", confDynamicInfoNotifyMessage.getLockSharing());
                                    updateMap.put("multiPicBroadcastStatus", hwcloudConferenceContext.getMultiPicBroadcastStatus());
                                    updateMap.put("callInRestriction",callInRestriction);

                                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, updateMap);

                                }
                                break;
                            case ParticipantsNotify:
                                //在线与会者信息推送
                                ParticipantsNotifyMessage participantsNotifyMessage = JSONObject.parseObject(updateItem.toJSONString(), ParticipantsNotifyMessage.class);

                                List<ParticipantsNotifyMessage.DataDTO> dataDTOList = participantsNotifyMessage.getData();
                                for (ParticipantsNotifyMessage.DataDTO dataDTO : dataDTOList) {
                                    //刷新
                                    if (dataDTO.getMode() == 0) {

                                        String state = dataDTO.getPinfoMap().getState();
                                        if (Objects.equals(state, "1")) {
                                            continue;
                                        }
                                        if(msgMode==0){
                                            SmcParitipantsStateRep.ContentDTO contentDTO = initContent(dataDTO);
                                            SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();

                                            if (hwcloudConferenceContext != null) {

                                                AttendeeHwcloud a = AttendeeUtils.matchAttendee(hwcloudConferenceContext, contentDTO);
                                                if (a != null) {
                                                    processParticipants(hwcloudConferenceContext, contentDTO, generalParam, a);
                                                } else {
                                                    new SelfCallAttendeeHwcloudProcessor(contentDTO, hwcloudConferenceContext).process();
                                                }
                                            }
                                        }else {
                                            AttendeeHwcloud attendeeHwcloud = hwcloudConferenceContext.getParticipantAttendeeAllMap().get(dataDTO.getPid());
                                            if(attendeeHwcloud!=null){
                                                SmcParitipantsStateRep.ContentDTO contentDTO = updateContent(attendeeHwcloud.getSmcParticipant(),dataDTO);
                                                processParticipants(hwcloudConferenceContext, contentDTO, attendeeHwcloud.getSmcParticipant().getGeneralParam(), attendeeHwcloud);
                                            }else {
                                                SmcParitipantsStateRep.ContentDTO contentDTO = initContent(dataDTO);
                                                SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();

                                                if (hwcloudConferenceContext != null) {

                                                    AttendeeHwcloud a = AttendeeUtils.matchAttendee(hwcloudConferenceContext, contentDTO);
                                                    if (a != null) {
                                                        processParticipants(hwcloudConferenceContext, contentDTO, generalParam, a);
                                                    } else {
                                                        new SelfCallAttendeeHwcloudProcessor(contentDTO, hwcloudConferenceContext).process();
                                                    }
                                                }
                                            }
                                        }

                                        JSONObject jsonObjectjs = new JSONObject();
                                        jsonObjectjs.put("attendeeCountingStatistics", new AttendeeCountingStatistics(hwcloudConferenceContext));
                                        HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObjectjs);
                                    } else {
                                        AttendeeHwcloud attendeeHwcloud = hwcloudConferenceContext.getParticipantAttendeeAllMap().get(dataDTO.getPid());
                                        if (attendeeHwcloud != null) {

                                            if(attendeeHwcloud instanceof CropDirAttendeeHwcloud){
                                                int meetingStatus = attendeeHwcloud.getMeetingStatus();
                                                if (meetingStatus == AttendeeMeetingStatus.IN.getValue()) {
                                                    attendeeHwcloud.leaveMeeting();
                                                }
                                                // 消息推送
                                                AttendeeMessagePusher.getInstance().pushMixingStatusMessage(attendeeHwcloud, hwcloudConferenceContext);
                                                AttendeeMessagePusher.getInstance().pushOnlineMessage(attendeeHwcloud, hwcloudConferenceContext);
                                                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeHwcloud);
                                            }else {
                                                AttendeeHwcloud attendee = hwcloudConferenceContext.removeAttendeeById(attendeeHwcloud.getId());
                                                if (attendee != null) {
                                                    HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.ATTENDEE_DELETE, attendee);
                                                }
                                                attendee.leaveMeeting();
                                                // 从缓存中移除
                                                Map<String, Object> updateMap = new HashMap<>();
                                                updateMap.put("id", attendee.getId());
                                                updateMap.put("deptId", attendee.getDeptId());
                                                updateMap.put("mcuAttendee", attendee.isMcuAttendee());
                                                HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                                                String reason = "【" + attendee.getName() + "】离会";
                                                HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                                                if (attendee == hwcloudConferenceContext.getMasterAttendee()) {
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("oldMasterAttendee", attendee);
                                                    data.put("newMasterAttendee", null);
                                                    HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(hwcloudConferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

                                                    StringBuilder messageTip = new StringBuilder();
                                                    messageTip.append("主会场已离会【").append(attendee.getName()).append("】");
                                                    HwcloudWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(hwcloudConferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                                                    hwcloudConferenceContext.clearMasterAttendee();
                                                }

                                                processUpdateParticipant(hwcloudConferenceContext, attendee, false);
                                                JSONObject jsonObjectjs = new JSONObject();
                                                jsonObjectjs.put("attendeeCountingStatistics", new AttendeeCountingStatistics(hwcloudConferenceContext));
                                                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObjectjs);
                                            }



                                        }

                                    }


                                }
                                break;
                            case InviteResultNotify:
                                // 邀请结果信息推送
                                InviteResultNotifyMessage inviteResultNotifyMessage = JSONObject.parseObject(updateItem.toJSONString(), InviteResultNotifyMessage.class);
                                List<InviteResultNotifyMessage.DataDTO> data = inviteResultNotifyMessage.getData();
                                for (InviteResultNotifyMessage.DataDTO datum : data) {
                                    String callNumber = datum.getCallNumber();
                                    String resultCode = datum.getResultCode();
                                    if(Strings.isNotBlank(resultCode)){
                                        if (Objects.equals(resultCode, "0")) {
                                            HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.MESSAGE_TIP, datum.getCallNumber() + "呼叫成功");

                                        } else {
                                            ErrorCode errorCode = ErrorCode.valueOf(resultCode);
                                            if (errorCode != null) {
                                                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.MESSAGE_TIP, datum.getCallNumber() + errorCode.getDescription());
                                            } else {
                                                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.MESSAGE_TIP, datum.getCallNumber() + "呼叫失败");
                                            }
                                        }
                                    }

                                }

                                break;
                            case NetConditionNotify:
                                // 会议媒体质量 推送
                                NetConditionNotifyDTO netConditionNotifyDTO = JSONObject.parseObject(updateItem.toJSONString(), NetConditionNotifyDTO.class);
                                List<NetConditionNotifyParticipant> netConditionNotifyParticipants = netConditionNotifyDTO.getData();
                                String confID1 = netConditionNotifyDTO.getConfID();
                                Map<String, List<NetConditionNotifyParticipant>> participantMap = NetConditionNotifyParticipantCache.getInstance().getParticipantMap();
                                if (msgMode == 0) {
                                    participantMap.put(confID1, netConditionNotifyParticipants);
                                } else {
                                    List<NetConditionNotifyParticipant> netConditionNotifyParticipants1 = participantMap.get(confID1);
                                    netConditionNotifyParticipants1.addAll(netConditionNotifyParticipants);
                                }
                                for (NetConditionNotifyParticipant netConditionNotifyParticipant : netConditionNotifyParticipants) {
                                    NetConditionNotifyParticipantCache.getInstance().put(netConditionNotifyParticipant.getParticipantID(), netConditionNotifyParticipant);
                                }
                                break;

                            case SpeakerChangeNotify:
                                SpeakerChangeNotify speakerChangeNotify = JSONObject.parseObject(updateItem.toJSONString(), SpeakerChangeNotify.class);
                                List<String> currentSpeakers = new ArrayList<>();
                                List<SpeakerChangeNotify.DataDTO> list = speakerChangeNotify.getData();
                                if (CollectionUtils.isNotEmpty(list)) {
                                    for (SpeakerChangeNotify.DataDTO dataDTO : list) {
                                        String pid = dataDTO.getPid();
                                        AttendeeHwcloud attendeeByPUuid = hwcloudConferenceContext.getAttendeeByPUuid(pid);
                                        if (attendeeByPUuid != null) {
                                            currentSpeakers.add(attendeeByPUuid.getId());
                                        }
                                    }
                                }

                                JSONObject jsonObject_sp = new JSONObject();
                                jsonObject_sp.put("conferenceId", hwcloudConferenceContext.getId());
                                jsonObject_sp.put("currentSpeakers", currentSpeakers);
                                HwcloudWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(hwcloudConferenceContext, WebsocketMessageType.CURRENT_SPEAKERS, jsonObject_sp);
                                break;

                            case WaitingListNotify:
                                WaitingListNotify waitingListNotify = JSONObject.parseObject(updateItem.toJSONString(), WaitingListNotify.class);

                                List<WaitingListNotify.DataDTO> dataList = waitingListNotify.getData();

                                for (WaitingListNotify.DataDTO dataDTO : dataList) {
                                    Integer mode = dataDTO.getMode();

                                    WaitingListNotify.DataDTO.PinfoMapDTO pinfoMap = dataDTO.getPinfoMap();


                                    if(mode==0){

                                        HashMap<String, Object> pinfo = WaitingParticipantCache.getInstance().get(dataDTO.getPid());

                                        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(pinfoMap));


                                        WaitingParticipantCache.getInstance().put(dataDTO.getPid(), entityChangeMap(pinfo,jsonObject));

                                        hwcloudConferenceContext.getWaitingParticipantMap().put(dataDTO.getPid(),entityChangeMap(pinfo,jsonObject));

                                    }else {
                                        WaitingParticipantCache.getInstance().remove(dataDTO.getPid());
                                        hwcloudConferenceContext.getWaitingParticipantMap().remove(dataDTO.getPid());
                                    }

                                }





                                break;
                            default:
                                break;
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
    }

 public  static  HashMap<String,Object> entityChangeMap(HashMap<String, Object> pinfo,JSONObject jsonObject){
            if(pinfo==null){
                pinfo  = new HashMap<>();
            }

     Set<String> keys = jsonObject.keySet();
     for (String key : keys) {
         Object o = jsonObject.get(key);
         if(o!=null){
             pinfo.put(key,o);
         }

     }
return pinfo;

 }


    private SmcParitipantsStateRep.ContentDTO updateContent(SmcParitipantsStateRep.ContentDTO contentDTO,ParticipantsNotifyMessage.DataDTO dataDTO) {

        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = contentDTO.getGeneralParam();
        ParticipantState participantState = contentDTO.getState();

        if (dataDTO.getPinfoMap() != null) {
            generalParam.setName(dataDTO.getPinfoMap().getName());

            if(dataDTO.getPinfoMap().getState()!=null){
                participantState.setOnline(Objects.equals("0", dataDTO.getPinfoMap().getState()));
            }

            if(dataDTO.getPinfoMap().getMute()!=null){
                participantState.setMute(Objects.equals("1", dataDTO.getPinfoMap().getMute()));
            }

            if(dataDTO.getPinfoMap().getCamerastate()!=null){
                participantState.setVideoMute(Objects.equals("0", dataDTO.getPinfoMap().getCamerastate()));
            }

            if(dataDTO.getPinfoMap().getBroadcast()!=null){
                participantState.setBroadcast(Objects.equals("1", dataDTO.getPinfoMap().getBroadcast()));
            }

            if(dataDTO.getPinfoMap().getRollcall()!=null){
                participantState.setRollcall(Objects.equals("1", dataDTO.getPinfoMap().getRollcall()));
            }

            if (dataDTO.getPinfoMap().getRole() != null) {

                Integer role = Integer.valueOf(dataDTO.getPinfoMap().getRole());
                //主持人
                if(role==1){
                    contentDTO.setUserRole(2);
                }
            }
            String isCohost = dataDTO.getPinfoMap().getIsCohost();
            if(Objects.equals(COHOST,isCohost)){
                contentDTO.setUserRole(6);
            }

            if(dataDTO.getPinfoMap().getHand()!=null){
                participantState.setRaise_hands_state(Objects.equals("1", dataDTO.getPinfoMap().getHand()));
            }
            if(dataDTO.getPinfoMap().getShare()!=null){
                participantState.setScreen_shared_state(Objects.equals("1", dataDTO.getPinfoMap().getShare()));
            }
            if(dataDTO.getPinfoMap().getUserAgent()!=null){
                contentDTO.setUserAgent(dataDTO.getPinfoMap().getUserAgent());
            }

            contentDTO.setAccountId(dataDTO.getPinfoMap().getAccount());
            if (dataDTO.getPinfoMap().getClientLoginType() != null) {
                contentDTO.setCallType(dataDTO.getPinfoMap().getClientLoginType());
            }

        }

        return contentDTO;
    }

    private SmcParitipantsStateRep.ContentDTO initContent(ParticipantsNotifyMessage.DataDTO dataDTO) {
        SmcParitipantsStateRep.ContentDTO contentDTO = new SmcParitipantsStateRep.ContentDTO();
        SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam = new SmcParitipantsStateRep.ContentDTO.GeneralParamDTO();
        ParticipantState participantState = new ParticipantState();

        generalParam.setId(dataDTO.getPid());
        participantState.setParticipantId(dataDTO.getPid());
        generalParam.setId(dataDTO.getPid());

        if (dataDTO.getPinfoMap() != null) {
            generalParam.setName(dataDTO.getPinfoMap().getName());
            contentDTO.setTel(dataDTO.getPinfoMap().getTel());
            if(dataDTO.getPinfoMap().getState()!=null){
                participantState.setOnline(Objects.equals("0", dataDTO.getPinfoMap().getState()));
            }

            if(dataDTO.getPinfoMap().getMute()!=null){
                participantState.setMute(Objects.equals("1", dataDTO.getPinfoMap().getMute()));
            }

            if(dataDTO.getPinfoMap().getCamerastate()!=null){
                participantState.setVideoMute(Objects.equals("0", dataDTO.getPinfoMap().getCamerastate()));
            }

            if(dataDTO.getPinfoMap().getBroadcast()!=null){
                participantState.setBroadcast(Objects.equals("1", dataDTO.getPinfoMap().getBroadcast()));
            }

            if(dataDTO.getPinfoMap().getRollcall()!=null){
                participantState.setRollcall(Objects.equals("1", dataDTO.getPinfoMap().getRollcall()));
            }

            if (dataDTO.getPinfoMap().getRole() != null) {

                Integer role = Integer.valueOf(dataDTO.getPinfoMap().getRole());
                //主持人
                if(role==1){
                    contentDTO.setUserRole(2);
                }else {
                    contentDTO.setUserRole(0);
                }
            }
            String isCohost = dataDTO.getPinfoMap().getIsCohost();
            if(Objects.equals(COHOST,isCohost)){
                contentDTO.setUserRole(6);
            }

            if(dataDTO.getPinfoMap().getHand()!=null){
                participantState.setRaise_hands_state(Objects.equals("1", dataDTO.getPinfoMap().getHand()));
            }
            if(dataDTO.getPinfoMap().getShare()!=null){
                participantState.setScreen_shared_state(Objects.equals("1", dataDTO.getPinfoMap().getShare()));
            }
            if(dataDTO.getPinfoMap().getUserAgent()!=null){
                contentDTO.setUserAgent(dataDTO.getPinfoMap().getUserAgent());
            }

            contentDTO.setAccountId(dataDTO.getPinfoMap().getAccount());

            if (dataDTO.getPinfoMap().getClientLoginType() != null) {
                contentDTO.setCallType(dataDTO.getPinfoMap().getClientLoginType());
            }

        }


        contentDTO.setState(participantState);
        contentDTO.setGeneralParam(generalParam);
        contentDTO.setTerminalOnline(true);



        return contentDTO;
    }

    private void processParticipants(HwcloudConferenceContext conferenceContext, SmcParitipantsStateRep.ContentDTO contentDTO, SmcParitipantsStateRep.ContentDTO.GeneralParamDTO generalParam, AttendeeHwcloud a) {
        conferenceContext.getParticipantAttendeeAllMap().put(generalParam.getId(), a);
        a.setConferenceNumber(conferenceContext.getConferenceNumber());
        a.setSmcParticipant(contentDTO);
        a.setParticipantUuid(generalParam.getId());
        a.setNumber(contentDTO.getTel());
        AttendeeUtils.updateByParticipant(conferenceContext, contentDTO, a);
        processUpdateParticipant(conferenceContext, a, false);
    }

    private void processUpdateParticipant(HwcloudConferenceContext conferenceContext, AttendeeHwcloud attendeeHwcloud, boolean updateMediaInfo) {
        IBusiMcuHwcloudHistoryConferenceService historyConferenceService = BeanFactory.getBean(IBusiMcuHwcloudHistoryConferenceService.class);
        historyConferenceService.updateBusiHistoryParticipant(conferenceContext, attendeeHwcloud, updateMediaInfo);
    }
}