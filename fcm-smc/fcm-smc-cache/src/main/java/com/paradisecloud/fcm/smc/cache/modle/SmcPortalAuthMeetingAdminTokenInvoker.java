package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.ConstAPI;
import com.paradisecloud.common.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/15 10:46
 */
public class SmcPortalAuthMeetingAdminTokenInvoker  extends SmcApiInvoker{

    public static final int INT = 30000;

    public SmcPortalAuthMeetingAdminTokenInvoker(String rootUrl, String meetingUrl, String username, String password) {
        super(rootUrl, meetingUrl);
        this.username = username;
        this.password = password;
    }

    private  String username;
    private  String password;

    public  String getAuthToken() throws IOException {
        String authToken = ClientAuthentication.getAuthToken(meetingUrl + "/tokens", username, password);
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse.getUuid();
    }


    private String getToken(String username,String password)  {
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

    public  void updateToken(String token){
        Map<String, String> headers=new HashMap<>(1);
        headers.put("token",token);
        String url="/tokens/update?clientType=smcportal";
        try {
            String s = ClientAuthentication.httpGet(meetingUrl + url, null, headers);
            System.out.println(s);
            if(s!=null&&s.contains(ConstAPI.TOKEN_NOT_EXIST)){
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

    private static Map<String, SmcAuthResponse> bridgeMeetingTokenMapExpire = new ConcurrentHashMap<>();

    public String getMeetingAuthToken() {
        return getToken(username, password);
    }

    public SmcAuthResponse getMeetingAuthTokenResExpire() {
        String authToken = null;
        try {
            authToken = ClientAuthentication.getAuthToken(meetingUrl+"/tokens", username, password);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("smc会议token获取失败");
        }
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse;
    }

    public  Map<String, String> getMeetingHeaders() {
        Map<String, String> headers = new HashMap<>(1);
        SmcAuthResponse smcAuthResponse = bridgeMeetingTokenMapExpire.get(baseIp);
        if(smcAuthResponse==null){
            getHeaderRequest(headers);
        }else {
            long expire = smcAuthResponse.getExpire();
            if(System.currentTimeMillis()- INT >=expire){
                getHeaderRequest(headers);
            }else {
                headers.put("token", smcAuthResponse.getUuid());
            }
        }
       // getHeaderRequest(headers);
        return headers;
    }

    private void getHeaderRequest(Map<String, String> headers) {
        SmcAuthResponse meetingAuthTokenResExpire = getMeetingAuthTokenResExpire();
        if(meetingAuthTokenResExpire!=null){
            bridgeMeetingTokenMapExpire.put(baseIp,meetingAuthTokenResExpire);
            headers.put("token", meetingAuthTokenResExpire.getUuid());
        }
    }

    private String checkMeetingHeader(Map<String, String> headers) {
        String serviceZoneId = getServiceZoneId(headers);
        if (serviceZoneId != null && serviceZoneId.contains(ConstAPI.TOKEN_NOT_EXIST)) {
            String meetingAuthToken = getMeetingAuthToken();
            TokenManager.setMeetingToken(baseIp, meetingAuthToken);
            return meetingAuthToken;
        }
        return headers.get("token");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
