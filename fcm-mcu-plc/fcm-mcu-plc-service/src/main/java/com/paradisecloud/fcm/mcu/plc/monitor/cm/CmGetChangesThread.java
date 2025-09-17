package com.paradisecloud.fcm.mcu.plc.monitor.cm;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuPlcBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuPlcBusiStatus;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuPlcTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuPlcTemplateConference;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcConferenceContextCache;
import com.paradisecloud.fcm.mcu.plc.cache.McuPlcBridgeCache;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.request.cm.*;
import com.paradisecloud.fcm.mcu.plc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.plc.model.response.cm.*;
import com.paradisecloud.fcm.mcu.plc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.plc.service.interfaces.IBusiMcuPlcConferenceService;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcCheckAttendeeOnlineStatusTask;
import com.paradisecloud.fcm.mcu.plc.task.McuPlcDelayTaskService;
import com.sinhy.spring.BeanFactory;

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
                for (McuPlcBridge mcuPlcBridge : McuPlcBridgeCache.getInstance().getMcuPlcBridges()) {
                    if (!serverThreadMap.containsKey(mcuPlcBridge.getBusiMcuPlc().getId())) {
                        startServerThread(mcuPlcBridge);
                        startServerOtherThread(mcuPlcBridge);
                        startMeetingThread(mcuPlcBridge);
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
                        McuPlcBridge mcuPlcBridge = McuPlcBridgeCache.getInstance().get(id);
                        if (mcuPlcBridge != null) {
                            mcuPlcBridge.cleanLoginInfo();
                        }
                    }
                    if (!McuPlcBridgeCache.getInstance().containsKey(id)) {
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

                McuPlcCheckAttendeeOnlineStatusTask mcuPlcCheckAttendeeOnlineStatusTask = new McuPlcCheckAttendeeOnlineStatusTask(McuType.MCU_PLC.getCode(), 0);
                McuPlcDelayTaskService mcuPlcDelayTaskService = BeanFactory.getBean(McuPlcDelayTaskService.class);
                mcuPlcDelayTaskService.addTask(mcuPlcCheckAttendeeOnlineStatusTask);
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

    private void startServerThread(McuPlcBridge mcuPlcBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    long currentTimeMillis = System.currentTimeMillis();
                    if (mcuPlcBridge.getLastUpdateTime() == 0 || StringUtils.isEmpty(mcuPlcBridge.getMcuToken()) || currentTimeMillis - mcuPlcBridge.getLastUpdateTime() > 10000) {
                        // 未登录时先登录
                        mcuPlcBridge.setConnectionFailedReason(null);
                        mcuPlcBridge.setDataInitialized(false);
                        mcuPlcBridge.setBridgeStatus(McuPlcBridgeStatus.INITIALIZING);
                        mcuPlcBridge.setDiffTime(0);
                        // 登录
                        CmLoginRequest cmLoginRequest = new CmLoginRequest();
                        cmLoginRequest.setIp(mcuPlcBridge.getBusiMcuPlc().getProxyHost());
                        cmLoginRequest.setPort(mcuPlcBridge.getBusiMcuPlc().getProxyPort());
                        cmLoginRequest.setUserName(mcuPlcBridge.getBusiMcuPlc().getUsername());
                        cmLoginRequest.setPassword(mcuPlcBridge.getBusiMcuPlc().getPassword());
                        CmLoginResponse cmLoginResponse = mcuPlcBridge.getConferenceManageApi().login(cmLoginRequest);
                        if (cmLoginResponse != null && CommonResponse.STATUS_OK.equals(cmLoginResponse.getStatus())) {
                            mcuPlcBridge.setMcuToken(cmLoginResponse.getMcuToken());
                            mcuPlcBridge.setMcuUserToken(cmLoginResponse.getMcuUserToken());
                            mcuPlcBridge.setLastUpdateTime(currentTimeMillis);
                            mcuPlcBridge.setBridgeStatus(McuPlcBridgeStatus.AVAILABLE);
                            mcuPlcBridge.getBusiMcuPlc().setStatus(McuPlcBusiStatus.ONLINE.getValue());
                            mcuPlcBridge.getMcuPlcLogger().logInfo("=============> MCU登录成功。mcuToken：" + mcuPlcBridge.getMcuToken());
                        } else {
                            mcuPlcBridge.setBridgeStatus(McuPlcBridgeStatus.NOT_AVAILABLE);
                            mcuPlcBridge.setConnectionFailedReason("无法登录。");
                            mcuPlcBridge.getBusiMcuPlc().setStatus(McuPlcBusiStatus.OFFLINE.getValue());
                            mcuPlcBridge.getMcuPlcLogger().logInfo("=============> MCU登录失败。" );
                            return;
                        }
                        // 获取MCU时间
                        CmGetMcuTimeRequest cmGetMcuTimeRequest = new CmGetMcuTimeRequest();
                        CmGetMcuTimeResponse cmGetMcuTimeResponse = mcuPlcBridge.getConferenceManageApi().getMcuTime(cmGetMcuTimeRequest);
                        if (cmGetMcuTimeResponse != null && CommonResponse.STATUS_OK.equals(cmGetMcuTimeResponse.getStatus())) {
                            mcuPlcBridge.setDiffTime(currentTimeMillis - cmGetMcuTimeResponse.getMcuTime().getTime());
                        }
                        // 初始化会议
                        if (McuPlcConferenceContextCache.getInstance().values().size() == 0) {
                            BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
                            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                            List<BusiMcuPlcTemplateConference> busiMcuPlcTemplateConferenceList = busiMcuPlcTemplateConferenceMapper.selectBusiMcuPlcTemplateConferenceList(new BusiMcuPlcTemplateConference());
                            for (BusiMcuPlcTemplateConference busiMcuPlcTemplateConference : busiMcuPlcTemplateConferenceList) {
                                try {
                                    McuPlcBridge mcuPlcBridgeTemp = null;
                                    try {
                                        mcuPlcBridgeTemp = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(busiMcuPlcTemplateConference.getDeptId()).getMasterMcuPlcBridge();
                                    } catch (Exception e) {
                                    }
                                    if (mcuPlcBridgeTemp != null && mcuPlcBridgeTemp.getBusiMcuPlc().getId().longValue() == mcuPlcBridge.getBusiMcuPlc().getId().longValue()) {
                                        McuPlcConferenceContext mcuPlcConferenceContext = McuPlcConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuPlcTemplateConference.getId(), McuType.MCU_PLC.getCode()));
                                        if (mcuPlcConferenceContext == null) {
                                            mcuPlcConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuPlcTemplateConference.getId());
                                            if (busiMcuPlcTemplateConference.getLastConferenceId() != null) {
                                                Long historyConferenceId = busiMcuPlcTemplateConference.getLastConferenceId();
                                                BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
                                                if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
                                                    mcuPlcConferenceContext.setAttendeeOperation(mcuPlcConferenceContext.getDefaultViewOperation());
                                                    mcuPlcConferenceContext.setStart(true);
                                                    mcuPlcConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                                    mcuPlcConferenceContext.setHistoryConference(busiHistoryConference);
                                                    McuPlcConferenceContextCache.getInstance().add(mcuPlcConferenceContext);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                        mcuPlcBridge.setDataInitialized(true);
                    }
                    if (StringUtils.isNotEmpty(mcuPlcBridge.getMcuToken())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (StringUtils.isEmpty(mcuPlcBridge.getMcuToken())) {
                                break;
                            }
                            currentTimeMillis = System.currentTimeMillis();
                            if (mcuPlcBridge.getLastUpdateTime() == 0 || currentTimeMillis - mcuPlcBridge.getLastUpdateTime() > 10000) {
                                CmGetChangesRequest cmGetChangesRequest = new CmGetChangesRequest();
                                CmGetChangesResponse response = mcuPlcBridge.getConferenceManageApi().getChanges(cmGetChangesRequest);
                                if (response != null && CommonResponse.STATUS_OK.equals(response.getStatus())) {
                                    mcuPlcBridge.setLastUpdateTime(currentTimeMillis);
                                } else {
                                    mcuPlcBridge.getMcuPlcLogger().logInfo("=============> MCU登退出登录。");
                                    mcuPlcBridge.setLastUpdateTime(0);
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
        serverThreadMap.put(mcuPlcBridge.getBusiMcuPlc().getId(), thread);
    }

    private void startServerOtherThread(McuPlcBridge mcuPlcBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuPlcBridge.isAvailable()) {
                        //获取资源占用信息
                        CmQuerySysResourceStatisticsRequest cmQuerySysResourceStatisticsRequest = new CmQuerySysResourceStatisticsRequest();
                        CmQuerySysResourceStatisticsResponse cmQuerySysResourceStatisticsResponse = mcuPlcBridge.getConferenceManageApi().querySysResourceStatistics(cmQuerySysResourceStatisticsRequest);
                        if (cmQuerySysResourceStatisticsResponse != null && CommonResponse.STATUS_OK.equals(cmQuerySysResourceStatisticsResponse.getStatus())) {
                            mcuPlcBridge.setSystemResourceCount(cmQuerySysResourceStatisticsResponse.getSystem_resource_count());
                            mcuPlcBridge.setUsedResourceCount(cmQuerySysResourceStatisticsResponse.getUsed_resource_count());
                        }
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
        serverOtherThreadMap.put(mcuPlcBridge.getBusiMcuPlc().getId(), thread);
    }

    private void startMeetingThread(McuPlcBridge mcuPlcBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuPlcBridge.isDataInitialized()) {
                        CmSearchMrRequest searchMrRequest = new CmSearchMrRequest();
                        CmSearchMrResponse searchMrResponse = mcuPlcBridge.getConferenceManageApi().searchMr(searchMrRequest);
                        if (searchMrResponse != null && CommonResponse.STATUS_OK.equals(searchMrResponse.getStatus())) {
                            mcuPlcBridge.setLastUpdateTime(System.currentTimeMillis());
                            if (searchMrResponse.getConfInfoMap() != null) {
                                processSearchMrResponse(searchMrResponse.getConfInfoMap(), mcuPlcBridge);
                            } else {
                                McuPlcConferenceContextCache.getInstance().clear();
                            }
                        } else {
                            mcuPlcBridge.setMcuToken("");
                            mcuPlcBridge.setMcuUserToken("");
                            mcuPlcBridge.setLastUpdateTime(0);
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
        meetingThreadMap.put(mcuPlcBridge.getBusiMcuPlc().getId(), thread);
    }

    private void processSearchMrResponse(Map<String, CmSearchMrResponse.ConfInfo> confInfoMap, McuPlcBridge mcuPlcBridge) {
        if (confInfoMap != null) {
            BusiMcuPlcTemplateConferenceMapper busiMcuPlcTemplateConferenceMapper = BeanFactory.getBean(BusiMcuPlcTemplateConferenceMapper.class);
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            IBusiMcuPlcConferenceService busiMcuPlcConferenceService = BeanFactory.getBean(IBusiMcuPlcConferenceService.class);
            HashSet<String> conferenceStarted = new HashSet<>();
            for (String conferenceNumber : confInfoMap.keySet()) {
                CmSearchMrResponse.ConfInfo confInfo = confInfoMap.get(conferenceNumber);
                String confId = confInfo.getId();
                McuPlcConferenceContext conferenceContext = null;
                Collection<McuPlcConferenceContext> conferenceContextListByConferenceNum = McuPlcConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNum != null) {
                    for (McuPlcConferenceContext mcuPlcConferenceContext : conferenceContextListByConferenceNum) {
                        if (mcuPlcConferenceContext.getMcuPlcBridge().getBusiMcuPlc().getIp().equals(mcuPlcBridge.getBusiMcuPlc().getIp())) {
                            conferenceContext = mcuPlcConferenceContext;
                        }
                    }
                }

                if (conferenceContext != null) {
                    conferenceStarted.add(conferenceNumber);
                } else {
                    try {
                        BusiMcuPlcTemplateConference busiMcuPlcTemplateConferenceCon = new BusiMcuPlcTemplateConference();
                        busiMcuPlcTemplateConferenceCon.setConferenceNumber(Long.valueOf(conferenceNumber));
                        List<BusiMcuPlcTemplateConference> busiMcuPlcTemplateConferenceList = busiMcuPlcTemplateConferenceMapper.selectAllBusiMcuPlcTemplateConferenceList(busiMcuPlcTemplateConferenceCon);
                        if (busiMcuPlcTemplateConferenceList.size() > 0) {
                            BusiMcuPlcTemplateConference busiMcuPlcTemplateConference = busiMcuPlcTemplateConferenceList.get(0);
                            if (busiMcuPlcTemplateConference.getLastConferenceId() != null) {
                                try {
                                    BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(busiMcuPlcTemplateConference.getLastConferenceId());
                                    if (busiHistoryConference != null) {
                                        if (busiHistoryConference.getConferenceEndTime() != null) {
                                            busiHistoryConference.setConferenceEndTime(null);
                                        }
                                        conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuPlcTemplateConference.getId());
                                        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                                        conferenceContext.setStart(true);
                                        conferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                        conferenceContext.setHistoryConference(busiHistoryConference);
                                        McuPlcConferenceContextCache.getInstance().add(conferenceContext);
                                        conferenceStarted.add(conferenceNumber);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        mcuPlcBridge.getMcuPlcLogger().logInfo("添加会议上下文失败", false, e);
                    }
                }

                Collection<McuPlcConferenceContext> conferenceContextListByConferenceNumT = McuPlcConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNumT != null) {
                    for (McuPlcConferenceContext mcuPlcConferenceContext : conferenceContextListByConferenceNumT) {
                        if (mcuPlcConferenceContext.getMcuPlcBridge().getBusiMcuPlc().getIp().equals(mcuPlcBridge.getBusiMcuPlc().getIp())) {
                            conferenceContext = mcuPlcConferenceContext;
                        }
                    }
                }

                if (conferenceContext == null) {
                    CmStopMrRequest cmStopMrRequest = new CmStopMrRequest();
                    cmStopMrRequest.setId(confId);
                    CmStopMrResponse cmStopMrResponse = mcuPlcBridge.getConferenceManageApi().stopMr(cmStopMrRequest);
                    if (cmStopMrResponse != null && CommonResponse.STATUS_OK.equals(cmStopMrResponse.getStatus())) {

                    }
                }
            }

            for (McuPlcConferenceContext mcuPlcConferenceContext : McuPlcConferenceContextCache.getInstance().values()) {
                if (!conferenceStarted.contains(mcuPlcConferenceContext.getConferenceNumber())) {
                    McuPlcBridge mcuPlcBridgeTemp = McuPlcBridgeCache.getInstance().getAvailableMcuPlcBridgesByDept(mcuPlcConferenceContext.getDeptId()).getMasterMcuPlcBridge();
                    if (mcuPlcBridgeTemp != null && mcuPlcBridgeTemp.getBusiMcuPlc().getId().longValue() == mcuPlcBridge.getBusiMcuPlc().getId().longValue()) {
                        if (!mcuPlcConferenceContext.isEnd()) {
                            if (System.currentTimeMillis() - mcuPlcConferenceContext.getStartTime().getTime() > 3000) {
                                mcuPlcConferenceContext.setEnd(true);
                                mcuPlcConferenceContext.setEndTime(new Date());
                                busiMcuPlcConferenceService.endConference(mcuPlcConferenceContext.getId(), ConferenceEndType.COMMON.getValue(), false, true);
                            }
                        }
                    }
                }
            }
        }
    }
}
