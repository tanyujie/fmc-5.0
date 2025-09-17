package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantTerminal;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 历史会议的参会者终端Mapper接口
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
public interface BusiHistoryParticipantTerminalMapper 
{
    /**
     * 查询历史会议的参会者终端
     * 
     * @param id 历史会议的参会者终端ID
     * @return 历史会议的参会者终端
     */
    public BusiHistoryParticipantTerminal selectBusiHistoryParticipantTerminalById(Long id);

    /**
     * 查询历史会议的参会者终端列表
     * 
     * @param busiHistoryParticipantTerminal 历史会议的参会者终端
     * @return 历史会议的参会者终端集合
     */
    public List<BusiHistoryParticipantTerminal> selectBusiHistoryParticipantTerminalList(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal);

    /**
     * 新增历史会议的参会者终端
     * 
     * @param busiHistoryParticipantTerminal 历史会议的参会者终端
     * @return 结果
     */
    public int insertBusiHistoryParticipantTerminal(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal);

    /**
     * 修改历史会议的参会者终端
     * 
     * @param busiHistoryParticipantTerminal 历史会议的参会者终端
     * @return 结果
     */
    public int updateBusiHistoryParticipantTerminal(BusiHistoryParticipantTerminal busiHistoryParticipantTerminal);

    /**
     * 删除历史会议的参会者终端
     * 
     * @param id 历史会议的参会者终端ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantTerminalById(Long id);

    /**
     * 批量删除历史会议的参会者终端
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantTerminalByIds(Long[] ids);

    /**
     * 查询历史会议的参会者详细信息列表
     * @param historyConferenceId
     * @return
     */
    List<BusiHistoryParticipantTerminal> selectHistoryParticipantTerminalDetailList(@Param("isJoin")Boolean isJoin, @Param("historyConferenceId")Long historyConferenceId);

    /**
     * 删除日期前的记录
     *
     * @param beforeDate 删除该日期前的数据
     * @return 结果
     */
    int deleteHistory(@Param("beforeDate") Date beforeDate);
}
