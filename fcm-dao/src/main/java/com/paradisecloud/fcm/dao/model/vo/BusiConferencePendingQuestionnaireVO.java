package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BusiConferencePendingQuestionnaireVO {
    /** 问卷ID（主键） */
    private Long questionnaireId;

    /** mcu type */
    private String mcuType;

    /** 关联的会议ID（外键，关联会议表） */
    private Long conferenceId;

    /** 问卷标题 */
    private String title;

    /** 有效时间（单位：分钟） */
    private Long effectiveTime;

    /** 创建时间 */
    private Long createQuestionnaireTime;

    /** 问卷状态（0：未开始，1：进行中，2：已结束） */
    private Integer status;
    /**
     * 问卷状态显示文本（如“进行中”“未开始”）
     */
    private String statusText;

    /**
     * 用户参与状态（如“未参与”）
     */
    private String participateStatus;
    /** 是否匿名（0：否，1：是） */
    private Integer isAnonymous;

    /** 创建人ID（外键，关联用户表） */
    private Long creatorId;

    //创建人")
    private String creatorNickname;
    /**
     * 投票问题列表
     */
    private List<QuestionVO> questionList;
    /**
     * 投票问题VO
     */
    @Data
    public static class QuestionVO {

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
        private List<OptionVO> optionList;

        // getter、setter方法
    }

    /**
     * 投票选项VO
     */
    @Data
    public static class OptionVO {

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
