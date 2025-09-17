package com.paradisecloud.fcm.web.service.interfaces;

import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUser;
import com.paradisecloud.fcm.dao.model.SipAccountInfo;
import com.paradisecloud.system.dao.model.SysUser;

import java.util.List;

public interface IBusiUserService {
    
    List<BusiUser> selectUserList(BusiUser var1);

    BusiUser selectUserByUserName(String var1);

    BusiUser selectUserById(Long var1);

    String selectUserRoleGroup(String var1);

    String selectUserPostGroup(String var1);

    String checkUserNameUnique(String var1);

    String checkPhoneUnique(BusiUser var1);

    String checkEmailUnique(BusiUser var1);

    void checkUserAllowed(BusiUser var1);

    int insertUser(BusiUser var1);

    int updateUser(BusiUser var1);

    int updateUserStatus(BusiUser var1);

    int updateUserProfile(BusiUser var1);

    boolean updateUserAvatar(String var1, String var2);

    int resetPwd(BusiUser var1);

    int resetUserPwd(String var1, String var2);

    int deleteUserById(Long var1);

    int deleteUserByIds(Long[] var1);

    String importUser(List<BusiUser> var1, Boolean var2, String var3);

    List<BusiTerminal> selectBusiTerminalListOfNotBoundByUser(BusiTerminal busiTerminal);

    SipAccountInfo getUserSipAccountInfo(Long userId, boolean isExternalRequest);

    int isLoginLocked(String userName);

    int unlockLogin(BusiUser busiUser);

    int updateLoginError(String userName, boolean success);

    /**
     * 查询用户信息列表
     * @param deptId
     * @return
     */
    List<SysUser> selectUserListByDeptId(Long deptId);
}
