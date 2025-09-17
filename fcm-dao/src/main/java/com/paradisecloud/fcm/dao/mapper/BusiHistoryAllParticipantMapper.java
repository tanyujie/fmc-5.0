package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiHistoryAllParticipant;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 历史全会议的参会者Mapper接口
 * 
 * @author lilinhai
 * @date 2021-01-20
 */
public interface BusiHistoryAllParticipantMapper
{
    /**
     * 查询历史会议的参会者
     * 
     * @param id 历史会议的参会者ID
     * @return 历史会议的参会者
     */
    BusiHistoryAllParticipant selectBusiHistoryAllParticipantById(Long id);

    /**
     * 根据callLegId查询历史会议参与者
     *
     * @param callLegId
     * @return
     */
    BusiHistoryAllParticipant selectBusiHistoryAllParticipantByCallLegId(String callLegId);

    /**
     * 查询历史会议的参会者列表
     * 
     * @param busiHistoryAllParticipant 历史会议的参会者
     * @return 历史会议的参会者集合
     */
    List<BusiHistoryAllParticipant> selectBusiHistoryAllParticipantList(BusiHistoryAllParticipant busiHistoryAllParticipant);

    /**
     * 查询所有没有结束的历史会议测参会者列表
     *
     * @return
     */
    List<BusiHistoryAllParticipant> selectNotEndHistoryParticipantList();
    
    /**
     * 新增历史会议的参会者
     * 
     * @param busiHistoryAllParticipant 历史会议的参会者
     * @return 结果
     */
    int insertBusiHistoryAllParticipant(BusiHistoryAllParticipant busiHistoryAllParticipant);

    /**
     * 修改历史会议的参会者
     * 
     * @param busiHistoryAllParticipant 历史会议的参会者
     * @return 结果
     */
     int updateBusiHistoryAllParticipant(BusiHistoryAllParticipant busiHistoryAllParticipant);

    /**
     * 删除历史会议的参会者
     * 
     * @param id 历史会议的参会者ID
     * @return 结果
     */
    int deleteBusiHistoryAllParticipantById(Long id);

    /**
     * 批量删除历史会议的参会者
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteBusiHistoryAllParticipantByIds(Long[] ids);

    /**
     * 批量插入历史会议的参会者
     *
     * @param busiHistoryAllParticipantList
     * @return
     */
    int batchInsertBusiHistoryAllParticipant(@Param("busiHistoryAllParticipantList") List<BusiHistoryAllParticipant> busiHistoryAllParticipantList);
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
    List<BusiHistoryAllParticipant> selectHistoryParticipantDetailList(@Param("isJoin")Boolean isJoin,@Param("historyConferenceId")Long historyConferenceId);

    /**
     * 根据部门和时间范围查询历史与会者记录
     * @param startTime
     * @param endTime
     * @return
     */
    List<BusiHistoryAllParticipant> selectParticipantByTime(@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 根据部门ID、入会时间、离会时间查询参会者详情(callLegStart/callLegEnd)
     * @param startTime
     * @param endTime
     * @return
     */
    List<BusiHistoryAllParticipant> selectParticipantDetailListByTime(@Param("startTime")String startTime,@Param("endTime")String endTime);

    /**
     * 通话时长最长的 5 个终端，时长/终端
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String,Object>> reportOrderByDuration( @Param("startTime")Date startTime, @Param("endTime")Date endTime);
}
