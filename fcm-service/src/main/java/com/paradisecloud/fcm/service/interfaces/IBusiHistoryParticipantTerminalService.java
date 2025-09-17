package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiHistoryParticipant;
import com.paradisecloud.fcm.dao.model.BusiHistoryParticipantTerminal;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 历史会议的参会者终端Service接口
 * 
 * @author lilinhai
 * @date 2022-06-08
 */
public interface IBusiHistoryParticipantTerminalService 
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
     * 批量删除历史会议的参会者终端
     * 
     * @param ids 需要删除的历史会议的参会者终端ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantTerminalByIds(Long[] ids);

    /**
     * 删除历史会议的参会者终端信息
     * 
     * @param id 历史会议的参会者终端ID
     * @return 结果
     */
    public int deleteBusiHistoryParticipantTerminalById(Long id);

    /**
     * 查询历史会议的参会者终端
     *
     * @param historyConferenceId 会议ID
     * @param terminalId 终端ID
     * @param remoteParty
     * @return 历史会议的参会者终端
     */
    public BusiHistoryParticipantTerminal selectBusiHistoryParticipantTerminalByConferenceTerminal(@NotNull Long historyConferenceId, Long terminalId, @NotNull String remoteParty);

    /**
     * 根据参会者信息更新（新增）参会者终端信息
     *
     * @param busiHistoryParticipant
     * @return
     */
    public int updateBusiHistoryParticipantTerminalByBusiHistoryParticipant(BusiHistoryParticipant busiHistoryParticipant);
}
