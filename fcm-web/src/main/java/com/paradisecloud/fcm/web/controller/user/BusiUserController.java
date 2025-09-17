package com.paradisecloud.fcm.web.controller.user;

import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.page.PaginationData;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.model.ModelBean;
import com.paradisecloud.common.utils.ServletUtils;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiUser;
import com.paradisecloud.fcm.dao.model.vo.TerminalSearchVo;
import com.paradisecloud.fcm.terminal.fs.db.FreeSwitchTransaction;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.dao.model.SysRole;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.ExcelUtil;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysPostService;
import com.paradisecloud.system.service.ISysRoleService;
import com.paradisecloud.system.utils.SecurityUtils;

import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping({"/busi/user"})
public class BusiUserController extends BaseController {

    @Resource
    private IBusiUserService userService;
    @Resource
    private ISysRoleService roleService;
    @Resource
    private ISysPostService postService;
    @Resource
    private TokenService tokenService;

    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping({"/list"})
    public RestResponse list(BusiUser user) {
        this.startPage();
        List<BusiUser> list = this.userService.selectUserList(user);
        return this.getDataTable(list);
    }

    @GetMapping({"/searchUserList"})
    public RestResponse searchUserList(BusiUser user) {
        this.startPage();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        user.setDeptId(loginUser.getUser().getDeptId());
        List<BusiUser> list = this.userService.selectUserList(user);
        return this.getDataTable(list);
    }

    @Log(
            title = "用户管理",
            businessType = BusinessType.EXPORT
    )
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping({"/export"})
    public RestResponse export(BusiUser user) {
        List<BusiUser> list = this.userService.selectUserList(user);
        ExcelUtil<BusiUser> util = new ExcelUtil(BusiUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(
            title = "用户管理",
            businessType = BusinessType.IMPORT
    )
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping({"/importData"})
    public RestResponse importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<BusiUser> util = new ExcelUtil(BusiUser.class);
        List<BusiUser> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = this.userService.importUser(userList, updateSupport, operName);
        return RestResponse.success(message);
    }

    @GetMapping({"/importTemplate"})
    public RestResponse importTemplate() {
        ExcelUtil<BusiUser> util = new ExcelUtil(BusiUser.class);
        return util.importTemplateExcel("用户数据");
    }

    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping({"/", "/{userId}"})
    public RestResponse getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        Map<String, Object> data = new HashMap();
        BusiUser user = null;
        SysUser con = new SysUser();
        if (userId != null) {
            user = this.userService.selectUserById(userId);
        }

        con.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        data.put("roles", this.roleService.selectRoleList(new SysRole(), con));
        data.put("posts", this.postService.selectPostAll());
        if (StringUtils.isNotNull(userId)) {
            data.put("user", user);
            data.put("postIds", this.postService.selectPostListByUserId(userId));
            data.put("roleIds", this.roleService.selectRoleListByUserId(userId));
        }

        return RestResponse.success(data);
    }

    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(
            title = "用户管理",
            businessType = BusinessType.INSERT
    )
    @PostMapping
    @FreeSwitchTransaction
    @Operation(summary = "", description = "新增用户")
    public RestResponse add(@Validated @RequestBody BusiUser user) {
        if ("1".equals(this.userService.checkUserNameUnique(user.getUserName()))) {
            return RestResponse.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && "1".equals(this.userService.checkPhoneUnique(user))) {
            return RestResponse.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && "1".equals(this.userService.checkEmailUnique(user))) {
            return RestResponse.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        } else {
            user.setCreateBy(SecurityUtils.getUsername());
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
            int i = this.userService.insertUser(user);
            if (i > 0) {
                return RestResponse.success(user);
            } else {
                return this.toAjax(i);
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(
            title = "用户管理",
            businessType = BusinessType.UPDATE
    )
    @PutMapping
    @FreeSwitchTransaction
    @Operation(summary = "", description = "修改用户")
    public RestResponse edit(@Validated @RequestBody BusiUser user) {
        this.userService.checkUserAllowed(user);
        BusiUser busiUser = userService.selectUserById(user.getUserId());
        if (busiUser.getDeptId() > 1 && busiUser.getDeptId() < 100) {
            return RestResponse.fail("该用户为系统保留部门用户，不可修改！");
        }
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && "1".equals(this.userService.checkPhoneUnique(user))) {
            return RestResponse.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && "1".equals(this.userService.checkEmailUnique(user))) {
            return RestResponse.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        } else {
            user.setUpdateBy(SecurityUtils.getUsername());
            int rows = this.userService.updateUser(user);
            return this.toAjax(rows);
        }
    }

    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(
            title = "用户管理",
            businessType = BusinessType.DELETE
    )
    @DeleteMapping({"/{userIds}"})
    @FreeSwitchTransaction
    @Operation(summary = "", description = "删除用户")
    public RestResponse remove(@PathVariable Long[] userIds) {
        return this.toAjax(this.userService.deleteUserByIds(userIds));
    }

    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(
            title = "用户管理",
            businessType = BusinessType.UPDATE
    )
    @PutMapping({"/resetPwd"})
    @Operation(summary = "", description = "重置密码")
    public RestResponse resetPwd(@RequestBody BusiUser user) {
        this.userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return this.toAjax(this.userService.resetPwd(user));
    }

    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(
            title = "用户管理",
            businessType = BusinessType.UPDATE
    )
    @PutMapping({"/changeStatus"})
    @Operation(summary = "", description = "修改用户状态")
    public RestResponse changeStatus(@RequestBody BusiUser user) {
        this.userService.checkUserAllowed(user);
        BusiUser busiUser = userService.selectUserById(user.getUserId());
        if (busiUser.getDeptId() > 1 && busiUser.getDeptId() < 100) {
            return RestResponse.fail("该用户为系统保留部门用户，不可修改！");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return this.toAjax(this.userService.updateUserStatus(user));
    }

    /**
     * 查询终端信息列表
     */
    @GetMapping("/terminal/list")
    @Operation(summary = "查询终端信息列表")
    public RestResponse list(BusiTerminal busiTerminal)
    {
        Assert.notNull(busiTerminal.getDeptId(), "部门ID deptId不能为空！");
        startPage();
        List<BusiTerminal> list = userService.selectBusiTerminalListOfNotBoundByUser(busiTerminal);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(
            title = "用户管理",
            businessType = BusinessType.UPDATE
    )
    @PostMapping("/login/unlock")
    @Operation(summary = "", description = "解锁")
    public RestResponse unlockUserLogin(@RequestBody BusiUser user) {
        if (user == null || user.getUserId() == null) {
            Assert.isTrue(false, "用户ID不能为空！");
        }
        return this.toAjax(userService.unlockLogin(user));
    }

    /**
     * 查询用户信息列表
     */
    @GetMapping("/getUserListBydeptId/{deptId}")
    @Operation(summary = "查询用户信息列表")
    public RestResponse getUserListBydeptId(@PathVariable Long deptId)
    {
        List<SysUser> busiUsers = userService.selectUserListByDeptId(deptId);
        return RestResponse.success(busiUsers);
    }
}
