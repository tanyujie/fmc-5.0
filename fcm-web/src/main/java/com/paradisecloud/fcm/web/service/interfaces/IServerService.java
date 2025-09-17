package com.paradisecloud.fcm.web.service.interfaces;

public interface IServerService {

    /**
     * 获取系统时间
     *
     * @param
     * @return
     */
    String getServerTime();

    /**
     * 设置系统时间
     *
     * @param date
     * @param time
     */
    boolean setServerTime(String date, String time);
}
