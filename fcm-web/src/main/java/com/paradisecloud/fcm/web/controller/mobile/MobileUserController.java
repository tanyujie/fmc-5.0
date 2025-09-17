package com.paradisecloud.fcm.web.controller.mobile;

import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.controller.BaseController;
import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.common.core.model.model.LoginBody;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.fcm.common.cache.CommonConfigCache;
import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.utils.RSAUtil;
import com.paradisecloud.fcm.dao.model.SipAccountInfo;
import com.paradisecloud.fcm.web.model.MobileUserTokenService;
import com.paradisecloud.fcm.web.service.impls.BusiUserLoginService;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.fcm.web.utils.AuthenticationUtil;
import com.paradisecloud.system.model.LoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 移动端用户
 *
 * @author nj
 * @date 2022/6/15 11:33
 */

@RestController
@RequestMapping("/mobile/user")
@Tag(name = "移动端控制器")
@Slf4j
public class MobileUserController extends BaseController {

    @Resource
    private MobileUserTokenService mobileUserTokenService;

    @Resource
    private IBusiUserService userService;
    @Value("${application.name:}")
    private String an;
    @Resource
    private BusiUserLoginService busiUserLoginService;
    @Resource
    private RedisCache redisCache;


    @GetMapping("/getToken")
    public RestResponse getToken(String appSecret) {
        String APP_SECRET = ExternalConfigCache.getInstance().getAppSecret();
        if (Objects.equals(appSecret, APP_SECRET)) {

            String userName = "apiUser";
            String password = "apiUser@2023";
//            String userName = "mobiletest";
//            String password = "mobiletest";
            if (!Objects.equals(userName, "superAdmin") && !Objects.equals(userName, "admin")) {
                // 用户验证
                LoginUser loginUser = mobileUserTokenService.getLoginUser(userName, password);

                // 生成token
                String token = mobileUserTokenService.createToken(loginUser);
                Long expireTime = loginUser.getExpireTime();
                HashMap<Object, Object> obj = new HashMap<>(2);
                obj.put("token",token);
                obj.put("expireTime",expireTime);
                obj.put("apiVer", 4);
                return RestResponse.success(obj);
            }
        }
        return RestResponse.fail("密钥错误");
    }

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public RestResponse login(HttpServletRequest request, @RequestBody LoginBody loginBody) {
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
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();
        // 用户验证
        LoginUser loginUser = mobileUserTokenService.getLoginUser(username, password);
        SipAccountInfo userSipAccountInfo = getSipAccountInfo(loginUser.getUser().getUserId(), isExternalRequest);
        if (Objects.isNull(userSipAccountInfo)) {
            throw new CustomException("登录失败,账户未绑定终端");
        }
        // 生成token
        String token = mobileUserTokenService.createToken(loginUser);
        Map<String, Object> data = new HashMap<>(2);
        data.put("userSipAccountInfo", userSipAccountInfo);
        data.put(Constants.TOKEN, token);
        data.put("apiVer", 4);
        // 删除移动端以前相关用户的token
       // mobileUserTokenService.deleteToken(loginUser);
        return RestResponse.success(data);
    }

    private SipAccountInfo getSipAccountInfo(Long userId, boolean isExternalRequest) {
        return userService.getUserSipAccountInfo(userId, isExternalRequest);
    }


    /**
     * 获取用户详细信息
     */
    @GetMapping("/userSipInfo")
    public RestResponse getInfo(HttpServletRequest request)
    {
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
        Long userId = AuthenticationUtil.getUserId();
        return RestResponse.success(getSipAccountInfo(userId, isExternalRequest));
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
        if (com.paradisecloud.common.utils.StringUtils.isEmpty(title)) {
            title = an;
        }
        data.put("title", title);
        data.put("mcuType", ExternalConfigCache.getInstance().getMcuType());
        String userName = ExternalConfigCache.getInstance().getAutoLoginUser();
        String password = ExternalConfigCache.getInstance().getAutoLoginPassword();
        if (com.paradisecloud.common.utils.StringUtils.isEmpty(userName) || com.paradisecloud.common.utils.StringUtils.isEmpty(password)) {
            return RestResponse.fail(0, "", data);
        }
        try {
            String uuid = IdUtils.simpleUUID();
            String verifyKey = "captcha_codes:" + uuid;
            String code = "999";
            this.redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
            String token = this.busiUserLoginService.login(userName, password, code, uuid);
            data.put("token", token);
            data.put("apiVer", 4);
            return RestResponse.success(data);
        } catch (Exception e) {
            return RestResponse.fail(0, "");
        }
    }

}
