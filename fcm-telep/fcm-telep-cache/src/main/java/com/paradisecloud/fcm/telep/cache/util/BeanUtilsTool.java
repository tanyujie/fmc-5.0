package com.paradisecloud.fcm.telep.cache.util;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author nj
 * @date 2022/10/13 14:56
 */
public class BeanUtilsTool {
    public static <T> Map<String, String> beanToMap(T bean)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return BeanUtils.describe(bean);
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> bean)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t = bean.newInstance();
        BeanUtils.populate(t, map);
        return t;
    }

}
