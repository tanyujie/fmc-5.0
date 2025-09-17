package com.paradisecloud.fcm.mcu.plc.monitor.cc;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.mcu.plc.attendee.model.operation.*;
import com.paradisecloud.fcm.mcu.plc.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.plc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.CcMrInfoRequest;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.*;
import com.paradisecloud.fcm.mcu.plc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IAttendeeForMcuPlcService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiHistoryConferenceForMcuPlcService;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.ISimpleConferenceControlForMcuPlcService;
import com.paradisecloud.fcm.mcu.plc.task.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.spring.BeanFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CcGetChangesThread extends Thread {

    /**
     * key:conferenceNumber
     */
    private Map<String, Thread> conferenceThreadMap = new ConcurrentHashMap<>();
    private Map<String, Thread> pollingThreadMap = new ConcurrentHashMap<>();

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }

            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setCcGetChangesTime(currentTimeMillis);
                for (McuPlcConferenceContext conferenceContext : McuPlcConferenceContextCache.getInstance().values()) {
                    if (conferenceContext.isEnd()) {
                        McuPlcConferenceContextCache.getInstance().remove(conferenceContext.getContextKey());
                    }
                    if (!conferenceThreadMap.containsKey(conferenceContext.getContextKey())) {
                        startConferenceThread(conferenceContext);
                    } else {
                        if (!pollingThreadMap.containsKey(conferenceContext.getContextKey())) {
                            startPollingThread(conferenceContext);
                        } else {
                            Thread pollingThread = pollingThreadMap.get(conferenceContext.getContextKey());
                            if (pollingThread == null || !pollingThread.isAlive() || pollingThread.isInterrupted()) {
                                startPollingThread(conferenceContext);
                            }
                        }
                    }
                }

                for (String contextKey : conferenceThreadMap.keySet()) {
                    Thread thread = conferenceThreadMap.get(contextKey);
                    if (thread == null || !thread.isAlive() || thread.isInterrupted()) {
                        conferenceThreadMap.remove(contextKey);
                        try {
                            pollingThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        pollingThreadMap.remove(contextKey);
                        McuPlcConferenceContext mcuPlcConferenceContext = McuPlcConferenceContextCache.getInstance().get(contextKey);
                        if (mcuPlcConferenceContext != null) {
                            McuPlcConferenceContextCache.getInstance().get(contextKey).cleanLoginInfo();
                        }
                    }
                    if (!McuPlcConferenceContextCache.getInstance().containsKey(contextKey)) {
                        try {
                            conferenceThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        conferenceThreadMap.remove(contextKey);
                        try {
                            pollingThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        pollingThreadMap.remove(contextKey);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void startConferenceThread(McuPlcConferenceContext conferenceContext) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (conferenceContext.isEnd()) {
                        return;
                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    if (conferenceContext.getLastUpdateTime() == 0 || StringUtils.isEmpty(conferenceContext.getMcuPlcBridge().getMcuToken()) || conferenceContext.getLastUpdateTime() - currentTimeMillis > 30000) {
                        // 恢复横幅
                        RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                        ISimpleConferenceControlForMcuPlcService simpleConferenceControlForMcuPlcService = BeanFactory.getBean(ISimpleConferenceControlForMcuPlcService.class);
                        String bannerText = redisCache.getCacheObject(conferenceContext.getId() + "_banner_text");
                        simpleConferenceControlForMcuPlcService.setBanner(conferenceContext, bannerText);
                    }
                    if (StringUtils.isNotEmpty(conferenceContext.getMcuPlcBridge().getMcuToken())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (conferenceContext.isEnd()) {
                                return;
                            }
                            // 会议信息
                            CcMrInfoRequest ccMrInfoRequest = new CcMrInfoRequest();
                            ccMrInfoRequest.setId(conferenceContext.getConfId());
                            CcMrInfoResponse ccMrInfoResponse = conferenceContext.getConferenceControlApi().getMrInfo(ccMrInfoRequest);
                            if (ccMrInfoResponse != null && CommonResponse.STATUS_OK.equals(ccMrInfoResponse.getStatus())) {
                                processMrInfoResponse(ccMrInfoResponse, conferenceContext);
                            } else {
                                conferenceContext.getMcuPlcBridge().setMcuToken("");
                                conferenceContext.getMcuPlcBridge().setMcuUserToken("");
                                conferenceContext.getMcuPlcBridge().setLastUpdateTime(0);
                            }

                            try {
                                sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        conferenceThreadMap.put(conferenceContext.getContextKey(), thread);
    }

    private void startPollingThread(McuPlcConferenceContext conferenceContext) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    try {
                        if (conferenceContext.isEnd()) {
                            return;
                        }
                        // 处理分屏、轮询、点名
                        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
                        if (attendeeOperation != null) {
                            try {
                                attendeeOperation.operate();
                            } catch (Exception e) {
                                if (!(attendeeOperation instanceof DefaultAttendeeOperation)) {
                                    conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
                                    attendeeOperation.cancel();
                                }
                            }
                        }

                        if (conferenceContext.getMessageCloseTime() != null) {
                            if (System.currentTimeMillis() - conferenceContext.getMessageCloseTime() > 0) {
                                RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                                ISimpleConferenceControlForMcuPlcService simpleConferenceControlForMcuPlcService = BeanFactory.getBean(ISimpleConferenceControlForMcuPlcService.class);
                                String bannerText = redisCache.getCacheObject(conferenceContext.getId() + "_banner_text");
                                simpleConferenceControlForMcuPlcService.setBanner(conferenceContext, bannerText);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        pollingThreadMap.put(conferenceContext.getContextKey(), thread);
    }

    private void processMrInfoResponse(CcMrInfoResponse ccMrInfoResponse, McuPlcConferenceContext conferenceContext) {
        if (ccMrInfoResponse != null && CommonResponse.STATUS_OK.equals(ccMrInfoResponse.getStatus())) {
            if (conferenceContext != null) {
                McuPlcDelayTaskService delayTaskService = BeanFactory.getBean(McuPlcDelayTaskService.class);
                if (2 == 1) {
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, true);
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (true ? "" : "解除") + "锁定");
                    conferenceContext.setLocked(true);
                } else {
                    if (conferenceContext.isLocked()) {
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, false);
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (false ? "" : "解除") + "锁定");
                        conferenceContext.setLocked(false);
                        UnlockAttendeesTask unlockAttendeesTask = new UnlockAttendeesTask(conferenceContext.getId(), 10, conferenceContext);
                        delayTaskService.addTask(unlockAttendeesTask);
                    }
                }
                if (2 == 1) {
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, true);
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启录制");
                    conferenceContext.setRecorded(true);
                } else {
                    if (conferenceContext.isRecorded()) {
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, false);
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭录制");
                        conferenceContext.setRecorded(false);
                    }
                }
                conferenceContext.setMuteParties(ccMrInfoResponse.isMuteParties());

                if (conferenceContext.getMasterAttendee() == null) {
                    RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                    try {
                        String masterAttendeeId = redisCache.getCacheObject(conferenceContext.getId() + "_" + "_master_attendee");
                        if (StringUtils.isNotEmpty(masterAttendeeId)) {
                            AttendeeForMcuPlc masterAttendee = conferenceContext.getAttendeeById(masterAttendeeId);
                            if (masterAttendee != null) {
                                conferenceContext.setMasterAttendee(masterAttendee);

                                Map<String, Object> data = new HashMap<>();
                                data.put("oldMasterAttendee", null);
                                data.put("newMasterAttendee", masterAttendee);
                                McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                Set<String> participantNameSet = new HashSet<>();
                Map<String, String> disconnectedParticipantMap = new HashMap<>();

                // 删除终端
                processEpsDeleteResponse(ccMrInfoResponse, conferenceContext);

                // 离会终端
                processEpsLeftResponse(ccMrInfoResponse, conferenceContext, participantNameSet, disconnectedParticipantMap);

                // 在会终端
                processEspInfoResponse(ccMrInfoResponse, conferenceContext, participantNameSet);

                conferenceContext.setParticipantNameSet(participantNameSet);
                conferenceContext.setDisconnectedParticipantMap(disconnectedParticipantMap);
            }
        }
    }

    private void processEspInfoResponse(CcMrInfoResponse ccMrInfoResponse, McuPlcConferenceContext conferenceContext, Set<String> participantNameSet) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean audioMuteChanged = false;
                List<CcMrInfoResponse.TerminalInfo> terminalInfoList = ccMrInfoResponse.getConnectedTerminalInfoList();
                for (CcMrInfoResponse.TerminalInfo terminalInfo : terminalInfoList) {
                    AttendeeForMcuPlc attendee = null;
                    String uuid = terminalInfo.getId();
                    String name = terminalInfo.getName();
                    String remoteParty = StringUtils.isNotEmpty(terminalInfo.getRemoteAddress()) ? terminalInfo.getRemoteAddress() : terminalInfo.getIp();
                    participantNameSet.add(name);
                    boolean changed = false;
                    boolean newJoined = false;

                    Map<String, AttendeeForMcuPlc> attendeeForMcuPlcMap = conferenceContext.getAttendeeMapByUri(remoteParty);
                    if (attendeeForMcuPlcMap != null && attendeeForMcuPlcMap.size() > 0) {
                        for (String key : attendeeForMcuPlcMap.keySet()) {
                            attendee = attendeeForMcuPlcMap.get(key);
                        }
                    }
                    if (attendee == null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                        if (busiTerminal != null) {
                            SelfCallAttendeeForMcuPlc selfCallAttendee = new SelfCallAttendeeForMcuPlc();
                            attendee = selfCallAttendee;
                            attendee.setTerminalId(busiTerminal.getId());
                            attendee.setSn(busiTerminal.getSn());
                            if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                attendee.setName(busiTerminal.getName() + "(" + name + ")");
                            } else {
                                attendee.setName(busiTerminal.getName());
                            }
                            attendee.setId(uuid);
                            if ("sip".equals(terminalInfo.getProtocol())) {
                                attendee.setProtoType(2);
                            } else {
                                attendee.setProtoType(1);
                            }
                            if ("personal".equals(terminalInfo.getLayoutType())) {
                                attendee.setPersonalLayout(true);
                            } else {
                                attendee.setPersonalLayout(false);
                            }
                            if ("dial_in".equals(terminalInfo.getCallingMode())) {
                                attendee.setDirection("incoming");
                            } else {
                                attendee.setDirection("outgoing");
                            }
                            String presentAttendId = conferenceContext.getPresentAttendeeId();
                            if (terminalInfo.isContentProvider()) {
                                if (YesOrNo.NO.getValue() == attendee.getPresentStatus()) {
                                    attendee.setPresentStatus(YesOrNo.YES.getValue());
                                    changed = true;
                                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在共享屏幕！");
                                }
                                conferenceContext.setPresentAttendeeId(attendee.getId());
                            } else {
                                if (YesOrNo.YES.getValue() == attendee.getPresentStatus()) {
                                    attendee.setPresentStatus(YesOrNo.NO.getValue());
                                    changed = true;
                                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】关闭共享屏幕！");
                                }
                                if (attendee.getId().equals(presentAttendId)) {
                                    conferenceContext.setPresentAttendeeId(null);
                                }
                            }
                            if (!terminalInfo.isAudioMute()) {
                                if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                    changed = true;
                                    audioMuteChanged = true;
                                }
                            } else {
                                if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                    attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                    McuPlcMuteStatusCheckTask muteStatusCheckTask = new McuPlcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                    BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(muteStatusCheckTask);
                                    changed = true;
                                    audioMuteChanged = true;
                                }
                            }
                            if (!terminalInfo.isVideoMute()) {
                                if (AttendeeVideoStatus.NO.getValue() == attendee.getVideoStatus()) {
                                    attendee.setVideoStatus(AttendeeVideoStatus.YES.getValue());
                                    changed = true;
                                }
                            } else {
                                if (AttendeeVideoStatus.YES.getValue() == attendee.getVideoStatus()) {
                                    attendee.setVideoStatus(AttendeeVideoStatus.NO.getValue());
                                    changed = true;
                                }
                            }
                            attendee.setParticipantUuid(uuid);
                            attendee.setHangUp(false);
                            attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                            attendee.setRemoteParty(remoteParty);
                            attendee.setIp(remoteParty);
                            attendee.setDeptId(conferenceContext.getDeptId());
                            attendee.setConferenceNumber(conferenceContext.getConferenceNumber());
                            attendee.setContextKey(conferenceContext.getContextKey());
                            attendee.setWeight(1);
                            attendee.setJoinedTime(terminalInfo.getConnectTime());
                            if (conferenceContext.isLocked()) {
                                attendee.setLocked(true);
                            }
                            conferenceContext.addAttendee(attendee);
                            if (conferenceContext.getMuteType() == 1) {
                                if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                    McuPlcAttendeeSetMuteTask mcuPlcAttendeeSetMuteTask = new McuPlcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
                                    BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(mcuPlcAttendeeSetMuteTask);
                                    attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                }
                            } else {
                                if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
                                    McuPlcAttendeeSetMuteTask mcuPlcAttendeeSetMuteTask = new McuPlcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
                                    BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(mcuPlcAttendeeSetMuteTask);
                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                }
                            }
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            updateOperation(conferenceContext);

                            conferenceContext.addUuidAttendee(attendee);

                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                            if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                                BeanFactory.getBean(IAttendeeForMcuPlcService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                            }

                            continue;
                        }
                    }
                    if (attendee != null) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuPlc) {
                            TerminalAttendeeForMcuPlc terminalAttendeeForMcuPlc = (TerminalAttendeeForMcuPlc) attendee;
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuPlc.getTerminalId());
                            if (busiTerminal != null) {
                                if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                } else {
                                    attendee.setName(busiTerminal.getName());
                                }
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuPlc) {
                            InvitedAttendeeForMcuPlc invitedAttendeeForMcuPlc = (InvitedAttendeeForMcuPlc) attendee;
                            if (invitedAttendeeForMcuPlc.getTerminalId() != null) {
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuPlc.getTerminalId());
                                if (busiTerminal != null) {
                                    if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                    } else {
                                        attendee.setName(busiTerminal.getName());
                                    }
                                }
                            }
                        } else if (attendee instanceof McuAttendeeForMcuPlc) {
                            attendee.getUpdateMap().put("ip", attendee.getIp());
                            attendee.getUpdateMap().put("remoteParty", attendee.getRemoteParty());
                        }
                        if ("sip".equals(terminalInfo.getProtocol())) {
                            attendee.setProtoType(2);
                        } else {
                            attendee.setProtoType(1);
                        }
                        if ("personal".equals(terminalInfo.getLayoutType())) {
                            attendee.setPersonalLayout(true);
                        } else {
                            attendee.setPersonalLayout(false);
                        }
                        if ("dial_in".equals(terminalInfo.getCallingMode())) {
                            attendee.setDirection("incoming");
                        } else {
                            attendee.setDirection("outgoing");
                        }
                        String presentAttendId = conferenceContext.getPresentAttendeeId();
                        if (terminalInfo.isContentProvider()) {
                            if (YesOrNo.NO.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.YES.getValue());
                                changed = true;
                                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在共享屏幕！");
                            }
                            conferenceContext.setPresentAttendeeId(attendee.getId());
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.NO.getValue());
                                changed = true;
                                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】关闭共享屏幕！");
                            }
                            if (attendee.getId().equals(presentAttendId)) {
                                conferenceContext.setPresentAttendeeId(null);
                            }
                        }
                        if (!terminalInfo.isAudioMute()) {
                            if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                changed = true;
                                audioMuteChanged = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuPlcMuteStatusCheckTask muteStatusCheckTask = new McuPlcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                                audioMuteChanged = true;
                            }
                        }
                        if (!terminalInfo.isVideoMute()) {
                            if (AttendeeVideoStatus.NO.getValue() == attendee.getVideoStatus()) {
                                attendee.setVideoStatus(AttendeeVideoStatus.YES.getValue());
                                changed = true;
                            }
                        } else {
                            if (AttendeeVideoStatus.YES.getValue() == attendee.getVideoStatus()) {
                                attendee.setVideoStatus(AttendeeVideoStatus.NO.getValue());
                                changed = true;
                            }
                        }
                        if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()
                                && terminalInfo.getId().equals(ccMrInfoResponse.getAudioSourceId())
                                && (StringUtils.isEmpty(conferenceContext.getLastSpeakerId())
                                || !conferenceContext.getLastSpeakerId().equals(ccMrInfoResponse.getAudioSourceId())
                                || System.currentTimeMillis() - conferenceContext.getLastSpeakerUpdateTime() >= 10000)) {
                            conferenceContext.setLastSpeakerId(terminalInfo.getId());
                            conferenceContext.setLastSpeakerUpdateTime(System.currentTimeMillis());
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在发言！");
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put(attendee.getId(), true);
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_SPEAKER, updateMap);
                        }
                        attendee.setParticipantUuid(uuid);
                        attendee.setHangUp(false);
                        if (attendee.getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
                            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                            changed = true;
                            newJoined = true;
                        }
                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        attendee.setJoinedTime(terminalInfo.getConnectTime());
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        if (changed) {
                            if (newJoined) {
                                if (conferenceContext.getMuteType() == 1) {
                                    if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                        McuPlcAttendeeSetMuteTask mcuPlcAttendeeSetMuteTask = new McuPlcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
                                        BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(mcuPlcAttendeeSetMuteTask);
                                        attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                    }
                                } else {
                                    if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
                                        McuPlcAttendeeSetMuteTask mcuPlcAttendeeSetMuteTask = new McuPlcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
                                        BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(mcuPlcAttendeeSetMuteTask);
                                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                    }
                                }
                            }
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            updateOperation(conferenceContext);

                            conferenceContext.addUuidAttendee(attendee);

                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                        }
                    } else {
                        SelfCallAttendeeForMcuPlc selfCallAttendee = new SelfCallAttendeeForMcuPlc();
                        selfCallAttendee.setName(name);
                        if (StringUtils.isNotEmpty(remoteParty)) {
                            selfCallAttendee.setRemoteParty(remoteParty);
                            BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                            if (busiTerminal == null) {
                                if (remoteParty.contains("@")) {
                                    try {
                                        String[] remotePartyArr = remoteParty.split("@");
                                        String credential = remotePartyArr[0];
                                        String ip = remotePartyArr[1];
                                        if (org.springframework.util.StringUtils.hasText(ip)) {
                                            FsbcBridge fsbcBridge = FsbcBridgeCache.getInstance().getByDomainName(ip);
                                            if (fsbcBridge != null) {
                                                String remotePartyNew = credential + "@" + fsbcBridge.getBusiFsbcRegistrationServer().getCallIp();
                                                busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                            }
                                            if (busiTerminal == null) {
                                                FcmBridge fcmBridge = FcmBridgeCache.getInstance().getByDomainName(ip);
                                                if (fcmBridge != null) {
                                                    String remotePartyNew = credential + "@" + fcmBridge.getBusiFreeSwitch().getIp();
                                                    busiTerminal = TerminalCache.getInstance().getByRemoteParty(remotePartyNew);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            if (busiTerminal != null) {
                                selfCallAttendee.setTerminalId(busiTerminal.getId());
                                selfCallAttendee.setSn(busiTerminal.getSn());
                                if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                    selfCallAttendee.setName(busiTerminal.getName() + "(" + name + ")");
                                } else {
                                    selfCallAttendee.setName(busiTerminal.getName());
                                }
                                BusiUserTerminalMapper busiUserTerminalMapper = BeanFactory.getBean(BusiUserTerminalMapper.class);
                                BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(selfCallAttendee.getTerminalId());
                                if (busiUserTerminal != null) {
                                    selfCallAttendee.setUserId(busiUserTerminal.getUserId());
                                }
                            }
                        }
                        attendee = selfCallAttendee;
                        attendee.setId(uuid);
                        if ("sip".equals(terminalInfo.getProtocol())) {
                            attendee.setProtoType(2);
                        } else {
                            attendee.setProtoType(1);
                        }
                        if ("personal".equals(terminalInfo.getLayoutType())) {
                            attendee.setPersonalLayout(true);
                        } else {
                            attendee.setPersonalLayout(false);
                        }
                        if ("dial_in".equals(terminalInfo.getCallingMode())) {
                            attendee.setDirection("incoming");
                        } else {
                            attendee.setDirection("outgoing");
                        }
                        String presentAttendId = conferenceContext.getPresentAttendeeId();
                        if (terminalInfo.isContentProvider()) {
                            if (YesOrNo.NO.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.YES.getValue());
                                changed = true;
                                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在共享屏幕！");
                            }
                            conferenceContext.setPresentAttendeeId(attendee.getId());
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.NO.getValue());
                                changed = true;
                                McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】关闭共享屏幕！");
                            }
                            if (attendee.getId().equals(presentAttendId)) {
                                conferenceContext.setPresentAttendeeId(null);
                            }
                        }
                        if (!terminalInfo.isAudioMute()) {
                            if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                changed = true;
                                audioMuteChanged = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuPlcMuteStatusCheckTask muteStatusCheckTask = new McuPlcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                                audioMuteChanged = true;
                            }
                        }
                        if (!terminalInfo.isVideoMute()) {
                            if (AttendeeVideoStatus.NO.getValue() == attendee.getVideoStatus()) {
                                attendee.setVideoStatus(AttendeeVideoStatus.YES.getValue());
                                changed = true;
                            }
                        } else {
                            if (AttendeeVideoStatus.YES.getValue() == attendee.getVideoStatus()) {
                                attendee.setVideoStatus(AttendeeVideoStatus.NO.getValue());
                                changed = true;
                            }
                        }
                        attendee.setParticipantUuid(uuid);
                        attendee.setHangUp(false);
                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                        attendee.setRemoteParty(remoteParty);
                        attendee.setIp(remoteParty);
                        attendee.setDeptId(conferenceContext.getDeptId());
                        attendee.setConferenceNumber(conferenceContext.getConferenceNumber());
                        attendee.setContextKey(conferenceContext.getContextKey());
                        attendee.setWeight(1);
                        attendee.setJoinedTime(terminalInfo.getConnectTime());
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        conferenceContext.addAttendee(attendee);
                        if (conferenceContext.getMuteType() == 1) {
                            if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                McuPlcAttendeeSetMuteTask mcuPlcAttendeeSetMuteTask = new McuPlcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
                                BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(mcuPlcAttendeeSetMuteTask);
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                            }
                        } else {
                            if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
                                McuPlcAttendeeSetMuteTask mcuPlcAttendeeSetMuteTask = new McuPlcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
                                BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(mcuPlcAttendeeSetMuteTask);
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            }
                        }
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        updateOperation(conferenceContext);

                        conferenceContext.addUuidAttendee(attendee);

                        BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                        // 更新数据库
                        processUpdateParticipant(conferenceContext, attendee, true);
                        if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                            BeanFactory.getBean(IAttendeeForMcuPlcService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                        }
                    }
                }

                if (audioMuteChanged) {
                    McuPlcMuteStatusCheckTask muteStatusCheckTask = new McuPlcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(muteStatusCheckTask);
                }
            }
        }).start();
    }

    private void processEpsLeftResponse(CcMrInfoResponse ccMrInfoResponse, McuPlcConferenceContext conferenceContext, Set<String> participantNameSet, Map<String, String> disconnectedParticipantMap) {
        List<CcMrInfoResponse.TerminalInfo> terminalInfoList = ccMrInfoResponse.getDisconnectedTerminalInfoList();
        for (CcMrInfoResponse.TerminalInfo terminalInfo : terminalInfoList) {
            String uuid = terminalInfo.getId();
            String name = terminalInfo.getName();
            String remoteParty = StringUtils.isNotEmpty(terminalInfo.getRemoteAddress()) ? terminalInfo.getRemoteAddress() : terminalInfo.getIp();
            participantNameSet.add(name);
            disconnectedParticipantMap.put(remoteParty, uuid);

            AttendeeForMcuPlc attendeeForMcuPlc = conferenceContext.getAttendeeByUuid(uuid);
            if (attendeeForMcuPlc != null && uuid.equals(attendeeForMcuPlc.getParticipantUuid())) {
                if (attendeeForMcuPlc.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                    McuPlcMuteStatusCheckTask muteStatusCheckTask = new McuPlcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(muteStatusCheckTask);
                }

                IAttendeeForMcuPlcService attendeeForMcuPlcService = BeanFactory.getBean(IAttendeeForMcuPlcService.class);
                if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                    if (attendeeForMcuPlc.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                        attendeeForMcuPlcService.cancelCallTheRoll(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                    if (attendeeForMcuPlc.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                        attendeeForMcuPlcService.cancelTalk(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                    if (attendeeForMcuPlc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                        attendeeForMcuPlcService.cancelChooseSee(conferenceContext.getId());
                    }
                }
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    defaultAttendeeOperation.operate();
                }

                attendeeForMcuPlc.resetUpdateMap();
                attendeeForMcuPlc.leaveMeeting();
                if (attendeeForMcuPlc instanceof McuAttendeeForMcuPlc) {
                    McuAttendeeForMcuPlc mcuAttendeeForMcuPlc = (McuAttendeeForMcuPlc) attendeeForMcuPlc;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuPlc.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuPlc.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuPlc.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuPlc);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuPlc.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuPlc.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuPlc.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuPlc.isMcuAttendee());
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        }
                    }
                } else if (attendeeForMcuPlc instanceof TerminalAttendeeForMcuPlc) {
                    TerminalAttendeeForMcuPlc terminalAttendeeForMcuPlc = (TerminalAttendeeForMcuPlc) attendeeForMcuPlc;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuPlc.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuPlc.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                } else if (attendeeForMcuPlc instanceof InvitedAttendeeForMcuPlc) {
                    InvitedAttendeeForMcuPlc invitedAttendeeForMcuPlc = (InvitedAttendeeForMcuPlc) attendeeForMcuPlc;
                    if (invitedAttendeeForMcuPlc.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuPlc.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuPlc.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuPlc.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuPlc.getId());
                        updateMap.put("deptId", attendeeForMcuPlc.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuPlc.isMcuAttendee());
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuPlc.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuPlc.getId());
                    updateMap.put("deptId", attendeeForMcuPlc.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuPlc.isMcuAttendee());
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
                String reason = "【" + attendeeForMcuPlc.getName() + "】离会";
                McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuPlc);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuPlc);

                attendeeForMcuPlc.setParticipantUuid(null);
            }
        }
    }

    private void processEpsDeleteResponse(CcMrInfoResponse ccMrInfoResponse, McuPlcConferenceContext conferenceContext) {
        Set<String> deletedTerminalIdSet = ccMrInfoResponse.getDeletedTerminalIdSet();
        for (String uuid : deletedTerminalIdSet) {
            AttendeeForMcuPlc attendeeForMcuPlc = conferenceContext.getAttendeeByUuid(uuid);
            if (attendeeForMcuPlc != null && uuid.equals(attendeeForMcuPlc.getParticipantUuid())) {
                if (attendeeForMcuPlc.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                    McuPlcMuteStatusCheckTask muteStatusCheckTask = new McuPlcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuPlcDelayTaskService.class).addTask(muteStatusCheckTask);
                }

                IAttendeeForMcuPlcService attendeeForMcuPlcService = BeanFactory.getBean(IAttendeeForMcuPlcService.class);
                if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                    if (attendeeForMcuPlc.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                        attendeeForMcuPlcService.cancelCallTheRoll(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                    if (attendeeForMcuPlc.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                        attendeeForMcuPlcService.cancelTalk(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                    if (attendeeForMcuPlc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                        attendeeForMcuPlcService.cancelChooseSee(conferenceContext.getId());
                    }
                }
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    defaultAttendeeOperation.operate();
                }

                attendeeForMcuPlc.resetUpdateMap();
                attendeeForMcuPlc.leaveMeeting();
                if (attendeeForMcuPlc instanceof McuAttendeeForMcuPlc) {
                    McuAttendeeForMcuPlc mcuAttendeeForMcuPlc = (McuAttendeeForMcuPlc) attendeeForMcuPlc;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuPlc.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuPlc.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuPlc.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuPlc);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuPlc.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuPlc.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuPlc.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuPlc.isMcuAttendee());
                            McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        }
                    }
                } else if (attendeeForMcuPlc instanceof TerminalAttendeeForMcuPlc) {
                    TerminalAttendeeForMcuPlc terminalAttendeeForMcuPlc = (TerminalAttendeeForMcuPlc) attendeeForMcuPlc;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuPlc.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuPlc.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                } else if (attendeeForMcuPlc instanceof InvitedAttendeeForMcuPlc) {
                    InvitedAttendeeForMcuPlc invitedAttendeeForMcuPlc = (InvitedAttendeeForMcuPlc) attendeeForMcuPlc;
                    if (invitedAttendeeForMcuPlc.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuPlc.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuPlc.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuPlc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuPlc.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuPlc.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuPlc.getId());
                        updateMap.put("deptId", attendeeForMcuPlc.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuPlc.isMcuAttendee());
                        McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuPlc.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuPlc.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuPlc.getId());
                    updateMap.put("deptId", attendeeForMcuPlc.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuPlc.isMcuAttendee());
                    McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuPlcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
                String reason = "【" + attendeeForMcuPlc.getName() + "】离会";
                McuPlcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuPlc);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuPlc);
                conferenceContext.removeUuidAttendee(attendeeForMcuPlc);
            }
        }
    }

    private void processUpdateParticipant(McuPlcConferenceContext conferenceContext, AttendeeForMcuPlc attendee) {
        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private void processUpdateParticipant(McuPlcConferenceContext conferenceContext, AttendeeForMcuPlc attendee, boolean updateMediaInfo) {
        IBusiHistoryConferenceForMcuPlcService busiHistoryConferenceForMcuPlcService = BeanFactory.getBean(IBusiHistoryConferenceForMcuPlcService.class);
        busiHistoryConferenceForMcuPlcService.updateBusiHistoryParticipant(conferenceContext, attendee, updateMediaInfo);
    }

    private void clearMasterAttendee(McuPlcConferenceContext conferenceContext) {
        AttendeeForMcuPlc oldMasterAttendee = conferenceContext.getMasterAttendee();
        Map<String, Object> data = new HashMap<>();
        data.put("oldMasterAttendee", oldMasterAttendee);
        data.put("newMasterAttendee", null);
        McuPlcWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("主会场已离会【").append(oldMasterAttendee.getName()).append("】");
        McuPlcWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        conferenceContext.clearMasterAttendee();
    }

    private void updateOperation(McuPlcConferenceContext conferenceContext) {
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        if (attendeeOperation instanceof DefaultAttendeeOperation) {
            attendeeOperation.setForceUpdateView(true);
        }
    }

}
