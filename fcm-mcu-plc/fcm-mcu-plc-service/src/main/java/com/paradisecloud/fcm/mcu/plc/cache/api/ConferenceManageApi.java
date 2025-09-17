package com.paradisecloud.fcm.mcu.plc.cache.api;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcBridge;
import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cm.*;
import com.paradisecloud.fcm.mcu.plc.model.response.cm.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 会议管理API
 */
public class ConferenceManageApi {

    private McuPlcBridge mcuPlcBridge;

    public ConferenceManageApi(McuPlcBridge mcuPlcBridge) {
        this.mcuPlcBridge = mcuPlcBridge;
    }

    private String buildUrl() {
        return mcuPlcBridge.getBaseUrl();
    }

    private Map<String, String> buildHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/xml");

        return headers;
    }

    private StringEntity buildParams(CommonRequest request) {
        if (!(request instanceof CmLoginRequest)) {
            if (StringUtils.isEmpty(mcuPlcBridge.getMcuToken())) {
                return null;
            }
        }
        request.setMcuToken(mcuPlcBridge.getMcuToken());
        request.setMcuUserToken(mcuPlcBridge.getMcuUserToken());
        String xml = request.buildToXml();
        StringEntity entity = new StringEntity(xml, ContentType.create("application/xml", Consts.UTF_8));
        return entity;
    }

    /**
     * 登录
     */
    public CmLoginResponse login(CmLoginRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmLoginResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmLoginResponse response = new CmLoginResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 长轮询（返回增量数据兼session保活）
     */
    public CmGetChangesResponse getChanges(CmGetChangesRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmGetChangesResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetChangesResponse response = new CmGetChangesResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取MCU时间
     */
    public CmGetMcuTimeResponse getMcuTime(CmGetMcuTimeRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmGetMcuTimeResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetMcuTimeResponse response = new CmGetMcuTimeResponse(contentBody);
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
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmStartMrResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmStartMrResponse response = new CmStartMrResponse(contentBody);
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
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmStopMrResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmStopMrResponse response = new CmStopMrResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询正在开着的会议id
     */
    public CmSearchMrResponse searchMr(CmSearchMrRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmSearchMrResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmSearchMrResponse response = new CmSearchMrResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询的会议CDR
     */
    public CmGetMrCdrResponse getMrCdr(CmGetMrCdrRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmGetMrCdrResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetMrCdrResponse response = new CmGetMrCdrResponse(contentBody);
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
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CmQuerySysResourceStatisticsResponse> genericValue = new GenericValue<>();
        mcuPlcBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQuerySysResourceStatisticsResponse response = new CmQuerySysResourceStatisticsResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

}
