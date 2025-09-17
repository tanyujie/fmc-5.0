package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.exception.CustomException;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.model.ConstAPI;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 10:46
 */
public class SmcPortalAuthMeetingAdminTokenInvoker extends SmcApiInvoker {

    public static final int INT = 30000;
    private static final Map<String, SmcAuthResponse> bridgeMeetingTokenMapExpire = new ConcurrentHashMap<>();
    private final Smc3Bridge smc3Bridge;


    public SmcPortalAuthMeetingAdminTokenInvoker(String rootUrl, String meetingUrl, Smc3Bridge smc3Bridge) {
        super(rootUrl, meetingUrl);
        this.smc3Bridge = smc3Bridge;
    }

    public String getAuthToken() throws IOException {
        String authToken = ClientAuthentication.getAuthToken(meetingUrl + "/tokens", smc3Bridge.getBusiSMC().getMeetingUsername(), smc3Bridge.getBusiSMC().getMeetingPassword());
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse.getUuid();
    }

    private String getToken(String username, String password) {
        String authToken = null;
        try {
            authToken = ClientAuthentication.getAuthToken(meetingUrl + "/tokens", username, password);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("smc会议token获取失败");
        }
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse.getUuid();
    }

    public void updateToken(String token) {
        Map<String, String> headers = new HashMap<>(1);
        headers.put("token", token);
        String url = "/tokens/update?clientType=smcportal";
        try {
            String s = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            System.out.println(s);
            if (s != null && s.contains(ConstAPI.TOKEN_NOT_EXIST)) {
                token = getMeetingAuthToken();
            } else {
                SmcAuthResponse smcAuthResponse = JSON.parseObject(s, SmcAuthResponse.class);
                token = smcAuthResponse.getUuid();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getServiceZoneId(Map<String, String> headers) {
        String url = "/servicezones?page=0&size=10&sort=createdDate,desc";
        try {
            return ClientAuthentication.httpGet(rootUrl + url, null, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMeetingAuthToken() {
        return getToken(smc3Bridge.getBusiSMC().getMeetingUsername(), smc3Bridge.getBusiSMC().getMeetingPassword());
    }

    public SmcAuthResponse getMeetingAuthTokenResExpire() {
        String authToken = null;
        try {
            authToken = ClientAuthentication.getAuthToken(meetingUrl + "/tokens",smc3Bridge.getBusiSMC().getMeetingUsername(), smc3Bridge.getBusiSMC().getMeetingPassword());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("smc会议token获取失败");
        }
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse;
    }

    public Map<String, String> getMeetingHeaders() {
        Map<String, String> headers = new HashMap<>(1);
        SmcAuthResponse smcAuthResponse = bridgeMeetingTokenMapExpire.get(baseIp);
        if (smcAuthResponse == null) {
            getHeaderRequest(headers);
        } else {
            long expire = smcAuthResponse.getExpire();
            if (System.currentTimeMillis() - INT >= expire) {
                getHeaderRequest(headers);
            } else {
                headers.put("token", smcAuthResponse.getUuid());
            }
        }
        // getHeaderRequest(headers);
        return headers;
    }

    private void getHeaderRequest(Map<String, String> headers) {
        SmcAuthResponse meetingAuthTokenResExpire = getMeetingAuthTokenResExpire();
        if (meetingAuthTokenResExpire != null) {
            meetingAuthTokenResExpire.setExpire(System.currentTimeMillis()+20*1000*60);
            bridgeMeetingTokenMapExpire.put(baseIp, meetingAuthTokenResExpire);
            headers.put("token", meetingAuthTokenResExpire.getUuid());
        }
    }

    private String checkMeetingHeader(Map<String, String> headers) {
        String serviceZoneId = getServiceZoneId(headers);
        if (serviceZoneId != null && serviceZoneId.contains(ConstAPI.TOKEN_NOT_EXIST)) {
            String meetingAuthToken = getMeetingAuthToken();
            //TokenManager.setMeetingToken(baseIp, meetingAuthToken);
            return meetingAuthToken;
        }
        return headers.get("token");
    }


}
