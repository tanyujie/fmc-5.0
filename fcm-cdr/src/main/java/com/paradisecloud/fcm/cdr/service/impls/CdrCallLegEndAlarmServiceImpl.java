package com.paradisecloud.fcm.cdr.service.impls;

import java.util.List;

import com.paradisecloud.fcm.cdr.service.interfaces.ICdrCallLegEndAlarmService;
import com.paradisecloud.fcm.dao.mapper.CdrCallLegEndAlarmMapper;
import com.paradisecloud.fcm.dao.model.CdrCallLegEndAlarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * callLegEnd报警信息Service业务层处理
 *
 * @author lilinhai
 * @date 2021-05-13
 */
@Service
public class CdrCallLegEndAlarmServiceImpl implements ICdrCallLegEndAlarmService
{
    @Autowired
    private CdrCallLegEndAlarmMapper cdrCallLegEndAlarmMapper;
    
    /**
     * 查询callLegEnd报警信息
     *
     * @param cdrId callLegEnd报警信息ID
     * @return callLegEnd报警信息
     */
    @Override
    public CdrCallLegEndAlarm selectCdrCallLegEndAlarmById(Integer cdrId)
    {
        return cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmById(cdrId);
    }
    
    /**
     * 查询callLegEnd报警信息列表
     *
     * @param cdrCallLegEndAlarm callLegEnd报警信息
     * @return callLegEnd报警信息
     */
    @Override
    public List<CdrCallLegEndAlarm> selectCdrCallLegEndAlarmList(CdrCallLegEndAlarm cdrCallLegEndAlarm)
    {
        return cdrCallLegEndAlarmMapper.selectCdrCallLegEndAlarmList(cdrCallLegEndAlarm);
    }
    
    /**
     * 新增callLegEnd报警信息
     *
     * @param cdrCallLegEndAlarm callLegEnd报警信息
     * @return 结果
     */
    @Override
    public int insertCdrCallLegEndAlarm(CdrCallLegEndAlarm cdrCallLegEndAlarm)
    {
        return cdrCallLegEndAlarmMapper.insertCdrCallLegEndAlarm(cdrCallLegEndAlarm);
    }
    
    /**
     * 修改callLegEnd报警信息
     *
     * @param cdrCallLegEndAlarm callLegEnd报警信息
     * @return 结果
     */
    @Override
    public int updateCdrCallLegEndAlarm(CdrCallLegEndAlarm cdrCallLegEndAlarm)
    {
        return cdrCallLegEndAlarmMapper.updateCdrCallLegEndAlarm(cdrCallLegEndAlarm);
    }
    
    /**
     * 批量删除callLegEnd报警信息
     *
     * @param cdrIds 需要删除的callLegEnd报警信息ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegEndAlarmByIds(Integer[] cdrIds)
    {
        return cdrCallLegEndAlarmMapper.deleteCdrCallLegEndAlarmByIds(cdrIds);
    }
    
    /**
     * 删除callLegEnd报警信息信息
     *
     * @param cdrId callLegEnd报警信息ID
     * @return 结果
     */
    @Override
    public int deleteCdrCallLegEndAlarmById(Integer cdrId)
    {
        return cdrCallLegEndAlarmMapper.deleteCdrCallLegEndAlarmById(cdrId);
    }
}
