package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import com.huaweicloud.sdk.meeting.v1.model.TokenInfo;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.cache.HwcloudMeetingBridge;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudBridgeStatus;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.CloseFrame;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.util.Objects;

/**
 * @author nj
 * @date 2022/10/8 14:40
 */
public class HwcloudWebsocketClientObj {


    /**
     * 创建链接
     *
     * @param hwcloudMeetingBridge
     * @return
     */
    public static HwcloudMeetingWebsocketClient createWebsocketClientObj(HwcloudMeetingBridge hwcloudMeetingBridge) {

        HwcloudMeetingWebsocketClient mwsc = null;
        try {
            try {
                if (!hwcloudMeetingBridge.getWebsocketAvailable()) {

                    TokenInfo tokenInfo = hwcloudMeetingBridge.getTokenInfo();
                    String wsURL = tokenInfo.getWsURL();

                    String websocketToken = hwcloudMeetingBridge.getMeetingControl().createConfToken(hwcloudMeetingBridge.getConfID(),tokenInfo.getToken());
                    String tmpToken = websocketToken;
                    String url = wsURL + "/cms/open/websocket/confctl/increment/conn?" + "confID=" + hwcloudMeetingBridge.getConfID() + "&tmpToken=" + tmpToken;
                    if (!ObjectUtils.isEmpty(tmpToken)) {
                        try {
                            mwsc = new HwcloudMeetingWebsocketClient(new URI(url), hwcloudMeetingBridge);
                            connectBlocking(mwsc, hwcloudMeetingBridge);
                        } catch (Exception e) {

                        }

                    } else {
                        hwcloudMeetingBridge.setBridgeStatus(HwcloudBridgeStatus.NOT_AVAILABLE);
                    }

                }
            } catch (Throwable e) {

            }
        } catch (Exception e) {

        }

        return mwsc;
    }

    public static void connectBlocking(HwcloudMeetingWebsocketClient mwsc, HwcloudMeetingBridge hwcloudMeetingBridge) {
        // 连接失败，会在onclose执行重试，因此不必单独处理try-catch
        if (mwsc != null) {
            try {
                if (Objects.equals(mwsc.getReadyState(), ReadyState.NOT_YET_CONNECTED)) {

                    if (mwsc.connectBlocking()) {
                        hwcloudMeetingBridge.setWebsocketAvailable(true);
                        addtoken(mwsc, hwcloudMeetingBridge);
                    } else {
                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false");
                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，connectBlocking=false", false);
                    }
                } else if (Objects.equals(mwsc.getReadyState(), ReadyState.CLOSED) || Objects.equals(mwsc.getReadyState(), ReadyState.CLOSING)) {
                    if (mwsc.reconnectBlocking()) {
                        hwcloudMeetingBridge.setWebsocketAvailable(true);
                        addtoken(mwsc, hwcloudMeetingBridge);
                    } else {
                        mwsc.closeConnection(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false");
                        mwsc.onClose(CloseFrame.ABNORMAL_CLOSE, "连接失败，reconnectBlocking=false", false);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void addtoken(HwcloudMeetingWebsocketClient mwsc, HwcloudMeetingBridge hwcloudMeetingBridge) {
        hwcloudMeetingBridge.addWebsocketConnectionCount(mwsc.getToken());
        hwcloudMeetingBridge.setBridgeStatus(HwcloudBridgeStatus.AVAILABLE, mwsc.getToken());
        HwcloudMeetingWebsocketContext.getInstance().put(hwcloudMeetingBridge.getConfID(), mwsc);
    }


}
