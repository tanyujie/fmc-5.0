package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import lombok.Data;

import java.util.List;

@Data
public class BusiConferenceVoteVO  extends BusiConferenceVote {
    private String conferenceId;
    private List<BusiConferenceVoteQuestionVO> questionList;
}