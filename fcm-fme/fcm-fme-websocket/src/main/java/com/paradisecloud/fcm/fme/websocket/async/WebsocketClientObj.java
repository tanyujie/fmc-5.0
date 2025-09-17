package com.paradisecloud.fcm.fme.websocket.async;

import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.websocket.core.WebsocketClient;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.sinhy.utils.CauseUtils;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/10/8 14:40
 */
public class WebsocketClientObj {


    /**
     * 创建链接
     *
     * @param fmeBridge
     * @return
     */
    public static WebsocketClient createWebsocketClientObj(FmeBridge fmeBridge) {

        WebsocketClient mwsc = null;
        try {
            try {
                if (!fmeBridge.isAvailable()) {
                    String authToken = fmeBridge.getWebSocketAuthToken();
                    String url = fmeBridge.getWebSocketEventUrl(authToken);
                    if (!ObjectUtils.isEmpty(url)) {
                        mwsc = new WebsocketClient(new URI(url), fmeBridge, authToken);
                        connectBlocking(mwsc, fmeBridge);
                    } else {
                        fmeBridge.setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, null);
                        BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
                        fmeBridge.getFmeLogger().logWebsocketInfo("获取authToken失败" , true, true);
                        fmeBridge.setConnectionFailedReason("获取authToken失败!");
                    }
                }
            } catch (Throwable e) {
                fmeBridge.getFmeLogger().logWebsocketInfo("获取authToken失败" , true, e);
                fmeBridge.setConnectionFailedReason("获取authToken失败: " + CauseUtils.getRootCause(e));
                BusiFmeDBSynchronizer.getInstance().add(fmeBridge.getBusiFme());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mwsc;
    }

    public static void connectBlocking(WebsocketClient mwsc, FmeBridge fmeBridge) {
        // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
        if (mwsc != null) {
            try {
                if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                    if (mwsc.connectBlocking()) {
                        tokenAdd(fmeBridge, mwsc);
                    } else {
                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false" , false);
                    }
                } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                    if (mwsc.reconnectBlocking()) {
                        tokenAdd(fmeBridge, mwsc);
                    } else {
                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false" , false);
                    }
                }

            } catch (InterruptedException e) {
                fmeBridge.getFmeLogger().logWebsocketInfo("连接失败，异常终止！" , true, e);
                e.printStackTrace();
            }
        }
    }

    private static void tokenAdd(FmeBridge fmeBridge, WebsocketClient mwsc) {
        fmeBridge.addWebsocketConnectionCount(mwsc.getAuthToken());
        fmeBridge.setBridgeStatus(FmeBridgeStatus.AVAILABLE, mwsc.getAuthToken());
        fmeBridge.getFmeLogger().logWebsocketInfo("连接创建成功，准备同步FME数据！" , true);
    }

}
