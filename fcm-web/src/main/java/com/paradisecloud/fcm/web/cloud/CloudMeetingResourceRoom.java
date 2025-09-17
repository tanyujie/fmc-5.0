package com.paradisecloud.fcm.web.cloud;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cloud.HuaWeiCloudMeetingService;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.tencent.cloud.TencentCloudMeetingService;
import com.sinhy.spring.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CloudMeetingResourceRoom {

    private static Logger logger= LoggerFactory.getLogger(CloudMeetingResourceRoom.class);

    private List<BaseConferenceContext> meetings;
    private int maxCapacity;

    private int maxTimeCapacity;

    private BookingStrategy bookingStrategy;

    public CloudMeetingResourceRoom(int maxCapacity, int maxTimeCapacity) {
        this.meetings = new ArrayList<>();
        this.maxCapacity = maxCapacity;
        this.maxTimeCapacity=maxTimeCapacity;
    }

    public BaseConferenceContext bookMeeting(String name,String cloudMcuType, int duration) {
        int canUseDuration=0;
        if (meetings.size() < maxCapacity) {
            int totalUsageTime = getTotalUsageTime();
            if(totalUsageTime<maxTimeCapacity){

                if(maxTimeCapacity-totalUsageTime>=duration){
                    canUseDuration=duration;
                }else {
                    canUseDuration=maxTimeCapacity-totalUsageTime;
                }
            }
            if(canUseDuration<=0){
                throw new CustomException("无可使用时长,无法预订新会议！");
            }

            McuType mcuType =McuType.valueOf(cloudMcuType);
            switch (mcuType) {
                case MCU_TENCENT : {
                    TencentCloudMeetingService tencentCloudMeetingService = BeanFactory.getBean(TencentCloudMeetingService.class);
                    BaseConferenceContext meeting = tencentCloudMeetingService.createMeeting(name, canUseDuration);
                    if(meeting!=null){
                        meetings.add(meeting);
                    }
                    break;
                }
                case MCU_HWCLOUD:{
                    HuaWeiCloudMeetingService huaWeiCloudMeetingService = BeanFactory.getBean(HuaWeiCloudMeetingService.class);
                    BaseConferenceContext meeting = huaWeiCloudMeetingService.createMeeting(name, canUseDuration);
                    if(meeting!=null){
                        meetings.add(meeting);
                    }
                    break;
                }
            }


        } else {
           throw new CustomException("会议室已满,无法预订新会议！");
        }
        return null;
    }


    public BaseConferenceContext bookMeetingByCount(String name,String cloudMcuType,int duration) {
        if (meetings.size() < maxCapacity) {
            McuType mcuType =McuType.valueOf(cloudMcuType);
            BaseConferenceContext meeting=null;
            switch (mcuType) {
                case MCU_TENCENT : {
                    TencentCloudMeetingService tencentCloudMeetingService = BeanFactory.getBean(TencentCloudMeetingService.class);
                    meeting = tencentCloudMeetingService.createMeeting(name, 0);
                    if(meeting!=null){
                        meetings.add(meeting);
                    }
                }
            }

            return meeting;
        } else {
            throw new CustomException("会议室已满,无法预订新会议！");
        }
    }



    public void endMeeting(BaseConferenceContext meeting) {
        if (meetings.contains(meeting)) {
            meetings.remove(meeting);
            logger.info("会议已结束：" + meeting.getStartTime() + " - " + meeting.getEndTime());
        } else {
            logger.info("未找到指定会议,无法结束。");
        }
    }


    public int getTotalUsageTime() {
        int totalUsageTime = 0;
        for (BaseConferenceContext meeting : meetings) {
            totalUsageTime += (System.currentTimeMillis() - meeting.getStartTime().getTime())/60000;
        }
        return totalUsageTime;
    }


    public void setBookingStrategy(BookingStrategy strategy) {
        this.bookingStrategy=strategy;
    }

}
