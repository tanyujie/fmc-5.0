package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.CdrCallLegEnd;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * callLegEnd记录Mapper接口
 *
 * @author lilinhai
 * @date 2021-05-13
 */
public interface CdrCallLegEndMapper {
    /**
     * 查询callLegEnd记录
     *
     * @param id callLegEnd记录ID
     * @return callLegEnd记录
     */
    public CdrCallLegEnd selectCdrCallLegEndById(Integer id);

    /**
     * 查询callLegEnd记录列表
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegEnd> selectCdrCallLegEndList(CdrCallLegEnd cdrCallLegEnd);

    /**
     * 新增callLegEnd记录
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return 结果
     */
    public int insertCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd);

    /**
     * 修改callLegEnd记录
     *
     * @param cdrCallLegEnd callLegEnd记录
     * @return 结果
     */
    public int updateCdrCallLegEnd(CdrCallLegEnd cdrCallLegEnd);

    /**
     * 删除callLegEnd记录
     *
     * @param id callLegEnd记录ID
     * @return 结果
     */
    public int deleteCdrCallLegEndById(Integer id);

    /**
     * 批量删除callLegEnd记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCdrCallLegEndByIds(Integer[] ids);

    /**
     * 查询callLegEnd记录列表
     *
     * @param historyConferenceId 会议id
     * @param terminalId 终端id
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegEnd> selectCallLegEndListForConferenceTerminalById(@Param("historyConferenceId") long historyConferenceId, @Param("terminalId") long terminalId);

    /**
     * 查询callLegEnd记录列表
     *
     * @param historyConferenceId 会议id
     * @param remoteParty 终端id
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegEnd> selectCallLegEndListForConferenceTerminalByRemoteParty(@Param("historyConferenceId") long historyConferenceId, @Param("remoteParty") String remoteParty);

    /**
     * 查询callLegEnd记录列表
     *
     * @param historyConferenceId 会议id
     * @param terminalId 终端id
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegEnd> selectCallLegEndForConferenceTerminalById(@Param("historyConferenceId") long historyConferenceId, @Param("terminalId") long terminalId);

    /**
     * 查询callLegEnd记录列表
     *
     * @param historyConferenceId 会议id
     * @param remoteParty 终端id
     * @return callLegEnd记录集合
     */
    public List<CdrCallLegEnd> selectCallLegEndForConferenceTerminalByRemoteParty(@Param("historyConferenceId") long historyConferenceId, @Param("remoteParty") String remoteParty);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
