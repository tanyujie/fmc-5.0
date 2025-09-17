package com.paradisecloud.fcm.service.conference.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author nj
 * @date 2023/6/12 15:17
 */
@Component
public class McuServiceFactory  implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        McuServiceFactory.applicationContext = applicationContext;
    }

    public static Object getBean(Class clazz) {
        return applicationContext.getBean(clazz);
    }
}
