/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeNewProcessor.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model
 * @author lilinhai 
 * @since 2021-02-19 15:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.tencent.templateConference.updateprocess;

import com.paradisecloud.fcm.common.enumer.AttendeeMeetingStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.WebsocketMessageType;
import com.paradisecloud.fcm.common.utils.AesEnsUtils;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiHistoryConferenceService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseAttendee;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.conference.cascade.CascadeInviteMessagePush;
import com.paradisecloud.fcm.service.interfaces.IMqttService;

import com.paradisecloud.fcm.tencent.busi.attende.AttendeeTencent;
import com.paradisecloud.fcm.tencent.busi.attende.SelfCallAttendeeTencent;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentWebSocketMessagePusher;
import com.paradisecloud.fcm.tencent.model.SmcParitipantsStateRep;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.operation.AttendeeOperation;
import com.paradisecloud.fcm.tencent.model.operation.ChangeMasterAttendeeOperation;
import com.paradisecloud.fcm.tencent.model.request.NameParticipantRequest;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.spring.BeanFactory;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**  
 * <pre>参会者新增处理器</pre>
 * @author lilinhai
 * @since 2021-02-19 15:29
 * @version V1.0  
 */
public class SelfCallAttendeeTencentProcessor extends AttendeeProcessor
{

    private static String streamingIdentification = AesEnsUtils.getAesEncryptor().decryptBase64ToString("frcb7n2IN9aWTILuSQXJXRf8OmF6ceqj4o5YIPhiHRU=");
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2021-02-19 15:29 
     * @param participant
     * @param conferenceContext
     */
    public SelfCallAttendeeTencentProcessor(SmcParitipantsStateRep.ContentDTO participant, TencentConferenceContext conferenceContext)
    {
        super(participant, conferenceContext);
    }
    
    @Override
    public void process()
    {
        SelfCallAttendeeTencent selfCallAttendee = new SelfCallAttendeeTencent();
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
                    selfCallAttendee = new SelfCallAttendeeTencent();
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
        selfCallAttendee.setMs_open_id(participant.getMs_open_id());
        selfCallAttendee.setWeight(1);
        selfCallAttendee.setIp(participant.getGeneralParam().getUri());
        selfCallAttendee.setDeptId(conferenceContext.getDeptId());
        setCommon(selfCallAttendee);
        selfCallAttendee.setSmcParticipant(participant);
        selfCallAttendee.setInstanceid(participant.getInstanceid());
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
            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
            TencentWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + selfCallAttendee.getName() + "】入会");
            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, selfCallAttendee);

        }
        IBusiMcuTencentHistoryConferenceService teleHistoryConferenceService = BeanFactory.getBean(IBusiMcuTencentHistoryConferenceService.class);
        teleHistoryConferenceService.updateBusiHistoryParticipant(conferenceContext, selfCallAttendee, false);

        logger.info("AttendeeNewProcessor add: " + selfCallAttendee);

        String upCascadeRemoteParty = conferenceContext.getUpCascadeRemoteParty();
        String upCascadeConferenceId = conferenceContext.getUpCascadeConferenceId();
        if(Strings.isNotBlank(upCascadeConferenceId)&&Strings.isNotBlank(upCascadeRemoteParty)){
            String contextKey = EncryptIdUtil.parasToContextKey(upCascadeConferenceId);
            BaseConferenceContext UpbaseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);

            List mcuAttendees = UpbaseConferenceContext.getMcuAttendees();

            for (Object mcuAttendee : mcuAttendees) {
                BaseAttendee mc=(BaseAttendee)mcuAttendee;
                String name1 = mc.getName();
                String name = selfCallAttendee.getName();
                if(Objects.equals(conferenceContext.getName(),name1)&&Objects.equals(name,UpbaseConferenceContext.getName())){
                    try {
                        AttendeeOperation old = conferenceContext.getAttendeeOperation();
                        conferenceContext.setLastAttendeeOperation(old);
                        old.cancel();
                        AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, selfCallAttendee);
                        conferenceContext.setAttendeeOperation(attendeeOperation);
                        attendeeOperation.operate();
                    } catch (Exception e) {
                        logger.error("MCU-TENCENT ChangeMasterAttendeeOperation error", e.getMessage());
                    }
                }
                if(Objects.equals(conferenceContext.getName(),name1)&&name.length()==5&&Integer.valueOf(name) >0&&Objects.equals(McuType.SMC2.getCode(),UpbaseConferenceContext.getMcuType())){

                    TencentConferenceCtrlClient conferenceCtrlClient = conferenceContext.getTencentBridge().getConferenceCtrlClient();
                    NameParticipantRequest request = new NameParticipantRequest();

                    request.setMeetingId(conferenceContext.getMeetingId());
                    request.setInstanceid(1);
                    request.setOperatorIdType(4);
                    request.setOperatorId(conferenceContext.getMsopenid());
                    NameParticipantRequest.UsersDTO usersDTO = new NameParticipantRequest.UsersDTO();
                    usersDTO.setMsOpenid(participant.getMs_open_id());
                    usersDTO.setInstanceid(participant.getInstanceid());
                    usersDTO.setNick_name(UpbaseConferenceContext.getName());
                    request.setUsers(Arrays.asList(usersDTO));
                    try {
                        conferenceCtrlClient.nameChange(request);
                    } catch (WemeetSdkException e) {
                    }
                    try {
                        AttendeeOperation old = conferenceContext.getAttendeeOperation();
                        conferenceContext.setLastAttendeeOperation(old);
                        old.cancel();
                        AttendeeOperation attendeeOperation = new ChangeMasterAttendeeOperation(conferenceContext, selfCallAttendee);
                        conferenceContext.setAttendeeOperation(attendeeOperation);
                        attendeeOperation.operate();
                    } catch (Exception e) {
                        logger.error("MCU-TENCENT ChangeMasterAttendeeOperation error", e.getMessage());
                    }
                }

            }

        }



    }
    
    private void setCommon(AttendeeTencent attendee)
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

    }
}
