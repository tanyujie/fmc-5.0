package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.dao.mapper.BusiConferenceSignInMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceUserSignInMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.service.impls.BusiConferenceNumberSectionServiceImpl;
import com.paradisecloud.im.service.IBusiConferenceUserSignInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service
public class BusiConferenceUserSignInServiceImpl implements IBusiConferenceUserSignInService {
    private final Logger logger = LoggerFactory.getLogger(BusiConferenceNumberSectionServiceImpl.class);

    @Resource
    private BusiConferenceUserSignInMapper userSignInMapper;
    @Override
    public boolean save(BusiConferenceUserSignIn userSignIn) {
        return userSignInMapper.insertBusiConferenceUserSignIn(userSignIn)>0;
    }
}
