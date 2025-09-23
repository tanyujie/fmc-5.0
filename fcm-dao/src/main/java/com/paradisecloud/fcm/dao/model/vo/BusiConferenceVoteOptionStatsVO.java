package com.paradisecloud.fcm.dao.model.vo;

import lombok.Data;

/**
 * 会议投票问题选项统计视图对应的VO对象
 * 用于封装每个问题下各选项的得票统计数据
 */
@Data
public class BusiConferenceVoteOptionStatsVO {
    /**
     * 投票ID
     */
    private Long voteId;

    /**
     * 问题ID
     */
    private Long questionId;

    /**
     * 问题内容
     */
    private String questionContent;

    /**
     * 问题类型（0：单选，1：多选）
     */
    private Integer questionType;

    /**
     * 选项ID
     */
    private Long optionId;

    /**
     * 选项内容
     */
    private String optionContent;

    /**
     * 该选项的得票数
     */
    private Integer optionVoteCount;

    /**
     * 该选项在当前问题中的得票率（百分比，保留2位小数）
     */
    private Double voteRatePercent;

    /**
     * 当前问题的总票数（所有选项得票之和）
     */
    private Integer questionTotalVotes;
}
