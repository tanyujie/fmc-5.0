package com.paradisecloud.fcm.fme.websocket.async;


import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.fcm.fme.cache.FmeBridgeCache;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.websocket.core.WebsocketClient;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.sinhy.utils.CauseUtils;
import com.sinhy.utils.HostUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author nj
 * @date 2022/9/30 17:30
 */
@Slf4j
public class FmeWebSocketClientMonitorReconnecter<T> implements ApplicationRunner {


    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
            new BasicThreadFactory.Builder().namingPattern("SOCKETMonitorReconnecter-schedule-pool-%d").daemon(true).build());


    /**
     * 异常断开的会议桥队列
     */
    protected final Queue<FmeBridge> queue = new ConcurrentLinkedQueue<>();




    @Override
    public void run(ApplicationArguments args) throws Exception {

        executorService.scheduleAtFixedRate(() -> {


            List<FmeBridge> fmeBridges = FmeBridgeCache.getInstance().getFmeBridges();
            for (FmeBridge fmeBridge : fmeBridges) {

                try {
                    if (!fmeBridge.isAvailable()&& !fmeBridge.isDeleted()) {
                        // 执行重连尝试
                        WebsocketClient mwsc = WebsocketClientObj.createWebsocketClientObj(fmeBridge);
                        log.info("执行重连尝试mwsc：" + fmeBridge.getBusiFme().getIp());

                        // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
                        if (mwsc != null) {
                            try {
                                if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                                    if (mwsc.connectBlocking()) {
                                        tokenAdd(fmeBridge, mwsc);
                                    } else {
                                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false", false);
                                        log.error("执行重连尝试失败mwsc connectBlocking=false：" + fmeBridge.getBusiFme().getIp());
                                    }
                                } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                                    if (mwsc.reconnectBlocking()) {
                                        tokenAdd(fmeBridge, mwsc);
                                    } else {
                                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false", false);
                                    }
                                }

                            } catch (Exception e) {
                                fmeBridge.getFmeLogger().logWebsocketInfo("连接失败，异常终止！", true, e);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        fmeBridge.getFmeLogger().logWebsocketInfo("会议桥链接正常！"+fmeBridge.getBridgeAddress()+":"+fmeBridge.getBridgeStatus().getName(), true);

                    }
                } catch (Throwable e) {
                    fmeBridge.setConnectionFailedReason("连接失败：" + CauseUtils.getRootCause(e));
                    fmeBridge.getFmeLogger().logWebsocketInfo("fme websocket connection build error", true, e);
                }

            }


        }, 60, 25, TimeUnit.SECONDS);

    }

    /**
     * <pre>创建websocket连接对象</pre>
     *
     * @param fmeBridge
     * @return McuNodeWebSocketClient
     * @author lilinhai
     * @since 2020-12-01 18:29
     */
    public WebsocketClient createWebsocketClientObj(FmeBridge fmeBridge) {
        WebsocketClient mwsc = null;
        try {
            if (HostUtils.isHostReachable(fmeBridge.getBusiFme().getIp(), 1000)) {
                String authToken = fmeBridge.getWebSocketAuthToken();
                String url = fmeBridge.getWebSocketEventUrl(authToken);
                if (!ObjectUtils.isEmpty(url)) {
                    mwsc = new WebsocketClient(new URI(url), fmeBridge, authToken);
                } else {
                    fmeBridge.setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, null);
                    BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
                    fmeBridge.getFmeLogger().logWebsocketInfo("获取authToken失败", true, true);
                    fmeBridge.setConnectionFailedReason("获取authToken失败!");
                }
            } else {
                fmeBridge.setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, null);
                fmeBridge.setConnectionFailedReason("会控无法拼通HostReachable=false!");
                BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
            }
        } catch (Throwable e) {
            fmeBridge.getFmeLogger().logWebsocketInfo("获取authToken失败", true, e);
            fmeBridge.setConnectionFailedReason("获取authToken失败: " + CauseUtils.getRootCause(e));
        }
        return mwsc;
    }


    private void tokenAdd(FmeBridge fmeBridge, WebsocketClient mwsc) {
        fmeBridge.addWebsocketConnectionCount(mwsc.getAuthToken());
        fmeBridge.setBridgeStatus(FmeBridgeStatus.AVAILABLE, mwsc.getAuthToken());
        fmeBridge.getFmeLogger().logWebsocketInfo("连接创建成功，准备同步FME数据！", true);
    }

    public void add(FmeBridge fmeBridge) {
        // websocket连接数减一
        if (!queue.isEmpty()) {
            FmeBridge peek = this.queue.peek();
            if (!Objects.equals(fmeBridge, peek)) {
               this.queue.add(fmeBridge);
            }
        } else {
            this.queue.add(fmeBridge);
        }
    }


}
