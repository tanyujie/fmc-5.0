package com.paradisecloud.fcm.mcu.plc.cache.api;

import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.plc.cache.model.McuPlcConferenceContext;
import com.paradisecloud.fcm.mcu.plc.model.request.CommonRequest;
import com.paradisecloud.fcm.mcu.plc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.plc.model.request.cm.CmLoginRequest;
import com.paradisecloud.fcm.mcu.plc.model.response.cc.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConferenceControlApi {

    private McuPlcConferenceContext conferenceContext;

    public ConferenceControlApi(McuPlcConferenceContext conferenceContext) {
        this.conferenceContext = conferenceContext;
    }

    private String buildUrl() {
        return conferenceContext.getMcuPlcBridge().getBaseUrl();
    }

    private Map<String, String> buildHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/xml");

        return headers;
    }

    private StringEntity buildParams(CommonRequest request) {
        if (!(request instanceof CmLoginRequest)) {
            if (StringUtils.isEmpty(conferenceContext.getMcuPlcBridge().getMcuToken())) {
                return null;
            }
        }
        request.setMcuToken(conferenceContext.getMcuPlcBridge().getMcuToken());
        request.setMcuUserToken(conferenceContext.getMcuPlcBridge().getMcuUserToken());
        String xml = request.buildToXml();
        StringEntity entity = new StringEntity(xml, ContentType.create("application/xml", Consts.UTF_8));
        return entity;
    }

    /**
     * 获取会议室信息
     */
    public CcMrInfoResponse getMrInfo(CcMrInfoRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcMrInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcMrInfoResponse response = new CcMrInfoResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取会议中终端的详情
     * @param request
     * @return
     */
    public CcGetTerminalInfoResponse getCcTerminalInfo(CcGetTerminalInfoRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcGetTerminalInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcGetTerminalInfoResponse response = new CcGetTerminalInfoResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *  设置会议的分屏配置
     * @param request
     * @return
     */
    public CcUpdateMrMosicConfigResponse updateMrMosicConfig(CcUpdateMrMosicConfigRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcUpdateMrMosicConfigResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdateMrMosicConfigResponse response = new CcUpdateMrMosicConfigResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *  设置不同角色的分屏配置
     * @param request
     * @return
     */
    public CcUpdatePersonalMosicConfigResponse updatePersonalMosicConfig(CcUpdatePersonalMosicConfigRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcUpdatePersonalMosicConfigResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdatePersonalMosicConfigResponse response = new CcUpdatePersonalMosicConfigResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 设置终端分屏类型
     * @param request
     * @return
     */
    public CcUpdatePersonalMosicTypeResponse updatePersonalMosicType(CcUpdatePersonalMosicTypeRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcUpdatePersonalMosicTypeResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdatePersonalMosicTypeResponse response = new CcUpdatePersonalMosicTypeResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *
     * 设置会为自动布局
     *
     * @param request
     * @return
     */
    public CcUpdateMrAutoMosicConfigResponse updateMrAutoMosicConfig(CcUpdateMrAutoMosicConfigRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcUpdateMrAutoMosicConfigResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdateMrAutoMosicConfigResponse response = new CcUpdateMrAutoMosicConfigResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 设置会议的音频状态
     * @param request
     * @return
     */
    public CcUpdateMrAudioResponse updateMrAudio(CcUpdateMrAudioRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcUpdateMrAudioResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdateMrAudioResponse response = new CcUpdateMrAudioResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 设置会议中终端的音频视频状态
     * @param request
     * @return
     */
    public CcUpdateTerminalAudioAndVideoResponse updateTerminalAudioAndVideo(CcUpdateTerminalAudioAndVideoRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcUpdateTerminalAudioAndVideoResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdateTerminalAudioAndVideoResponse response = new CcUpdateTerminalAudioAndVideoResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }


    /**
     * 设置横幅
     * @param request
     * @return
     */
    public CcSetBannerResponse setBanner(CcSetBannerRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcSetBannerResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcSetBannerResponse response = new CcSetBannerResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 断开会议中终端的连接
     * @return
     */
    public CcSetConnectMrTerminalResponse setConnectMrTerminal(CcSetConnectMrTerminalRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcSetConnectMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcSetConnectMrTerminalResponse response = new CcSetConnectMrTerminalResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *
     * 删除会议中的终端
     * @param request
     * @return
     */
    public CcDeleteMrTerminalResponse deleteMrTerminal(CcDeleteMrTerminalRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcDeleteMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcDeleteMrTerminalResponse response = new CcDeleteMrTerminalResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 现在终端
     * @param request
     * @return
     */
    public CcAddMrTerminalResponse addMrTerminal(CcAddMrTerminalRequest request) {
        String url = buildUrl();
        Map<String, String> headers = buildHeader();
        StringEntity params = buildParams(request);
        if (params == null) {
            return null;
        }
        GenericValue<CcAddMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuPlcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcAddMrTerminalResponse response = new CcAddMrTerminalResponse(contentBody);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

}
