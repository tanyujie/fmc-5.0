package com.paradisecloud.smc3.invoker;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.smc3.busi.cache.Smc3Bridge;
import com.paradisecloud.smc3.model.ConstAPI;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nj
 * @date 2022/8/12 10:46
 */
@Slf4j
public class SmcPortalAuthTokenInvoker extends SmcApiInvoker {
    private Smc3Bridge smc3Bridge;

    public static final int INT = 30000;
    private static Map<String, SmcAuthResponse> bridgeSystemTokenMapExpire = new ConcurrentHashMap<>();

    public SmcPortalAuthTokenInvoker(String rootUrl, String meetingUrl, Smc3Bridge smc3Bridge) {
        super(rootUrl,meetingUrl);
        this.smc3Bridge=smc3Bridge;

    }


    public String getAuthToken() throws IOException {
        String authToken = ClientAuthentication.getAuthToken(rootUrl + "/tokens?clientType=smcportal", smc3Bridge.getBusiSMC().getUsername(),  smc3Bridge.getBusiSMC().getPassword());
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
            meetingAuthTokenResExpire.setExpire(System.currentTimeMillis()+20*1000*60);
            bridgeSystemTokenMapExpire.put(baseIp,meetingAuthTokenResExpire);
            headers.put("token", meetingAuthTokenResExpire.getUuid());
        }
    }

    private SmcAuthResponse  getSystemAuthTokenResExpire(){
        String authToken = null;
        try {
            authToken = ClientAuthentication.getAuthToken(rootUrl + "/tokens?clientType=smcportal", smc3Bridge.getBusiSMC().getUsername(),  smc3Bridge.getBusiSMC().getPassword());
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

    public String checkVmr(String number,  Map<String, String> headers) {
        String url = "/vmr/legality" + "/" + number;
        HashMap<String, String> params = new HashMap<>();
        try {
            return ClientAuthentication.httpGet(rootUrl + url, params, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getVmr(String number,  Map<String, String> headers) {
        String url = "/vmr/usedinfo" + "/" + number;
        HashMap<String, String> params = new HashMap<>();
        try {
            return ClientAuthentication.httpGet(rootUrl + url, params, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createVmr(String params,  Map<String, String> headers) {
        String url = "/vmr";
        try {
            return ClientAuthentication.httpPost(rootUrl + url, params, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String changeVmrPwd(String id, String chairmanPassword, String guestPassword, Map<String, String> headers) {
        String url = "/vmr/pwd" + "/" + id;
        HashMap<String, Object> params = new HashMap<>();
        params.put("guestActive", false);
        params.put("chairmanPassword", chairmanPassword);
        params.put("guestPassword", guestPassword);
        String requestBody = JSON.toJSONString(params);
        try {
            return ClientAuthentication.httpPut(rootUrl + url, requestBody, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteVmr(String id, Map<String, String> headers) {
        String url = "/vmr/list";
        List<HashMap<String, Object>> params = new ArrayList<>();
        HashMap<String, Object> idObject = new HashMap<>();
        idObject.put("id", id);
        params.add(idObject);
        String requestBody = JSON.toJSONString(params);
        try {
            return ClientAuthentication.httpDelete(rootUrl + url, requestBody, headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String checkSysTemHeader(Map<String, String> headers) {
        String serviceZoneId = getUserInfo(smc3Bridge.getBusiSMC().getUsername(),headers);
        if (serviceZoneId != null && serviceZoneId.contains(ConstAPI.TOKEN_NOT_EXIST)) {
            String authToken = null;
            try {
                authToken = getAuthToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //TokenManager.setSystemToken(baseIp, authToken);
            return authToken;
        }
        return headers.get("token");
    }


    public String getUserName() {
        return  smc3Bridge.getBusiSMC().getUsername();
    }


}
