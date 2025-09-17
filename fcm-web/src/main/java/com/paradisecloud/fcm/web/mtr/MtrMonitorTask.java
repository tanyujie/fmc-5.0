package com.paradisecloud.fcm.web.mtr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * MTR探测定时任务类
 */
@Component
public class MtrMonitorTask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每30秒启动检查MTR探测线程状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void checkCmGetChangesThread() {
        logger.info("检查MTR探测线程状态定时任务启动");

        MtrMonitorThreadCache monitorThreadCache = MtrMonitorThreadCache.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        // MTR
        {
            MtrThread mtrThread = monitorThreadCache.getMtrThread();
            if (mtrThread != null) {
                long mtrChangesTime = monitorThreadCache.getMtrChangesTime();
                if (currentTimeMillis - mtrChangesTime > 60000) {
                    logger.info("MTR探测线程长时间停滞，中断后重启");
                    mtrThread.interrupt();
                    mtrThread = new MtrThread();
                    mtrThread.start();
                    monitorThreadCache.setMtrThread(mtrThread);
                }
            } else {
                logger.info("MTR探测线程开始");
                mtrThread = new MtrThread();
                mtrThread.start();
                monitorThreadCache.setMtrThread(mtrThread);
            }
        }
        // iperf3
        {
            Iperf3ServerStatusThread iperf3ServerStatusThread = monitorThreadCache.getIperf3ServerStatusThread();
            if (iperf3ServerStatusThread != null) {
                long iperf3ServerChangesTime = monitorThreadCache.getIperf3ServerChangesTime();
                if (currentTimeMillis - iperf3ServerChangesTime > 60000) {
                    logger.info("iperf3服务器线程长时间停滞，中断后重启");
                    iperf3ServerStatusThread.interrupt();
                    iperf3ServerStatusThread = new Iperf3ServerStatusThread();
                    iperf3ServerStatusThread.start();
                    monitorThreadCache.setIperf3ServerStatusThread(iperf3ServerStatusThread);
                }
            } else {
                logger.info("iperf3服务器线程开始");
                iperf3ServerStatusThread = new Iperf3ServerStatusThread();
                iperf3ServerStatusThread.start();
                monitorThreadCache.setIperf3ServerStatusThread(iperf3ServerStatusThread);
            }
        }
    }

}
