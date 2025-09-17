package com.paradisecloud.fcm.mcu.kdc.cache.model;

import com.paradisecloud.fcm.common.enumer.McuKdcBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuKdcBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuKdc;
import com.paradisecloud.fcm.mcu.kdc.cache.api.ConferenceManageApi;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import org.cometd.client.BayeuxClient;
import org.springframework.util.ObjectUtils;

public class McuKdcBridge {

    private McuKdcLogger mcuKdcLogger;
    private volatile BusiMcuKdc busiMcuKdc;
    private String bridgeAddress;
    private volatile boolean deleted;

    private volatile McuKdcBridgeStatus bridgeStatus;
    /**
     * 连接失败原因
     */
    private volatile String connectionFailedReason;

    private String baseUrl;
    private HttpRequester httpRequester;
    private volatile String token = "";
    private volatile String cookie = "";
    private volatile String userDomainMoid = "";
    private ConferenceManageApi conferenceManageApi;
    private volatile long lastUpdateTime = 0;
    private volatile boolean dataInitialized;
    private volatile int systemResourceCount = 0;
    private volatile int usedResourceCount = 0;
    private volatile long diffTime = 0;
    private volatile BayeuxClient bayeuxClient;

    public McuKdcBridge(BusiMcuKdc busiMcuKdc) {
        this.busiMcuKdc = busiMcuKdc;
        init();
    }

    private void init() {
        destroy();
        mcuKdcLogger = new McuKdcLogger(this, true);
        bridgeAddress = busiMcuKdc.getIp();
        baseUrl = "http://" + busiMcuKdc.getIp();
        if (busiMcuKdc.getPort() != null && busiMcuKdc.getPort() != 80) {
            baseUrl += ":" + busiMcuKdc.getPort();
        }
        httpRequester = HttpObjectCreator.getInstance().createHttpRequester();
        conferenceManageApi = new ConferenceManageApi(this);
    }

    public void cleanLoginInfo() {
        setLastUpdateTime(0);
        setToken("");
        setCookie("");
        setUserDomainMoid("");
    }

    public McuKdcLogger getMcuKdcLogger() {
        return mcuKdcLogger;
    }

    public BusiMcuKdc getBusiMcuKdc() {
        return busiMcuKdc;
    }

    public String getBridgeAddress() {
        return bridgeAddress;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpRequester getHttpRequester() {
        return this.httpRequester;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserDomainMoid() {
        return userDomainMoid;
    }

    public void setUserDomainMoid(String userDomainMoid) {
        this.userDomainMoid = userDomainMoid;
    }

    public ConferenceManageApi getConferenceManageApi() {
        return this.conferenceManageApi;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public int getSystemResourceCount() {
        return systemResourceCount;
    }

    public void setSystemResourceCount(int systemResourceCount) {
        this.systemResourceCount = systemResourceCount;
    }

    public int getUsedResourceCount() {
        return usedResourceCount;
    }

    public void setUsedResourceCount(int usedResourceCount) {
        this.usedResourceCount = usedResourceCount;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(long diffTime) {
        this.diffTime = diffTime;
    }

    public boolean isDataInitialized() {
        return dataInitialized;
    }

    public void setDataInitialized(boolean dataInitialized) {
        this.dataInitialized = dataInitialized;
    }

    public BayeuxClient getBayeuxClient() {
        return bayeuxClient;
    }

    public void setBayeuxClient(BayeuxClient bayeuxClient) {
        this.bayeuxClient = bayeuxClient;
    }

    public void destroy()
    {
        if (httpRequester != null)
        {
            httpRequester.destroy();
        }
        if (bayeuxClient != null) {
            try {
                bayeuxClient.disconnect();
            } catch (Exception e) {
            }
        }
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        busiMcuKdc.getParams().put("isDeleted", true);
    }

    /**
     * <p>Set Method   :   bridgeStatus McuKdcBridgeStatus</p>
     * @param bridgeStatus
     */
    public void setBridgeStatus(McuKdcBridgeStatus bridgeStatus)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == McuKdcBridgeStatus.AVAILABLE)
        {

        }
        else if (bridgeStatus == McuKdcBridgeStatus.NOT_AVAILABLE)
        {
            busiMcuKdc.setStatus(McuKdcBusiStatus.OFFLINE.getValue());
        }
        else
        {
            busiMcuKdc.setStatus(McuKdcBusiStatus.OFFLINE.getValue());
        }
    }

    /**
     * <p>Get Method   :   bridgeStatus McuKdcBridgeStatus</p>
     * @return bridgeStatus
     */
    public McuKdcBridgeStatus getBridgeStatus()
    {
        return bridgeStatus;
    }

    public String getConnectionFailedReason() {
        return connectionFailedReason;
    }

    public void setConnectionFailedReason(String connectionFailedReason) {
        this.connectionFailedReason = connectionFailedReason;
    }

    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isAvailable()
    {
        return bridgeStatus == McuKdcBridgeStatus.AVAILABLE;
    }

    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isInitializing()
    {
        return bridgeStatus == null || bridgeStatus == McuKdcBridgeStatus.INITIALIZING;
    }

    public String getAttendeeIp()
    {
        if (!ObjectUtils.isEmpty(busiMcuKdc.getCucmIp()))
        {
            return busiMcuKdc.getCucmIp();
        }
        return busiMcuKdc.getIp();
    }
}
