package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiHistoryConference;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/8 0:16
 **/
public interface ICdrTaskResultService
{
    /**
     * 更新每天参会的会议数量和会议总时长
     * 
     * @param deptId
     * @param groupList
     * @return
     */
    int updateMeetingNumAndDuration(Long deptId);

    /**
     * 更新每天参会的会议数量和会议总时长（新）
     *
     * @param deptId
     * @param historyConferenceNotEndList
     * @param calcStartTime
     * @param calcEndTime
     * @return
     */
    int updateMeetingNumAndDuration(Long deptId, List<BusiHistoryConference> historyConferenceNotEndList, Date calcStartTime, Date calcEndTime);
    
    /**
     * 更新每天会议的会议质量数量统计
     * 
     * @param deptId
     * @return
     */
    int updateCallQuality(Long deptId);
    
    /**
     * 参会者告警统计
     * 
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> reportAlarmTypeRate(ReportSearchVo searchVo);
}
