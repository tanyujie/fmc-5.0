package com.paradisecloud.smc3.busi.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.paradisecloud.fcm.common.enumer.FmeBusiStatus;
import com.paradisecloud.fcm.dao.model.BusiMcuSmc3;
import com.paradisecloud.smc3.invoker.*;
import com.paradisecloud.smc3.model.SmcBridgeStatus;
import com.paradisecloud.smc3.model.response.ConfigIvr;
import com.paradisecloud.smc3.model.response.SmcOrganization;
import com.paradisecloud.smc3.model.response.UserInfoRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author nj
 * @date 2022/10/11 14:16
 */
public class Smc3Bridge {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Object> params;

    public static final String HTTP = "https://";
    private String rootUrl;
    private String meetingUrl;
    private BusiMcuSmc3 busiSMC;
    private Smc3BridgeCluster smc3BridgeCluster;
    private String scUrl;
    private String connectionFailedReason;
    private Boolean isWebsocketAvailable;
    private SmcBridgeStatus bridgeStatus;
    private volatile int systemResourceCount = 0;
    private volatile int usedResourceCount = 0;

    private volatile int webSocketBreakCount;
    private volatile Set<String> wsAuthTokens = new HashSet<>();
    private volatile int websocketConnectionTryTimesSinceLastDisconnected;
    private volatile Date firstConnectedTime;
    private volatile Date lastConnectedTime;
    private volatile Date lastDisConnectedTime;
    private SmcPortalAuthTokenInvoker smcportalTokenInvoker;
    private volatile int weight;
    private volatile Map<String, SmcOrganization> smcOrganizationMap;

    private SmcPortalAuthMeetingAdminTokenInvoker smcPortalAuthMeetingAdminTokenInvoker;

    private SmcConferencesTemplateInvoker smcConferencesTemplateInvoker;

    private SmcConferencesInvoker smcConferencesInvoker;

    private TicketAPIInvoker ticketAPIInvoker;

    private SmcMeetingroomsInvoker smcMeetingroomsInvoker;


    private SmcOrganizationsInvoker smcOrganizationsInvoker;


    private SmcDeviceroutesInvoker smcDeviceroutesInvoker;

    private SmcServiceZoneIdInvoker smcServiceZoneIdInvoker;

    private SmcParticipantInvoker smcParticipantInvoker;

    private SmcParticipantsInvoker smcParticipantsInvoker;

    private SmcMultiPicPollInvoker smcMultiPicPollInvoker;

    private SmcUserInvoker smcUserInvoker;
    private SmcTicketInvoker smcTicketInvoker;


    public SmcUserInvoker getSmcUserInvoker() {
        return smcUserInvoker;
    }


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setSmcUserInvoker(SmcUserInvoker smcUserInvoker) {
        this.smcUserInvoker = smcUserInvoker;
    }

    public SmcMultiPicPollInvoker getSmcMultiPicPollInvoker() {
        return smcMultiPicPollInvoker;
    }

    public void setSmcMultiPicPollInvoker(SmcMultiPicPollInvoker smcMultiPicPollInvoker) {
        this.smcMultiPicPollInvoker = smcMultiPicPollInvoker;
    }

    public BusiMcuSmc3 getBusiSMC() {
        return busiSMC;
    }

    public void setBusiSMC(BusiMcuSmc3 busiSMC) {
        this.busiSMC = busiSMC;
    }

    public SmcParticipantsInvoker getSmcParticipantsInvoker() {
        return smcParticipantsInvoker;
    }

    public void setSmcParticipantsInvoker(SmcParticipantsInvoker smcParticipantsInvoker) {
        this.smcParticipantsInvoker = smcParticipantsInvoker;
    }

    public SmcParticipantInvoker getSmcParticipantInvoker() {
        return smcParticipantInvoker;
    }

    public void setSmcParticipantInvoker(SmcParticipantInvoker smcParticipantInvoker) {
        this.smcParticipantInvoker = smcParticipantInvoker;
    }

    public SmcDeviceroutesInvoker getSmcDeviceroutesInvoker() {
        return smcDeviceroutesInvoker;
    }

    public void setSmcDeviceroutesInvoker(SmcDeviceroutesInvoker smcDeviceroutesInvoker) {
        this.smcDeviceroutesInvoker = smcDeviceroutesInvoker;
    }

    public SmcServiceZoneIdInvoker getSmcServiceZoneIdInvoker() {
        return smcServiceZoneIdInvoker;
    }

    public void setSmcServiceZoneIdInvoker(SmcServiceZoneIdInvoker smcServiceZoneIdInvoker) {
        this.smcServiceZoneIdInvoker = smcServiceZoneIdInvoker;
    }

    public String getScUrl() {
        return scUrl;
    }

    public void setScUrl(String scUrl) {
        this.scUrl = scUrl;
    }

    public SmcOrganizationsInvoker getSmcOrganizationsInvoker() {
        return smcOrganizationsInvoker;
    }

    public void setSmcOrganizationsInvoker(SmcOrganizationsInvoker smcOrganizationsInvoker) {
        this.smcOrganizationsInvoker = smcOrganizationsInvoker;
    }

    public TicketAPIInvoker getTicketAPIInvoker() {
        return ticketAPIInvoker;
    }

    public void setTicketAPIInvoker(TicketAPIInvoker ticketAPIInvoker) {
        this.ticketAPIInvoker = ticketAPIInvoker;
    }

    public SmcMeetingroomsInvoker getSmcMeetingroomsInvoker() {
        return smcMeetingroomsInvoker;
    }

    public void setSmcMeetingroomsInvoker(SmcMeetingroomsInvoker smcMeetingroomsInvoker) {
        this.smcMeetingroomsInvoker = smcMeetingroomsInvoker;
    }

    public SmcConferencesInvoker getSmcConferencesInvoker() {
        return smcConferencesInvoker;
    }

    public void setSmcConferencesInvoker(SmcConferencesInvoker smcConferencesInvoker) {
        this.smcConferencesInvoker = smcConferencesInvoker;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public SmcPortalAuthMeetingAdminTokenInvoker getSmcPortalAuthMeetingAdminTokenInvoker() {
        return smcPortalAuthMeetingAdminTokenInvoker;
    }

    public void setSmcPortalAuthMeetingAdminTokenInvoker(SmcPortalAuthMeetingAdminTokenInvoker smcPortalAuthMeetingAdminTokenInvoker) {
        this.smcPortalAuthMeetingAdminTokenInvoker = smcPortalAuthMeetingAdminTokenInvoker;
    }

    public SmcConferencesTemplateInvoker getSmcConferencesTemplateInvoker() {
        return smcConferencesTemplateInvoker;
    }

    public void setSmcConferencesTemplateInvoker(SmcConferencesTemplateInvoker smcConferencesTemplateInvoker) {
        this.smcConferencesTemplateInvoker = smcConferencesTemplateInvoker;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }



    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }



    public SmcPortalAuthTokenInvoker getSmcportalTokenInvoker() {
        return smcportalTokenInvoker;
    }

    public void setSmcportalTokenInvoker(SmcPortalAuthTokenInvoker smcportalTokenInvoker) {
        this.smcportalTokenInvoker = smcportalTokenInvoker;
    }

    public Boolean getWebsocketAvailable() {
        return isWebsocketAvailable;
    }

    public void setWebsocketAvailable(Boolean websocketAvailable) {
        isWebsocketAvailable = websocketAvailable;
    }

    public SmcBridgeStatus getBridgeStatus() {
        return bridgeStatus;
    }

    public void setBridgeStatus(SmcBridgeStatus bridgeStatus) {
        this.bridgeStatus = bridgeStatus;
        busiSMC.setStatus(FmeBusiStatus.ONLINE.getValue());
    }

    public void setBridgeStatus(SmcBridgeStatus bridgeStatus, String authToken)
    {
        this.bridgeStatus = bridgeStatus;
        if (bridgeStatus == SmcBridgeStatus.AVAILABLE)
        {
            if (this.wsAuthTokens.contains(authToken))
            {
                busiSMC.setStatus(FmeBusiStatus.ONLINE.getValue());
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
            busiSMC.setStatus(FmeBusiStatus.OFFLINE.getValue());
            this.decWebsocketConnectionCount(authToken);
        }
        else
        {
            busiSMC.setStatus(FmeBusiStatus.OFFLINE.getValue());
        }
    }

    public String getConnectionFailedReason() {
        return connectionFailedReason;
    }

    public void setConnectionFailedReason(String connectionFailedReason) {
        this.connectionFailedReason = connectionFailedReason;
    }

    public Smc3Bridge(BusiMcuSmc3 busiFme) {
        this.busiSMC = busiFme;
        init();
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

    public void init() {
        this.params=new HashMap<>();
        String baseUrl = HTTP + busiSMC.getIp();
        this.rootUrl = baseUrl + "/sys-portal";
        this.meetingUrl = baseUrl + "/conf-portal";
        this.smcportalTokenInvoker = new SmcPortalAuthTokenInvoker(rootUrl, meetingUrl,this);
        this.smcPortalAuthMeetingAdminTokenInvoker = new SmcPortalAuthMeetingAdminTokenInvoker(rootUrl, meetingUrl, this);
        this.smcConferencesTemplateInvoker = new SmcConferencesTemplateInvoker(rootUrl, meetingUrl);
        this.smcConferencesInvoker = new SmcConferencesInvoker(rootUrl, meetingUrl);
        this.ticketAPIInvoker = new TicketAPIInvoker(rootUrl, meetingUrl);
        this.smcMeetingroomsInvoker = new SmcMeetingroomsInvoker(rootUrl, meetingUrl);
        this.scUrl = busiSMC.getScUrl();
        this.smcOrganizationsInvoker=new SmcOrganizationsInvoker(rootUrl, meetingUrl);
        this.smcParticipantInvoker=new SmcParticipantInvoker(rootUrl,meetingUrl);
        this.smcConferencesInvoker=new SmcConferencesInvoker(rootUrl,meetingUrl);
        this.smcMultiPicPollInvoker=new SmcMultiPicPollInvoker(rootUrl,meetingUrl);
        this.smcUserInvoker=new SmcUserInvoker(rootUrl,meetingUrl);
        this.smcServiceZoneIdInvoker=new SmcServiceZoneIdInvoker(rootUrl,meetingUrl);
        this.smcDeviceroutesInvoker=new SmcDeviceroutesInvoker(rootUrl,meetingUrl);
        this.smcParticipantsInvoker =new SmcParticipantsInvoker(rootUrl,meetingUrl);
        this.smcTicketInvoker=new SmcTicketInvoker(rootUrl,meetingUrl);
        this.isWebsocketAvailable=false;



    }


    public  String getIp(){
        return this.busiSMC.getIp();
    }


    public  String refreshSysToken(String token){
        Map<String, String> param = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        param.put("clientType","smcportal");
        header.put("token",token);
        String authToken = null;
        try {
            authToken = ClientAuthentication.httpGet(rootUrl + "/tokens/update",param,header);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse.getUuid();
    }


    public String refreshMeetingToken(String token){
        Map<String, String> param = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        param.put("clientType","smcportal");
        header.put("token",token);
        String authToken = null;
        try {
            authToken = ClientAuthentication.httpGet(meetingUrl + "/tokens/update",param,header);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse.getUuid();
    }



    public String getOrgId() {

        UserInfoRep userInfoRep = null;
        try {
            String userInfo = this.getSmcUserInvoker().getUserInfo(this.getSmcportalTokenInvoker().getAuthToken(), this.getSmcportalTokenInvoker().getSystemHeaders());
            userInfoRep = JSON.parseObject(userInfo, UserInfoRep.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userInfoRep == null) {
            return null;
        }
        String id = userInfoRep.getAccount().getOrganization().getId();
        return id;
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

    public boolean isAvailable() {

        return bridgeStatus == SmcBridgeStatus.AVAILABLE;
    }

    public Smc3BridgeCluster getSmc3BridgeCluster() {
        return smc3BridgeCluster;
    }

    public void setSmc3BridgeCluster(Smc3BridgeCluster smc3BridgeCluster) {
        this.smc3BridgeCluster = smc3BridgeCluster;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Map<String, SmcOrganization> getSmcOrganizationMap() {
        return smcOrganizationMap;
    }

    public void setSmcOrganizationMap(Map<String, SmcOrganization> smcOrganizationMap) {
        this.smcOrganizationMap = smcOrganizationMap;
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

    public String getBridgeIp()
    {
        if (!ObjectUtils.isEmpty(busiSMC.getCucmIp()))
        {
            return busiSMC.getCucmIp();
        }
        return busiSMC.getIp();
    }

    public String getTenantId() {
        try {
            String configConferenceIvr = this.smcServiceZoneIdInvoker.getConfigConferenceIvr(this.smcportalTokenInvoker.getSystemHeaders());
            logger.info("smc3 tenantId ->configConferenceIvr:{}", configConferenceIvr);
            ConfigIvr configIvr = JSONObject.parseObject(configConferenceIvr, ConfigIvr.class);
            return configIvr.getValue().getUnifiedAccessCode();
        } catch (Exception e) {
            logger.error("获取smc3 tenantId失败");
        }
        return "9";
    }



}
