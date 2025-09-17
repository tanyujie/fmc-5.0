package com.paradisecloud.fcm.tencent.scheduler;

import com.paradisecloud.fcm.common.constant.EndReasonsType;
import com.paradisecloud.fcm.common.enumer.ConferenceEndType;
import com.paradisecloud.fcm.dao.model.BusiMcuTencentConferenceAppointment;
import com.paradisecloud.fcm.tencent.cache.TencentBridge;
import com.paradisecloud.fcm.tencent.cache.TencentBridgeCache;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContext;
import com.paradisecloud.fcm.tencent.cache.TencentConferenceContextCache;
import com.paradisecloud.fcm.tencent.model.TencentMeetingStateEnum;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.request.TencentDismissMeetingRequest;
import com.paradisecloud.fcm.tencent.service2.interfaces.IBusiTencentConferenceService;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.ThreadUtils;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.models.meeting.CancelMeetingRequest;
import com.tencentcloudapi.wemeet.models.meeting.MeetingInfo;
import com.tencentcloudapi.wemeet.models.meeting.QueryMeetingByIdRequest;
import com.tencentcloudapi.wemeet.models.meeting.QueryMeetingDetailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * @author nj
 * @date 2023/7/24 14:14
 */
@Component
public class TencentConferenceContextMonitorThread extends Thread implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void run() {

        while (true) {
            try {
                Collection<TencentConferenceContext> values = TencentConferenceContextCache.getInstance().values();
                if (CollectionUtils.isEmpty(values)) {
                    return;
                }
                for (TencentConferenceContext value : values) {
                    if (value != null && value.isStart()) {
                        QueryMeetingByIdRequest queryMeetingByIdRequest = new QueryMeetingByIdRequest();
                        queryMeetingByIdRequest.setMeetingId(value.getId());
                        queryMeetingByIdRequest.setInstanceId(1);
                        queryMeetingByIdRequest.setUserId(value.getTencentUser());
                        TencentMeetingClient meeting_client = value.getTencentBridge().getMEETING_CLIENT();
                        QueryMeetingDetailResponse queryMeetingDetailResponse = meeting_client.queryMeetingById(queryMeetingByIdRequest);
                        List<MeetingInfo> meetingInfoList = queryMeetingDetailResponse.getMeetingInfoList();
                        if (CollectionUtils.isEmpty(meetingInfoList)) {
                            return;
                        }
                        MeetingInfo meetingInfo = meetingInfoList.get(0);
                        String meetingInfoStatus = meetingInfo.getStatus();
                        if (Objects.equals(meetingInfoStatus, TencentMeetingStateEnum.MEETING_STATE_STARTED.name()) ||
                                Objects.equals(meetingInfoStatus, TencentMeetingStateEnum.MEETING_STATE_INIT.name())) {

                            if(value.isAppointment()){
                                Date endTime = value.getEndTime();
                                if(endTime.getTime() < System.currentTimeMillis()){
                                    IBusiTencentConferenceService tencentConferenceService = BeanFactory.getBean(IBusiTencentConferenceService.class);
                                    tencentConferenceService.endConference(value.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.ABNORMAL_END);
                                }
                            }

                        } else {
                            IBusiTencentConferenceService tencentConferenceService = BeanFactory.getBean(IBusiTencentConferenceService.class);
                            tencentConferenceService.endConference(value.getId(), ConferenceEndType.COMMON.getValue(), EndReasonsType.ABNORMAL_END);
                        }
                    }
                }

            } catch (Throwable e) {
                logger.error("会议状态检查错误", e);
            } finally {
                ThreadUtils.sleep(10000);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }
}
