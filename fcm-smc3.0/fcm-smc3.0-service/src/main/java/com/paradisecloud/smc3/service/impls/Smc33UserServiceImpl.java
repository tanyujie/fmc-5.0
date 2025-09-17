package com.paradisecloud.smc3.service.impls;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.busi.cache.Smc3BridgeCache;
import com.paradisecloud.smc3.model.response.UserInfoRep;
import com.paradisecloud.smc3.service.interfaces.Smc3UserService;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2022/8/29 17:31
 */
@Service
public class Smc33UserServiceImpl implements Smc3UserService {

    @Override
    public UserInfoRep getUserInfo(Long deptId) {
        Smc3Bridge bridge = Smc3BridgeCache.getInstance().getBridgesByDept(deptId);
        String userInfo = bridge.getSmcUserInvoker().getUserInfo(bridge.getSmcportalTokenInvoker().getUserName(), bridge.getSmcportalTokenInvoker().getSystemHeaders());
        UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
        return  userInfoRep;
    }
}
