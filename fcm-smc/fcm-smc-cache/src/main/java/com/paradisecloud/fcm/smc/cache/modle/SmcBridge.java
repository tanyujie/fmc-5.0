package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.SmcBridgeStatus;
import com.paradisecloud.com.fcm.smc.modle.response.UserInfoRep;
import com.paradisecloud.smc.dao.model.BusiSmc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author nj
 * @date 2022/8/12 10:01
 */
public class SmcBridge  {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, Object> params;

    public static final String HTTP = "https://";
    private String rootUrl;
    private String meetingUrl;
    private BusiSmc busiSMC;

    private String scUrl;
    private String connectionFailedReason;
    private Boolean isWebsocketAvailable;
    private SmcBridgeStatus bridgeStatus;
    private SmcPortalAuthTokenInvoker smcportalTokenInvoker;


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

    public BusiSmc getBusiSMC() {
        return busiSMC;
    }

    public void setBusiSMC(BusiSmc busiSMC) {
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
    }

    public String getConnectionFailedReason() {
        return connectionFailedReason;
    }

    public void setConnectionFailedReason(String connectionFailedReason) {
        this.connectionFailedReason = connectionFailedReason;
    }

    public SmcBridge(BusiSmc busiFme) {
        this.busiSMC = busiFme;
        init();
    }

    public void init() {
        this.params=new HashMap<>();
        String baseUrl = HTTP + busiSMC.getIp();
        this.rootUrl = baseUrl + "/sys-portal";
        this.meetingUrl = baseUrl + "/conf-portal";
        this.smcportalTokenInvoker = new SmcPortalAuthTokenInvoker(rootUrl, meetingUrl, busiSMC.getUsername(), busiSMC.getPassword());
        this.smcPortalAuthMeetingAdminTokenInvoker = new SmcPortalAuthMeetingAdminTokenInvoker(rootUrl, meetingUrl, busiSMC.getMeetingUsername(), busiSMC.getMeetingPassword());
        this.smcConferencesTemplateInvoker = new SmcConferencesTemplateInvoker(rootUrl, meetingUrl);
        this.smcConferencesInvoker = new SmcConferencesInvoker(rootUrl, meetingUrl);
        this.ticketAPIInvoker = new TicketAPIInvoker(rootUrl, meetingUrl);
        this.smcMeetingroomsInvoker = new SmcMeetingroomsInvoker(rootUrl, meetingUrl);
        this.scUrl = busiSMC.getScIp();
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
        String userInfo = this.getSmcUserInvoker().getUserInfo(this.getSmcportalTokenInvoker().getUsername(), this.getSmcportalTokenInvoker().getSystemHeaders());

        UserInfoRep userInfoRep = null;
        try {
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


}
