package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.BusiConferenceVote;
import lombok.Data;

import java.util.List;

@Data
public class BusiConferenceVoteVO  extends BusiConferenceVote {
    private String confId;
    //用户uuid，方便匿名登录
    private String userUUID;
    private List<BusiConferenceVoteQuestionVO> questionList;
}