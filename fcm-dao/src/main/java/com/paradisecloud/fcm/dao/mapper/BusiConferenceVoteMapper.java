package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteOptionStatsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议投票主Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-18
 */
public interface BusiConferenceVoteMapper
{
    /**
     * 查询会议投票主
     *
     * @param voteId 会议投票主ID
     * @return 会议投票主
     */
    public BusiConferenceVote selectBusiConferenceVoteById(Long voteId);

    /**
     * 查询会议投票主列表
     *
     * @param busiConferenceVote 会议投票主
     * @return 会议投票主集合
     */
    public List<BusiConferenceVote> selectBusiConferenceVoteList(BusiConferenceVote busiConferenceVote);
    /**
     * 查询会议投票主列表
     *
     * @param conferenceId 会议id
     * @return 会议投票主集合
     */
    public List<BusiConferenceVote> selectBusiConferenceVoteByConferenceId(Long conferenceId);

    /**
     * 新增会议投票主
     *
     * @param busiConferenceVote 会议投票主
     * @return 结果
     */
    public int insertBusiConferenceVote(BusiConferenceVote busiConferenceVote);

    /**
     * 修改会议投票主
     *
     * @param busiConferenceVote 会议投票主
     * @return 结果
     */
    public int updateBusiConferenceVote(BusiConferenceVote busiConferenceVote);

    /**
     * 删除会议投票主
     *
     * @param voteId 会议投票主ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteById(Long voteId);

    /**
     * 批量删除会议投票主
     *
     * @param voteIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteByIds(Long[] voteIds);
    /**
     * 根据状态查询投票
     * @param conferenceId 状态值（1表示进行中）
     * @return 符合条件的投票列表
     */
    BusiConferenceVote selectPendingVote(Long conferenceId);

    /**
     * 基于投票ID查询选项统计数据
     * @param voteId
     * @return
     */
    List<BusiConferenceVoteOptionStatsVO> selectOptionStatsByVoteId(Long voteId);
}