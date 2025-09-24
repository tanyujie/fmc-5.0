package com.paradisecloud.im.service.impls;

import java.util.List;

import com.paradisecloud.fcm.dao.mapper.BusiConferenceQuestionnaireRecordMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaireRecord;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 会议问卷记录Service业务层处理
 *
 * @author lilinhai
 * @date 2025-09-24
 */
@Service
public class BusiConferenceQuestionnaireRecordServiceImpl implements IBusiConferenceQuestionnaireRecordService
{
    @Autowired
    private BusiConferenceQuestionnaireRecordMapper busiConferenceQuestionnaireRecordMapper;

    /**
     * 查询会议问卷记录
     *
     * @param recordId 会议问卷记录ID
     * @return 会议问卷记录
     */
    @Override
    public BusiConferenceQuestionnaireRecord selectBusiConferenceQuestionnaireRecordById(Long recordId)
    {
        return busiConferenceQuestionnaireRecordMapper.selectBusiConferenceQuestionnaireRecordById(recordId);
    }

    /**
     * 查询会议问卷记录列表
     *
     * @param busiConferenceQuestionnaireRecord 会议问卷记录
     * @return 会议问卷记录
     */
    @Override
    public List<BusiConferenceQuestionnaireRecord> selectBusiConferenceQuestionnaireRecordList(BusiConferenceQuestionnaireRecord busiConferenceQuestionnaireRecord)
    {
        return busiConferenceQuestionnaireRecordMapper.selectBusiConferenceQuestionnaireRecordList(busiConferenceQuestionnaireRecord);
    }

    /**
     * 新增会议问卷记录
     *
     * @param busiConferenceQuestionnaireRecord 会议问卷记录
     * @return 结果
     */
    @Override
    public int insertBusiConferenceQuestionnaireRecord(BusiConferenceQuestionnaireRecord busiConferenceQuestionnaireRecord)
    {
        return busiConferenceQuestionnaireRecordMapper.insertBusiConferenceQuestionnaireRecord(busiConferenceQuestionnaireRecord);
    }

    /**
     * 修改会议问卷记录
     *
     * @param busiConferenceQuestionnaireRecord 会议问卷记录
     * @return 结果
     */
    @Override
    public int updateBusiConferenceQuestionnaireRecord(BusiConferenceQuestionnaireRecord busiConferenceQuestionnaireRecord)
    {
        return busiConferenceQuestionnaireRecordMapper.updateBusiConferenceQuestionnaireRecord(busiConferenceQuestionnaireRecord);
    }

    /**
     * 批量删除会议问卷记录
     *
     * @param recordIds 需要删除的会议问卷记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceQuestionnaireRecordByIds(Long[] recordIds)
    {
        return busiConferenceQuestionnaireRecordMapper.deleteBusiConferenceQuestionnaireRecordByIds(recordIds);
    }

    /**
     * 删除会议问卷记录信息
     *
     * @param recordId 会议问卷记录ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceQuestionnaireRecordById(Long recordId)
    {
        return busiConferenceQuestionnaireRecordMapper.deleteBusiConferenceQuestionnaireRecordById(recordId);
    }
}
