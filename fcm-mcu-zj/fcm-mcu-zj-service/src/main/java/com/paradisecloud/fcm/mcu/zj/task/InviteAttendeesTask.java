package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.InvitedAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.McuAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.TerminalAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcAddMrTempUsrsRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrEpsStatusRequest;
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

    private McuZjConferenceContext conferenceContext;
    private List<AttendeeForMcuZj> attendees;

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee) {
        super("invite_1_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = new ArrayList<>();
        this.attendees.add(attendee);
    }

    public InviteAttendeesTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext, List<AttendeeForMcuZj> attendees) {
        super("invite_M_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
        this.attendees = attendees;
    }

    @Override
    public void run() {
        LOGGER.info("MCU_ZJ终端邀请开始。ID:" + getId());

        if (getId().startsWith("invite_M_")) {
            if (conferenceContext.isInvitingTerminal()) {
                LOGGER.info("其它线程正在处理邀请");
                return;
            }
            conferenceContext.setInvitingTerminal(true);
        }

        try {
            if (attendees != null) {
                    List<String> terminalListZj = new ArrayList<>();
                    List<CcAddMrTempUsrsRequest> terminalListTemp = new ArrayList<>();
                    for (AttendeeForMcuZj attendeeForMcuZj : attendees) {
                        if (!attendeeForMcuZj.isMeetingJoined()) {
                            if (conferenceContext != null) {
                                String remoteParty = attendeeForMcuZj.getRemoteParty();
                                CcAddMrTempUsrsRequest ccAddMrTempUsrsRequest = new CcAddMrTempUsrsRequest();
                                ccAddMrTempUsrsRequest.setDisp_name(attendeeForMcuZj.getName());
                                if (attendeeForMcuZj instanceof TerminalAttendeeForMcuZj) {
                                    TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendeeForMcuZj;
                                    if (terminalAttendeeForMcuZj.getTerminalId() != null) {
                                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
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
                                            if (TerminalType.isZJ(busiTerminal.getType())) {
                                                remoteParty = "";
                                                terminalListZj.add(busiTerminal.getCredential());
                                            }
                                        }
                                    }
                                    Integer callType = 2;
                                    if (callType != null && callType == 1) {
                                        ccAddMrTempUsrsRequest.setUsr_type(1);// h323
                                    } else {
                                        ccAddMrTempUsrsRequest.setUsr_type(2);// sip
                                    }
                                } else if (attendeeForMcuZj instanceof InvitedAttendeeForMcuZj) {
                                    InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendeeForMcuZj;
                                    if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
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
                                            if (TerminalType.isZJ(busiTerminal.getType())) {
                                                remoteParty = "";
                                                terminalListZj.add(busiTerminal.getCredential());
                                            }
                                        }
                                    }
                                    Integer callType = invitedAttendeeForMcuZj.getCallType();
                                    if (callType != null && callType == 1) {
                                        ccAddMrTempUsrsRequest.setUsr_type(1);// h323
                                    } else {
                                        ccAddMrTempUsrsRequest.setUsr_type(2);// sip
                                    }
                                } else if (attendeeForMcuZj instanceof McuAttendeeForMcuZj) {
                                    McuAttendeeForMcuZj mcuAttendeeForMcuZj = (McuAttendeeForMcuZj) attendeeForMcuZj;
                                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZj.getCascadeConferenceId());
                                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                                    if (baseConferenceContext != null) {
                                        remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                                        if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                                            remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                                        }
                                        String oldRemoteParty = mcuAttendeeForMcuZj.getRemoteParty();
                                        attendeeForMcuZj.setRemoteParty(remoteParty);
                                        attendeeForMcuZj.setIp(baseConferenceContext.getMcuCallIp());
                                        conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, mcuAttendeeForMcuZj);
                                        if (McuType.MCU_ZJ.getCode().equals(mcuAttendeeForMcuZj.getCascadeMcuType())) {
                                            ccAddMrTempUsrsRequest.setUsr_type(7);// 分会场
                                        } else {
                                            ccAddMrTempUsrsRequest.setUsr_type(2);// sip
                                        }
                                    }
                                }
                                if (StringUtils.isNotEmpty(remoteParty)) {
                                    ccAddMrTempUsrsRequest.setCall_url(remoteParty);
                                    terminalListTemp.add(ccAddMrTempUsrsRequest);
                                }
                            }
                        }
                    }

                    if (terminalListZj.size() > 0) {
                        String[] usrIds = terminalListZj.toArray(new String[terminalListZj.size()]);
                        CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                        ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_add_participants);
                        ccUpdateMrEpsStatusRequest.setUsr_ids(usrIds);
                        boolean result = conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                        if (!result) {
                        }
                    }
                    if (terminalListTemp.size() > 0) {
                        for (CcAddMrTempUsrsRequest ccAddMrTempUsrsRequest : terminalListTemp) {
                            conferenceContext.getConferenceControlApi().addMrTempUsrs(ccAddMrTempUsrsRequest);
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            }
        } catch (Exception e) {
        }
        if (getId().startsWith("invite_M_")) {
            conferenceContext.setInvitingTerminal(false);
        }
    }
}
