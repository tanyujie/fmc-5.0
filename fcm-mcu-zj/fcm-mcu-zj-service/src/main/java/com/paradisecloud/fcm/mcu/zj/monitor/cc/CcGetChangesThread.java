package com.paradisecloud.fcm.mcu.zj.monitor.cc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.sign.Base64;
import com.paradisecloud.fcm.common.constant.ConfigConstant;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.utils.SipAccountUtil;
import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.ViewTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUserTerminal;
import com.paradisecloud.fcm.dao.model.ViewTemplateConference;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.attendee.model.operation.DefaultAttendeeOperationForGuest;
import com.paradisecloud.fcm.mcu.zj.cache.AttendeeCountingStatistics;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjWebSocketMessagePusher;
import com.paradisecloud.fcm.mcu.zj.cache.api.ConferenceControlApi;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.EpsInfo;
import com.paradisecloud.fcm.mcu.zj.model.UserInfo;
import com.paradisecloud.fcm.mcu.zj.model.busi.attendee.*;
import com.paradisecloud.fcm.mcu.zj.model.busi.operation.AttendeeOperation;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcGetChangesRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcLoginRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.CcUpdateMrEpsStatusRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmGetUsrInfoRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.CmSearchUsrRequest;
import com.paradisecloud.fcm.mcu.zj.model.response.cc.*;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmGetUsrInfoResponse;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.CmSearchUsrResponse;
import com.paradisecloud.fcm.mcu.zj.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IAttendeeForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiHistoryConferenceForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.ISimpleConferenceControlForMcuZjService;
import com.paradisecloud.fcm.mcu.zj.task.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.interfaces.IMqttService;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;
import com.paradisecloud.fcm.terminal.fsbc.cache.FsbcBridgeCache;
import com.paradisecloud.fcm.terminal.fsbc.model.FsbcBridge;
import com.paradisecloud.system.service.ISysConfigService;
import com.sinhy.spring.BeanFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CcGetChangesThread extends Thread {

    /**
     * key:conferenceNumber
     */
    private Map<String, Thread> conferenceThreadMap = new ConcurrentHashMap<>();
    private Map<String, Thread> meetingInfoThreadMap = new ConcurrentHashMap<>();
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
                for (McuZjConferenceContext conferenceContext : McuZjConferenceContextCache.getInstance().values()) {
                    if (conferenceContext.isEnd()) {
                        DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
                        String coSpace = conferenceContext.getCoSpaceId();
                        DownloadRecordFileTask downloadRecordFileTask = new DownloadRecordFileTask(coSpace, 30000, conferenceContext.getMcuZjBridge(), coSpace, conferenceContext.getDeptId(), conferenceContext.getName());
                        delayTaskService.addTask(downloadRecordFileTask);
                        McuZjConferenceContextCache.getInstance().remove(conferenceContext.getContextKey());
                    }
                    if (!conferenceThreadMap.containsKey(conferenceContext.getContextKey())) {
                        startConferenceThread(conferenceContext);
                        startJoinedTerminalThread(conferenceContext);
                    } else {
                        if (!meetingInfoThreadMap.containsKey(conferenceContext.getContextKey())) {
                            startJoinedTerminalThread(conferenceContext);
                        } else {
                            Thread meetingInfoThread = meetingInfoThreadMap.get(conferenceContext.getContextKey());
                            if (meetingInfoThread == null || !meetingInfoThread.isAlive() || meetingInfoThread.isInterrupted()) {
                                startJoinedTerminalThread(conferenceContext);
                            }
                        }
                        if (!pollingThreadMap.containsKey(conferenceContext.getContextKey())) {
                            startPollingThread(conferenceContext);
                        } else {
                            Thread pollingThread = pollingThreadMap.get(conferenceContext.getContextKey());
                            if (pollingThread == null || !pollingThread.isAlive() || pollingThread.isInterrupted()) {
                                startPollingThread(conferenceContext);
                            }
                        }
                    }
                    GetUsrOnlineStatusInConferenceTask getUsrOnlineStatusInConferenceTask = new GetUsrOnlineStatusInConferenceTask(conferenceContext.getId(), 0, conferenceContext);
                    DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
                    delayTaskService.addTask(getUsrOnlineStatusInConferenceTask);
                }

                for (String contextKey : conferenceThreadMap.keySet()) {
                    Thread thread = conferenceThreadMap.get(contextKey);
                    if (thread == null || !thread.isAlive() || thread.isInterrupted()) {
                        conferenceThreadMap.remove(contextKey);
                        try {
                            meetingInfoThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        meetingInfoThreadMap.remove(contextKey);
                        try {
                            pollingThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        pollingThreadMap.remove(contextKey);
                        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(contextKey);
                        if (mcuZjConferenceContext != null) {
                            mcuZjConferenceContext.cleanLoginInfo();
                        }
                    }
                    if (!McuZjConferenceContextCache.getInstance().containsKey(contextKey)) {
                        try {
                            conferenceThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        conferenceThreadMap.remove(contextKey);
                        try {
                            meetingInfoThreadMap.get(contextKey).interrupt();
                        } catch (Exception e) {
                        }
                        meetingInfoThreadMap.remove(contextKey);
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
//                e.printStackTrace();
                return;
            }
        }
    }

    public void startConferenceThread(McuZjConferenceContext conferenceContext) {
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
                        long currentTimeMillis = System.currentTimeMillis();
                        if (conferenceContext.getLastUpdateTime() == 0 || StringUtils.isEmpty(conferenceContext.getSessionId()) || currentTimeMillis - conferenceContext.getLastUpdateTime() > 30000) {
                            // 未登录时先登录
                            // 获取验证码
                            CcGetVerifyCodeResponse ccGetVerifyCodeResponse = conferenceContext.getConferenceControlApi().getVerifyCode();
                            if (ccGetVerifyCodeResponse != null && ConferenceControlApi.CMD_ID_verify_code.equals(ccGetVerifyCodeResponse.getCmdid())) {
                                CcLoginRequest ccLoginRequest = new CcLoginRequest();
                                String certCode = new String(Base64.decode(ccGetVerifyCodeResponse.getBase64()));
                                ccLoginRequest.setCert_code(certCode);
                                ccLoginRequest.setMr_id(conferenceContext.getConferenceNumber());
                                ccLoginRequest.setCtrl_pwd(conferenceContext.getConferenceCtrlPassword());
                                // 登录
                                CcLoginResponse ccLoginResponse = conferenceContext.getConferenceControlApi().login(ccLoginRequest);
                                if (ccLoginResponse != null && ConferenceControlApi.CMD_ID_ctrl_mr_rsp.equals(ccLoginResponse.getCmdid())) {
                                    if ("404".equals(ccLoginResponse.getResult()) || StringUtils.isEmpty(ccLoginResponse.getSess_id())) {
                                        conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo("=============> 会议不存在，删除会议！");
                                        McuZjConferenceContextCache.getInstance().remove(conferenceContext.getContextKey());
                                        return;
                                    }
                                    conferenceContext.setSessionId(ccLoginResponse.getSess_id());
                                    conferenceContext.setLastUpdateTime(currentTimeMillis);
                                    conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo("=============> 会议登录成功。会议号："+ conferenceContext.getConferenceNumber() + "" + "，sessionId：" + conferenceContext.getSessionId());
                                } else {
                                    conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo("=============> 会议登录失败。会议号："+ conferenceContext.getConferenceNumber());
                                    return;
                                }
                            } else {
                                return;
                            }
                            // 恢复横幅
                            RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                            ISimpleConferenceControlForMcuZjService simpleConferenceControlForMcuZjService = BeanFactory.getBean(ISimpleConferenceControlForMcuZjService.class);
                            Object cacheObject = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_banner_text");
                            if (ObjectUtils.isNotEmpty(cacheObject)) {
                                String bannerText = (String) cacheObject;
                                simpleConferenceControlForMcuZjService.setBanner(conferenceContext, bannerText);
                            }
                        }
                        if (StringUtils.isNotEmpty(conferenceContext.getSessionId())) {
                            while (true) {
                                if (isInterrupted()) {
                                    return;
                                }
                                if (conferenceContext.isEnd()) {
                                    return;
                                }
                                if (StringUtils.isEmpty(conferenceContext.getSessionId())) {
                                    conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo("=============> 会议退出登录。会议号："+ conferenceContext.getConferenceNumber());
                                    conferenceContext.setLastUpdateTime(0);
                                    return;
                                }
                                CcGetChangesRequest ccGetChangesRequest = new CcGetChangesRequest();
                                ccGetChangesRequest.setBlock_secs(10);
                                String response = conferenceContext.getConferenceControlApi().getChanges(ccGetChangesRequest);
                                if (response != null) {
                                    try {
                                        boolean logout = false;
                                        conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo(conferenceContext.getBaseUrl() + " #CC# getChange:response:" + response);
                                        List<JSONObject> jsonArray = JSON.parseArray(response, JSONObject.class);
                                        for (JSONObject jsonObject : jsonArray) {
                                            try {
                                                processResponse(jsonObject, conferenceContext);
                                            } catch (Exception e) {
                                                if ("logout".equals(e.getMessage())) {
                                                    logout = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (logout) {
                                            conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo("=============> 会议退出登录。会议号："+ conferenceContext.getConferenceNumber());
                                            conferenceContext.setLastUpdateTime(0);
                                            break;
                                        }
                                    } catch (Exception e) {
                                    }
                                    conferenceContext.setLastUpdateTime(currentTimeMillis);
                                } else {
                                    conferenceContext.getMcuZjBridge().getMcuZjLogger().logInfo("=============> 会议退出登录。会议号："+ conferenceContext.getConferenceNumber());
                                    conferenceContext.setLastUpdateTime(0);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        conferenceThreadMap.put(conferenceContext.getContextKey(), thread);
    }

    private void startJoinedTerminalThread(McuZjConferenceContext conferenceContext) {
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
                        boolean hasMrCascadeSub = false;
                        // 处理未匹配的会场
                        List<EpsInfo> epsInfoList = conferenceContext.getEpsInfoList();
                        if (epsInfoList != null) {
                            for (EpsInfo epsInfo : epsInfoList) {
                                String userId = epsInfo.getUsr_id();
                                String devType = epsInfo.getDev_type();
                                if ("mr_cascade_sub".equals(devType)) {
                                    hasMrCascadeSub = true;
                                    continue;
                                }
                                if (SipAccountUtil.isZjAutoAccount(userId)) {
                                    userId = userId.substring(4);
                                }
                                CmSearchUsrRequest cmSearchUsrRequest = new CmSearchUsrRequest();
                                String[] filterType = new String[1];
                                filterType[0] = "usr_mark";
                                Object[] filterValue = new Object[1];
                                filterValue[0] = userId;
                                cmSearchUsrRequest.setFilter_type(filterType);
                                cmSearchUsrRequest.setFilter_value(filterValue);
                                CmSearchUsrResponse cmSearchUsrResponse = conferenceContext.getMcuZjBridge().getConferenceManageApi().searchUsr(cmSearchUsrRequest);
                                if (cmSearchUsrResponse != null && cmSearchUsrResponse.getUsr_ids().length > 0) {
                                    // 存在
                                    CmGetUsrInfoRequest cmGetUsrInfoRequest = new CmGetUsrInfoRequest();
                                    cmGetUsrInfoRequest.setUsr_ids(cmSearchUsrResponse.getUsr_ids());
                                    Integer[] lastModifyDtms = new Integer[cmSearchUsrResponse.getUsr_ids().length];
                                    for (int i = 0; i < cmSearchUsrResponse.getUsr_ids().length; i++) {
                                        lastModifyDtms[i] = 0;
                                    }
                                    cmGetUsrInfoRequest.setLast_modify_dtms(lastModifyDtms);
                                    CmGetUsrInfoResponse cmGetUsrInfoResponse = conferenceContext.getMcuZjBridge().getConferenceManageApi().getUsrInfo(cmGetUsrInfoRequest);
                                    if (cmGetUsrInfoResponse != null && cmGetUsrInfoResponse.getUsr_ids().length > 0) {
                                        for (int i = 0; i < cmGetUsrInfoResponse.getUsr_ids().length; i++) {
                                            UserInfo userInfo = new UserInfo();
                                            userInfo.setUsr_id(cmGetUsrInfoResponse.getUsr_marks()[i]);
                                            userInfo.setNick_name(cmGetUsrInfoResponse.getNick_names()[i]);
                                            userInfo.setCall_addr(cmGetUsrInfoResponse.getCall_addrs()[i]);
                                            conferenceContext.addUserInfo(userInfo);
                                        }
                                    }
                                }
                            }
                            if (hasMrCascadeSub) {
                                List<McuAttendeeForMcuZj> mcuAttendees = conferenceContext.getMcuAttendees();
                                for (McuAttendeeForMcuZj attendee : mcuAttendees) {
                                    if (attendee.isMeetingJoined()) {
                                        continue;
                                    }
                                    String contextKey = EncryptIdUtil.parasToContextKey(attendee.getId());
                                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                                    if (baseConferenceContext != null) {
                                        String remoteParty = baseConferenceContext.getTenantId() + baseConferenceContext.getConferenceNumber() + "@" + baseConferenceContext.getMcuCallIp();
                                        if (baseConferenceContext.getMcuCallPort() != null && baseConferenceContext.getMcuCallPort() != 5060) {
                                            remoteParty += ":" + baseConferenceContext.getMcuCallPort();
                                        }
                                        String oldRemoteParty = attendee.getRemoteParty();
                                        attendee.setRemoteParty(remoteParty);
                                        attendee.setIp(baseConferenceContext.getMcuCallIp());
                                        conferenceContext.updateAttendeeToRemotePartyMap(oldRemoteParty, attendee);
                                    } else {
                                        attendee.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                                    }
                                }
                            }
                            for (EpsInfo epsInfo : epsInfoList) {
                                AttendeeForMcuZj attendee = null;
                                Integer epId = epsInfo.getEp_id();
                                String usrId = epsInfo.getUsr_id();
                                String uuid = epsInfo.getEp_uuid();
                                String name = epsInfo.getUsr_name();
                                String uri = epsInfo.getUsr_uri();
                                Integer protoType = epsInfo.getProto_type();
                                String userMark = usrId;
                                String devType = epsInfo.getDev_type();
                                if ("mr_cascade_sub".equals(devType)) {
                                    boolean delayCheck = false;
                                    UserInfo userInfo = conferenceContext.getUserInfo(usrId);
                                    if (userInfo != null) {
                                        String remoteParty = userInfo.getCall_addr();
                                        Map<String, AttendeeForMcuZj> attendeeMap = conferenceContext.getAttendeeMapByUri(remoteParty);
                                        if (attendeeMap != null) {
                                            for (AttendeeForMcuZj attendeeEx : attendeeMap.values()) {
                                                attendee = attendeeEx;
                                            }
                                            // 临时邀请未知终端修改名称
                                            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                                            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_displayname);
                                            ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{usrId});
                                            ccUpdateMrEpsStatusRequest.setValue(attendee.getName() + "(" + remoteParty.substring(0, remoteParty.indexOf("@")) + ")");
                                            conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                                        } else {
                                            delayCheck = true;
                                        }
                                    } else {
                                        delayCheck = true;
                                    }
                                    if (delayCheck) {
                                        continue;
                                    }
                                }
                                if (SipAccountUtil.isZjAutoAccount(usrId)) {
                                    userMark = usrId.substring(4);
                                }
                                UserInfo userInfo = conferenceContext.getUserInfo(userMark);
                                String remoteParty = "";
                                if (userInfo != null) {
                                    if (StringUtils.isNotEmpty(userInfo.getCall_addr())) {
                                        remoteParty = userInfo.getCall_addr();
                                    }
                                }
                                if (StringUtils.isNotEmpty(remoteParty)) {
                                    if (remoteParty.equals(conferenceContext.getStreamingRemoteParty())) {
                                        attendee = conferenceContext.getStreamingAttendee();
                                        attendee.resetUpdateMap();
                                        if (attendee instanceof TerminalAttendeeForMcuZj) {
                                            TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendee;
                                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                                            if (busiTerminal != null) {
                                                if (!busiTerminal.getName().equals(name)) {
                                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                                } else {
                                                    attendee.setName(busiTerminal.getName());
                                                }
                                            }
                                        } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                                            InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendee;
                                            if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                                                if (busiTerminal != null) {
                                                    if (!busiTerminal.getName().equals(name)) {
                                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                                    } else {
                                                        attendee.setName(busiTerminal.getName());
                                                    }
                                                }
                                            }
                                        }
                                        attendee.setEpId(epId);
                                        attendee.setEpUserId(usrId);
                                        attendee.setProtoType(protoType);
                                        attendee.setParticipantUuid(uuid);
                                        attendee.setHangUp(false);
                                        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());

                                        boolean isShowEnabled = false;
                                        String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLE);
                                        if (ConfigConstant.SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLED.equals(showEnable)) {
                                            isShowEnabled = true;
                                        }
                                        if (isShowEnabled) {
                                            AttendeeForMcuZj attendeeExist = conferenceContext.getAttendeeById(attendee.getId());
                                            conferenceContext.addAttendee(attendee);
                                            conferenceContext.addEpUserAttendee(attendee);
                                            if (attendeeExist == null) {
                                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                                            }
                                        }

                                        if (!conferenceContext.isStreaming()) {
                                            conferenceContext.setStreaming(true);
                                            // 向所有客户端通知会议的录制状态
                                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, true);
                                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");

                                            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                                        }

                                        continue;
                                    } else {
                                        Map<String, AttendeeForMcuZj> attendeeMap = conferenceContext.getAttendeeMapByUri(remoteParty);
                                        if (attendeeMap != null) {
                                            for (AttendeeForMcuZj attendeeEx : attendeeMap.values()) {
                                                attendee = attendeeEx;
                                            }
                                        }
                                    }
                                } else {
                                    {
                                        // IP邀请入会
                                        Map<String, AttendeeForMcuZj> attendeeForMcuZjMap = conferenceContext.getAttendeeMapByUri(name);
                                        if (attendeeForMcuZjMap != null && attendeeForMcuZjMap.size() > 0) {
                                            for (String key : attendeeForMcuZjMap.keySet()) {
                                                attendee = attendeeForMcuZjMap.get(key);
                                            }
                                            // 临时邀请未知终端修改名称
                                            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                                            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_displayname);
                                            ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{usrId});
                                            ccUpdateMrEpsStatusRequest.setValue(attendee.getName());
                                            conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                                        }
                                    }
                                    if (attendee == null) {
                                        // IP直接呼入
                                        Map<String, AttendeeForMcuZj> attendeeForMcuZjMap = conferenceContext.getAttendeeMapByUri(uri);
                                        if (attendeeForMcuZjMap != null && attendeeForMcuZjMap.size() > 0) {
                                            for (String key : attendeeForMcuZjMap.keySet()) {
                                                attendee = attendeeForMcuZjMap.get(key);
                                            }
                                            if (attendee instanceof TerminalAttendeeForMcuZj) {
                                                TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendee;
                                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                                                if (busiTerminal != null) {
                                                    if (!busiTerminal.getName().equals(name)) {
                                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                                    } else {
                                                        attendee.setName(busiTerminal.getName());
                                                    }
                                                }
                                            } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                                                InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendee;
                                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                                                if (busiTerminal != null) {
                                                    if (!busiTerminal.getName().equals(name)) {
                                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                                    } else {
                                                        attendee.setName(busiTerminal.getName());
                                                    }
                                                }
                                            }
                                        }
                                        if (attendee == null) {
                                            BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(uri);
                                            if (busiTerminal != null) {
                                                if (!busiTerminal.getName().equals(name)) {
                                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                                } else {
                                                    attendee.setName(busiTerminal.getName());
                                                }
                                            }
                                        }
                                    }
                                }
                                if (attendee != null) {
                                    attendee.resetUpdateMap();
                                    attendee.setEpId(epId);
                                    attendee.setEpUserId(usrId);
                                    attendee.setProtoType(protoType);
                                    attendee.setParticipantUuid(uuid);
                                    attendee.setHangUp(false);
                                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                    attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                                    if (conferenceContext.isLocked()) {
                                        attendee.setLocked(true);
                                    }
                                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());

                                    // 主会场
                                    AttendeeForMcuZj masterAttendee = conferenceContext.getMasterAttendee();
                                    if (masterAttendee != null) {
                                        if (masterAttendee.getId().equals(attendee.getId())) {
                                            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                                            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_speaker);
                                            String[] strings = new String[1];
                                            strings[0] = attendee.getEpUserId();
                                            ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
                                            ccUpdateMrEpsStatusRequest.setValue(1);
                                            conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                                        }
                                    }
                                } else {
                                    SelfCallAttendeeForMcuZj selfCallAttendee = new SelfCallAttendeeForMcuZj();
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
                                            if (!busiTerminal.getName().equals(name)) {
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
                                    attendee.setEpId(epId);
                                    attendee.setEpUserId(usrId);
                                    attendee.setProtoType(protoType);
                                    attendee.setParticipantUuid(uuid);
                                    attendee.setHangUp(false);
                                    if (StringUtils.isEmpty(attendee.getRemoteParty())) {
                                        attendee.setRemoteParty(uri);
                                    }
                                    if (uri.contains("@")) {
                                        attendee.setIp(uri.substring(uri.indexOf("@") + 1));
                                    } else {
                                        attendee.setIp(uri);
                                    }
                                    attendee.setDeptId(conferenceContext.getDeptId());
                                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                    attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                                    attendee.setConferenceNumber(conferenceContext.getConferenceNumber());
                                    attendee.setWeight(1);
                                    if (conferenceContext.isLocked()) {
                                        attendee.setLocked(true);
                                    }
                                    conferenceContext.addAttendee(attendee);
                                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, selfCallAttendee);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                                    updateOperation(conferenceContext);
                                }

                                conferenceContext.addEpUserAttendee(attendee);

                                BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);

                                conferenceContext.removeEpsInfo(epsInfo);
                                // 更新数据库
                                processUpdateParticipant(conferenceContext, attendee, true);
                                if (StringUtils.isNotEmpty(epsInfo.getUsr_stat())) {
                                    CcEpsStatusResponse ccEpsStatusResponse = new CcEpsStatusResponse();
                                    ccEpsStatusResponse.setUsr_ids(new String[]{epsInfo.getUsr_id()});
                                    ccEpsStatusResponse.setUsr_stats(new String[]{epsInfo.getUsr_stat()});
                                    processEspStatusResponse(ccEpsStatusResponse, conferenceContext);
                                }
                                if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                                    BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                                } else if (StringUtils.isNotEmpty(conferenceContext.getUpCascadeConferenceId())) {
                                    BaseConferenceContext upCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceContext.getUpCascadeConferenceId()));
                                    if (upCascadeConferenceContext != null) {
                                        if (attendee.getName().equals(upCascadeConferenceContext.getName()) && attendee.getRemoteParty().equals(upCascadeConferenceContext.getMcuCallIp())) {
                                            attendee.setRemoteParty(conferenceContext.getUpCascadeRemoteParty());
                                            BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        meetingInfoThreadMap.put(conferenceContext.getContextKey(), thread);
    }

    private void startPollingThread(McuZjConferenceContext conferenceContext) {
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
                        // 处理分屏（观众）
                        AttendeeOperation attendeeOperationForGuest = conferenceContext.getAttendeeOperationForGuest();
                        if (attendeeOperationForGuest != null) {
                            if (attendeeOperationForGuest instanceof DefaultAttendeeOperationForGuest) {
                                try {
                                    attendeeOperationForGuest.operate();
                                } catch (Exception e) {
                                    if (!(attendeeOperationForGuest instanceof DefaultAttendeeOperation)) {
                                        conferenceContext.setAttendeeOperationForGuest(conferenceContext.getLastAttendeeOperationForGuest());
                                        attendeeOperationForGuest.cancel();
                                    }
                                }
                            }
                        }

                        if (conferenceContext.getMessageCloseTime() != null) {
                            if (System.currentTimeMillis() - conferenceContext.getMessageCloseTime() > 0) {
                                RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                                ISimpleConferenceControlForMcuZjService simpleConferenceControlForMcuZjService = BeanFactory.getBean(ISimpleConferenceControlForMcuZjService.class);
                                Object cacheObject = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + conferenceContext.getStartTime().getTime() + "_banner_text");
                                String bannerText = "";
                                if (ObjectUtils.isNotEmpty(cacheObject)) {
                                    bannerText = (String) cacheObject;
                                }
                                simpleConferenceControlForMcuZjService.setBanner(conferenceContext, bannerText);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        pollingThreadMap.put(conferenceContext.getContextKey(), thread);
    }

    private void processResponse(JSONObject jsonObject, McuZjConferenceContext conferenceContext) throws Exception {
        String cmdid = jsonObject.getString("cmdid");
        if (StringUtils.isNotEmpty(cmdid)) {
            if (ConferenceControlApi.CMD_ID_one_mr_info.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcMrInfoResponse ccMrInfoResponse = JSON.parseObject(contentBody, CcMrInfoResponse.class);
                processMrInfoResponse(ccMrInfoResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_one_mr_status.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcMrStatusResponse ccMrStatusResponse = JSON.parseObject(contentBody, CcMrStatusResponse.class);
                processMrStatusResponse(ccMrStatusResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_mr_participants.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcMrParticipantsResponse ccMrParticipantsResponse = JSON.parseObject(contentBody, CcMrParticipantsResponse.class);
                processMrParticipantsResponse(ccMrParticipantsResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_online_eps_info.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcEpsInfoResponse ccEpsInfoResponse = JSON.parseObject(contentBody, CcEpsInfoResponse.class);
                processEspInfoResponse(ccEpsInfoResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_online_eps_status.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcEpsStatusResponse ccEpsStatusResponse = JSON.parseObject(contentBody, CcEpsStatusResponse.class);
                processEspStatusResponse(ccEpsStatusResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_online_eps_left.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcEpsLeftResponse ccEpsLeftResponse = JSON.parseObject(contentBody, CcEpsLeftResponse.class);
                processEpsLeftResponse(ccEpsLeftResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_left_call.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CcLeftCallResponse ccLeftCallResponse = JSON.parseObject(contentBody, CcLeftCallResponse.class);
                processLeftCallResponse(ccLeftCallResponse, conferenceContext);
            } else if (ConferenceControlApi.CMD_ID_force_logout.equals(cmdid)) {
                throw new Exception("logout");
            }
        }
    }

    private void processMrInfoResponse(CcMrInfoResponse ccMrInfoResponse, McuZjConferenceContext conferenceContext) {
        if (ccMrInfoResponse != null && ccMrInfoResponse.getCmdid() != null) {
            if (conferenceContext != null) {
                // 未完待续
            }
        }
    }

    private void processMrStatusResponse(CcMrStatusResponse ccMrStatusResponse, McuZjConferenceContext conferenceContext) {
        if (ccMrStatusResponse != null && ccMrStatusResponse.getCmdid() != null) {
            DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
            if (conferenceContext != null) {
                // 未完待续
                Integer mr_locked = ccMrStatusResponse.getMr_locked();
                Integer rec_enabled = ccMrStatusResponse.getRec_enabled();
                Long flv_fsize = ccMrStatusResponse.getFlv_fsize();
                Integer bypass_enabled = ccMrStatusResponse.getBypass_enabled();
                boolean changed = false;
                if (mr_locked != null) {
                    if (mr_locked == 1) {
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, mr_locked == 1);
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (mr_locked == 1 ? "" : "解除") + "锁定");
                        conferenceContext.setLocked(true);
                        changed = true;
                    } else {
                        if (conferenceContext.isLocked()) {
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.CONFERENCE_LOCK, mr_locked == 1);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已" + (mr_locked == 1 ? "" : "解除") + "锁定");
                            conferenceContext.setLocked(false);
                            UnlockAttendeesTask unlockAttendeesTask = new UnlockAttendeesTask(conferenceContext.getId(), 10, conferenceContext);
                            delayTaskService.addTask(unlockAttendeesTask);
                            changed = true;
                        }
                    }
                }
                if (rec_enabled != null) {
                    if (rec_enabled == 1) {
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, true);
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启录制");
                        conferenceContext.setRecorded(true);
                        changed = true;
                    } else {
                        if (conferenceContext.isRecorded()) {
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, false);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭录制");
                            conferenceContext.setRecorded(false);
                            changed = true;
                        }
                    }
                } else {
                    if (flv_fsize != null) {
                        if (!conferenceContext.isRecorded()) {
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.RECORDED, true);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启录制");
                            conferenceContext.setRecorded(true);
                            changed = true;
                        }
                    }
                }
                if (bypass_enabled != null) {
                    if (bypass_enabled == 1) {
                        conferenceContext.setStreaming(true);
                        changed = true;
                        // 向所有客户端通知会议的录制状态
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, true);
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");
                    } else {
                        if (conferenceContext.isStreaming()) {
                            conferenceContext.setStreaming(false);
                            changed = true;
                            // 向所有客户端通知会议的录制状态
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, false);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");
                        }
                    }
                }

                if (changed) {
                    BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                }
            }
        }
    }

    private void processMrParticipantsResponse(CcMrParticipantsResponse ccMrParticipantsResponse, McuZjConferenceContext conferenceContext) {
        if (ccMrParticipantsResponse != null && ccMrParticipantsResponse.getAdded_usr_ids() != null) {
            for (int i = 0; i < ccMrParticipantsResponse.getAdded_usr_ids().length; i++) {
                if (ccMrParticipantsResponse.getAdded_usr_ids()[i].startsWith("guest.")) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsr_id(ccMrParticipantsResponse.getAdded_usr_ids()[i]);
                    userInfo.setNick_name(ccMrParticipantsResponse.getAdded_usr_names()[i]);
                    userInfo.setCall_addr(ccMrParticipantsResponse.getAdded_call_addrs()[i].replace(",", "@"));
                    conferenceContext.addUserInfo(userInfo);
                }
            }
        }
    }

    private void processEspInfoResponse(CcEpsInfoResponse ccEpsInfoResponse, McuZjConferenceContext conferenceContext) {
        if (ccEpsInfoResponse != null && ccEpsInfoResponse.getEp_ids() != null) {
            for (int i = 0; i < ccEpsInfoResponse.getUsr_ids().length; i++) {
                AttendeeForMcuZj attendee = null;
                Integer epId = ccEpsInfoResponse.getEp_ids()[i];
                String usrId = ccEpsInfoResponse.getUsr_ids()[i];
                String uuid = ccEpsInfoResponse.getEp_uuid()[i];
                String name = ccEpsInfoResponse.getUsr_names()[i];
                String uri = ccEpsInfoResponse.getUsr_uris()[i];
                String devType = ccEpsInfoResponse.getDev_type()[i];
                uri = uri.replace("sip:", "");
                if (uri.contains(":")) {
                    uri = uri.substring(0, uri.indexOf(":"));
                }
                if (uri.contains(";")) {
                    uri = uri.substring(0, uri.indexOf(";"));
                }
                Integer protoType = ccEpsInfoResponse.getProto_types()[i];
                String remoteParty = "";
                {
                    String zjRemoteParty;
                    if (NumberUtils.isDigits(usrId)) {
                        zjRemoteParty = usrId + "@" + conferenceContext.getMcuZjBridge().getBusiMcuZj().getIp();
                        if (!SipAccountUtil.isZjAutoAccount(usrId)) {
                            remoteParty = zjRemoteParty;
                        }
                    } else {
                        zjRemoteParty = uri;
                    }
                    if ("mr_cascade_sub".equals(devType)) {
                        boolean delayCheck = false;
                        UserInfo userInfo = conferenceContext.getUserInfo(usrId);
                        if (userInfo != null) {
                            zjRemoteParty = userInfo.getCall_addr();
                            Map<String, AttendeeForMcuZj> attendeeMap = conferenceContext.getAttendeeMapByUri(zjRemoteParty);
                            if (attendeeMap != null) {
                                for (AttendeeForMcuZj attendeeEx : attendeeMap.values()) {
                                    attendee = attendeeEx;
                                }
                                // 临时邀请未知终端修改名称
                                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                                ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_displayname);
                                ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{usrId});
                                ccUpdateMrEpsStatusRequest.setValue(attendee.getName() + "(" + zjRemoteParty.substring(0, zjRemoteParty.indexOf("@")) + ")");
                                conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                            } else {
                                delayCheck = true;
                            }
                        } else {
                            delayCheck = true;
                        }
                        if (delayCheck) {
                            EpsInfo epsInfo = new EpsInfo();
                            epsInfo.setMr_id(ccEpsInfoResponse.getMr_id());
                            epsInfo.setUsr_id(ccEpsInfoResponse.getUsr_ids()[i]);
                            epsInfo.setUsr_uri(ccEpsInfoResponse.getUsr_uris()[i]);
                            epsInfo.setUsr_name(ccEpsInfoResponse.getUsr_names()[i]);
                            epsInfo.setEnter_dtm(ccEpsInfoResponse.getEnter_dtms()[i]);
                            epsInfo.setDev_type(ccEpsInfoResponse.getDev_type()[i]);
                            epsInfo.setEp_version(ccEpsInfoResponse.getEp_version()[i]);
                            epsInfo.setEp_id(ccEpsInfoResponse.getEp_ids()[i]);
                            epsInfo.setProto_type(ccEpsInfoResponse.getProto_types()[i]);
                            epsInfo.setGbk_name(ccEpsInfoResponse.getGbk_names()[i]);
                            epsInfo.setEp_uuid(ccEpsInfoResponse.getEp_uuid()[i]);
                            conferenceContext.addEpsInfo(epsInfo);

                            continue;
                        }
                    }
                    Map<String, AttendeeForMcuZj> attendeeMap = conferenceContext.getAttendeeMapByUri(zjRemoteParty);
                    if (attendeeMap != null) {
                        for (AttendeeForMcuZj attendeeEx : attendeeMap.values()) {
                            attendee = attendeeEx;
                        }
                    }
                    if (zjRemoteParty.equals(conferenceContext.getStreamingRemoteParty())) {
                        attendee = conferenceContext.getStreamingAttendee();
                        attendee.resetUpdateMap();
                        if (attendee instanceof TerminalAttendeeForMcuZj) {
                            TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendee;
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                            if (busiTerminal != null) {
                                if (!busiTerminal.getName().equals(name)) {
                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                } else {
                                    attendee.setName(busiTerminal.getName());
                                }
                            }
                        } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                            InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendee;
                            if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                                BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                                if (busiTerminal != null) {
                                    if (!busiTerminal.getName().equals(name)) {
                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                    } else {
                                        attendee.setName(busiTerminal.getName());
                                    }
                                }
                            }
                        }
                        attendee.setEpId(epId);
                        attendee.setEpUserId(usrId);
                        attendee.setProtoType(protoType);
                        attendee.setParticipantUuid(uuid);
                        attendee.setHangUp(false);
                        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());

                        boolean isShowEnabled = false;
                        String showEnable = BeanFactory.getBean(ISysConfigService.class).selectConfigByKey(ConfigConstant.CONFIG_KEY_SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLE);
                        if (ConfigConstant.SHOW_STREAMING_AND_RECORDING_TERMINAL_ENABLED.equals(showEnable)) {
                            isShowEnabled = true;
                        }
                        if (isShowEnabled) {
                            AttendeeForMcuZj attendeeExist = conferenceContext.getAttendeeById(attendee.getId());
                            conferenceContext.addAttendee(attendee);
                            conferenceContext.addEpUserAttendee(attendee);
                            if (attendeeExist == null) {
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                            }
                        }

                        if (!conferenceContext.isStreaming()) {
                            conferenceContext.setStreaming(true);
                            // 向所有客户端通知会议的录制状态
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, true);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");

                            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);
                        }

                        continue;
                    }
                    if (attendee == null) {
                        if (StringUtils.isNotEmpty(remoteParty) && remoteParty.contains("@")) {
                            SelfCallAttendeeForMcuZj selfCallAttendee = new SelfCallAttendeeForMcuZj();
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
                                    if (!busiTerminal.getName().equals(name)) {
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
                            attendee.setEpId(epId);
                            attendee.setEpUserId(usrId);
                            attendee.setProtoType(protoType);
                            attendee.setParticipantUuid(uuid);
                            attendee.setHangUp(false);
                            attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                            attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                            if (StringUtils.isEmpty(remoteParty)) {
                                attendee.setRemoteParty(uri);
                            }
                            if (uri.contains("@")) {
                                attendee.setIp(uri.substring(uri.indexOf("@") + 1));
                            } else {
                                attendee.setIp(uri);
                            }
                            attendee.setDeptId(conferenceContext.getDeptId());
                            attendee.setConferenceNumber(conferenceContext.getConferenceNumber());
                            attendee.setWeight(1);
                            if (conferenceContext.isLocked()) {
                                attendee.setLocked(true);
                            }
                            conferenceContext.addAttendee(attendee);
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            updateOperation(conferenceContext);

                            conferenceContext.addEpUserAttendee(attendee);

                            BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendee, true);
                            if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                                BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                            } else if (StringUtils.isNotEmpty(conferenceContext.getUpCascadeConferenceId())) {
                                BaseConferenceContext upCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceContext.getUpCascadeConferenceId()));
                                if (upCascadeConferenceContext != null) {
                                    if (attendee.getName().equals(upCascadeConferenceContext.getName()) && attendee.getRemoteParty().equals(upCascadeConferenceContext.getMcuCallIp())) {
                                        attendee.setRemoteParty(conferenceContext.getUpCascadeRemoteParty());
                                        BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                                    }
                                }
                            }
                        }
                    }
                }
                if (attendee == null) {
                    if (SipAccountUtil.isZjAutoAccount(usrId)) {
                        String usrIdNew = usrId.substring(4);
                        // 为注册用户
                        UserInfo userInfo = conferenceContext.getUserInfo(usrIdNew);
                        if (userInfo != null) {
                            if (StringUtils.isNotEmpty(userInfo.getCall_addr())) {
                                remoteParty = userInfo.getCall_addr();
                            }
                        }
                        if (StringUtils.isNotEmpty(remoteParty)) {
                            if (remoteParty.equals(conferenceContext.getStreamingRemoteParty())) {
                                attendee = conferenceContext.getStreamingAttendee();
                                attendee.resetUpdateMap();
                                if (attendee instanceof TerminalAttendeeForMcuZj) {
                                    TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendee;
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                                    if (busiTerminal != null) {
                                        if (!busiTerminal.getName().equals(name)) {
                                            attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                        } else {
                                            attendee.setName(busiTerminal.getName());
                                        }
                                    }
                                } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                                    InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendee;
                                    if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                                        if (busiTerminal != null) {
                                            if (!busiTerminal.getName().equals(name)) {
                                                attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                            } else {
                                                attendee.setName(busiTerminal.getName());
                                            }
                                        }
                                    }
                                }
                                attendee.setEpId(epId);
                                attendee.setEpUserId(usrId);
                                attendee.setProtoType(protoType);
                                attendee.setParticipantUuid(uuid);
                                attendee.setHangUp(false);
                                attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                                attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());

                                conferenceContext.setStreaming(true);
                                // 向所有客户端通知会议的录制状态
                                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, true);
                                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已开启直播");

                                BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);

                                continue;
                            } else {
                                Map<String, AttendeeForMcuZj> attendeeMap = conferenceContext.getAttendeeMapByUri(remoteParty);
                                if (attendeeMap != null) {
                                    for (AttendeeForMcuZj attendeeEx : attendeeMap.values()) {
                                        attendee = attendeeEx;
                                    }
                                }
                            }
                        } else {
                            EpsInfo epsInfo = new EpsInfo();
                            epsInfo.setMr_id(ccEpsInfoResponse.getMr_id());
                            epsInfo.setUsr_id(ccEpsInfoResponse.getUsr_ids()[i]);
                            epsInfo.setUsr_uri(ccEpsInfoResponse.getUsr_uris()[i]);
                            epsInfo.setUsr_name(ccEpsInfoResponse.getUsr_names()[i]);
                            epsInfo.setEnter_dtm(ccEpsInfoResponse.getEnter_dtms()[i]);
                            epsInfo.setDev_type(ccEpsInfoResponse.getDev_type()[i]);
                            epsInfo.setEp_version(ccEpsInfoResponse.getEp_version()[i]);
                            epsInfo.setEp_id(ccEpsInfoResponse.getEp_ids()[i]);
                            epsInfo.setProto_type(ccEpsInfoResponse.getProto_types()[i]);
                            epsInfo.setGbk_name(ccEpsInfoResponse.getGbk_names()[i]);
                            epsInfo.setEp_uuid(ccEpsInfoResponse.getEp_uuid()[i]);
                            conferenceContext.addEpsInfo(epsInfo);

                            continue;
                        }
                    } else {
                        {
                            // IP邀请入会
                            Map<String, AttendeeForMcuZj> attendeeForMcuZjMap = conferenceContext.getAttendeeMapByUri(name);
                            if (attendeeForMcuZjMap != null && attendeeForMcuZjMap.size() > 0) {
                                for (String key : attendeeForMcuZjMap.keySet()) {
                                    attendee = attendeeForMcuZjMap.get(key);
                                }
                                // 临时邀请未知终端修改名称
                                CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                                ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_displayname);
                                ccUpdateMrEpsStatusRequest.setUsr_ids(new String[]{usrId});
                                ccUpdateMrEpsStatusRequest.setValue(attendee.getName());
                                conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                                if (name.contains("@")) {
                                    RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                                    redisCache.setCacheObject(conferenceContext.getConferenceNumber() + "_" + uuid, name, 48, TimeUnit.HOURS);
                                }
                            } else {
                                if (name.contains("@")) {
                                    RedisCache redisCache = BeanFactory.getBean(RedisCache.class);
                                    Object cacheObject = redisCache.getCacheObject(conferenceContext.getConferenceNumber() + "_" + uuid);
                                    if (ObjectUtils.isNotEmpty(cacheObject)) {
                                        String uriStored = (String) cacheObject;
                                        if (uriStored != null && uriStored.contains("@")) {
                                            attendeeForMcuZjMap = conferenceContext.getAttendeeMapByUri(name);
                                            if (attendeeForMcuZjMap != null && attendeeForMcuZjMap.size() > 0) {
                                                for (String key : attendeeForMcuZjMap.keySet()) {
                                                    attendee = attendeeForMcuZjMap.get(key);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (attendee == null) {
                            // IP直接呼入
                            Map<String, AttendeeForMcuZj> attendeeForMcuZjMap = conferenceContext.getAttendeeMapByUri(uri);
                            if (attendeeForMcuZjMap != null && attendeeForMcuZjMap.size() > 0) {
                                for (String key : attendeeForMcuZjMap.keySet()) {
                                    attendee = attendeeForMcuZjMap.get(key);
                                }
                                if (attendee instanceof TerminalAttendeeForMcuZj) {
                                    TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendee;
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                                    if (busiTerminal != null) {
                                        if (!busiTerminal.getName().equals(name)) {
                                            attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                        } else {
                                            attendee.setName(busiTerminal.getName());
                                        }
                                    }
                                } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                                    InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendee;
                                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                                    if (busiTerminal != null) {
                                        if (!busiTerminal.getName().equals(name)) {
                                            attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                        } else {
                                            attendee.setName(busiTerminal.getName());
                                        }
                                    }
                                }
                            }
                            if (attendee == null) {
                                BusiTerminal busiTerminal = TerminalCache.getInstance().getByRemoteParty(uri);
                                if (busiTerminal != null) {
                                    SelfCallAttendeeForMcuZj selfCallAttendee = new SelfCallAttendeeForMcuZj();
                                    attendee = selfCallAttendee;
                                    attendee.setTerminalId(busiTerminal.getId());
                                    attendee.setSn(busiTerminal.getSn());
                                    if (!busiTerminal.getName().equals(name)) {
                                        attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                    } else {
                                        attendee.setName(busiTerminal.getName());
                                    }
                                    attendee.setId(uuid);
                                    attendee.setEpId(epId);
                                    attendee.setEpUserId(usrId);
                                    attendee.setProtoType(protoType);
                                    attendee.setParticipantUuid(uuid);
                                    attendee.setHangUp(false);
                                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                                    attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                                    attendee.setRemoteParty(uri);
                                    if (uri.contains("@")) {
                                        attendee.setIp(uri.substring(uri.indexOf("@") + 1));
                                    } else {
                                        attendee.setIp(uri);
                                    }
                                    attendee.setDeptId(conferenceContext.getDeptId());
                                    attendee.setConferenceNumber(conferenceContext.getConferenceNumber());
                                    attendee.setWeight(1);
                                    if (conferenceContext.isLocked()) {
                                        attendee.setLocked(true);
                                    }
                                    conferenceContext.addAttendee(attendee);
                                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                                    updateOperation(conferenceContext);

                                    conferenceContext.addEpUserAttendee(attendee);

                                    BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                                    // 更新数据库
                                    processUpdateParticipant(conferenceContext, attendee, true);
                                    if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                                        BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                                    } else if (StringUtils.isNotEmpty(conferenceContext.getUpCascadeConferenceId())) {
                                        BaseConferenceContext upCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceContext.getUpCascadeConferenceId()));
                                        if (upCascadeConferenceContext != null) {
                                            if (attendee.getName().equals(upCascadeConferenceContext.getName()) && attendee.getRemoteParty().equals(upCascadeConferenceContext.getMcuCallIp())) {
                                                attendee.setRemoteParty(conferenceContext.getUpCascadeRemoteParty());
                                                BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                                            }
                                        }
                                    }

                                    continue;
                                }
                            }
                        }
                    }
                }
                if (attendee == null) {
                    attendee = conferenceContext.getAttendeeByEpUserId(usrId);
                    if (attendee != null && conferenceContext.getAttendeeById(attendee.getId()) == null) {
                        conferenceContext.removeEpUserAttendee(attendee);
                        attendee.setEpId(epId);
                        attendee.setEpUserId(usrId);
                        attendee.setProtoType(protoType);
                        attendee.setParticipantUuid(uuid);
                        attendee.setHangUp(false);
                        attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                        attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        attendee.setWeight(1);
                        if (conferenceContext.isLocked()) {
                            attendee.setLocked(true);
                        }
                        conferenceContext.addAttendee(attendee);
                        conferenceContext.addEpUserAttendee(attendee);
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        updateOperation(conferenceContext);

                        BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                        // 更新数据库
                        processUpdateParticipant(conferenceContext, attendee, true);

                        continue;
                    }
                }
                if (attendee != null) {
                    attendee.resetUpdateMap();
                    if (attendee instanceof TerminalAttendeeForMcuZj) {
                        TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendee;
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                        if (busiTerminal != null) {
                            if (!busiTerminal.getName().equals(name)) {
                                attendee.setName(busiTerminal.getName() + "(" + name + ")");
                            } else {
                                attendee.setName(busiTerminal.getName());
                            }
                        }
                    } else if (attendee instanceof InvitedAttendeeForMcuZj) {
                        InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendee;
                        if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                            if (busiTerminal != null) {
                                if (!busiTerminal.getName().equals(name)) {
                                    attendee.setName(busiTerminal.getName() + "(" + name + ")");
                                } else {
                                    attendee.setName(busiTerminal.getName());
                                }
                            }
                        }
                    } else if (attendee instanceof McuAttendeeForMcuZj) {
                        attendee.getUpdateMap().put("ip", attendee.getIp());
                        attendee.getUpdateMap().put("remoteParty", attendee.getRemoteParty());
                    }
                    attendee.setEpId(epId);
                    attendee.setEpUserId(usrId);
                    attendee.setProtoType(protoType);
                    attendee.setParticipantUuid(uuid);
                    attendee.setHangUp(false);
                    attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    if (conferenceContext.isLocked()) {
                        attendee.setLocked(true);
                    }
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendee.getUpdateMap());
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                    // 主会场
                    AttendeeForMcuZj masterAttendee = conferenceContext.getMasterAttendee();
                    if (masterAttendee != null) {
                        if (masterAttendee.getId().equals(attendee.getId())) {
                            CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
                            ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_speaker);
                            String[] strings = new String[1];
                            strings[0] = attendee.getEpUserId();
                            ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
                            ccUpdateMrEpsStatusRequest.setValue(1);
                            conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
                        }
                    }
                } else {
                    SelfCallAttendeeForMcuZj selfCallAttendee = new SelfCallAttendeeForMcuZj();
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
                            if (!busiTerminal.getName().equals(name)) {
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
                    attendee.setEpId(epId);
                    attendee.setEpUserId(usrId);
                    attendee.setProtoType(protoType);
                    attendee.setParticipantUuid(uuid);
                    attendee.setHangUp(false);
                    attendee.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    attendee.setMeetingStatus(AttendeeMeetingStatus.IN.getValue());
                    if (StringUtils.isEmpty(attendee.getRemoteParty())) {
                        attendee.setRemoteParty(uri);
                    }
                    if (uri.contains("@")) {
                        attendee.setIp(uri.substring(uri.indexOf("@")));
                    } else {
                        attendee.setIp(uri);
                    }
                    attendee.setDeptId(conferenceContext.getDeptId());
                    attendee.setConferenceNumber(conferenceContext.getConferenceNumber());
                    attendee.setWeight(1);
                    if (conferenceContext.isLocked()) {
                        attendee.setLocked(true);
                    }
                    if (StringUtils.isNotEmpty(conferenceContext.getUpCascadeConferenceId())) {
                        BaseConferenceContext upCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceContext.getUpCascadeConferenceId()));
                        if (upCascadeConferenceContext != null) {
                            if (attendee.getName().equals(upCascadeConferenceContext.getName()) && attendee.getRemoteParty().equals(upCascadeConferenceContext.getMcuCallIp())) {
                                attendee.setRemoteParty(conferenceContext.getUpCascadeRemoteParty());
                            }
                        }
                    }
                    conferenceContext.addAttendee(attendee);
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_ADD, attendee);
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendee.getName() + "】入会");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    updateOperation(conferenceContext);
                }

                conferenceContext.addEpUserAttendee(attendee);

                BeanFactory.getBean(IMqttService.class).sendJoinConferenceToPushTargetTerminal(conferenceContext, attendee);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendee, true);
                if (attendee.getRemoteParty().equals(conferenceContext.getUpCascadeRemoteParty())) {
                    BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                } else if (StringUtils.isNotEmpty(conferenceContext.getUpCascadeConferenceId())) {
                    BaseConferenceContext upCascadeConferenceContext = AllConferenceContextCache.getInstance().get(EncryptIdUtil.parasToContextKey(conferenceContext.getUpCascadeConferenceId()));
                    if (upCascadeConferenceContext != null) {
                        if (attendee.getName().equals(upCascadeConferenceContext.getName()) && attendee.getRemoteParty().equals(upCascadeConferenceContext.getMcuCallIp())) {
                            attendee.setRemoteParty(conferenceContext.getUpCascadeRemoteParty());
                            BeanFactory.getBean(IAttendeeForMcuZjService.class).changeMaster(conferenceContext.getId(), attendee.getId());
                        }
                    }
                }
            }
        }
    }

    private void processEspStatusResponse(CcEpsStatusResponse ccEpsStatusResponse, McuZjConferenceContext conferenceContext) {
        if (ccEpsStatusResponse != null && ccEpsStatusResponse.getCmdid() != null) {
            for (int i = 0; i < ccEpsStatusResponse.getUsr_ids().length; i++) {
                String usrId = ccEpsStatusResponse.getUsr_ids()[i];
                String usrStat = ccEpsStatusResponse.getUsr_stats()[i];
                AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeByEpUserId(usrId);
                if (attendeeForMcuZj == null) {
                    EpsInfo epsInfo = conferenceContext.getEpsInfo(usrId);
                    if (epsInfo != null) {
                        epsInfo.setUsr_stat(usrStat);
                    }
                }
                if (attendeeForMcuZj != null) {
                    // 未完待续
                    try {
                        Integer a_rx = Integer.valueOf(usrStat.substring(0, 1));// 接收端的音频
                        Integer v_rx = Integer.valueOf(usrStat.substring(1, 2));// 接收端的视频
                        Integer a_tx = Integer.valueOf(usrStat.substring(2, 3));// 发送音频给端
                        Integer v_tx = Integer.valueOf(usrStat.substring(3, 4));// 发送视频给端
                        Integer talking = Integer.valueOf(usrStat.substring(4, 5));// 检测到端在说话
                        Integer guest = Integer.valueOf(usrStat.substring(5, 6));// 是嘉宾
                        Integer speaker = Integer.valueOf(usrStat.substring(6, 7));// 是主会场
                        Integer presentation = Integer.valueOf(usrStat.substring(7, 8));// 当前是否正在发送双流
                        Integer canPresentation = Integer.valueOf(usrStat.substring(8, 9));// 是否允许发送双流
                        Integer mic = Integer.valueOf(usrStat.substring(9, 10));// 终端 MIC 状态，0 正常，1 静音
                        Integer camera = Integer.valueOf(usrStat.substring(10, 11));// 终端摄像头状态， 0 正常，1 关闭
                        Integer video = Integer.valueOf(usrStat.substring(11, 12));// 终端接收视频状态，0 正常，1 不接收视频
                        Integer micGain = Integer.valueOf(usrStat.substring(12, 13));// 麦克风增益
                        Integer raiseHandStatus = Integer.valueOf(usrStat.substring(13, 14));// 端的举手状态，0 未举手，1 举手
                        Integer chair = Integer.valueOf(usrStat.substring(14, 15));// 是否为主持人 0 不为主持人 1 为主持人

                        attendeeForMcuZj.resetUpdateMap();
                        boolean changed = false;
                        if (a_rx == 1) {
                            if (AttendeeMixingStatus.NO.getValue() == attendeeForMcuZj.getMixingStatus()) {
                                attendeeForMcuZj.setMixingStatus(AttendeeMixingStatus.YES.getValue());
                                McuZjMuteStatusCheckTask muteStatusCheckTask = new McuZjMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(DelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                            }
                        } else {
                            if (AttendeeMixingStatus.YES.getValue() == attendeeForMcuZj.getMixingStatus()) {
                                attendeeForMcuZj.setMixingStatus(AttendeeMixingStatus.NO.getValue());
                                McuZjMuteStatusCheckTask muteStatusCheckTask = new McuZjMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                                BeanFactory.getBean(DelayTaskService.class).addTask(muteStatusCheckTask);
                                changed = true;
                            }
                        }
                        if (v_rx == 1) {
                            if (AttendeeVideoStatus.NO.getValue() == attendeeForMcuZj.getVideoStatus()) {
                                attendeeForMcuZj.setVideoStatus(AttendeeVideoStatus.YES.getValue());
                                changed = true;
                            }
                        } else {
                            if (AttendeeVideoStatus.YES.getValue() == attendeeForMcuZj.getVideoStatus()) {
                                attendeeForMcuZj.setVideoStatus(AttendeeVideoStatus.NO.getValue());
                                changed = true;
                            }
                        }
                        if (talking == 1) {
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeForMcuZj.getName() + "】正在发言！");
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put(attendeeForMcuZj.getId(), true);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_SPEAKER, updateMap);
                        }
                        String presentAttendId = conferenceContext.getPresentAttendeeId();
                        if (presentation == 1) {
                            if (YesOrNo.NO.getValue() == attendeeForMcuZj.getPresentStatus()) {
                                attendeeForMcuZj.setPresentStatus(YesOrNo.YES.getValue());
                                changed = true;
                                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeForMcuZj.getName() + "】正在共享屏幕！");
                            }
                            conferenceContext.setPresentAttendeeId(attendeeForMcuZj.getId());
                        } else {
                            if (YesOrNo.YES.getValue() == attendeeForMcuZj.getPresentStatus()) {
                                attendeeForMcuZj.setPresentStatus(YesOrNo.NO.getValue());
                                changed = true;
                                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "【" + attendeeForMcuZj.getName() + "】关闭共享屏幕！");
                            }
                            if (attendeeForMcuZj.getId().equals(presentAttendId)) {
                                conferenceContext.setPresentAttendeeId(null);
                            }
                        }
//                        if (conferenceContext.hasPresent()) {
//                            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
//                            if (attendeeOperation instanceof DefaultAttendeeOperation) {
//                                conferenceContext.setLastAttendeeOperation(attendeeOperation);
//                                PresentAttendeeOperation presentAttendeeOperation = new PresentAttendeeOperation(conferenceContext);
//                                conferenceContext.setAttendeeOperation(presentAttendeeOperation);
//                            }
//                        } else {
//                            AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
//                            if (attendeeOperation instanceof PresentAttendeeOperation) {
//                                attendeeOperation.cancel();
//                                conferenceContext.setAttendeeOperation(conferenceContext.getLastAttendeeOperation());
//                            }
//                        }
                        if (speaker == 1) {
                            AttendeeForMcuZj oldMasterAttendee = conferenceContext.getMasterAttendee();
                            if (oldMasterAttendee == null || !usrId.equals(oldMasterAttendee.getEpUserId())) {
                                conferenceContext.setMasterAttendee(attendeeForMcuZj);
                                StringBuilder messageTip = new StringBuilder();
                                messageTip.append("主会场已切换至【").append(attendeeForMcuZj.getName()).append("】");
                                Map<String, Object> data = new HashMap<>();
                                data.put("oldMasterAttendee", oldMasterAttendee);
                                data.put("newMasterAttendee", attendeeForMcuZj);
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
                            }
                        }
                        if (changed) {
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
//                            // 更新数据库
//                            processUpdateParticipant(conferenceContext, attendeeForMcuZj);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private void processEpsLeftResponse(CcEpsLeftResponse ccEpsLeftResponse, McuZjConferenceContext conferenceContext) {
        if (ccEpsLeftResponse != null && ccEpsLeftResponse.getUsr_ids().length > 0) {
            for (int i = 0; i < ccEpsLeftResponse.getUsr_ids().length; i++) {
                String userId = ccEpsLeftResponse.getUsr_ids()[i];
                if (conferenceContext.getStreamingAttendee() != null) {
                    AttendeeForMcuZj attendeeForMcuZjStreaming = conferenceContext.getStreamingAttendee();
                    if (userId.equals(attendeeForMcuZjStreaming.getEpUserId())) {
                        attendeeForMcuZjStreaming.leaveMeeting();
                        if (conferenceContext.isStreaming()) {
                            conferenceContext.setStreaming(false);
                            // 向所有客户端通知会议的录制状态
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, false);
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");

                            BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);

                            AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeByEpUserId(userId);
                            if (attendeeForMcuZj != null) {
                                conferenceContext.removeAttendeeById(attendeeForMcuZj.getId());
                                updateOperation(conferenceContext);
                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZj.getId())) {
                                    clearMasterAttendee(conferenceContext);
                                }

                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("id", attendeeForMcuZj.getId());
                                updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);

                                String reason = "【" + attendeeForMcuZj.getName() + "】离会";
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZj);
                                // 更新数据库
                                processUpdateParticipant(conferenceContext, attendeeForMcuZj);
                            }

                            continue;
                        }
                    }
                }
                AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeByEpUserId(userId);
                if (attendeeForMcuZj != null) {
                    if (attendeeForMcuZj.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                        McuZjMuteStatusCheckTask muteStatusCheckTask = new McuZjMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                        BeanFactory.getBean(DelayTaskService.class).addTask(muteStatusCheckTask);
                    }
                    attendeeForMcuZj.resetUpdateMap();
                    attendeeForMcuZj.leaveMeeting();
                    if (attendeeForMcuZj instanceof McuAttendeeForMcuZj) {
                        McuAttendeeForMcuZj mcuAttendeeForMcuZj = (McuAttendeeForMcuZj) attendeeForMcuZj;
                        String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZj.getId());
                        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                        if (baseConferenceContext != null) {
                            attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                        } else {
                            attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);

                        if (baseConferenceContext == null) {
                            ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                            ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                            viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuZj.getCascadeTemplateId());
                            viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuZj.getCascadeMcuType());
                            viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                            viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                            List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                            if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                                conferenceContext.removeMcuAttendee(mcuAttendeeForMcuZj);
                                updateOperation(conferenceContext);
                                if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuZj.getId())) {
                                    clearMasterAttendee(conferenceContext);
                                }
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("id", mcuAttendeeForMcuZj.getId());
                                updateMap.put("deptId", mcuAttendeeForMcuZj.getDeptId());
                                updateMap.put("mcuAttendee", mcuAttendeeForMcuZj.isMcuAttendee());
                                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                                jsonObject = new JSONObject();
                                jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                                McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                            }
                        }
                    } else if (attendeeForMcuZj instanceof TerminalAttendeeForMcuZj) {
                        TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendeeForMcuZj;
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuZj.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    } else if (attendeeForMcuZj instanceof InvitedAttendeeForMcuZj) {
                        InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendeeForMcuZj;
                        if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                            BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                            if (busiTerminal != null) {
                                attendeeForMcuZj.setOnlineStatus(busiTerminal.getOnlineStatus());
                            } else {
                                attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                            }
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        } else {
                            conferenceContext.removeAttendeeById(attendeeForMcuZj.getId());
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZj.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }

                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", attendeeForMcuZj.getId());
                            updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                            updateMap.put("mcuAttendee", attendeeForMcuZj.isMcuAttendee());
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        }
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuZj.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZj.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuZj.getId());
                        updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuZj.isMcuAttendee());
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    }
                    String reason = "【" + attendeeForMcuZj.getName() + "】离会";
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                    BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZj);
                    // 更新数据库
                    processUpdateParticipant(conferenceContext, attendeeForMcuZj);
                }
            }
        }
    }

    private void processLeftCallResponse(CcLeftCallResponse ccLeftCallResponse, McuZjConferenceContext conferenceContext) {
        if (ccLeftCallResponse != null && ccLeftCallResponse.getCmdid() != null) {
            Integer epId = ccLeftCallResponse.getEp_id();
            if (conferenceContext.getStreamingAttendee() != null) {
                AttendeeForMcuZj attendeeForMcuZjStreaming = conferenceContext.getStreamingAttendee();
                if (attendeeForMcuZjStreaming.getEpId() != null && epId == attendeeForMcuZjStreaming.getEpId()) {
                    attendeeForMcuZjStreaming.leaveMeeting();
                    if (conferenceContext.isStreaming()) {
                        conferenceContext.setStreaming(false);
                        // 向所有客户端通知会议的录制状态
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.STREAMING, false);
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, "会议已关闭直播");

                        BeanFactory.getBean(IMqttService.class).sendConferenceInfoToPushTargetTerminal(conferenceContext);

                        AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeByEpId(epId);
                        if (attendeeForMcuZj != null) {
                            conferenceContext.removeAttendeeById(attendeeForMcuZj.getId());
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZj.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }

                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", attendeeForMcuZj.getId());
                            updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);

                            String reason = "【" + attendeeForMcuZj.getName() + "】离会";
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                            BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZj);
                            // 更新数据库
                            processUpdateParticipant(conferenceContext, attendeeForMcuZj);
                        }
                        return;
                    }
                }
            }
            AttendeeForMcuZj attendeeForMcuZj = conferenceContext.getAttendeeByEpId(epId);
            if (attendeeForMcuZj != null) {
                if (attendeeForMcuZj.getMixingStatus() == AttendeeMixingStatus.YES.getValue()) {
                    McuZjMuteStatusCheckTask muteStatusCheckTask = new McuZjMuteStatusCheckTask(conferenceContext.getId(), 1000, conferenceContext);
                    BeanFactory.getBean(DelayTaskService.class).addTask(muteStatusCheckTask);
                }
                attendeeForMcuZj.resetUpdateMap();
                attendeeForMcuZj.leaveMeeting();
                if (attendeeForMcuZj instanceof McuAttendeeForMcuZj) {
                    McuAttendeeForMcuZj mcuAttendeeForMcuZj = (McuAttendeeForMcuZj) attendeeForMcuZj;
                    String contextKey = EncryptIdUtil.parasToContextKey(mcuAttendeeForMcuZj.getId());
                    BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
                    if (baseConferenceContext != null) {
                        attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.ONLINE.getValue());
                    } else {
                        attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());

                    if (baseConferenceContext == null) {
                        ViewTemplateConferenceMapper viewTemplateConferenceMapper = BeanFactory.getBean(ViewTemplateConferenceMapper.class);
                        ViewTemplateConference viewTemplateConferenceConCascade = new ViewTemplateConference();
                        viewTemplateConferenceConCascade.setId(mcuAttendeeForMcuZj.getCascadeTemplateId());
                        viewTemplateConferenceConCascade.setMcuType(mcuAttendeeForMcuZj.getCascadeMcuType());
                        viewTemplateConferenceConCascade.setUpCascadeId(conferenceContext.getTemplateConferenceId());
                        viewTemplateConferenceConCascade.setUpCascadeMcuType(conferenceContext.getMcuType());
                        List<ViewTemplateConference> viewTemplateConferenceList = viewTemplateConferenceMapper.selectAllViewTemplateConferenceList(viewTemplateConferenceConCascade);
                        if (viewTemplateConferenceList == null || viewTemplateConferenceList.size() == 0) {
                            conferenceContext.removeMcuAttendee(mcuAttendeeForMcuZj);
                            updateOperation(conferenceContext);
                            if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(mcuAttendeeForMcuZj.getId())) {
                                clearMasterAttendee(conferenceContext);
                            }
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("id", mcuAttendeeForMcuZj.getId());
                            updateMap.put("deptId", mcuAttendeeForMcuZj.getDeptId());
                            updateMap.put("mcuAttendee", mcuAttendeeForMcuZj.isMcuAttendee());
                            McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                            McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                        }
                    }
                } else if (attendeeForMcuZj instanceof TerminalAttendeeForMcuZj) {
                    TerminalAttendeeForMcuZj terminalAttendeeForMcuZj = (TerminalAttendeeForMcuZj) attendeeForMcuZj;
                    BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendeeForMcuZj.getTerminalId());
                    if (busiTerminal != null) {
                        attendeeForMcuZj.setOnlineStatus(busiTerminal.getOnlineStatus());
                    } else {
                        attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                    }
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                } else if (attendeeForMcuZj instanceof InvitedAttendeeForMcuZj) {
                    InvitedAttendeeForMcuZj invitedAttendeeForMcuZj = (InvitedAttendeeForMcuZj) attendeeForMcuZj;
                    if (invitedAttendeeForMcuZj.getTerminalId() != null) {
                        BusiTerminal busiTerminal = TerminalCache.getInstance().get(invitedAttendeeForMcuZj.getTerminalId());
                        if (busiTerminal != null) {
                            attendeeForMcuZj.setOnlineStatus(busiTerminal.getOnlineStatus());
                        } else {
                            attendeeForMcuZj.setOnlineStatus(TerminalOnlineStatus.OFFLINE.getValue());
                        }
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_UPDATE, attendeeForMcuZj.getUpdateMap());
                    } else {
                        conferenceContext.removeAttendeeById(attendeeForMcuZj.getId());
                        updateOperation(conferenceContext);
                        if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZj.getId())) {
                            clearMasterAttendee(conferenceContext);
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("id", attendeeForMcuZj.getId());
                        updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                        updateMap.put("mcuAttendee", attendeeForMcuZj.isMcuAttendee());
                        McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                        McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                    }
                } else {
                    conferenceContext.removeAttendeeById(attendeeForMcuZj.getId());
                    updateOperation(conferenceContext);
                    if (conferenceContext.getMasterAttendee() != null && conferenceContext.getMasterAttendee().getId().equals(attendeeForMcuZj.getId())) {
                        clearMasterAttendee(conferenceContext);
                    }

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("id", attendeeForMcuZj.getId());
                    updateMap.put("deptId", attendeeForMcuZj.getDeptId());
                    updateMap.put("mcuAttendee", attendeeForMcuZj.isMcuAttendee());
                    McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.ATTENDEE_DELETE, updateMap);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("attendeeCountingStatistics",new AttendeeCountingStatistics(conferenceContext));
                    McuZjWebSocketMessagePusher.getInstance().pushSpecificConferenceMessage(conferenceContext,WebsocketMessageType.CONFERENCE_CHANGE,jsonObject);
                }
                String reason = "【" + attendeeForMcuZj.getName() + "】离会";
                String reasonCode = ccLeftCallResponse.getReason();
                CallLegEndReasonEnum callLegEndReasonEnum = convertLeftReason(reasonCode);
                attendeeForMcuZj.setCallLegEndReasonEnum(callLegEndReasonEnum);
                reason += ("：" + callLegEndReasonEnum.getDisplayName());
                McuZjWebSocketMessagePusher.getInstance().upwardPushConferenceMessage(conferenceContext, WebsocketMessageType.MESSAGE_TIP, reason);

                BeanFactory.getBean(IMqttService.class).sendLeftConferenceToPushTargetTerminal(conferenceContext, attendeeForMcuZj);
                // 更新数据库
                processUpdateParticipant(conferenceContext, attendeeForMcuZj);
            }
        }
    }

    private CallLegEndReasonEnum convertLeftReason(String reasonCode) {
        if ("user hangup".equals(reasonCode) || "android normal quit".equals(reasonCode) || "Remote disconnect".equals(reasonCode) || "Call disconnected".equals(reasonCode) || "3/user hungup".equals(reasonCode) || "user hungup".equals(reasonCode)
            || "H323_REASON_UNKNOWNERROR".equals(reasonCode) || "H323_REASON_RESETBYPEER".equals(reasonCode) || "H323_REASON_DROPBYPEER".equals(reasonCode)) {
            return CallLegEndReasonEnum.LOCAL_TEARDOWN;
        } else {
            return CallLegEndReasonEnum.REMOTE_TEARDOWN;
        }
    }

    private void processUpdateParticipant(McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee) {
        processUpdateParticipant(conferenceContext, attendee, false);
    }

    private void processUpdateParticipant(McuZjConferenceContext conferenceContext, AttendeeForMcuZj attendee, boolean updateMediaInfo) {
        IBusiHistoryConferenceForMcuZjService busiHistoryConferenceForMcuZjService = BeanFactory.getBean(IBusiHistoryConferenceForMcuZjService.class);
        busiHistoryConferenceForMcuZjService.updateBusiHistoryParticipant(conferenceContext, attendee, updateMediaInfo);
    }

    private void clearMasterAttendee(McuZjConferenceContext conferenceContext) {
        AttendeeForMcuZj oldMasterAttendee = conferenceContext.getMasterAttendee();
        Map<String, Object> data = new HashMap<>();
        data.put("oldMasterAttendee", oldMasterAttendee);
        data.put("newMasterAttendee", null);
        McuZjWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MASTER_CHANGED, data);

        StringBuilder messageTip = new StringBuilder();
        messageTip.append("主会场已离会【").append(oldMasterAttendee.getName()).append("】");
        McuZjWebSocketMessagePusher.getInstance().pushConferenceMessageToAll(conferenceContext, WebsocketMessageType.MESSAGE_TIP, messageTip);
        CcUpdateMrEpsStatusRequest ccUpdateMrEpsStatusRequest = new CcUpdateMrEpsStatusRequest();
        ccUpdateMrEpsStatusRequest.setAction(CcUpdateMrEpsStatusRequest.ACTION_speaker);
        String[] strings = new String[1];
        strings[0] = "";
        ccUpdateMrEpsStatusRequest.setUsr_ids(strings);
        ccUpdateMrEpsStatusRequest.setValue(1);
        boolean success = conferenceContext.getConferenceControlApi().updateMrEpsStatus(ccUpdateMrEpsStatusRequest);
        if (success) {
            conferenceContext.clearMasterAttendee();
        }
    }

    private void updateOperation(McuZjConferenceContext conferenceContext) {
        AttendeeOperation attendeeOperation = conferenceContext.getAttendeeOperation();
        if (attendeeOperation instanceof DefaultAttendeeOperation) {
            attendeeOperation.setForceUpdateView(true);
        }
    }

}
