package com.paradisecloud.smc3.utils;

import com.paradisecloud.common.constant.HttpStatus;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.system.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author nj
 * @date 2022/6/15 17:39
 */
public class AuthenticationUtil {

    public static Long getDeptId() {
        return getLoginUser().getUser().getDeptId();
    }

    public static Long getUserId() {
        try {
            return getLoginUser().getUser().getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static LoginUser getLoginUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return (LoginUser) authentication.getPrincipal();
        } catch (Exception e) {
            throw new CustomException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

}
