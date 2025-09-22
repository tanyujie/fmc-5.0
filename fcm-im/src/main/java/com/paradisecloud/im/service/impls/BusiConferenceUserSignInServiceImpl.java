package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceSignInMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceUserSignInMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceUserSignInVO;
import com.paradisecloud.fcm.mcu.kdc.service.interfaces.IAttendeeForMcuKdcService;
import com.paradisecloud.fcm.service.conference.AllConferenceContextCache;
import com.paradisecloud.fcm.service.conference.BaseConferenceContext;
import com.paradisecloud.fcm.service.impls.BusiConferenceNumberSectionServiceImpl;
import com.paradisecloud.im.service.IBusiConferenceUserSignInService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class BusiConferenceUserSignInServiceImpl implements IBusiConferenceUserSignInService {
    private final Logger logger = LoggerFactory.getLogger(BusiConferenceNumberSectionServiceImpl.class);

    @Resource
    private BusiConferenceUserSignInMapper userSignInMapper;
    @Resource
    private BusiConferenceSignInMapper signInMapper;

    @Override
    public boolean save(BusiConferenceUserSignInVO userSignIn) {
        String contextKey = EncryptIdUtil.parasToContextKey(userSignIn.getConfId());
        BaseConferenceContext baseConferenceContext = AllConferenceContextCache.getInstance().get(contextKey);
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        BusiConferenceSignIn conferenceSignIn = signInMapper.selectBusiConferenceSignInByConferenceId(id);
        // 设置会议和签到ID
        userSignIn.setConferenceId(conferenceSignIn.getConferenceId());
        userSignIn.setSignInId(conferenceSignIn.getSignInId());

        // 新增判断：检查该用户是否已签到
        BusiConferenceUserSignIn existingSignIn = userSignInMapper.selectByConferenceIdAndSignInIdAndNickname(
                id,
                conferenceSignIn.getSignInId(),
                userSignIn.getUserNickname()
        );

        // 如果已签到，直接返回true
        if (existingSignIn != null) {
            if (existingSignIn.getSignStatus() == 1) {
                existingSignIn.setSignStatus(2);
                userSignInMapper.updateBusiConferenceUserSignIn(existingSignIn);
            }
            return true;
        }

        // 未签到则执行新的签到操作
        userSignIn.setSignInTime(System.currentTimeMillis() / 1000); // Java中获取秒级时间戳
        userSignIn.setSignStatus(2);
        return userSignInMapper.insertBusiConferenceUserSignIn(userSignIn) > 0;
    }

    /**
     * 查询成员签到关联列表
     *
     * @param userSignIn 成员签到关联
     * @return 成员签到关联
     */
    @Override
    public List<BusiConferenceUserSignIn> selectBusiConferenceUserSignInList(BusiConferenceUserSignInVO userSignIn) {
        String contextKey = EncryptIdUtil.parasToContextKey(userSignIn.getConfId());
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        userSignIn.setConferenceId(id);
        List<BusiConferenceUserSignIn> list= userSignInMapper.selectBusiConferenceUserSignInList(userSignIn);
        return list;
    }
}
