package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.TerminalJoinedCount;
import com.paradisecloud.fcm.dao.model.vo.BusiHistoryParticipantConferenceVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 历史会议的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiHistoryParticipantMapper 
{
    /**
     * 查询历史会议的参会者
     * 
     * @param id 历史会议的参会者ID
     * @return 历史会议的参会者
     */
    public BusiHistoryParticipant selectBusiHistoryParticipantById(Long id);
    
    BusiHistoryParticipant selectBusiHistoryParticipantByCallLegId(String callLegId);

    /**
     * 查询历史会议的参会者列表
     * 
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 历史会议的参会者集合
     */
    public List<BusiHistoryParticipant> selectBusiHistoryParticipantList(BusiHistoryParticipant busiHistoryParticipant);

    List<BusiHistoryParticipant> selectNotEndHistoryParticipantList();
    
    /**
     * 新增历史会议的参会者
     * 
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    public int insertBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant);

    /**
     * 修改历史会议的参会者
     * 
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    public int updateBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant);

    /**
     * 删除历史会议的参会者
     * 
     * @param id 历史会议的参会者ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantById(Long id);

    /**
     * 批量删除历史会议的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantByIds(Long[] ids);

    int batchInsertBusiHistoryParticipant(@Param("busiHistoryParticipantList") List<BusiHistoryParticipant> busiHistoryParticipantList);
    /**
     * 根据callLegId更新与会者记录表中的离会时间
     *
     * @param callLegId
     * @param outgoingTime 离会时间
     * @return
     */
    int updateHistoryParticipantByCallLegId(@Param("callLegId")String callLegId,@Param("outgoingTime") Date outgoingTime,@Param("updateTime")Date updateTime,@Param("durationSeconds")Integer durationSeconds,@Param("joined")Boolean joined);

    /**
     * 查询历史会议的参会者详细信息列表
     * @param historyConferenceId
     * @return
     */
    List<BusiHistoryParticipant> selectHistoryParticipantDetailList(@Param("isJoin")Boolean isJoin,@Param("historyConferenceId")Long historyConferenceId);

    /**
     * 根据部门和时间范围查询历史与会者记录
     * @param deptId
     * @param startTime
     * @param endTime
     * @return
     */
    List<BusiHistoryParticipant> selectParticipantByDeptAndTime(@Param("deptId")Long deptId,@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 根据部门ID、入会时间、离会时间查询参会者详情(callLegStart/callLegEnd)
     * @param deptId
     * @param startTime
     * @param endTime
     * @return
     */
    List<BusiHistoryParticipant> selectParticipantDetailListByDeptAndTime(@Param("deptId")Long deptId,@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 通话时长最长的 5 个终端，时长/终端
     * @param deptId
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> reportOrderByDuration(@Param("deptId")Long deptId, @Param("startTime")Date startTime, @Param("endTime")Date endTime);

    /**
     * 根据终端查询历史会议的参会者详细信息列表
     *
     * @param historyConferenceId
     * @param terminalId
     * @param remoteParty
     * @return
     */
    List<BusiHistoryParticipant> selectHistoryParticipantDetailListByTerminal(@Param("historyConferenceId") Long historyConferenceId, @Param("terminalId") Long terminalId, @Param("remoteParty") String remoteParty);

    /**
     * 根据终端查询历史会议的参会者详细信息列表
     *
     * @param historyConferenceId
     * @param terminalId
     * @param remoteParty
     * @return
     */
    TerminalJoinedCount getTerminalJoinedCount(@Param("historyConferenceId") Long historyConferenceId, @Param("terminalId") Long terminalId, @Param("remoteParty") String remoteParty);

    /**
     * 根据终端查询历史会议的离会参会者详细信息列表
     *
     * @param terminalId
     * @param startTime
     * @param endTime
     * @param startIndex
     * @param maxCount
     * @return
     */
    List<BusiHistoryParticipant> selectEndedHistoryParticipantListForTerminal(@Param("terminalId") Long terminalId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("startIndex") Long startIndex, @Param("maxCount") Long maxCount);

    /**
     * 根据终端查询历史会议的离会参会者详细信息列表
     *
     * @param terminalId
     * @param startTime
     * @param endTime
     * @param startIndex
     * @param maxCount
     * @return
     */
    List<BusiHistoryParticipant> selectNotEndHistoryParticipantListForTerminal(@Param("terminalId") Long terminalId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("startIndex") Long startIndex, @Param("maxCount") Long maxCount);

    /**
     * 查询会议终端最后进入
     *
     * @param historyConferenceId
     * @param terminalId
     * @param remoteParty
     * @return
     */
    List<BusiHistoryParticipant> selectBusiHistoryParticipantLastForConferenceTerminal(@Param("historyConferenceId") Long historyConferenceId, @Param("terminalId") Long terminalId, @Param("remoteParty") String remoteParty);

    /**
     * 入会修改历史会议的参会者
     *
     * @param busiHistoryParticipant 历史会议的参会者
     * @return 结果
     */
    int updateBusiHistoryParticipantForJoin(BusiHistoryParticipant busiHistoryParticipant);

    /**
     * 查询当日（处理的前一日）未结束的参会者
     *
     * @param startId
     * @param startTime eg.2022-06-01 00:00:00
     * @return 返回eg.2022-05-31 当日未结束的参会者
     */
    List<BusiHistoryParticipant> selectBusiHistoryParticipantListForTodayNotEndTerminal(@Param("startId") Long startId, @Param("startTime") Date startTime);

    /**
     * 查询会议当日的与会者数量
     *
     * @param historyConferenceId
     * @param startTime eg.2022-06-01 00:00:00
     * @param endTime eg.2022-06-01 23:59:59
     * @return 返回eg.2022-06-01 当日参与的参会者
     */
    long getConferenceDateTerminalJoinedCount(@Param("historyConferenceId") Long historyConferenceId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据终端查询历史会议的参会者详细信息列表
     *
     * @param historyConferenceId
     * @param terminalId
     * @param remoteParty
     * @param isJoin
     * @return
     */
    List<BusiHistoryParticipantConferenceVo> selectHistoryParticipantTerminalDetailListByTerminal(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("historyConferenceId") Long historyConferenceId, @Param("terminalId") Long terminalId, @Param("remoteParty") String remoteParty, @Param("isJoin") Boolean isJoin);

    /**
     * 根据终端查询历史会议的离会参会者详细信息列表
     *
     * @param terminalId
     * @param startTime
     * @param endTime
     * @param startIndex
     * @param maxCount
     * @return
     */
    List<BusiHistoryParticipant> selectHistoryParticipantListForTerminal(@Param("terminalId") Long terminalId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("startIndex") Long startIndex, @Param("maxCount") Long maxCount);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
