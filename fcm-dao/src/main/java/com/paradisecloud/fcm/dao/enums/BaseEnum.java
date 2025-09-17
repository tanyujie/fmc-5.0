package com.paradisecloud.fcm.dao.enums;

/**
 * @Description 定义mybatis枚举通用接口方法
 * @Author johnson liu
 * @Date 2021/6/6 13:42
 **/
public interface BaseEnum<E extends Enum<?>, T> {
    /**
     * 获取枚举code值
     * @return
     */
    public T getEnumCode();

    /**
     * 获取枚举显示名称
     * @return
     */
    public String getDisplayName();
}
