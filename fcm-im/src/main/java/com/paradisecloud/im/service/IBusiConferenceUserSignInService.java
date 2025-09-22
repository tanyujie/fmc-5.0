package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceUserSignInVO;

import java.util.List;

public interface IBusiConferenceUserSignInService {
    boolean save(BusiConferenceUserSignInVO option);
    /**
     * 查询成员签到关联列表
     *
     * @param busiConferenceUserSignIn 成员签到关联
     * @return 成员签到关联集合
     */
    public List<BusiConferenceUserSignIn> selectBusiConferenceUserSignInList(BusiConferenceUserSignInVO busiConferenceUserSignIn);
}
