package com.paradisecloud.fcm.web.service.impls;

import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.exception.user.CaptchaException;
import com.paradisecloud.common.exception.user.CaptchaExpireException;
import com.paradisecloud.common.exception.user.UserPasswordNotMatchException;
import com.paradisecloud.common.utils.MessageUtils;
import com.paradisecloud.fcm.web.service.interfaces.IBusiUserService;
import com.paradisecloud.framework.manager.AsyncManager;
import com.paradisecloud.framework.manager.factory.AsyncFactory;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.model.LoginUser;
import javax.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class BusiUserLoginService {
    @Resource
    private TokenService tokenService;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private IBusiUserService userService;
    @Resource
    private RedisCache redisCache;

    public BusiUserLoginService() {
    }

    public String login(String username, String password, String code, String uuid) {
        return login(username, password, code, uuid, false);
    }

    public String login(String username, String password, String code, String uuid, boolean singleLogin) {
        String verifyKey = "captcha_codes:" + uuid;
        String captcha = (String)this.redisCache.getCacheObject(verifyKey);
        this.redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Error", MessageUtils.message("user.jcaptcha.expire", new Object[0]), new Object[0]));
            throw new CaptchaExpireException();
        } else if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Error", MessageUtils.message("user.jcaptcha.error", new Object[0]), new Object[0]));
            throw new CaptchaException();
        } else {
            int loginErrorCount = userService.isLoginLocked(username);
            if (loginErrorCount > 0) {
                throw new CustomException("登录失败超过次数限制，请等待" + loginErrorCount + "分钟后重试或者联系管理员。");
            }
            Authentication authentication = null;

            try {
                authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            } catch (Exception var9) {
                userService.updateLoginError(username, false);
                if (var9 instanceof BadCredentialsException) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Error", MessageUtils.message("user.password.not.match", new Object[0]), new Object[0]));
                    throw new UserPasswordNotMatchException();
                }

                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Error", var9.getMessage(), new Object[0]));
                throw new CustomException(var9.getMessage());
            }

            userService.updateLoginError(username, true);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Success", MessageUtils.message("user.login.success", new Object[0]), new Object[0]));
            LoginUser loginUser = (LoginUser)authentication.getPrincipal();
            String token = tokenService.createToken(loginUser);
            if (singleLogin) {
                try {
                    String loginUserIdSingleTokenKey = Constants.LOGIN_USER_ID_SINGLE_TOKEN_ + loginUser.getUser().getUserId();
                    redisCache.setCacheObject(loginUserIdSingleTokenKey, token);
                } catch (Exception e) {
                }
            }
            return token;
        }
    }

    public String loginNoCode(String username, String password) {
        int loginErrorCount = userService.isLoginLocked(username);
        if (loginErrorCount > 0) {
            throw new CustomException("登录失败超过次数限制，请等待" + loginErrorCount + "分钟后重试或者联系管理员。");
        }
        Authentication authentication = null;

        try {
            authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception var9) {
            userService.updateLoginError(username, false);
            if (var9 instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Error", MessageUtils.message("user.password.not.match", new Object[0]), new Object[0]));
                throw new UserPasswordNotMatchException();
            }

            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Error", var9.getMessage(), new Object[0]));
            throw new CustomException(var9.getMessage());
        }

        userService.updateLoginError(username, true);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, "Success", MessageUtils.message("user.login.success", new Object[0]), new Object[0]));
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        return this.tokenService.createToken(loginUser);
    }

}

