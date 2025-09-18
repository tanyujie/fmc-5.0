package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceVote;

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
}