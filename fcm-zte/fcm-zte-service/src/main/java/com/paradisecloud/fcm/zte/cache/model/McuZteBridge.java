package com.paradisecloud.fcm.zte.cache.model;

import com.paradisecloud.fcm.common.enumer.McuZteBridgeStatus;
import com.paradisecloud.fcm.common.enumer.McuZteBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuZte;
import com.paradisecloud.fcm.zte.cache.api.ConferenceManageApi;
import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;
import com.zte.m900.services.MultimediaConference.MultimediaConferenceServiceLocator;
import com.zte.m900.services.MultimediaConference.MultimediaConferenceSoapBindingStub;
import org.springframework.util.ObjectUtils;

import javax.xml.rpc.ServiceException;

public class McuZteBridge {

    private McuZteLogger mcuZteLogger;
    private volatile BusiMcuZte busiMcuZte;
    private String bridgeAddress;
    private volatile boolean deleted;

    private volatile McuZteBridgeStatus bridgeStatus;
    /**
     * 连接失败原因
     */
    private volatile String connectionFailedReason;

    private String baseUrl;
    private HttpRequester httpRequester;
    private volatile String mcuToken = "";
    private volatile String mcuUserToken = "";
    private volatile String mcuUserId = "";
    private ConferenceManageApi conferenceManageApi;
    private volatile long lastUpdateTime = 0;
    private volatile boolean dataInitialized;
    private volatile int systemResourceCount = 0;
    private volatile int usedResourceCount = 0;
    private volatile long diffTime = 0;

    private   MultimediaConferenceServiceLocator mLocator;

    private  MultimediaConferenceSoapBindingStub mStub;

    public McuZteBridge(BusiMcuZte busiMcuZte) {
        this.busiMcuZte = busiMcuZte;
        init();
    }

    private void init() {
        destroy();
        mcuZteLogger = new McuZteLogger(this, true);
        bridgeAddress = busiMcuZte.getIp();
        baseUrl = "http://" + busiMcuZte.getIp() + ":" + busiMcuZte.getPort();
        httpRequester = HttpObjectCreator.getInstance().createHttpRequester();
        try {
            mLocator = new MultimediaConferenceServiceLocator();
            mLocator.setMultimediaConferenceEndpointAddress(baseUrl+"/services/MultimediaConference?wsdl");
            mStub = (MultimediaConferenceSoapBindingStub) mLocator.getMultimediaConference();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        conferenceManageApi = new ConferenceManageApi(this);
    }

    public void cleanLoginInfo() {
        setLastUpdateTime(0);
        setMcuToken("");
        setMcuUserToken("");
    }

    public McuZteLogger getMcuZteLogger() {
        return mcuZteLogger;
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
        busiMcuZte.getParams().put("isDeleted", true);
    }

    public String getMcuUserId() {
        return mcuUserId;
    }

    public void setMcuUserId(String mcuUserId) {
        this.mcuUserId = mcuUserId;
    }

    /**
     * <p>Set Method   :   bridgeStatus McuZteBridgeStatus</p>
     * @param bridgeStatus
     */
    public void setBridgeStatus(McuZteBridgeStatus bridgeStatus)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == McuZteBridgeStatus.AVAILABLE)
        {

        }
        else if (bridgeStatus == McuZteBridgeStatus.NOT_AVAILABLE)
        {
            busiMcuZte.setStatus(McuZteBusiStatus.OFFLINE.getValue());
        }
        else
        {
            busiMcuZte.setStatus(McuZteBusiStatus.OFFLINE.getValue());
        }
    }

    /**
     * <p>Get Method   :   bridgeStatus McuZteBridgeStatus</p>
     * @return bridgeStatus
     */
    public McuZteBridgeStatus getBridgeStatus()
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
        return bridgeStatus == McuZteBridgeStatus.AVAILABLE;
    }

    /**
     * <p>Get Method   :   isAvailable boolean</p>
     * @return isAvailable
     */
    public boolean isInitializing()
    {
        return bridgeStatus == null || bridgeStatus == McuZteBridgeStatus.INITIALIZING;
    }

    public String getAttendeeIp()
    {
        if (!ObjectUtils.isEmpty(busiMcuZte.getCucmIp()))
        {
            return busiMcuZte.getCucmIp();
        }
        return busiMcuZte.getIp();
    }

    public BusiMcuZte getBusiMcuZte() {
        return busiMcuZte;
    }

    public void setBusiMcuZte(BusiMcuZte busiMcuZte) {
        this.busiMcuZte = busiMcuZte;
    }

    public MultimediaConferenceSoapBindingStub getmStub() {
        return mStub;
    }
}
