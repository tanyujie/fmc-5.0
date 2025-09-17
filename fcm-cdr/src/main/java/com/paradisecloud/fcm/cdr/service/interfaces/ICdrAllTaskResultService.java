package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiHistoryAllConference;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/8 0:16
 **/
public interface ICdrAllTaskResultService
{
    /**
     * 更新每天参会的会议数量和会议总时长
     *
     * @param groupList
     * @return
     */
    int updateMeetingNumAndDuration(List<BusiHistoryAllConference> groupList);
    
    /**
     * 更新每天会议的会议质量数量统计
     *
     * @return
     */
    int updateCallQuality();
    
    /**
     * 参会者告警统计
     * 
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> reportAlarmTypeRate(ReportSearchVo searchVo);
}
