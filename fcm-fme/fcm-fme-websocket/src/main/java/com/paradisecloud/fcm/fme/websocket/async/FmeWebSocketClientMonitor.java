/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : WebsocketAbnormalMonitor.java
 * Package     : com.paradisecloud.fcm.sync.thread
 * @author lilinhai 
 * @since 2020-12-17 18:17
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.websocket.async;

import java.util.Iterator;

import org.java_websocket.framing.CloseFrame;

import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.dao.model.BusiFme;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils;
import com.paradisecloud.fcm.fme.cache.utils.BridgeUtils.BridgeDiagnosisResult;
import com.paradisecloud.fcm.fme.model.response.system.SystemStatusResponse;
import com.paradisecloud.fcm.fme.websocket.core.WebsocketClient;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;

/**  
 * <pre>websocket异常情况监控</pre>
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version V1.0  
 */
public class FmeWebSocketClientMonitor extends AsyncMessageProcessor<WebsocketClient>
{
    
    /**
     * 单例线程对象
     */
    private static final FmeWebSocketClientMonitor INSTANCE = new FmeWebSocketClientMonitor();
    
    /**
     * <pre>构造方法</pre>
     * @author lilinhai 
     * @since 2020-12-02 14:23  
     */
    private FmeWebSocketClientMonitor()
    {
        super("FmeWebSocketClientMonitor-Thread");
        this.sleepMillisecondsPerProcess = 5000;
        this.waitMessage = " FCM-FME-WEBSOCKET----WebsocketAbnormalMonitor---Sleep---没有可用的websocket客户端对象，websocket异常情况监控线程进入休眠状态！";
        this.workMessage = " FCM-FME-WEBSOCKET----WebsocketAbnormalMonitor---Work---收到可用的websocket客户端对象通知，websocket异常情况监控线程进入工作状态！";
    }
    
    protected void process()
    {
        for (Iterator<WebsocketClient> iterator = queue.iterator(); iterator.hasNext();)
        {
            try
            {
                WebsocketClient webSocketClient = iterator.next();
                boolean isWebsocketConnectionOpen = webSocketClient.isOpen()
                        && !webSocketClient.getSocket().isClosed()
                        && !webSocketClient.getSocket().isInputShutdown()
                        && !webSocketClient.getSocket().isOutputShutdown();
                
                StringBuilder websocketConnectionStatus = new StringBuilder();
                websocketConnectionStatus.append("isOpen:").append(webSocketClient.isOpen()).append('\n');
                websocketConnectionStatus.append("isClosed:").append(webSocketClient.getSocket().isClosed()).append('\n');
                websocketConnectionStatus.append("isInputShutdown:").append(webSocketClient.getSocket().isInputShutdown()).append('\n');
                websocketConnectionStatus.append("isOutputShutdown:").append(webSocketClient.getSocket().isOutputShutdown()).append('\n');
                
                // 会议桥没被删除和禁用，且和现在已连接的webSocket的地址相同，则执行检测逻辑，否则执行webSocket连接关闭
                if (!webSocketClient.getWebSocketProcessor().getFmeBridge().isDeleted())
                {
                    BusiFme busiFme = webSocketClient.getWebSocketProcessor().getFmeBridge().getBusiFme();
                    
                    // 如果出现一次无法连接，则再做诊断，让三次检测结果都指向无法连接，则证明该会议桥确实不可用
                    if (!BridgeUtils.isConnectable(busiFme))
                    {
                        BridgeDiagnosisResult result = BridgeUtils.diagnosis(busiFme);
                        if (result.getSuccessCount() == 0 || !isWebsocketConnectionOpen)
                        {
                            if (isWebsocketConnectionOpen)
                            {
                                webSocketClient.closeConnection(CloseFrame.ABNORMAL_CLOSE, result.toString());
                                webSocketClient.onClose(CloseFrame.ABNORMAL_CLOSE, result.toString(), false);
                            }
                            else
                            {
                                webSocketClient.closeConnection(CloseFrame.ABNORMAL_CLOSE, websocketConnectionStatus.toString() + "-2");
                                webSocketClient.onClose(CloseFrame.ABNORMAL_CLOSE, websocketConnectionStatus.toString() + "-2", false);
                            }
                            webSocketClient.setResult(result);
                            
                            // 移除上一个取出的对象，即webSocketClient
                            iterator.remove();
                            
                            // 记录日志
                            webSocketClient.getWebSocketProcessor().getFmeBridge().getFmeLogger().logWebsocketInfo("The websocket client was closed and removed from the websocketclientqueue-0-0!", true);
                        }
                        else if (result.getSuccessRate() < 1)
                        {
                            // 记录日志
                            webSocketClient.getWebSocketProcessor().getFmeBridge().getFmeLogger().logWebsocketInfo("Websocket health: " + result.toString(), true, true);
                        }
                    }
                    // 此种情况进来的概率万分之一，但该场景一旦发生就是致命的，必须要处理
                    else if (!isWebsocketConnectionOpen)
                    {
                        webSocketClient.closeConnection(CloseFrame.ABNORMAL_CLOSE, websocketConnectionStatus.toString() + "-1");
                        
                        // 移除上一个取出的对象，即webSocketClient
                        iterator.remove();
                        
                        webSocketClient.onClose(CloseFrame.ABNORMAL_CLOSE, websocketConnectionStatus.toString() + "-1", false);
                        
                        // 记录日志
                        webSocketClient.getWebSocketProcessor().getFmeBridge().getFmeLogger().logWebsocketInfo("The websocket client was closed and removed from the websocketclientqueue-1-1!", true);
                    }
                    else
                    {
                        try
                        {
                            // 正常链接情况就做别的事情
                            SystemStatusResponse ssr = webSocketClient.getWebSocketProcessor().getFmeBridge().getSystemInvoker().getSystemStatus();
                            if (ssr != null && ssr.getStatus() != null)
                            {
                                webSocketClient.getWebSocketProcessor().getFmeBridge().getDataCache().setSystemStatus(ssr.getStatus());
                            }
                        }
                        catch (Exception e)
                        {
                            webSocketClient.getWebSocketProcessor().getFmeBridge().getFmeLogger().logInfo("Error processing basic business", true, e);
                        }
                    }
                }
                else
                {
                    BusiFmeDBSynchronizer.getInstance().add(webSocketClient.getWebSocketProcessor().getFmeBridge().getBusiFme());
                    webSocketClient.closeConnection(CloseFrame.ABNORMAL_CLOSE, "Listening to the deletion or disabling of the conference bridge or the change of the conference bridge address, this program has closed the websocket connection of the conference bridge!" );
                    
                    // 移除上一个取出的对象，即webSocketClient
                    iterator.remove();
                    webSocketClient.onClose(CloseFrame.ABNORMAL_CLOSE, "The websocket connection exception is detected and closed-3-3!" , false);
                    webSocketClient.getWebSocketProcessor().getFmeBridge().getFmeLogger().logWebsocketInfo("The websocket client was closed and removed from the websocketclientqueue-2-2!", true);
                }
            }
            catch (Throwable e)
            {
                logger.error(" FCM-FME-WEBSOCKET---WebsocketAbnormalMonitor run error-2", e);
            }
        }
    }

    /**
     * <pre>获取单例</pre>
     * @author lilinhai
     * @since 2020-12-02 14:24 
     * @return WebsocketAbnormalMonitor
     */
    public static FmeWebSocketClientMonitor getInstance()
    {
        return INSTANCE;
    }
}
