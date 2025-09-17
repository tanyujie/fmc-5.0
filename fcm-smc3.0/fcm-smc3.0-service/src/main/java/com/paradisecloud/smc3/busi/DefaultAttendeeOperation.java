/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : DefaultChooseToSeeAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author lilinhai
 * @since 2021-02-22 18:16
 * @version  V1.0
 */
package com.paradisecloud.smc3.busi;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.common.utils.bean.BeanUtils;
import com.paradisecloud.fcm.common.enumer.PanePlacementSelfPaneMode;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.smc3.busi.attende.AttendeeSmc3;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.busi.enumer.AttendeeImportance;
import com.paradisecloud.smc3.busi.layout.CellScreen;
import com.paradisecloud.smc3.busi.operation.DefaultViewOperation;
import com.paradisecloud.smc3.busi.utils.Smc3WebSocketMessagePusher;
import com.paradisecloud.smc3.invoker.ConferenceState;
import com.paradisecloud.smc3.model.ChooseMultiPicInfo;
import com.paradisecloud.smc3.model.DetailConference;
import com.paradisecloud.smc3.model.PresetMultiPicReqDto;
import com.paradisecloud.smc3.model.SmcConferenceTemplate;
import com.paradisecloud.smc3.model.request.ConferenceStatusRequest;
import com.paradisecloud.smc3.model.request.MultiPicInfoReq;
import com.paradisecloud.smc3.service.interfaces.IBusiSmc3ConferenceService;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * <pre>会议室默认视图</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-02-22 18:16
 */
public class DefaultAttendeeOperation extends DefaultViewOperation {

    /**
     * <pre>用一句话描述这个变量的含义</pre>
     *
     * @since 2021-02-22 18:16
     */
    private static final long serialVersionUID = 1L;
    public static final int _INT = 10;
    private volatile List<JSONObject> defaultViewDepts = new ArrayList<>();

    private volatile List<AttendeeSmc3> targetAttendees = new ArrayList<>();
    private volatile Set<String> checkedAttendeeIdSet = new HashSet<>();

    private volatile JSONObject jsonObject;

    private volatile int count;
    /**
     * <pre>构造方法</pre>
     *
     * @param conferenceContext
     * @author lilinhai
     * @since 2021-04-12 17:48
     */
    public DefaultAttendeeOperation(Smc3ConferenceContext conferenceContext) {
        super(conferenceContext);
    }

    public DefaultAttendeeOperation(Smc3ConferenceContext conferenceContext,JSONObject jsonObject) {
        super(conferenceContext);
        this.jsonObject=jsonObject;
    }
    public DefaultAttendeeOperation(Smc3ConferenceContext conferenceContext,JSONObject jsonObject, int count) {
        super(conferenceContext);
        this.jsonObject=jsonObject;
        this.count=count;
    }


    public void initSplitScreen() {
        int maxImportance = YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.YES ? AttendeeImportance.BROADCAST.getEndValue() : AttendeeImportance.CHOOSE_SEE.getEndValue();

    }

    @Override
    public void operate() {
        doRun();
        while (count>0){
            doRun();
            count--;
        }
    }

    public void cancelBroadCast(){
        cancelBroadcast(conferenceContext.getSmc3Bridge(),conferenceContext.getSmc3conferenceId());
    }

    private void doRun() {
        try {
            Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(conferenceContext.getDeptId());
            String conferenceId = conferenceContext.getSmc3conferenceId();
            if(jsonObject==null){
                return;
            }

            initTargetAttendees();

            Boolean broadFlag=false;
            Boolean autoVoiceActive=false;
            MultiPicInfoReq parseObject= JSONObject.parseObject(JSONObject.toJSONString(jsonObject), MultiPicInfoReq.class);
            if(parseObject!=null&&parseObject.getMultiPicInfo()!=null){

                MultiPicInfoReq.MultiPicInfoDTO multiPicInfo1 = parseObject.getMultiPicInfo();
                broadFlag=parseObject.getBroadcast();
                if(Objects.equals(multiPicInfo1,conferenceContext.getMultiPicInfo())){
                    return;
                }
                if(parseObject.getMultiPicInfo()!=null){
                    createMultiPic(bridge, conferenceId, parseObject, multiPicInfo1);
                }
            }else  if(parseObject!=null&&parseObject.getMultiPicInfo()==null&&parseObject.getBroadcast()!=null){
                 broadFlag=parseObject.getBroadcast();
            } else {
                if(conferenceContext.getConfPresetParamDTO()!=null){
                    List<PresetMultiPicReqDto> presetMultiPics =conferenceContext.getConfPresetParamDTO().getPresetMultiPics();

                    for (PresetMultiPicReqDto presetMultiPic : presetMultiPics) {

                        Boolean autoEffect = presetMultiPic.getAutoEffect();
                        broadFlag=presetMultiPic.getAutoBroadCast();
                        autoVoiceActive = presetMultiPic.getAutoVoiceActive();
                        if(autoEffect){
                            MultiPicInfoReq multiPicInfo_Pre = new MultiPicInfoReq();
                            MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO = new MultiPicInfoReq.MultiPicInfoDTO();
                            multiPicInfoDTO.setPicNum(presetMultiPic.getPicNum());
                            multiPicInfoDTO.setMode(presetMultiPic.getMode());
                            List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList=new ArrayList<>();
                            List<PresetMultiPicReqDto.PresetMultiPicRollsDTO> presetMultiPicRolls = presetMultiPic.getPresetMultiPicRolls();
                            for (PresetMultiPicReqDto.PresetMultiPicRollsDTO presetMultiPicRoll : presetMultiPicRolls) {
                                List<PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO> subPicList1 = presetMultiPicRoll.getSubPicList();
                                for (PresetMultiPicReqDto.PresetMultiPicRollsDTO.SubPicListDTO subPicListDTO : subPicList1) {
                                    MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO1 = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
                                    subPicListDTO1.setStreamNumber(0);
                                    if(Strings.isNotBlank(subPicListDTO.getUri())){
                                        Map<String, AttendeeSmc3> stringAttendeeSmc3Map = conferenceContext.getRemotePartyAttendeesMap().get(subPicListDTO.getUri());
                                        if(stringAttendeeSmc3Map!=null){
                                            for (AttendeeSmc3 value : stringAttendeeSmc3Map.values()) {
                                                String participantUuid = value.getParticipantUuid();
                                                subPicListDTO1.setParticipantId(participantUuid);
                                                //subPicListDTO.setParticipantId(participantUuid);
                                            }
                                        }
                                    }
                                    subPicList.add(subPicListDTO1);
                                }
                            }
                            multiPicInfoDTO.setSubPicList(subPicList);
                            multiPicInfo_Pre.setMultiPicInfo(multiPicInfoDTO);
                            multiPicInfo_Pre.setConferenceId(conferenceId);
                            multiPicInfo_Pre.setBroadcast(presetMultiPic.getAutoBroadCast());
                            createMultiPic(bridge, conferenceId, multiPicInfo_Pre, multiPicInfo_Pre.getMultiPicInfo());
                        }
                    }

                }
            }






            //不广播 设置主席为多画面
            if (broadFlag) {
                broadcast(bridge, conferenceId);
            }else {
                cancelBroadcast(bridge, conferenceId);
            }

              if(autoVoiceActive!=null){
                  JSONObject jsonObject = new JSONObject();
                  jsonObject.put("isVoiceActive",autoVoiceActive);
                  bridge.getSmcConferencesInvoker().conferencesControl(conferenceContext.getSmc3conferenceId(), jsonObject.toJSONString(), bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());

              }


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Threads.sleep(1000);
        }
    }

     public void  broadcast(){
         broadcast(conferenceContext.getSmc3Bridge(),conferenceContext.getSmc3conferenceId());
    }

    private void broadcast(Smc3Bridge bridge, String conferenceId) {
        ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo2 = conferenceContext.getMultiPicInfo();
        if(multiPicInfo2==null){
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "多画面未设置不能广播");
            return ;
        }
        //开始广播
        broadcastStart(conferenceId, bridge,true);
        setMute(conferenceId, true, bridge);

        ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();

        List<ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String participantId = subPicListDTO.getParticipantId();
                if(Strings.isNotBlank(participantId)){
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(participantId);
                    if(attendeeBySmc3Id!=null){
                        AttendeeImportance.BROADCAST.processAttendeeWebsocketMessage(attendeeBySmc3Id);
                    }
                }

            }
        }

        conferenceContext.setMultiPicBroadcastStatus(true);
    }

    private void createMultiPic(Smc3Bridge bridge, String conferenceId, MultiPicInfoReq multiPicInfoReq, MultiPicInfoReq.MultiPicInfoDTO multiPicInfo1) {
        bridge.getSmcConferencesInvoker().createMulitiPic(conferenceId, multiPicInfoReq, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
        ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo2=new ConferenceState.StateDTO.MultiPicInfoDTO();
        BeanUtils.copyProperties(multiPicInfo1,multiPicInfo2);
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList1 = multiPicInfo1.getSubPicList();
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(subPicList1)){
            List<ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO> subPicList=new ArrayList<>();
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList1) {

                ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO subPicListDTO2 = new ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO();
                subPicListDTO2.setParticipantId(subPicListDTO.getParticipantId());
                subPicListDTO2.setStreamNumber(subPicListDTO.getStreamNumber());
                subPicList.add(subPicListDTO2);
            }
            multiPicInfo2.setSubPicList(subPicList);
        }

        conferenceContext.setMultiPicInfo(multiPicInfo2);
    }

    private void cancelBroadcast(Smc3Bridge bridge, String conferenceId) {
        if(conferenceContext.getMultiPicBroadcastStatus()!=null&&conferenceContext.getMultiPicBroadcastStatus()){
            broadcastStart(conferenceId, bridge,false);
            AttendeeSmc3 masterAttendee = conferenceContext.getMasterAttendee();
            if(masterAttendee!=null){
                ConferenceStatusRequest conferenceStatusRequest = new ConferenceStatusRequest();
                conferenceStatusRequest.setBroadcaster(masterAttendee.getSmcParticipant().getGeneralParam().getId());
                bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, conferenceStatusRequest, bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
            }
            ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
            List<ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
            if (!CollectionUtils.isEmpty(subPicList)) {
                for (ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                    String participantId = subPicListDTO.getParticipantId();
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(participantId);
                    if(attendeeBySmc3Id!=null){
                        attendeeBySmc3Id.setBroadcastStatus(2);
                       Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.ATTENDEE_UPDATE,attendeeBySmc3Id.getUpdateMap());
                    }

                }
            }
            conferenceContext.setMultiPicBroadcastStatus(false);
            List<AttendeeSmc3> targetAttendees = this.targetAttendees;
            for (AttendeeSmc3 targetAttendee : targetAttendees) {
                AttendeeImportance.COMMON.processAttendeeWebsocketMessage(targetAttendee);
                Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.ATTENDEE_UPDATE,targetAttendee.getUpdateMap());
            }
        }
    }
    private void cancelBroadcastStatusNoMaster() {
        if(conferenceContext.getMultiPicBroadcastStatus()){
            ConferenceState.StateDTO.MultiPicInfoDTO multiPicInfo = conferenceContext.getMultiPicInfo();
            List<ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfo.getSubPicList();
            if (!CollectionUtils.isEmpty(subPicList)) {
                for (ConferenceState.StateDTO.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                    String participantId = subPicListDTO.getParticipantId();
                    AttendeeSmc3 attendeeBySmc3Id = conferenceContext.getAttendeeBySmc3Id(participantId);
                    if(attendeeBySmc3Id!=null){
                        attendeeBySmc3Id.setBroadcastStatus(2);
                        Smc3WebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext,WebsocketMessageType.ATTENDEE_UPDATE,attendeeBySmc3Id.getUpdateMap());
                    }

                }
            }
            conferenceContext.setMultiPicBroadcastStatus(false);
        }
    }



    @Override
    public void cancel() {
        if (conferenceContext != null) {
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);
            cancelBroadcast(conferenceContext.getSmc3Bridge(),conferenceContext.getSmc3conferenceId());
            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            logger.info("默认视图已结束");
        }
    }

    public void cancelWithOutMaster() {
        if (conferenceContext != null) {
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.DEFAULT_VIEW_RUNNING, false);
            cancelBroadcastStatusNoMaster();
            StringBuilder messageTip1 = new StringBuilder();
            messageTip1.append("默认视图已结束");
            Smc3WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip1);
            logger.info("默认视图已结束");
        }
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

    private int getCheckedMeetingJoinedCount() {
        int c = 0;
        if (!ObjectUtils.isEmpty(attendees)) {
            for (AttendeeSmc3 attendee : attendees) {
                if (attendee.isMeetingJoined() && !(YesOrNo.convert(defaultViewIsBroadcast) == YesOrNo.NO
                        && PanePlacementSelfPaneMode.convert(defaultViewIsDisplaySelf) != PanePlacementSelfPaneMode.SELF
                        && attendee == conferenceContext.getMasterAttendee())) {
                    c++;
                }
            }
        }
        return c;
    }

    /**
     * <pre>初始化</pre>
     *
     * @author lilinhai
     * @since 2021-04-09 18:00  void
     */
    private void init() {


    }



    /**
     * <p>Get Method   :   defaultViewDepts List<JSONObject></p>
     *
     * @return defaultViewDepts
     */
    public List<JSONObject> getDefaultViewDepts() {
        return defaultViewDepts;
    }

    /**
     * <p>Set Method   :   defaultViewDepts List<JSONObject></p>
     *
     * @param dept
     */
    public void addDefaultViewDept(JSONObject dept) {
        this.defaultViewDepts.add(dept);
    }

    @Override
    public boolean contains(AttendeeSmc3 attendee) {
        for (CellScreen cellScreen : splitScreen.getCellScreens()) {
            if (cellScreen.getLastOperationAttendee() == attendee) {
                return true;
            }
        }
        return false;
    }

    public void broadcastStart(String conferenceId,Smc3Bridge smc3Bridge,boolean enable) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcaster", "");
        if (enable) {
            jsonObject.put("broadcaster", "00000000-0000-0000-0000-000000000000");
        }
        smc3Bridge.getSmcConferencesInvoker().conferencesStatusControl(conferenceId, jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

    public void setMute(String conferenceId, boolean b,Smc3Bridge smc3Bridge) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isMute", b);
        smc3Bridge.getSmcConferencesInvoker().conferencesControl(conferenceId, jsonObject.toJSONString(), smc3Bridge.getSmcPortalAuthMeetingAdminTokenInvoker().getMeetingHeaders());
    }

}
