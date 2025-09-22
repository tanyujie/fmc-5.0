package com.paradisecloud.fcm.dao.model.vo;
import lombok.Data;

import java.util.List;

/**
 * 用户待投票VO
 * 用于展示用户可参与的投票（未参与且状态为进行中）
 */
@Data
public class BusiConferencePendingVoteVO {

    /**
     * 投票ID
     */
    private Long voteId;

    /**
     * 投票主题
     */
    private String title;

    /**
     * 补充描述
     */
    private String description;

    /**
     * 投票状态（0：未开始，1：进行中，2：已结束）
     */
    private Integer status;

    /**
     * 投票状态显示文本（如“进行中”“未开始”）
     */
    private String statusText;

    /**
     * 用户参与状态（如“未参与”）
     */
    private String participateStatus;

    /**
     * 投票问题列表
     */
    private List<VoteQuestionVO> questionList;

    // getter、setter方法

    /**
     * 投票问题VO
     */
    @Data
    public static class VoteQuestionVO {

        /**
         * 问题ID
         */
        private Long questionId;

        /**
         * 问题内容
         */
        private String content;

        /**
         * 问题类型（0：单选，1：多选）
         */
        private Integer type;

        /**
         * 问题选项列表
         */
        private List<VoteOptionVO> optionList;

        // getter、setter方法
    }

    /**
     * 投票选项VO
     */
    @Data
    public static class VoteOptionVO {

        /**
         * 选项ID
         */
        private Long optionId;

        /**
         * 选项内容
         */
        private String content;

        /**
         * 该选项的投票数
         */
        private Integer voteCount;


    }
}
