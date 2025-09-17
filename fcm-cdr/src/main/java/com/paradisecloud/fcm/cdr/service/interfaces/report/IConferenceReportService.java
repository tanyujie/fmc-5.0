package com.paradisecloud.fcm.cdr.service.interfaces.report;

import com.paradisecloud.fcm.dao.model.vo.ReportSearchVo;

import java.util.List;
import java.util.Map;

/**
 * 会议统计
 * 
 * @author johnson liu
 * @date 2021/6/18 14:06
 */
public interface IConferenceReportService
{
    /**
     * 每天/周/月 发起会议的数量 统计
     * 
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo);

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo, boolean isConcurrency);

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @param dateType 0:日 1：月
     * @return
     */
    List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo, int dateType);

    /**
     * 每天/周/月 发起会议的数量 统计
     *
     * @param searchVo
     * @param dateType 0:日 1：月
     * @return
     */
    List<Map<String, Object>> selectConferenceNumOfDay(ReportSearchVo searchVo, int dateType, boolean isConcurrency);
    
    /**
     * 每天/周/月 发起会议的总时长 统计
     * 
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> selectConferenceDurationOfDay(ReportSearchVo searchVo);
    
    /**
     * 每天/周/月 参会者的质量分析统计
     * 
     * @param searchVo
     * @return
     */
    List<Map<String, Object>> reportCallQualityOfDay(ReportSearchVo searchVo);
}
