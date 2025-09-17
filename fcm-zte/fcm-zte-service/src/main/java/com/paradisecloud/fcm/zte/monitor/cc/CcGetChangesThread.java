package com.paradisecloud.fcm.zte.monitor.cc;

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
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.fcm.zte.attendee.model.operation.ChooseSeeAttendeeOperation;
import com.paradisecloud.fcm.zte.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.zte.attendee.model.operation.RollCallAttendeeOperation;
import com.paradisecloud.fcm.zte.attendee.model.operation.TalkAttendeeOperation;
import com.paradisecloud.fcm.zte.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.McuZteWebSocketMessagePusher;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.busi.attendee.*;
import com.paradisecloud.fcm.zte.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.zte.model.request.cc.CcMrInfoRequest;
import com.paradisecloud.fcm.zte.model.request.cc.CcgetParticipantStatusV4Request;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.fcm.zte.model.response.cc.CcMrInfoResponse;
import com.paradisecloud.fcm.zte.model.response.cc.CcgetParticipantStatusV4Response;
import com.paradisecloud.fcm.zte.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.zte.service.interfaces.IAttendeeForMcuZteService;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiHistoryConferenceForMcuZteService;
import com.paradisecloud.fcm.zte.service.interfaces.ISimpleConferenceControlForMcuZteService;
import com.paradisecloud.fcm.zte.task.*;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.bean.PartStaV4;
import com.zte.m900.request.GetConferenceInfoRequest;
import com.zte.m900.request.GetConferenceStatusRequest;
import com.zte.m900.request.GetParticipantStatusV4Request;
import com.zte.m900.response.GetConferenceInfoResponse;
import com.zte.m900.response.GetConferenceStatusResponse;
import com.zte.m900.response.GetParticipantStatusV4Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
                for (McuZteConferenceContext conferenceContext : McuZteConferenceContextCache.getInstance().values()) {
                    if (conferenceContext.isEnd()) {
                        McuZteConferenceContextCache.getInstance().remove(conferenceContext.getContextKey());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_ENDED, "会议已结束");
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
                        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(contextKey);
                        if (mcuZteConferenceContext != null) {
                            McuZteConferenceContextCache.getInstance().get(contextKey).cleanLoginInfo();
                        }
                    }
                    if (!McuZteConferenceContextCache.getInstance().containsKey(contextKey)) {
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

    public void startConferenceThread(McuZteConferenceContext conferenceContext) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (conferenceContext.isEnd()) {
                        return;
                    }
                    long currentTimeMillis = System.currentTimeMillis();
                    if (conferenceContext.getLastUpdateTime() == 0 || StringUtils.isEmpty(conferenceContext.getMcuZteBridge().getMcuToken()) || conferenceContext.getLastUpdateTime() - currentTimeMillis > 30000) {
                        // 恢复横幅

                    }
                    if (StringUtils.isNotEmpty(conferenceContext.getMcuZteBridge().getMcuToken())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (conferenceContext.isEnd()) {
                                return;
                            }
                            // 会议信息
                            GetConferenceStatusRequest ccMrInfoRequest = new GetConferenceStatusRequest();
                            ccMrInfoRequest.setConferenceIdentifier(conferenceContext.getConfId());
                            GetConferenceStatusResponse ccMrInfoResponse = conferenceContext.getConferenceControlApi().getConferenceStatus(ccMrInfoRequest);
                            if (ccMrInfoResponse != null && CommonResponse.STATUS_OK.equals(ccMrInfoResponse.getResult())) {
                                try {
                                    processMrInfoResponse(ccMrInfoResponse, conferenceContext);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                conferenceContext.getMcuZteBridge().setMcuToken("");
                                conferenceContext.getMcuZteBridge().setMcuUserToken("");
                                conferenceContext.getMcuZteBridge().setLastUpdateTime(0);
                            }

                            GetParticipantStatusV4Request ccgetParticipantStatusV4Request = new GetParticipantStatusV4Request();
                            ccgetParticipantStatusV4Request.setConfIdentifier(conferenceContext.getConfId());
                            GetParticipantStatusV4Response ccgetParticipantStatusV4Response = conferenceContext.getConferenceControlApi().getParticipantStatusV4(ccgetParticipantStatusV4Request);
                            if (ccgetParticipantStatusV4Response != null && CommonResponse.STATUS_OK.equals(ccgetParticipantStatusV4Response.getResult())) {

                                Set<String> participantNameSet = new HashSet<>();
                                Map<String, String> disconnectedParticipantMap = new HashMap<>();

                                // 删除终端
                                processEpsDeleteResponse(ccgetParticipantStatusV4Response, conferenceContext);

                                // 离会终端
                                processEpsLeftResponse(ccgetParticipantStatusV4Response, conferenceContext, participantNameSet, disconnectedParticipantMap);

                                // 在会终端
                                processEspInfoResponse(ccgetParticipantStatusV4Response, conferenceContext, participantNameSet);

                                conferenceContext.setParticipantNameSet(participantNameSet);
                                conferenceContext.setDisconnectedParticipantMap(disconnectedParticipantMap);

                                try {
                                    // 删除终端
                                    processEpsDeleteOnMcu(ccgetParticipantStatusV4Response, conferenceContext);
                                } catch (Exception e) {

                                }
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

    private void startPollingThread(McuZteConferenceContext conferenceContext) {
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
                                ISimpleConferenceControlForMcuZteService simpleConferenceControlForMcuZteService = BeanFactory.getBean(ISimpleConferenceControlForMcuZteService.class);
                                String bannerText = redisCache.getCacheObject(conferenceContext.getId() + "_banner_text");
                                simpleConferenceControlForMcuZteService.setBanner(conferenceContext, bannerText);
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

    private void processMrInfoResponse(GetConferenceStatusResponse ccMrInfoResponse, McuZteConferenceContext conferenceContext) {
        if (ccMrInfoResponse != null && CommonResponse.STATUS_OK.equals(ccMrInfoResponse.getResult())) {
            if (conferenceContext != null) {
                McuZteDelayTaskService delayTaskService = BeanFactory.getBean(McuZteDelayTaskService.class);
                if (ccMrInfoResponse.getConferenceInfo().isLockState()) {
                    if(!conferenceContext.isLocked()){
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, true);
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (true ? "" : "解除") + "锁定");
                        conferenceContext.setLocked(true);
                    }
                } else {
                    if (conferenceContext.isLocked()) {
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, false);
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (false ? "" : "解除") + "锁定");
                        conferenceContext.setLocked(false);
                        UnlockAttendeesTask unlockAttendeesTask = new UnlockAttendeesTask(conferenceContext.getId(), 10, conferenceContext);
                        delayTaskService.addTask(unlockAttendeesTask);
                    }
                }
                if (ccMrInfoResponse.getConferenceInfo().isIfRecord()) {
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, true);
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启录制");
                    conferenceContext.setRecorded(true);
                } else {
                    if (conferenceContext.isRecorded()) {
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, false);
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭录制");
                        conferenceContext.setRecorded(false);
                    }
                }
                //辅视频源会场 ID
                String dualBoardcaster = ccMrInfoResponse.getConferenceInfo().getDualBoardcaster();


                if (Strings.isNotBlank(dualBoardcaster) && !Objects.equals(dualBoardcaster, "-1")) {
                    String presentAttendeeId = conferenceContext.getPresentAttendeeId();
                    if (Strings.isNotBlank(presentAttendeeId)) {
                        AttendeeForMcuZte attendeeById = conferenceContext.getAttendeeById(presentAttendeeId);
                        if(!Objects.equals(attendeeById.getParticipantUuid(),dualBoardcaster)){
                            attendeeById.setPresentStatus(YesOrNo.NO.getValue());
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, attendeeById.getName() + "已关闭辅流");


                            AttendeeForMcuZte attendeeByUuid = conferenceContext.getAttendeeByUuid(dualBoardcaster);
                            if(attendeeByUuid!=null){
                                conferenceContext.setPresentAttendeeId(attendeeByUuid.getId());
                                attendeeByUuid.setPresentStatus(YesOrNo.YES.getValue());
                                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeByUuid.getUpdateMap());
                                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, attendeeByUuid.getName() + "已开启辅流");
                            }

                        }

                    }else {
                        AttendeeForMcuZte attendeeByUuid = conferenceContext.getAttendeeByUuid(dualBoardcaster);
                        conferenceContext.setPresentAttendeeId(attendeeByUuid.getId());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("presentAttendeeId", attendeeByUuid.getId());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启辅流");


                        attendeeByUuid.setPresentStatus(YesOrNo.YES.getValue());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeByUuid.getUpdateMap());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, attendeeByUuid.getName() + "已开启辅流");
                    }




                } else {
                    String presentAttendeeId = conferenceContext.getPresentAttendeeId();
                    if (Strings.isNotBlank(presentAttendeeId)) {
                        AttendeeForMcuZte attendeeById = conferenceContext.getAttendeeById(presentAttendeeId);
                        attendeeById.setPresentStatus(YesOrNo.NO.getValue());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeById.getUpdateMap());
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, attendeeById.getName() + "已关闭辅流");
                    }
                    if (conferenceContext.getPresentAttendeeId() != null) {
                        conferenceContext.setPresentAttendeeId(null);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("presentAttendeeId", "");
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭辅流");
                    }

                }

                String confCtrlMode = ccMrInfoResponse.getConferenceInfo().getConfCtrlMode();
                String confCtrlMode_old = conferenceContext.getConfCtrlMode();
                if (!Objects.equals(confCtrlMode, confCtrlMode_old)) {
                    conferenceContext.setConfCtrlMode(confCtrlMode);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("confCtrlMode", confCtrlMode);
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }


                if (conferenceContext.getMasterAttendee() == null) {
                    RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                    try {
                        String masterAttendeeId = redisCache.getCacheObject(conferenceContext.getId() + "_" + "_master_attendee");
                        if (StringUtils.isNotEmpty(masterAttendeeId)) {
                            AttendeeForMcuZte masterAttendee = conferenceContext.getAttendeeById(masterAttendeeId);
                            if (masterAttendee != null) {
                                conferenceContext.setMasterAttendee(masterAttendee);

                                Map<String, Object> data = new HashMap<>();
                                data.put("oldMasterAttendee", null);
                                data.put("newMasterAttendee", masterAttendee);
                                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                            }
                        }
                    } catch (Exception e) {
                    }
                }


            }
        }
    }

    private void processEspInfoResponse(GetParticipantStatusV4Response ccgetParticipantStatusV4Response, McuZteConferenceContext conferenceContext, Set<String> participantNameSet) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean audioMuteChanged = false;
                PartStaV4[] partStaV4List = ccgetParticipantStatusV4Response.getPartStaV4();
                List<PartStaV4> partStaV4s = Arrays.asList(partStaV4List);
                Set<PartStaV4> onLineTerminalSet = partStaV4s.stream().filter(partStaV4 -> partStaV4.getState() == 5).collect(Collectors.toSet());
                for (PartStaV4 partStaV4 : onLineTerminalSet) {
                    AttendeeForMcuZte attendee = null;
                    String uuid = partStaV4.getId();
                    AttendeeForMcuZte deletedParticipant = conferenceContext.getDeletedParticipant(uuid);
                    if (deletedParticipant != null) {
                        McuZteDelayTaskService delayTaskService = BeanFactory.getBean(McuZteDelayTaskService.class);
                        DeleteAttendeesTask deleteAttendeesTask = new DeleteAttendeesTask(conferenceContext.getId(), 3000, conferenceContext, uuid);
                        delayTaskService.addTask(deleteAttendeesTask);
                        return;
                    }
                    String name = partStaV4.getName();
                    if (Strings.isBlank(name)) {
                        name = partStaV4.getId();
                    }
                    String remoteParty = partStaV4.getNo();
                    participantNameSet.add(name);
                    boolean changed = false;
                    boolean newJoined = false;

                    Map<String, AttendeeForMcuZte> attendeeForMcuZteMap = conferenceContext.getAttendeeMapByUri(remoteParty);
                    if (attendeeForMcuZteMap != null && attendeeForMcuZteMap.size() > 0) {
                        for (String key : attendeeForMcuZteMap.keySet()) {
                            attendee = attendeeForMcuZteMap.get(key);
                        }
                    }
                    if (attendee == null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(remoteParty);
                        if (busiTerminal != null) {
                            SelfCallAttendeeForMcuZte selfCallAttendee = new SelfCallAttendeeForMcuZte();
                            attendee = selfCallAttendee;
                            attendee.setTerminalId(busiTerminal.getId());
                            attendee.setSn(busiTerminal.getSn());
                            if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                attendee.setName(busiTerminal.getName() + "(" + name + ")");
                            } else {
                                attendee.setName(busiTerminal.getName());
                            }
                            attendee.setId(uuid);

                            String presentAttendId = conferenceContext.getPresentAttendeeId();

                            if (partStaV4.getMute() == 0) {
                                if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                    changed = true;
                                    audioMuteChanged = true;
                                }
                            } else {
                                if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                    attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                    McuZteMuteStatusCheckTask muteStatusCheckTask = new McuZteMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                    BeanFactory.getBean(McuZteDelayTaskService.class).addTask(muteStatusCheckTask);
                                    changed = true;
                                    audioMuteChanged = true;
                                }
                            }
                            if (partStaV4.getCamera() == 1) {//打开
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

                            if (partStaV4.getSilent() == 1) {//静音
                                if (YesOrNo.NO.getValue() == attendee.getSpeakerStatus()) {
                                    attendee.setSpeakerStatus(YesOrNo.YES.getValue());
                                    changed = true;
                                }
                            } else {
                                if (YesOrNo.YES.getValue() == attendee.getSpeakerStatus()) {
                                    attendee.setVideoStatus(YesOrNo.NO.getValue());
                                    changed = true;
                                }
                            }

                            if (partStaV4.getHandup() == 1) {//举手
                                if (YesOrNo.NO.getValue() == attendee.getRaiseHandStatus()) {
                                    attendee.setHangUp(true);
                                    changed = true;
                                }
                            } else {
                                if (YesOrNo.YES.getValue() == attendee.getRaiseHandStatus()) {
                                    attendee.setHangUp(false);
                                    changed = true;
                                }
                            }

                            if (partStaV4.getSilent() == 0) {//扬声器
                                if (YesOrNo.NO.getValue() == attendee.getSpeakerStatus()) {
                                    attendee.setSpeakerStatus(YesOrNo.YES.getValue());
                                    changed = true;
                                }
                            } else {
                                if (YesOrNo.YES.getValue() == attendee.getSpeakerStatus()) {
                                    attendee.setSpeakerStatus(YesOrNo.NO.getValue());
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
                            attendee.setJoinedTime(new Date());
                            if (conferenceContext.isLocked()) {
                                attendee.setLocked(true);
                            }
                            conferenceContext.addAttendee(attendee);
//                            if (conferenceContext.getMuteType() == 1) {
//                                if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
//                                    McuZteAttendeeSetMuteTask mcuZteAttendeeSetMuteTask = new McuZteAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
//                                    BeanFactory.getBean(McuZteDelayTaskService.class).addTask(mcuZteAttendeeSetMuteTask);
//                                    attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
//                                }
//                            } else {
//                                if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
//                                    McuZteAttendeeSetMuteTask mcuZteAttendeeSetMuteTask = new McuZteAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
//                                    BeanFactory.getBean(McuZteDelayTaskService.class).addTask(mcuZteAttendeeSetMuteTask);
//                                    attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
//
//                                }
//                            }
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                            updateOperation(conferenceContext);

                            conferenceContext.addUuidAttendee(attendee);

                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                            if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                                BeanFactory.getBean(IAttendeeForMcuZteService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                            }

                            continue;
                        }
                    }
                    if (attendee != null) {
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuZte) {
                            TerminalAttendeeForMcuZte terminalAttendeeForMcuZte = (TerminalAttendeeForMcuZte) attendee;
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                            if (busiTerminal != null) {
                                if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                } else {
                                    attendee.setName(busiTerminal.getName());
                                }
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuZte) {
                            InvitedAttendeeForMcuZte invitedAttendeeForMcuZte = (InvitedAttendeeForMcuZte) attendee;
                            if (invitedAttendeeForMcuZte.getTerminalId() != null) {
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZte.getTerminalId());
                                if (busiTerminal != null) {
                                    if (StringUtils.isNotEmpty(name) && !busiTerminal.getName().equals(name)) {
                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                    } else {
                                        attendee.setName(busiTerminal.getName());
                                    }
                                }
                            }
                        } else if (attendee instanceof McuAttendeeForMcuZte) {
                            attendee.getUpdateMap().put("ip", attendee.getIp());
                            attendee.getUpdateMap().put("remoteParty", attendee.getRemoteParty());
                        }


                        if (partStaV4.getMute() == 0) {
                            if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                changed = true;
                                audioMuteChanged = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuZteMuteStatusCheckTask muteStatusCheckTask = new McuZteMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(McuZteDelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                                audioMuteChanged = true;
                            }
                        }
                        if (partStaV4.getCamera() == 1) {
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


                        if (partStaV4.getHandup() == 1) {//举手
                            if (YesOrNo.NO.getValue() == attendee.getRaiseHandStatus()) {
                                attendee.setHangUp(true);
                                changed = true;
                            }
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getRaiseHandStatus()) {
                                attendee.setHangUp(false);
                                changed = true;
                            }
                        }
                        if (partStaV4.getSilent() == 0) {//扬声器
                            if (YesOrNo.NO.getValue() == attendee.getSpeakerStatus()) {
                                attendee.setSpeakerStatus(YesOrNo.YES.getValue());
                                changed = true;
                            }
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getSpeakerStatus()) {
                                attendee.setSpeakerStatus(YesOrNo.NO.getValue());
                                changed = true;
                            }
                        }

                        attendee.setParticipantUuid(uuid);
                        attendee.setHangUp(false);
                        if (attendee.getMeetingStatus() != AttendeeMeetingStatus.IN.getValue()) {
                            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                            changed = true;
                            newJoined = true;
                        }
                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        attendee.setJoinedTime(new Date());
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        if (changed) {
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                            if (newJoined) {
//                                if (conferenceContext.getMuteType() == 1) {
//                                    if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
//                                        McuZteAttendeeSetMuteTask mcuZteAttendeeSetMuteTask = new McuZteAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
//                                        BeanFactory.getBean(McuZteDelayTaskService.class).addTask(mcuZteAttendeeSetMuteTask);
//                                        attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
//                                    }
//                                } else {
//                                    if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
//                                        McuZteAttendeeSetMuteTask mcuZteAttendeeSetMuteTask = new McuZteAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
//                                        BeanFactory.getBean(McuZteDelayTaskService.class).addTask(mcuZteAttendeeSetMuteTask);
//                                        attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
//                                    }
//                                }

                                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                                McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                                updateOperation(conferenceContext);
                                conferenceContext.addUuidAttendee(attendee);
                            }





                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                        }
                    } else {
                        SelfCallAttendeeForMcuZte selfCallAttendee = new SelfCallAttendeeForMcuZte();
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


                        if (partStaV4.getMute() == 0) {
                            if (AttendeeMixingStatus.NO.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                changed = true;
                                audioMuteChanged = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendee.getMixingStatus()) {
                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuZteMuteStatusCheckTask muteStatusCheckTask = new McuZteMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(McuZteDelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                                audioMuteChanged = true;
                            }
                        }
                        if (partStaV4.getCamera() == 1) {
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



                        if (partStaV4.getHandup() == 1) {//举手
                            if (YesOrNo.NO.getValue() == attendee.getRaiseHandStatus()) {
                                attendee.setHangUp(true);
                                changed = true;
                            }
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getRaiseHandStatus()) {
                                attendee.setHangUp(false);
                                changed = true;
                            }
                        }
                        if (partStaV4.getSilent() == 0) {//扬声器
                            if (YesOrNo.NO.getValue() == attendee.getSpeakerStatus()) {
                                attendee.setSpeakerStatus(YesOrNo.YES.getValue());
                                changed = true;
                            }
                        } else {
                            if (YesOrNo.YES.getValue() == attendee.getSpeakerStatus()) {
                                attendee.setSpeakerStatus(YesOrNo.NO.getValue());
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
                        attendee.setJoinedTime(new Date());
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        conferenceContext.addAttendee(attendee);
//                        if (conferenceContext.getMuteType() == 1) {
//                            if (attendee.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
//                                McuZteAttendeeSetMuteTask mcuZteAttendeeSetMuteTask = new McuZteAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, true);
//                                BeanFactory.getBean(McuZteDelayTaskService.class).addTask(mcuZteAttendeeSetMuteTask);
//                                attendee.setMixingStatus(AttendeeMixingStatus.NO.getValue());
//                            }
//                        } else {
//                            if (attendee.getMixingStatus() == AttendeeMixingStatus.NO.getValue()) {
//                                McuZteAttendeeSetMuteTask mcuZteAttendeeSetMuteTask = new McuZteAttendeeSetMuteTask(attendee.getId(), 0, conferenceContext, attendee, false);
//                                BeanFactory.getBean(McuZteDelayTaskService.class).addTask(mcuZteAttendeeSetMuteTask);
//                                attendee.setMixingStatus(AttendeeMixingStatus.YES.getValue());
//                            }
//                        }
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        updateOperation(conferenceContext);

                        conferenceContext.addUuidAttendee(attendee);

                        BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                        // 更新数据库
                        processUpdateParticipant(conferenceContext, attendee, true);
                        if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                            BeanFactory.getBean(IAttendeeForMcuZteService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                        }
                    }
                }

                if (audioMuteChanged) {
                    McuZteMuteStatusCheckTask muteStatusCheckTask = new McuZteMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuZteDelayTaskService.class).addTask(muteStatusCheckTask);
                }
            }
        }).start();
    }

    private void processEpsLeftResponse(GetParticipantStatusV4Response ccgetParticipantStatusV4Response, McuZteConferenceContext conferenceContext, Set<String> participantNameSet, Map<String, String> disconnectedParticipantMap) {
        PartStaV4[] partStaV4List = ccgetParticipantStatusV4Response.getPartStaV4();
        List<PartStaV4> partStaV4s = Arrays.asList(partStaV4List);
        Set<PartStaV4> offLineTerminalSet = partStaV4s.stream().filter(partStaV4 -> partStaV4.getState() == 1).collect(Collectors.toSet());
        for (PartStaV4 partStaV4 : offLineTerminalSet) {
            String uuid = partStaV4.getId();
            String name = partStaV4.getName();
            String remoteParty = partStaV4.getNo();
            participantNameSet.add(name);
            disconnectedParticipantMap.put(remoteParty, uuid);

            AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeByUuid(uuid);
            if (attendeeForMcuZte != null && uuid.equals(attendeeForMcuZte.getParticipantUuid())) {
                if (attendeeForMcuZte.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                    McuZteMuteStatusCheckTask muteStatusCheckTask = new McuZteMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(McuZteDelayTaskService.class).addTask(muteStatusCheckTask);
                }

                IAttendeeForMcuZteService attendeeForMcuZteService = BeanFactory.getBean(IAttendeeForMcuZteService.class);
                if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                    if (attendeeForMcuZte.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelCallTheRoll(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                    if (attendeeForMcuZte.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelTalk(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                    if (attendeeForMcuZte.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelChooseSee(conferenceContext.getId());
                    }
                }
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    defaultAttendeeOperation.operate();
                }

                attendeeForMcuZte.resetUpdateMap();
                attendeeForMcuZte.leaveMeeting();
                if (attendeeForMcuZte instanceof McuAttendeeForMcuZte) {
                    McuAttendeeForMcuZte mcuAttendeeForMcuZte = (McuAttendeeForMcuZte) attendeeForMcuZte;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZte.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuZte.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuZte.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuZte);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuZte.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuZte.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuZte.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuZte.isMcuAttendee());
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        }
                    }
                } else if (attendeeForMcuZte instanceof TerminalAttendeeForMcuZte) {
                    TerminalAttendeeForMcuZte terminalAttendeeForMcuZte = (TerminalAttendeeForMcuZte) attendeeForMcuZte;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuZte.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                } else if (attendeeForMcuZte instanceof InvitedAttendeeForMcuZte) {
                    InvitedAttendeeForMcuZte invitedAttendeeForMcuZte = (InvitedAttendeeForMcuZte) attendeeForMcuZte;
                    if (invitedAttendeeForMcuZte.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZte.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuZte.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuZte.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZte.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuZte.getId());
                        updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuZte.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZte.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuZte.getId());
                    updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }
                String reason = "【" + attendeeForMcuZte.getName() + "】离会";
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZte);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuZte);


            }
        }
    }

    private void processEpsDeleteResponse(GetParticipantStatusV4Response ccgetParticipantStatusV4Response, McuZteConferenceContext conferenceContext) {
        PartStaV4[] partStaV4List = ccgetParticipantStatusV4Response.getPartStaV4();
        List<PartStaV4> partStaV4s = Arrays.asList(partStaV4List);
        Set<PartStaV4> deletedTerminalSet = partStaV4s.stream().filter(partStaV4 -> partStaV4.getState() == 255).collect(Collectors.toSet());
        for (PartStaV4 partStaV4 : deletedTerminalSet) {
            String uuid = partStaV4.getId();
            AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeByUuid(uuid);
            if (attendeeForMcuZte != null && uuid.equals(attendeeForMcuZte.getParticipantUuid())) {

                IAttendeeForMcuZteService attendeeForMcuZteService = BeanFactory.getBean(IAttendeeForMcuZteService.class);
                if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                    if (attendeeForMcuZte.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelCallTheRoll(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                    if (attendeeForMcuZte.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelTalk(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                    if (attendeeForMcuZte.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelChooseSee(conferenceContext.getId());
                    }
                }
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    defaultAttendeeOperation.operate();
                }

                attendeeForMcuZte.resetUpdateMap();
                attendeeForMcuZte.leaveMeeting();
                if (attendeeForMcuZte instanceof McuAttendeeForMcuZte) {
                    McuAttendeeForMcuZte mcuAttendeeForMcuZte = (McuAttendeeForMcuZte) attendeeForMcuZte;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZte.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuZte.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuZte.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuZte);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuZte.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuZte.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuZte.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuZte.isMcuAttendee());
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        }
                    }
                } else if (attendeeForMcuZte instanceof TerminalAttendeeForMcuZte) {
                    TerminalAttendeeForMcuZte terminalAttendeeForMcuZte = (TerminalAttendeeForMcuZte) attendeeForMcuZte;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuZte.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                } else if (attendeeForMcuZte instanceof InvitedAttendeeForMcuZte) {
                    InvitedAttendeeForMcuZte invitedAttendeeForMcuZte = (InvitedAttendeeForMcuZte) attendeeForMcuZte;
                    if (invitedAttendeeForMcuZte.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZte.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuZte.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuZte.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZte.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuZte.getId());
                        updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuZte.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZte.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuZte.getId());
                    updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }
                String reason = "【" + attendeeForMcuZte.getName() + "】离会";
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZte);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuZte);
                conferenceContext.removeUuidAttendee(attendeeForMcuZte);
            }
        }

        //
    }

    private void processEpsDeleteOnMcu(GetParticipantStatusV4Response ccgetParticipantStatusV4Response, McuZteConferenceContext conferenceContext) {
        PartStaV4[] partStaV4List = ccgetParticipantStatusV4Response.getPartStaV4();
        List<PartStaV4> partStaV4s = Arrays.asList(partStaV4List);
        List<String> ids = new ArrayList<>();
        Set<PartStaV4> deletedTerminalSet = partStaV4s.stream().filter(partStaV4 -> partStaV4.getState() == 255).collect(Collectors.toSet());
        List<String> ids_delete= new ArrayList<>();
        for (PartStaV4 partStaV4 : deletedTerminalSet){
            ids_delete.add(partStaV4.getId());
        }
        for (PartStaV4 partStaV4 : partStaV4s) {
            String id = partStaV4.getId();
            ids.add(id);
        }
        List<AttendeeForMcuZte> attendees = conferenceContext.getAttendees();
        for (AttendeeForMcuZte attendee : attendees) {

            String participantUuid = attendee.getParticipantUuid();
            if(participantUuid!=null){
                if(!ids.contains(participantUuid)){
                    ids_delete.add(participantUuid);
                }
            }

        }

        AttendeeForMcuZte masterAttendee = conferenceContext.getMasterAttendee();
        if(masterAttendee!=null){
            String participantUuid = masterAttendee.getParticipantUuid();
            if(participantUuid!=null){
                if(!ids.contains(participantUuid)){
                    ids_delete.add(participantUuid);
                }
            }
        }
        List<AttendeeForMcuZte> masterAttendees = conferenceContext.getMasterAttendees();
        for (AttendeeForMcuZte attendee : masterAttendees) {
            String participantUuid = attendee.getParticipantUuid();
            if(participantUuid!=null){
                if(!ids.contains(participantUuid)){
                    ids_delete.add(participantUuid);
                }
            }
        }


        for (String idD : ids_delete) {
            String uuid = idD;
            AttendeeForMcuZte attendeeForMcuZte = conferenceContext.getAttendeeByUuid(uuid);
            if (attendeeForMcuZte != null && uuid.equals(attendeeForMcuZte.getParticipantUuid())) {

                IAttendeeForMcuZteService attendeeForMcuZteService = BeanFactory.getBean(IAttendeeForMcuZteService.class);
                if (conferenceContext.getAttendeeOperation() instanceof RollCallAttendeeOperation) {
                    if (attendeeForMcuZte.getCallTheRollStatus() == AttendeeCallTheRollStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelCallTheRoll(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof TalkAttendeeOperation) {
                    if (attendeeForMcuZte.getTalkStatus() == AttendeeTalkStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelTalk(conferenceContext.getId());
                    }
                } else if (conferenceContext.getAttendeeOperation() instanceof ChooseSeeAttendeeOperation) {
                    if (attendeeForMcuZte.getChooseSeeStatus() == AttendeeChooseSeeStatus.YES.getValue()) {
                        attendeeForMcuZteService.cancelChooseSee(conferenceContext.getId());
                    }
                }
                if (conferenceContext.getAttendeeOperation() instanceof DefaultAttendeeOperation) {
                    DefaultAttendeeOperation defaultAttendeeOperation = (DefaultAttendeeOperation) conferenceContext.getAttendeeOperation();
                    defaultAttendeeOperation.operate();
                }

                attendeeForMcuZte.resetUpdateMap();
                attendeeForMcuZte.leaveMeeting();
                if (attendeeForMcuZte instanceof McuAttendeeForMcuZte) {
                    McuAttendeeForMcuZte mcuAttendeeForMcuZte = (McuAttendeeForMcuZte) attendeeForMcuZte;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZte.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuZte.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuZte.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuZte);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuZte.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuZte.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuZte.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuZte.isMcuAttendee());
                            McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                            McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                        }
                    }
                } else if (attendeeForMcuZte instanceof TerminalAttendeeForMcuZte) {
                    TerminalAttendeeForMcuZte terminalAttendeeForMcuZte = (TerminalAttendeeForMcuZte) attendeeForMcuZte;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZte.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuZte.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                } else if (attendeeForMcuZte instanceof InvitedAttendeeForMcuZte) {
                    InvitedAttendeeForMcuZte invitedAttendeeForMcuZte = (InvitedAttendeeForMcuZte) attendeeForMcuZte;
                    if (invitedAttendeeForMcuZte.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZte.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuZte.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuZte.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZte.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuZte.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZte.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuZte.getId());
                        updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                        McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                        McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuZte.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZte.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuZte.getId());
                    updateMap.put("deptId", attendeeForMcuZte.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuZte.isMcuAttendee());
                    McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics", new AttendeeCountingStatistics(conferenceContext));
                    McuZteWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_CHANGE, jsonObject);
                }
                String reason = "【" + attendeeForMcuZte.getName() + "】离会";
                McuZteWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZte);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuZte);
                conferenceContext.removeUuidAttendee(attendeeForMcuZte);
            }
        }

        //
    }

    private void processUpdateParticipant(McuZteConferenceContext conferenceContext, AttendeeForMcuZte attendee) {
        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private void processUpdateParticipant(McuZteConferenceContext conferenceContext, AttendeeForMcuZte attendee, boolean updateMediaInfo) {
        IBusiHistoryConferenceForMcuZteService busiHistoryConferenceForMcuZteService = BeanFactory.getBean(IBusiHistoryConferenceForMcuZteService.class);
        busiHistoryConferenceForMcuZteService.updateBusiHistoryParticipant(conferenceContext, attendee, updateMediaInfo);
    }

    private void clearMasterAttendee(McuZteConferenceContext conferenceContext) {
        AttendeeForMcuZte oldMasterAttendee = conferenceContext.getMasterAttendee();
        Map<String, Object> data = new HashMap<>();
        data.put("oldMasterAttendee", oldMasterAttendee);
        data.put("newMasterAttendee", null);
        McuZteWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("主会场已离会【").append(oldMasterAttendee.getName()).append("】");
        McuZteWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        conferenceContext.clearMasterAttendee();
    }

    private void updateOperation(McuZteConferenceContext conferenceContext) {
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        if (attendeeOperation instanceof DefaultAttendeeOperation) {
            attendeeOperation.setForceUpdateView(true);
        }
    }

}
