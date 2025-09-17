package com.paradisecloud.fcm.tencent.cache;

import com.paradisecloud.fcm.common.enumer.FmeBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuTencent;
import com.paradisecloud.fcm.tencent.model.BridgeStatus;
import com.paradisecloud.fcm.tencent.model.client.TencentConferenceCtrlClient;
import com.paradisecloud.fcm.tencent.model.client.TencentLayoutClient;
import com.paradisecloud.fcm.tencent.model.client.TencentMeetingClient;
import com.paradisecloud.fcm.tencent.model.client.TencentUserClient;
import com.paradisecloud.fcm.tencent.model.reponse.TencentQueryUsersResponse;
import com.tencentcloudapi.wemeet.common.RequestSender;
import com.tencentcloudapi.wemeet.common.exception.WemeetSdkException;
import com.tencentcloudapi.wemeet.common.profile.HttpProfile;
import com.tencentcloudapi.wemeet.models.user.QueryUsersRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author nj
 * @date 2023/5/17 15:10
 */
public class TencentBridge {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String tencentUserId;
    private Map<String, Object> params;
    private BusiMcuTencent busiTencent;
    private volatile boolean deleted;
    private String connectionFailedReason;
    private BridgeStatus bridgeStatus;
    private volatile boolean needInit = true;
    private String mraIP;


    private TencentBridgeCluster TencentBridgeCluster;
    private volatile int webSocketBreakCount;
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    private volatile int weight;

    /**
     * 初始化全局会议client
     */
    private TencentMeetingClient MEETING_CLIENT;
    /**
     * 初始化全局用户client
     */
    private TencentUserClient USER_CLIENT;

    private TencentConferenceCtrlClient CONFERENCE_CTRL_CLIENT;
    private TencentLayoutClient LAYOUT_CLIENT;

    public TencentBridge(BusiMcuTencent busiTencent) {
        this.busiTencent = busiTencent;
        init();
    }

    private void init() {
        this.bridgeStatus = BridgeStatus.INITIALIZING;
        HttpProfile profile = new HttpProfile();
        profile.setAppId(busiTencent.getAppId());
        profile.setSdkId(busiTencent.getSdkId());
        profile.setHost("https://api.meeting.qq.com");
        profile.setSecretId(busiTencent.getSecretId());
        profile.setSecretKey(busiTencent.getSecretKey());
        profile.setDebug(true);
        profile.setReadTimeout(3);
        profile.setConnTimeout(1);
        RequestSender sender = new RequestSender(profile);
        // 实例化client
        MEETING_CLIENT = new TencentMeetingClient(sender);
        USER_CLIENT = new TencentUserClient(sender);
        CONFERENCE_CTRL_CLIENT = new TencentConferenceCtrlClient(sender);
        LAYOUT_CLIENT = new TencentLayoutClient(sender);
        QueryUsersRequest queryUsersRequest = new QueryUsersRequest();
        queryUsersRequest.setPage(1);
        queryUsersRequest.setPageSize(20);
        try {
            getUsers(1);
        } catch (Exception e) {
            logger.info("腾讯桥初始化失败",e.getMessage());
            setBridgeStatus(BridgeStatus.NOT_AVAILABLE);
        }

    }


    public TencentQueryUsersResponse getUsers(int page) {
        QueryUsersRequest queryUsersRequest = new QueryUsersRequest();
        queryUsersRequest.setPage(page);
        queryUsersRequest.setPageSize(20);
        TencentQueryUsersResponse queryUsersResponse = null;
        try {
            queryUsersResponse = USER_CLIENT.queryUsers(queryUsersRequest);
        } catch (WemeetSdkException e) {
            e.printStackTrace();
        }
        if (queryUsersResponse != null) {
            List<TencentQueryUsersResponse.UserDetail> users = queryUsersResponse.getUsers();
            if (!CollectionUtils.isEmpty(users)) {
                for (TencentQueryUsersResponse.UserDetail user : users) {
                    if (Objects.equals("ADMIN_ROLE", user.getRoleCode())) {
                        this.tencentUserId = user.getUserId();
                        setBridgeStatus(BridgeStatus.AVAILABLE);
                        needInit = false;
                        break;
                    }
                }
            }
            if (!needInit) {
                return queryUsersResponse;
            }
            Integer totalCount = queryUsersResponse.getTotalCount();
            double ceil = Math.ceil(20 / totalCount);

            if (needInit && queryUsersResponse.getCurrentPage() <= (int) ceil) {
                getUsers(queryUsersResponse.getCurrentPage() + 1);
            }

        } else {
            setBridgeStatus(BridgeStatus.NOT_AVAILABLE);
            return queryUsersResponse;
        }
        return queryUsersResponse;
    }


    public String getTencentUserId() {
        return tencentUserId;
    }

    public void setTencentUserId(String tencentUserId) {
        this.tencentUserId = tencentUserId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public BusiMcuTencent getBusiTencent() {
        return busiTencent;
    }

    public void setBusiTencent(BusiMcuTencent busiTencent) {
        this.busiTencent = busiTencent;
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
            busiTencent.setStatus(FmeBusiStatus.ONLINE.getValue());
        } else if (bridgeStatus == BridgeStatus.NOT_AVAILABLE) {
            busiTencent.setStatus(FmeBusiStatus.OFFLINE.getValue());
        } else {
            busiTencent.setStatus(FmeBusiStatus.OFFLINE.getValue());
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isAvailable() {
        return bridgeStatus == BridgeStatus.AVAILABLE;
    }

    public TencentMeetingClient getMEETING_CLIENT() {
        return MEETING_CLIENT;
    }

    public TencentUserClient getUSER_CLIENT() {
        return USER_CLIENT;
    }

    public TencentConferenceCtrlClient getConferenceCtrlClient() {
        return CONFERENCE_CTRL_CLIENT;
    }

    public void setConferenceCtrlClient(TencentConferenceCtrlClient conferenceCtrlClient) {
        this.CONFERENCE_CTRL_CLIENT = conferenceCtrlClient;
    }

    public TencentLayoutClient getLAYOUT_CLIENT() {
        return LAYOUT_CLIENT;
    }

    public void setLAYOUT_CLIENT(TencentLayoutClient LAYOUT_CLIENT) {
        this.LAYOUT_CLIENT = LAYOUT_CLIENT;
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


    public TencentBridgeCluster getSmc3BridgeCluster() {
        return TencentBridgeCluster;
    }

    public void setTencentBridgeCluster(TencentBridgeCluster TencentBridgeCluster) {
        this.TencentBridgeCluster = TencentBridgeCluster;
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
}
