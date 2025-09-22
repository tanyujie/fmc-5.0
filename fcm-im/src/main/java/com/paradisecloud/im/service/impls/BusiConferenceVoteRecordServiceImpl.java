package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.dao.mapper.BusiConferenceVoteOptionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceVoteRecordMapper;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceVoteRecordVO;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.im.service.IBusiConferenceVoteRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service
public class BusiConferenceVoteRecordServiceImpl implements IBusiConferenceVoteRecordService {
    @Resource
    private BusiConferenceVoteRecordMapper voteRecordMapper;
    @Override
    public boolean save(BusiConferenceVoteRecordVO vo) {
        voteRecordMapper.insertBusiConferenceVoteRecord(vo);
        return true;
    }
}
