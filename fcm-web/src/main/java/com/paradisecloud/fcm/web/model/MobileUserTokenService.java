package com.paradisecloud.fcm.web.model;

import com.paradisecloud.common.constant.Constants;
import com.paradisecloud.common.core.redis.RedisCache;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.common.exception.user.UserPasswordNotMatchException;
import com.paradisecloud.common.utils.MessageUtils;
import com.paradisecloud.common.utils.ServletUtils;
import com.paradisecloud.common.utils.ip.AddressUtils;
import com.paradisecloud.common.utils.ip.IpUtils;
import com.paradisecloud.common.utils.uuid.IdUtils;
import com.paradisecloud.framework.manager.AsyncManager;
import com.paradisecloud.framework.manager.factory.AsyncFactory;
import com.paradisecloud.framework.web.service.TokenService;
import com.paradisecloud.system.model.LoginUser;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/6/29 15:00
 */
@Component
public class MobileUserTokenService {


    @Value("${token.secret}")
    private String secret;

    @Resource
    private TokenService tokenService;


    @Resource
    private RedisCache redisCache;

    @Resource
    private AuthenticationManager authenticationManager;


    public void deleteToken(LoginUser loginUser) {

        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);

            if (Objects.equals(user.getOs(), "mobile") && Objects.equals(user.getUsername(), loginUser.getUsername()) && !Objects.equals(loginUser.getToken(), user.getToken())) {
                redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + user.getToken());
                break;
            }
        }

    }


    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        tokenService.refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>(1);
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs("mobile");
    }


    public String createToken(Map<String, Object> claims) {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    public LoginUser getLoginUser(String username, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return loginUser;
    }


}
