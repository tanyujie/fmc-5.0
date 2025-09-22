package com.paradisecloud.im.service.impls;

import com.paradisecloud.fcm.common.utils.EncryptIdUtil;
import com.paradisecloud.fcm.common.vo.ConferenceIdVo;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceSignInMapper;
import com.paradisecloud.fcm.dao.mapper.BusiConferenceUserSignInMapper;
import com.paradisecloud.fcm.dao.model.BusiConferenceSignIn;
import com.paradisecloud.fcm.dao.model.BusiConferenceUserSignIn;
import com.paradisecloud.fcm.dao.model.vo.BusiConferenceUserSignInDetailVO;
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
            if (existingSignIn.getSignStatus()==null||existingSignIn.getSignStatus() != 2) {
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
    public BusiConferenceUserSignInDetailVO selectBusiConferenceUserSignInList(BusiConferenceUserSignInVO userSignIn) {
        String contextKey = EncryptIdUtil.parasToContextKey(userSignIn.getConfId());
        ConferenceIdVo conferenceIdVo = EncryptIdUtil.parasContextKey(contextKey);
        Long id = conferenceIdVo.getId();
        userSignIn.setConferenceId(id);
        // 2. 查询该会议的所有签到记录
        BusiConferenceSignIn signInInfo = signInMapper.selectBusiConferenceSignInByConferenceId(id);
        // 2. 查询该会议的所有签到记录
        List<BusiConferenceUserSignIn> signInList = userSignInMapper.selectBusiConferenceUserSignInList(userSignIn);

        // 3. 统计核心数据：总记录数、已签到人数（signStatus=2代表已签到，与表结构定义一致）
        int totalCount = signInList.size(); // 总签到记录数（包含已签到/未签到）
        // 流式统计已签到人数（过滤signStatus=2的记录）
        int signedCount = (int) signInList.stream()
                .filter(signIn -> signIn.getSignStatus() != null && 2 == signIn.getSignStatus())
                .count();

        // 4. 组装返回VO
        BusiConferenceUserSignInDetailVO resultVo = new BusiConferenceUserSignInDetailVO();
        resultVo.setTotalCount(totalCount);     // 总记录数
        resultVo.setSignedCount(signedCount);   // 已签到人数
        resultVo.setUserSignInList(signInList); // 具体签到列表
        // 签到状态1进行中2已结束
        resultVo.setSignStatus(signInInfo.getStatus());

        return resultVo;
    }
}
