package com.paradisecloud.im.service;

import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteRecordVO;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteVO;

public interface IBusiConferenceVoteRecordService {
    public boolean save(BusiConferenceVoteRecordVO vo);
}
