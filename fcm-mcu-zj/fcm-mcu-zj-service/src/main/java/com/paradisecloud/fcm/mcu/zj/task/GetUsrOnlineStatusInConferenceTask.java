package com.paradisecloud.fcm.mcu.zj.task;

import com.paradisecloud.fcm.common.enumer.TerminalOnlineStatus;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.AttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.InvitedAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.SelfCallAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.TerminalAttendeeForMcuZj;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmGetUsrOnlineStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmGetUsrOnlineStatusResponse;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.sinhy.spring.BeanFactory;

import java.util.*;

public class GetUsrOnlineStatusInConferenceTask extends DelayTask {

    private McuZjConferenceContext conferenceContext;

    public GetUsrOnlineStatusInConferenceTask(String id, long delayInMilliseconds, McuZjConferenceContext conferenceContext) {
        super("get_eps_online_" + id, delayInMilliseconds);
        this.conferenceContext = conferenceContext;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (conferenceContext != null) {
            McuZjBridge mcuZjBridge = conferenceContext.getMcuZjBridge();
            if (mcuZjBridge != null) {
                List<BusiTerminal> terminalList = new ArrayList<>();
                if (conferenceContext.getMasterAttendee() != null) {
                    AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getMasterAttendee();
                    addTerminal(attendeeForMcuZj, terminalList);
                }
                if (conferenceContext.getAttendees() != null) {
                    for (AttendeeForMcuZj attendeeForMcuZj : conferenceContext.getAttendees()) {
                        addTerminal(attendeeForMcuZj, terminalList);
                    }
                }

                if (conferenceContext.getMasterAttendees() != null) {
                    for (AttendeeForMcuZj attendeeForMcuZj : conferenceContext.getMasterAttendees()) {
                        addTerminal(attendeeForMcuZj, terminalList);
                    }
                }

                for (Long deptId : conferenceContext.getCascadeAttendeesMap().keySet()) {
                    List<AttendeeForMcuZj> attendees = conferenceContext.getCascadeAttendeesMap().get(deptId);
                    if (attendees != null) {
                        for (AttendeeForMcuZj attendeeForMcuZj : attendees) {
                            addTerminal(attendeeForMcuZj, terminalList);
                        }
                    }
                }
                getStatus(terminalList, mcuZjBridge);
            }
        }
    }

    private void addTerminal(AttendeeForMcuZj attendeeForMcuZj, List<BusiTerminal> terminalList) {
        if (attendeeForMcuZj.isMeetingJoined()) {
            return;
        }
        Long terminalId = null;
        if (attendeeForMcuZj instanceof TerminalAttendeeForMcuZj) {
            TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendeeForMcuZj;
            terminalId = terminalAttendeeForMcuZj.getTerminalId();
        } else if (attendeeForMcuZj instanceof SelfCallAttendeeForMcuZj) {
            SelfCallAttendeeForMcuZj selfCallAttendeeForMcuZj = (SelfCallAttendeeForMcuZj) attendeeForMcuZj;
            terminalId = selfCallAttendeeForMcuZj.getTerminalId();
        } else if (attendeeForMcuZj instanceof InvitedAttendeeForMcuZj) {
            InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendeeForMcuZj;
            terminalId = invitedAttendeeForMcuZj.getTerminalId();
        }
        if (terminalId != null) {
            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalId);
            if (TerminalType.isZJ(busiTerminal.getType())) {
                terminalList.add(busiTerminal);
            }
        }
    }

    public void getStatus(List<BusiTerminal> terminalList, McuZjBridge mcuZjBridge) {
        if (terminalList != null && terminalList.size() > 0 && mcuZjBridge != null) {
            List<Integer> userIdList = new ArrayList<>();
            for (BusiTerminal busiTerminal : terminalList) {
                if (TerminalType.isZJ(busiTerminal.getType())) {
                    userIdList.add(busiTerminal.getZjUserId().intValue());
                }
            }
            Map<Integer, Boolean> onlineMap = new HashMap<>();
            if (userIdList.size() > 0) {
                Integer[] userIds = new Integer[userIdList.size()];
                for (int i = 0; i< userIds.length; i++) {
                    userIds[i] = userIdList.get(i);
                }
                CmGetUsrOnlineStatusRequest cmGetUsrOnlineStatusRequest = new CmGetUsrOnlineStatusRequest();
                cmGetUsrOnlineStatusRequest.setUsr_ids(userIds);
                CmGetUsrOnlineStatusResponse cmGetUsrOnlineStatusResponse = mcuZjBridge.getConferenceManageApi().getUsrOnlineStatus(cmGetUsrOnlineStatusRequest);
                if (cmGetUsrOnlineStatusResponse != null && cmGetUsrOnlineStatusResponse.getUsr_ids() != null) {
                    for (int i = 0; i < cmGetUsrOnlineStatusResponse.getUsr_ids().length; i++) {
                        boolean isOnline = false;
                        Integer online_status = cmGetUsrOnlineStatusResponse.getOnline_status()[i];
                        if (online_status != null && online_status > 0) {
                            isOnline = true;
                        }
                        onlineMap.put(cmGetUsrOnlineStatusResponse.getUsr_ids()[i], isOnline);
                    }
                }
            }
            BusiTerminalMapper busiTerminalMapper = BeanFactory.getBean(BusiTerminalMapper.class);
            for (BusiTerminal busiTerminalTemp : terminalList) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(busiTerminalTemp.getId());
                if (TerminalType.isZJ(busiTerminal.getType())) {
                    boolean isOnline = false;
                    Boolean isOnlineTemp = onlineMap.get(busiTerminal.getZjUserId().intValue());
                    if (isOnlineTemp != null) {
                        isOnline = isOnlineTemp;
                    }
                    TerminalOnlineStatus oldStatus = TerminalOnlineStatus.convert(busiTerminal.getOnlineStatus());
                    TerminalOnlineStatus newStatus = TerminalOnlineStatus.OFFLINE;
                    if (isOnline) {
                        newStatus = TerminalOnlineStatus.ONLINE;
                    }
                    processTerminalInfo(busiTerminal, oldStatus, newStatus, busiTerminalMapper);
                }
            }
        }
    }

    private void processTerminalInfo(BusiTerminal busiTerminal, TerminalOnlineStatus oldStatus, TerminalOnlineStatus realStatus, BusiTerminalMapper busiTerminalMapper) {
        if (oldStatus != realStatus) {
            busiTerminal.setOnlineStatus(realStatus.getValue());
            busiTerminalMapper.updateBusiTerminal(busiTerminal);
        }
    }

}
