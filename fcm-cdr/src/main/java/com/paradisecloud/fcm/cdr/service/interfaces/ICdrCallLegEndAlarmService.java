package com.paradisecloud.fcm.cdr.service.interfaces;

import com.paradisecloud.fcm.dao.model.CdrCallLegEndAlarm;

import java.util.List;

/**
 * callLegEnd报警信息Service接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface ICdrCallLegEndAlarmService
{
    /**
     * 查询callLegEnd报警信息
     *
     * @param cdrId callLegEnd报警信息ID
     * @return callLegEnd报警信息
     */
    public CdrCallLegEndAlarm selectCdrCallLegEndAlarmById(Integer cdrId);
    
    /**
     * 查询callLegEnd报警信息列表
     *
     * @param cdrCallLegEndAlarm callLegEnd报警信息
     * @return callLegEnd报警信息集合
     */
    public List<CdrCallLegEndAlarm> selectCdrCallLegEndAlarmList(CdrCallLegEndAlarm cdrCallLegEndAlarm);
    
    /**
     * 新增callLegEnd报警信息
     *
     * @param cdrCallLegEndAlarm callLegEnd报警信息
     * @return 结果
     */
    public int insertCdrCallLegEndAlarm(CdrCallLegEndAlarm cdrCallLegEndAlarm);
    
    /**
     * 修改callLegEnd报警信息
     *
     * @param cdrCallLegEndAlarm callLegEnd报警信息
     * @return 结果
     */
    public int updateCdrCallLegEndAlarm(CdrCallLegEndAlarm cdrCallLegEndAlarm);
    
    /**
     * 批量删除callLegEnd报警信息
     *
     * @param cdrIds 需要删除的callLegEnd报警信息ID
     * @return 结果
     */
    public int deleteCdrCallLegEndAlarmByIds(Integer[] cdrIds);
    
    /**
     * 删除callLegEnd报警信息信息
     *
     * @param cdrId callLegEnd报警信息ID
     * @return 结果
     */
    public int deleteCdrCallLegEndAlarmById(Integer cdrId);
}
