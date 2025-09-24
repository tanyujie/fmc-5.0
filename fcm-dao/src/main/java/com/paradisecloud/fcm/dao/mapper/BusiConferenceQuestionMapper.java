package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestion;

import java.util.List;

/**
 * 会议问卷问题Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-24
 */
public interface BusiConferenceQuestionMapper
{
    /**
     * 查询会议问卷问题
     *
     * @param questionId 会议问卷问题ID
     * @return 会议问卷问题
     */
    public BusiConferenceQuestion selectBusiConferenceQuestionById(Long questionId);

    /**
     * 查询会议问卷问题列表
     *
     * @param busiConferenceQuestion 会议问卷问题
     * @return 会议问卷问题集合
     */
    public List<BusiConferenceQuestion> selectBusiConferenceQuestionList(BusiConferenceQuestion busiConferenceQuestion);

    /**
     * 新增会议问卷问题
     *
     * @param busiConferenceQuestion 会议问卷问题
     * @return 结果
     */
    public int insertBusiConferenceQuestion(BusiConferenceQuestion busiConferenceQuestion);

    /**
     * 修改会议问卷问题
     *
     * @param busiConferenceQuestion 会议问卷问题
     * @return 结果
     */
    public int updateBusiConferenceQuestion(BusiConferenceQuestion busiConferenceQuestion);

    /**
     * 删除会议问卷问题
     *
     * @param questionId 会议问卷问题ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionById(Long questionId);

    /**
     * 批量删除会议问卷问题
     *
     * @param questionIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionByIds(Long[] questionIds);
    public List<BusiConferenceQuestion> selectByQuestionnaireId(Long questionnaireId);

}
