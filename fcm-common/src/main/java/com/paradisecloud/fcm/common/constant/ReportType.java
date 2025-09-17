package com.paradisecloud.fcm.common.constant;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Description 报表类型
 * @Author johnson liu
 * @Date 2021/6/8 0:26
 **/
public interface ReportType
{
    /**
     * 会议数量
     */
    int CONFERENCE_NUM = 1;
    
    /**
     * 会议时长
     */
    int CONFERENCE_DURATION = 2;
    
    /**
     * 会议时长类型
     */
    int CONFERENCE_DURATION_TYPE = 3;
    
    /**
     * 通话质量
     */
    int CALL_QUALITY = 4;
    
    /**
     * 断线原因
     */
    int BREAK_REASON = 5;
    
    /**
     * 参会者数量范围
     */
    int PARTICIPANTS_RANGE = 6;
    
    /**
     * 参会者告警类型数量统计
     */
    int PARTICIPANTS_ALARM_TYPE = 7;

    /**
     * 会议数量（并发）
     */
    int CONFERENCE_NUM_CONCURRENCY = 11;
    
    /**
     * media类型
     */
    ArrayList<String> ARRAYLIST = new ArrayList<>(Arrays.asList("//rxAudio", "//txAudio", "//rxVideo", "//txVideo"));
}
