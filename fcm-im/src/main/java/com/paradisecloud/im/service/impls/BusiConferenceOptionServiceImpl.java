package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.dao.model.BusiConferenceOption;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.im.service.IBusiConferenceOptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BusiConferenceOptionServiceImpl implements IBusiConferenceOptionService {
    @Override
    public BusiConferenceOption getById(long optionId) {
        return null;
    }

    @Override
    public boolean save(BusiConferenceOption option) {
        return false;
    }

    @Override
    public boolean updateById(BusiConferenceOption option) {
        return false;
    }

    @Override
    public boolean removeById(long optionId) {
        return false;
    }
}
