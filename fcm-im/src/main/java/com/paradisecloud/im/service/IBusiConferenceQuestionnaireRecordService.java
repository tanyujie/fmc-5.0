package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaireRecord;
import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;

import java.util.List;

/**
 * 会议问卷记录Service接口
 *
 * @author lilinhai
 * @date 2025-09-24
 */
public interface IBusiConferenceQuestionnaireRecordService
{
    /**
     * 查询会议问卷记录
     *
     * @param recordId 会议问卷记录ID
     * @return 会议问卷记录
     */
    public BusiConferenceQuestionnaireRecord selectBusiConferenceQuestionnaireRecordById(Long recordId);

    /**
     * 查询会议问卷记录列表
     *
     * @param busiConferenceQuestionnaireRecord 会议问卷记录
     * @return 会议问卷记录集合
     */
    public List<BusiConferenceQuestionnaireRecord> selectBusiConferenceQuestionnaireRecordList(BusiConferenceQuestionnaireRecord busiConferenceQuestionnaireRecord);

    /**
     * 新增会议问卷记录
     *
     * @param busiConferenceQuestionnaireRecord 会议问卷记录
     * @return 结果
     */
    public int insertBusiConferenceQuestionnaireRecord(BusiConferenceQuestionnaireRecord busiConferenceQuestionnaireRecord);

    /**
     * 修改会议问卷记录
     *
     * @param busiConferenceQuestionnaireRecord 会议问卷记录
     * @return 结果
     */
    public int updateBusiConferenceQuestionnaireRecord(BusiConferenceQuestionnaireRecord busiConferenceQuestionnaireRecord);

    /**
     * 批量删除会议问卷记录
     *
     * @param recordIds 需要删除的会议问卷记录ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionnaireRecordByIds(Long[] recordIds);

    /**
     * 删除会议问卷记录信息
     *
     * @param recordId 会议问卷记录ID
     * @return 结果
     */
    public int deleteBusiConferenceQuestionnaireRecordById(Long recordId);


}
