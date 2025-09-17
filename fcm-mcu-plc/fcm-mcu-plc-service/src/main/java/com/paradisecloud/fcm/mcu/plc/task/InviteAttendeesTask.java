package com.paradisecloud.fcm.mcu.plc.task;

import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.AttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.InvitedAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.McuAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.TerminalAttendeeForMcuPlc;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcAddMrTerminalRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcSetConnectMrTerminalRequest;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InviteAttendeesTask extends DelayTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteAttendeesTask.class);

    private McuPlcConferenceContext conferenceContext;
    private List<AttendeeForMcuPlc> attendees;

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuPlcConferenceContext conferenceContext, AttendeeForMcuPlc attendee) {
        super("invite_1_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuPlcConferenceContext conferenceContext, List<AttendeeForMcuPlc> attendees) {
        super("invite_M_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("MCU_PLC终端邀请开始。ID:" + getId());

        if (getId().startsWith("invite_M_")) {
            if (conferenceContext.isInvitingTerminal()) {
                LOGGER.info("其它线程正在处理邀请");
                return;
            }
            conferenceContext.setInvitingTerminal(true);
        }

        try {
            if (attendees != null) {
                List<CcAddMrTerminalRequest> addTerminalListTemp = new ArrayList<>();
                List<CcSetConnectMrTerminalRequest> connectTerminalListTemp = new ArrayList<>();
                for (AttendeeForMcuPlc attendeeForMcuPlc : attendees) {
                    if (!attendeeForMcuPlc.isMeetingJoined()) {
                        if (conferenceContext != null) {
                            String remoteParty = attendeeForMcuPlc.getRemoteParty();
                            String partyName = attendeeForMcuPlc.getName();
                            String assName = remoteParty;
                            String partyInterface;
                            Integer callType = 2;
                            if (attendeeForMcuPlc instanceof TerminalAttendeeForMcuPlc) {
                                TerminalAttendeeForMcuPlc terminalAttendeeForMcuPlc = (TerminalAttendeeForMcuPlc) attendeeForMcuPlc;
                                if (terminalAttendeeForMcuPlc.getTerminalId() != null) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuPlc.getTerminalId());
                                    if (busiTerminal != null) {
                                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                                            if (fcmBridge != null) {
                                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                                if (callPort != null) {
                                                    remoteParty += ":" + callPort;
                                                }
                                            }
                                        }
                                        partyName = busiTerminal.getName();
                                        if (StringUtils.isNotEmpty(busiTerminal.getCredential())) {
                                            assName = busiTerminal.getCredential();
                                        } else {
                                            assName = busiTerminal.getIp();
                                        }
                                        if (TerminalType.FSBC_H323.getId() == busiTerminal.getType()) {
                                            callType = 1;
                                        }
                                    }
                                }
                            } else if (attendeeForMcuPlc instanceof InvitedAttendeeForMcuPlc) {
                                InvitedAttendeeForMcuPlc invitedAttendeeForMcuPlc = (InvitedAttendeeForMcuPlc) attendeeForMcuPlc;
                                if (invitedAttendeeForMcuPlc.getTerminalId() != null) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuPlc.getTerminalId());
                                    if (busiTerminal != null) {
                                        if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                                            FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                                            if (fcmBridge != null) {
                                                Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                                                if (callPort != null) {
                                                    remoteParty += ":" + callPort;
                                                }
                                            }
                                        }
                                        partyName = busiTerminal.getName();
                                        if (StringUtils.isNotEmpty(busiTerminal.getCredential())) {
                                            assName = busiTerminal.getCredential();
                                        } else {
                                            assName = busiTerminal.getIp();
                                        }
                                    }
                                }
                                callType = invitedAttendeeForMcuPlc.getCallType();
                            } else if (attendeeForMcuPlc instanceof McuAttendeeForMcuPlc) {
                                McuAttendeeForMcuPlc mcuAttendeeForMcuPlc = (McuAttendeeForMcuPlc) attendeeForMcuPlc;
                                String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuPlc.getCascadeConferenceId());
                                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                                if (baseConferenceContext != null) {
                                    remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                                    if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                                        remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                                    }
                                    String oldRemoteParty = mcuAttendeeForMcuPlc.getRemoteParty();
                                    attendeeForMcuPlc.setRemoteParty(remoteParty);
                                    attendeeForMcuPlc.setIp(baseConferenceContext.getMcuCallIp());
                                    conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, mcuAttendeeForMcuPlc);
                                    if (StringUtils.isNotEmpty(baseConferenceContext.getConferenceNumber())) {
                                        assName = baseConferenceContext.getConferenceNumber();
                                    } else {
                                        assName = baseConferenceContext.getMcuCallIp();
                                    }
                                    callType = 2;// sip
                                }
                            }

                            String uuid = conferenceContext.getDisconnectedParticipantUuidByRemoteParty(remoteParty);
                            if (StringUtils.isNotEmpty(uuid)) {
                                CcSetConnectMrTerminalRequest ccSetConnectMrTerminalRequest = new CcSetConnectMrTerminalRequest();
                                ccSetConnectMrTerminalRequest.setId(conferenceContext.getConfId());
                                ccSetConnectMrTerminalRequest.setParty_id(uuid);
                                ccSetConnectMrTerminalRequest.setConnect(true);
                                connectTerminalListTemp.add(ccSetConnectMrTerminalRequest);
                            } else {
                                CcAddMrTerminalRequest ccAddMrTerminalRequest = new CcAddMrTerminalRequest();
                                ccAddMrTerminalRequest.setId(conferenceContext.getConfId());
                                if (callType != null && callType == 1) {
                                    partyInterface = "h323";// h323
                                } else {
                                    partyInterface = "sip";// sip
                                }
                                ccAddMrTerminalRequest.setParty_interface(partyInterface);
                                if (conferenceContext.hasParticipantName(partyName)) {
                                    partyName += "(" + assName + ")";
                                }
                                ccAddMrTerminalRequest.setParty_name(partyName);
                                if (StringUtils.isNotEmpty(remoteParty)) {
                                    if (remoteParty.contains("@")) {
                                        ccAddMrTerminalRequest.setParty_ip("");
                                        ccAddMrTerminalRequest.setParty_remote_address(remoteParty);
                                    } else {
                                        ccAddMrTerminalRequest.setParty_ip(remoteParty);
                                        ccAddMrTerminalRequest.setParty_remote_address("");
                                    }
                                }
                                addTerminalListTemp.add(ccAddMrTerminalRequest);
                            }
                        }
                    }
                }

                for (CcSetConnectMrTerminalRequest ccSetConnectMrTerminalRequest : connectTerminalListTemp) {
                    conferenceContext.getConferenceControlApi().setConnectMrTerminal(ccSetConnectMrTerminalRequest);
                }
                for (CcAddMrTerminalRequest ccAddMrTerminalRequest : addTerminalListTemp) {
                    conferenceContext.getConferenceControlApi().addMrTerminal(ccAddMrTerminalRequest);
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        } catch (Exception e) {
        }
        if (getId().startsWith("invite_M_")) {
            conferenceContext.setInvitingTerminal(false);
        }
    }
}
