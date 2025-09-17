package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceOption;

public interface IBusiConferenceOptionService {
    BusiConferenceOption getById(long optionId);
    boolean save(BusiConferenceOption option);
    boolean updateById(BusiConferenceOption option);
    boolean  removeById(long optionId);
}
