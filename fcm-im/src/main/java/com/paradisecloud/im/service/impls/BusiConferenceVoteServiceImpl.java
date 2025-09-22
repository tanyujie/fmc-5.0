package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.*;
import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import com.paradisecloud.fcm.dao.model.BusiConferenceVoteOption;
import com.paradisecloud.fcm.dao.model.BusiConferenceVoteQuestion;
import com.paradisecloud.fcm.dao.model.BusiConferenceVoteRecord;
import com.paradisecloud.fcm.dao.model.vo.*;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.impls.BusiConferenceNumberSectionServiceImpl;
import com.paradisecloud.im.service.IBusiConferenceVoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class BusiConferenceVoteServiceImpl implements IBusiConferenceVoteService {
    private final Logger logger = LoggerFactory.getLogger(BusiConferenceVoteServiceImpl.class);

    @Resource
    private BusiConferenceVoteMapper voteMapper;
    @Resource
    private BusiConferenceVoteQuestionMapper questionMapper;
    @Resource
    private BusiConferenceVoteOptionMapper voteOptionMapper;
    @Resource
    private BusiConferenceVoteRecordMapper voteRecordMapper;


    @Override
    public boolean save(BusiConferenceVoteVO vo) {
        String contextKey = EncryptIdUtil.parasToContextKey(vo.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        vo.setVoteCreateTime( (new Date()).getTime());
        vo.setStatus(0);
        vo.setIsAnonymous(1);
        vo.setConferenceId(conferenceIdVo.getId());
        voteMapper.insertBusiConferenceVote(vo);
        if(vo.getQuestionList().size()>0){
            vo.getQuestionList().forEach(e->{
                e.setVoteId(vo.getVoteId());
                questionMapper.insertBusiConferenceVoteQuestion(e);
                if(e.getOptionList().size()>0){
                    e.getOptionList().forEach(o->{
                        o.setQuestionId(e.getQuestionId());
                        o.setVoteCount(0);
                        voteOptionMapper.insertBusiConferenceVoteOption(o);
                    });
                }


            });


        }

        return true;
    }

    /**
     * 获取待投票详情
     * @param vo
     * @return
     */
    @Override
    public BusiConferencePendingVoteVO getPendingVoteDetail(BusiConferenceVoteVO vo) {
        String contextKey = EncryptIdUtil.parasToContextKey(vo.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        BusiConferencePendingVoteVO pendingVoteVO = new BusiConferencePendingVoteVO();
        // 1. 查询“进行中”的投票（status = 0：未开始，1：进行中，2：已结束）
        BusiConferenceVote ongoingVote = voteMapper.selectPendingVote(conferenceIdVo.getId());
        if (null==ongoingVote) {
            return pendingVoteVO; // 无进行中的投票，直接返回空VO
        }

        // 2. 遍历投票，筛选“用户未参与”的投票
        Long voteId = ongoingVote.getVoteId();
        String userUiid = vo.getUserUUID(); // 假设入参vo包含当前用户ID

        // 查询该用户是否已参与该投票
/*        BusiConferenceVoteRecord record = busiConferenceVoteRecordMapper.selectByVoteId(voteId);
        if (record == null) { // 未参与，才封装详情*/
            pendingVoteVO.setVoteId(voteId);
            pendingVoteVO.setTitle(ongoingVote.getTitle());
            pendingVoteVO.setDescription(ongoingVote.getDescription());
            pendingVoteVO.setStatus(ongoingVote.getStatus());
            pendingVoteVO.setStatusText("进行中");
            pendingVoteVO.setParticipateStatus("未参与");

            // 3. 查询该投票的问题列表
            List<BusiConferenceVoteQuestion> questions = questionMapper.selectByVoteId(voteId);
            List<BusiConferencePendingVoteVO.VoteQuestionVO> questionVOList = new ArrayList<>();

            for (BusiConferenceVoteQuestion question : questions) {
                BusiConferencePendingVoteVO.VoteQuestionVO questionVO = new BusiConferencePendingVoteVO.VoteQuestionVO();
                questionVO.setQuestionId(question.getQuestionId());
                questionVO.setContent(question.getContent());
                questionVO.setType(question.getType());

                // 4. 查询问题的选项列表
                List<BusiConferenceVoteOption> options = voteOptionMapper.selectByQuestionId(question.getQuestionId());
                List<BusiConferencePendingVoteVO.VoteOptionVO> optionVOList = new ArrayList<>();

                for (BusiConferenceVoteOption option : options) {
                    BusiConferencePendingVoteVO.VoteOptionVO optionVO = new BusiConferencePendingVoteVO.VoteOptionVO();
                    optionVO.setOptionId(option.getOptionId());
                    optionVO.setContent(option.getContent());
                    optionVO.setVoteCount(option.getVoteCount());
                    optionVOList.add(optionVO);
                }
                questionVO.setOptionList(optionVOList);
                questionVOList.add(questionVO);
            }
            pendingVoteVO.setQuestionList(questionVOList);
/*        }*/


        return pendingVoteVO;
    }


    /**
     * 保存用户投票记录
     * @param voteId 投票ID
     * @param questionList 问题列表（包含用户选择的选项）
     * @param userId 用户ID
     * @param userName 用户名
     * @return 是否保存成功
     */
    public boolean saveVoteRecords(BusiConferenceVoteRecordAddVO addVO) {
        try {
            LocalDateTime now = LocalDateTime.now();

            for (BusiConferenceVoteQuestionVO question : addVO.getQuestionList()) {
                BusiConferenceVoteRecord record = new BusiConferenceVoteRecord();
                record.setVoteId(addVO.getVoteId());
                record.setQuestionId(question.getQuestionId());
                record.setUserId(addVO.getUserId());
                record.setUserName(addVO.getUserName());
                record.setVoteTime(System.currentTimeMillis());

                // 根据题型处理选项ID
                List<BusiConferenceVoteOptionVO> selectedOptions = question.getOptionList().stream()
                        .filter(BusiConferenceVoteOptionVO::isSelected) // 假设Option有isSelected()方法标识是否被选中
                        .collect(Collectors.toList());

                // 验证选择数量是否符合题型要求
                if (question.getType() == 1 && selectedOptions.size() > 1) {
                    throw new RuntimeException("单选题只能选择一个选项");
                }
                if (question.getType() == 2 && selectedOptions.isEmpty()) {
                    throw new RuntimeException("多选题至少选择一个选项");
                }

                // 处理选项ID，多选用逗号分隔
                String optionIds = selectedOptions.stream()
                        .map(option -> String.valueOf(option.getOptionId()))
                        .collect(Collectors.joining(","));

                record.setOptionIds(optionIds);
                voteRecordMapper.insertBusiConferenceVoteRecord(record);
            }
            return true;
        } catch (Exception e) {
            // 日志记录
           // log.error("保存投票记录失败", e);
            return false;
        }
    }
}
