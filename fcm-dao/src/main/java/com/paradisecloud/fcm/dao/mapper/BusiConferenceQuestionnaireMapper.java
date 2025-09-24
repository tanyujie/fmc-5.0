package com.paradisecloud.fcm.dao.mapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;
import com.paradisecloud.fcm.dao.model.BusiConferenceVote;

import java.util.List;

/**
 * 会议问卷主Mapper接口
 *
 * @author lilinhai
 * @date 2025-09-24
 */
public interface BusiConferenceQuestionnaireMapper
{
    /**
     * 查询会议问卷主
     *
     * @param questionnaireId 会议问卷主ID
     * @return 会议问卷主
     */
    public BusiConferenceQuestionnaire selectBusiConferenceQuestionnaireById(Long questionnaireId);

    /**
     * 查询会议问卷主列表
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 会议问卷主集合
     */
    public List<BusiConferenceQuestionnaire> selectBusiConferenceQuestionnaireList(BusiConferenceQuestionnaire busiConferenceQuestionnaire);

    /**
     * 新增会议问卷主
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 结果
     */
    public int insertBusiConferenceQuestionnaire(BusiConferenceQuestionnaire busiConferenceQuestionnaire);

    /**
     * 修改会议问卷主
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 结果
     */
    public int updateBusiConferenceQuestionnaire(BusiConferenceQuestionnaire busiConferenceQuestionnaire);

    /**
     * 删除会议问卷主
     *
     * @param questionnaireId 会议问卷主ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionnaireById(Long questionnaireId);

    /**
     * 批量删除会议问卷主
     *
     * @param questionnaireIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionnaireByIds(Long[] questionnaireIds);
    /**
     * 根据状态查询问卷
     * @param conferenceId 状态值（1表示进行中）
     * @return 符合条件的投票列表
     */
    BusiConferenceQuestionnaire selectPendingQuestionnaire(Long conferenceId);
}