/*
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : TerminalCache.java
 * Package     : com.paradisecloud.fcm.terminal
 * @author lilinhai
 * @since 2021-01-22 18:06
 * @version  V1.0
 */
package com.paradisecloud.fcm.mcu.kdc.monitor.cache;

import com.paradisecloud.fcm.mcu.kdc.monitor.apc.AppointmentConferenceThread;
import com.paradisecloud.fcm.mcu.kdc.monitor.cc.CcGetChangesThread;
import com.paradisecloud.fcm.mcu.kdc.monitor.cm.CmGetChangesThread;
import com.paradisecloud.fcm.mcu.kdc.monitor.ct.ConferenceMonitorThread;

/**
 * <pre>监控线程缓存</pre>
 *
 * @author lilinhai
 * @version V1.0
 * @since 2021-01-22 18:06
 */
public class MonitorThreadCache {

    private static final MonitorThreadCache INSTANCE = new MonitorThreadCache();

    private volatile long cmGetChangesTime = 0;
    private CmGetChangesThread cmGetChangesThread = null;

    private volatile long ccGetChangesTime = 0;
    private CcGetChangesThread ccGetChangesThread = null;

    private volatile long appointmentConferenceTime = 0;
    private AppointmentConferenceThread appointmentConferenceThread = null;

    private volatile long conferenceMonitorTime = 0;
    private ConferenceMonitorThread conferenceMonitorThread = null;

    /**
     * <pre>构造方法</pre>
     *
     * @author lilinhai
     * @since 2021-01-22 18:07
     */
    private MonitorThreadCache() {
    }

    public static MonitorThreadCache getInstance() {
        return INSTANCE;
    }

    public long getCmGetChangesTime() {
        return cmGetChangesTime;
    }

    public void setCmGetChangesTime(long cmGetChangesTime) {
        this.cmGetChangesTime = cmGetChangesTime;
    }

    public CmGetChangesThread getCmGetChangesThread() {
        return cmGetChangesThread;
    }

    public void setCmGetChangesThread(CmGetChangesThread cmGetChangesThread) {
        this.cmGetChangesThread = cmGetChangesThread;
    }

    public long getCcGetChangesTime() {
        return ccGetChangesTime;
    }

    public void setCcGetChangesTime(long ccGetChangesTime) {
        this.ccGetChangesTime = ccGetChangesTime;
    }

    public CcGetChangesThread getCcGetChangesThread() {
        return ccGetChangesThread;
    }

    public void setCcGetChangesThread(CcGetChangesThread ccGetChangesThread) {
        this.ccGetChangesThread = ccGetChangesThread;
    }

    public long getAppointmentConferenceTime() {
        return appointmentConferenceTime;
    }

    public void setAppointmentConferenceTime(long appointmentConferenceTime) {
        this.appointmentConferenceTime = appointmentConferenceTime;
    }

    public AppointmentConferenceThread getAppointmentConferenceThread() {
        return appointmentConferenceThread;
    }

    public void setAppointmentConferenceThread(AppointmentConferenceThread appointmentConferenceThread) {
        this.appointmentConferenceThread = appointmentConferenceThread;
    }

    public long getConferenceMonitorTime() {
        return conferenceMonitorTime;
    }

    public void setConferenceMonitorTime(long conferenceMonitorTime) {
        this.conferenceMonitorTime = conferenceMonitorTime;
    }

    public ConferenceMonitorThread getConferenceMonitorThread() {
        return conferenceMonitorThread;
    }

    public void setConferenceMonitorThread(ConferenceMonitorThread conferenceMonitorThread) {
        this.conferenceMonitorThread = conferenceMonitorThread;
    }
}
