package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import lombok.Data;

import java.util.List;
@Data
public class BusiConferenceQuestionnaireAddVO extends BusiConferenceQuestionnaire {
    private String confId;
    //用户uuid，方便匿名登录
    private String userUUID;
    private List<BusiConferenceQuestionVO> questionList;
}
