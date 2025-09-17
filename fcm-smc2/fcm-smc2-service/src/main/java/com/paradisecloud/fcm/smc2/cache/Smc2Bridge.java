package com.paradisecloud.fcm.smc2.cache;

import com.paradisecloud.com.fcm.smc.modle.SmcBridgeStatus;
import com.paradisecloud.fcm.common.enumer.FmeBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc2;
import com.suntek.smc.esdk.ServiceFactoryEx;
import com.suntek.smc.esdk.service.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @date 2023/5/17 15:10
 */
public class Smc2Bridge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Object> params;

    public static final String HTTP = "https://";
    private String rootUrl;
    private BusiMcuSmc2 busiSmc2;
    private volatile boolean deleted;
    private String sdkserverUrl;
    private String connectionFailedReason;
    private SmcBridgeStatus bridgeStatus;
    private ConferenceServiceEx conferenceServiceEx;

    private volatile int systemResourceCount = 0;
    private volatile int usedResourceCount = 0;
    private Smc2BridgeCluster smc2BridgeCluster;
    private volatile int webSocketBreakCount;
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    private volatile int weight;

    private SMCAddressChangeService smcAddressChangeService;
    private  SubscribeServiceEx subscribeServiceEx;
    private   AuthorizeServiceEx authorizeService;
    private TemplateServiceEx templateServiceEx;

    public Smc2Bridge(BusiMcuSmc2 busiSmc2) {
        this.busiSmc2 = busiSmc2;
        init();
    }

    private void init() {
        logger.error("SMC2BRIDGE INIT START..."+ HTTP+busiSmc2.getIp());
        rootUrl = HTTP+busiSmc2.getIp();
        sdkserverUrl=rootUrl+"/ws/Smcexternal2.asmx";
        smcAddressChangeService = ServiceFactoryEx.getService(SMCAddressChangeService.class);
        try {
            smcAddressChangeService.setSmcAddress(false,false,busiSmc2.getIp());
            authorizeService = ServiceFactoryEx.getService(AuthorizeServiceEx.class);
            Integer login = authorizeService.login(busiSmc2.getUsername(), busiSmc2.getPassword());
            if(login==0){
                    authorizeService.keepAlive();
                    final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
                    scheduledExecutorService.scheduleAtFixedRate(() -> {
                        try {
                            Integer integer = authorizeService.keepAlive();
                            if(integer!=0){
                                Integer loginAg = authorizeService.login(busiSmc2.getUsername(), busiSmc2.getPassword());
                                if(loginAg!=0){
                                    setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
                                    setConnectionFailedReason("登录失败");
                                }else {
                                    authorizeService.keepAlive();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
                            setConnectionFailedReason(e.getMessage().substring(0,500));
                        }
                    }, 20, 30, TimeUnit.SECONDS);
                conferenceServiceEx=ServiceFactoryEx.getService(ConferenceServiceEx.class);
                subscribeServiceEx = ServiceFactoryEx.getService(SubscribeServiceEx.class);
                subscribeServiceEx.enablePushEx(1, "");
                authorizeService = ServiceFactoryEx.getService(AuthorizeServiceEx.class);
                smcAddressChangeService = ServiceFactoryEx.getService(SMCAddressChangeService.class);
                templateServiceEx = ServiceFactoryEx.getService(TemplateServiceEx.class);
                addtoken(this);
            }else {
                setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
                logger.error("SMC2BRIDGE INIT FAIL ERROCODE"+login);
                if(login==1347420213){
                    logger.error("未启用第三方功能!!!!!!!!!");
                    logger.error("未启用第三方功能!!!!!!!!!");
                    logger.error("未启用第三方功能!!!!!!!!!");
                }
                setConnectionFailedReason("登录失败");
            }
        } catch (Exception e) {
            setBridgeStatus(SmcBridgeStatus.NOT_AVAILABLE);
            logger.error("SMC2BRIDGE INIT FAIL ERROCODE");
            setConnectionFailedReason("INIT FAIL...");
        }
    }


    private void logout(){
         if(authorizeService!=null){
             authorizeService.logout();
         }
    }
    public SMCAddressChangeService getSmcAddressChangeService() {
        return smcAddressChangeService;
    }

    public void setSmcAddressChangeService(SMCAddressChangeService smcAddressChangeService) {
        this.smcAddressChangeService = smcAddressChangeService;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }



    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public BusiMcuSmc2 getBusiSmc2() {
        return busiSmc2;
    }

    public void setBusiSmc2(BusiMcuSmc2 busiSmc2) {
        this.busiSmc2 = busiSmc2;
    }

    public String getSdkserverUrl() {
        return sdkserverUrl;
    }

    public void setSdkserverUrl(String sdkserverUrl) {
        this.sdkserverUrl = sdkserverUrl;
    }

    public String getConnectionFailedReason() {
        return connectionFailedReason;
    }

    public void setConnectionFailedReason(String connectionFailedReason) {
        this.connectionFailedReason = connectionFailedReason;
    }

    public SmcBridgeStatus getBridgeStatus() {
        return bridgeStatus;
    }

    public void setBridgeStatus(SmcBridgeStatus bridgeStatus) {
        this.bridgeStatus = bridgeStatus;
        busiSmc2.setStatus(bridgeStatus == SmcBridgeStatus.AVAILABLE?FmeBusiStatus.ONLINE.getValue():FmeBusiStatus.OFFLINE.getValue());
    }

    public void setBridgeStatus(SmcBridgeStatus bridgeStatus, String authToken)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == SmcBridgeStatus.AVAILABLE)
        {
            if (this.wsAuthTokens.contains(authToken))
            {
                busiSmc2.setStatus(FmeBusiStatus.ONLINE.getValue());
                this.lastConnectedTime = new Date();
                if (this.firstConnectedTime == null)
                {
                    this.firstConnectedTime = this.lastConnectedTime;
                }
                websocketConnectionTryTimesSinceLastDisconnected = 0;
                this.connectionFailedReason = null;
                logger.info("websocket 已连接，当前连接数: " + getWsAuthTokens(), true);
            }
            else
            {
                logger.info("websocket 连接失败，连接authToken[" + authToken + "]已被销毁", true);
            }
        }
        else if (bridgeStatus == SmcBridgeStatus.NOT_AVAILABLE)
        {
            busiSmc2.setStatus(FmeBusiStatus.OFFLINE.getValue());
            this.decWebsocketConnectionCount(authToken);
        }
        else
        {
            busiSmc2.setStatus(FmeBusiStatus.OFFLINE.getValue());
        }
    }


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ConferenceServiceEx getConferenceServiceEx() {
        return conferenceServiceEx;
    }

    public void setConferenceServiceEx(ConferenceServiceEx conferenceServiceEx) {
        this.conferenceServiceEx = conferenceServiceEx;
    }

    public SubscribeServiceEx getSubscribeServiceEx() {
        return subscribeServiceEx;
    }

    public void setSubscribeServiceEx(SubscribeServiceEx subscribeServiceEx) {
        this.subscribeServiceEx = subscribeServiceEx;
    }

    public AuthorizeServiceEx getAuthorizeService() {
        return authorizeService;
    }

    public void setAuthorizeService(AuthorizeServiceEx authorizeService) {
        this.authorizeService = authorizeService;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isAvailable() {
        return bridgeStatus == SmcBridgeStatus.AVAILABLE;
    }

    public TemplateServiceEx getTemplateServiceEx() {
        return templateServiceEx;
    }

    public void setTemplateServiceEx(TemplateServiceEx templateServiceEx) {
        this.templateServiceEx = templateServiceEx;
    }



    public int getWebSocketBreakCount() {
        return webSocketBreakCount;
    }

    public void setWebSocketBreakCount(int webSocketBreakCount) {
        this.webSocketBreakCount = webSocketBreakCount;
    }

    public Set<String> getWsAuthTokens() {
        return wsAuthTokens;
    }

    public void setWsAuthTokens(Set<String> wsAuthTokens) {
        this.wsAuthTokens = wsAuthTokens;
    }

    public int getWebsocketConnectionTryTimesSinceLastDisconnected() {
        return websocketConnectionTryTimesSinceLastDisconnected;
    }

    public void setWebsocketConnectionTryTimesSinceLastDisconnected(int websocketConnectionTryTimesSinceLastDisconnected) {
        this.websocketConnectionTryTimesSinceLastDisconnected = websocketConnectionTryTimesSinceLastDisconnected;
    }

    public Date getFirstConnectedTime() {
        return firstConnectedTime;
    }

    public void setFirstConnectedTime(Date firstConnectedTime) {
        this.firstConnectedTime = firstConnectedTime;
    }

    public Date getLastConnectedTime() {
        return lastConnectedTime;
    }

    public void setLastConnectedTime(Date lastConnectedTime) {
        this.lastConnectedTime = lastConnectedTime;
    }

    public Date getLastDisConnectedTime() {
        return lastDisConnectedTime;
    }

    public void setLastDisConnectedTime(Date lastDisConnectedTime) {
        this.lastDisConnectedTime = lastDisConnectedTime;
    }



    public Smc2BridgeCluster getSmc3BridgeCluster() {
        return smc2BridgeCluster;
    }

    public void setSmc2BridgeCluster(Smc2BridgeCluster smc2BridgeCluster) {
        this.smc2BridgeCluster = smc2BridgeCluster;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * <p>Set Method   :   websocketConnectionCount int</p>
     * @param authToken
     */
    public void addWebsocketConnectionCount(String authToken)
    {
        this.wsAuthTokens.add(authToken);
    }

    /**
     * <p>Set Method   :   websocketConnectionCount int</p>
     */
    private void decWebsocketConnectionCount(String authToken)
    {
        if (!ObjectUtils.isEmpty(authToken))
        {
            if (this.wsAuthTokens.remove(authToken))
            {
                this.lastDisConnectedTime = new Date();
                this.webSocketBreakCount++;
            }
        }
        websocketConnectionTryTimesSinceLastDisconnected++;
    }


    private  void addtoken(Smc2Bridge smcBridge) {
        wsAuthTokens.add(smcBridge.getBusiSmc2().getIp());
        smcBridge.setBridgeStatus(SmcBridgeStatus.AVAILABLE, smcBridge.getBusiSmc2().getIp());
    }


}
