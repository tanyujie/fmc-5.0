package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import lombok.Data;

import java.util.List;
/**
 * 会议投票汇总信息VO
 * 包含投票主信息及该投票下所有问题的选项统计数据
 */
@Data
public class BusiConferenceVoteSummaryVO {
    /**
     * 投票主信息（对应投票主表数据）
     */
    private BusiConferenceVote voteInfo;
    /**
     * 投票选项统计列表
     * 包含该投票下所有问题的各个选项的统计数据
     */
    private List<BusiConferenceVoteOptionStatsVO> optionStatisticsList;
}
