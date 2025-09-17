package com.paradisecloud.fcm.license;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2022/8/1 15:26
 */
//@Aspect
//@Component
public class FmeUpdateAspect {

    @Resource
    private LicenseCheckListener licenseCheckListener;

    public FmeUpdateAspect() {

    }

    @Pointcut("execution (* com.paradisecloud.fcm.web.controller.fme.BusiFmeController.saveFme(..))")
    private void myPointCut() {

    }

    @After("myPointCut()")
    public void after(JoinPoint joinPoint) {

        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(() -> {
            try {
                LicenseExecutor.limitScheduledExecutorService(LicenseManagerHolder.getInstance(null).verify(),licenseCheckListener.getDefaultLimit());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 2, TimeUnit.SECONDS);

    }

}
