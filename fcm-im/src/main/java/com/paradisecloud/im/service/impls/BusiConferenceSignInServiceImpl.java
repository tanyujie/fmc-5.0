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
        // 1. 解析会议ID（原逻辑保留）
        String contextKey = EncryptIdUtil.parasToContextKey(option.getConfId());
        // BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long conferenceId = conferenceIdVo.getId(); // 会议ID，用于查询是否已存在签到记录

        // 2. 关键：查询该会议是否已创建签到记录（核心判断逻辑）
        BusiConferenceSignIn existingSignIn = conferenceSignInMapper.selectBusiConferenceSignInByConferenceId(conferenceId);
        // 若已存在（不为null），直接返回true，表示“已创建”
        if (existingSignIn != null) {
            return true;
        }

        // 3. 若未创建，设置参数并执行新增
        option.setMcuType(McuType.SMC3.getCode());
        option.setConferenceId(conferenceId); // 关联会议ID
        // 新增成功返回true，失败返回false
        return conferenceSignInMapper.insertBusiConferenceSignIn(option) > 0;
    }

    @Override
    public boolean getList(BusiConferenceSignInVO option) {
        return false;
    }
}
