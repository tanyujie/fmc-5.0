package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.common.enumer.McuType;
import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceNumberSectionMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceSignInMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceSignInVO;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.impls.BusiConferenceNumberSectionServiceImpl;
import com.paradisecloud.im.service.IBusiConferenceSignInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service
public class BusiConferenceSignInServiceImpl implements IBusiConferenceSignInService {
    private final Logger logger = LoggerFactory.getLogger(BusiConferenceNumberSectionServiceImpl.class);

    @Resource
    private BusiConferenceSignInMapper conferenceSignInMapper;
    @Override
    public boolean save(BusiConferenceSignInVO option) {
        String contextKey = EncryptIdUtil.parasToContextKey(option.getConferenceId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();

        option.setMcuType(McuType.SMC3.getCode());
        option.setTemplateConferenceId(id);
        return conferenceSignInMapper.insertBusiConferenceSignIn(option)>0;
    }
}
