package com.paradisecloud.fcm.wvp.gb28181;

import com.sinhy.http.HttpObjectCreator;
import com.sinhy.http.HttpRequester;

public class WvpBridge {
    private String ip;
    private int port;
    private HttpRequester httpRequester;
    private String baseUrl;
    private String accessToken;
    private volatile long lastUpdateTime = 0;

    private WvpControllApi wvpControllApi;

    public WvpBridge(String ip,int port) {
      this.ip=ip;
      this.port=port;
        init();
    }

    private void init() {

        baseUrl = "http://" +ip + ":" + port;
        httpRequester = HttpObjectCreator.getInstance().createHttpRequester();
        this.wvpControllApi=new WvpControllApi(this);

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpRequester getHttpRequester() {
        return httpRequester;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public WvpControllApi getWvpControllApi() {
        return wvpControllApi;
    }
}
