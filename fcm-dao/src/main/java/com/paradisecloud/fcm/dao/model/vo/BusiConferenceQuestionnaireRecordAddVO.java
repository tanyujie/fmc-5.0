package com.paradisecloud.fcm.dao.model.vo;

import lombok.Data;

import java.util.List;
@Data
public class BusiConferenceQuestionnaireRecordAddVO {
    private Long questionnaireId;
    private String confId;
    private List<BusiConferenceQuestionVO> answers;
    private String userId;
    private String userName;

    // 无参构造函数
    public BusiConferenceQuestionnaireRecordAddVO() {
    }

    // 全参构造函数
    public BusiConferenceQuestionnaireRecordAddVO(Long questionnaireId, List<BusiConferenceQuestionVO> questionList, String userId, String userName) {
        this.questionnaireId = questionnaireId;
        this.answers = questionList;
        this.userId = userId;
        this.userName = userName;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }

    public List<BusiConferenceQuestionVO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<BusiConferenceQuestionVO> answers) {
        this.answers = answers;
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
