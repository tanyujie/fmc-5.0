package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import com.paradisecloud.fcm.dao.model.BusiConferenceVoteQuestion;
import lombok.Data;

import java.util.List;



/**
 * 投票相关信息的VO对象
 * 包含投票ID、问题列表、用户ID和用户名
 */
@Data
public class BusiConferenceVoteRecordAddVO {
    private Long voteId;
    private List<BusiConferenceVoteQuestionVO> questionList;
    private String userId;
    private String userName;

    // 无参构造函数
    public BusiConferenceVoteRecordAddVO() {
    }

    // 全参构造函数
    public BusiConferenceVoteRecordAddVO(Long voteId, List<BusiConferenceVoteQuestionVO> questionList, String userId, String userName) {
        this.voteId = voteId;
        this.questionList = questionList;
        this.userId = userId;
        this.userName = userName;
    }

    // getter和setter方法
    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public List<BusiConferenceVoteQuestionVO> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<BusiConferenceVoteQuestionVO> questionList) {
        this.questionList = questionList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}