/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeNewProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.huaweicloud.huaweicloud.templateConference.updateprocess;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.AttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.busi.attende.SelfCallAttendeeHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudConferenceContext;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudWebSocketMessagePusher;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudHistoryConferenceService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;


import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.spring.BeanFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;


/**  
 * <pre>参会者新增处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:29
 * @version V1.0  
 */
public class SelfCallAttendeeHwcloudProcessor extends AttendeeProcessor
{

    private static String streamingIdentification = AesEnsUtils.getAesEncryptor().decryptBase64ToString("frcb7n2IN9aWTILuSQXJXRf8OmF6ceqj4o5YIPhiHRU=");
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:29 
     * @param participant
     * @param conferenceContext
     */
    public SelfCallAttendeeHwcloudProcessor(SmcParitipantsStateRep.ContentDTO participant, HwcloudConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
    }
    
    @Override
    public void process()
    {

        AttendeeHwcloud attendeeByPUuid = conferenceContext.getAttendeeByPUuid(participant.getGeneralParam().getId());
        if(attendeeByPUuid!=null){
            return;
        }

        SelfCallAttendeeHwcloud selfCallAttendee = new SelfCallAttendeeHwcloud();
        selfCallAttendee.setSmcParticipant(participant);
        selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());
        selfCallAttendee.setName(participant.getGeneralParam().getName());
        String remotePartyNew = "";
        boolean addAttendee = true;

        {
            // 自主呼入匹配会控注册终端
            String remoteParty = participant.getGeneralParam().getUri();
            if(Strings.isNotBlank(remoteParty)){
                if (remoteParty.contains(":")) {
                    remoteParty = remoteParty.substring(0, remoteParty.indexOf(":"));
                }
                BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                if (busiTerminal == null) {
                    if (remoteParty.contains("@")) {
                        try {
                            String[] remotePartyArr = remoteParty.split("@");
                            String credential = remotePartyArr[0];
                            String ip = remotePartyArr[1];
                            if (StringUtils.hasText(ip)) {
                                FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                                if (fsbcBridge != null) {
                                    String remotePartyIp = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                    busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyIp);
                                }
                                if (busiTerminal == null) {
                                    FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                                    if (fcmBridge != null) {
                                        String remotePartyIp = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                        busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyIp);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                } else {
                    if (remoteParty.contains("@")) {
                        String[] remotePartyArr = remoteParty.split("@");
                        String credential = remotePartyArr[0];
                        String ip = remotePartyArr[1];
                        if (StringUtils.hasText(ip)) {
                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByIp(ip);
                            if (fsbcBridge != null) {
                                String domainName = fsbcBridge.getBusiFsbcRegistrationServer().getDomainName();
                                if (!ObjectUtils.isEmpty(domainName)) {
                                    remotePartyNew = credential + "@" + domainName;
                                }
                            } else {
                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByIp(ip);
                                if (fcmBridge != null) {
                                    String domainName = fcmBridge.getBusiFreeSwitch().getDomainName();
                                    if (!ObjectUtils.isEmpty(domainName)) {
                                        remotePartyNew = credential + "@" + domainName;
                                    }
                                }
                            }
                        }
                    }
                }
                if (busiTerminal != null) {
                    selfCallAttendee.setTerminalId(busiTerminal.getId());
                    BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                    BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(selfCallAttendee.getTerminalId());
                    if (busiUserTerminal != null) {
                        selfCallAttendee.setUserId(busiUserTerminal.getUserId());
                    }
                    String name = "";
                    if (!ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                        if (!busiTerminal.getName().equals(participant.getGeneralParam().getName())) {
                            name += busiTerminal.getName() + "(" + participant.getGeneralParam().getName() + ")";
                        }
                    }
                    if (!ObjectUtils.isEmpty(name)) {
                        selfCallAttendee.setName(name);
                    } else {
                        selfCallAttendee.setName(busiTerminal.getName());
                    }
                } else {
                    selfCallAttendee = new SelfCallAttendeeHwcloud();
                    if (!ObjectUtils.isEmpty(participant.getGeneralParam().getName())) {
                        selfCallAttendee.setName(participant.getGeneralParam().getName());
                    } else {
                        selfCallAttendee.setName(participant.getGeneralParam().getName());
                    }
                }
            }
        }
        if(Strings.isNotBlank(participant.getNick_name())){
            selfCallAttendee.setNickName(participant.getNick_name());
        }
        selfCallAttendee.setNumber(participant.getTel());
        selfCallAttendee.setWeight(1);
        selfCallAttendee.setIp(participant.getGeneralParam().getUri());
        selfCallAttendee.setDeptId(conferenceContext.getDeptId());
        setCommon(selfCallAttendee);
        selfCallAttendee.setSmcParticipant(participant);
        selfCallAttendee.setParticipantUuid(participant.getGeneralParam().getId());

        // 关联绑定attendee
        participant.setAttendeeId(selfCallAttendee.getId());

        selfCallAttendee.setConferenceId(conferenceContext.getId());
        if (addAttendee) {
            conferenceContext.addAttendee(selfCallAttendee);
        }
        //sip:90839@10.0.66.104
        String remoteParty = selfCallAttendee.getRemoteParty();



        
        // 标记锁定状态
        if (conferenceContext.isLocked())
        {
            selfCallAttendee.setLocked(true);
        }
        // 清空
        selfCallAttendee.resetUpdateMap();
        String conferenceNumber = selfCallAttendee.getConferenceNumber();
        if(!StringUtils.hasText(conferenceNumber)){
            selfCallAttendee.setConferenceNumber(conferenceContext.getConferenceNumber());
        }
        if (addAttendee) {
            HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
            HwcloudWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + selfCallAttendee.getName() + "】入会");
            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, selfCallAttendee);

        }
        IBusiMcuHwcloudHistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuHwcloudHistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, selfCallAttendee, false);
        logger.info("AttendeeNewProcessor add: " + selfCallAttendee);



    }
    
    private void setCommon(AttendeeHwcloud attendee)
    {
        attendee.setConferenceNumber(String.valueOf(conferenceContext.getConferenceNumber()));
        attendee.setId(participant.getGeneralParam().getId());
        // 必须覆盖fmeAttendee
        if (!streamingIdentification.equals(attendee.getRemoteParty())) {
            attendee.setRemoteParty(participant.getMs_open_id());
        }
        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
        attendee.setParticipantUuid(participant.getGeneralParam().getId());
        attendee.setUserRole(participant.getUserRole());

    }
}
