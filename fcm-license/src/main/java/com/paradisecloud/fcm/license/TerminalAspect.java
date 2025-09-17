package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.web.service.interfaces.IBusiTerminalWebService;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 11:43
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class TerminalAspect {

    public TerminalAspect() {
    }

    @Pointcut("execution(* com.paradisecloud.fcm.web.controller.terminal.BusiTerminalController.add(..))")
    private void myPointCut() {
   }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) throws Exception {

        LicenseContent licenseContent = null;
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
            licenseContent = licenseManager.verify();
        } catch (Exception e) {
            throw new CustomException("当前服务器没在授权范围内,请联系供应商授权后使用");
        }

        Map extra = (Map<String,Object>)licenseContent.getExtra();
        boolean schedule = (boolean) extra.get("schedule");
        int termianlAmount = (int)extra.get("termianlAmount");
        if (schedule) {
            String participantLimitTime = (String) extra.get("participantLimitTime");
            long willTime = DateUtil.convertDateByString(participantLimitTime, null).getTime();
            if (System.currentTimeMillis() - willTime >= 0) {
                throw new CustomException("LICENSE到期 ,请联系供应商授权后使用");
            }
        }

        if(TerminalCache.getInstance().size() >= termianlAmount){
            throw new CustomException("可管理终端数量超过限制 ,请联系供应商授权后使用");
        }

    }
}
