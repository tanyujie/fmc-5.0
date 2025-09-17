package com.paradisecloud.fcm.license;

import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2022/8/1 15:01
 */
//@Component
//@Order
public class LicenseTask implements ApplicationRunner, ApplicationContextAware {


    public static final String PROD = "prod";
    @Resource
    private LicenseCheckListener licenseCheckListener;

    private static ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args)   {

        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if(Objects.equals(PROD,activeProfiles[0])){
            final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(()-> {
                try {
                    List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
                    fmeBridges.forEach(fmeBridge ->{
                        if(fmeBridge.isAvailable()){
                            licenseCheckListener.install();
                            try {
                                LicenseExecutor.limitScheduledExecutorService(LicenseManagerHolder.getInstance(null).verify(),0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                }
            },6,60, TimeUnit.SECONDS);
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LicenseTask.applicationContext=applicationContext;
    }
}
