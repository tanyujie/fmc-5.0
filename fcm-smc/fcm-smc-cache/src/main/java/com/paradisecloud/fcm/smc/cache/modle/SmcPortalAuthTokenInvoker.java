package com.paradisecloud.fcm.smc.cache.modle;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.com.fcm.smc.modle.ConstAPI;
import com.paradisecloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/12 10:46
 */
@Slf4j
public class SmcPortalAuthTokenInvoker extends SmcApiInvoker {


    public static final int INT = 30000;
    private static Map<String, SmcAuthResponse> bridgeSystemTokenMapExpire = new ConcurrentHashMap<>();

    public SmcPortalAuthTokenInvoker(String rootUrl,String meetingUrl, String username, String password) {
        super(rootUrl,meetingUrl);
        this.username = username;
        this.password = password;
    }

    private String username;
    private String password;

    public String getAuthToken() throws IOException {
        String authToken = ClientAuthentication.getAuthToken(rootUrl + "/tokens?clientType=smcportal", username, password);
        log.info("smc："+authToken);
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse.getUuid();
    }

    public Map<String, String> getSystemHeaders() {
        return getStringStringMap(baseIp);
    }

    public Map<String, String> getSystemHeaders(String ipAddress) {
        return getStringStringMap(ipAddress);
    }

    private Map<String, String> getStringStringMap(String baseIp) {
        Map<String, String> headers = new HashMap<>(1);
        SmcAuthResponse smcAuthResponse = bridgeSystemTokenMapExpire.get(baseIp);
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
        return headers;

    }

    private void getHeaderRequest(Map<String, String> headers) {
        SmcAuthResponse meetingAuthTokenResExpire = getSystemAuthTokenResExpire();
        if(meetingAuthTokenResExpire!=null){
            bridgeSystemTokenMapExpire.put(baseIp,meetingAuthTokenResExpire);
            headers.put("token", meetingAuthTokenResExpire.getUuid());
        }
    }

    private SmcAuthResponse  getSystemAuthTokenResExpire(){
        String authToken = null;
        try {
            authToken = ClientAuthentication.getAuthToken(rootUrl + "/tokens?clientType=smcportal", username, password);
          log.info("rootUrl=>"+rootUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("smc："+authToken);
        if(authToken==null){
            return null;
        }
        SmcAuthResponse smcAuthResponse = JSON.parseObject(authToken, SmcAuthResponse.class);
        return smcAuthResponse;
    }

    public String getUserInfo(String name,  Map<String, String> headers) {
        String url = "/users/search/names";
        HashMap<String, String> param = new HashMap<>();
        param.put("name", name);
        try {
            return ClientAuthentication.httpGet(rootUrl + url, param, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String checkSysTemHeader(Map<String, String> headers) {
        String serviceZoneId = getUserInfo(username,headers);
        if (serviceZoneId != null && serviceZoneId.contains(ConstAPI.TOKEN_NOT_EXIST)) {
            String authToken = null;
            try {
                authToken = getAuthToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            TokenManager.setSystemToken(baseIp, authToken);
            return authToken;
        }
        return headers.get("token");
    }




    public String getUsername () {
        return username;
    }

}
