package com.paradisecloud.fcm.mcu.kdc.cache.api;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcBridge;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cm.*;
import com.paradisecloud.fcm.mcu.kdc.model.response.CommonResponse;
import com.paradisecloud.fcm.mcu.kdc.model.response.cm.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会议管理API
 */
public class ConferenceManageApi {

    private McuKdcBridge mcuKdcBridge;
    private String rootUrl = "/api/v1/";

    public ConferenceManageApi(McuKdcBridge mcuKdcBridge) {
        this.mcuKdcBridge = mcuKdcBridge;
    }

    private String buildUrl(String myPath) {
        return mcuKdcBridge.getBaseUrl() + rootUrl + myPath;
    }

    private Map<String, String> buildHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "identity");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("API-Level", "1");

        return headers;
    }

    private Map<String, String> buildHeaderWithCookie() {
        if (StringUtils.isEmpty(mcuKdcBridge.getCookie())) {
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "identity");
        headers.put("Cookie", mcuKdcBridge.getCookie());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("API-Level", "1");

        return headers;
    }

    private HttpEntity buildParams(CommonRequest request) {
        List<NameValuePair> params = request.buildToList();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        return entity;
    }

    private HttpEntity buildParamsWithToken(CommonRequest request) {
        if (StringUtils.isEmpty(mcuKdcBridge.getToken())) {
            return null;
        }
        List<NameValuePair> params = request.buildToList();
        params.add(new BasicNameValuePair("account_token", mcuKdcBridge.getToken()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        return entity;
    }

    /**
     * 获取token
     */
    public CmTokenResponse token(CmTokenRequest request) {
        String url = buildUrl("system/token");
        Map<String, String> headers = buildHeader();
        HttpEntity params = buildParams(request);
        GenericValue<CmTokenResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmTokenResponse response = JSON.parseObject(contentBody, CmTokenResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * token心跳
     */
    public CmHeartbeatTokenResponse heartbeatToken(CmHeartbeatTokenRequest request) {
        String url = buildUrl("system/token/" + mcuKdcBridge.getToken() + "/heartbeat");
        Map<String, String> headers = buildHeader();
        HttpEntity params = buildParams(request);
        GenericValue<CmHeartbeatTokenResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmHeartbeatTokenResponse response = JSON.parseObject(contentBody, CmHeartbeatTokenResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 登录
     */
    public CmLoginResponse login(CmLoginRequest request) {
        String url = buildUrl("system/login");
        Map<String, String> headers = buildHeader();
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CmLoginResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmLoginResponse response = JSON.parseObject(contentBody, CmLoginResponse.class);
                if (response != null && response.isSuccess()) {
                    Header header = httpResponse.getFirstHeader("Set-Cookie");
                    response.setCookie(header.getValue());
                }
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 心跳
     */
    public CmHeartbeatResponse heartbeat(CmHeartbeatRequest request) {
        String url = buildUrl("system/heartbeat");
        Map<String, String> headers = buildHeader();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CmHeartbeatResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmHeartbeatResponse response = JSON.parseObject(contentBody, CmHeartbeatResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 开启会议
     * @param request
     * @return
     */
    public CmStartMrResponse startMr(CmStartMrRequest request) {
        String url = buildUrl("mc/confs");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CmStartMrResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmStartMrResponse response = JSON.parseObject(contentBody, CmStartMrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 停止会议
     * @param request
     * @return
     */
    public CmStopMrResponse stopMr(CmStopMrRequest request) {
        String url = buildUrl("mc/confs/" + request.getConf_id());
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CmStopMrResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().delete(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmStopMrResponse response = JSON.parseObject(contentBody, CmStopMrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询正在开着的会议id
     */
    public CmSearchMrResponse searchMr(CmSearchMrRequest request) {
        String url = buildUrl("vc/confs");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        url +=  "?account_token=" + mcuKdcBridge.getToken();
        GenericValue<CmSearchMrResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmSearchMrResponse response = JSON.parseObject(contentBody, CmSearchMrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取系统所有资源数
     * @param request
     * @return
     */
    public CmQuerySysResourceStatisticsResponse querySysResourceStatistics(CmQuerySysResourceStatisticsRequest request) {
        String url = buildUrl("mc/resources");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        url +=  "?account_token=" + mcuKdcBridge.getToken();
        GenericValue<CmQuerySysResourceStatisticsResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQuerySysResourceStatisticsResponse response = JSON.parseObject(contentBody, CmQuerySysResourceStatisticsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取媒体资源数
     * @param request
     * @return
     */
    public CmGetMeetingResourceResponse getMeetingResource(CmGetMeetingResourceRequest request) {
        String url = mcuKdcBridge.getBaseUrl() + "/nms/statistic/meetingresource";
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        url +=  "?parentMoid=all";
        GenericValue<CmGetMeetingResourceResponse> genericValue = new GenericValue<>();
        mcuKdcBridge.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetMeetingResourceResponse response = JSON.parseObject(contentBody, CmGetMeetingResourceResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

}
