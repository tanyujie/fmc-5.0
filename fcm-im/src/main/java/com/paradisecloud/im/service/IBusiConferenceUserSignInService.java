package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;

public interface IBusiConferenceUserSignInService {
    boolean save(BusiConferenceUserSignIn option);
}
