package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author nj
 * @date 2022/8/2 11:43
 */
//@Aspect
//@Component
public class LiveTerminalAspect {



    public LiveTerminalAspect() {
    }

    @Pointcut("execution(* com.paradisecloud.fcm.web.controller.mqtt.BusiTerminalActionController.getLiveTerminal(..))")
    private void myPointCut() {


    }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) throws Exception {

        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        LicenseContent licenseContent = licenseManager.verify();

        Map extra = (Map<String,Object>)licenseContent.getExtra();
        boolean recorder = (boolean) extra.get("streamer");
        if(!recorder){
            throw new CustomException("已禁止使用直播功能,请联系供应商授权后使用");

        }


    }
}
