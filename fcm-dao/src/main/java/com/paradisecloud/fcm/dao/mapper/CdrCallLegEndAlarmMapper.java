package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallLegEndAlarm;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * callLegEnd报警信息Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrCallLegEndAlarmMapper {
    /**
     * 查询callLegEnd报警信息
     *
     * @param id callLegEnd报警信息ID
     * @return callLegEnd报警信息
     */
    public CdrCallLegEndAlarm selectCdrCallLegEndAlarmById(Integer id);

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
     * 删除callLegEnd报警信息
     *
     * @param id callLegEnd报警信息ID
     * @return 结果
     */
    public int deleteCdrCallLegEndAlarmById(Integer id);

    /**
     * 批量删除callLegEnd报警信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallLegEndAlarmByIds(Integer[] ids);

    /**
     * 查询callLegEnd报警信息列表
     *
     * @param historyConferenceId 会议id
     * @param terminalId 终端id
     * @return callLegEnd报警信息集合
     */
    public List<CdrCallLegEndAlarm> selectAlarmListForConferenceTerminalById(@Param("historyConferenceId") long historyConferenceId, @Param("terminalId") long terminalId);

    /**
     * 查询callLegEnd报警信息列表
     *
     * @param historyConferenceId 会议id
     * @param remoteParty 终端id
     * @return callLegEnd报警信息集合
     */
    public List<CdrCallLegEndAlarm> selectAlarmListForConferenceTerminalByRemoteParty(@Param("historyConferenceId") long historyConferenceId, @Param("remoteParty") String remoteParty);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
