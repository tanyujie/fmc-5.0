package com.paradisecloud.fcm.mcu.kdc.monitor.cm;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuKdcBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuKdcBusiStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuKdcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuKdcTemplateConference;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcConferenceContextCache;
import com.paradisecloud.fcm.mcu.kdc.cache.McuKdcBridgeCache;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.request.cm.*;
import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cm.*;
import com.paradisecloud.fcm.mcu.kdc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IBusiMcuKdcConferenceService;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcCheckAttendeeOnlineStatusTask;
import com.paradisecloud.fcm.mcu.kdc.task.McuKdcDelayTaskService;
import com.sinhy.spring.BeanFactory;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;

import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CmGetChangesThread extends Thread {

    /**
     * key:mcuId
     */
    private Map<Long, Thread> serverThreadMap = new ConcurrentHashMap<>();
    private Map<Long, Thread> serverOtherThreadMap = new ConcurrentHashMap<>();
    private Map<Long, Thread> meetingThreadMap = new ConcurrentHashMap<>();

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }

            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setCmGetChangesTime(currentTimeMillis);
                for (McuKdcBridge mcuKdcBridge : McuKdcBridgeCache.getInstance().getMcuKdcBridges()) {
                    if (!serverThreadMap.containsKey(mcuKdcBridge.getBusiMcuKdc().getId())) {
                        startServerThread(mcuKdcBridge);
                        startServerOtherThread(mcuKdcBridge);
                        startMeetingThread(mcuKdcBridge);
                    }
                }

                for (Long id : serverThreadMap.keySet()) {
                    Thread thread = serverThreadMap.get(id);
                    if (thread == null || !thread.isAlive() || thread.isInterrupted()) {
                        serverThreadMap.remove(id);
                        try {
                            serverOtherThreadMap.get(id).interrupt();
                        } catch (Exception e) {
                        }
                        serverOtherThreadMap.remove(id);
                        try {
                            meetingThreadMap.get(id).interrupt();
                        } catch (Exception e) {
                        }
                        meetingThreadMap.remove(id);
                        McuKdcBridge mcuKdcBridge = McuKdcBridgeCache.getInstance().get(id);
                        if (mcuKdcBridge != null) {
                            mcuKdcBridge.cleanLoginInfo();
                        }

                    }
                    if (!McuKdcBridgeCache.getInstance().containsKey(id)) {
                        try {
                            serverThreadMap.get(id).interrupt();
                        } catch (Exception e) {
                        }
                        serverThreadMap.remove(id);
                        try {
                            serverOtherThreadMap.get(id).interrupt();
                        } catch (Exception e) {
                        }
                        serverOtherThreadMap.remove(id);
                        try {
                            meetingThreadMap.get(id).interrupt();
                        } catch (Exception e) {
                        }
                        meetingThreadMap.remove(id);
                    }
                }

                McuKdcCheckAttendeeOnlineStatusTask mcuKdcCheckAttendeeOnlineStatusTask = new McuKdcCheckAttendeeOnlineStatusTask(McuType.MCU_KDC.getCode(), 0);
                McuKdcDelayTaskService mcuKdcDelayTaskService = BeanFactory.getBean(McuKdcDelayTaskService.class);
                mcuKdcDelayTaskService.addTask(mcuKdcCheckAttendeeOnlineStatusTask);
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

    private void startServerThread(McuKdcBridge mcuKdcBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    long currentTimeMillis = System.currentTimeMillis();
                    if (mcuKdcBridge.getLastUpdateTime() == 0 || StringUtils.isEmpty(mcuKdcBridge.getCookie()) || currentTimeMillis - mcuKdcBridge.getLastUpdateTime() > 10000) {
                        // 未登录时先登录
                        mcuKdcBridge.setConnectionFailedReason(null);
                        mcuKdcBridge.setDataInitialized(false);
                        mcuKdcBridge.setBridgeStatus(McuKdcBridgeStatus.INITIALIZING);
                        mcuKdcBridge.setDiffTime(0);
                        mcuKdcBridge.setBayeuxClient(null);
                        // 获取TOKEN
                        CmTokenRequest cmTokenRequest = new CmTokenRequest();
                        cmTokenRequest.setOauth_consumer_key(mcuKdcBridge.getBusiMcuKdc().getDevKey());
                        cmTokenRequest.setOauth_consumer_secret(mcuKdcBridge.getBusiMcuKdc().getDevValue());
                        CmTokenResponse cmTokenResponse = mcuKdcBridge.getConferenceManageApi().token(cmTokenRequest);
                        if (cmTokenResponse != null && cmTokenResponse.isSuccess()) {
                            mcuKdcBridge.setToken(cmTokenResponse.getAccount_token());
                        } else {
                            mcuKdcBridge.setToken("");
                            mcuKdcBridge.setConnectionFailedReason("获取TOKEN失败。");
                            mcuKdcBridge.getMcuKdcLogger().logInfo("=============> MCU获取TOKEN失败。" );
                            return;
                        }

                        // 登录
                        CmLoginRequest cmLoginRequest = new CmLoginRequest();
                        cmLoginRequest.setUsername(mcuKdcBridge.getBusiMcuKdc().getUsername());
                        cmLoginRequest.setPassword(mcuKdcBridge.getBusiMcuKdc().getPassword());
                        CmLoginResponse cmLoginResponse = mcuKdcBridge.getConferenceManageApi().login(cmLoginRequest);
                        if (cmLoginResponse != null && cmLoginResponse.isSuccess()) {
                            mcuKdcBridge.setCookie(cmLoginResponse.getCookie());
                            mcuKdcBridge.setUserDomainMoid(cmLoginResponse.getUserdomainmoid());
                            mcuKdcBridge.setLastUpdateTime(currentTimeMillis);
                            mcuKdcBridge.setBridgeStatus(McuKdcBridgeStatus.AVAILABLE);
                            mcuKdcBridge.getBusiMcuKdc().setStatus(McuKdcBusiStatus.ONLINE.getValue());
                            mcuKdcBridge.getMcuKdcLogger().logInfo("=============> MCU登录成功。cookie：" + mcuKdcBridge.getCookie());
                            System.err.println("============> 111 domainId:" + cmLoginResponse.getUserdomainmoid());
                        } else {
                            mcuKdcBridge.setCookie("");
                            mcuKdcBridge.setUserDomainMoid("");
                            mcuKdcBridge.setBridgeStatus(McuKdcBridgeStatus.NOT_AVAILABLE);
                            mcuKdcBridge.setConnectionFailedReason("无法登录。");
                            mcuKdcBridge.getBusiMcuKdc().setStatus(McuKdcBusiStatus.OFFLINE.getValue());
                            mcuKdcBridge.getMcuKdcLogger().logInfo("=============> MCU登录失败。" );
                            return;
                        }
                        // 推送
                        checkPush(mcuKdcBridge);
                        // 初始化会议
                        if (McuKdcConferenceContextCache.getInstance().values().size() == 0) {
                            BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuKdcTemplateConferenceMapper.class);
                            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                            List<BusiMcuKdcTemplateConference> busiMcuKdcTemplateConferenceList = busiMcuKdcTemplateConferenceMapper.selectBusiMcuKdcTemplateConferenceList(new BusiMcuKdcTemplateConference());
                            for (BusiMcuKdcTemplateConference busiMcuKdcTemplateConference : busiMcuKdcTemplateConferenceList) {
                                try {
                                    McuKdcBridge mcuKdcBridgeTemp = null;
                                    try {
                                        mcuKdcBridgeTemp = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(busiMcuKdcTemplateConference.getDeptId()).getMasterMcuKdcBridge();
                                    } catch (Exception e) {
                                    }
                                    if (mcuKdcBridgeTemp != null && mcuKdcBridgeTemp.getBusiMcuKdc().getId().longValue() == mcuKdcBridge.getBusiMcuKdc().getId().longValue()) {
                                        McuKdcConferenceContext mcuKdcConferenceContext = McuKdcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuKdcTemplateConference.getId(), McuType.MCU_KDC.getCode()));
                                        if (mcuKdcConferenceContext == null) {
                                            mcuKdcConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuKdcTemplateConference.getId());
                                            if (busiMcuKdcTemplateConference.getLastConferenceId() != null) {
                                                Long historyConferenceId = busiMcuKdcTemplateConference.getLastConferenceId();
                                                BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
                                                if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
                                                    mcuKdcConferenceContext.setAttendeeOperation(mcuKdcConferenceContext.getDefaultViewOperation());
                                                    mcuKdcConferenceContext.setStart(true);
                                                    mcuKdcConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                                    mcuKdcConferenceContext.setHistoryConference(busiHistoryConference);
                                                    McuKdcConferenceContextCache.getInstance().add(mcuKdcConferenceContext);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                        mcuKdcBridge.setDataInitialized(true);
                    }
                    if (StringUtils.isNotEmpty(mcuKdcBridge.getCookie())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (StringUtils.isEmpty(mcuKdcBridge.getCookie())) {
                                break;
                            }
                            currentTimeMillis = System.currentTimeMillis();
                            if (mcuKdcBridge.getLastUpdateTime() == 0 || currentTimeMillis - mcuKdcBridge.getLastUpdateTime() > 60000) {
                                CmHeartbeatRequest cmHeartbeatRequest = new CmHeartbeatRequest();
                                CmHeartbeatResponse cmHeartbeatResponse = mcuKdcBridge.getConferenceManageApi().heartbeat(cmHeartbeatRequest);
                                if (cmHeartbeatResponse != null && cmHeartbeatResponse.isSuccess()) {
                                    mcuKdcBridge.setLastUpdateTime(currentTimeMillis);
                                } else {
                                    mcuKdcBridge.getMcuKdcLogger().logInfo("=============> MCU登退出登录。");
                                    mcuKdcBridge.setLastUpdateTime(0);
                                    break;
                                }
                                CmHeartbeatTokenRequest cmHeartbeatTokenRequest = new CmHeartbeatTokenRequest();
                                CmHeartbeatTokenResponse cmHeartbeatTokenResponse = mcuKdcBridge.getConferenceManageApi().heartbeatToken(cmHeartbeatTokenRequest);
                                if (cmHeartbeatTokenResponse != null && cmHeartbeatTokenResponse.isSuccess()) {
                                    mcuKdcBridge.setLastUpdateTime(currentTimeMillis);
                                } else {
                                    mcuKdcBridge.getMcuKdcLogger().logInfo("=============> MCU登退出登录。");
                                    mcuKdcBridge.setLastUpdateTime(0);
                                    break;
                                }
                            }

                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }
            }
        });
        thread.start();
        serverThreadMap.put(mcuKdcBridge.getBusiMcuKdc().getId(), thread);
    }

    private void startServerOtherThread(McuKdcBridge mcuKdcBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuKdcBridge.isAvailable()) {
//                        //获取资源占用信息
//                        CmQuerySysResourceStatisticsRequest cmQuerySysResourceStatisticsRequest = new CmQuerySysResourceStatisticsRequest();
//                        CmQuerySysResourceStatisticsResponse cmQuerySysResourceStatisticsResponse = mcuKdcBridge.getConferenceManageApi().querySysResourceStatistics(cmQuerySysResourceStatisticsRequest);
//                        if (cmQuerySysResourceStatisticsResponse != null && cmQuerySysResourceStatisticsResponse.isSuccess()) {
//                            int total = 0;
//                            int used = 0;
//                            if (cmQuerySysResourceStatisticsResponse.getResources() != null) {
//                                for (CmQuerySysResourceStatisticsResponse.Resource resource : cmQuerySysResourceStatisticsResponse.getResources()) {
//                                    total += resource.getTotal();
//                                    used += resource.getUsed();
//                                }
//                            }
//                            mcuKdcBridge.setSystemResourceCount(total);
//                            mcuKdcBridge.setUsedResourceCount(used);
//                        }
                        // 获取媒体资源信息
                        CmGetMeetingResourceRequest cmGetMeetingResourceRequest = new CmGetMeetingResourceRequest();
                        CmGetMeetingResourceResponse cmGetMeetingResourceResponse = mcuKdcBridge.getConferenceManageApi().getMeetingResource(cmGetMeetingResourceRequest);
                        if (cmGetMeetingResourceResponse != null && cmGetMeetingResourceResponse.isSuccess()) {
                            int total = 0;
                            int used = 0;
                            if (cmGetMeetingResourceResponse.getData() != null) {
                                if (cmGetMeetingResourceResponse.getData().getTRA264Usable() != null && cmGetMeetingResourceResponse.getData().getTRAStarted() != null) {
                                    total = cmGetMeetingResourceResponse.getData().getTRA264Usable() + cmGetMeetingResourceResponse.getData().getTRAStarted();
                                    used = cmGetMeetingResourceResponse.getData().getTRAStarted();
                                }
                            }
                            mcuKdcBridge.setSystemResourceCount(total);
                            mcuKdcBridge.setUsedResourceCount(used);
                        }
                        // 推送
                        checkPush(mcuKdcBridge);
                    }

                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        serverOtherThreadMap.put(mcuKdcBridge.getBusiMcuKdc().getId(), thread);
    }

    private void startMeetingThread(McuKdcBridge mcuKdcBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuKdcBridge.isDataInitialized()) {
                        CmSearchMrRequest searchMrRequest = new CmSearchMrRequest();
                        CmSearchMrResponse searchMrResponse = mcuKdcBridge.getConferenceManageApi().searchMr(searchMrRequest);
                        if (searchMrResponse != null && searchMrResponse.isSuccess()) {
                            mcuKdcBridge.setLastUpdateTime(System.currentTimeMillis());
                            if (searchMrResponse.getConfs() != null) {
                                processSearchMrResponse(searchMrResponse.getConfs(), mcuKdcBridge);
                            }
                        }
                    }

                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        meetingThreadMap.put(mcuKdcBridge.getBusiMcuKdc().getId(), thread);
    }

    private void processSearchMrResponse(List<CmSearchMrResponse.ConfInfo> confInfoList, McuKdcBridge mcuKdcBridge) {
        if (confInfoList != null) {
            BusiMcuKdcTemplateConferenceMapper busiMcuKdcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuKdcTemplateConferenceMapper.class);
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            IBusiMcuKdcConferenceService busiMcuKdcConferenceService = BeanFactory.getBean(IBusiMcuKdcConferenceService.class);
            HashSet<String> conferenceStarted = new HashSet<>();
            for (CmSearchMrResponse.ConfInfo confInfo : confInfoList) {
                String confId = confInfo.getConf_id();
                McuKdcConferenceContext conferenceContext = McuKdcConferenceContextCache.getInstance().get(confId);

                if (conferenceContext != null) {
                    conferenceStarted.add(confId);
                } else {
                    try {
                        BusiMcuKdcTemplateConference busiMcuKdcTemplateConferenceCon = new BusiMcuKdcTemplateConference();
                        busiMcuKdcTemplateConferenceCon.setConferenceNumber(Long.valueOf(confId));
                        List<BusiMcuKdcTemplateConference> busiMcuKdcTemplateConferenceList = busiMcuKdcTemplateConferenceMapper.selectAllBusiMcuKdcTemplateConferenceList(busiMcuKdcTemplateConferenceCon);
                        if (busiMcuKdcTemplateConferenceList.size() > 0) {
                            BusiMcuKdcTemplateConference busiMcuKdcTemplateConference = busiMcuKdcTemplateConferenceList.get(0);
                            if (busiMcuKdcTemplateConference.getLastConferenceId() != null) {
                                try {
                                    BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(busiMcuKdcTemplateConference.getLastConferenceId());
                                    if (busiHistoryConference != null) {
                                        if (busiHistoryConference.getConferenceEndTime() != null) {
                                            busiHistoryConference.setConferenceEndTime(null);
                                        }
                                        conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuKdcTemplateConference.getId());
                                        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                                        conferenceContext.setStart(true);
                                        conferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                        conferenceContext.setHistoryConference(busiHistoryConference);
                                        McuKdcConferenceContextCache.getInstance().add(conferenceContext);
                                        conferenceStarted.add(confId);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        mcuKdcBridge.getMcuKdcLogger().logInfo("添加会议上下文失败", false, e);
                    }
                }

                conferenceContext = McuKdcConferenceContextCache.getInstance().get(confId);
                if (conferenceContext == null) {
                    CmStopMrRequest cmStopMrRequest = new CmStopMrRequest();
                    cmStopMrRequest.setConf_id(confId);
                    CmStopMrResponse cmStopMrResponse = mcuKdcBridge.getConferenceManageApi().stopMr(cmStopMrRequest);
                    if (cmStopMrResponse != null && cmStopMrResponse.isSuccess()) {

                    }
                }
            }

            for (McuKdcConferenceContext mcuKdcConferenceContext : McuKdcConferenceContextCache.getInstance().values()) {
                if (!conferenceStarted.contains(mcuKdcConferenceContext.getConferenceNumber())) {
                    McuKdcBridge mcuKdcBridgeTemp = McuKdcBridgeCache.getInstance().getAvailableMcuKdcBridgesByDept(mcuKdcConferenceContext.getDeptId()).getMasterMcuKdcBridge();
                    if (mcuKdcBridgeTemp != null && mcuKdcBridgeTemp.getBusiMcuKdc().getId().longValue() == mcuKdcBridge.getBusiMcuKdc().getId().longValue()) {
                        if (!mcuKdcConferenceContext.isEnd()) {
                            if (System.currentTimeMillis() - mcuKdcConferenceContext.getStartTime().getTime() > 3000) {
                                mcuKdcConferenceContext.setEnd(true);
                                mcuKdcConferenceContext.setEndTime(new Date());
                                busiMcuKdcConferenceService.endConference(mcuKdcConferenceContext.getConferenceNumber(), ConferenceEndType.COMMON.getValue(), false, true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkPush(McuKdcBridge mcuKdcBridge) {
        if (mcuKdcBridge.getBayeuxClient() != null) {
            if (mcuKdcBridge.getBayeuxClient().isDisconnected()) {
                mcuKdcBridge.setBayeuxClient(null);
            }
        }
        if (mcuKdcBridge.getBayeuxClient() == null) {
            try {
                HttpClient httpClient = new HttpClient();
                httpClient.start();

                Map<String, Object> options = new HashMap<>();
                ClientTransport transport = new LongPollingTransport(options, httpClient);
                BayeuxClient bayeuxClient = new BayeuxClient(mcuKdcBridge.getBaseUrl() + "/api/v1/publish", transport);

                String[] cookieNamePair = mcuKdcBridge.getCookie().split("=");
                HttpCookie cookie = new HttpCookie(cookieNamePair[0], cookieNamePair[1]);
                bayeuxClient.putCookie(cookie);
                bayeuxClient.handshake(hsListener);
                boolean handshaken = bayeuxClient.waitFor(5000, BayeuxClient.State.CONNECTED);
                if (handshaken) {
                    mcuKdcBridge.setBayeuxClient(bayeuxClient);
                }
            } catch (Exception e) {
            }
        }
    }

    private org.cometd.bayeux.client.ClientSession.MessageListener hsListener = new ClientSession.MessageListener() {
        @Override
        public void onMessage(Message message) {
            if (message.isSuccessful()) {
                Map<String, Object> map = message.getExt();
                String domainId = map.get("user_domain_moid").toString();
                System.err.println("============> domainId:" + domainId);
            }
        }
    };
}

