package com.paradisecloud.fcm.mcu.zj.monitor.cm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.*;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjResourceTemplateMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuZjTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.BusiMcuZjResourceTemplate;
import com.paradisecloud.fcm.dao.model.BusiMcuZjTemplateConference;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjConferenceContextCache;
import com.paradisecloud.fcm.mcu.zj.cache.McuZjBridgeCache;
import com.paradisecloud.fcm.mcu.zj.cache.api.ConferenceManageApi;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.conference.model.templateconference.BuildTemplateConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.SourceTemplate;
import com.paradisecloud.fcm.mcu.zj.model.busi.layout.splitscreen.*;
import com.paradisecloud.fcm.mcu.zj.model.enumer.LayoutTemplates;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.*;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.*;
import com.paradisecloud.fcm.mcu.zj.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.zj.service.interfaces.IBusiMcuZjConferenceService;
import com.paradisecloud.fcm.mcu.zj.task.McuZjCheckAttendeeOnlineStatusTask;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mcu.zj.task.McuZjDeleteRoomTask;
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
                for (McuZjBridge mcuZjBridge : McuZjBridgeCache.getInstance().getMcuZjBridges()) {
                    if (!serverThreadMap.containsKey(mcuZjBridge.getBusiMcuZj().getId())) {
                        startServerThread(mcuZjBridge);
                        startServerOtherThread(mcuZjBridge);
                        startMeetingThread(mcuZjBridge);
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
                        McuZjBridge mcuZjBridge = McuZjBridgeCache.getInstance().get(id);
                        if (mcuZjBridge != null) {
                            mcuZjBridge.cleanLoginInfo();
                        }
                    }
                    if (!McuZjBridgeCache.getInstance().containsKey(id)) {
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

                McuZjCheckAttendeeOnlineStatusTask mcuZjCheckAttendeeOnlineStatusTask = new McuZjCheckAttendeeOnlineStatusTask(McuType.MCU_ZJ.getCode(), 0);
                DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
                delayTaskService.addTask(mcuZjCheckAttendeeOnlineStatusTask);
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

    private void startServerThread(McuZjBridge mcuZjBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    long currentTimeMillis = System.currentTimeMillis();
                    if (mcuZjBridge.getLastUpdateTime() == 0 || StringUtils.isEmpty(mcuZjBridge.getSessionId()) || currentTimeMillis - mcuZjBridge.getLastUpdateTime() > 30000) {
                        // 未登录时先登录
                        mcuZjBridge.setConnectionFailedReason(null);
                        mcuZjBridge.setDataInitialized(false);
                        mcuZjBridge.setBridgeStatus(McuZjBridgeStatus.INITIALIZING);
                        // 获取验证码
                        CmGetVerifyCodeResponse cmGetVerifyCodeResponse = mcuZjBridge.getConferenceManageApi().getVerifyCode();
                        if (cmGetVerifyCodeResponse != null && ConferenceManageApi.CMD_ID_verify_code.equals(cmGetVerifyCodeResponse.getCmdid())) {
                            CmLoginRequest cmLoginRequest = new CmLoginRequest();
                            if (mcuZjBridge.getBusiMcuZj().getUsername().contains("@")) {
                                cmLoginRequest.setLogin_id(mcuZjBridge.getBusiMcuZj().getUsername());
                            } else {
                                cmLoginRequest.setLogin_id(mcuZjBridge.getBusiMcuZj().getUsername() + "@" + mcuZjBridge.getBusiMcuZj().getMcuDomain());
                            }
                            cmLoginRequest.setLogin_pwd(mcuZjBridge.getBusiMcuZj().getPassword());
                            cmLoginRequest.setCert_code(cmGetVerifyCodeResponse.getCert_code());
                            cmLoginRequest.setLogin_type(1);// 管理员身份
                            cmLoginRequest.setDate_time(cmGetVerifyCodeResponse.getDate_time());
                            // 登录
                            CmLoginResponse cmLoginResponse = mcuZjBridge.getConferenceManageApi().login(cmLoginRequest);
                            if (cmLoginResponse != null && ConferenceManageApi.CMD_ID_login_rsp.equals(cmLoginResponse.getCmdid())) {
                                if ("404".equals(cmLoginResponse.getResult()) || StringUtils.isEmpty(cmLoginResponse.getSession_id())) {
                                    mcuZjBridge.setBridgeStatus(McuZjBridgeStatus.NOT_AVAILABLE);
                                    mcuZjBridge.setConnectionFailedReason("无法登录。");
                                    mcuZjBridge.getBusiMcuZj().setStatus(McuZjBusiStatus.OFFLINE.getValue());
                                    mcuZjBridge.getMcuZjLogger().logInfo("=============> MCU登录失败！");
                                    return;
                                }
                                mcuZjBridge.setSessionId(cmLoginResponse.getSession_id());
                                mcuZjBridge.setTenantId(cmLoginResponse.getTenant_id());
                                mcuZjBridge.setLastUpdateTime(currentTimeMillis);
                                mcuZjBridge.setBridgeStatus(McuZjBridgeStatus.AVAILABLE);
                                mcuZjBridge.getBusiMcuZj().setStatus(McuZjBusiStatus.ONLINE.getValue());
                                mcuZjBridge.getMcuZjLogger().logInfo("=============> MCU登录成功。sessionId：" + mcuZjBridge.getSessionId());
                            } else {
                                mcuZjBridge.setBridgeStatus(McuZjBridgeStatus.NOT_AVAILABLE);
                                mcuZjBridge.setConnectionFailedReason("无法登录。");
                                mcuZjBridge.getBusiMcuZj().setStatus(McuZjBusiStatus.OFFLINE.getValue());
                                mcuZjBridge.getMcuZjLogger().logInfo("=============> MCU登录失败。");
                                return;
                            }
                            // 获取总部
                            CmSearchDepartmentsRequest cmSearchDepartmentsRequest = new CmSearchDepartmentsRequest();
                            cmSearchDepartmentsRequest.setFilter_type(new String[]{"senior_id"});
                            cmSearchDepartmentsRequest.setFilter_value(new Object[]{-1});
                            CmSearchDepartmentsResponse cmSearchDepartmentsResponse = mcuZjBridge.getConferenceManageApi().searchDepartments(cmSearchDepartmentsRequest);
                            if (cmSearchDepartmentsResponse != null) {
                                if (cmSearchDepartmentsResponse.getDepartment_ids() != null && cmSearchDepartmentsResponse.getDepartment_ids().length > 0) {
                                    mcuZjBridge.setTopDepartmentId(cmSearchDepartmentsResponse.getDepartment_ids()[0]);
                                }
                            }
                            // 获取资源模板
                            getResourceTemplate(mcuZjBridge);
                            // 初始化会议
                            McuZjConferenceContextCache.getInstance().clear();
                            BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
                            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
                            List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceMapper.selectBusiMcuZjTemplateConferenceList(new BusiMcuZjTemplateConference());
                            for (BusiMcuZjTemplateConference busiMcuZjTemplateConference : busiMcuZjTemplateConferenceList) {
                                try {
                                    McuZjBridge mcuZjBridgeTemp = null;
                                    try {
                                        mcuZjBridgeTemp = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(busiMcuZjTemplateConference.getDeptId()).getMasterMcuZjBridge();
                                    } catch (Exception e) {
                                    }
                                    if (mcuZjBridgeTemp != null && mcuZjBridgeTemp.getBusiMcuZj().getId().longValue() == mcuZjBridge.getBusiMcuZj().getId().longValue()) {
                                        McuZjConferenceContext mcuZjConferenceContext = McuZjConferenceContextCache.getInstance().get(EncryptIdUtil.generateContextKey(busiMcuZjTemplateConference.getId(), McuType.MCU_ZJ.getCode()));
                                        if (mcuZjConferenceContext == null) {
                                            mcuZjConferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuZjTemplateConference.getId());
                                            if (busiMcuZjTemplateConference.getLastConferenceId() != null) {
                                                Long historyConferenceId = busiMcuZjTemplateConference.getLastConferenceId();
                                                BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(historyConferenceId);
                                                if (busiHistoryConference != null && busiHistoryConference.getConferenceEndTime() == null) {
                                                    mcuZjConferenceContext.setAttendeeOperation(mcuZjConferenceContext.getDefaultViewOperation());
                                                    if (mcuZjConferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                                                        mcuZjConferenceContext.setAttendeeOperationForGuest(mcuZjConferenceContext.getDefaultViewOperation());
                                                    } else {
                                                        mcuZjConferenceContext.setAttendeeOperationForGuest(mcuZjConferenceContext.getDefaultViewOperationForGuest());
                                                    }
                                                    mcuZjConferenceContext.setLastAttendeeOperationForGuest(mcuZjConferenceContext.getDefaultViewOperationForGuest());
                                                    mcuZjConferenceContext.setStart(true);
                                                    mcuZjConferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                                    mcuZjConferenceContext.setHistoryConference(busiHistoryConference);
                                                    McuZjConferenceContextCache.getInstance().add(mcuZjConferenceContext);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                            mcuZjBridge.setDataInitialized(true);
                        } else {
                            mcuZjBridge.setBridgeStatus(McuZjBridgeStatus.NOT_AVAILABLE);
                            mcuZjBridge.setConnectionFailedReason("无法访问或者连接参数配置错误。");
                            mcuZjBridge.getBusiMcuZj().setStatus(McuZjBusiStatus.OFFLINE.getValue());
                            return;
                        }
                    }
                    if (StringUtils.isNotEmpty(mcuZjBridge.getSessionId())) {
                        while (true) {
                            if (isInterrupted()) {
                                return;
                            }
                            if (StringUtils.isEmpty(mcuZjBridge.getSessionId())) {
                                mcuZjBridge.getMcuZjLogger().logInfo("=============> MCU登退出登录。");
                                mcuZjBridge.setLastUpdateTime(0);
                                return;
                            }
                            CmGetChangesRequest cmGetChangesRequest = new CmGetChangesRequest();
                            cmGetChangesRequest.setBlock_secs(10);
                            String response = mcuZjBridge.getConferenceManageApi().getChanges(cmGetChangesRequest);
                            if (response != null) {
                                try {
                                    mcuZjBridge.getMcuZjLogger().logInfo(mcuZjBridge.getBaseUrl() + " #CM# getChange:response:" + response);
                                    List<JSONObject> jsonArray = JSON.parseArray(response, JSONObject.class);
                                    for (JSONObject jsonObject : jsonArray) {
                                        processResponse(jsonObject, mcuZjBridge);
                                    }
                                } catch (Exception e) {
                                }
                                mcuZjBridge.setLastUpdateTime(currentTimeMillis);
                            } else {
                                mcuZjBridge.getMcuZjLogger().logInfo("=============> MCU登退出登录。");
                                mcuZjBridge.setLastUpdateTime(0);
                                break;
                            }
                        }
                    }
                }
            }
        });
        thread.start();
        serverThreadMap.put(mcuZjBridge.getBusiMcuZj().getId(), thread);
    }

    private void getResourceTemplate(McuZjBridge mcuZjBridge) {
        // 获取资源模板
        BusiMcuZjResourceTemplateMapper busiMcuZjResourceTemplateMapper = BeanFactory.getBean(BusiMcuZjResourceTemplateMapper.class);
        BusiMcuZjResourceTemplate busiMcuZjResourceTemplateCon = new BusiMcuZjResourceTemplate();
        busiMcuZjResourceTemplateCon.setMcuZjServerId(mcuZjBridge.getBusiMcuZj().getId());
        List<BusiMcuZjResourceTemplate> busiMcuZjResourceTemplateList = busiMcuZjResourceTemplateMapper.selectBusiMcuZjResourceTemplateList(busiMcuZjResourceTemplateCon);
        Map<String, BusiMcuZjResourceTemplate> resourceTemplateMap = new HashMap<>();
        for (BusiMcuZjResourceTemplate busiMcuZjResourceTemplate : busiMcuZjResourceTemplateList) {
            resourceTemplateMap.put(busiMcuZjResourceTemplate.getName(), busiMcuZjResourceTemplate);
        }
        if (resourceTemplateMap.size() > 0) {
            boolean hasDefault = false;
            boolean getResourceTemplate = true;
            for (int m = 0; m < 2 && getResourceTemplate; m++) {
                getResourceTemplate = false;
                CmQueryResourceTmplResponse cmQueryResourceTmplResponse = mcuZjBridge.getConferenceManageApi().queryResourceTmpl();
                if (cmQueryResourceTmplResponse != null) {
                    CmQueryResourceTmplInfoRequest cmQueryResourceTmplInfoRequest = new CmQueryResourceTmplInfoRequest();
                    cmQueryResourceTmplInfoRequest.setIds(cmQueryResourceTmplResponse.getIds());
                    CmQueryResourceTmplInfoResponse cmQueryResourceTmplInfoResponse = mcuZjBridge.getConferenceManageApi().queryResourceTmplInfo(cmQueryResourceTmplInfoRequest);
                    if (cmQueryResourceTmplInfoResponse != null) {
                        for (int i = 0; i < cmQueryResourceTmplInfoResponse.getIds().length; i++) {
                            String name = cmQueryResourceTmplInfoResponse.getNames()[i];
                            if (resourceTemplateMap.containsKey(name)) {
                                BusiMcuZjResourceTemplate busiMcuZjResourceTemplate = resourceTemplateMap.get(name);
                                SourceTemplate sourceTemplate = getSourceTemplate(i, cmQueryResourceTmplInfoResponse);
                                sourceTemplate.setIs_default(0);
                                if (busiMcuZjResourceTemplate.getIsDefault() == 1) {
                                    sourceTemplate.setIs_default(1);
                                    hasDefault = true;
                                }
                                mcuZjBridge.addSourceTemplate(sourceTemplate);
                            }
                        }
                        for (String resourceName : resourceTemplateMap.keySet()) {
                            if (mcuZjBridge.getSourceTemplate(resourceName) == null) {
                                BusiMcuZjResourceTemplate busiMcuZjResourceTemplate = resourceTemplateMap.get(resourceName);
                                CmAddResourceRequest cmAddResourceRequest = CmAddResourceRequest.buildDefaultRequest();
                                cmAddResourceRequest.setName(busiMcuZjResourceTemplate.getName());
                                cmAddResourceRequest.setHas_mosic(busiMcuZjResourceTemplate.getHasMosic());
                                cmAddResourceRequest.setHas_record(busiMcuZjResourceTemplate.getHasRecord());
                                cmAddResourceRequest.setMax_mosic(busiMcuZjResourceTemplate.getMaxMosic());
                                cmAddResourceRequest.setMax_spk_mosic(busiMcuZjResourceTemplate.getMaxSpkMosic());
                                cmAddResourceRequest.setMax_guest_mosic(busiMcuZjResourceTemplate.getMaxGuestMosic());
                                cmAddResourceRequest.setMax_chair_mosic(busiMcuZjResourceTemplate.getMaxChairMosic());
                                cmAddResourceRequest.setChair_copy(busiMcuZjResourceTemplate.getChairCopy());
                                cmAddResourceRequest.setRes_bw(busiMcuZjResourceTemplate.getResBw());
                                cmAddResourceRequest.setSingle_view(busiMcuZjResourceTemplate.getSingleView());
                                if (ConferenceManageApi.RESOURCE_RES_BW_4KP30_4M.equals(busiMcuZjResourceTemplate.getResBw())
                                        || ConferenceManageApi.RESOURCE_RES_BW_4KP30_8M.equals(busiMcuZjResourceTemplate.getResBw())) {
                                    cmAddResourceRequest.setHas_h265(1);
                                }
                                mcuZjBridge.getConferenceManageApi().addResourceTmpl(cmAddResourceRequest);

                                getResourceTemplate = true;
                            }
                        }
                    }
                }
            }
            List<SourceTemplate> sourceTemplateList = mcuZjBridge.getSourceTemplateList();
            if (sourceTemplateList.size() > 0) {
                if (!hasDefault) {
                    sourceTemplateList.get(0).setIs_default(1);
                }
                Integer[] resourceTemplateIds = new Integer[sourceTemplateList.size()];
                Integer[] maxCalls = new Integer[sourceTemplateList.size()];
                for (int i = 0; i < sourceTemplateList.size(); i++) {
                    SourceTemplate sourceTemplate = sourceTemplateList.get(i);
                    resourceTemplateIds[i] = sourceTemplate.getId();
                    maxCalls[i] = 1000;
                }
                //资源模板预估资源占用
                CmQueryResourceEvaluationRequest cmQueryResourceEvaluationRequest = new CmQueryResourceEvaluationRequest();
                cmQueryResourceEvaluationRequest.setResource_template_ids(resourceTemplateIds);
                cmQueryResourceEvaluationRequest.setMax_calls(maxCalls);
                CmQueryResourceEvaluationResponse cmQueryResourceEvaluationResponse = mcuZjBridge.getConferenceManageApi().queryResourceEvaluation(cmQueryResourceEvaluationRequest);
                if (cmQueryResourceEvaluationResponse != null && cmQueryResourceEvaluationResponse.getResource_evaluations() != null && cmQueryResourceEvaluationResponse.getResource_evaluations().length == sourceTemplateList.size()) {
                    for (int j = 0; j < sourceTemplateList.size(); j++) {
                        SourceTemplate sourceTemplate = sourceTemplateList.get(j);
                        sourceTemplate.setEvaluationResourceCount(cmQueryResourceEvaluationResponse.getResource_evaluations()[j]);
                    }
                }
            }
        }
    }

    private SourceTemplate getSourceTemplate(int i, CmQueryResourceTmplInfoResponse cmQueryResourceTmplInfoResponse) {
        SourceTemplate sourceTemplate = new SourceTemplate();
        sourceTemplate.setId(cmQueryResourceTmplInfoResponse.getIds()[i]);
        sourceTemplate.setMr_count(cmQueryResourceTmplInfoResponse.getMr_counts()[i]);
        sourceTemplate.setHas_record(cmQueryResourceTmplInfoResponse.getHas_records()[i]);
        sourceTemplate.setHas_mosic(cmQueryResourceTmplInfoResponse.getHas_mosics()[i]);
        sourceTemplate.setMax_mosic(cmQueryResourceTmplInfoResponse.getMax_mosics()[i]);
        sourceTemplate.setMax_spk_mosic(cmQueryResourceTmplInfoResponse.getMax_spk_mosics()[i]);
        sourceTemplate.setMax_guest_mosic(cmQueryResourceTmplInfoResponse.getMax_guest_mosics()[i]);
        sourceTemplate.setMax_chair_mosic(cmQueryResourceTmplInfoResponse.getMax_chair_mosics()[i]);
        sourceTemplate.setChair_copy(cmQueryResourceTmplInfoResponse.getChair_copys()[i]);
        sourceTemplate.setRes_bw(cmQueryResourceTmplInfoResponse.getRes_bws()[i]);
        sourceTemplate.setIs_readonly(cmQueryResourceTmplInfoResponse.getIs_readonlys()[i]);
        sourceTemplate.setIs_default(cmQueryResourceTmplInfoResponse.getIs_defaults()[i]);
        sourceTemplate.setExtra(cmQueryResourceTmplInfoResponse.getExtras()[i]);
        sourceTemplate.setSingle_view(cmQueryResourceTmplInfoResponse.getSingle_views()[i]);
        sourceTemplate.setName(cmQueryResourceTmplInfoResponse.getNames()[i]);
        String sourceName = cmQueryResourceTmplInfoResponse.getNames()[i];
        sourceTemplate.setSourceName(sourceName);
        if (sourceTemplate.getHas_mosic() == 1) {
            sourceTemplate.setSupportSplitScreen(true);
            sourceTemplate.setSupportPolling(true);
            sourceTemplate.setSupportRollCall(true);
            sourceTemplate.setSupportChooseSee(true);
            sourceTemplate.setSupportTalk(true);
        } else {
            sourceTemplate.setSupportSplitScreen(false);
            sourceTemplate.setSupportPolling(false);
            sourceTemplate.setSupportRollCall(false);
            sourceTemplate.setSupportChooseSee(false);
            sourceTemplate.setSupportTalk(false);
        }
        List<ModelBean> speakerSplitScreenList = new ArrayList<>();
        List<ModelBean> guestSplitScreenList = new ArrayList<>();
        if (sourceTemplate.getHas_mosic() == 1) {
            //主会场
            if (sourceTemplate.getSingle_view() == 0) {
                {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.AUTO.getName());
                    modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 1) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_1.getName());
                    modelBean.put("value", OneSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 4) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_4.getName());
                    modelBean.put("value", FourSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 6) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_6.getName());
                    modelBean.put("value", OnePlusFiveSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 8) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_8.getName());
                    modelBean.put("value", OnePlusSevenSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 9) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_9.getName());
                    modelBean.put("value", NineSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 10) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_10.getName());
                    modelBean.put("value", OnePlusNineSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 16) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_16.getName());
                    modelBean.put("value", SixteenSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }
                if (sourceTemplate.getMax_spk_mosic() >= 25) {
                    ModelBean modelBean = new ModelBean();
                    modelBean.put("name", LayoutTemplates.SCREEN_25.getName());
                    modelBean.put("value", TwentyFiveSplitScreen.LAYOUT);
                    speakerSplitScreenList.add(modelBean);
                }

                if (sourceTemplate.getMax_spk_mosic() <= sourceTemplate.getMax_guest_mosic()) {
                    sourceTemplate.setSupportBroadcast(true);
                }
            }
            //观众
            {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.AUTO.getName());
                modelBean.put("value", AutomaticSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 1) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_1.getName());
                modelBean.put("value", OneSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 4) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_4.getName());
                modelBean.put("value", FourSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 6) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_6.getName());
                modelBean.put("value", OnePlusFiveSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 8) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_8.getName());
                modelBean.put("value", OnePlusSevenSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 9) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_9.getName());
                modelBean.put("value", NineSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 10) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_10.getName());
                modelBean.put("value", OnePlusNineSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 16) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_16.getName());
                modelBean.put("value", SixteenSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
            if (sourceTemplate.getMax_guest_mosic() >= 25) {
                ModelBean modelBean = new ModelBean();
                modelBean.put("name", LayoutTemplates.SCREEN_25.getName());
                modelBean.put("value", TwentyFiveSplitScreen.LAYOUT);
                guestSplitScreenList.add(modelBean);
            }
        }
        if (sourceTemplate.getSingle_view() == 1) {
            speakerSplitScreenList = guestSplitScreenList;
        }
        sourceTemplate.setSpeakerSplitScreenList(speakerSplitScreenList);
        sourceTemplate.setGuestSplitScreenList(guestSplitScreenList);
        return sourceTemplate;
    }

    private void processResponse(JSONObject jsonObject, McuZjBridge mcuZjBridge) {
        String cmdid = jsonObject.getString("cmdid");
        if (StringUtils.isNotEmpty(cmdid)) {
            if (ConferenceManageApi.CMD_ID_self_usr_info.equals(cmdid)) {

            } else if (ConferenceManageApi.CMD_ID_sys_mosics.equals(cmdid)) {

            } else if (ConferenceManageApi.CMD_ID_global_info.equals(cmdid)) {

            } else if (ConferenceManageApi.CMD_ID_mr_info.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CmQueryMrResponse queryMrResponse = JSON.parseObject(contentBody, CmQueryMrResponse.class);
                processQueryMrResponse(queryMrResponse, mcuZjBridge);
            } else if (ConferenceManageApi.CMD_ID_moded_mr.equals(cmdid)) {

            } else if (ConferenceManageApi.CMD_ID_stopped_mr.equals(cmdid)) {
                String contentBody = jsonObject.toJSONString();
                CmStopMrResponse stopMrResponse = JSON.parseObject(contentBody, CmStopMrResponse.class);
                processStopMrResponse(stopMrResponse, mcuZjBridge);
            }
        }
    }

    private void startServerOtherThread(McuZjBridge mcuZjBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuZjBridge.isAvailable()) {
                        if (mcuZjBridge.isSourceTemplateChanged()) {
                            mcuZjBridge.setSourceTemplateChanged(false);
                            getResourceTemplate(mcuZjBridge);
                        }
                        //获取资源占用信息
                        CmQuerySysResourceStatisticsResponse cmQuerySysResourceStatisticsResponse = mcuZjBridge.getConferenceManageApi().querySysResourceStatistics();
                        if (cmQuerySysResourceStatisticsResponse != null) {
                            mcuZjBridge.setSystemResourceCount(cmQuerySysResourceStatisticsResponse.getSystem_resource_count());
                            mcuZjBridge.setUsedResourceCount(cmQuerySysResourceStatisticsResponse.getUsed_resource_count());
                        }
                    }

                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        serverOtherThreadMap.put(mcuZjBridge.getBusiMcuZj().getId(), thread);
    }

    private void startMeetingThread(McuZjBridge mcuZjBridge) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isInterrupted()) {
                        return;
                    }

                    if (mcuZjBridge.isDataInitialized()) {
                        CmSearchMrResponse searchMrResponse = mcuZjBridge.getConferenceManageApi().searchMr();
                        if (searchMrResponse != null) {
                            if (searchMrResponse.getMr_ids().length > 0) {
                                CmQueryMrRequest queryMrRequest = new CmQueryMrRequest();
                                queryMrRequest.setMr_ids(searchMrResponse.getMr_ids());
                                CmQueryMrResponse queryMrResponse = mcuZjBridge.getConferenceManageApi().queryMr(queryMrRequest);
                                processQueryMrResponse(queryMrResponse, mcuZjBridge);
                            } else {
                                McuZjConferenceContextCache.getInstance().clear();
                            }
                        }
                    }

                    try {
                        sleep(20000);
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        thread.start();
        meetingThreadMap.put(mcuZjBridge.getBusiMcuZj().getId(), thread);
    }

    private void processQueryMrResponse(CmQueryMrResponse queryMrResponse, McuZjBridge mcuZjBridge) {
        if (queryMrResponse != null && queryMrResponse.getMr_ids() != null) {
            BusiMcuZjTemplateConferenceMapper busiMcuZjTemplateConferenceMapper = BeanFactory.getBean(BusiMcuZjTemplateConferenceMapper.class);
            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
            IBusiMcuZjConferenceService busiMcuZjConferenceService = BeanFactory.getBean(IBusiMcuZjConferenceService.class);
            DelayTaskService delayTaskService = BeanFactory.getBean(DelayTaskService.class);
            HashSet<String> conferenceStarted = new HashSet<>();
            for (int i = 0; i < queryMrResponse.getMr_ids().length; i++) {
                String mrId = queryMrResponse.getMr_ids()[i];
                String launcherUsrName = queryMrResponse.getLauncher_usr_names()[i];
                String conferenceNumber = mrId.substring(mcuZjBridge.getTenantId().length());
                McuZjConferenceContext conferenceContext = null;
                Collection<McuZjConferenceContext> conferenceContextListByConferenceNum = McuZjConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNum != null) {
                    for (McuZjConferenceContext mcuZjConferenceContext : conferenceContextListByConferenceNum) {
                        if (mcuZjConferenceContext.getMcuZjBridge().getBusiMcuZj().getIp().equals(mcuZjBridge.getBusiMcuZj().getIp())) {
                            conferenceContext = mcuZjConferenceContext;
                        }
                    }
                }

                if (conferenceContext != null) {
                    conferenceStarted.add(conferenceNumber);
                } else {
                    try {
                        BusiMcuZjTemplateConference busiMcuZjTemplateConferenceCon = new BusiMcuZjTemplateConference();
                        busiMcuZjTemplateConferenceCon.setConferenceNumber(Long.valueOf(conferenceNumber));
                        List<BusiMcuZjTemplateConference> busiMcuZjTemplateConferenceList = busiMcuZjTemplateConferenceMapper.selectAllBusiMcuZjTemplateConferenceList(busiMcuZjTemplateConferenceCon);
                        if (busiMcuZjTemplateConferenceList.size() > 0) {
                            BusiMcuZjTemplateConference busiMcuZjTemplateConference = busiMcuZjTemplateConferenceList.get(0);
                            if (busiMcuZjTemplateConference.getLastConferenceId() != null) {
                                try {
                                    BusiHistoryConference busiHistoryConference = busiHistoryConferenceMapper.selectBusiHistoryConferenceById(busiMcuZjTemplateConference.getLastConferenceId());
                                    if (busiHistoryConference != null) {
                                        if (busiHistoryConference.getConferenceEndTime() != null) {
                                            busiHistoryConference.setConferenceEndTime(null);
                                        }
                                        conferenceContext = new BuildTemplateConferenceContext().buildTemplateConferenceContext(busiMcuZjTemplateConference.getId());
                                        conferenceContext.setAttendeeOperation(conferenceContext.getDefaultViewOperation());
                                        if (conferenceContext.getDefaultViewOperation().getDefaultViewIsBroadcast() == YesOrNo.YES.getValue()) {
                                            conferenceContext.setAttendeeOperationForGuest(conferenceContext.getDefaultViewOperation());
                                        } else {
                                            conferenceContext.setAttendeeOperationForGuest(conferenceContext.getDefaultViewOperationForGuest());
                                        }
                                        conferenceContext.setLastAttendeeOperationForGuest(conferenceContext.getDefaultViewOperationForGuest());
                                        conferenceContext.setStart(true);
                                        conferenceContext.setStartTime(busiHistoryConference.getConferenceStartTime());
                                        conferenceContext.setHistoryConference(busiHistoryConference);
                                        McuZjConferenceContextCache.getInstance().add(conferenceContext);
                                        conferenceStarted.add(conferenceNumber);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                        mcuZjBridge.getMcuZjLogger().logInfo("添加会议上下文失败", false, e);
                    }
                }

                Collection<McuZjConferenceContext> conferenceContextListByConferenceNumT = McuZjConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNumT != null) {
                    for (McuZjConferenceContext mcuZjConferenceContext : conferenceContextListByConferenceNumT) {
                        if (mcuZjConferenceContext.getMcuZjBridge().getBusiMcuZj().getIp().equals(mcuZjBridge.getBusiMcuZj().getIp())) {
                            conferenceContext = mcuZjConferenceContext;
                        }
                    }
                }

                String userName = mcuZjBridge.getBusiMcuZj().getUsername();
                if (userName.indexOf("@") > -1) {
                    userName = userName.substring(0, userName.indexOf("@"));
                }
                if (!userName.equals(launcherUsrName)) {
                    conferenceContext = null;
                }
                if (conferenceContext == null) {
                    CmStopMrRequest cmStopMrRequest = new CmStopMrRequest();
                    cmStopMrRequest.setMr_id(mrId);
                    cmStopMrRequest.setReason("系统结束会议");
                    boolean success = mcuZjBridge.getConferenceManageApi().stopMr(cmStopMrRequest);
                    if (success) {
                        // 删除会议室
                        McuZjDeleteRoomTask mcuZjDeleteRoomTask = new McuZjDeleteRoomTask(mcuZjBridge.getBusiMcuZj().getId() + "_" + conferenceNumber, 30000, mcuZjBridge, conferenceNumber);
                        delayTaskService.addTask(mcuZjDeleteRoomTask);
                    }
                }
            }

            for (McuZjConferenceContext mcuZjConferenceContext : McuZjConferenceContextCache.getInstance().values()) {
                if (!conferenceStarted.contains(mcuZjConferenceContext.getConferenceNumber())) {
                    McuZjBridge mcuZjBridgeTemp = McuZjBridgeCache.getInstance().getAvailableMcuZjBridgesByDept(mcuZjConferenceContext.getDeptId()).getMasterMcuZjBridge();
                    if (mcuZjBridgeTemp != null && mcuZjBridgeTemp.getBusiMcuZj().getId().longValue() == mcuZjBridge.getBusiMcuZj().getId().longValue()) {
                        if (!mcuZjConferenceContext.isEnd()) {
                            mcuZjConferenceContext.setEnd(true);
                            mcuZjConferenceContext.setEndTime(new Date());
                            busiMcuZjConferenceService.endConference(mcuZjConferenceContext.getContextKey(), ConferenceEndType.COMMON.getValue(), false, true);
                        }
                    }
                }
            }
        }
    }

    private void processStopMrResponse(CmStopMrResponse stopMrResponse, McuZjBridge mcuZjBridge) {
        IBusiMcuZjConferenceService busiMcuZjConferenceService = BeanFactory.getBean(IBusiMcuZjConferenceService.class);
        if (stopMrResponse != null && stopMrResponse.getMr_ids() != null) {
            for (int i = 0; i < stopMrResponse.getMr_ids().length; i++) {
                String mrId = stopMrResponse.getMr_ids()[i];
                String conferenceNumber = mrId.substring(mcuZjBridge.getTenantId().length());
                McuZjConferenceContext conferenceContext = null;
                Collection<McuZjConferenceContext> conferenceContextListByConferenceNum = McuZjConferenceContextCache.getInstance().getConferenceContextListByConferenceNum(conferenceNumber);
                if (conferenceContextListByConferenceNum != null) {
                    for (McuZjConferenceContext mcuZjConferenceContext : conferenceContextListByConferenceNum) {
                        if (mcuZjConferenceContext.getMcuZjBridge().getBusiMcuZj().getIp().equals(mcuZjBridge.getBusiMcuZj().getIp())) {
                            conferenceContext = mcuZjConferenceContext;
                        }
                    }
                }
                conferenceContext.getMcuIp();
                if (conferenceContext != null) {
                    conferenceContext.setEnd(true);
                    conferenceContext.setEndTime(new Date());
                    busiMcuZjConferenceService.endConference(conferenceContext.getContextKey(), ConferenceEndType.COMMON.getValue(), false, true);
                }
            }
        }
    }
}
