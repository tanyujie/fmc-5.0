package com.paradisecloud.fcm.huaweicloud.huaweicloud.core;

import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuHwcloudMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloud;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloudDept;

import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.DeptHwcloudMappingCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudBridgeCache;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.service.interfaces.IBusiMcuHwcloudHistoryConferenceService;

import com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client.HwcloudMeetingWebsocketReconnecter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 登录保活
 *
 * @author nj
 * @date 2023/4/21 17:18
 */
@Component
@Order(1)
public class HwcloudModuleInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HwcloudModuleInitializer.class);

    @Resource
    BusiMcuHwcloudDeptMapper busiMcuHwcloudDeptMapper;
    @Resource
    BusiMcuHwcloudMapper busiMcuHwcloudMapper;

    @Resource
    IBusiMcuHwcloudHistoryConferenceService busiHwcloudHistoryConferenceService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        LOGGER.info("腾讯[Hwcloud3ModuleInitializer]启动成功！");
        initDept();
        initBridge();
        HwcloudMeetingWebsocketReconnecter.getInstance().start();
       // syncConference();
      // endConference();



    }

    private void endConference() throws Exception {
        HwcloudBridge HwcloudBridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(1L);


    }


    private void initDept() {
        List<BusiMcuHwcloudDept> busiHwcloudDepts = busiMcuHwcloudDeptMapper.selectBusiMcuHwcloudDeptList(new BusiMcuHwcloudDept());
        if (!CollectionUtils.isEmpty(busiHwcloudDepts)) {
            for (BusiMcuHwcloudDept busiHwcloudDept : busiHwcloudDepts) {
                DeptHwcloudMappingCache.getInstance().put(busiHwcloudDept.getDeptId(), busiHwcloudDept);
            }
        }

    }

    private void initBridge() {
        List<BusiMcuHwcloud> busiHwclouds = busiMcuHwcloudMapper.selectBusiMcuHwcloudList(new BusiMcuHwcloud());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiHwclouds)) {
            for (BusiMcuHwcloud busiHwcloud : busiHwclouds) {
                HwcloudBridge HwcloudBridge = new HwcloudBridge(busiHwcloud);
                if (HwcloudBridge.isAvailable()) {
                    HwcloudBridgeCache.getInstance().init(HwcloudBridge);
                }
            }
        }

    }

    private void syncConference() {
        try {

//            List<BusiHwcloudHistoryConference> busiHwcloudHistoryConferences = busiHwcloudHistoryConferenceService.selectBusiHwcloudHistoryConferenceNotTemplate(null, null);
//            for (BusiHwcloudHistoryConference busiHwcloudHistoryConference : busiHwcloudHistoryConferences) {
//                BusiHwcloudTemplateConferenceMapper busiHwcloudTemplateConferenceMapper = BeanFactory.getBean(BusiHwcloudTemplateConferenceMapper.class);
//                // 获取模板会议实体对象
//                BusiHwcloudTemplateConference ttc = busiHwcloudTemplateConferenceMapper.selectBusiHwcloudTemplateConferenceById(busiHwcloudHistoryConference.getTemplateId().longValue());
//                if(ttc==null){
//                    continue;
//                }
//                String confId = ttc.getMeetingId();
//                if (confId != null) {
//                    HwcloudConferenceContext conferenceContext = HwcloudConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().encryptToHex(confId));
//                    if (conferenceContext == null) {
//                        //查询会议状态
//                        HwcloudBridge availableBridge = HwcloudBridgeCache.getInstance().getAvailableBridgesByDept(ttc.getDeptId());
//                        if(availableBridge==null){
//                            continue;
//                        }
//                        HwcloudMeetingClient meeting_client = availableBridge.getMEETING_CLIENT();
//                        QueryMeetingByIdRequest queryMeetingByIdRequest = new QueryMeetingByIdRequest();
//                        queryMeetingByIdRequest.setMeetingId(confId);
//                        queryMeetingByIdRequest.setInstanceId(1);
//                        queryMeetingByIdRequest.setUserId(availableBridge.getHwcloudUserId());
//                        QueryMeetingDetailResponse queryMeetingDetailResponse = meeting_client.queryMeetingById(queryMeetingByIdRequest);
//
//                        List<MeetingInfo> meetingInfoList = queryMeetingDetailResponse.getMeetingInfoList();
//                        if(CollectionUtils.isEmpty(meetingInfoList)){
//                            busiHwcloudHistoryConference.setEndStatus(1L);
//                            busiHwcloudHistoryConference.setEndTime(new Date());
//                            busiHwcloudHistoryConferenceService.updateBusiHwcloudHistoryConference(busiHwcloudHistoryConference);
//                            BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
//                            BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
//                            busiHistoryConference.setNumber(confId);
//                            List<BusiHistoryConference> busiHistoryConferences = busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
//                            if(!CollectionUtils.isEmpty(busiHistoryConferences)){
//                                BusiHistoryConference busiHistoryConference1 = busiHistoryConferences.get(0);
//                                busiHistoryConference1.setEndReasonsType(EndReasonsType.ABNORMAL_END);
//                                busiHistoryConference1.setConferenceEndTime(new Date());
//                                busiHistoryConference1.setUpdateTime(new Date());
//                                busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference1);
//                            }
//
//                            continue;
//                        }
//                        MeetingInfo meetingInfo = meetingInfoList.get(0);
//
//                        String meetingInfoStatus = meetingInfo.getStatus();
//                        if(Objects.equals(meetingInfoStatus, HwcloudMeetingStateEnum.MEETING_STATE_STARTED.name())||
//                                Objects.equals(meetingInfoStatus, HwcloudMeetingStateEnum.MEETING_STATE_INIT.name())){
//                            BuildTemplateConferenceContext buildTemplateConferenceContext = new BuildTemplateConferenceContext();
//                            try {
//                                syncLocal(ttc, meetingInfo, buildTemplateConferenceContext);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                LOGGER.error("Hwcloud 会议同步错误："+e.getMessage());
//
//                            }
//
//
//                        }else {
//                            endStatus(busiHwcloudHistoryConference, meetingInfo);
//
//                        }
//
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Hwcloud 会议同步错误：" + e.getMessage());
        }
    }





    public Date TimeStamp2Date(String timestampString) {
        String formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        //日期格式字符串
        String dateStr = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern("yyyy-MM-dd HH:mm:ss");
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
