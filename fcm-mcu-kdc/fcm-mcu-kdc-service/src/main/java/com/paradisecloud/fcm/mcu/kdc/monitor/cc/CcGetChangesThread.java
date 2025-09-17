package com.paradisecloud.fcm.mcu.kdc.monitor.cc;

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
import com.paradisecloud.fcm.mcu.kdc.attendee.model.operation.*;
import com.paradisecloud.fcm.mcu.kdc.attendee.utils.McuKdcConferenceContextUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.kdc.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcGetTerminalListRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.CcMrInfoRequest;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.*;
import com.paradisecloud.fcm.mcu.kdc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiHistoryConferenceForMcuKdcService;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.ISimpleConferenceControlForMcuKdcService;
import com.paradisecloud.fcm.mcu.kdc.task.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.sinhy.spring.BeanFactory;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CcGetChangesThread extends Thread {

    /**
     * key:conferenceNumber
     */
    private Map<String, Thread> conferenceThreadMap = new ConcurrentHashMap<>();
    private Map<String, Thread> pollingThreadMap = new ConcurrentHashMap<>();
    private Map<String, Long> lastUpdateTimeMap = new ConcurrentHashMap<>();

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }

            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setCcGetChangesTime(currentTimeMillis);
                for (McuKdcConferenceContext conferenceContext : McuKdcConferenceContextCache.getInstance().values()) {
                    if (conferenceContext.isEnd()) {
                        McuKdcConferenceContextCache.getInstance().remove(conferenceContext.getContextKey());
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
                        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(contextKey);
                        if (mcuKdcConferenceContext != null) {
                            McuKdcConferenceContextCache.getInstance().get(contextKey).cleanLoginInfo();
                        }
                    }
                    if (!McuKdcConferenceContextCache.getInstance().containsKey(contextKey)) {
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

    public void startConferenceThread(McuKdcConferenceContext conferenceContext) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (conferenceContext.isEnd()) {
                        return;
                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    if (conferenceContext.getLastUpdateTime() == 0 || StringUtils.isEmpty(conferenceContext.getMcuKdcBridge().getToken()) || conferenceContext.getLastUpdateTime() - currentTimeMillis > 30000) {
                        // 恢复横幅
                        RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                        ISimpleConferenceControlForMcuKdcService simpleConferenceControlForMcuKdcService = BeanFactory.getBean(ISimpleConferenceControlForMcuKdcService.class);
                        String bannerText = redisCache.getCacheObject(conferenceContext.getId() + "_banner_text");
                        simpleConferenceControlForMcuKdcService.setBanner(conferenceContext, bannerText);
                    }
                    if (StringUtils.isNotEmpty(conferenceContext.getMcuKdcBridge().getToken())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (conferenceContext.isEnd()) {
                                return;
                            }
                            // 订阅会议信息
                            if (conferenceContext.getBayeuxClient() == null || conferenceContext.getBayeuxClient() != conferenceContext.getMcuKdcBridge().getBayeuxClient()) {
                                try {
                                    String channel = "/userdomains/" + conferenceContext.getMcuKdcBridge().getUserDomainMoid() + "/confs/" + conferenceContext.getConfId() + "/**";
                                    conferenceContext.getMcuKdcBridge().getBayeuxClient().getChannel(channel).subscribe(messageListener, subscribeListener);
                                    conferenceContext.setBayeuxClient(conferenceContext.getMcuKdcBridge().getBayeuxClient());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (conferenceContext.getBayeuxClient() != null) {

                            }
                            Long lastUpdateTime = lastUpdateTimeMap.get(conferenceContext.getContextKey());
                            if (lastUpdateTime == null) {
                                lastUpdateTime = 0l;
                            }
                            if (lastUpdateTime == 0) {
                                // 会议信息
                                CcMrInfoRequest ccMrInfoRequest = new CcMrInfoRequest();
                                ccMrInfoRequest.setConf_id(conferenceContext.getConfId());
                                CcMrInfoResponse ccMrInfoResponse = conferenceContext.getConferenceControlApi().getMrInfo(ccMrInfoRequest);
                                if (ccMrInfoResponse != null && ccMrInfoResponse.isSuccess()) {
                                    processMrInfoResponse(ccMrInfoResponse, conferenceContext);
                                }
                                // 终端列表
                                CcGetTerminalListRequest ccGetTerminalListRequest = new CcGetTerminalListRequest();
                                ccGetTerminalListRequest.setConf_id(conferenceContext.getConfId());
                                CcGetTerminalListResponse ccGetTerminalListResponse = conferenceContext.getConferenceControlApi().getTerminalList(ccGetTerminalListRequest);
                                if (ccGetTerminalListResponse != null && ccGetTerminalListResponse.isSuccess()) {
                                    processEspInfoResponse(ccGetTerminalListResponse, conferenceContext);
                                }
                                lastUpdateTimeMap.put(conferenceContext.getContextKey(), currentTimeMillis);
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

    private void startPollingThread(McuKdcConferenceContext conferenceContext) {
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
                                ISimpleConferenceControlForMcuKdcService simpleConferenceControlForMcuKdcService = BeanFactory.getBean(ISimpleConferenceControlForMcuKdcService.class);
                                String bannerText = redisCache.getCacheObject(conferenceContext.getId() + "_banner_text");
                                simpleConferenceControlForMcuKdcService.setBanner(conferenceContext, bannerText);
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

    private ClientSessionChannel.MessageListener messageListener = new ClientSessionChannel.MessageListener() {
        @Override
        public void onMessage(ClientSessionChannel clientSessionChannel, Message message) {
            //通道名
            String channel = message.getChannel();
            String[] channelArr = channel.split("/");
            if (channelArr.length > 4) {
                String conferenceNum = channelArr[4];
                if (channel.endsWith("/confs/" + conferenceNum)
                        || (channel.contains("/confs/" + conferenceNum + "/") && channel.contains("/mts/"))
                        || (channel.contains("/confs/" + conferenceNum + "/") && channel.contains("/mixs/"))) {

                    lastUpdateTimeMap.put(conferenceNum, 0l);
                }
            }
        }
    };

    private org.cometd.bayeux.client.ClientSession.MessageListener subscribeListener = new ClientSession.MessageListener() {
        @Override
        public void onMessage(Message message) {
            //通道名
            String channel = message.getChannel();
            String[] channelArr = channel.split("/");
            if (channelArr.length >= 2) {
                String conferenceNum = channelArr[1];
                if (channel.equals("confs/" + conferenceNum)
                || (channel.startsWith("confs/" + conferenceNum + "/") && channel.contains("/mts/"))
                || (channel.startsWith("confs/" + conferenceNum + "/") && channel.contains("/mixs/"))) {

//                    lastUpdateTimeMap.put(conferenceNum, 0l);
                }
            }

            //通道方法
            Map<String, Object> data = message.getDataAsMap();
            String method = data.get("method").toString();
        }
    };

    private void processMrInfoResponse(CcMrInfoResponse ccMrInfoResponse, McuKdcConferenceContext conferenceContext) {
        if (ccMrInfoResponse != null && ccMrInfoResponse.isSuccess()) {
            if (conferenceContext != null) {
                McuKdcDelayTaskService delayTaskService = BeanFactory.getBean(McuKdcDelayTaskService.class);
                if (ccMrInfoResponse.getClosed_conf() == 1) {
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, true);
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (true ? "" : "解除") + "锁定");
                    conferenceContext.setLocked(true);
                } else {
                    if (conferenceContext.isLocked()) {
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, false);
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (false ? "" : "解除") + "锁定");
                        conferenceContext.setLocked(false);
                        UnlockAttendeesTask unlockAttendeesTask = new UnlockAttendeesTask(conferenceContext.getId(), 10, conferenceContext);
                        delayTaskService.addTask(unlockAttendeesTask);
                    }
                }
                if (2 == 1) {
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, true);
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启录制");
                    conferenceContext.setRecorded(true);
                } else {
                    if (conferenceContext.isRecorded()) {
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, false);
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭录制");
                        conferenceContext.setRecorded(false);
                    }
                }
                conferenceContext.setMuteParties(ccMrInfoResponse.getMix_enable() == 0);

                if (conferenceContext.getMasterAttendee() == null) {
                    RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                    try {
                        String masterAttendeeId = redisCache.getCacheObject(conferenceContext.getId() + "_" + "_master_attendee");
                        if (StringUtils.isNotEmpty(masterAttendeeId)) {
                            AttendeeForMcuKdc masterAttendee = conferenceContext.getAttendeeById(masterAttendeeId);
                            if (masterAttendee != null && masterAttendee.isMeetingJoined()) {
                                conferenceContext.setMasterAttendee(masterAttendee);

                                Map<String, Object> data = new HashMap<>();
                                data.put("oldMasterAttendee", null);
                                data.put("newMasterAttendee", masterAttendee);
                                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private void processEspInfoResponse(CcGetTerminalListResponse ccGetTerminalListResponse, McuKdcConferenceContext conferenceContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean audioMuteChanged = false;
                List<CcGetTerminalListResponse.Mt> onlineTerminalList = new ArrayList<>();
                List<CcGetTerminalListResponse.Mt> offlineTerminalList = new ArrayList<>();
                Set<String> remotePartySet = new HashSet<>();
                Map<String, String> disconnectedParticipantMap = new HashMap<>();
                List<CcGetTerminalListResponse.Mt> terminalInfoList = ccGetTerminalListResponse.getMts();
                for (CcGetTerminalListResponse.Mt mt : terminalInfoList) {
                    String uuid = mt.getMt_id();
                    Integer online = mt.getOnline();
                    String remoteParty = StringUtils.isNotEmpty(mt.getAccount()) ? mt.getAccount() : mt.getIp();
                    if (online != null && online == 1) {
                        onlineTerminalList.add(mt);
                    } else {
                        offlineTerminalList.add(mt);
                    }
                    remotePartySet.add(remoteParty);
                    disconnectedParticipantMap.put(remoteParty, uuid);
                }
                conferenceContext.setDisconnectedParticipantMap(disconnectedParticipantMap);
                McuKdcConferenceContextUtils.eachAttendeeInConference(conferenceContext, (attendee) -> {
                    if (!remotePartySet.contains(attendee.getRemoteParty())) {
                        processEpsDeleteResponse(attendee, conferenceContext);
                    }
                });
                processEpsLeftResponse(offlineTerminalList, conferenceContext);
                for (CcGetTerminalListResponse.Mt mt : onlineTerminalList) {
                    AttendeeForMcuKdc attendee = null;
                    String uuid = mt.getMt_id();
                    String name = mt.getAlias();
                    String remoteParty = StringUtils.isNotEmpty(mt.getAccount()) ? mt.getAccount() : mt.getIp();
                    boolean changed = false;
                    boolean newJoined = false;

                    Map<String, AttendeeForMcuKdc> attendeeForMcuKdcMap = conferenceContext.getAttendeeMapByUri(remoteParty);
                    if (attendeeForMcuKdcMap != null && attendeeForMcuKdcMap.size() > 0) {
                        for (String key : attendeeForMcuKdcMap.keySet()) {
                            attendee = attendeeForMcuKdcMap.get(key);
                        }
                    }
                    if (attendee == null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                        if (busiTerminal != null) {
                            SelfCallAttendeeForMcuKdc selfCallAttendee = new SelfCallAttendeeForMcuKdc();
                            attendee = selfCallAttendee;
                            attendee.setTerminalId(busiTerminal.getId());
                            attendee.setSn(busiTerminal.getSn());
                            if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                attendee.setName(busiTerminal.getName() + "(" + name + ")");
                            } else {
                                attendee.setName(busiTerminal.getName());
                            }
                            attendee.setId(uuid);
                            if (mt.getProtocol() == 1) {
                                attendee.setProtoType(2);
                            } else {
                                attendee.setProtoType(1);
                            }
                            attendee.setDirection("outgoing");
                            String presentAttendId = conferenceContext.getPresentAttendeeId();
                            if (mt.getDual() != null && mt.getDual() == 1) {
                                if (YesOrNo.NO.getValue() == attendee.getPresentStatus()) {
                                    attendee.setPresentStatus(YesOrNo.YES.getValue());
                                    changed = true;
                                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在共享屏幕！");
                                }
                                conferenceContext.setPresentAttendeeId(attendee.getId());
                            } else {
                                if (YesOrNo.YES.getValue() == attendee.getPresentStatus()) {
                                    attendee.setPresentStatus(YesOrNo.NO.getValue());
                                    changed = true;
                                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】关闭共享屏幕！");
                                }
                                if (attendee.getId().equals(presentAttendId)) {
                                    conferenceContext.setPresentAttendeeId(null);
                                }
                            }
                            if (mt.getMix() == 1 || (conferenceContext.isDiscuss() && mt.getMute() == 0)) {
                                if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                    changed = true;
                                    audioMuteChanged = true;
                                }
                            } else {
                                if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                    attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                    McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                    BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
                                    changed = true;
                                    audioMuteChanged = true;
                                }
                            }
                            attendee.setVideoStatus(AttendeeVideoStatus.YES.getValue());
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
                            if (conferenceContext.isLocked()) {
                                attendee.setLocked(true);
                            }
                            conferenceContext.addAttendee(attendee);
                            if (conferenceContext.getMuteType() == 1) {
                                if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                    McuKdcAttendeeSetMuteTask mcuKdcAttendeeSetMuteTask = new McuKdcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
                                    BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(mcuKdcAttendeeSetMuteTask);
                                    attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                }
                            } else {
                                if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
                                    McuKdcAttendeeSetMuteTask mcuKdcAttendeeSetMuteTask = new McuKdcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
                                    BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(mcuKdcAttendeeSetMuteTask);
                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                }
                            }
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            updateOperation(conferenceContext);

                            conferenceContext.addUuidAttendee(attendee);

                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                            if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                                BeanFactory.getBean(IAttendeeForMcuKdcService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                            }

                            continue;
                        }
                    }
                    if (attendee != null) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuKdc) {
                            TerminalAttendeeForMcuKdc terminalAttendeeForMcuKdc = (TerminalAttendeeForMcuKdc) attendee;
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuKdc.getTerminalId());
                            if (busiTerminal != null) {
                                if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                } else {
                                    attendee.setName(busiTerminal.getName());
                                }
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuKdc) {
                            InvitedAttendeeForMcuKdc invitedAttendeeForMcuKdc = (InvitedAttendeeForMcuKdc) attendee;
                            if (invitedAttendeeForMcuKdc.getTerminalId() != null) {
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuKdc.getTerminalId());
                                if (busiTerminal != null) {
                                    if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                    } else {
                                        attendee.setName(busiTerminal.getName());
                                    }
                                }
                            }
                        } else if (attendee instanceof McuAttendeeForMcuKdc) {
                            attendee.getUpdateMap().put("ip", attendee.getIp());
                            attendee.getUpdateMap().put("remoteParty", attendee.getRemoteParty());
                        }
                        if (mt.getProtocol() == 1) {
                            attendee.setProtoType(2);
                        } else {
                            attendee.setProtoType(1);
                        }
                        attendee.setDirection("outgoing");
                        String presentAttendId = conferenceContext.getPresentAttendeeId();
                        if (mt.getDual() == 1) {
                            if (YesOrNo.NO.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.YES.getValue());
                                changed = true;
                                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在共享屏幕！");
                            }
                            conferenceContext.setPresentAttendeeId(attendee.getId());
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.NO.getValue());
                                changed = true;
                                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】关闭共享屏幕！");
                            }
                            if (attendee.getId().equals(presentAttendId)) {
                                conferenceContext.setPresentAttendeeId(null);
                            }
                        }
                        if (mt.getMix() == 1 || (mt.getMute() == 0 && (conferenceContext.isDiscuss() || attendee == conferenceContext.getMasterAttendee()))) {
                            if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                changed = true;
                                audioMuteChanged = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                                audioMuteChanged = true;
                            }
                        }
                        attendee.setVideoStatus(AttendeeVideoStatus.YES.getValue());
                        attendee.setParticipantUuid(uuid);
                        attendee.setHangUp(false);
                        if (attendee.getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
                            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                            changed = true;
                            newJoined = true;
                        }
                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        if (changed) {
                            if (newJoined) {
                                if (conferenceContext.getMuteType() == 1) {
                                    if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                        McuKdcAttendeeSetMuteTask mcuKdcAttendeeSetMuteTask = new McuKdcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
                                        BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(mcuKdcAttendeeSetMuteTask);
                                        attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                    }
                                } else {
                                    if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
                                        McuKdcAttendeeSetMuteTask mcuKdcAttendeeSetMuteTask = new McuKdcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
                                        BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(mcuKdcAttendeeSetMuteTask);
                                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                    }
                                }
                            }
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            updateOperation(conferenceContext);

                            conferenceContext.addUuidAttendee(attendee);

                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                        }
                    } else {
                        SelfCallAttendeeForMcuKdc selfCallAttendee = new SelfCallAttendeeForMcuKdc();
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
                        if (mt.getProtocol() == 1) {
                            attendee.setProtoType(2);
                        } else {
                            attendee.setProtoType(1);
                        }
                        attendee.setDirection("outgoing");
                        String presentAttendId = conferenceContext.getPresentAttendeeId();
                        if (mt.getDual() == 1) {
                            if (YesOrNo.NO.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.YES.getValue());
                                changed = true;
                                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】正在共享屏幕！");
                            }
                            conferenceContext.setPresentAttendeeId(attendee.getId());
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getPresentStatus()) {
                                attendee.setPresentStatus(YesOrNo.NO.getValue());
                                changed = true;
                                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】关闭共享屏幕！");
                            }
                            if (attendee.getId().equals(presentAttendId)) {
                                conferenceContext.setPresentAttendeeId(null);
                            }
                        }
                        if (mt.getMix() == 1 || (conferenceContext.isDiscuss() && mt.getMute() == 0)) {
                            if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                changed = true;
                                audioMuteChanged = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                                audioMuteChanged = true;
                            }
                        }
                        attendee.setVideoStatus(AttendeeVideoStatus.YES.getValue());
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
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        conferenceContext.addAttendee(attendee);
                        if (conferenceContext.getMuteType() == 1) {
                            if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                                McuKdcAttendeeSetMuteTask mcuKdcAttendeeSetMuteTask = new McuKdcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
                                BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(mcuKdcAttendeeSetMuteTask);
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                            }
                        } else {
                            if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
                                McuKdcAttendeeSetMuteTask mcuKdcAttendeeSetMuteTask = new McuKdcAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
                                BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(mcuKdcAttendeeSetMuteTask);
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                            }
                        }
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        updateOperation(conferenceContext);

                        conferenceContext.addUuidAttendee(attendee);

                        BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                        // 更新数据库
                        processUpdateParticipant(conferenceContext, attendee, true);
                        if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                            BeanFactory.getBean(IAttendeeForMcuKdcService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                        }
                    }
                }

                if (audioMuteChanged) {
                    McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
                }
            }
        }).start();
    }

    private void processEpsLeftResponse(List<CcGetTerminalListResponse.Mt> mtsLeft, McuKdcConferenceContext conferenceContext) {
        for (CcGetTerminalListResponse.Mt mt : mtsLeft) {
            String uuid = mt.getMt_id();
            String name = mt.getAlias();
            String remoteParty = StringUtils.isNotEmpty(mt.getAccount()) ? mt.getAccount() : mt.getIp();

            AttendeeForMcuKdc attendeeForMcuKdc = conferenceContext.getAttendeeByUuid(uuid);
            if (attendeeForMcuKdc != null && uuid.equals(attendeeForMcuKdc.getParticipantUuid())) {
                if (attendeeForMcuKdc.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                    McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
                }

                IAttendeeForMcuKdcService attendeeForMcuKdcService = BeanFactory.getBean(IAttendeeForMcuKdcService.class);
                if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                    if (attendeeForMcuKdc.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                        attendeeForMcuKdcService.cancelCallTheRoll(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                    if (attendeeForMcuKdc.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                        attendeeForMcuKdcService.cancelTalk(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                    if (attendeeForMcuKdc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                        attendeeForMcuKdcService.cancelChooseSee(conferenceContext.getId());
                    }
                }
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    defaultAttendeeOperation.operate();
                }

                attendeeForMcuKdc.resetUpdateMap();
                attendeeForMcuKdc.leaveMeeting();
                if (attendeeForMcuKdc instanceof McuAttendeeForMcuKdc) {
                    McuAttendeeForMcuKdc mcuAttendeeForMcuKdc = (McuAttendeeForMcuKdc) attendeeForMcuKdc;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuKdc.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuKdc.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuKdc.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuKdc);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuKdc.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuKdc.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuKdc.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuKdc.isMcuAttendee());
                            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        }
                    }
                } else if (attendeeForMcuKdc instanceof TerminalAttendeeForMcuKdc) {
                    TerminalAttendeeForMcuKdc terminalAttendeeForMcuKdc = (TerminalAttendeeForMcuKdc) attendeeForMcuKdc;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuKdc.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuKdc.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                } else if (attendeeForMcuKdc instanceof InvitedAttendeeForMcuKdc) {
                    InvitedAttendeeForMcuKdc invitedAttendeeForMcuKdc = (InvitedAttendeeForMcuKdc) attendeeForMcuKdc;
                    if (invitedAttendeeForMcuKdc.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuKdc.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuKdc.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuKdc.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuKdc.getId());
                        updateMap.put("deptId", attendeeForMcuKdc.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuKdc.isMcuAttendee());
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuKdc.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuKdc.getId());
                    updateMap.put("deptId", attendeeForMcuKdc.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuKdc.isMcuAttendee());
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
                String reason = "【" + attendeeForMcuKdc.getName() + "】离会";
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuKdc);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuKdc);

                attendeeForMcuKdc.setParticipantUuid(null);
            }
        }
    }

    private void processEpsDeleteResponse(AttendeeForMcuKdc attendeeForMcuKdc, McuKdcConferenceContext conferenceContext) {
        if (attendeeForMcuKdc != null) {
            if (attendeeForMcuKdc.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                McuKdcMuteStatusCheckTask muteStatusCheckTask = new McuKdcMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                BeanFactory.getBean(McuKdcDelayTaskService.class).addTask(muteStatusCheckTask);
            }

            IAttendeeForMcuKdcService attendeeForMcuKdcService = BeanFactory.getBean(IAttendeeForMcuKdcService.class);
            if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                if (attendeeForMcuKdc.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                    attendeeForMcuKdcService.cancelCallTheRoll(conferenceContext.getId());
                }
            } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                if (attendeeForMcuKdc.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                    attendeeForMcuKdcService.cancelTalk(conferenceContext.getId());
                }
            } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                if (attendeeForMcuKdc.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                    attendeeForMcuKdcService.cancelChooseSee(conferenceContext.getId());
                }
            }
            if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                defaultAttendeeOperation.operate();
            }

            attendeeForMcuKdc.resetUpdateMap();
            attendeeForMcuKdc.leaveMeeting();
            if (attendeeForMcuKdc instanceof McuAttendeeForMcuKdc) {
                McuAttendeeForMcuKdc mcuAttendeeForMcuKdc = (McuAttendeeForMcuKdc) attendeeForMcuKdc;
                String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuKdc.getId());
                BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                if (baseConferenceContext != null) {
                    attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                } else {
                    attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                }
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                if (baseConferenceContext == null) {
                    ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                    ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                    viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuKdc.getCascadeTemplateId());
                    viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuKdc.getCascadeMcuType());
                    viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                    viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                    List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                    if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                        conferenceContext.removeMcuAttendee(mcuAttendeeForMcuKdc);
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuKdc.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }
                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", mcuAttendeeForMcuKdc.getId());
                        updateMap.put("deptId", mcuAttendeeForMcuKdc.getDeptId());
                        updateMap.put("mcuAttendee", mcuAttendeeForMcuKdc.isMcuAttendee());
                        McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    }
                }
            } else if (attendeeForMcuKdc instanceof TerminalAttendeeForMcuKdc) {
                TerminalAttendeeForMcuKdc terminalAttendeeForMcuKdc = (TerminalAttendeeForMcuKdc) attendeeForMcuKdc;
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuKdc.getTerminalId());
                if (busiTerminal != null) {
                    attendeeForMcuKdc.setOnlineStatus(busiTerminal.getOnlineStatus());
                } else {
                    attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                }
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
            } else if (attendeeForMcuKdc instanceof InvitedAttendeeForMcuKdc) {
                InvitedAttendeeForMcuKdc invitedAttendeeForMcuKdc = (InvitedAttendeeForMcuKdc) attendeeForMcuKdc;
                if (invitedAttendeeForMcuKdc.getTerminalId() != null) {
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuKdc.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuKdc.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuKdc.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuKdc.getUpdateMap());
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuKdc.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuKdc.getId());
                    updateMap.put("deptId", attendeeForMcuKdc.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuKdc.isMcuAttendee());
                    McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
            } else {
                conferenceContext.removeAttendeeById(attendeeForMcuKdc.getId());
                updateOperation(conferenceContext);
                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuKdc.getId())) {
                    clearMasterAttendee(conferenceContext);
                }

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("id", attendeeForMcuKdc.getId());
                updateMap.put("deptId", attendeeForMcuKdc.getDeptId());
                updateMap.put("mcuAttendee", attendeeForMcuKdc.isMcuAttendee());
                McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                McuKdcWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
            }
            String reason = "【" + attendeeForMcuKdc.getName() + "】离会";
            McuKdcWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

            BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuKdc);
            // 更新数据库
            processUpdateParticipant(conferenceContext, attendeeForMcuKdc);
            conferenceContext.removeUuidAttendee(attendeeForMcuKdc);
        }
    }

    private void processUpdateParticipant(McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee) {
        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private void processUpdateParticipant(McuKdcConferenceContext conferenceContext, AttendeeForMcuKdc attendee, boolean updateMediaInfo) {
        IBusiHistoryConferenceForMcuKdcService busiHistoryConferenceForMcuKdcService = BeanFactory.getBean(IBusiHistoryConferenceForMcuKdcService.class);
        busiHistoryConferenceForMcuKdcService.updateBusiHistoryParticipant(conferenceContext, attendee, updateMediaInfo);
    }

    private void clearMasterAttendee(McuKdcConferenceContext conferenceContext) {
        AttendeeForMcuKdc oldMasterAttendee = conferenceContext.getMasterAttendee();
        Map<String, Object> data = new HashMap<>();
        data.put("oldMasterAttendee", oldMasterAttendee);
        data.put("newMasterAttendee", null);
        McuKdcWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("主会场已离会【").append(oldMasterAttendee.getName()).append("】");
        McuKdcWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        conferenceContext.clearMasterAttendee();
    }

    private void updateOperation(McuKdcConferenceContext conferenceContext) {
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        if (attendeeOperation instanceof DefaultAttendeeOperation) {
            attendeeOperation.setForceUpdateView(true);
        }
    }

}
