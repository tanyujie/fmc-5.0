package com.paradisecloud.fcm.mcu.plc.cache.model;

import com.paradisecloud.fcm.common.enumer.McuPlcBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuPlcBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuPlc;
import com.paradisecloud.fcm.mcu.plc.cache.api.ConferenceManageApi;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import org.springframework.util.ObjectUtils;

public class McuPlcBridge {

    private McuPlcLogger mcuPlcLogger;
    private volatile BusiMcuPlc busiMcuPlc;
    private String bridgeAddress;
    private volatile boolean deleted;

    private volatile McuPlcBridgeStatus bridgeStatus;
    /**
     * 连接失败原因
     */
    private volatile String connectionFailedReason;

    private String baseUrl;
    private HttpRequester httpRequester;
    private volatile String mcuToken = "";
    private volatile String mcuUserToken = "";
    private ConferenceManageApi conferenceManageApi;
    private volatile long lastUpdateTime = 0;
    private volatile boolean dataInitialized;
    private volatile int systemResourceCount = 0;
    private volatile int usedResourceCount = 0;
    private volatile long diffTime = 0;

    public McuPlcBridge(BusiMcuPlc busiMcuPlc) {
        this.busiMcuPlc = busiMcuPlc;
        init();
    }

    private void init() {
        destroy();
        mcuPlcLogger = new McuPlcLogger(this, true);
        bridgeAddress = busiMcuPlc.getIp();
        if (busiMcuPlc.getProxyPort().intValue() == 80) {
            baseUrl = "http://" + busiMcuPlc.getProxyHost();
        } else {
            baseUrl = "http://" + busiMcuPlc.getProxyHost() + ":" + busiMcuPlc.getProxyPort();
        }
        httpRequester = HttpObjectCreator.getInstance().createHttpRequester();
        conferenceManageApi = new ConferenceManageApi(this);
    }

    public void cleanLoginInfo() {
        setLastUpdateTime(0);
        setMcuToken("");
        setMcuUserToken("");
    }

    public McuPlcLogger getMcuPlcLogger() {
        return mcuPlcLogger;
    }

    public BusiMcuPlc getBusiMcuPlc() {
        return busiMcuPlc;
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

    public String getMcuToken() {
        return mcuToken;
    }

    public void setMcuToken(String mcuToken) {
        this.mcuToken = mcuToken;
    }

    public String getMcuUserToken() {
        return mcuUserToken;
    }

    public void setMcuUserToken(String mcuUserToken) {
        this.mcuUserToken = mcuUserToken;
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

    public void destroy()
    {
        if (httpRequester != null)
        {
            httpRequester.destroy();
        }
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        busiMcuPlc.getParams().put("isDeleted", true);
    }

    /**
     * <p>Set Method   :   bridgeStatus McuPlcBridgeStatus</p>
     * @param bridgeStatus
     */
    public void setBridgeStatus(McuPlcBridgeStatus bridgeStatus)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == McuPlcBridgeStatus.AVAILABLE)
        {

        }
        else if (bridgeStatus == McuPlcBridgeStatus.NOT_AVAILABLE)
        {
            busiMcuPlc.setStatus(McuPlcBusiStatus.OFFLINE.getValue());
        }
        else
        {
            busiMcuPlc.setStatus(McuPlcBusiStatus.OFFLINE.getValue());
        }
    }

    /**
     * <p>Get Method   :   bridgeStatus McuPlcBridgeStatus</p>
     * @return bridgeStatus
     */
    public McuPlcBridgeStatus getBridgeStatus()
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
        return bridgeStatus == McuPlcBridgeStatus.AVAILABLE;
    }

    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isInitializing()
    {
        return bridgeStatus == null || bridgeStatus == McuPlcBridgeStatus.INITIALIZING;
    }

    public String getAttendeeIp()
    {
        if (!ObjectUtils.isEmpty(busiMcuPlc.getCucmIp()))
        {
            return busiMcuPlc.getCucmIp();
        }
        return busiMcuPlc.getIp();
    }
}
