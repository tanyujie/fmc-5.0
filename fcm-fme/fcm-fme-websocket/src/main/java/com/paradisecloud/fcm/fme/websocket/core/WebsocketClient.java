/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : McuNodeWebSocketClient.java
 * Package : com.paradisecloud.sync.core
 * 
 * @author lilinhai
 * 
 * @since 2020-12-01 14:17
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.websocket.core;

import java.net.URI;

import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessage;
import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessageQueue;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils.BridgeDiagnosisResult;
import com.paradisecloud.fcm.fme.websocket.async.FmeWebSocketClientMonitor;
import com.paradisecloud.fcm.fme.websocket.async.WebsocketOnOpenProcessor;
import com.paradisecloud.fcm.fme.websocket.async.WebsocketReconnecter;
import com.paradisecloud.fcm.fme.websocket.interfaces.IFmeCacheService;
import com.paradisecloud.fcm.fme.websocket.interfaces.IWebSocketService;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.paradisecloud.fcm.fme.websocket.model.WebSocketBusiProcessor;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.CauseUtils;

/**
 * <pre>FME-Websocket客户端</pre>
 * 
 * @author lilinhai
 * @since 2020-12-01 14:17
 * @version V1.0
 */
public class WebsocketClient extends SSLWebSocketClient
{
    private IWebSocketService mcuNodeWebSocketService;
    private WebSocketBusiProcessor webSocketProcessor;
    private IFmeCacheService fmeCacheService;
    private volatile boolean isExecCloseMethod;
    private String authToken;
    
    /**
     * 本连接诊断报告
     */
    private BridgeDiagnosisResult result;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-01 14:18
     * @param serverURI
     * @param fmeBridge
     * @param authToken 
     */
    public WebsocketClient(URI serverURI, FmeBridge fmeBridge, String authToken)
    {
        super(serverURI, new Draft_6455());
        this.authToken = authToken;
        fmeBridge.setBridgeStatus(FmeBridgeStatus.INITIALIZING, authToken);
        this.mcuNodeWebSocketService = BeanFactory.getBean(IWebSocketService.class);
        this.fmeCacheService = BeanFactory.getBean(IFmeCacheService.class);
        this.webSocketProcessor = new WebSocketBusiProcessor(this, fmeBridge, this.mcuNodeWebSocketService);
    }
    
    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        boolean isWebsocketConnectionOpen = isOpen() 
                && !getSocket().isClosed() 
                && !getSocket().isInputShutdown() 
                && !getSocket().isOutputShutdown();
        if (isWebsocketConnectionOpen)
        {
            this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("Websocket connection succeeded ：" + ", HttpStatus: " + handshakedata.getHttpStatus() + ", HttpStatusMessage: " + handshakedata.getHttpStatusMessage(), true);
            FmeWebSocketClientMonitor.getInstance().add(this);
            WebsocketOnOpenProcessor.getInstance().add(this);
        }
        else
        {
            this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("websocket onOpen execution failed: connection failed: " + ", HttpStatus: " + handshakedata.getHttpStatus() + ", HttpStatusMessage: " + handshakedata.getHttpStatusMessage(), true);
            StringBuilder websocketConnectionStatus = new StringBuilder();
            websocketConnectionStatus.append("isOpen:").append(isOpen()).append('\n');
            websocketConnectionStatus.append("isClosed:").append(getSocket().isClosed()).append('\n');
            websocketConnectionStatus.append("isInputShutdown:").append(getSocket().isInputShutdown()).append('\n');
            websocketConnectionStatus.append("isOutputShutdown:").append(getSocket().isOutputShutdown()).append('\n');
            closeConnection(CloseFrame.ABNORMAL_CLOSE, websocketConnectionStatus.toString() + "-3");
            onClose(CloseFrame.ABNORMAL_CLOSE, websocketConnectionStatus.toString() + "-3", false);
        }
    }
    
    @Override
    public void onMessage(String message)
    {
        try
        {
            webSocketProcessor.processMessage(message);
        }
        catch (Throwable e)
        {
            this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("onMessage Error ", true, e);
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        if (isExecCloseMethod)
        {
            this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("The websocket link has been closed and does not need to be executed repeatedly: "
                    + code + ", reason: " + reason + ", remote: " + remote, true);
            return;
        }
        
        if (!webSocketProcessor.getFmeBridge().isDeleted())
        {
            if (result == null)
            {
                result = BridgeUtils.diagnosis(webSocketProcessor.getFmeBridge().getBusiFme());
            }
            
            this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("Websocket health: " + result.toString(), true);
            
            webSocketProcessor.getFmeBridge().setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, this.authToken);
            webSocketProcessor.getFmeBridge().setConnectionFailedReason("连接失败: " + reason);
            BusiFmeDBSynchronizer.getInstance().add(webSocketProcessor.getFmeBridge().getBusiFme());
            FmeBridgeMessageQueue.getInstance().put(new FmeBridgeMessage(webSocketProcessor.getFmeBridge()));
            WebsocketReconnecter.getInstance().add(webSocketProcessor.getFmeBridge());
            
            // 销毁该key对应的连接
            this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("Connection is closed, code: " + code + ", reason: " + reason + ", remote: " + remote, true);
        }
        else
        {
            webSocketProcessor.getFmeBridge().setBridgeStatus(FmeBridgeStatus.NOT_AVAILABLE, this.authToken);
            fmeCacheService.clearFmeDataCache(webSocketProcessor.getFmeBridge());
            BusiFmeDBSynchronizer.getInstance().add(webSocketProcessor.getFmeBridge().getBusiFme());
        }
        isExecCloseMethod = true;
    }
    
    @Override
    public void onError(Exception ex)
    {
        this.webSocketProcessor.getFmeBridge().getFmeLogger().logWebsocketInfo("onError-" + CauseUtils.getRootCause(ex), true, ex);
        closeConnection(CloseFrame.ABNORMAL_CLOSE, CauseUtils.getRootCause(ex));
        onClose(CloseFrame.ABNORMAL_CLOSE, CauseUtils.getRootCause(ex) , false);
    }
    
    /**
     * <p>Get Method   :   webSocketProcessor McuNodeWebSocketProcessor</p>
     * @return webSocketProcessor
     */
    public WebSocketBusiProcessor getWebSocketProcessor()
    {
        return webSocketProcessor;
    }
    
    /**
     * <p>Set Method   :   result BridgeHostDiagnosisResult</p>
     * @param result
     */
    public void setResult(BridgeDiagnosisResult result)
    {
        this.result = result;
    }

    /**
     * <p>Get Method   :   authToken String</p>
     * @return authToken
     */
    public String getAuthToken()
    {
        return authToken;
    }
}
