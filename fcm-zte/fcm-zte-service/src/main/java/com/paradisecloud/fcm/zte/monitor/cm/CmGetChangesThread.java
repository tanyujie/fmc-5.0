package com.paradisecloud.fcm.zte.monitor.cm;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.sign.Md5Utils;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.enumer.McuZteBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuZteBusiStatus;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZteTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuZteTemplateConference;
import com.paradisecloud.fcm.zte.cache.McuZteBridgeCache;
import com.paradisecloud.fcm.zte.cache.McuZteConferenceContextCache;
import com.paradisecloud.fcm.zte.cache.model.McuZteBridge;
import com.paradisecloud.fcm.zte.cache.model.McuZteConferenceContext;
import com.paradisecloud.fcm.zte.model.response.CommonResponse;
import com.paradisecloud.fcm.zte.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.zte.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.zte.service.interfaces.IBusiMcuZteConferenceService;
import com.paradisecloud.fcm.zte.task.McuZteCheckAttendeeOnlineStatusTask;
import com.paradisecloud.fcm.zte.task.McuZteDelayTaskService;
import com.sinhy.spring.BeanFactory;
import com.zte.m900.bean.ConferenceStatus;
import com.zte.m900.request.EmeetingloginRequest;
import com.zte.m900.request.EndConferenceRequest;
import com.zte.m900.request.GetConferenceInfoListRequest;
import com.zte.m900.request.QueryAddressBookV2Request;
import com.zte.m900.response.EmeetingloginResponse;
import com.zte.m900.response.EndConferenceResponse;
import com.zte.m900.response.GetConferenceInfoListResponse;
import com.zte.m900.response.QueryAddressBookV2Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CmGetChangesThread extends Thread {

    /**
     * key:mcuId
     */
    private final Map<Long, Thread> serverThreadMap = new ConcurrentHashMap<>();
    private final Map<Long, Thread> serverOtherThreadMap = new ConcurrentHashMap<>();
    private final Map<Long, Thread> meetingThreadMap = new ConcurrentHashMap<>();

    @Override
    public void run() {

        while (true) {
            if (isInterrupted()) {
                return;
            }

            try {
                long currentTimeMillis = System.currentTimeMillis();
                MonitorThreadCache.getInstance().setCmGetChangesTime(currentTimeMillis);
                for (McuZteBridge mcuZteBridge : McuZteBridgeCache.getInstance().getMcuZteBridges()) {
                    if (!serverThreadMap.containsKey(mcuZteBridge.getBusiMcuZte().getId())) {
                        startServerThread(mcuZteBridge);
                        startMeetingThread(mcuZteBridge);
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
                        McuZteBridge mcuZteBridge = McuZteBridgeCache.getInstance().get(id);
                        if (mcuZteBridge != null) {
                            mcuZteBridge.cleanLoginInfo();
                        }
                    }
                    if (!McuZteBridgeCache.getInstance().containsKey(id)) {
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

                McuZteCheckAttendeeOnlineStatusTask mcuZteCheckAttendeeOnlineStatusTask = new McuZteCheckAttendeeOnlineStatusTask(McuType.MCU_ZTE.getCode(), 0);
                McuZteDelayTaskService mcuZteDelayTaskService = BeanFactory.getBean(McuZteDelayTaskService.class);
                mcuZteDelayTaskService.addTask(mcuZteCheckAttendeeOnlineStatusTask);
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

    private void startServerThread(McuZteBridge mcuZteBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    long currentTimeMillis = System.currentTimeMillis();
                    if (mcuZteBridge.getLastUpdateTime() == 0 || StringUtils.isEmpty(mcuZteBridge.getMcuToken()) || currentTimeMillis - mcuZteBridge.getLastUpdateTime() > 10000) {
                        // 未登录时先登录
                        mcuZteBridge.setConnectionFailedReason(null);
                        mcuZteBridge.setDataInitialized(false);
                        mcuZteBridge.setBridgeStatus(McuZteBridgeStatus.INITIALIZING);
                        mcuZteBridge.setDiffTime(0);
                        // 登录
                        EmeetingloginRequest cmLoginRequest = new EmeetingloginRequest();
                        cmLoginRequest.setUserName(mcuZteBridge.getBusiMcuZte().getUsername());
                        cmLoginRequest.setPassword(Md5Utils.hash(mcuZteBridge.getBusiMcuZte().getPassword()));
                        EmeetingloginResponse cmLoginResponse = mcuZteBridge.getConferenceManageApi().login(cmLoginRequest);
                        if (cmLoginResponse != null && CommonResponse.STATUS_OK.equals(cmLoginResponse.getStatus())) {
                            mcuZteBridge.setMcuToken(Md5Utils.hash(mcuZteBridge.getBusiMcuZte().getPassword() + cmLoginResponse.getRandomKey()).toLowerCase());
                            com.zte.m900.bean.UserInfo userInfo = cmLoginResponse.getUserInfo();
                            if (userInfo != null) {
                                mcuZteBridge.setMcuUserId(userInfo.getUserID());
                            }
                            mcuZteBridge.setLastUpdateTime(currentTimeMillis);
                            mcuZteBridge.setBridgeStatus(McuZteBridgeStatus.AVAILABLE);
                            mcuZteBridge.getBusiMcuZte().setStatus(McuZteBusiStatus.ONLINE.getValue());
                            mcuZteBridge.getMcuZteLogger().logInfo("=============>ZTE- MCU登录成功。mcuToken：" + mcuZteBridge.getMcuToken());
                        } else {
                            mcuZteBridge.setBridgeStatus(McuZteBridgeStatus.NOT_AVAILABLE);
                            mcuZteBridge.setConnectionFailedReason("ZTE无法登录。");
                            mcuZteBridge.getBusiMcuZte().setStatus(McuZteBusiStatus.OFFLINE.getValue());
                            mcuZteBridge.getMcuZteLogger().logInfo("=============>ZTE MCU登录失败。");
                            return;
                        }
                        // 获取MCU时间

                        // 初始化会议
                        if (McuZteConferenceContextCache.getInstance().values().size() == 0) {
                            BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
                            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                            List<BusiMcuZteTemplateConference> busiMcuZteTemplateConferenceList = busiMcuZteTemplateConferenceMapper.selectBusiMcuZteTemplateConferenceList(new BusiMcuZteTemplateConference());
                            for (BusiMcuZteTemplateConference busiMcuZteTemplateConference : busiMcuZteTemplateConferenceList) {
                                try {
                                    McuZteBridge mcuZteBridgeTemp = null;
                                    try {
                                        mcuZteBridgeTemp = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(busiMcuZteTemplateConference.getDeptId()).getMasterMcuZteBridge();
                                    } catch (Exception e) {
                                    }
                                    if (mcuZteBridgeTemp != null && mcuZteBridgeTemp.getBusiMcuZte().getId().longValue() == mcuZteBridge.getBusiMcuZte().getId().longValue()) {
                                        McuZteConferenceContext mcuZteConferenceContext = McuZteConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZteTemplateConference.getId(), McuType.MCU_ZTE.getCode()));
                                        if (mcuZteConferenceContext == null) {
                                            if (busiMcuZteTemplateConference.getLastConferenceId() != null) {
                                                Long historyConferenceId = busiMcuZteTemplateConference.getLastConferenceId();
                                                BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
                                                if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
                                                    GetConferenceInfoListRequest searchMrRequest = new GetConferenceInfoListRequest();
                                                    searchMrRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());
                                                    GetConferenceInfoListResponse searchMrResponse = mcuZteBridge.getConferenceManageApi().getConferenceInfoList(searchMrRequest);
                                                    if (searchMrResponse != null && CommonResponse.STATUS_OK.equals(searchMrResponse.getStatus())) {
                                                        ConferenceStatus[] conferenceList = searchMrResponse.getConferenceList();
                                                        if (conferenceList.length > 0) {
                                                            for (ConferenceStatus conferenceStatus : conferenceList) {
                                                                if (Objects.equals(conferenceStatus.getConferenceNumber(), busiHistoryConference.getNumber())) {
                                                                    mcuZteConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuZteTemplateConference.getId());
                                                                    mcuZteConferenceContext.setAttendeeOperation(mcuZteConferenceContext.getDefaultViewOperation());
                                                                    mcuZteConferenceContext.setStart(true);
                                                                    mcuZteConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                                                    mcuZteConferenceContext.setHistoryConference(busiHistoryConference);
                                                                    McuZteConferenceContextCache.getInstance().add(mcuZteConferenceContext);
                                                                }

                                                            }
                                                        }
                                                    }


                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                        mcuZteBridge.setDataInitialized(true);
                    }
                    if (StringUtils.isNotEmpty(mcuZteBridge.getMcuToken())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (StringUtils.isEmpty(mcuZteBridge.getMcuToken())) {
                                break;
                            }
                            currentTimeMillis = System.currentTimeMillis();
                            if (mcuZteBridge.getLastUpdateTime() == 0 || currentTimeMillis - mcuZteBridge.getLastUpdateTime() > 10000) {
                                QueryAddressBookV2Request cmGetChangesRequest = new QueryAddressBookV2Request();
                                cmGetChangesRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());
                                cmGetChangesRequest.setPage(1);
                                cmGetChangesRequest.setNumPerPage(10);
                                QueryAddressBookV2Response response = mcuZteBridge.getConferenceManageApi().getCcTerminalInfo(cmGetChangesRequest);
                                if (response != null && CommonResponse.STATUS_OK.equals(response.getStatus())) {
                                    mcuZteBridge.setLastUpdateTime(currentTimeMillis);
                                } else {
                                    mcuZteBridge.getMcuZteLogger().logInfo("=============>ZTE- MCU登退出登录。");
                                    mcuZteBridge.setLastUpdateTime(0);
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
        serverThreadMap.put(mcuZteBridge.getBusiMcuZte().getId(), thread);
    }


    private void startMeetingThread(McuZteBridge mcuZteBridge) {
        Thread thread = new Thread(new Runnable() {
            private final Map<String, AtomicInteger> endConferenceMap = new ConcurrentHashMap<>();

            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuZteBridge.isDataInitialized()) {
                        GetConferenceInfoListRequest searchMrRequest = new GetConferenceInfoListRequest();
                        searchMrRequest.setAccount(mcuZteBridge.getBusiMcuZte().getUsername());
                        GetConferenceInfoListResponse searchMrResponse = mcuZteBridge.getConferenceManageApi().getConferenceInfoList(searchMrRequest);
                        if (searchMrResponse != null && CommonResponse.STATUS_OK.equals(searchMrResponse.getStatus())) {
                            mcuZteBridge.setLastUpdateTime(System.currentTimeMillis());
                            if (searchMrResponse.getConferenceList() != null && searchMrResponse.getConferenceList().length > 0) {
                                processSearchMrResponse(searchMrResponse.getConferenceList(), mcuZteBridge, endConferenceMap);
                            } else {
                                McuZteConferenceContextCache.getInstance().clear();
                            }
                        } else {
                            mcuZteBridge.setMcuToken("");
                            mcuZteBridge.setMcuUserToken("");
                            mcuZteBridge.setLastUpdateTime(0);
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
        meetingThreadMap.put(mcuZteBridge.getBusiMcuZte().getId(), thread);
    }

    private void processSearchMrResponse(ConferenceStatus[] conferenceStatuses, McuZteBridge mcuZteBridge, Map<String, AtomicInteger> endConferenceMap) {
        if (conferenceStatuses != null) {
            BusiMcuZteTemplateConferenceMapper busiMcuZteTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZteTemplateConferenceMapper.class);
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            IBusiMcuZteConferenceService busiMcuZteConferenceService = BeanFactory.getBean(IBusiMcuZteConferenceService.class);
            HashSet<String> conferenceStarted = new HashSet<>();
            for (ConferenceStatus conferenceStatus : conferenceStatuses) {
                String conferenceNumber = conferenceStatus.getConferenceNumber();
                String confId = conferenceStatus.getConferenceIdentifier();
                McuZteConferenceContext conferenceContext = null;
                Collection<McuZteConferenceContext> conferenceContextListByConferenceNum = McuZteConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNum != null) {
                    for (McuZteConferenceContext mcuZteConferenceContext : conferenceContextListByConferenceNum) {
                        if (mcuZteConferenceContext.getMcuZteBridge().getBusiMcuZte().getIp().equals(mcuZteBridge.getBusiMcuZte().getIp())) {
                            conferenceContext = mcuZteConferenceContext;
                        }
                    }
                }

                if (conferenceContext != null) {
                    conferenceStarted.add(conferenceNumber);
                } else {
                    try {
                        BusiMcuZteTemplateConference busiMcuZteTemplateConferenceCon = new BusiMcuZteTemplateConference();
                        busiMcuZteTemplateConferenceCon.setConferenceNumber(Long.valueOf(conferenceNumber));
                        List<BusiMcuZteTemplateConference> busiMcuZteTemplateConferenceList = busiMcuZteTemplateConferenceMapper.selectAllBusiMcuZteTemplateConferenceList(busiMcuZteTemplateConferenceCon);
                        if (busiMcuZteTemplateConferenceList.size() > 0) {
                            BusiMcuZteTemplateConference busiMcuZteTemplateConference = busiMcuZteTemplateConferenceList.get(0);
                            if (busiMcuZteTemplateConference.getLastConferenceId() != null) {
                                try {
                                    BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(busiMcuZteTemplateConference.getLastConferenceId());
                                    if (busiHistoryConference != null) {
                                        if (busiHistoryConference.getConferenceEndTime() != null) {
                                            busiHistoryConference.setConferenceEndTime(null);
                                        }
                                        conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuZteTemplateConference.getId());
                                        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                                        conferenceContext.setStart(true);
                                        conferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                        conferenceContext.setHistoryConference(busiHistoryConference);
                                        McuZteConferenceContextCache.getInstance().add(conferenceContext);
                                        conferenceStarted.add(conferenceNumber);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        mcuZteBridge.getMcuZteLogger().logInfo("ZTE添加会议上下文失败", false, e);
                    }
                }

                Collection<McuZteConferenceContext> conferenceContextListByConferenceNumT = McuZteConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNumT != null) {
                    for (McuZteConferenceContext mcuZteConferenceContext : conferenceContextListByConferenceNumT) {
                        if (mcuZteConferenceContext.getMcuZteBridge().getBusiMcuZte().getIp().equals(mcuZteBridge.getBusiMcuZte().getIp())) {
                            conferenceContext = mcuZteConferenceContext;
                        }
                    }
                }

                if (conferenceContext == null) {
                    EndConferenceRequest cmStopMrRequest = new EndConferenceRequest();
                    cmStopMrRequest.setConferenceIdentifier(confId);
                    EndConferenceResponse cmStopMrResponse = mcuZteBridge.getConferenceManageApi().stopMr(cmStopMrRequest);

                }
            }


            for (McuZteConferenceContext mcuZteConferenceContext : McuZteConferenceContextCache.getInstance().values()) {
                if (!conferenceStarted.contains(mcuZteConferenceContext.getConferenceNumber())) {
                    McuZteBridge mcuZteBridgeTemp = McuZteBridgeCache.getInstance().getAvailableMcuZteBridgesByDept(mcuZteConferenceContext.getDeptId()).getMasterMcuZteBridge();
                    if (mcuZteBridgeTemp != null && mcuZteBridgeTemp.getBusiMcuZte().getId().longValue() == mcuZteBridge.getBusiMcuZte().getId().longValue()) {
                        if (!mcuZteConferenceContext.isEnd()) {
                            AtomicInteger atomicInteger = endConferenceMap.get(mcuZteConferenceContext.getId());
                            if (atomicInteger == null) {
                                atomicInteger = new AtomicInteger(1);
                                endConferenceMap.put(mcuZteConferenceContext.getId(), atomicInteger);
                            } else {
                                int i = atomicInteger.get();
                                if (i > 2) {
                                    endConferenceMap.remove(mcuZteConferenceContext.getId());
                                    mcuZteConferenceContext.setEnd(true);
                                    mcuZteConferenceContext.setEndTime(new Date());
                                    busiMcuZteConferenceService.endConference(mcuZteConferenceContext.getId(), ConferenceEndType.COMMON.getValue(), false, true);
                                } else {
                                    atomicInteger.incrementAndGet();
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
