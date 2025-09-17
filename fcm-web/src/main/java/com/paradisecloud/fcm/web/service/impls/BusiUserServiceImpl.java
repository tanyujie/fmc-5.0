package com.paradisecloud.fcm.web.service.impls;

import com.github.pagehelper.Page;
import com.paradisecloud.common.annotation.DataScope;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.common.enumer.AttendType;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.utils.SipAccountUtil;
import com.paradisecloud.fcm.dao.mapper.BusiSipAccountAutoMapper;
import com.paradisecloud.fcm.dao.mapper.BusiTerminalMapper;
import com.paradisecloud.fcm.dao.mapper.BusiUserTerminalMapper;
import com.paradisecloud.fcm.dao.model.*;
import com.paradisecloud.fcm.mcu.zj.task.DelayTaskService;
import com.paradisecloud.fcm.mqtt.common.TerminalSipAccount;
import com.paradisecloud.fcm.terminal.service.interfaces.IBusiTerminalService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.system.dao.mapper.*;
import com.paradisecloud.system.dao.model.*;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysConfigService;
import com.paradisecloud.system.service.ISysDeptService;
import com.paradisecloud.system.utils.SecurityUtils;
import com.sinhy.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户Service业务层处理
 */
@Service
public class BusiUserServiceImpl implements IBusiUserService {
    private static final Logger logger = LoggerFactory.getLogger(BusiUserServiceImpl.class);

    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysPostMapper postMapper;
    @Resource
    private SysUserRoleMapper userRoleMapper;
    @Resource
    private SysUserPostMapper userPostMapper;
    @Resource
    private ISysConfigService configService;
    @Resource
    private BusiUserTerminalMapper busiUserTerminalMapper;
    @Resource
    private BusiTerminalMapper busiTerminalMapper;
    @Resource
    private IBusiTerminalService busiTerminalService;
    @Resource
    private BusiSipAccountAutoMapper busiSipAccountAutoMapper;
    @Resource
    private ISysDeptService sysDeptService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private DelayTaskService delayTaskService;

    @DataScope(
            deptAlias = "d",
            userAlias = "u"
    )
    public List<BusiUser> selectUserList(BusiUser user) {
        Page userList = new Page<>();
        if (user.getDeptId() == null) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            user.setDeptId(loginUser.getUser().getDeptId());
        }
        List<SysUser> sysUserList = this.userMapper.selectUserList(user);
        for (SysUser sysUser : sysUserList) {
            BusiUser busiUser = new BusiUser(sysUser);
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(sysUser.getUserId());
            if (busiUserTerminal != null) {
                BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminal.getTerminalId());
                if (busiTerminal != null) {
                    busiUser.setTerminalId(busiTerminal.getId());
                    busiUser.setTerminalName(busiTerminal.getName());
                    busiUser.setTerminalType(busiTerminal.getType());
                    busiUser.setTerminalTypeName(TerminalType.convert(busiTerminal.getType()).getDisplayName());
                }
            }
            busiUser.setLoginLocked(isLoginLocked(busiUser.getUserName()) > 0);
            userList.add(busiUser);
        }
        if (sysUserList instanceof Page) {
            Page sysUserPage = (Page) sysUserList;
            userList.setTotal(sysUserPage.getTotal());
        }
        return userList;
    }

    public BusiUser selectUserByUserName(String userName) {
        SysUser sysUser = this.userMapper.selectUserByUserName(userName);
        if (sysUser == null) {
            return null;
        }
        BusiUser busiUser = new BusiUser(sysUser);
        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(sysUser.getUserId());
        if (busiUserTerminal != null) {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminal.getTerminalId());
            if (busiTerminal != null) {
                busiUser.setTerminalId(busiTerminal.getId());
                busiUser.setTerminalName(busiTerminal.getName());
                busiUser.setTerminalType(busiTerminal.getType());
                busiUser.setTerminalTypeName(TerminalType.convert(busiTerminal.getType()).getDisplayName());
            }
        }
        busiUser.setLoginLocked(isLoginLocked(busiUser.getUserName()) > 0);
        return busiUser;
    }

    public BusiUser selectUserById(Long userId) {
        SysUser sysUser = this.userMapper.selectUserById(userId);
        if (sysUser == null) {
            return null;
        }
        BusiUser busiUser = new BusiUser(sysUser);
        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(sysUser.getUserId());
        if (busiUserTerminal != null) {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminal.getTerminalId());
            if (busiTerminal != null) {
                busiUser.setTerminalId(busiTerminal.getId());
                busiUser.setTerminalName(busiTerminal.getName());
                busiUser.setTerminalType(busiTerminal.getType());
                busiUser.setTerminalTypeName(TerminalType.convert(busiTerminal.getType()).getDisplayName());
            }
        }
        busiUser.setLoginLocked(isLoginLocked(busiUser.getUserName()) > 0);
        return busiUser;
    }

    public String selectUserRoleGroup(String userName) {
        List<SysRole> list = this.roleMapper.selectRolesByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        Iterator var4 = list.iterator();

        while (var4.hasNext()) {
            SysRole role = (SysRole) var4.next();
            idsStr.append(role.getRoleName()).append(",");
        }

        return StringUtils.isNotEmpty(idsStr.toString()) ? idsStr.substring(0, idsStr.length() - 1) : idsStr.toString();
    }

    public String selectUserPostGroup(String userName) {
        List<SysPost> list = this.postMapper.selectPostsByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        Iterator var4 = list.iterator();

        while (var4.hasNext()) {
            SysPost post = (SysPost) var4.next();
            idsStr.append(post.getPostName()).append(",");
        }

        return StringUtils.isNotEmpty(idsStr.toString()) ? idsStr.substring(0, idsStr.length() - 1) : idsStr.toString();
    }

    public String checkUserNameUnique(String userName) {
        int count = this.userMapper.checkUserNameUnique(userName);
        return count > 0 ? "1" : "0";
    }

    public String checkPhoneUnique(BusiUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = this.userMapper.checkPhoneUnique(user.getPhonenumber());
        return StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue() ? "1" : "0";
    }

    public String checkEmailUnique(BusiUser user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = this.userMapper.checkEmailUnique(user.getEmail());
        return StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue() ? "1" : "0";
    }

    public void checkUserAllowed(BusiUser user) {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
            throw new CustomException("不允许操作超级管理员用户");
        }
    }

    @Transactional
    public int insertUser(BusiUser user) {
        int rows = this.userMapper.insertUser(user);
        this.insertUserPost(user);
        this.insertUserRole(user);
        try {
            updateUserTerminal(user);
        } catch (Exception e) {
            throw e;
        }
        return rows;
    }

    @Transactional
    public int updateUser(BusiUser user) {
        Long userId = user.getUserId();
        this.userRoleMapper.deleteUserRoleByUserId(userId);
        this.insertUserRole(user);
        this.userPostMapper.deleteUserPostByUserId(userId);
        this.insertUserPost(user);
        int rows = this.userMapper.updateUser(user);
        try {
            updateUserTerminal(user);
        } catch (Exception e) {
            throw e;
        }
        return rows;
    }

    public int updateUserStatus(BusiUser user) {
        return this.userMapper.updateUser(user);
    }

    public int updateUserProfile(BusiUser user) {
        return this.userMapper.updateUser(user);
    }

    public boolean updateUserAvatar(String userName, String avatar) {
        return this.userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    public int resetPwd(BusiUser user) {
        int rows = this.userMapper.updateUser(user);
        if (rows > 0) {
            updateLoginError(user.getUserName(), true);
        }
        return rows;
    }

    public int resetUserPwd(String userName, String password) {
        int rows = this.userMapper.resetUserPwd(userName, password);
        if (rows > 0) {
            updateLoginError(userName, true);
        }
        return rows;
    }

    public void insertUserRole(BusiUser user) {
        Long[] roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles)) {
            List<SysUserRole> list = new ArrayList();
            Long[] var4 = roles;
            int var5 = roles.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Long roleId = var4[var6];
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }

            if (list.size() > 0) {
                this.userRoleMapper.batchUserRole(list);
            }
        }

    }

    public void insertUserPost(BusiUser user) {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts)) {
            List<SysUserPost> list = new ArrayList();
            Long[] var4 = posts;
            int var5 = posts.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Long postId = var4[var6];
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }

            if (list.size() > 0) {
                this.userPostMapper.batchUserPost(list);
            }
        }

    }

    @Transactional
    public int deleteUserById(Long userId) {
        BusiUser busiUser = selectUserById(userId);
        if (busiUser.getDeptId() >= 1 && busiUser.getDeptId() < 100) {
            throw new CustomException("该用户为系统保留部门用户，不可删除！");
        }
        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(userId);
        if (busiUserTerminal != null) {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminal.getTerminalId());
            if (busiTerminal != null) {
                if (SipAccountUtil.isAutoAccount(busiTerminal.getCredential())) {
                    try {
                        busiTerminalService.deleteBusiTerminalById(busiTerminal.getId());
                    } catch (Exception e) {
                        logger.error("终端删除失败。terminalId:" + busiTerminal.getId(), e);
                    }
                }
            }
            busiUserTerminalMapper.deleteBusiUserTerminalById(busiUserTerminal.getId());
        }
        this.userRoleMapper.deleteUserRoleByUserId(userId);
        this.userPostMapper.deleteUserPostByUserId(userId);
        return deleteUserByUserId(userId);
    }

    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        Long[] var2 = userIds;
        int var3 = userIds.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Long userId = var2[var4];
            this.checkUserAllowed(new BusiUser(userId));
            BusiUser busiUser = selectUserById(userId);
            if (busiUser.getDeptId() >= 1 && busiUser.getDeptId() < 100) {
                throw new CustomException("该用户为系统保留部门用户，不可删除！");
            }
            BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(userId);
            if (busiUserTerminal != null) {
                busiUserTerminalMapper.deleteBusiUserTerminalById(busiUserTerminal.getId());
                BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminal.getTerminalId());
                if (busiTerminal != null) {
                    if (SipAccountUtil.isAutoAccount(busiTerminal.getCredential())) {
                        try {
                            busiTerminalService.deleteBusiTerminalById(busiTerminal.getId());
                        } catch (Exception e) {
                            logger.error("终端删除失败。terminalId:" + busiTerminal.getId(), e);
                        }
                    }
                }
            }
        }

        this.userRoleMapper.deleteUserRole(userIds);
        this.userPostMapper.deleteUserPost(userIds);
        return deleteUserByUserIds(userIds);
    }

    public String importUser(List<BusiUser> userList, Boolean isUpdateSupport, String operName) {
        if (!StringUtils.isNull(userList) && userList.size() != 0) {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder successMsg = new StringBuilder();
            StringBuilder failureMsg = new StringBuilder();
            String password = this.configService.selectConfigByKey("sys.user.initPassword");
            Iterator var9 = userList.iterator();

            while (var9.hasNext()) {
                BusiUser user = (BusiUser) var9.next();

                try {
                    if (user.getDeptId() != null) {
                        if (user.getDeptId() > 1 && user.getDeptId() < 100) {
                            throw new Exception("部门ID：" + user.getDeptId() + "为系统保留部门，不能添加用户。");
                        }
                    }
                    SysUser u = this.userMapper.selectUserByUserName(user.getUserName());
                    if (StringUtils.isNull(u)) {
                        user.setPassword(SecurityUtils.encryptPassword(password));
                        user.setCreateBy(operName);
                        this.insertUser(user);
                        ++successNum;
                        successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                        try {
                            updateUserTerminal(user);
                        } catch (Exception e) {
                            successMsg.append("<br/>" + e.getMessage());
                        }
                    } else if (isUpdateSupport) {
                        user.setUpdateBy(operName);
                        this.updateUser(user);
                        ++successNum;
                        successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                        try {
                            updateUserTerminal(user);
                        } catch (Exception e) {
                            successMsg.append("<br/>" + e.getMessage());
                        }
                    } else {
                        ++failureNum;
                        failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                    }
                } catch (Exception var13) {
                    ++failureNum;
                    String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                    failureMsg.append(msg + var13.getMessage());
                    logger.error(msg, var13);
                }
            }

            if (failureNum > 0) {
                failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
                throw new CustomException(failureMsg.toString());
            } else {
                successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                return successMsg.toString();
            }
        } else {
            throw new CustomException("导入用户数据不能为空！");
        }
    }

    private void updateUserTerminal(BusiUser user) throws CustomException {
        try {
            if (user.getTerminalId() == null && user.getTerminalType() == null) {
                BusiUserTerminal busiUserTerminalExist = busiUserTerminalMapper.selectBusiUserTerminalByUserId(user.getUserId());
                if (busiUserTerminalExist != null) {
                    BusiTerminal busiTerminalExist = busiTerminalMapper.selectBusiTerminalById(busiUserTerminalExist.getTerminalId());
                    if (SipAccountUtil.isAutoAccount(busiTerminalExist.getCredential())) {
                        try {
                            busiTerminalMapper.deleteBusiTerminalById(busiTerminalExist.getId());
                        } catch (Exception e) {
                            throw new CustomException("终端解绑失败：该终端在模板中或在会议中");
                        }
                    }
                    busiUserTerminalMapper.deleteBusiUserTerminalById(busiUserTerminalExist.getId());
                }
            } else if (user.getTerminalId() != null) {
                BusiUserTerminal busiUserTerminalExist = busiUserTerminalMapper.selectBusiUserTerminalByUserId(user.getUserId());
                if (busiUserTerminalExist != null) {
                    if (user.getTerminalId().longValue() != busiUserTerminalExist.getTerminalId().longValue()) {
                        BusiTerminal busiTerminalExist = busiTerminalMapper.selectBusiTerminalById(busiUserTerminalExist.getTerminalId());
                        busiUserTerminalMapper.deleteBusiUserTerminalById(busiUserTerminalExist.getId());
                        if (SipAccountUtil.isAutoAccount(busiTerminalExist.getCredential())) {
                            try {
                                busiTerminalMapper.deleteBusiTerminalById(busiTerminalExist.getId());
                            } catch (Exception e) {
                                throw new CustomException("终端解绑失败：该终端在模板中或在会议中");
                            }
                        }

                        BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(user.getTerminalId());
                        if (busiTerminal == null) {
                            throw new CustomException("终端绑定失败：该终端不存在或已被删除。");
                        }
                        if (busiTerminal.getDeptId() != user.getDeptId()) {
                            throw new CustomException("终端绑定失败：只能绑定同部门下的终端。");
                        }
                        BusiUserTerminal busiUserTerminalOtherBound = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(user.getTerminalId());
                        if (busiUserTerminalOtherBound != null && busiUserTerminalOtherBound.getUserId().longValue() != user.getUserId()) {
                            throw new CustomException("终端绑定失败：该终端已经被其他用户绑定。");
                        }
                        if (busiUserTerminalOtherBound == null) {
                            if (!TerminalType.isFSIP(busiTerminal.getType())) {
                                throw new CustomException("终端绑定失败：终端只能绑定FSBC-SIP或者FCM-SIP或者ZJ-SIP类型的终端。");
                            }
                            BusiUserTerminal userTerminal = new BusiUserTerminal();
                            userTerminal.setUserId(user.getUserId());
                            userTerminal.setTerminalId(user.getTerminalId());
                            busiUserTerminalMapper.insertBusiUserTerminal(userTerminal);
                        }
                    }
                } else {
                    BusiUserTerminal busiUserTerminalBound = busiUserTerminalMapper.selectBusiUserTerminalByTerminalId(user.getTerminalId());
                    if (busiUserTerminalBound != null) {
                        throw new CustomException("终端绑定失败：该终端已经被其他用户绑定。");
                    } else {
                        BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(user.getTerminalId());
                        if (busiTerminal == null) {
                            throw new CustomException("终端绑定失败：该终端不存在或已被删除。");
                        }
                        if (!TerminalType.isFSIP(busiTerminal.getType())) {
                            throw new CustomException("终端绑定失败：终端只能绑定FSBC-SIP或者FCM-SIP类型的终端。");
                        }
                        BusiUserTerminal userTerminal = new BusiUserTerminal();
                        userTerminal.setUserId(user.getUserId());
                        userTerminal.setTerminalId(user.getTerminalId());
                        busiUserTerminalMapper.insertBusiUserTerminal(userTerminal);
                    }
                }
            } else if (user.getTerminalType() != null) {
                if (!TerminalType.isFSIP(user.getTerminalType())) {
                    throw new CustomException("终端绑定失败：终端只能绑定FSBC-SIP或者FCM-SIP或者ZJ-SIP类型的终端。");
                }
                if (TerminalType.isZJ(user.getTerminalType())) {
                    throw new CustomException("终端绑定失败：请选择要绑定的ZJ-SIP类型的终端。");
                }
                {
                    BusiUserTerminal busiUserTerminalExist = busiUserTerminalMapper.selectBusiUserTerminalByUserId(user.getUserId());
                    if (busiUserTerminalExist != null) {
                        BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminalExist.getTerminalId());
                        if (busiTerminal != null) {
                            if ((TerminalType.isFSBC(user.getTerminalType()) && TerminalType.isFSBC(busiTerminal.getType()))
                                    || (TerminalType.isFCMSIP(user.getTerminalType()) && TerminalType.isFCMSIP(busiTerminal.getType()))
                                    || (TerminalType.isZJ(user.getTerminalType()) && TerminalType.isZJ(busiTerminal.getType()))) {
                                return;
                            } else {
                                if (SipAccountUtil.isAutoAccount(busiTerminal.getCredential())) {
                                    try {
                                        busiTerminalMapper.deleteBusiTerminalById(busiTerminal.getId());
                                    } catch (Exception e) {
                                        throw new CustomException("终端解绑失败：该终端在模板中或在会议中");
                                    }
                                }
                                busiUserTerminalMapper.deleteBusiUserTerminalById(busiUserTerminalExist.getId());
                            }
                        } else {
                            busiUserTerminalMapper.deleteBusiUserTerminalById(busiUserTerminalExist.getId());
                        }
                    }
                }
                BusiTerminal busiTerminal = new BusiTerminal();
                try {
                    BusiSipAccountAuto busiSipAccountAuto = new BusiSipAccountAuto();
                    busiSipAccountAuto.setCreateTime(new Date());
                    busiSipAccountAutoMapper.insertBusiSipAccountAuto(busiSipAccountAuto);
                    String credential = SipAccountUtil.createAccount(busiSipAccountAuto.getId());
                    String password = SipAccountUtil.createPassword();
                    busiTerminal.setAttendType(AttendType.AUTO_JOIN.getValue());
                    busiTerminal.setBusinessFieldType(100);// BusinessFieldType.COMMON
                    busiTerminal.setCredential(credential);
                    busiTerminal.setPassword(password);
                    busiTerminal.setDeptId(user.getDeptId());
                    busiTerminal.setType(user.getTerminalType());
                    busiTerminal.setName(user.getNickName());
                    int c = busiTerminalService.insertBusiTerminal(busiTerminal, true);
                    if (c > 0) {
                    }
                } catch (Exception e) {
                    if (e instanceof SystemException || e instanceof CustomException) {
                        throw e;
                    } else {
                        throw new CustomException("终端绑定失败：" + e.getMessage() + "。");
                    }
                }

                try {
                    BusiUserTerminal userTerminal = new BusiUserTerminal();
                    userTerminal.setUserId(user.getUserId());
                    userTerminal.setTerminalId(busiTerminal.getId());
                    busiUserTerminalMapper.insertBusiUserTerminal(userTerminal);
                } catch (Exception e) {
                    throw new CustomException("终端绑定失败：" + e.getMessage() + "。");
                }
            }
        } catch (Exception e) {
            if (e instanceof CustomException) {
                throw e;
            } else {
                throw new CustomException("终端绑定失败：" + e.getMessage() + "。");
            }
        }
    }

    /**
     * 查询未绑定用户的终端信息列表
     *
     * @param busiTerminal 终端信息
     * @return 终端信息
     */
    @Override
    public List<BusiTerminal> selectBusiTerminalListOfNotBoundByUser(BusiTerminal busiTerminal)
    {
        return busiTerminalMapper.selectBusiTerminalListOfNotBoundByUser(busiTerminal);
    }

    /**
     * 查询未绑定用户的终端信息列表
     *
     * @param userId 用户ID
     * @return 终端信息
     */
    @Override
    public SipAccountInfo getUserSipAccountInfo(Long userId, boolean isExternalRequest)
    {
        BusiUserTerminal busiUserTerminal = busiUserTerminalMapper.selectBusiUserTerminalByUserId(userId);
        if (busiUserTerminal != null) {
            BusiTerminal busiTerminal = busiTerminalMapper.selectBusiTerminalById(busiUserTerminal.getTerminalId());
            if (busiTerminal != null) {
                BusiUser busiUser = selectUserById(userId);
                SipAccountInfo sipAccountInfo = TerminalSipAccount.getInstance().getTerminalSipAccountInfo(busiTerminal, isExternalRequest);
                if (busiUser != null && StringUtils.isNotEmpty(busiUser.getNickName())) {
                    sipAccountInfo.setDisplayName(busiUser.getNickName());
                }
                return sipAccountInfo;
            }
        }
        return null;
    }

    public int isLoginLocked(String userName) {
        String loginUserKey = "login_error_count:" + userName;
        String expireTimeKey = "login_error_expire:" + userName;
        Object obj = redisCache.getCacheObject(loginUserKey);
        if (obj != null) {
            int loginErrorCount = (int) obj;
            if (loginErrorCount >= 5) {
                Object objEx = redisCache.getCacheObject(expireTimeKey);
                if (objEx != null) {
                    return loginErrorCount;
                }
            }
        }
        return 0;
    }

    public int unlockLogin(BusiUser busiUser) {
        BusiUser busiUserExist = selectUserById(busiUser.getUserId());
        if (busiUserExist != null) {
            updateLoginError(busiUserExist.getUserName(), true);
            return 1;
        }
        return 0;
    }

    public int updateLoginError(String userName, boolean success) {
        int loginErrorCount = 0;
        String loginUserKey = "login_error_count:" + userName;
        String expireTimeKey = "login_error_expire:" + userName;
        if (success) {
            redisCache.deleteObject(loginUserKey);
            redisCache.deleteObject(expireTimeKey);
        } else {
            Object obj = redisCache.getCacheObject(loginUserKey);
            if (obj != null) {
                loginErrorCount = (int) obj;
            }
            loginErrorCount = loginErrorCount + 1;
            redisCache.setCacheObject(loginUserKey, loginErrorCount);
            redisCache.setCacheObject(expireTimeKey, loginErrorCount);
            redisCache.expire(expireTimeKey, loginErrorCount, TimeUnit.MINUTES);
            BusiUser busiUser = selectUserByUserName(userName);
            if (busiUser != null) {
                redisCache.expire(loginUserKey, 12, TimeUnit.HOURS);
            } else {
                redisCache.expire(loginUserKey, 5, TimeUnit.MINUTES);
            }
        }
        return loginErrorCount;
    }

    /**
     * 查询用户信息列表
     * @param deptId
     * @return
     */
    @Override
    public List<SysUser> selectUserListByDeptId(Long deptId) {
        List<Long> deptIds = null;
        if (deptId != null) {
            deptIds = new ArrayList<>();
        }

        List<SysUser> sysUserList = new ArrayList<>();
        SysDept dept = new SysDept();
        dept.setDeptId(deptId);
        List<SysDept> ds = sysDeptService.selectDeptList(dept);
        for (SysDept sysDept : ds) {
            if (deptIds != null) {
                deptIds.add(sysDept.getDeptId());
            }
        }
        for (Long id : deptIds) {
            BusiUser busiUser = new BusiUser();
            busiUser.setDeptId(id);
            sysUserList.addAll(userMapper.selectUserList(busiUser));
        }
        return sysUserList;
    }

    private int deleteUserByUserIds(Long[] userIds) {
        int rows = 0;
        for (Long userId : userIds) {
            rows += deleteUserByUserId(userId);
        }
        return rows;
    }

    private int deleteUserByUserId(Long userId) {
        userMapper.deleteUserByIds(new Long[]{userId});
        SysUser sysUser = userMapper.selectUserById(userId);
        String remark = sysUser.getUserName();
        if (sysUser.getPhonenumber() != null) {
            remark += "," + sysUser.getPhonenumber();
        }
        if (sysUser.getEmail() != null) {
            remark += "," + sysUser.getEmail();
        }
        sysUser.setRemark(remark);
        sysUser.setUserName("deleted_" + System.currentTimeMillis() + "_" + generateRandom());
        return userMapper.updateUserForDelete(sysUser);
    }

    private String generateRandom() {
        String str = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            str += "" + random.nextInt(9);
        }
        return str;
    }
}
