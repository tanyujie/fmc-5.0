package com.paradisecloud.fcm.mcu.zj.cache.api;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjConferenceContext;
import com.paradisecloud.fcm.mcu.zj.model.request.cc.*;
import com.paradisecloud.fcm.mcu.zj.model.response.cc.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class ConferenceControlApi {

    //-------------------cmdid-start---------------------------
    // -- 登录相关 --
    // 验证码响应
    public static final String CMD_ID_verify_code = "verify_code";
    // 登录请求
    public static final String CMD_ID_ctrl_mr_req = "ctrl_mr_req";
    // 登录响应
    public static final String CMD_ID_ctrl_mr_rsp = "ctrl_mr_rsp";
    // 长轮询请求（获取增量数据兼session保活）
    public static final String CMD_ID_get_changes = "get_changes";
    // 强制退出
    public static final String CMD_ID_force_logout = "force_logout";

    // -- 会议相关 --
    // 获取会议信息响应
    public static final String CMD_ID_one_mr_info = "one_mr_info";
    // 获取会议状态响应
    public static final String CMD_ID_one_mr_status = "one_mr_status";
    // 修改会议状态
    public static final String CMD_ID_mod_mr_status = "mod_mr_status";
    // 申请会议延时
    public static final String CMD_ID_mod_mr_info = "mod_mr_info";
    // 结束会议
    public static final String CMD_ID_stop_mr_req = "stop_mr_req";
    // 直播推流
    public static final String CMD_ID_mod_bypass_url = "mod_bypass_url";

    // -- 会场相关（终端相关） --
    // 在线会场列表响应
    public static final String CMD_ID_online_eps_info = "online_eps_info";
    // 会场状态列表响应
    public static final String CMD_ID_online_eps_status = "online_eps_status";
    // 设置会场状态请求（不返回数据，将在长轮询中返回）
    public static final String CMD_ID_ctrl_mr_eps = "ctrl_mr_eps";
    // 离会通知响应
    public static final String CMD_ID_left_call = "left_call";
    // 在线会场离线
    public static final String CMD_ID_online_eps_left = "online_eps_left";
    // 设置不同角色的分屏配置
    public static final String CMD_ID_config_mosic = "config_mosic";
    // 控制摄像头
    public static final String CMD_ID_ctrl_usr_fecc = "ctrl_usr_fecc";
    // 设置横幅
    public static final String CMD_ID_set_title = "set_title";

    // -- 用户相关 --
    // 获取参会者列表响应
    public static final String CMD_ID_mr_participants = "mr_participants";
    // 获取用户详细信息请求
    public static final String CMD_ID_get_user_infos = "get_user_infos";
    // 获取用户详细信息响应
    public static final String CMD_ID_user_infos = "user_infos";
    // 添加临时用户
    public static final String CMD_ID_create_temp_call = "create_temp_call";


    //-------------------cmdid-end------------------------------

    private McuZjConferenceContext conferenceContext;
    private String rootUrl = "/rest/v1/meetings/";
    private String statusUrl = "/status/participant/";

    public ConferenceControlApi(McuZjConferenceContext conferenceContext) {
        this.conferenceContext = conferenceContext;
    }

    private String buildUrlWithoutSessionId(String myPath) {
        return conferenceContext.getBaseUrl() + rootUrl + conferenceContext.getMcuZjBridge().getTenantId() + conferenceContext.getConferenceNumber() + "/sessions/" + myPath;
    }

    private String buildUrl(String myPath) {
        if (StringUtils.isNotEmpty(conferenceContext.getSessionId())) {
            return conferenceContext.getBaseUrl() + rootUrl + conferenceContext.getMcuZjBridge().getTenantId() + conferenceContext.getConferenceNumber() + "/sessions/" + conferenceContext.getSessionId() + "/" + myPath;
        } else {
            return null;
        }
    }

    private String buildUrlForStatus(String myPath) {
        return conferenceContext.getBaseUrl() + statusUrl + myPath;
    }

    private Map<String, String> buildHeader(String url) {
        Map<String, String> headers = new HashMap<>();
        String devId = conferenceContext.getBusiMcuZj().getDevId();
        String devToken = conferenceContext.getBusiMcuZj().getDevToken();
        String randTime = String.valueOf(System.currentTimeMillis() / 1000);
        String urlPath = url.replace(conferenceContext.getBaseUrl(), "");
        String seed = devId + "," + devToken + "," + randTime + "," + urlPath;
        String md5Token = encrypt2ToMD5(seed);
        headers.put("devid", devId);
        headers.put("randdtm", randTime);
        headers.put("md5token", md5Token);

        return headers;
    }

    private String encrypt2ToMD5(String str) {
        // 加密后的16进制字符串
        String hexStr = "";
        try {
            // 此 MessageDigest 类为应用程序提供信息摘要算法的功能
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 转换为MD5码
            hexStr = new String(Hex.encodeHex(messageDigest.digest(str.getBytes("utf-8"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexStr;
    }

    private StringEntity buildParams(Object params) {
        StringEntity entity = new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_JSON);
        return entity;
    }

    /**
     * 获取验证码
     */
    public CcGetVerifyCodeResponse getVerifyCode() {
        String url = conferenceContext.getBaseUrl() + rootUrl + "temp-verify-code";
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcGetVerifyCodeResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcGetVerifyCodeResponse response = JSON.parseObject(contentBody, CcGetVerifyCodeResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 登录
     */
    public CcLoginResponse login(CcLoginRequest request) {
        String url = buildUrlWithoutSessionId("");
        request.setCmdid(CMD_ID_ctrl_mr_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CcLoginResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcLoginResponse response = JSON.parseObject(contentBody, CcLoginResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 长轮询（返回增量数据兼session保活）
     */
    public String getChanges(CcGetChangesRequest request) {
        String url = buildUrl("changes");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_changes);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<String> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(contentBody);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取会议室信息
     */
    public CcMrInfoResponse getMrInfo() {
        String url = buildUrl("mr-info");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcMrInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
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
     * 获取会议状态
     * @return
     */
    public CcQueryMrStatusInfoResponse getMrStatusInfo() {
        String url = buildUrl("mr-status");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcQueryMrStatusInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcQueryMrStatusInfoResponse response = JSON.parseObject(contentBody, CcQueryMrStatusInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 修改会议状态
     * @param request
     */
    public boolean updateMrStatus(CcUpdateMrStatusRequest request) {
        String url = buildUrl("mr-status");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_mod_mr_status);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request.buildParams());
        GenericValue<Boolean> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 申请会议延时
     * @param request
     */
    public void increaseMrTime(CcIncreaseMrTimeRequest request) {
        String url = buildUrl("mr-info");
        if (StringUtils.isEmpty(url)) {
            return;
        }
        request.setCmdid(CMD_ID_mod_mr_info);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<String> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(contentBody);
            }
        });
    }

    /**
     * 结束正在进行的会议
     * @param request
     */
    public void stopMr(CcStopMrRequest request){
        String url = buildUrl("mr-status");
        if (StringUtils.isEmpty(url)) {
            return;
        }
        request.setCmdid(CMD_ID_stop_mr_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<String> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(contentBody);
            }
        });
    }

    /**
     * 修改推流地址
     * @param request
     */
    public void updateBypassUrl(CcUpdateBypassUrlRequest request){
        String url = buildUrl("mod-bypass-url");
        if (StringUtils.isEmpty(url)) {
            return;
        }
        request.setCmdid(CMD_ID_mod_bypass_url);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<String> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(contentBody);
            }
        });
    }

    /**
     * 获取在线会场信息
     */
    public CcEpsInfoResponse getEpsInfo() {
        String url = buildUrl("eps-info");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcEpsInfoResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcEpsInfoResponse response = JSON.parseObject(contentBody, CcEpsInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取会场状态列表
     *
     * @return
     */
    public CcEpsStatusResponse getEpsStatus() {
        String url = buildUrl("eps-status");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcEpsStatusResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcEpsStatusResponse response = JSON.parseObject(contentBody, CcEpsStatusResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 设置会议中会场的状态
     *
     * @param request
     */
    public boolean updateMrEpsStatus(CcUpdateMrEpsStatusRequest request) {
        String url = buildUrl("eps-status");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_ctrl_mr_eps);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 获取会议中会场的媒体信息
     *
     * @param ep_Id
     * @return
     */
    public CcQueryMrEpsMediaResponse getMrEpsMediaInfo(String ep_Id) {
        String path = "ep_Id/media_stream";
        String ep_id_path = path.replaceAll("ep_Id", ep_Id);
        String url = buildUrlForStatus(ep_id_path);
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcQueryMrEpsMediaResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcQueryMrEpsMediaResponse response = JSON.parseObject(contentBody, CcQueryMrEpsMediaResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取会议中会场丢包率信息
     *
     * @param mr_id
     * @return map key 为 uuid 或 ep_id， 即请求消息中的uuids 中的元素，str 型， value 是一个数组， 第 0 项为接收的丢包率， 第 1 项为发送的丢包率， 均为 float 型
     */
    public Map<String, Float[]> getCcMrEpsPacketsLostRate(String mr_id) {
        String url = buildUrlForStatus("packets_lost_rate/");
        Map<String, String> headers = buildHeader(url);
        Map<String, String> ep_idsMap = new HashMap<>();
        ep_idsMap.put("mr_id", mr_id);
        StringEntity entity = new StringEntity(JSON.toJSONString(ep_idsMap), Consts.UTF_8);
        GenericValue<Map<String, Float[]>> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, entity, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                Map<String, Float[]> response = JSON.parseObject(contentBody, HashMap.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取参会者列表
     *
     * @return
     */
    public CcMrParticipantsResponse getMrParticipants() {
        String url = buildUrl("participants");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcMrParticipantsResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcMrParticipantsResponse response = JSON.parseObject(contentBody, CcMrParticipantsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取用户详细信息
     *
     * @param request
     */
    public CcUserInfosResponse getUserInfos(CcUserInfosRequest request) {
        String url = buildUrl("user-infos");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_user_infos);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CcUserInfosResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcUserInfosResponse response = JSON.parseObject(contentBody, CcUserInfosResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 添加临时用户
     *
     * @param request
     */
    public boolean addMrTempUsrs(CcAddMrTempUsrsRequest request) {
        String url = buildUrl("temp-usrs");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_create_temp_call);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 获取分屏类型
     * @return
     */
    public CcQueryMrSysMsicsResponse getMrSysMosics() {
        String url = buildUrl("sys_mosics");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcQueryMrSysMsicsResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcQueryMrSysMsicsResponse response = JSON.parseObject(contentBody, CcQueryMrSysMsicsResponse.class);
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
    public boolean updateMrMosicConfig(CcUpdateMrMosicConfigRequest request) {
        String url = buildUrl("mosic-config");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_config_mosic);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 获取不同角色的分屏配置
     * @return
     */
    public CcUpdateMrMosicConfigResponse getMrMosicConfig() {
        String url = buildUrl("mosic-config");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcUpdateMrMosicConfigResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
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
     * 控制摄像头
     * @param request
     * @return
     */
    public boolean cameraControl(CcCameraControlRequest request) {
        String url = buildUrl("fecc");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_ctrl_usr_fecc);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 设置横幅
     * @param request
     * @return
     */
    public boolean setBanner(CcSetBannerRequest request) {
        String url = buildUrl("title-config");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_set_title);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 获取横幅信息
     * @return
     */
    public CcBannerResponse getBanner() {
        String url = buildUrl("title-config");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcBannerResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcBannerResponse response = JSON.parseObject(contentBody, CcBannerResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取参会者列表
     * @return
     */
    public CcMrParticipantsResponse getParticipants() {
        String url = buildUrl("participants");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CcMrParticipantsResponse> genericValue = new GenericValue<>();
        conferenceContext.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CcMrParticipantsResponse response = JSON.parseObject(contentBody, CcMrParticipantsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

}
