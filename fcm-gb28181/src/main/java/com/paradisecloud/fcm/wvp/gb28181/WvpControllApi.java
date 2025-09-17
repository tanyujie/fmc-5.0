package com.paradisecloud.fcm.wvp.gb28181;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.fcm.wvp.gb28181.request.WvpAddDeviceRequest;
import com.paradisecloud.fcm.wvp.gb28181.request.WvpLoginRequest;
import com.paradisecloud.fcm.wvp.gb28181.reponse.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WvpControllApi {

    private WvpBridge wvpBridge;

    public WvpControllApi(WvpBridge wvpBridge) {
        this.wvpBridge = wvpBridge;
    }


    private String buildUrl() {
        return wvpBridge.getBaseUrl();
    }


    private Map<String, String> buildHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        if(wvpBridge.getAccessToken()!=null){
            headers.put("Access-Token", wvpBridge.getAccessToken());
        }
        return headers;
    }

    private StringEntity buildParams(Object params) {
        StringEntity entity = new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_JSON);
        return entity;
    }



    /**
     * 登录
     */
    public WvpLoginResponse login(WvpLoginRequest request) {
        String url = buildUrl();
        url+="/api/user/login?username="+request.getUserName()+"&password="+"21232f297a57a5a743894a0e4a801fc3";
        GenericValue<WvpLoginResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpLoginResponse response = JSON.parseObject(contentBody, WvpLoginResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 添加电子设备
     */
    public WvpCommonResponse addDevice(WvpAddDeviceRequest request) {
        String url = buildUrl();
        url+="/api/device/query/device/add/?"+"deviceId="+request.getDeviceId()+"&name="+request.getName()+"&password="
                +request.getPassword()+"&subscribeCycleForCatalog="
                +request.getSubscribeCycleForCatalog()
                +"&subscribeCycleForMobilePosition="
                +request.getSubscribeCycleForMobilePosition()
                +"&mobilePositionSubmissionInterval="+request.getMobilePositionSubmissionInterval();
        GenericValue<WvpCommonResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().post(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpCommonResponse response = JSON.parseObject(contentBody, WvpCommonResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 同步
     * @param deviceId
     * @return
     */
    public WvpCommonResponse syncDevice(String  deviceId) {
        String url = buildUrl();
        url+="/api/device/query/devices/"+deviceId+"/sync";
        GenericValue<WvpCommonResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpCommonResponse response = JSON.parseObject(contentBody, WvpCommonResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 同步状态
     * @param deviceId
     * @return
     */
    public WvpSyncStatusResponse syncStatus(String  deviceId) {
        String url = buildUrl();
        url+="/api/device/query/"+deviceId+"/sync_status/";
        GenericValue<WvpSyncStatusResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpSyncStatusResponse response = JSON.parseObject(contentBody, WvpSyncStatusResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询通道
     * @param deviceId
     * @return
     */
    public WvpChannelsResponse queryChannels(String  deviceId) {
        String url = buildUrl();
        url+="/api/device/query/devices/"+deviceId+"/channels?page=1&count=15&query=&online=&channelType=";
        GenericValue<WvpChannelsResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpChannelsResponse response = JSON.parseObject(contentBody, WvpChannelsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 播放流
     * @param deviceId
     * @return
     */
    public WvpPlayStartResponse playStart(String  deviceId, String channelId) {
        String url = buildUrl();
        url+="/api/play/start/"+deviceId+"/"+channelId;
        GenericValue<WvpPlayStartResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpPlayStartResponse response = JSON.parseObject(contentBody, WvpPlayStartResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 设备列表查询
     * @return
     */
    public WvpCommonResponse devices(){
        String url = buildUrl();
        url+="/api/device/query/devices?page=1&count=15";
        GenericValue<WvpCommonResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpCommonResponse response = JSON.parseObject(contentBody, WvpCommonResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 设备列表查询
     * @return
     */
    public WvpDevicesResponse devices(int page,int count){
        String url = buildUrl();
        url+="/api/device/query/devices?page="+page+"&count="+count;
        GenericValue<WvpDevicesResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpDevicesResponse response = JSON.parseObject(contentBody, WvpDevicesResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 修改设备
     * @param request
     * @return
     */
    public WvpCommonResponse updateDevice(WvpAddDeviceRequest request) {
        String url = buildUrl();
        url+="/api/device/query/device/update/?"+"deviceId="+request.getDeviceId()+"&name="+request.getName()+"&password="
                +request.getPassword()+"&subscribeCycleForCatalog="
                +request.getSubscribeCycleForCatalog()
                +"&subscribeCycleForMobilePosition="
                +request.getSubscribeCycleForMobilePosition()
                +"&mobilePositionSubmissionInterval="+request.getMobilePositionSubmissionInterval();
        GenericValue<WvpCommonResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().post(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpCommonResponse response = JSON.parseObject(contentBody, WvpCommonResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除设备
     * @param deviceId
     * @return
     */
    public WvpCommonResponse deleteDevice(String  deviceId) {
        String url = buildUrl();
        url+="/api/device/query/devices/"+deviceId+"/delete";
        GenericValue<WvpCommonResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().delete(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpCommonResponse response = JSON.parseObject(contentBody, WvpCommonResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }


    /**
     * 系统信息
     * @param
     * @return
     */
    public WvpConfigInfoResponse configInfo() {
        String url = buildUrl();
        url+="/api/server/system/configInfo";
        GenericValue<WvpConfigInfoResponse> genericValue = new GenericValue<>();
        wvpBridge.getHttpRequester().get(url, buildHeader(), new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                WvpConfigInfoResponse response = JSON.parseObject(contentBody, WvpConfigInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }


}
