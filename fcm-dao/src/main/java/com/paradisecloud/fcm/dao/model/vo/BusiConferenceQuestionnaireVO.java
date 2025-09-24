package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceQuestionnaire;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import lombok.Data;

@Data
public class BusiConferenceQuestionnaireVO extends BusiConferenceQuestionnaire {
    private String confId;
}
