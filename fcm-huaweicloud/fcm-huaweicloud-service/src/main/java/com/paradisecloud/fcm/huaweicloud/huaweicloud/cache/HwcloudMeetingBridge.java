package com.paradisecloud.fcm.huaweicloud.huaweicloud.cache;

import com.huaweicloud.sdk.meeting.v1.model.CreateConfTokenResponse;
import com.huaweicloud.sdk.meeting.v1.model.TokenInfo;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.HwcloudBridgeStatus;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingControl;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.MeetingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author nj
 * @date 2023/5/17 15:10
 */
public class HwcloudMeetingBridge {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, Object> params;
    private volatile boolean deleted;
    private String connectionFailedReason;
    private HwcloudBridgeStatus hwcloudBridgeStatus;
    private String mraIP;
    private Boolean isWebsocketAvailable;
    private volatile int webSocketBreakCount;
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    private TokenInfo tokenInfo;
    private String confID;
    private String hostPassword;
    private HwcloudBridge hwcloudbridge;
    private String tmpWstoken;

    private MeetingManager meetingManager;
    private MeetingControl meetingControl;

    public HwcloudMeetingBridge(HwcloudBridge hwcloudbridge) {
        this.hwcloudbridge = hwcloudbridge;
        init();
    }

    public HwcloudBridge getHwcloudbridge() {
        return hwcloudbridge;
    }

    public void setHwcloudbridge(HwcloudBridge hwcloudbridge) {
        this.hwcloudbridge = hwcloudbridge;
    }

    public MeetingManager getMeetingManager() {
        return meetingManager;
    }

    public void setMeetingManager(MeetingManager meetingManager) {
        this.meetingManager = meetingManager;
    }

    public MeetingControl getMeetingControl() {
        return meetingControl;
    }

    public void setMeetingControl(MeetingControl meetingControl) {
        this.meetingControl = meetingControl;
    }

    private void init() {
        this.hwcloudBridgeStatus = HwcloudBridgeStatus.INITIALIZING;
        setWebsocketAvailable(false);
        this.meetingControl = new MeetingControl(hwcloudbridge.getMeetingClient());
        MeetingManager meetingManager = new MeetingManager(hwcloudbridge.getMeetingClient());
        this.meetingManager=meetingManager;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
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

    public void setBridgeStatus(HwcloudBridgeStatus hwcloudBridgeStatus) {
        this.hwcloudBridgeStatus = hwcloudBridgeStatus;

    }

    public Boolean getWebsocketAvailable() {
        return isWebsocketAvailable;
    }

    public void setWebsocketAvailable(Boolean websocketAvailable) {
        this.isWebsocketAvailable = websocketAvailable;
    }

    public void setBridgeStatus(HwcloudBridgeStatus bridgeStatus, String authToken) {
        this.hwcloudBridgeStatus = bridgeStatus;
        if (bridgeStatus == HwcloudBridgeStatus.AVAILABLE) {
            if (this.wsAuthTokens.contains(authToken)) {
                this.lastConnectedTime = new Date();
                if (this.firstConnectedTime == null) {
                    this.firstConnectedTime = this.lastConnectedTime;
                }
                websocketConnectionTryTimesSinceLastDisconnected = 0;
                this.connectionFailedReason = null;
                logger.info("websocket 已连接，当前连接数: " + getWsAuthTokens(), true);
            } else {
                logger.info("websocket 连接失败，连接authToken[" + authToken + "]已被销毁", true);
            }
        } else if (bridgeStatus == HwcloudBridgeStatus.NOT_AVAILABLE) {
            this.decWebsocketConnectionCount(authToken);
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

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
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
        if (tokenInfo != null) {
            Long expireTime = tokenInfo.getExpireTime();
            if (System.currentTimeMillis() - expireTime <= 0) {
                CreateConfTokenResponse confTokenResponse = meetingControl.createConfTokenResponse(confID, hostPassword);
                this.tokenInfo = confTokenResponse.getData();
                return tokenInfo;
            }
        }
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public HwcloudBridgeStatus getHwcloudBridgeStatus() {
        return hwcloudBridgeStatus;
    }

    public void setHwcloudBridgeStatus(HwcloudBridgeStatus hwcloudBridgeStatus) {
        this.hwcloudBridgeStatus = hwcloudBridgeStatus;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getTmpWstoken() {
        return tmpWstoken;
    }

    public void setTmpWstoken(String tmpWstoken) {
        this.tmpWstoken = tmpWstoken;
    }
}
