package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import lombok.Data;

@Data
public class BusiConferenceUserSignInVO  extends BusiConferenceUserSignIn {
    private String conferenceId;
}
