/*
 * Copyright : LinHai Technologies Co., Ltd. Copyright 2015-2020, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName : WebsocketAbnormalMonitor.java
 * Package : com.paradisecloud.fcm.sync.thread
 * 
 * @author lilinhai
 * 
 * @since 2020-12-17 18:17
 * 
 * @version V1.0
 */
package com.paradisecloud.fcm.fme.websocket.async;

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.paradisecloud.common.core.model.RestResponse;
import com.paradisecloud.fcm.common.async.AsyncMessageProcessor;
import com.paradisecloud.fcm.common.enumer.FmeBridgeStatus;
import com.paradisecloud.fcm.fme.cache.model.FmeBridge;
import com.paradisecloud.fcm.fme.cache.model.enumer.OutgoingH264chpValue;
import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessage;
import com.paradisecloud.fcm.fme.cache.model.message.FmeBridgeMessageQueue;
import com.paradisecloud.fcm.fme.cache.utils.FcmThreadPool;
import com.paradisecloud.fcm.fme.model.cms.CdrReceiver;
import com.paradisecloud.fcm.fme.model.parambuilder.CdrParamBuilder;
import com.paradisecloud.fcm.fme.model.response.cdr.CdrReceiversResponse;
import com.paradisecloud.fcm.fme.websocket.core.WebsocketClient;
import com.paradisecloud.fcm.fme.websocket.interfaces.IWebSocketService;
import com.paradisecloud.fcm.fme.websocket.model.BusiFmeDBSynchronizer;
import com.sinhy.spring.BeanFactory;
import com.sinhy.utils.HostUtils;

/**
 * <pre>websocket首次打开执行器</pre>
 * 
 * @author lilinhai
 * @since 2020-12-17 18:17
 * @version V1.0
 */
public class WebsocketOnOpenProcessor extends AsyncMessageProcessor<WebsocketClient>
{
    
    /**
     * 单例线程对象
     */
    private static final WebsocketOnOpenProcessor INSTANCE = new WebsocketOnOpenProcessor();
    
    private IWebSocketService mcuNodeWebSocketService;
    
    /**
     * <pre>构造方法</pre>
     * 
     * @author lilinhai
     * @since 2020-12-02 14:23
     */
    private WebsocketOnOpenProcessor()
    {
        super("WebsocketOnOpenProcessor-Thread");
        this.sleepMillisecondsPerProcess = 3000;
        this.waitMessage = " FCM-FME-WEBSOCKET----WebsocketOnOpenProcessor---Sleep---没有新创建的websocket客户端对象，websocket首次打开执行器线程进入休眠状态！";
        this.workMessage = " FCM-FME-WEBSOCKET----WebsocketOnOpenProcessor---Work---收到新创建的websocket连接对象通知，websocket首次打开执行器线程进入工作状态！";
        this.mcuNodeWebSocketService = BeanFactory.getBean(IWebSocketService.class);
    }
    
    protected void process(WebsocketClient websocketClient)
    {
        if (!websocketClient.getWebSocketProcessor().getFmeBridge().isDeleted())
        {
            FcmThreadPool.exec(() -> {
                websocketClient.getWebSocketProcessor().getFmeBridge().initCallBridgeId();
                mcuNodeWebSocketService.syncAllData(websocketClient.getWebSocketProcessor().getFmeBridge());
                configureCdrReceiver(websocketClient.getWebSocketProcessor().getFmeBridge());
                bindNtpServer(websocketClient.getWebSocketProcessor().getFmeBridge());
                allowOutgoingH264chp(websocketClient.getWebSocketProcessor().getFmeBridge());
                websocketClient.getWebSocketProcessor().getFmeBridge().setBridgeStatus(FmeBridgeStatus.AVAILABLE, websocketClient.getAuthToken());
                BusiFmeDBSynchronizer.getInstance().add(websocketClient.getWebSocketProcessor().getFmeBridge().getBusiFme());
                FmeBridgeMessageQueue.getInstance().put(new FmeBridgeMessage(websocketClient.getWebSocketProcessor().getFmeBridge()));
                websocketClient.getWebSocketProcessor().sendFirstSubscriptionRequest();
            });
        }
    }
    
    private void bindNtpServer(FmeBridge fmeBridge)
    {
        try
        {
            List<String> ips = HostUtils.getLocalIPAddresses();
            if (ObjectUtils.isEmpty(ips))
            {
                fmeBridge.getFmeLogger().logInfo("CDR接收地址配置失败，无法读取本机IP地址！", true, true);
                return;
            }
            fmeBridge.getJschInvoker().execCmd("ntp server del " + ips.get(0));
            fmeBridge.getJschInvoker().execCmd("ntp server add " + ips.get(0));
            logger.info("设置ntp服务器成功！");
        }
        catch (Exception e)
        {
            logger.error("设置ntp服务器失败", e);
        }
    }
    
    private void allowOutgoingH264chp(FmeBridge fmeBridge)
    {
        try
        {
            fmeBridge.getFmeBackgroundInvoker().allowOutgoingH264chp(OutgoingH264chpValue.ON);
            logger.info("allowOutgoingH264chp设置成功！");
        }
        catch (Throwable e)
        {
            logger.error("allowOutgoingH264chp设置成失败", e);
        }
    }
    
    private void configureCdrReceiver(FmeBridge fmeBridge)
    {
        List<String> ips = HostUtils.getLocalIPAddresses();
        if (ObjectUtils.isEmpty(ips))
        {
            fmeBridge.getFmeLogger().logInfo("CDR接收地址配置失败，无法读取本机IP地址！", true, true);
            return;
        }
        StringBuilder cdrUriBuilder = new StringBuilder();
        cdrUriBuilder.append("http://");
        cdrUriBuilder.append(ips.get(0)).append(":18275/fcm/busi/cdr/receiveCdr");
        
        String cdrUri = cdrUriBuilder.toString();
        CdrReceiversResponse cdrReceiversResponse = fmeBridge.getCdrReceiverInvoker().getCdrReceivers(0);
        List<CdrReceiver> cdrReceivers = cdrReceiversResponse.getCdrReceivers().getCdrReceiver();
        boolean isCdrUriConfiged = false;
        if (!ObjectUtils.isEmpty(cdrReceivers))
        {
            for (CdrReceiver cdrReceiver : cdrReceivers)
            {
                if (!ObjectUtils.isEmpty(cdrReceiver.getUri()))
                {
                    if (cdrUri.equals(cdrReceiver.getUri()))
                    {
                        isCdrUriConfiged = true;
                        break;
                    }
                }
            }
        }
        
        if (!isCdrUriConfiged)
        {
            if (!ObjectUtils.isEmpty(cdrReceivers) && cdrReceivers.size() >= 4)
            {
                for (int i = 3; i < cdrReceivers.size(); i++)
                {
                    RestResponse restResponse = fmeBridge.getCdrReceiverInvoker().deleteCdrReceiver(cdrReceivers.get(i).getId());
                    if (restResponse.isSuccess())
                    {
                        fmeBridge.getFmeLogger().logInfo("Delete Illegal cdr-uri successfully: " + cdrReceivers.get(i).getUri(), true, false);
                    }
                    else
                    {
                        fmeBridge.getFmeLogger().logInfo("Delete Illegal cdr-uri failed: " + cdrReceivers.get(i).getUri(), true, false);
                    }
                }
            }
            String id = fmeBridge.getCdrReceiverInvoker().createCdrReceiver(new CdrParamBuilder().uri(cdrUri).build());
            fmeBridge.getFmeLogger().logInfo("Set cdr-uri success[" + id + "]: " + cdrUri, true, false);
        }
    }
    
    /**
     * <pre>获取单例</pre>
     * 
     * @author lilinhai
     * @since 2020-12-02 14:24
     * @return WebsocketAbnormalMonitor
     */
    public static WebsocketOnOpenProcessor getInstance()
    {
        return INSTANCE;
    }
}
