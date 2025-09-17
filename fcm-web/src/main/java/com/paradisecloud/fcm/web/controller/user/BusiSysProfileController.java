package com.paradisecloud.fcm.web.controller.user;


import com.paradisecloud.common.annotation.Log;
import com.paradisecloud.common.config.ApplicationConfig;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.enums.BusinessType;
import com.paradisecloud.common.utils.ServletUtils;
import com.paradisecloud.common.utils.file.FileUploadUtils;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysUserService;
import com.paradisecloud.system.utils.SecurityUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/busi/user/profile"})
public class BusiSysProfileController extends BaseController {
    @Autowired
    private ISysUserService userService;
    @Autowired
    private TokenService tokenService;

    public BusiSysProfileController() {
    }

    @GetMapping
    @Operation(summary = "", description = "")
    public RestResponse profile() {
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        Map<String, Object> data = new HashMap();
        data.put("roleGroup", this.userService.selectUserRoleGroup(loginUser.getUsername()));
        data.put("postGroup", this.userService.selectUserPostGroup(loginUser.getUsername()));
        data.put("user", user);
        return RestResponse.success(data);
    }

    @Log(
            title = "个人信息",
            businessType = BusinessType.UPDATE
    )
    @PutMapping
    @Operation(summary = "", description = "用户修改个人信息")
    public RestResponse updateProfile(@RequestBody SysUser user) {
        if (this.userService.updateUserProfile(user) > 0) {
            LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
            loginUser.getUser().setNickName(user.getNickName());
            loginUser.getUser().setPhonenumber(user.getPhonenumber());
            loginUser.getUser().setEmail(user.getEmail());
            loginUser.getUser().setSex(user.getSex());
            this.tokenService.setLoginUser(loginUser);
            return RestResponse.success();
        } else {
            return RestResponse.fail("修改个人信息异常，请联系管理员");
        }
    }

    @Log(
            title = "个人信息",
            businessType = BusinessType.UPDATE
    )
    @PutMapping({"/updatePwd"})
    @Operation(summary = "", description = "用户修改密码")
    public RestResponse updatePwd(String oldPassword, String newPassword) {
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return RestResponse.fail("修改密码失败，旧密码错误");
        } else if (SecurityUtils.matchesPassword(newPassword, password)) {
            return RestResponse.fail("新密码不能与旧密码相同");
        } else if (this.userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0) {
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            this.tokenService.setLoginUser(loginUser);
            return RestResponse.success();
        } else {
            return RestResponse.fail("修改密码异常，请联系管理员");
        }
    }

    @Log(
            title = "用户头像",
            businessType = BusinessType.UPDATE
    )
    @PostMapping({"/avatar"})
    public RestResponse avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
            String avatar = FileUploadUtils.upload(ApplicationConfig.getAvatarPath(), file);
            if (this.userService.updateUserAvatar(loginUser.getUsername(), avatar)) {
                Map<String, Object> data = new HashMap();
                data.put("imgUrl", avatar);
                loginUser.getUser().setAvatar(avatar);
                this.tokenService.setLoginUser(loginUser);
                return RestResponse.success(data);
            }
        }

        return RestResponse.fail("上传图片异常，请联系管理员");
    }
}

