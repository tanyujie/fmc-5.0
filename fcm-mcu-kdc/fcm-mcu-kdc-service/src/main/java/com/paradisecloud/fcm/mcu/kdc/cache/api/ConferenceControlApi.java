package com.paradisecloud.fcm.mcu.kdc.cache.api;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.kdc.cache.model.McuKdcConferenceContext;
import com.paradisecloud.fcm.mcu.kdc.model.request.CommonRequest;
import com.paradisecloud.fcm.mcu.kdc.model.request.cc.*;
import com.paradisecloud.fcm.mcu.kdc.model.response.cc.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConferenceControlApi {

    private McuKdcConferenceContext conferenceContext;
    private String rootUrl = "/api/v1/";

    public ConferenceControlApi(McuKdcConferenceContext conferenceContext) {
        this.conferenceContext = conferenceContext;
    }

    private String buildUrl(String myPath) {
        return conferenceContext.getMcuKdcBridge().getBaseUrl() + rootUrl + myPath;
    }

    private Map<String, String> buildHeaderWithCookie() {
        if (StringUtils.isEmpty(conferenceContext.getMcuKdcBridge().getCookie())) {
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "identity");
        headers.put("Cookie", conferenceContext.getMcuKdcBridge().getCookie());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("API-Level", "1");

        return headers;
    }

    private HttpEntity buildParamsWithToken(CommonRequest request) {
        if (StringUtils.isEmpty(conferenceContext.getMcuKdcBridge().getToken())) {
            return null;
        }
        List<NameValuePair> params = request.buildToList();
        params.add(new BasicNameValuePair("account_token", conferenceContext.getMcuKdcBridge().getToken()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        return entity;
    }

    /**
     * 获取会议室信息
     */
    public CcMrInfoResponse getMrInfo(CcMrInfoRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id());
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        url +=  "?account_token=" + conferenceContext.getMcuKdcBridge().getToken();
        GenericValue<CcMrInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcMrInfoResponse response = JSON.parseObject(contentBody, CcMrInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取会议中终端列表
     * @param request
     * @return
     */
    public CcGetTerminalListResponse getTerminalList(CcGetTerminalListRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mts");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        url +=  "?account_token=" + conferenceContext.getMcuKdcBridge().getToken();
        GenericValue<CcGetTerminalListResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcGetTerminalListResponse response = JSON.parseObject(contentBody, CcGetTerminalListResponse.class);
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
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mts/" + request.getMt_id());
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        url +=  "?account_token=" + conferenceContext.getMcuKdcBridge().getToken();
        GenericValue<CcGetTerminalInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcGetTerminalInfoResponse response = JSON.parseObject(contentBody, CcGetTerminalInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *  修改会议画面合成（自定义）
     * @param request
     * @return
     */
    public CcUpdateMrMosicConfigResponse updateMrMosicDiyConfig(CcUpdateMrMosicConfigRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/vmps/" + request.getVmp_id());
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcUpdateMrMosicConfigResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUpdateMrMosicConfigResponse response = JSON.parseObject(contentBody, CcUpdateMrMosicConfigResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 开启/关闭语音激励
     * @param request
     * @return
     */
    public CcVoiceMotivateResponse voiceMotivate(CcVoiceMotivateRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/vad");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcVoiceMotivateResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcVoiceMotivateResponse response = JSON.parseObject(contentBody, CcVoiceMotivateResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 终端哑音操作
     * @param request
     * @return
     */
    public CcTerminalForceMuteResponse terminalForceMute(CcTerminalForceMuteRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mts/" + request.getMt_id() + "/mute");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcTerminalForceMuteResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcTerminalForceMuteResponse response = JSON.parseObject(contentBody, CcTerminalForceMuteResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 会议哑音操作
     * @param request
     * @return
     */
    public CcMrForceMuteResponse mrForceMute(CcMrForceMuteRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mute");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcMrForceMuteResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcMrForceMuteResponse response = JSON.parseObject(contentBody, CcMrForceMuteResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *
     * 终端摄像头控制
     *
     * @param request
     * @return
     */
    public CcCameraControlResponse controlCamera(CcCameraControlRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mts/" + request.getMt_id() + "/camera");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcCameraControlResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcCameraControlResponse response = JSON.parseObject(contentBody, CcCameraControlResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     *
     * 删除终端（并挂断）
     * @param request
     * @return
     */
    public CcDeleteMrTerminalResponse deleteMrTerminal(CcDeleteMrTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mts");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcDeleteMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().delete(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcDeleteMrTerminalResponse response = JSON.parseObject(contentBody, CcDeleteMrTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 添加终端（并呼叫一次）
     * @param request
     * @return
     */
    public CcAddMrTerminalResponse addMrTerminal(CcAddMrTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mts");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcAddMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcAddMrTerminalResponse response = JSON.parseObject(contentBody, CcAddMrTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 呼叫终端（已添加的终端）
     * @param request
     * @return
     */
    public CcCallMrTerminalResponse callMrTerminal(CcCallMrTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/online_mts");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcCallMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcCallMrTerminalResponse response = JSON.parseObject(contentBody, CcCallMrTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 挂断终端
     * @param request
     * @return
     */
    public CcHangUpMrTerminalResponse hangUpMrTerminal(CcHangUpMrTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/online_mts");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcHangUpMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().delete(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcHangUpMrTerminalResponse response = JSON.parseObject(contentBody, CcHangUpMrTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 指定会议主席
     * @param request
     * @return
     */
    public CcSetChairmanMrTerminalResponse setMrChairman(CcSetChairmanMrTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/chairman");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcSetChairmanMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcSetChairmanMrTerminalResponse response = JSON.parseObject(contentBody, CcSetChairmanMrTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 指定会议发言人
     * @param request
     * @return
     */
    public CcSetSpeakerMrTerminalResponse setMrSpeaker(CcSetSpeakerMrTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/speaker");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcSetSpeakerMrTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcSetSpeakerMrTerminalResponse response = JSON.parseObject(contentBody, CcSetSpeakerMrTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 开启会议混音
     * @param request
     * @return
     */
    public CcStartMrMixingResponse startMrMixing(CcStartMrMixingRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mixs");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcStartMrMixingResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcStartMrMixingResponse response = JSON.parseObject(contentBody, CcStartMrMixingResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 关闭会议混音
     * @param request
     * @return
     */
    public CcStopMrMixingResponse stopMrMixing(CcStopMrMixingRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mixs/" + request.getMix_id());
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcStopMrMixingResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().delete(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcStopMrMixingResponse response = JSON.parseObject(contentBody, CcStopMrMixingResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 开启终端选看
     * @param request
     * @return
     */
    public CcStartTerminalChooseSeeResponse startTerminalChooseSee(CcStartTerminalChooseSeeRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/inspections");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcStartTerminalChooseSeeResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcStartTerminalChooseSeeResponse response = JSON.parseObject(contentBody, CcStartTerminalChooseSeeResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 取消终端选看
     * @param request
     * @return
     */
    public CcCancelTerminalChooseSeeResponse cancelTerminalChooseSee(CcCancelTerminalChooseSeeRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/inspections/" + request.getMt_id() + "/" + request.getMode());
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcCancelTerminalChooseSeeResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().delete(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcCancelTerminalChooseSeeResponse response = JSON.parseObject(contentBody, CcCancelTerminalChooseSeeResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 添加混音终端
     * @param request
     * @return
     */
    public CcAddMixingTerminalResponse addMixingTerminal(CcAddMixingTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mixs/" + request.getMix_id() + "/members");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcAddMixingTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcAddMixingTerminalResponse response = JSON.parseObject(contentBody, CcAddMixingTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 关闭终端混音
     * @param request
     * @return
     */
    public CcRemoveMixingTerminalResponse removeMixingTerminal(CcRemoveMixingTerminalRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/mixs/" + request.getMix_id() + "/members");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcRemoveMixingTerminalResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().delete(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcRemoveMixingTerminalResponse response = JSON.parseObject(contentBody, CcRemoveMixingTerminalResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 会议安全操作
     * @param request
     * @return
     */
    public CcSetMrSecureResponse setMrSecure(CcSetMrSecureRequest request) {
        String url = buildUrl("vc/confs/" + request.getConf_id() + "/safty");
        Map<String, String> headers = buildHeaderWithCookie();
        if (headers == null) {
            return null;
        }
        HttpEntity params = buildParamsWithToken(request);
        GenericValue<CcSetMrSecureResponse> genericValue = new GenericValue<>();
        conferenceContext.getMcuKdcBridge().getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcSetMrSecureResponse response = JSON.parseObject(contentBody, CcSetMrSecureResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

}
