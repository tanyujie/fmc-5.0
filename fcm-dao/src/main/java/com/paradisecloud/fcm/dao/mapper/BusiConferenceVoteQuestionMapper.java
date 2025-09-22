package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceVoteQuestion;

import java.util.List;

/**
 * 会议投票问题Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-18
 */
public interface BusiConferenceVoteQuestionMapper
{
    /**
     * 查询会议投票问题
     *
     * @param questionId 会议投票问题ID
     * @return 会议投票问题
     */
    public BusiConferenceVoteQuestion selectBusiConferenceVoteQuestionById(Long questionId);

    /**
     * 查询会议投票问题列表
     *
     * @param busiConferenceVoteQuestion 会议投票问题
     * @return 会议投票问题集合
     */
    public List<BusiConferenceVoteQuestion> selectBusiConferenceVoteQuestionList(BusiConferenceVoteQuestion busiConferenceVoteQuestion);

    /**
     * 新增会议投票问题
     *
     * @param busiConferenceVoteQuestion 会议投票问题
     * @return 结果
     */
    public int insertBusiConferenceVoteQuestion(BusiConferenceVoteQuestion busiConferenceVoteQuestion);

    /**
     * 修改会议投票问题
     *
     * @param busiConferenceVoteQuestion 会议投票问题
     * @return 结果
     */
    public int updateBusiConferenceVoteQuestion(BusiConferenceVoteQuestion busiConferenceVoteQuestion);

    /**
     * 删除会议投票问题
     *
     * @param questionId 会议投票问题ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteQuestionById(Long questionId);

    /**
     * 批量删除会议投票问题
     *
     * @param questionIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteQuestionByIds(Long[] questionIds);

    /**
     * 获取投票问题
     * @param voteId
     * @return
     */
    List<BusiConferenceVoteQuestion> selectByVoteId(Long voteId);
}