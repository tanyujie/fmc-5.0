/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : BookingConferenceTask.java
 * Package     : com.paradisecloud.fcm.fme.conference.task
 * @author lilinhai
 * @since 2021-05-20 18:47
 * @version  V1.0
 */
package com.paradisecloud.fcm.ops.monitor;


import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.ConferenceNumberCreateType;
import com.paradisecloud.fcm.common.enumer.ConferenceTemplateCreateType;
import com.paradisecloud.fcm.common.utils.PathUtil;
import com.paradisecloud.fcm.common.utils.PropertiesUtil;
import com.paradisecloud.fcm.dao.mapper.BusiCallLegProfileMapper;
import com.paradisecloud.fcm.dao.mapper.BusiOpsInfoMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTemplateConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiCallLegProfile;
import com.paradisecloud.fcm.dao.model.BusiConferenceNumber;
import com.paradisecloud.fcm.dao.model.BusiOpsInfo;
import com.paradisecloud.fcm.dao.model.BusiTemplateConference;
import com.paradisecloud.fcm.fme.apiservice.interfaces.IBusiCallLegProfileService;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.FmeBridgeCollection;
import com.paradisecloud.fcm.fme.conference.interfaces.IBusiTemplateConferenceService;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.response.calllegprofile.CallLegProfileInfoResponse;
import com.paradisecloud.fcm.ops.utils.IpUtil;
import com.paradisecloud.fcm.service.interfaces.IBusiConferenceNumberService;
import com.paradisecloud.system.dao.model.SysDept;
import com.paradisecloud.system.dao.model.SysUser;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;


//@Component
public class ConferenceTemplateScheduler extends Thread implements InitializingBean {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private BusiOpsInfoMapper busiOpsInfoMapper;

    @Resource
    private BusiTemplateConferenceMapper busiTemplateConferenceMapper;

    @Resource
    private IBusiConferenceNumberService busiConferenceNumberService;

    @Resource
    private IBusiTemplateConferenceService busiTemplateConferenceService;

    @Override
    public void run() {
        logger.info("固定模板调度器启动成功！");
        String filePath = PathUtil.getRootPath() + "/external_config.properties";
        Properties properties = PropertiesUtil.readProperties(filePath);
        String region = properties.getProperty("region");
        if(!Objects.equals(region,"ops")){
            return;
        }
        ThreadUtils.sleep(3 * 1000);
        long startTime = System.currentTimeMillis();
        while (true) {
            long endTime = System.currentTimeMillis();
            if(endTime-startTime>1000*1000*200){
                logger.info("ConferenceTemplateScheduler超时退出 运行时间："+(endTime-startTime)+"ms");
                break;
            }

            try {
                FmeBridgeCollection fmeBridgeCollection = FmeBridgeCache.getInstance().getAvailableFmeBridgesByDept(100L);
                if (fmeBridgeCollection != null && CollectionUtils.isNotEmpty(fmeBridgeCollection.getFmeBridges())) {
                    List<FmeBridge> fmeBridges = fmeBridgeCollection.getFmeBridges();
                    for (FmeBridge fmeBridge : fmeBridges) {
                        List<BusiOpsInfo> busiOpsInfos = busiOpsInfoMapper.selectBusiOpsInfoList(new BusiOpsInfo());
                        if (CollectionUtils.isNotEmpty(busiOpsInfos)) {
                            BusiOpsInfo busiOpsInfo = busiOpsInfos.get(0);
                            if (Objects.equals(fmeBridge.getBusiFme().getIp(), busiOpsInfo.getFmeIp())) {
                                //非静音入会
                                notMuteTemp(fmeBridge);
                                //静音入会
                                muteTemp();
                                //四分屏入会
                                allEqualQuartersTemp();
                                return;
                            }
                        }

                    }
                }

            } catch (Throwable e) {
                logger.error("固定模板调度器调度出错", e);
                break;
            } finally {
                ThreadUtils.sleep(1000);
            }
        }
    }

    private void allEqualQuartersTemp() {
        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        //静音入会
        busiTemplateConference.setConferenceNumber(88888L);
        busiTemplateConference.setDeptId(100L);
        List<BusiTemplateConference> busiTemplateConferences = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
        if (!CollectionUtils.isNotEmpty(busiTemplateConferences)) {
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(88888L);
            if (busiConferenceNumber == null) {
                BusiConferenceNumber busiConferenceNumber1 = new BusiConferenceNumber();
                busiConferenceNumber1.setId(88888L);
                busiConferenceNumber1.setDeptId(100L);
                busiConferenceNumber1.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
                busiConferenceNumber1.setMcuType("fme");
                int i = busiConferenceNumberService.insertBusiConferenceNumber(busiConferenceNumber1);
            }


            //创建模板：
            BusiTemplateConference templateConference = new BusiTemplateConference();
            templateConference.setConferenceNumber(88888L);
            templateConference.setName("四分屏入会");
            templateConference.setDeptId(100L);
            templateConference.setIsAutoCall(2);
            templateConference.setBandwidth(2);
            templateConference.setBusinessFieldType(100);
            templateConference.setDurationEnabled(0);
            templateConference.setIsAutoCreateConferenceNumber(2);
            templateConference.setIsAutoCreateStreamUrl(1);
            templateConference.setViewType(1);
            templateConference.setCreateUserId(1L);
            templateConference.setCreateUserName("superAdmin");
            templateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

            int c = busiTemplateConferenceService.insertBusiTemplateConferenceOps(templateConference, null, new ArrayList<>(), new ArrayList<>());
            if (c > 0) {
                // 分屏
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("defaultViewLayout", "allEqualQuarters");
                    jsonObj.put("defaultViewIsBroadcast", 1);
                    jsonObj.put("defaultViewIsDisplaySelf", 1);
                    jsonObj.put("defaultViewIsFill", 1);
                    jsonObj.put("pollingInterval", 10);
                    jsonObj.put("defaultViewCellScreens", new ArrayList<>());
                    jsonObj.put("defaultViewDepts", new ArrayList<>());
                    jsonObj.put("defaultViewPaticipants", new ArrayList<>());
                    busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, templateConference.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

            }
        }
    }

    private void muteTemp() {
        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        //静音入会
        busiTemplateConference.setConferenceNumber(88886L);
        busiTemplateConference.setDeptId(100L);
        List<BusiTemplateConference> busiTemplateConferences = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
        if (!CollectionUtils.isNotEmpty(busiTemplateConferences)) {
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(88886L);
            if (busiConferenceNumber == null) {
                BusiConferenceNumber busiConferenceNumber1 = new BusiConferenceNumber();
                busiConferenceNumber1.setId(88886L);
                busiConferenceNumber1.setDeptId(100L);
                busiConferenceNumber1.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
                busiConferenceNumber1.setMcuType("fme");
                int i = busiConferenceNumberService.insertBusiConferenceNumber(busiConferenceNumber1);
            }


            //创建模板：
            BusiTemplateConference templateConference = new BusiTemplateConference();
            templateConference.setConferenceNumber(88886L);
            templateConference.setName("静音入会");
            templateConference.setDeptId(100L);
            templateConference.setIsAutoCall(2);
            templateConference.setBandwidth(2);
            templateConference.setBusinessFieldType(100);
            templateConference.setDurationEnabled(0);
            templateConference.setIsAutoCreateConferenceNumber(2);
            templateConference.setIsAutoCreateStreamUrl(1);
            templateConference.setViewType(1);
            templateConference.setCreateUserId(1L);
            templateConference.setCreateUserName("superAdmin");
            templateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

            int c = busiTemplateConferenceService.insertBusiTemplateConferenceOps(templateConference, null, new ArrayList<>(), new ArrayList<>());
            if (c > 0) {
                // 分屏
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("defaultViewLayout", "allEqual");
                    jsonObj.put("defaultViewIsBroadcast", 1);
                    jsonObj.put("defaultViewIsDisplaySelf", -1);
                    jsonObj.put("pollingInterval", 10);
                    jsonObj.put("defaultViewIsFill", -1);
                    jsonObj.put("defaultViewCellScreens", new ArrayList<>());
                    jsonObj.put("defaultViewDepts", new ArrayList<>());
                    jsonObj.put("defaultViewPaticipants", new ArrayList<>());
                    busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, templateConference.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

            }
        }
    }

    private void notMuteTemp(FmeBridge fmeBridge) {
        BusiTemplateConference busiTemplateConference = new BusiTemplateConference();
        //非静音入会
        busiTemplateConference.setConferenceNumber(88887L);
        List<BusiTemplateConference> busiTemplateConferences = busiTemplateConferenceMapper.selectBusiTemplateConferenceList(busiTemplateConference);
        if (CollectionUtils.isNotEmpty(busiTemplateConferences)) {
            for (BusiTemplateConference templateConference : busiTemplateConferences) {
                String callLegProfileId = templateConference.getCallLegProfileId();
                //查询callLegProfileId
                CallLegProfileInfoResponse callLegProfile = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(callLegProfileId);
                if (callLegProfile == null) {
                    String defaultCalllegProfileIsMute = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfileIsMute(fmeBridge, 100L, false);
                    templateConference.setCallLegProfileId(defaultCalllegProfileIsMute);
                    busiTemplateConferenceMapper.updateBusiTemplateConference(templateConference);

                    BusiCallLegProfile con = new BusiCallLegProfile();
                    con.setDeptId(100L);
                    con.setCallLegProfileUuid(defaultCalllegProfileIsMute);
                    con.setFmeId(fmeBridge.getBusiFme().getId());
                    con.setCreateTime(new Date());
                    BusiCallLegProfileMapper busiCallLegProfileMapper = BeanFactory.getBean(BusiCallLegProfileMapper.class);
                    busiCallLegProfileMapper.insertBusiCallLegProfile(con);
                }

            }
        } else {
            //创建非静音入会模板 全等分屏

            //1查询会议号码存在不
            BusiConferenceNumber busiConferenceNumber = busiConferenceNumberService.selectBusiConferenceNumberById(88887L);
            if (busiConferenceNumber == null) {
                BusiConferenceNumber busiConferenceNumber1 = new BusiConferenceNumber();
                busiConferenceNumber1.setId(88887L);
                busiConferenceNumber1.setDeptId(100L);
                busiConferenceNumber1.setCreateType(ConferenceNumberCreateType.MANUAL.getValue());
                busiConferenceNumber1.setMcuType("fme");
                int i = busiConferenceNumberService.insertBusiConferenceNumber(busiConferenceNumber1);


            }

            //入会方案检查
            String calllegprofileId = "";
            BusiCallLegProfile con = new BusiCallLegProfile();
            con.setDeptId(100L);
            List<BusiCallLegProfile> clps = BeanFactory.getBean(IBusiCallLegProfileService.class).selectBusiCallLegProfileList(con);
            if (!ObjectUtils.isEmpty(clps)) {
                for (BusiCallLegProfile busiCallLegProfile : clps) {
                    CallLegProfile clp = fmeBridge.getDataCache().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                    if (clp != null) {
                        Boolean rxAudioMute = clp.getRxAudioMute();
                        if (rxAudioMute != null && rxAudioMute.equals(false)) {
                            calllegprofileId = clp.getId();
                            break;
                        }
                    } else {
                        CallLegProfileInfoResponse callLegProfile = fmeBridge.getCallLegProfileInvoker().getCallLegProfile(busiCallLegProfile.getCallLegProfileUuid());
                        if (callLegProfile != null) {
                            if (callLegProfile.getCallLegProfile() != null) {

                                if (!callLegProfile.getCallLegProfile().getRxAudioMute()) {
                                    calllegprofileId = callLegProfile.getCallLegProfile().getId();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (Strings.isBlank(calllegprofileId)) {
                calllegprofileId = BeanFactory.getBean(IBusiCallLegProfileService.class).createDefaultCalllegProfileIsMute(fmeBridge, 100L, false);
            }
            //创建模板：

            BusiTemplateConference templateConference = new BusiTemplateConference();
            templateConference.setConferenceNumber(88887L);
            templateConference.setName("非静音入会");
            templateConference.setCallLegProfileId(calllegprofileId);
            templateConference.setDeptId(100L);
            templateConference.setIsAutoCall(2);
            templateConference.setBandwidth(2);
            templateConference.setBusinessFieldType(100);
            templateConference.setDurationEnabled(0);
            templateConference.setIsAutoCreateConferenceNumber(2);
            templateConference.setIsAutoCreateStreamUrl(1);
            templateConference.setViewType(1);
            templateConference.setCreateUserId(1L);
            templateConference.setCreateUserName("superAdmin");
            templateConference.setCreateType(ConferenceTemplateCreateType.MANUAL.getValue());

            int c = busiTemplateConferenceService.insertBusiTemplateConferenceOps(templateConference, null, new ArrayList<>(), new ArrayList<>());
            if (c > 0) {
                // 分屏
                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("defaultViewLayout", "allEqual");
                    jsonObj.put("defaultViewIsBroadcast", 1);
                    jsonObj.put("defaultViewIsDisplaySelf", -1);
                    jsonObj.put("defaultViewIsFill", -1);
                    jsonObj.put("pollingInterval", 10);
                    jsonObj.put("defaultViewCellScreens", new ArrayList<>());
                    jsonObj.put("defaultViewDepts", new ArrayList<>());
                    jsonObj.put("defaultViewPaticipants", new ArrayList<>());
                    busiTemplateConferenceService.updateDefaultViewConfigInfo(jsonObj, templateConference.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
