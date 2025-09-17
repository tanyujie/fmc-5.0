package com.paradisecloud.fcm.zte.task;

import com.alibaba.druid.support.http.util.IPAddress;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.AttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.InvitedAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.McuAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.busi.attendee.TerminalAttendeeForMcuZte;
import com.paradisecloud.fcm.zte.model.request.cc.CcAddMrTerminalRequest;
import com.paradisecloud.fcm.zte.model.request.cc.CcSetConnectMrTerminalRequest;
import com.zte.m900.bean.Participant;
import com.zte.m900.request.ConnectParticipantRequest;
import com.zte.m900.request.InviteParticipantRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

public class InviteAttendeesTask extends DelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteAttendeesTask.class);

    private McuZteConferenceContext conferenceContext;
    private List<AttendeeForMcuZte> attendees;

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuZteConferenceContext conferenceContext, AttendeeForMcuZte attendee) {
        super("invite_1_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuZteConferenceContext conferenceContext, List<AttendeeForMcuZte> attendees) {
        super("invite_M_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("MCU_ZTE终端邀请开始。ID:" + getId());

        if (getId().startsWith("invite_M_")) {
            if (conferenceContext.isInvitingTerminal()) {
                LOGGER.info("其它线程正在处理邀请");
                return;
            }
            conferenceContext.setInvitingTerminal(true);
        }

        try {
            if (attendees != null) {
                List<InviteParticipantRequest> addTerminalListTemp = new ArrayList<>();

                List<String> terminalIdentifierList = new ArrayList();
                for (AttendeeForMcuZte attendeeForMcuZte : attendees) {
                    if (!attendeeForMcuZte.isMeetingJoined()) {
                        if (conferenceContext != null) {


                            String remoteParty = attendeeForMcuZte.getRemoteParty();

                            int terType = 0;
                            Integer callModel = 1;
                            if (attendeeForMcuZte instanceof TerminalAttendeeForMcuZte) {
                                TerminalAttendeeForMcuZte terminalAttendeeForMcuZte = (TerminalAttendeeForMcuZte) attendeeForMcuZte;
                                if (terminalAttendeeForMcuZte.getTerminalId() != null) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                                    if (busiTerminal != null) {

                                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                                            if (fcmBridge != null) {
                                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                                if (callPort != null) {
                                                    remoteParty += ":" + callPort;
                                                }
                                                terType = 3;
                                                callModel = 2;
                                            }
                                        }
                                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                                            terType = 3;
                                            callModel = 2;
                                        }
                                        if (TerminalType.isZTE(busiTerminal.getType())) {
                                            terType = busiTerminal.getZteTerminalType();
                                            callModel = busiTerminal.getCallmodel();
                                        }
                                        if (TerminalType.isIp(busiTerminal.getType())) {

                                            callModel = 1;
                                            terType = 0;
                                        }


                                    }
                                }
                            } else if (attendeeForMcuZte instanceof InvitedAttendeeForMcuZte) {
                                InvitedAttendeeForMcuZte invitedAttendeeForMcuZte = (InvitedAttendeeForMcuZte) attendeeForMcuZte;
                                if (invitedAttendeeForMcuZte.getTerminalId() != null) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZte.getTerminalId());
                                    if (busiTerminal != null) {
                                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                                            if (fcmBridge != null) {
                                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                                if (callPort != null) {
                                                    remoteParty += ":" + callPort;
                                                }
                                                terType = 3;
                                                callModel = 2;
                                            }
                                        }
                                        if (TerminalType.isFSBC(busiTerminal.getType())) {
                                            terType = 3;
                                            callModel = 2;
                                        }
                                        if (TerminalType.isZTE(busiTerminal.getType())) {
                                            terType = busiTerminal.getZteTerminalType();
                                            callModel = busiTerminal.getCallmodel();
                                        }
                                        if (TerminalType.isIp(busiTerminal.getType())) {

                                            callModel = 1;
                                            terType = 0;
                                        }
                                        if (TerminalType.isWindows(busiTerminal.getType())) {

                                            callModel = 1;
                                            terType = 0;
                                        }

                                    }
                                }else {
                                    if(remoteParty.contains("@")){
                                        callModel = 2;
                                        terType = 3;
                                    }else {
                                        callModel = 1;
                                        terType = 0;
                                    }
                                }
                            } else if (attendeeForMcuZte instanceof McuAttendeeForMcuZte) {
                                McuAttendeeForMcuZte mcuAttendeeForMcuZte = (McuAttendeeForMcuZte) attendeeForMcuZte;
                                String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZte.getCascadeConferenceId());
                                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                                if (baseConferenceContext != null) {
                                    remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                                    if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                                        remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                                    }
                                    String oldRemoteParty = mcuAttendeeForMcuZte.getRemoteParty();
                                    attendeeForMcuZte.setRemoteParty(remoteParty);
                                    attendeeForMcuZte.setIp(baseConferenceContext.getMcuCallIp());
                                    conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, mcuAttendeeForMcuZte);
                                    terType = 3;
                                    callModel = 2;
                                }
                            }


                            String uuid = conferenceContext.getDisconnectedParticipantUuidByRemoteParty(remoteParty);
                            if (StringUtils.isNotEmpty(uuid)) {
                                terminalIdentifierList.add(uuid);
                            } else {
                                InviteParticipantRequest inviteParticipantRequest = new InviteParticipantRequest();
                                inviteParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                                inviteParticipantRequest.setConferencePassword(conferenceContext.getConferencePassword());
                                com.zte.m900.bean.Participant participant = new Participant();
                                inviteParticipantRequest.setParticipant(participant);


                                participant.setTerType(terType);
                                participant.setCallMode(callModel);
                                participant.setTerminalName(attendeeForMcuZte.getName());
                                participant.setIpAddress(attendeeForMcuZte.getIp());
                                participant.setTerminalNumber(attendeeForMcuZte.getRemoteParty());

                                addTerminalListTemp.add(inviteParticipantRequest);
                            }
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(terminalIdentifierList)) {
                    ConnectParticipantRequest connectParticipantRequest = new ConnectParticipantRequest();
                    connectParticipantRequest.setConferenceIdentifier(conferenceContext.getConfId());
                    connectParticipantRequest.setTerminalIdentifier(terminalIdentifierList.toArray(new String[0]));
                    conferenceContext.getConferenceControlApi().setConnectMrTerminal(connectParticipantRequest);
                }


                for (InviteParticipantRequest inviteParticipantRequest : addTerminalListTemp) {
                    conferenceContext.getConferenceControlApi().inviteParticipant(inviteParticipantRequest);

                }
            }
        } catch (Exception e) {
        }
        if (getId().startsWith("invite_M_")) {
            conferenceContext.setInvitingTerminal(false);
        }
    }
}
