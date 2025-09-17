package com.paradisecloud.fcm.web.controller.user;

import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.model.model.LoginBody;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.ServletUtils;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.Region;
import com.paradisecloud.fcm.common.utils.RSAUtil;
import com.paradisecloud.fcm.dao.mapper.BusiOpsMapper;
import com.paradisecloud.fcm.dao.model.BusiOps;
import com.paradisecloud.fcm.dao.model.SipAccountInfo;
import com.paradisecloud.fcm.web.service.impls.BusiUserLoginService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.framework.web.service.SysPermissionService;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.dao.mapper.SysUserMapper;
import com.paradisecloud.system.dao.model.SysMenu;
import com.paradisecloud.system.dao.model.SysUser;
import com.paradisecloud.system.model.LoginUser;
import com.paradisecloud.system.service.ISysMenuService;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping({"/user"})
public class UserLoginController {

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
    private TokenService tokenService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private BusiOpsMapper busiOpsMapper;

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private IBusiUserService userService;

    public UserLoginController() {
    }

    @PostMapping({"/login"})
    public RestResponse login(HttpServletRequest request, @RequestBody LoginBody loginBody) {
        Map<String, Object> data = new HashMap();
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
        data.put("token", token);
        data.put("region", ExternalConfigCache.getInstance().getRegion());
        String title = ExternalConfigCache.getInstance().getTitle();
        if (StringUtils.isEmpty(title)) {
            title = an;
        }
        data.put("title", title);
        data.put("mcuType", ExternalConfigCache.getInstance().getMcuType());
        boolean isExternalRequest = false;
        String requestUrl = request.getRequestURL().toString();
        String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
        String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(fmcRootUrlExternal)) {
            if (fmcRootUrlExternal.equals(fmcRootUrl)) {
                isExternalRequest = true;
            }
            if (requestUrl.startsWith(fmcRootUrlExternal)) {
                isExternalRequest = true;
            }
            try {
                String referer = request.getHeader("referer");
                String ip = referer.replace("http://", "").replace("https://", "");
                if (ip.indexOf(":") > 0) {
                    ip = ip.substring(0, ip.indexOf(":"));
                }
                if (ip.indexOf("/") > 0) {
                    ip = ip.substring(0, ip.indexOf("/"));
                }
                String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                if (externalIp.indexOf(":") > 0) {
                    externalIp = externalIp.substring(0, externalIp.indexOf(":"));
                }
                if (externalIp.indexOf("/") > 0) {
                    externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                }
                if (externalIp.equals(ip)) {
                    isExternalRequest = true;
                }
            } catch (Exception e) {
            }
        }
        SysUser sysUser = sysUserMapper.selectUserByUserName(userName);
        if (sysUser == null) {
            return RestResponse.fail("用户不存在或者无权限使用！");
        }
        SipAccountInfo userSipAccountInfo = getSipAccountInfo(sysUser.getUserId(), isExternalRequest);
        data.put("userSipAccountInfo", userSipAccountInfo);
        return RestResponse.success(data);
    }

    @PostMapping({"/loginAuto"})
    public RestResponse loginAuto() {
        String region = ExternalConfigCache.getInstance().getRegion();
        Map<String, Object> data = new HashMap();
        RSAUtil.SignCertInfo signCertInfo = CommonConfigCache.getInstance().getSignCertInfo();
        if (signCertInfo != null) {
            if (signCertInfo.getPublicKey() != null) {
                data.put("publicKey", RSAUtil.encryptBASE64(signCertInfo.getPublicKey().getEncoded()));
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

    private SipAccountInfo getSipAccountInfo(Long userId, boolean isExternalRequest) {
        return userService.getUserSipAccountInfo(userId, isExternalRequest);
    }

    @GetMapping({"getInfo"})
    public RestResponse getInfo(HttpServletRequest request) {
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest(), singleLogin);
        if (loginUser != null) {
            SysUser user = loginUser.getUser();
            Set<String> roles = this.permissionService.getRolePermission(user);
            Set<String> permissions = this.permissionService.getMenuPermission(user);
            Map<String, Object> data = new HashMap();
            data.put("user", user);
            data.put("roles", roles);
            data.put("permissions", permissions);
            boolean isExternalRequest = false;
            String requestUrl = request.getRequestURL().toString();
            String fmcRootUrl = ExternalConfigCache.getInstance().getFmcRootUrl();
            String fmcRootUrlExternal = ExternalConfigCache.getInstance().getFmcRootUrlExternal();
            if (StringUtils.isNotEmpty(fmcRootUrlExternal)) {
                if (fmcRootUrlExternal.equals(fmcRootUrl)) {
                    isExternalRequest = true;
                }
                if (requestUrl.startsWith(fmcRootUrlExternal)) {
                    isExternalRequest = true;
                }
                try {
                    String referer = request.getHeader("referer");
                    String ip = referer.replace("http://", "").replace("https://", "");
                    if (ip.indexOf(":") > 0) {
                        ip = ip.substring(0, ip.indexOf(":"));
                    }
                    if (ip.indexOf("/") > 0) {
                        ip = ip.substring(0, ip.indexOf("/"));
                    }
                    String externalIp = fmcRootUrlExternal.replace("http://", "").replace("https://", "");
                    if (externalIp.indexOf(":") > 0) {
                        externalIp = externalIp.substring(0, externalIp.indexOf(":"));
                    }
                    if (externalIp.indexOf("/") > 0) {
                        externalIp = externalIp.substring(0, externalIp.indexOf("/"));
                    }
                    if (externalIp.equals(ip)) {
                        isExternalRequest = true;
                    }
                } catch (Exception e) {
                }
            }
            SipAccountInfo userSipAccountInfo = getSipAccountInfo(user.getUserId(), isExternalRequest);
            if (Objects.isNull(userSipAccountInfo)) {
                throw new CustomException("登录失败,账户未绑定终端");
            }
            data.put("userSipAccountInfo", userSipAccountInfo);
            return RestResponse.success(data);
        } else {
            return RestResponse.fail();
        }
    }

    @GetMapping({"getRouters"})
    public RestResponse getRouters() {
        LoginUser loginUser = this.tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = this.menuService.selectMenuTreeByUserId(user.getUserId());
        return RestResponse.success(this.menuService.buildMenus(menus));
    }

    @PostMapping({"/loginNoCode"})
    public RestResponse loginNoCode(@RequestBody LoginBody loginBody) {
        String userName = loginBody.getUsername();
        String password = loginBody.getPassword();

        String token = this.busiUserLoginService.loginNoCode(userName, password);
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

    @GetMapping({"/loginSn"})
    public RestResponse loginNoCode(@RequestParam String sn) {
        BusiOps busiOps_query = new BusiOps();
        busiOps_query.setSn(sn);
        List<BusiOps> busiOps = busiOpsMapper.selectBusiOpsList(busiOps_query);
        if(CollectionUtils.isEmpty(busiOps)){
            return RestResponse.fail();
        }
        BusiOps busiOps1 = busiOps.get(0);

        Long userId = busiOps1.getUserId();
        SysUser sysUser = sysUserMapper.selectUserById(userId);
        String token = this.busiUserLoginService.loginNoCode(sysUser.getUserName(), sysUser.getPassword());
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
}

