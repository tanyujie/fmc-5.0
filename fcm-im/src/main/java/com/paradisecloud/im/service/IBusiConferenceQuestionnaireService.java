package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;

import java.util.List;

public interface IBusiConferenceQuestionnaireService {
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
     * 批量删除会议问卷主
     *
     * @param questionnaireIds 需要删除的会议问卷主ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionnaireByIds(Long[] questionnaireIds);

    /**
     * 删除会议问卷主信息
     *
     * @param questionnaireId 会议问卷主ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionnaireById(Long questionnaireId);
}
