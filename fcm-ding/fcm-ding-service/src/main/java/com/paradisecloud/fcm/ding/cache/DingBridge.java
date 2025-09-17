package com.paradisecloud.fcm.ding.cache;

import com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceHeaders;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetSsoUserInfoResponse;
import com.aliyun.tea.TeaException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserListadminRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserListadminResponse;
import com.paradisecloud.fcm.ding.model.BridgeStatus;
import com.paradisecloud.fcm.common.enumer.FmeBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuDing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2023/5/17 15:10
 */
public class DingBridge {
    public static final int EXPIRE_TIME = 7200 * 1000;
    private static Map<String, DingAccessToken> bridgeAccessTokenMapExpire = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String dingUserId;
    private Map<String, Object> params;
    private BusiMcuDing busiDing;
    private volatile boolean deleted;
    private String connectionFailedReason;
    private BridgeStatus bridgeStatus;
    private volatile boolean needInit = true;
    private String mraIP;


    private DingBridgeCluster DingBridgeCluster;
    private volatile int webSocketBreakCount;
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    private volatile int weight;
    private volatile Boolean stream=false;
    /**
     * 初始化全局会议client
     */
    private Client client;


    public DingBridge(BusiMcuDing busiDing) {
        this.busiDing = busiDing;
        init();
    }

    private void init() {
        this.bridgeStatus = BridgeStatus.INITIALIZING;

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(busiDing.getSecretId());
            request.setAppsecret(busiDing.getSecretKey());
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            String accessToken = response.getAccessToken();

            DingTalkClient client_user = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/listadmin");
            OapiUserListadminRequest req = new OapiUserListadminRequest();
            OapiUserListadminResponse rsp = client_user.execute(req, accessToken);
            List<OapiUserListadminResponse.ListAdminResponse> result = rsp.getResult();
            for (OapiUserListadminResponse.ListAdminResponse listAdminResponse : result) {
                Long sysLevel = listAdminResponse.getSysLevel();
                if(sysLevel==2){
                    dingUserId=listAdminResponse.getUserid();
                    break;
                }
            }

        } catch (Exception e) {
        }
        DingAccessToken cacheDingAccessToken = getCacheDingAccessToken();
        if(cacheDingAccessToken!=null){
            setBridgeStatus(BridgeStatus.AVAILABLE);
        }else {
            setBridgeStatus(BridgeStatus.NOT_AVAILABLE);
        }

    }

    private GetAccessTokenResponse  getAccessToken() {
        try {
            com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
            config.protocol = "https";
            config.regionId = "central";
            Client client2= new com.aliyun.dingtalkoauth2_1_0.Client(config);
            com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                    .setAppKey(busiDing.getSecretId())
                    .setAppSecret(busiDing.getSecretKey());
            try {
                GetAccessTokenResponse accessToken = client2.getAccessToken(getAccessTokenRequest);
                return accessToken;
            } catch (TeaException err) {
                if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                    logger.info("Ding Bridge init error code:"+ err.code);
                    logger.info("Ding Bridge init error message"+ err.message);
                }
            } catch (Exception _err) {
                TeaException err = new TeaException(_err.getMessage(), _err);
                if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                    logger.info("Ding Bridge init error code:"+ err.code);
                    logger.info("Ding Bridge init error message"+ err.message);
                }
            }

        } catch (Exception e) {
            logger.info("Ding Bridge init error "+ e.getMessage());
        }
        return null;
    }


    public String getDingUserId() {
        return dingUserId;
    }

    public void setDingUserId(String DingUserId) {
        this.dingUserId = DingUserId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public BusiMcuDing getBusiDing() {
        return busiDing;
    }

    public void setBusiDing(BusiMcuDing busiDing) {
        this.busiDing = busiDing;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getConnectionFailedReason() {
        return connectionFailedReason;
    }

    public void setConnectionFailedReason(String connectionFailedReason) {
        this.connectionFailedReason = connectionFailedReason;
    }

    public BridgeStatus getBridgeStatus() {
        return bridgeStatus;
    }

    public void setBridgeStatus(BridgeStatus bridgeStatus) {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == BridgeStatus.AVAILABLE) {
            busiDing.setStatus(FmeBusiStatus.ONLINE.getValue());
        } else if (bridgeStatus == BridgeStatus.NOT_AVAILABLE) {
            busiDing.setStatus(FmeBusiStatus.OFFLINE.getValue());
        } else {
            busiDing.setStatus(FmeBusiStatus.OFFLINE.getValue());
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isAvailable() {
        return bridgeStatus == BridgeStatus.AVAILABLE;
    }



    public String getMraIP() {
        return mraIP;
    }

    public void setMraIP(String mraIP) {
        this.mraIP = mraIP;
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


    public DingBridgeCluster getSmc3BridgeCluster() {
        return DingBridgeCluster;
    }

    public void setDingBridgeCluster(DingBridgeCluster DingBridgeCluster) {
        this.DingBridgeCluster = DingBridgeCluster;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * <p>Set Method   :   websocketConnectionCount int</p>
     *
     * @param authToken
     */
    public void addWebsocketConnectionCount(String authToken) {
        this.wsAuthTokens.add(authToken);
    }

    /**
     * <p>Set Method   :   websocketConnectionCount int</p>
     */
    private void decWebsocketConnectionCount(String authToken) {
        if (!ObjectUtils.isEmpty(authToken)) {
            if (this.wsAuthTokens.remove(authToken)) {
                this.lastDisConnectedTime = new Date();
                this.webSocketBreakCount++;
            }
        }
        websocketConnectionTryTimesSinceLastDisconnected++;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }


    public CreateVideoConferenceHeaders getDingtalkAccessHeader() {
        com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceHeaders createVideoConferenceHeaders = new com.aliyun.dingtalkconference_1_0.models.CreateVideoConferenceHeaders();
        createVideoConferenceHeaders.xAcsDingtalkAccessToken = getCacheDingAccessToken().getAccessToken();
        return   createVideoConferenceHeaders;
    }



    public DingAccessToken getCacheDingAccessToken() {
        DingAccessToken dingAccessToken = bridgeAccessTokenMapExpire.get(busiDing.getSecretId());
        if(dingAccessToken==null){
             dingAccessToken = getDingAccessToken();
        }else {
            Long expire = dingAccessToken.getExpireIn();
            if(System.currentTimeMillis()- EXPIRE_TIME >=expire){
                dingAccessToken = getDingAccessToken();
                bridgeAccessTokenMapExpire.put(busiDing.getSecretId(),dingAccessToken);
            }
        }
        return dingAccessToken;

    }

    private DingAccessToken getDingAccessToken() {
        DingAccessToken dingAccessToken;
        GetAccessTokenResponse accessTokenResponse = getAccessToken();
        if(accessTokenResponse==null){
            return null;
        }
        dingAccessToken = new DingAccessToken();
        dingAccessToken.setAccessToken(accessTokenResponse.getBody().getAccessToken());
        dingAccessToken.setExpireIn(accessTokenResponse.getBody().getExpireIn());
        return dingAccessToken;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
