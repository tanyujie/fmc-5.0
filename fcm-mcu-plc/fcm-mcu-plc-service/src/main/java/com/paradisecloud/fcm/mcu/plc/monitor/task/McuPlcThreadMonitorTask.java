package com.paradisecloud.fcm.mcu.plc.monitor.task;

import com.paradisecloud.fcm.mcu.plc.monitor.apc.AppointmentConferenceThread;
import com.paradisecloud.fcm.mcu.plc.monitor.cache.MonitorThreadCache;
import com.paradisecloud.fcm.mcu.plc.monitor.cc.CcGetChangesThread;
import com.paradisecloud.fcm.mcu.plc.monitor.cm.CmGetChangesThread;
import com.paradisecloud.fcm.mcu.plc.monitor.ct.ConferenceMonitorThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * CDR定时任务类
 */
@Component
public class McuPlcThreadMonitorTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每30秒启动检查会管长轮询线程状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void checkCmGetChangesThread() {
        logger.info("检查会管长轮询线程状态定时任务启动");

        MonitorThreadCache monitorThreadCache = MonitorThreadCache.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        // 会管
        {
            CmGetChangesThread cmGetChangesThread = monitorThreadCache.getCmGetChangesThread();
            if (cmGetChangesThread != null) {
                long cmGetChangesTime = monitorThreadCache.getCmGetChangesTime();
                if (currentTimeMillis - cmGetChangesTime > 60000) {
                    logger.info("会管长轮询线程长时间停滞，中断后重启");
                    cmGetChangesThread.interrupt();
                    cmGetChangesThread = new CmGetChangesThread();
                    cmGetChangesThread.start();
                    monitorThreadCache.setCmGetChangesThread(cmGetChangesThread);
                }
            } else {
                logger.info("会管长轮询线程开始");
                cmGetChangesThread = new CmGetChangesThread();
                cmGetChangesThread.start();
                monitorThreadCache.setCmGetChangesThread(cmGetChangesThread);
            }
        }
    }

    /**
     * 每30秒启动检查会控长轮询线程状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void checkCcGetChangesThread() {
        logger.info("检查会控长轮询线程状态定时任务启动");

        MonitorThreadCache monitorThreadCache = MonitorThreadCache.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        // 会控
        {
            CcGetChangesThread ccGetChangesThread = monitorThreadCache.getCcGetChangesThread();
            if (ccGetChangesThread != null) {
                long ccGetChangesTime = monitorThreadCache.getCcGetChangesTime();
                if (currentTimeMillis - ccGetChangesTime > 60000) {
                    logger.info("会管长轮询线程长时间停滞，中断后重启");
                    ccGetChangesThread.interrupt();
                    ccGetChangesThread = new CcGetChangesThread();
                    ccGetChangesThread.start();
                    monitorThreadCache.setCcGetChangesThread(ccGetChangesThread);
                }
            } else {
                logger.info("会控长轮询线程开始");
                ccGetChangesThread = new CcGetChangesThread();
                ccGetChangesThread.start();
                monitorThreadCache.setCcGetChangesThread(ccGetChangesThread);
            }
        }
    }

    /**
     * 每30秒启动检查预约会议线程状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void checkAppointmentConferenceThread() {
        logger.info("检查预约会议线程状态定时任务启动");

        MonitorThreadCache monitorThreadCache = MonitorThreadCache.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        // 会控
        {
            AppointmentConferenceThread appointmentConferenceThread = monitorThreadCache.getAppointmentConferenceThread();
            if (appointmentConferenceThread != null) {
                long appointmentConferenceTime = monitorThreadCache.getAppointmentConferenceTime();
                if (currentTimeMillis - appointmentConferenceTime > 60000) {
                    logger.info("预约会议线程长时间停滞，中断后重启");
                    appointmentConferenceThread.interrupt();
                    appointmentConferenceThread = new AppointmentConferenceThread();
                    appointmentConferenceThread.start();
                    monitorThreadCache.setAppointmentConferenceThread(appointmentConferenceThread);
                }
            } else {
                logger.info("预约会议线程开始");
                appointmentConferenceThread = new AppointmentConferenceThread();
                appointmentConferenceThread.start();
                monitorThreadCache.setAppointmentConferenceThread(appointmentConferenceThread);
            }
        }
    }

    /**
     * 每1分钟启动检查会议线程状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkConferenceThread() {
        logger.info("检查会议线程状态定时任务启动");

        MonitorThreadCache monitorThreadCache = MonitorThreadCache.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        // 会控
        {
            ConferenceMonitorThread conferenceMonitorThread = monitorThreadCache.getConferenceMonitorThread();
            if (conferenceMonitorThread != null) {
                long conferenceMonitorTime = monitorThreadCache.getConferenceMonitorTime();
                if (currentTimeMillis - conferenceMonitorTime > 360000) {
                    logger.info("会议线程长时间停滞，中断后重启");
                    conferenceMonitorThread.interrupt();
                    conferenceMonitorThread = new ConferenceMonitorThread();
                    conferenceMonitorThread.start();
                    monitorThreadCache.setConferenceMonitorThread(conferenceMonitorThread);
                }
            } else {
                logger.info("会议线程开始");
                conferenceMonitorThread = new ConferenceMonitorThread();
                conferenceMonitorThread.start();
                monitorThreadCache.setConferenceMonitorThread(conferenceMonitorThread);
            }
        }
    }
}
