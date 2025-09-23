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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
        vo.setVoteCreateTime((new Date()).getTime());
        vo.setStatus(0);
        vo.setIsAnonymous(1);
        vo.setConferenceId(conferenceIdVo.getId());
        voteMapper.insertBusiConferenceVote(vo);
        if (vo.getQuestionList().size() > 0) {
            vo.getQuestionList().forEach(e -> {
                e.setVoteId(vo.getVoteId());
                questionMapper.insertBusiConferenceVoteQuestion(e);
                if (e.getOptionList().size() > 0) {
                    e.getOptionList().forEach(o -> {
                        o.setQuestionId(e.getQuestionId());
                        o.setVoteCount(0);
                        voteOptionMapper.insertBusiConferenceVoteOption(o);
                    });
                }


            });


        }

        return true;
    }
    @Override
    public boolean update(BusiConferenceVoteVO vo) {
        String contextKey = EncryptIdUtil.parasToContextKey(vo.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        vo.setVoteCreateTime((new Date()).getTime());
        vo.setStatus(0);
        vo.setIsAnonymous(1);
        vo.setConferenceId(conferenceIdVo.getId());
        voteMapper.insertBusiConferenceVote(vo);
        if (vo.getQuestionList().size() > 0) {
            vo.getQuestionList().forEach(e -> {
                e.setVoteId(vo.getVoteId());
                questionMapper.insertBusiConferenceVoteQuestion(e);
                if (e.getOptionList().size() > 0) {
                    e.getOptionList().forEach(o -> {
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
     *
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
        if (null == ongoingVote) {
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
     *
     * @return 是否保存成功
     */
    public boolean saveVoteRecords(BusiConferenceVoteRecordAddVO addVO) {
        try {
            // 遍历所有回答
            for (BusiConferenceVoteQuestionVO question : addVO.getAnswers()) {
                BusiConferenceVoteRecord record = new BusiConferenceVoteRecord();
                record.setVoteId(addVO.getVoteId());
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
                voteRecordMapper.insertBusiConferenceVoteRecord(record);
            }
            return true;
        } catch (Exception e) {
            // 日志记录
            // log.error("保存投票记录失败", e);
            return false;
        }
    }

    @Override
    public List<BusiConferenceVoteSummaryVO> getSummaryByConference(BusiConferenceVoteVO voteVO) {
        String contextKey = EncryptIdUtil.parasToContextKey(voteVO.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        List<BusiConferenceVoteSummaryVO> voteSummary =  new ArrayList<>();
        List<BusiConferenceVote> voteList=voteMapper.selectBusiConferenceVoteByConferenceId(conferenceIdVo.getId());
        for(int i=0;i<voteList.size();i++){
            BusiConferenceVoteSummaryVO voteSummaryVO = new BusiConferenceVoteSummaryVO();
            // 1. 完善：查询并设置投票主信息（从投票主表查询当前会议关联的投票）
            if (conferenceIdVo != null && conferenceIdVo.getId() != null) {
                // ① 调用DAO/Service查询会议关联的投票主数据（假设通过会议ID关联投票主表）
                // 注：若一个会议可能有多个投票，需根据业务补充筛选条件（如当前生效投票）
                BusiConferenceVote voteMainInfo =voteList.get(i);
                if (voteMainInfo != null) {
                    // ② 将投票主信息设置到汇总VO
                    voteSummaryVO.setVoteInfo(voteMainInfo);
                    // 2. 完善：查询并设置投票选项统计列表（基于投票ID查询选项统计数据）
                    Long voteId = voteMainInfo.getVoteId();
                    // ① 调用DAO查询选项统计（对应之前定义的"投票问题选项统计视图"）
                    List<BusiConferenceVoteOptionStatsVO> optionStatsList =
                            voteMapper.selectOptionStatsByVoteId(voteId);

                    // ② 空列表处理（避免返回null，减少下游空指针风险）
                    voteSummaryVO.setOptionStatisticsList(optionStatsList != null ? optionStatsList : Collections.emptyList());
                } else {
                    // 业务处理：当前会议无关联投票，设置空默认值
                    voteSummaryVO.setVoteInfo(null); // 或new BusiConferenceVote()，根据业务决定
                    voteSummaryVO.setOptionStatisticsList(Collections.emptyList());
                }
            } else {
                // 异常场景处理：会议ID解析失败，设置空默认值
                voteSummaryVO.setVoteInfo(null);
                voteSummaryVO.setOptionStatisticsList(Collections.emptyList());
            }
            voteSummary.add(voteSummaryVO);
        }

        return voteSummary;
    }

    /**
     * 删除会议投票主信息
     *
     * @param voteId 会议投票主ID
     * @return 结果
     */
    @Override
    public int deleteBusiConferenceVoteById(Long voteId)
    {
        return voteMapper.deleteBusiConferenceVoteById(voteId);
    }
    /**
     * 查询会议投票主列表
     *
     * @param busiConferenceVote 会议投票主
     * @return 会议投票主
     */
    @Override
    public List<BusiConferenceVote> selectBusiConferenceVoteList(BusiConferenceVoteVO busiConferenceVote)
    {
        return voteMapper.selectBusiConferenceVoteList(busiConferenceVote);
    }

}
