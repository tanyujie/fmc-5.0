package com.paradisecloud.fcm.mcu.kdc.task;

import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.AttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.InvitedAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.McuAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.TerminalAttendeeForMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcAddMrTerminalRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcCallMrTerminalRequest;
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

    private McuKdcConferenceContext conferenceContext;
    private List<AttendeeForMcuKdc> attendees;

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee) {
        super("invite_1_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuKdcConferenceContext conferenceContext, List<AttendeeForMcuKdc> attendees) {
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
                List<CcAddMrTerminalRequest.Mt> addTerminalListTemp = new ArrayList<>();
                List<CcCallMrTerminalRequest.Mt> connectTerminalListTemp = new ArrayList<>();
                for (AttendeeForMcuKdc attendeeForMcuKdc : attendees) {
                    if (!attendeeForMcuKdc.isMeetingJoined()) {
                        if (conferenceContext != null) {
                            String remoteParty = attendeeForMcuKdc.getRemoteParty();
                            String partyName = attendeeForMcuKdc.getName();
                            String assName = remoteParty;
                            Integer callType = 2;
                            if (attendeeForMcuKdc instanceof TerminalAttendeeForMcuKdc) {
                                TerminalAttendeeForMcuKdc terminalAttendeeForMcuKdc = (TerminalAttendeeForMcuKdc) attendeeForMcuKdc;
                                if (terminalAttendeeForMcuKdc.getTerminalId() != null) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuKdc.getTerminalId());
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
                            } else if (attendeeForMcuKdc instanceof InvitedAttendeeForMcuKdc) {
                                InvitedAttendeeForMcuKdc invitedAttendeeForMcuKdc = (InvitedAttendeeForMcuKdc) attendeeForMcuKdc;
                                if (invitedAttendeeForMcuKdc.getTerminalId() != null) {
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuKdc.getTerminalId());
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
                                callType = invitedAttendeeForMcuKdc.getCallType();
                            } else if (attendeeForMcuKdc instanceof McuAttendeeForMcuKdc) {
                                McuAttendeeForMcuKdc mcuAttendeeForMcuPlc = (McuAttendeeForMcuKdc) attendeeForMcuKdc;
                                String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuPlc.getCascadeConferenceId());
                                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                                if (baseConferenceContext != null) {
                                    remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                                    if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                                        remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                                    }
                                    String oldRemoteParty = mcuAttendeeForMcuPlc.getRemoteParty();
                                    attendeeForMcuKdc.setRemoteParty(remoteParty);
                                    attendeeForMcuKdc.setIp(baseConferenceContext.getMcuCallIp());
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
                                CcCallMrTerminalRequest.Mt mt = new CcCallMrTerminalRequest.Mt();
                                mt.setMt_id(uuid);
                                connectTerminalListTemp.add(mt);
                            } else {
                                CcAddMrTerminalRequest.Mt mt = new CcAddMrTerminalRequest.Mt();
                                mt.setAccount(remoteParty);
                                if (remoteParty.contains("@")) {
                                    mt.setAccount_type(8);
                                } else {
                                    mt.setAccount_type(7);
                                }
                                if (callType != null && callType == 1) {
                                    mt.setProtocol(0);// h323
                                } else {
                                    mt.setProtocol(1);// sip
                                }
                                mt.setBitrate(conferenceContext.getBandwidth());
                                mt.setForced_call(1);
                                addTerminalListTemp.add(mt);
                            }
                        }
                    }
                }

                if (connectTerminalListTemp.size() > 0) {
                    CcCallMrTerminalRequest ccCallMrTerminalRequest = new CcCallMrTerminalRequest();
                    ccCallMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    List<CcCallMrTerminalRequest.Mt> mts = new ArrayList<>();
                    for (CcCallMrTerminalRequest.Mt mt : connectTerminalListTemp) {
                        mts.add(mt);
                    }
                    ccCallMrTerminalRequest.setMts(mts);
                    conferenceContext.getConferenceControlApi().callMrTerminal(ccCallMrTerminalRequest);
                }
                if (addTerminalListTemp.size() > 0) {
                    CcAddMrTerminalRequest ccAddMrTerminalRequest = new CcAddMrTerminalRequest();
                    ccAddMrTerminalRequest.setConf_id(conferenceContext.getConfId());
                    List<CcAddMrTerminalRequest.Mt> mts = new ArrayList<>();
                    for (CcAddMrTerminalRequest.Mt mt : addTerminalListTemp) {
                        mts.add(mt);
                    }
                    ccAddMrTerminalRequest.setMts(mts);
                    conferenceContext.getConferenceControlApi().addMrTerminal(ccAddMrTerminalRequest);
                }
            }
        } catch (Exception e) {
        }
        if (getId().startsWith("invite_M_")) {
            conferenceContext.setInvitingTerminal(false);
        }
    }
}
