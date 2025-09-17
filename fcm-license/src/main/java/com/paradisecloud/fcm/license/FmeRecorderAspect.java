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
 * @date 2022/8/2 10:49
 */
@Aspect
@Component
public class FmeRecorderAspect {

    public FmeRecorderAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.recording.RecordingController.changeRecordingStatus(..))")
    private void myPointCut() {


    }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) throws Exception {

        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        LicenseContent licenseContent = licenseManager.verify();

        Map extra = (Map<String,Object>)licenseContent.getExtra();
        boolean recorder = (boolean) extra.get("recorder");
        if(!recorder){
            throw new CustomException("已禁止使用录制功能,请联系供应商授权后使用");

        }


    }
}
