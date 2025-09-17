package com.paradisecloud.fcm.service.minutes;

import org.java_websocket.handshake.ServerHandshake;

public interface IWebsocketMessageProcessor {

    void processMessage(String message);
    void onOpen(ServerHandshake serverHandshake);
    void onClose(int code, String reason, boolean remote);
    void onError(Exception ex);
}
