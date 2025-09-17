package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

import com.huaweicloud.sdk.meeting.v1.MeetingClient;
import com.huaweicloud.sdk.meeting.v1.model.*;
import com.paradisecloud.fcm.common.enumer.FmeBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuHwcloud;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.core.HwcloudMeetingClient;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudBridgeStatus;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingCorpDir;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MyMeetingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/5/17 15:10
 */
public class HwcloudBridge {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String HwcloudUserId;
    private Map<String, Object> params;
    private BusiMcuHwcloud busiHwcloud;
    private volatile boolean deleted;
    private String connectionFailedReason;
    private HwcloudBridgeStatus hwcloudBridgeStatus;
    private volatile boolean needInit = true;
    private String mraIP;
    private MeetingClient meetingClient;
    private MyMeetingClient mymeetingClient;
    private Boolean isWebsocketAvailable;
    private HwcloudBridgeCluster HwcloudBridgeCluster;
    private volatile int webSocketBreakCount;
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    private volatile int weight;
    private TokenInfo tokenInfo;
    private ShowCorpResourceResponse showCorpResourceResponse;


    public HwcloudBridge(BusiMcuHwcloud busiHwcloud) {
        this.busiHwcloud = busiHwcloud;
        init();
    }

    private void init() {
        this.hwcloudBridgeStatus = HwcloudBridgeStatus.INITIALIZING;
        try {
            HwcloudMeetingClient hwcloudMeetingClient = new HwcloudMeetingClient(busiHwcloud.getAppId(), busiHwcloud.getAppKey());
            hwcloudMeetingClient.createCorpManagerClient();
            hwcloudMeetingClient.createCorpMyManagerClient();
            this.meetingClient=hwcloudMeetingClient.getManagerClient();
            this.mymeetingClient=hwcloudMeetingClient.getMyMeetingClient();
            MeetingCorpDir meetingCorpDir = new MeetingCorpDir(this.meetingClient);
            this.showCorpResourceResponse= meetingCorpDir.showCorpResource();

            setBridgeStatus(HwcloudBridgeStatus.AVAILABLE);
        } catch (Exception e) {
            setBridgeStatus(HwcloudBridgeStatus.NOT_AVAILABLE);
            logger.info("HwcloudBridge init error:"+e.getMessage());
        }

    }


    public MyMeetingClient getMymeetingClient() {
        return mymeetingClient;
    }

    public void setMymeetingClient(MyMeetingClient mymeetingClient) {
        this.mymeetingClient = mymeetingClient;
    }

    public String getHwcloudUserId() {
        return HwcloudUserId;
    }

    public void setHwcloudUserId(String HwcloudUserId) {
        this.HwcloudUserId = HwcloudUserId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public BusiMcuHwcloud getBusiHwcloud() {
        return busiHwcloud;
    }

    public void setBusiHwcloud(BusiMcuHwcloud busiHwcloud) {
        this.busiHwcloud = busiHwcloud;
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

    public HwcloudBridgeStatus getBridgeStatus() {
        return hwcloudBridgeStatus;
    }


    public Boolean getWebsocketAvailable() {
        return isWebsocketAvailable;
    }

    public void setWebsocketAvailable(Boolean websocketAvailable) {
        isWebsocketAvailable = websocketAvailable;
    }

    public void setBridgeStatus(HwcloudBridgeStatus hwcloudBridgeStatus) {
        this.hwcloudBridgeStatus = hwcloudBridgeStatus;
        if (hwcloudBridgeStatus == HwcloudBridgeStatus.AVAILABLE) {
            busiHwcloud.setStatus(FmeBusiStatus.ONLINE.getValue());
        } else if (hwcloudBridgeStatus == HwcloudBridgeStatus.NOT_AVAILABLE) {
            busiHwcloud.setStatus(FmeBusiStatus.OFFLINE.getValue());
        } else {
            busiHwcloud.setStatus(FmeBusiStatus.OFFLINE.getValue());
        }
    }





    public Logger getLogger() {
        return logger;
    }

    public boolean isAvailable() {
        return hwcloudBridgeStatus == HwcloudBridgeStatus.AVAILABLE;
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


    public HwcloudBridgeCluster getHwcloud3BridgeCluster() {
        return HwcloudBridgeCluster;
    }

    public void setHwcloudBridgeCluster(HwcloudBridgeCluster HwcloudBridgeCluster) {
        this.HwcloudBridgeCluster = HwcloudBridgeCluster;
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

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public MeetingClient getMeetingClient() {
        return meetingClient;
    }

    public void setMeetingClient(MeetingClient meetingClient) {
        this.meetingClient = meetingClient;
    }

    public ShowCorpResourceResponse getShowCorpResourceResponse() {
        return showCorpResourceResponse;
    }

    public void setShowCorpResourceResponse(ShowCorpResourceResponse showCorpResourceResponse) {
        this.showCorpResourceResponse = showCorpResourceResponse;
    }
}
