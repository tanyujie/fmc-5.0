package com.paradisecloud.smc.service.impl;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridge;
import com.paradisecloud.fcm.smc.cache.modle.SmcBridgeCache;
import com.paradisecloud.smc.service.SmcUserService;
import org.springframework.stereotype.Service;

/**
 * @author nj
 * @date 2022/8/29 17:31
 */
@Service
public class SmcUserServiceImpl implements SmcUserService {
    @Override
    public UserInfoRep getUserInfo() {
        SmcBridge bridge = SmcBridgeCache.getInstance().getSmcBridgeByDeptId(null);
        String userInfo = bridge.getSmcUserInvoker().getUserInfo(bridge.getSmcportalTokenInvoker().getUsername(), bridge.getSmcportalTokenInvoker().getSystemHeaders());
        UserInfoRep userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
       return  userInfoRep;
    }
}
