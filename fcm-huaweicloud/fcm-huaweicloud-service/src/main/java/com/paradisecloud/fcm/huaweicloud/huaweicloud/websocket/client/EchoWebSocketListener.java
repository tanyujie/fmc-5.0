package com.paradisecloud.fcm.huaweicloud.huaweicloud.websocket.client;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author nj
 * @date 2023/2/16 11:47
 */
public final class EchoWebSocketListener extends WebSocketListener {


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
    }
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    }
}
