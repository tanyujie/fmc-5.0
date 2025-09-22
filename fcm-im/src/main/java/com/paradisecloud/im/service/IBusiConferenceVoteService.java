package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.vo.BusiConferencePendingVoteVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;

public interface IBusiConferenceVoteService {
    public boolean save(BusiConferenceVoteVO vo);
    public BusiConferencePendingVoteVO getPendingVoteDetail(BusiConferenceVoteVO vo);

}
