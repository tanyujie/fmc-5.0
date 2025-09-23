package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BusiConferenceQuestionnaireImpl implements IBusiConferenceQuestionnaireService {
    @Autowired
    private BusiConferenceQuestionnaireMapper busiConferenceQuestionnaireMapper;

    /**
     * 查询会议问卷主
     *
     * @param questionnaireId 会议问卷主ID
     * @return 会议问卷主
     */
    @Override
    public BusiConferenceQuestionnaire selectBusiConferenceQuestionnaireById(Long questionnaireId)
    {
        return busiConferenceQuestionnaireMapper.selectBusiConferenceQuestionnaireById(questionnaireId);
    }

    /**
     * 查询会议问卷主列表
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 会议问卷主
     */
    @Override
    public List<BusiConferenceQuestionnaire> selectBusiConferenceQuestionnaireList(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        return busiConferenceQuestionnaireMapper.selectBusiConferenceQuestionnaireList(busiConferenceQuestionnaire);
    }

    /**
     * 新增会议问卷主
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 结果
     */
    @Override
    public int insertBusiConferenceQuestionnaire(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        busiConferenceQuestionnaire.setCreateTime(new Date());
        return busiConferenceQuestionnaireMapper.insertBusiConferenceQuestionnaire(busiConferenceQuestionnaire);
    }

    /**
     * 修改会议问卷主
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 结果
     */
    @Override
    public int updateBusiConferenceQuestionnaire(BusiConferenceQuestionnaire busiConferenceQuestionnaire)
    {
        busiConferenceQuestionnaire.setUpdateTime(new Date());
        return busiConferenceQuestionnaireMapper.updateBusiConferenceQuestionnaire(busiConferenceQuestionnaire);
    }

    /**
     * 批量删除会议问卷主
     *
     * @param questionnaireIds 需要删除的会议问卷主ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceQuestionnaireByIds(Long[] questionnaireIds)
    {
        return busiConferenceQuestionnaireMapper.deleteBusiConferenceQuestionnaireByIds(questionnaireIds);
    }

    /**
     * 删除会议问卷主信息
     *
     * @param questionnaireId 会议问卷主ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceQuestionnaireById(Long questionnaireId)
    {
        return busiConferenceQuestionnaireMapper.deleteBusiConferenceQuestionnaireById(questionnaireId);
    }
}
