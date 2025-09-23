package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import com.paradisecloud.fcm.dao.model.vo.BusiConferencePendingVoteVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteRecordAddVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteSummaryVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;

import java.util.List;

public interface IBusiConferenceVoteService {
    public boolean save(BusiConferenceVoteVO vo);
    public boolean update(BusiConferenceVoteVO vo);
    public BusiConferencePendingVoteVO getPendingVoteDetail(BusiConferenceVoteVO vo);
    public boolean saveVoteRecords(BusiConferenceVoteRecordAddVO addVO);
    public List<BusiConferenceVoteSummaryVO> getSummaryByConference(BusiConferenceVoteVO voteVO);
    /**
     * 删除会议投票主信息
     *
     * @param voteId 会议投票主ID
     * @return 结果
     */
    public int deleteBusiConferenceVoteById(Long voteId);
    /**
     * 查询会议投票主列表
     *
     * @param busiConferenceVote 会议投票主
     * @return 会议投票主集合
     */
    public List<BusiConferenceVote> selectBusiConferenceVoteList(BusiConferenceVoteVO busiConferenceVote);
}
