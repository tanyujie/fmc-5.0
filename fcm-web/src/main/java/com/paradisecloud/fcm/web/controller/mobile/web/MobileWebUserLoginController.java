package com.paradisecloud.fcm.web.controller.mobile.web;

import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.model.model.LoginBody;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.utils.ServletUtils;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.Region;
import com.paradisecloud.fcm.common.utils.RSAUtil;
import com.paradisecloud.fcm.web.service.impls.BusiUserLoginService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.framework.web.service.SysPermissionService;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.dao.model.SysMenu;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysMenuService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({"/mobileWeb/user"})
public class MobileWebUserLoginController {

    @Value("${application.name:}")
    private String an;
    @Value("${application.singleLogin:}")
    private boolean singleLogin;
    @Resource
    private BusiUserLoginService busiUserLoginService;
    @Resource
    private ISysMenuService menuService;
    @Resource
    private SysPermissionService permissionService;
    @Resource
    private TokenService tokenService;;
    @Resource
    private RedisCache redisCache;
    @Resource
    private IBusiUserService userService;

    public MobileWebUserLoginController() {
    }

    @PostMapping({"/login"})
    public RestResponse login(@RequestBody LoginBody loginBody) {
        String userName = loginBody.getUsername();
        String password = loginBody.getPassword();
        RSAUtil.SignCertInfo signCertInfo = CommonConfigCache.getInstance().getSignCertInfo();
        if (signCertInfo != null) {
            if (signCertInfo.getPrivateKey() != null) {
                userName = RSAUtil.decrypt(userName, signCertInfo.getPrivateKey());
                password = RSAUtil.decrypt(password, signCertInfo.getPrivateKey());
            }
        }
        String token = this.busiUserLoginService.login(userName, password, loginBody.getCode(), loginBody.getUuid(), singleLogin);
        Map<String, Object> data = new HashMap();
        data.put("token", token);
        data.put("region", ExternalConfigCache.getInstance().getRegion());
        String title = ExternalConfigCache.getInstance().getTitle();
        if (StringUtils.isEmpty(title)) {
            title = an;
        }
        data.put("title", title);
        data.put("mcuType", ExternalConfigCache.getInstance().getMcuType());
        return RestResponse.success(data);
    }

    @PostMapping({"/loginAuto"})
    public RestResponse loginAuto() {
        String region = ExternalConfigCache.getInstance().getRegion();
        Map<String, Object> data = new HashMap();
        RSAUtil.SignCertInfo signCertInfo = CommonConfigCache.getInstance().getSignCertInfo();
        if (signCertInfo != null) {
            if (signCertInfo.getPublicKey() != null) {
                data.put("publicKey", RSAUtil.encryptBASE64(signCertInfo.getPrivateKey().getEncoded()));
            }
        }
        data.put("region", region);
        String title = ExternalConfigCache.getInstance().getTitle();
        if (StringUtils.isEmpty(title)) {
            title = an;
        }
        data.put("title", title);
        data.put("mcuType", ExternalConfigCache.getInstance().getMcuType());
        if (!Region.HUNAN_JIDU.getCode().equals(region)) {
            return RestResponse.fail(0, "", data);
        }
        String userName = ExternalConfigCache.getInstance().getAutoLoginUser();
        String password = ExternalConfigCache.getInstance().getAutoLoginPassword();
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return RestResponse.fail(0, "", data);
        }
        try {
            String uuid = IdUtils.simpleUUID();
            String verifyKey = "captcha_codes:" + uuid;
            String code = "999";
            this.redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
            String token = this.busiUserLoginService.login(userName, password, code, uuid);
            data.put("token", token);
            return RestResponse.success(data);
        } catch (Exception e) {
            return RestResponse.fail(0, "");
        }
    }

    @GetMapping({"getInfo"})
    public RestResponse getInfo() {
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest(), singleLogin);
        if (loginUser != null) {
            SysUser user = loginUser.getUser();
            Set<String> roles = this.permissionService.getRolePermission(user);
            Set<String> permissions = this.permissionService.getMenuPermission(user);
            Map<String, Object> data = new HashMap();
            data.put("user", user);
            data.put("roles", roles);
            data.put("permissions", permissions);
            return RestResponse.success(data);
        } else {
            return RestResponse.fail(HttpStatus.UNAUTHORIZED.value(), "");
        }
    }

    @GetMapping({"getRouters"})
    public RestResponse getRouters() {
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = this.menuService.selectMenuTreeByUserId(user.getUserId());
        return RestResponse.success(this.menuService.buildMenus(menus));
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
