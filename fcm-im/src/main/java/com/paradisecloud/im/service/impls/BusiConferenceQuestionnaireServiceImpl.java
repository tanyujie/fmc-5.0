package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceOptionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceQuestionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceQuestionnaireMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceQuestionnaireRecordMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.dao.model.vo.*;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.im.service.IBusiConferenceQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议问卷主Service业务层处理
 *
 * @author lilinhai
 * @date 2025-09-24
 */
@Service
public class BusiConferenceQuestionnaireServiceImpl implements IBusiConferenceQuestionnaireService {
    @Autowired
    private BusiConferenceQuestionnaireMapper questionnaireMapper;
    @Autowired
    private BusiConferenceQuestionMapper questionMapper;
    @Autowired
    private BusiConferenceOptionMapper optionMapper;
    @Autowired
    private BusiConferenceQuestionnaireRecordMapper recordMapper;
    /**
     * 查询会议问卷主
     *
     * @param questionnaireId 会议问卷主ID
     * @return 会议问卷主
     */
    @Override
    public BusiConferenceQuestionnaire selectBusiConferenceQuestionnaireById(Long questionnaireId) {
        return questionnaireMapper.selectBusiConferenceQuestionnaireById(questionnaireId);
    }

    /**
     * 查询会议问卷主列表
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 会议问卷主
     */
    @Override
    public List<BusiConferenceQuestionnaire> selectBusiConferenceQuestionnaireList(BusiConferenceQuestionnaire busiConferenceQuestionnaire) {
        return questionnaireMapper.selectBusiConferenceQuestionnaireList(busiConferenceQuestionnaire);
    }

    /**
     * 新增会议问卷
     *
     * @param vo 会议问卷
     * @return 结果
     */
    @Override
    public boolean insertBusiConferenceQuestionnaire(BusiConferenceQuestionnaireAddVO vo) {
        String contextKey = EncryptIdUtil.parasToContextKey(vo.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        vo.setCreateQuestionnaireTime(System.currentTimeMillis() / 1000);
        vo.setStatus(0);
        vo.setIsAnonymous(1);
        vo.setConferenceId(conferenceIdVo.getId());
        questionnaireMapper.insertBusiConferenceQuestionnaire(vo);
        if (vo.getQuestionList().size() > 0) {
            vo.getQuestionList().forEach(e -> {
                e.setQuestionnaireId(vo.getQuestionnaireId());
                questionMapper.insertBusiConferenceQuestion(e);
                if (e.getOptionList().size() > 0) {
                    e.getOptionList().forEach(o -> {
                        o.setQuestionId(e.getQuestionId());
                        o.setVoteCount(0);
                        optionMapper.insertBusiConferenceOption(o);
                    });
                }

            });

        }
        return true;

    }

    @Override
    public BusiConferencePendingQuestionnaireVO getPendingQuestionnaireDetail(BusiConferenceQuestionnaireVO vo) {
        String contextKey = EncryptIdUtil.parasToContextKey(vo.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        BusiConferencePendingQuestionnaireVO pendingQuestionnaireVO = new BusiConferencePendingQuestionnaireVO();
        // 1. 查询“进行中”的问卷（status = 0：未开始，1：进行中，2：已结束）
        BusiConferenceQuestionnaire ongoingQuestionnaire = questionnaireMapper.selectPendingQuestionnaire(conferenceIdVo.getId());
        if (null == ongoingQuestionnaire) {
            return pendingQuestionnaireVO; // 无进行中的投票，直接返回空VO
        }

        // 2. 遍历投票，筛选“用户未参与”的问卷
        Long questionnaireId = ongoingQuestionnaire.getQuestionnaireId();
        //String userUiid = vo.getUserUUID(); // 假设入参vo包含当前用户ID

        // 查询该用户是否已参与该投票
/*        BusiConferenceVoteRecord record = busiConferenceVoteRecordMapper.selectByVoteId(voteId);
        if (record == null) { // 未参与，才封装详情*/
        pendingQuestionnaireVO.setQuestionnaireId(ongoingQuestionnaire.getQuestionnaireId());
        pendingQuestionnaireVO.setTitle(ongoingQuestionnaire.getTitle());
        pendingQuestionnaireVO.setStatus(ongoingQuestionnaire.getStatus());
        pendingQuestionnaireVO.setStatusText("进行中");
        pendingQuestionnaireVO.setParticipateStatus("未参与");

        // 3. 查询该投票的问题列表
        List<BusiConferenceQuestion> questions = questionMapper.selectByQuestionnaireId(ongoingQuestionnaire.getQuestionnaireId());
        List<BusiConferencePendingQuestionnaireVO.QuestionVO> questionVOList = new ArrayList<>();

        for (BusiConferenceQuestion question : questions) {
            BusiConferencePendingQuestionnaireVO.QuestionVO questionVO = new BusiConferencePendingQuestionnaireVO.QuestionVO();
            questionVO.setQuestionId(question.getQuestionId());
            questionVO.setContent(question.getContent());
            questionVO.setType(question.getType());

            // 4. 查询问题的选项列表
            List<BusiConferenceOption> options = optionMapper.selectByQuestionId(question.getQuestionId());
            List<BusiConferencePendingQuestionnaireVO.OptionVO> optionVOList = new ArrayList<>();

            for (BusiConferenceOption option : options) {
                BusiConferencePendingQuestionnaireVO.OptionVO optionVO = new BusiConferencePendingQuestionnaireVO.OptionVO();
                optionVO.setOptionId(option.getOptionId());
                optionVO.setContent(option.getContent());
                optionVO.setVoteCount(option.getVoteCount());
                optionVOList.add(optionVO);
            }
            questionVO.setOptionList(optionVOList);
            questionVOList.add(questionVO);
        }
        pendingQuestionnaireVO.setQuestionList(questionVOList);
        /*        }*/


        return pendingQuestionnaireVO;
    }

    /**
     * 修改会议问卷主
     *
     * @param busiConferenceQuestionnaire 会议问卷主
     * @return 结果
     */
    @Override
    public int updateBusiConferenceQuestionnaire(BusiConferenceQuestionnaire busiConferenceQuestionnaire) {
        //  busiConferenceQuestionnaire.setUpdateTime(new Date());
        return questionnaireMapper.updateBusiConferenceQuestionnaire(busiConferenceQuestionnaire);
    }

    /**
     * 批量删除会议问卷主
     *
     * @param questionnaireIds 需要删除的会议问卷主ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceQuestionnaireByIds(Long[] questionnaireIds) {
        return questionnaireMapper.deleteBusiConferenceQuestionnaireByIds(questionnaireIds);
    }

    /**
     * 删除会议问卷主信息
     *
     * @param questionnaireId 会议问卷主ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceQuestionnaireById(Long questionnaireId) {
        return questionnaireMapper.deleteBusiConferenceQuestionnaireById(questionnaireId);
    }

    @Override
    public boolean saveQuestionnaireRecords(BusiConferenceQuestionnaireRecordAddVO addVO) {
        try {
            // 遍历所有回答
            for (BusiConferenceQuestionVO question : addVO.getAnswers()) {
                BusiConferenceQuestionnaireRecord  record = new BusiConferenceQuestionnaireRecord ();
                record.setQuestionnaireId(addVO.getQuestionnaireId());
                record.setQuestionId(question.getQuestionId());
                record.setUserId(addVO.getUserId());
                record.setUserName(addVO.getUserName());
                record.setVoteTime(System.currentTimeMillis()); // 使用当前时间戳

                // 获取选中的选项ID列表
                List<Long> selectedOptionIds = question.getOptionIds();

                // 验证选择数量是否符合题型要求
                if (question.getType() == 1) { // 单选题
                    if (selectedOptionIds == null || selectedOptionIds.size() != 1) {
                        throw new RuntimeException("单选题必须且只能选择一个选项，问题ID: " + question.getQuestionId());
                    }
                } else if (question.getType() == 2) { // 多选题
                    if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
                        throw new RuntimeException("多选题至少选择一个选项，问题ID: " + question.getQuestionId());
                    }
                } else {
                    throw new RuntimeException("未知题型，问题ID: " + question.getQuestionId());
                }

                // 将选项ID列表转换为逗号分隔的字符串存储
                String optionIdsStr = selectedOptionIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                record.setOptionIds(optionIdsStr);
                recordMapper.insertBusiConferenceQuestionnaireRecord(record);
            }
            return true;
        } catch (Exception e) {
            // 日志记录
            // log.error("保存投票记录失败", e);
            return false;
        }
    }
}