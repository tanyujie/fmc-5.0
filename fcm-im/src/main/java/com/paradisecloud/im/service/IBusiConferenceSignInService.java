package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceOption;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;

public interface IBusiConferenceSignInService {
    boolean save(BusiConferenceSignInVO option);
    boolean getList(BusiConferenceSignInVO option);
}
