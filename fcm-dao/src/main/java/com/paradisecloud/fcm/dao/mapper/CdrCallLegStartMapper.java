package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallLegEnd;
import com.paradisecloud.fcm.dao.model.CdrCallLegStart;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * callLegStart 记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrCallLegStartMapper {
    /**
     * 查询callLegStart 记录
     *
     * @param id callLegStart 记录ID
     * @return callLegStart 记录
     */
    public CdrCallLegStart selectCdrCallLegStartById(Integer id);

    /**
     * 查询callLegStart 记录列表
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return callLegStart 记录集合
     */
    public List<CdrCallLegStart> selectCdrCallLegStartList(CdrCallLegStart cdrCallLegStart);

    /**
     * 新增callLegStart 记录
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return 结果
     */
    public int insertCdrCallLegStart(CdrCallLegStart cdrCallLegStart);

    /**
     * 修改callLegStart 记录
     *
     * @param cdrCallLegStart callLegStart 记录
     * @return 结果
     */
    public int updateCdrCallLegStart(CdrCallLegStart cdrCallLegStart);

    /**
     * 根据CallLegId更新
     * @param cdrCallLegStart
     * @return
     */
    int updateByCallLegId(CdrCallLegStart cdrCallLegStart);

    /**
     * 删除callLegStart 记录
     *
     * @param id callLegStart 记录ID
     * @return 结果
     */
    public int deleteCdrCallLegStartById(Integer id);

    /**
     * 批量删除callLegStart 记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallLegStartByIds(Integer[] ids);

    CdrCallLegStart selectCdrCallLegStartByRecordIndex(@Param("cdrId")String id,@Param("recordIndex")Integer recordIndex, @Param("correlatorIndex") Integer correlatorIndex);

    /**
     * 查询callLegStart记录列表
     *
     * @param historyConferenceId 会议id
     * @param terminalId 终端id
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegStart> selectCallLegStartListForConferenceTerminalById(@Param("historyConferenceId") long historyConferenceId, @Param("terminalId") long terminalId);

    /**
     * 查询callLegStart记录列表
     *
     * @param historyConferenceId 会议id
     * @param remoteParty 终端id
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegStart> selectCallLegStartListForConferenceTerminalByRemoteParty(@Param("historyConferenceId") long historyConferenceId, @Param("remoteParty") String remoteParty);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
