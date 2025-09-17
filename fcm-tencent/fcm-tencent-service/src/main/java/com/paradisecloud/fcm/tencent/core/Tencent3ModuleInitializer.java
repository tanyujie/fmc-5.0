package com.paradisecloud.fcm.tencent.core;

import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.dao.mapper.BusiHistoryConferenceMapper;
import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.tencent.cache.*;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentDeptMapper;
import com.paradisecloud.fcm.dao.mapper.BusiMcuTencentMapper;
import com.paradisecloud.fcm.dao.model.BusiMcuTencent;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentDept;
import com.paradisecloud.fcm.dao.model.BusiTencentTemplateConference;
import com.paradisecloud.fcm.tencent.model.TencentMeetingStateEnum;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.client.TencentUserClient;
import com.paradisecloud.fcm.tencent.model.reponse.QueryUserMsOpenIdResponse;
import com.paradisecloud.fcm.tencent.model.request.QueryUserMsOpenIdRequest;
import com.paradisecloud.fcm.tencent.model.request.TencentDismissMeetingRequest;
import com.paradisecloud.fcm.tencent.monitor.RealTimeParticipantsThread;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiMcuTencentHistoryConferenceService;
import com.paradisecloud.fcm.tencent.templateConference.BuildTemplateConferenceContext;
import com.sinhy.spring.BeanFactory;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.meeting.CancelMeetingRequest;
import com.tencentcloudapi.wemeet.models.meeting.MeetingInfo;
import com.tencentcloudapi.wemeet.models.meeting.QueryUserMeetingsRequest;
import com.tencentcloudapi.wemeet.models.meeting.QueryUserMeetingsResponse;
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
import java.util.Objects;

/**
 * 登录保活
 *
 * @author nj
 * @date 2023/4/21 17:18
 */
@Component
@Order(1)
public class Tencent3ModuleInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tencent3ModuleInitializer.class);

    @Resource
    BusiMcuTencentDeptMapper busiMcuTencentDeptMapper;
    @Resource
    BusiMcuTencentMapper busiMcuTencentMapper;

    @Resource
    IBusiMcuTencentHistoryConferenceService busiTencentHistoryConferenceService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        LOGGER.info("腾讯[Tencent3ModuleInitializer]启动成功！");
        initDept();
        initBridge();
       // syncConference();
       //endConference();



    }

    private void endConference() throws WemeetSdkException {
        TencentBridge tencentBridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(1L);

        QueryUserMeetingsRequest queryMeetingByIdRequest = new QueryUserMeetingsRequest();
        queryMeetingByIdRequest.setInstanceId(1);
        queryMeetingByIdRequest.setUserId(tencentBridge.getTencentUserId());
        TencentMeetingClient meeting_client = tencentBridge.getMEETING_CLIENT();
        QueryUserMeetingsResponse queryMeetingDetailResponse = meeting_client.queryUserMeetings(queryMeetingByIdRequest);
        List<MeetingInfo> meetingInfoList = queryMeetingDetailResponse.getMeetingInfoList();

        if (!CollectionUtils.isEmpty(meetingInfoList)) {
            for (MeetingInfo meetingInfo : meetingInfoList) {

                if (Objects.equals(meetingInfo.getStatus(), TencentMeetingStateEnum.MEETING_STATE_STARTED.name()) ||
                        Objects.equals(meetingInfo.getStatus(), TencentMeetingStateEnum.MEETING_STATE_INIT.name())) {

                    try {
                        TencentDismissMeetingRequest dismissMeetingRequest = new TencentDismissMeetingRequest();
                        dismissMeetingRequest.setMeetingId(meetingInfo.getMeetingId());
                        dismissMeetingRequest.setUserId(tencentBridge.getTencentUserId());
                        dismissMeetingRequest.setInstanceId(1);
                        dismissMeetingRequest.setReasonCode(1);
                        meeting_client.dismissMeeting(dismissMeetingRequest);
                    } catch (WemeetSdkException e) {
                        LOGGER.info(e.getMessage());
                    }

                    try {
                        CancelMeetingRequest cancelMeetingRequest = new CancelMeetingRequest();
                        cancelMeetingRequest.setMeetingId(meetingInfo.getMeetingId());
                        cancelMeetingRequest.setUserId(tencentBridge.getTencentUserId());
                        cancelMeetingRequest.setInstanceId(1);
                        cancelMeetingRequest.setReasonCode(1);
                        meeting_client.cancelMeeting(cancelMeetingRequest);
                    } catch (Exception e) {
                        LOGGER.info(e.getMessage());
                    }
                }
            }
        }
        Integer nextPos = queryMeetingDetailResponse.getNextPos();
        if (nextPos != null && nextPos > 0) {
            queryMeetingByIdRequest.setPos(nextPos);
            endConference();
        }
    }


    private void initDept() {
        List<BusiMcuTencentDept> busiTencentDepts = busiMcuTencentDeptMapper.selectBusiMcuTencentDeptList(new BusiMcuTencentDept());
        if (!CollectionUtils.isEmpty(busiTencentDepts)) {
            for (BusiMcuTencentDept busiTencentDept : busiTencentDepts) {
                DeptTencentMappingCache.getInstance().put(busiTencentDept.getDeptId(), busiTencentDept);
            }
        }

    }

    private void initBridge() {
        List<BusiMcuTencent> busiTencents = busiMcuTencentMapper.selectBusiMcuTencentList(new BusiMcuTencent());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(busiTencents)) {
            for (BusiMcuTencent busiTencent : busiTencents) {
                TencentBridge tencentBridge = new TencentBridge(busiTencent);
                TencentBridgeCache.getInstance().init(tencentBridge);
            }
        }

    }

    private void syncConference() {
        try {

//            List<BusiTencentHistoryConference> busiTencentHistoryConferences = busiTencentHistoryConferenceService.selectBusiTencentHistoryConferenceNotTemplate(null, null);
//            for (BusiTencentHistoryConference busiTencentHistoryConference : busiTencentHistoryConferences) {
//                BusiTencentTemplateConferenceMapper busiTencentTemplateConferenceMapper = BeanFactory.getBean(BusiTencentTemplateConferenceMapper.class);
//                // 获取模板会议实体对象
//                BusiTencentTemplateConference ttc = busiTencentTemplateConferenceMapper.selectBusiTencentTemplateConferenceById(busiTencentHistoryConference.getTemplateId().longValue());
//                if(ttc==null){
//                    continue;
//                }
//                String confId = ttc.getMeetingId();
//                if (confId != null) {
//                    TencentConferenceContext conferenceContext = TencentConferenceContextCache.getInstance().get(AesEnsUtils.getAesEncryptor().encryptToHex(confId));
//                    if (conferenceContext == null) {
//                        //查询会议状态
//                        TencentBridge availableBridge = TencentBridgeCache.getInstance().getAvailableBridgesByDept(ttc.getDeptId());
//                        if(availableBridge==null){
//                            continue;
//                        }
//                        TencentMeetingClient meeting_client = availableBridge.getMEETING_CLIENT();
//                        QueryMeetingByIdRequest queryMeetingByIdRequest = new QueryMeetingByIdRequest();
//                        queryMeetingByIdRequest.setMeetingId(confId);
//                        queryMeetingByIdRequest.setInstanceId(1);
//                        queryMeetingByIdRequest.setUserId(availableBridge.getTencentUserId());
//                        QueryMeetingDetailResponse queryMeetingDetailResponse = meeting_client.queryMeetingById(queryMeetingByIdRequest);
//
//                        List<MeetingInfo> meetingInfoList = queryMeetingDetailResponse.getMeetingInfoList();
//                        if(CollectionUtils.isEmpty(meetingInfoList)){
//                            busiTencentHistoryConference.setEndStatus(1L);
//                            busiTencentHistoryConference.setEndTime(new Date());
//                            busiTencentHistoryConferenceService.updateBusiTencentHistoryConference(busiTencentHistoryConference);
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
//                        if(Objects.equals(meetingInfoStatus, TencentMeetingStateEnum.MEETING_STATE_STARTED.name())||
//                                Objects.equals(meetingInfoStatus, TencentMeetingStateEnum.MEETING_STATE_INIT.name())){
//                            BuildTemplateConferenceContext buildTemplateConferenceContext = new BuildTemplateConferenceContext();
//                            try {
//                                syncLocal(ttc, meetingInfo, buildTemplateConferenceContext);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                LOGGER.error("tencent 会议同步错误："+e.getMessage());
//
//                            }
//
//
//                        }else {
//                            endStatus(busiTencentHistoryConference, meetingInfo);
//
//                        }
//
//                    }
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("tencent 会议同步错误：" + e.getMessage());
        }
    }

    private void endStatus(IBusiMcuTencentHistoryConferenceService busiTencentHistoryConference, MeetingInfo meetingInfo) {
//        busiTencentHistoryConference.setEndStatus(1L);
//        String endTime = meetingInfo.getEndTime();
//        String startTime = meetingInfo.getStartTime();
//        Long endLong = Long.valueOf(endTime);
//        Long startLong = Long.valueOf(startTime);
//        Long duration =  (endLong- startLong)/60;
//
//        busiTencentHistoryConference.setEndTime(TimeStamp2Date(endTime));
//        busiTencentHistoryConferenceService.updateBusiHistoryParticipant(busiTencentHistoryConference);
//        BusiHistoryConferenceMapper busiHistoryConferenceMapper = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
//        BusiHistoryConference busiHistoryConference = new BusiHistoryConference();
//        busiHistoryConference.setNumber(meetingInfo.getMeetingCode());
//        List<BusiHistoryConference> busiHistoryConferences = busiHistoryConferenceMapper.selectBusiHistoryConferenceList(busiHistoryConference);
//        if(!CollectionUtils.isEmpty(busiHistoryConferences)){
//            BusiHistoryConference busiHistoryConference1 = busiHistoryConferences.get(0);
//            busiHistoryConference1.setEndReasonsType(EndReasonsType.ABNORMAL_END);
//            busiHistoryConference1.setConferenceEndTime(TimeStamp2Date(endTime));
//            busiHistoryConference1.setUpdateTime(new Date());
//            busiHistoryConference1.setDuration(duration.intValue());
//            busiHistoryConferenceMapper.updateBusiHistoryConference(busiHistoryConference1);
//        }
    }

    private void syncLocal(BusiTencentTemplateConference ttc, MeetingInfo meetingInfo, BuildTemplateConferenceContext buildTemplateConferenceContext) {
        TencentConferenceContext conferenceContext = buildTemplateConferenceContext.buildTemplateConferenceContext(ttc.getId());

        conferenceContext.setStart(true);
        Date startDate = TimeStamp2Date(meetingInfo.getStartTime());
        conferenceContext.setStartTime(startDate);
        conferenceContext.setMeetingId(meetingInfo.getMeetingId());
        conferenceContext.setConferenceNumber(meetingInfo.getMeetingCode());
        conferenceContext.setName(meetingInfo.getSubject());
        conferenceContext.setJoinUrl(meetingInfo.getJoinUrl());
        conferenceContext.setAccessCode(meetingInfo.getMeetingCode());
        String startTimeStr = meetingInfo.getStartTime();

        if (!Objects.isNull(meetingInfo.getSettings())) {
            conferenceContext.setSettings(JSONObject.toJSONString(meetingInfo.getSettings()));
        }
        TencentUserClient user_client = conferenceContext.getTencentBridge().getUSER_CLIENT();
        QueryUserMsOpenIdRequest queryUserMsOpenIdRequest = new QueryUserMsOpenIdRequest();
        queryUserMsOpenIdRequest.setMeetingId(conferenceContext.getMeetingId());
        queryUserMsOpenIdRequest.setOperatorId(conferenceContext.getTencentUser());
        queryUserMsOpenIdRequest.setOperatorIdType(1);
        try {
            QueryUserMsOpenIdResponse msOpenIdResponse = user_client.queryUserMsOpenId(queryUserMsOpenIdRequest);
            String msOpenId = msOpenIdResponse.getMsOpenId();
            conferenceContext.setMsopenid(msOpenId);
        } catch (WemeetSdkException e) {
            e.printStackTrace();
        }

        // 保存历史记录
        BusiHistoryConferenceMapper busiHistoryConference = BeanFactory.getBean(BusiHistoryConferenceMapper.class);
        BusiHistoryConference condition = new BusiHistoryConference();
        condition.setCallLegProfileId(conferenceContext.getTemplateConferenceId().toString());
        condition.setName(conferenceContext.getName());
        List<BusiHistoryConference> busiHistoryConferences = busiHistoryConference.selectBusiHistoryConferenceList(condition);
        conferenceContext.setHistoryConference(busiHistoryConferences.get(0));


        TencentConferenceContextCache.getInstance().add(conferenceContext);

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
