package com.paradisecloud.fcm.license;

import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.fcm.common.utils.DateUtil;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author nj
 * @date 2023/4/21 11:26
 */
@Aspect
@Component
@Conditional(CheckEnvironmentCondition.class)
public class MonitorAspect {

    public MonitorAspect() {
    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.mcu.all.BusiMcuSmcCospaceController.getInfo(..))")
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
        Map extra = (Map<String, Object>) licenseContent.getExtra();
        boolean monitor = (boolean) extra.get("monitor");
        if(!monitor){
            throw new CustomException("会议监控无法使用 ,请联系供应商授权后使用");
        }else {
            String monitorLimitTime = (String) extra.get("smcMonitorLimitTime");
            if(Strings.isNotBlank(monitorLimitTime)){
                long willTime = DateUtil.convertDateByString(monitorLimitTime, null).getTime();
                if (System.currentTimeMillis() - willTime >= 0) {
                    throw new CustomException("会议监控LICENSE到期 ,请联系供应商授权后使用");
                }
            }
        }
    }
}
