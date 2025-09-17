/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TalkAttendeeOperation.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.operation
 * @author sinhy
 * @since 2021-12-01 10:16
 * @version  V1.0
 */
package com.paradisecloud.fcm.smc2.model.attendee.operation;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.Threads;
import com.paradisecloud.fcm.common.enumer.AttendeeTalkStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.smc2.cache.Smc2ConferenceContext;
import com.paradisecloud.fcm.smc2.cache.Smc2WebSocketMessagePusher;
import com.paradisecloud.fcm.smc2.model.AttendeeOperation;
import com.paradisecloud.fcm.smc2.model.attendee.AttendeeSmc2;
import com.paradisecloud.fcm.smc2.model.layout.ContinuousPresenceModeEnum;
import com.paradisecloud.fcm.smc2.model.request.MultiPicInfoReq;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.pojo.local.WSCtrlSiteCommParamEx;
import com.suntek.smc.esdk.service.client.ConferenceServiceEx;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 参会者对话操作
 *
 */
public class TalkAttendeeOperation extends AttendeeOperation
{

    private  MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO;
    private AttendeeSmc2 target;
    /**
     * <pre>用一句话描述这个变量的含义</pre>
     * @since 2021-12-01 10:16
     */
    private static final long serialVersionUID = 1L;

    /**
     * <pre>构造方法</pre>
     * @param conferenceContext
     */
    public TalkAttendeeOperation(Smc2ConferenceContext conferenceContext, AttendeeSmc2 attendee)
    {
        super(conferenceContext);
        this.target=attendee;

    }

    public MultiPicInfoReq.MultiPicInfoDTO getMultiPicInfoDTO() {
        return multiPicInfoDTO;
    }

    public void setMultiPicInfoDTO(MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO) {
        this.multiPicInfoDTO = multiPicInfoDTO;
    }

    @Override
    public void operate()
    {
        AttendeeSmc2 masterAttendee = conferenceContext.getMasterAttendee();
        if(masterAttendee==null){
            logger.error("主会场不存在");
            return;
        }
        String confId = conferenceContext.getSmc2conferenceId();

        ConferenceServiceEx conferenceServiceEx = ServiceFactoryEx.getService(ConferenceServiceEx.class);
        if (target==null||target == conferenceContext.getMasterAttendee())
        {
            logger.error("主会场不能和自己对话");
            return;
        }
        //取消广播
        conferenceServiceEx.setBroadcastSiteEx(confId, masterAttendee.getRemoteParty(), 1);
        conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 1);

        Threads.sleep(1000);
        target.setTalkStatus(AttendeeTalkStatus.YES.getValue());
        masterAttendee.setTalkStatus(AttendeeTalkStatus.YES.getValue());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE,masterAttendee.getUpdateMap());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE,target.getUpdateMap());

        Integer integer = conferenceServiceEx.setVideoSourceEx(conferenceContext.getSmc2conferenceId(), target.getRemoteParty(), masterAttendee.getRemoteParty(), 0);
        logger.info(conferenceContext.getName()+"会议对话"+confId+"目标会场设置"+target.getName()+"视频源为"+masterAttendee.getName()+"uri"+masterAttendee.getRemoteParty()+"返回结果"+integer);
        conferenceServiceEx.setVideoSourceEx(conferenceContext.getSmc2conferenceId(), masterAttendee.getRemoteParty(),target.getRemoteParty(), 0);

        //锁定主席和对话者
        ctrlSiteCommParams(conferenceServiceEx,confId,1,target,masterAttendee);


        List<String> uris = new ArrayList<>();
        uris.add(masterAttendee.getSmcParticipant().getGeneralParam().getUri());
        uris.add(target.getSmcParticipant().getGeneralParam().getUri());
        openMixing(uris);


        List<String> uriList=new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(conferenceContext.getAttendees())) {
            for (AttendeeSmc2 attendee : conferenceContext.getAttendees()) {
                otherSiteMute(attendee,uriList);
            }
        }
        for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
            List<AttendeeSmc2> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
            if (attendees != null) {
                for (AttendeeSmc2 attendee : attendees) {
                    otherSiteMute(attendee,uriList);
                }
            }
        }

        for (AttendeeSmc2 attendee : conferenceContext.getMasterAttendees()) {
            otherSiteMute(attendee,uriList);
        }


        MultiPicInfoReq.MultiPicInfoDTO multiPicInfoD=new  MultiPicInfoReq.MultiPicInfoDTO();
        multiPicInfoD.setPicNum(2);
        multiPicInfoD.setMode(1);
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList=new ArrayList<>();
        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO_master = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO_master.setParticipantId(masterAttendee.getParticipantUuid());
        subPicListDTO_master.setStreamNumber(0);
        subPicList.add(subPicListDTO_master);
        MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO_target = new MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO();
        subPicListDTO_target.setParticipantId(target.getParticipantUuid());
        subPicListDTO_target.setStreamNumber(0);
        subPicList.add(subPicListDTO_target);
        multiPicInfoD.setSubPicList(subPicList);

        // 取消除当前对话者外的其他已开麦参会者的混音状态
        closeMixing(uriList);



        //设置多画面参数
        makeSubpic(multiPicInfoD, conferenceContext, conferenceServiceEx, conferenceContext.getSmc2conferenceId());
        //广播多画面
        conferenceServiceEx.setBroadcastContinuousPresenceEx(confId, 0);
        //对话设置 解锁
        ctrlSiteCommParams(conferenceServiceEx,confId,0,target,masterAttendee);




    }

    private void otherSiteMute(AttendeeSmc2 attendee,List<String> uriList) {

        if(Objects.equals(attendee.getParticipantUuid(), this.target.getParticipantUuid())){
            return;
        }
        uriList.add(attendee.getSmcParticipant().getGeneralParam().getUri());
    }


    private void otherSitId(AttendeeSmc2 attendee,List<String> idLsit) {

        if(Objects.equals(attendee.getParticipantUuid(), this.target.getParticipantUuid())){
            return;
        }
        idLsit.add(attendee.getId());
    }



    public void openMixing(List<String> uris){
        //是否闭音。
        //0：不闭音
        //1：闭音
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(conferenceContext.getSmc2conferenceId(), uris, 0);
        if (resultCode != 0) {
            throw new CustomException("全体静音错误：" + resultCode);
        }
    }

    public void closeMixing(List<String> uris){
        //是否闭音。
        //0：不闭音
        //1：闭音
        ConferenceServiceEx conferenceServiceEx = conferenceContext.getSmc2Bridge().getConferenceServiceEx();
        Integer resultCode = conferenceServiceEx.setSitesMuteEx(conferenceContext.getSmc2conferenceId(), uris, 1);
        if (resultCode != 0) {
            throw new CustomException("全体静音错误：" + resultCode);
        }
    }



    @Override
    public void cancel()
    {
        closeMixing(new ArrayList<>(Collections.singletonList(target.getRemoteParty())));
        target.setTalkStatus(AttendeeTalkStatus.NO.getValue());
        conferenceContext.getMasterAttendee().setTalkStatus(AttendeeTalkStatus.NO.getValue());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, conferenceContext.getMasterAttendee().getUpdateMap());
        Smc2WebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE,target.getUpdateMap());
    }



    public void defaultAttendeeOperation(){

        if(conferenceContext.getMasterAttendee()!=null){
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            ChangeMasterAttendeeOperation changeMasterAttendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, conferenceContext.getMasterAttendee());
            conferenceContext.setAttendeeOperation(changeMasterAttendeeOperation);
            changeMasterAttendeeOperation.operate();
        }else {
            conferenceContext.setLastAttendeeOperation(conferenceContext.getAttendeeOperation());
            DefaultAttendeeOperation defaultAttendeeOperation = new DefaultAttendeeOperation(conferenceContext);
            conferenceContext.setAttendeeOperation(defaultAttendeeOperation);
            defaultAttendeeOperation.operate();
        }
    }



    private void makeSubpic(MultiPicInfoReq.MultiPicInfoDTO multiPicInfoDTO, Smc2ConferenceContext smc2ConferenceContext, ConferenceServiceEx conferenceServiceEx, String confId) {
        String target = "(%CP0)";
        List<MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO> subPicList = multiPicInfoDTO.getSubPicList();
        Integer picNum = multiPicInfoDTO.getPicNum();
        Integer mode = multiPicInfoDTO.getMode();
        int presenceMode = ContinuousPresenceModeEnum.getModelValue(picNum, mode);
        if (presenceMode == -1) {
            throw new CustomException("多画面设置失败:不支持该" + picNum + "画面");
        }
        List<String> subPics = new ArrayList<>();
        if (!CollectionUtils.isEmpty(subPicList)) {
            for (MultiPicInfoReq.MultiPicInfoDTO.SubPicListDTO subPicListDTO : subPicList) {
                String m_participantId = subPicListDTO.getParticipantId();
                AttendeeSmc2 attendee = smc2ConferenceContext.getAttendeeById(m_participantId);
                if (attendee != null) {
                    subPics.add(attendee.getRemoteParty());
                }else {
                    subPics.add("");
                }
            }

        }

        Integer integer = conferenceServiceEx.setContinuousPresenceEx(confId, target, presenceMode, subPics);
        if(integer == 0){
            this.multiPicInfoDTO=multiPicInfoDTO;
        }
    }



   public void ctrlSiteCommParams(ConferenceServiceEx conferenceServiceEx, String confId,int lock,AttendeeSmc2... attendeeSmc2s){
       List<WSCtrlSiteCommParamEx> wsCtrlSiteCommParamsAll = new ArrayList<>();
       for (AttendeeSmc2 attendeeSmc2 : attendeeSmc2s) {
           WSCtrlSiteCommParamEx item = new WSCtrlSiteCommParamEx();
           //锁定
           item.setOperaTypeParam(lock);
           item.setSiteUri(attendeeSmc2.getRemoteParty());
           wsCtrlSiteCommParamsAll.add(item);
       }
       conferenceServiceEx.setVSAttrCtrlEx(confId, wsCtrlSiteCommParamsAll);
    }

}
