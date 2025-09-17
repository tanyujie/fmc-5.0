package com.paradisecloud.fcm.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;

import java.util.List;
import java.util.Map;

/**
 * 历史全会议的参会者Service接口
 *
 * @author lilinhai
 * @date 2021-01-20
 */
public interface IBusiHistoryAllParticipantService
{
    /**
     * 查询历史会议的参会者
     *
     * @param id 历史会议的参会者ID
     * @return 历史会议的参会者
     */
    public BusiHistoryAllParticipant selectBusiHistoryAllParticipantById(Long id);

    /**
     * 查询历史会议的参会者列表
     *
     * @param BusiHistoryAllParticipant 历史会议的参会者
     * @return 历史会议的参会者集合
     */
    public List<BusiHistoryAllParticipant> selectBusiHistoryAllParticipantList(BusiHistoryAllParticipant BusiHistoryAllParticipant);

    /**
     * 新增历史会议的参会者
     *
     * @param BusiHistoryAllParticipant 历史会议的参会者
     * @return 结果
     */
    public int insertBusiHistoryAllParticipant(BusiHistoryAllParticipant BusiHistoryAllParticipant);

    /**
     * 修改历史会议的参会者
     *
     * @param BusiHistoryAllParticipant 历史会议的参会者
     * @return 结果
     */
    public int updateBusiHistoryAllParticipant(BusiHistoryAllParticipant BusiHistoryAllParticipant);

    /**
     * 批量删除历史会议的参会者
     *
     * @param ids 需要删除的历史会议的参会者ID
     * @return 结果
     */
    public int deleteBusiHistoryAllParticipantByIds(Long[] ids);

    /**
     * 删除历史会议的参会者信息
     *
     * @param id 历史会议的参会者ID
     * @return 结果
     */
    public int deleteBusiHistoryAllParticipantById(Long id);

    /**
     * 通过历史会议ID查询该会议的与会者信息
     * @param hisConferenceId
     * @return
     */
    List<Map<String,Object>> reportByHisConferenceId(String hisConferenceId, Boolean isJoin);
}
