package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author nj
 * @date 2023/3/1 11:36
 */
@Slf4j
public class MonitorUtil {

    private static Map<String, HwcloudMeetingWebsocketClient> monitorWebSocketMap=new ConcurrentHashMap<>();
    private ScheduledFuture<?> monitorAliveTask = null;

    private  int monitorInterval;
    private String monitorName;

    public MonitorUtil(String monitorName) {
        this.monitorName = monitorName;
    }

    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("MonitorUtil-schedule-pool-%d").build());

    public void start(int monitorInterval, HwcloudMeetingWebsocketClient hwcloudMeetingWebsocketClient) {
        monitorInterval = monitorInterval;

        monitorWebSocketMap.put(monitorName, hwcloudMeetingWebsocketClient);

        log.info("监控数据：{}", monitorWebSocketMap);
        // 执行任务 立即执行，间隔时间由配置monitor.interval决定
        monitorAliveTask = scheduledExecutorService.scheduleWithFixedDelay(monitorAliveRunnable(), 0, monitorInterval, TimeUnit.SECONDS);
    }


    /**
     * 定时任务-发送websocket心跳
     */
    private Runnable monitorAliveRunnable(){
        return ()->{
            for (Map.Entry<String, HwcloudMeetingWebsocketClient> entry : monitorWebSocketMap.entrySet()) {
                try {
                    entry.getValue().send("/n");
                } catch (Exception e) {
                    entry.getValue().onClose(0, null, true);
                }
            }
        };
    }

}
