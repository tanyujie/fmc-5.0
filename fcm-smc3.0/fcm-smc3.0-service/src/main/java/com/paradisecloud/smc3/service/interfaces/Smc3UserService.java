package com.paradisecloud.smc3.service.interfaces;


import com.paradisecloud.smc3.model.response.UserInfoRep;

/**
 * @author nj
 * @date 2022/8/29 17:31
 */
public interface Smc3UserService {
    UserInfoRep getUserInfo(Long deptId);
}
