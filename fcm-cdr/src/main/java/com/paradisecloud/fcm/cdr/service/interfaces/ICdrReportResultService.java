package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.enums.CallLegEndReasonEnum;
import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author johnson liu
 * @date 2021/5/27 16:48
 */
public interface ICdrReportResultService
{
    /**
     * 首页会议总时长、会议次数、参数终端数统计
     * 
     * @return
     */
    Map<String, Object> reportConferenceOfIndex(Long deptId, String startTime, String endTime);
    
    /**
     * 离线原因统计
     * 
     * @param deptId
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> reportByReason(Long deptId, Date startTime, Date endTime);
    
    /**
     * 获取所有离线原因列表
     * 
     * @return
     */
    List<CallLegEndReasonEnum> getAllReason();
    
    /**
     * 根据设备类型统计各类型设备数量
     * 
     * @param deptId
     * @return
     */
    List<Map<String, Object>> reportByDeviceType(Long deptId);
    
    /**
     * 终端使用情况分析(柱状图)
     * 通话时长最长的 5 个终端，时长/终端
     * 
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> reportOrderByDuration(ReportSearchVo searchVo);
    
    /**
     * MCU使用情况统计查询
     * 
     * @param deptId
     * @param fmeIp
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> usedRate(Integer deptId, String fmeIp, String startTime, String endTime);
}
