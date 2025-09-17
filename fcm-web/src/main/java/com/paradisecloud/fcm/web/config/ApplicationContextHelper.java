package com.paradisecloud.fcm.web.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author nj
 * @date 2022/6/16 13:50
 */
@Component("applicationContextHelper")
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        applicationContext = context;
    }

    /**
     * 获取bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T popBean(Class<T> clazz) {
        //先判断是否为空
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(clazz);
    }


    public static <T> T popBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }

        return applicationContext.getBean(name, clazz);

    }

    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new RuntimeException("applicaitonContext属性未注入");
        }
    }


}
