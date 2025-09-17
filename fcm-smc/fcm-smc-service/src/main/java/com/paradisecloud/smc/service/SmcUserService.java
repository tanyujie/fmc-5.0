package com.paradisecloud.smc.service;

import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.fcm.smc.cache.modle.SmcUserInvoker;

/**
 * @author nj
 * @date 2022/8/29 17:31
 */
public interface SmcUserService {
    UserInfoRep getUserInfo();
}
