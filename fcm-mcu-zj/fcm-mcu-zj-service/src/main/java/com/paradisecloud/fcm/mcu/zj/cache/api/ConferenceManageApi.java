package com.paradisecloud.fcm.mcu.zj.cache.api;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.mcu.zj.cache.model.McuZjBridge;
import com.paradisecloud.fcm.mcu.zj.model.request.CommonRequest;
import com.paradisecloud.fcm.mcu.zj.model.request.cm.*;
import com.paradisecloud.fcm.mcu.zj.model.response.cm.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * 会议管理API
 */
public class ConferenceManageApi {

    //-------------------cmdid-start---------------------------
    // -- 登录相关 --
    // 验证码响应
    public static final String CMD_ID_verify_code = "verify_code";
    // 登录请求
    public static final String CMD_ID_login_req = "login_req";
    // 登录响应
    public static final String CMD_ID_login_rsp = "login_rsp";
    // 登出请求
    public static final String CMD_ID_logout_req = "logout_req";
    // 长轮询请求（获取增量数据兼session保活）
    public static final String CMD_ID_get_changes = "get_changes";

    // 部门相关
    // 筛选部门请求
    public static final String CMD_ID_search_department_ids = "search_department_ids";

    // -- 会议室管理 --
    // 筛选会议室请求
    public static final String CMD_ID_search_room = "search_room";
    // 创建会议室请求
    public static final String CMD_ID_add_room = "add_room";
    // 修改会议室
    public static final String CMD_ID_mod_room = "mod_room";
    // 创建会议室响应
    public static final String CMD_ID_add_room_rsp = "add_room_rsp";
    // 删除会议室
    public static final String CMD_ID_delete_room = "delete_room";

    // -- 会议管理 --
    // 开启会议请求
    public static final String CMD_ID_start_mr_req = "start_mr_req";
    // 添加预约会议请求
    public static final String CMD_ID_add_schedule_req = "add_schedule";
    // 删除预约会议
    public static final String CMD_ID_delete_schedule_req = "delete_schedule";
    // 开启会议响应
    public static final String CMD_ID_started_mr = "started_mr";
    // 正在开着会议id响应
    public static final String CMD_ID_mr_ids = "mr_ids";
    // 正在开着会议信请求
    public static final String CMD_ID_query_mr = "query_mr";
    // 正在开着会议信息响应
    public static final String CMD_ID_mr_info = "mr_info";
    // 停止会议请求
    public static final String CMD_ID_stop_mr_req = "stop_mr_req";
    // 停止会议响应
    public static final String CMD_ID_stopped_mr = "stopped_mr";

    // --会议模板管理 --
    // 筛选会议模板
    public static final String CMD_ID_search_mr_template = "search_mr_template";
    // 订阅会议模板
    public static final String CMD_ID_subscribe_mr_templates = "subscribe_mr_templates";
    // 创建会议模板
    public static final String CMD_ID_add_mr_template = "add_mr_template";
    // 修改会议模板
    public static final String CMD_ID_mod_mr_template = "mod_mr_template";
    // 删除会议模板
    public static final String CMD_ID_delete_mr_template = "delete_mr_template";

    // --用户、终端管理 --
    // 筛选用户、终端请求
    public static final String CMD_ID_search_usr = "search_usr";
    // 筛选用户、终端响应
    public static final String CMD_ID_usr_ids = "usr_ids";
    // 订阅用户、终端信息请求
    public static final String CMD_ID_subscribe_usr = "subscribe_usr";
    // 订阅用户、终端信息响应
    public static final String CMD_ID_usr_info = "usr_info";
    // 添加用户、终端请求
    public static final String CMD_ID_add_usr = "add_usr";
    // 添加用户、终端响应
    public static final String CMD_ID_add_usr_rsp = "add_usr_rsp";
    // 修改用户、终端请求
    public static final String CMD_ID_mod_usr = "mod_usr";
    // 删除用户、终端请求
    public static final String CMD_ID_delete_usr = "delete_usr";
    // 删除用户、终端响应
    public static final String CMD_ID_usr_delete_rsp = "usr_delete_rsp";
    // 查询用户、终端在线状态请求
    public static final String CMD_ID_get_usr_online_status = "get_usr_online_status";

    // -- 其它 --
    // 登录者（当前登录账号）的个人信息
    public static final String CMD_ID_self_usr_info = "self_usr_info";
    // 系统支持的分屏类型（用户画面布局使用）
    public static final String CMD_ID_sys_mosics = "sys_mosics";
    // 登录者（当前登录账号）所在租户的信息
    public static final String CMD_ID_global_info = "global_info";
    // 会议信息修改
    public static final String CMD_ID_moded_mr = "moded_mr";
    // 与会者入会
    public static final String CMD_ID_ep_added = "ep_added";
    // 与会者离会
    public static final String CMD_ID_ep_deled = "ep_deled";

    // --资源模板--
    // 查询资源模板
    public static final String CMD_ID_search_resource_tmpl = "search_resource_tmpl";
    // 订阅资源模板
    public static final String CMD_ID_subscribe_resource_tmpl = "subscribe_resource_tmpl";
    // 创建资源模板
    public static final String CMD_ID_add_resource_tmpl = "add_resource_tmpl";
    // 删除资源模板
    public static final String CMD_ID_delete_resource_tmpl = "delete_resource_tmpl";

    // --资源模板 分辨率_带宽--
    // 360P30@1M
    public static final String RESOURCE_RES_BW_360p30_1M = "360P30@1M";
    // 720P30@1M
    public static final String RESOURCE_RES_BW_720P30_1M = "720P30@1M";
    // 720P60@2M
    public static final String RESOURCE_RES_BW_720P60_2M = "720P60@2M";
    // 1080P30@2M
    public static final String RESOURCE_RES_BW_1080P30_2M = "1080P30@2M";
    // 2kP30@2M
    public static final String RESOURCE_RES_BW_2KP30_2M = "2KP30@2M";
    // 1080P30@4M
    public static final String RESOURCE_RES_BW_1080P30_4M = "1080P30@4M";
    // 1080P60@4M
    public static final String RESOURCE_RES_BW_1080P60_4M = "1080P60@4M";
    // 2KP60@4M
    public static final String RESOURCE_RES_BW_2KP60_4M = "2KP60@4M";
    // 4KP60@4M
    public static final String RESOURCE_RES_BW_4KP30_4M = "4kP30@4M";
    // 1080P60@6M
    public static final String RESOURCE_RES_BW_1080P60_6M = "1080P60@6M";
    // 4KP60@6M
    public static final String RESOURCE_RES_BW_4KP30_6M = "4kP30@6M";
    // 1080P60@8M
    public static final String RESOURCE_RES_BW_1080P60_8M = "1080P60@8M";
    // 4KP60@8M
    public static final String RESOURCE_RES_BW_4KP30_8M = "4kP30@8M";

    // --录制管理--
    //  筛选录播
    public static final String CMD_ID_search_vod = "search_vod";
    //  订阅录播
    public static final String CMD_ID_subscribe_vod = "subscribe_vod";
    // 删除录制文件
    public static final String CMD_ID_del_or_restore_vod = "del_or_restore_vod";
    // 获取分享录播或观看录播链接
    public static final String CMD_ID_get_share_or_watch_vod_info = "get_share_or_watch_vod_info";
    // 获取系统所有资源数
    public static final String CMD_ID_get_many_show_infos = "get_many_show_infos";
    // 获取系统资源占用数
    public static final String CMD_ID_get_system_resource_statistics_rsp = "get_system_resource_statistics_rsp";
    // 获取资源模板预计占用资源数
    public static final String CMD_ID_get_resource_evaluation = "get_resource_evaluation";

    // --会议统计--
    // 获取一段时间内的历史会议列表
    public static final String CMD_ID_get_room_history_req = "get_room_history_req";

    //-------------------cmdid-end------------------------------

    private McuZjBridge mcuZjBridge;
    private String rootUrl = "/rest/v1/app1/manager/sessions/";
    private String rootUserUrl = "/rest/v1/app1/user/sessions/";

    public ConferenceManageApi(McuZjBridge mcuZjBridge) {
        this.mcuZjBridge = mcuZjBridge;
    }

    private String buildUrlWithoutSessionId(String myPath) {
        return mcuZjBridge.getBaseUrl() + rootUrl + myPath;
    }

    private String buildUrl(String myPath) {
        if (StringUtils.isNotEmpty(mcuZjBridge.getSessionId())) {
            return mcuZjBridge.getBaseUrl() + rootUrl + mcuZjBridge.getSessionId() + "/" + myPath;
        } else {
            return null;
        }
    }

    private String buildUserUrl(String myPath) {
        if (StringUtils.isNotEmpty(mcuZjBridge.getSessionId())) {
            return mcuZjBridge.getBaseUrl() + rootUserUrl + mcuZjBridge.getSessionId() + "/" + myPath;
        } else {
            return null;
        }
    }

    private Map<String, String> buildHeader(String url) {
        Map<String, String> headers = new HashMap<>();
        String devId = mcuZjBridge.getBusiMcuZj().getDevId();
        String devToken = mcuZjBridge.getBusiMcuZj().getDevToken();
        String randTime = String.valueOf(System.currentTimeMillis());
        String urlPath = url.replace(mcuZjBridge.getBaseUrl(), "");
        String seed = devId + "," + devToken + "," + randTime + "," + urlPath;
        String md5Token = encrypt2ToMD5(seed);
        headers.put("devid", devId);
        headers.put("randdtm", randTime);
        headers.put("md5token", md5Token);
        headers.put("sessionid", mcuZjBridge.getSessionId());

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
    public CmGetVerifyCodeResponse getVerifyCode() {
        String url = buildUrlWithoutSessionId("verify-code/");
        Map<String, String> headers = buildHeader(url);
        GenericValue<CmGetVerifyCodeResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetVerifyCodeResponse response = JSON.parseObject(contentBody, CmGetVerifyCodeResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 登录
     */
    public CmLoginResponse login(CmLoginRequest request) {
        String url = buildUrlWithoutSessionId("login_req/");
        request.setCmdid(CMD_ID_login_req);
        request.setLogin_pwd(encrypt2ToMD5(request.getLogin_pwd()));
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmLoginResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmLoginResponse response = JSON.parseObject(contentBody, CmLoginResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 登出
     */
    public boolean logout(CmLogoutRequest request) {
        String url = buildUrl("logout_req/");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_logout_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 长轮询（返回增量数据兼session保活）
     */
    public String getChanges(CmGetChangesRequest request) {
        String url = buildUrl("data/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_changes);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<String> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                genericValue.setValue(contentBody);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 筛选部门
     */
    public CmSearchDepartmentsResponse searchDepartments(CmSearchDepartmentsRequest request) {
        String url = buildUrl("departments/department_ids/search/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_search_department_ids);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmSearchDepartmentsResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmSearchDepartmentsResponse response = JSON.parseObject(contentBody, CmSearchDepartmentsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 筛选会议室
     */
    public CmSearchRoomsResponse searchRooms(CmSearchRoomsRequest request) {
        String url = buildUrl("rooms/room_ids/search/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_search_room);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmSearchRoomsResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmSearchRoomsResponse response = JSON.parseObject(contentBody, CmSearchRoomsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 创建会议室
     */
    public CmAddRoomResponse addRoom(CmAddRoomRequest request) {
        String url = buildUrl("rooms/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_add_room);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmAddRoomResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmAddRoomResponse response = JSON.parseObject(contentBody, CmAddRoomResponse.class);
                genericValue.setValue(response);
            }

        });
        return genericValue.getValue();
    }

    /**
     * 修改会议室
     * @param request
     * @return
     */
    public CmModRoomResponse modRoom(CmModRoomRequest request) {
        String url = buildUrl("rooms/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_mod_room);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmModRoomResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmModRoomResponse response = JSON.parseObject(contentBody, CmModRoomResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除会议室
     * @param request
     * @return
     */
    public CmDeleteRoomResponse deleteRoom(CmDeleteRoomRequest request){
        String url = buildUrl("rooms/trash-can/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_delete_room);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmDeleteRoomResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmDeleteRoomResponse response = JSON.parseObject(contentBody, CmDeleteRoomResponse.class);
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
    public boolean startMr(CmStartMrRequest request) {
        String url = buildUrl("mr/start-mr/");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_start_mr_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 添加预约会议
     * @param request
     * @return
     */
    public CmAddScheduleResponse addSchedules(CmAddScheduleRequest request) {
        String url = buildUrl("schedules/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_add_schedule_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmAddScheduleResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {String contentBody = getBodyContent(httpResponse);
                CmAddScheduleResponse response = JSON.parseObject(contentBody, CmAddScheduleResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除预约会议
     * @param request
     * @return
     */
    public CmDelScheduleResponse delSchedules(CmDelScheduleRequest request) {
        String url = buildUrl("schedules/trash-can/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_delete_schedule_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmDelScheduleResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {String contentBody = getBodyContent(httpResponse);
                CmDelScheduleResponse response = JSON.parseObject(contentBody, CmDelScheduleResponse.class);
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
    public boolean stopMr(CmStopMrRequest request) {
        String url = buildUrl("mr/stop-mr/");
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        request.setCmdid(CMD_ID_stop_mr_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<Boolean> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                genericValue.setValue(true);
            }
        });
        if (genericValue.getValue() != null) {
            return genericValue.getValue();
        }
        return false;
    }

    /**
     * 查询正在开着的会议id
     */
    public CmSearchMrResponse searchMr() {
        String url = buildUrl("mr/search-mr/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CmSearchMrResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
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
     * 查询会议信息
     */
    public CmQueryMrResponse queryMr(CmQueryMrRequest request) {
        String url = buildUrl("mr/query-mr/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_query_mr);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryMrResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryMrResponse response = JSON.parseObject(contentBody, CmQueryMrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 筛选会议模板
     * @param request
     * @return
     */
    public CmQueryMrTemplateResponse queryMrTemplate(CmQueryMrRequest request) {
        String url = buildUrl("mr_templates/mr_template_ids/search/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_search_mr_template);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryMrTemplateResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryMrTemplateResponse response = JSON.parseObject(contentBody, CmQueryMrTemplateResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 订阅会议模板
     * @param request
     * @return
     */
    public CmQueryMrTemplateInfoResponse queryMrTemplateInfo(CmQueryMrTemplateInfoRequest request) {
        String url = buildUrl("mr_templates/mr_template_ids/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_subscribe_mr_templates);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryMrTemplateInfoResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryMrTemplateInfoResponse response = JSON.parseObject(contentBody, CmQueryMrTemplateInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 创建会议模板
     * @param request
     * @return
     */
    public CmAddMrTemplatesResponse addMrTemplates(CmAddMrTemplatesRequest request) {
        String url = buildUrl("mr_templates/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_add_mr_template);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmAddMrTemplatesResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmAddMrTemplatesResponse response = JSON.parseObject(contentBody, CmAddMrTemplatesResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 更改会议室
     * @param request
     * @return
     */
    public CmUpdateTemplatesResponse updateTemplates(CmUpdateTemlatesRequest request) {
        String url = buildUrl("mr_templates/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_mod_mr_template);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmUpdateTemplatesResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmUpdateTemplatesResponse response = JSON.parseObject(contentBody, CmUpdateTemplatesResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除会议模板
     * @param request
     * @return
     */
    public CmDeleteTemplateResponse deleteTemplate(CmDeleteTemplateRequest request) {
        String url = buildUrl("mr_templates/trash-can/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_delete_mr_template);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmDeleteTemplateResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmDeleteTemplateResponse response = JSON.parseObject(contentBody, CmDeleteTemplateResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询用户终端
     * @param request
     * @return
     */
    public CmSearchUsrResponse searchUsr(CmSearchUsrRequest request) {
        String url = buildUrl("usrs/usr_id/search/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_search_usr);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmSearchUsrResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmSearchUsrResponse response = JSON.parseObject(contentBody, CmSearchUsrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询用户终端信息
     * @param request
     * @return
     */
    public CmGetUsrInfoResponse getUsrInfo(CmGetUsrInfoRequest request) {
        String url = buildUrl("usrs/usr_ids/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_subscribe_usr);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmGetUsrInfoResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetUsrInfoResponse response = JSON.parseObject(contentBody, CmGetUsrInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 添加用户终端
     * @param request
     * @return
     */
    public CmAddUsrResponse addUsr(CmAddUsrRequest request) {
        String url = buildUrl("usrs/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_add_usr);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmAddUsrResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmAddUsrResponse response = JSON.parseObject(contentBody, CmAddUsrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 修改用户终端
     * @param request
     * @return
     */
    public CmModUsrResponse modifyUsr(CmModUsrRequest request) {
        String url = buildUrl("usrs/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_mod_usr);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmModUsrResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().put(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmModUsrResponse response = JSON.parseObject(contentBody, CmModUsrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除用户终端
     * @param request
     * @return
     */
    public CmDeleteUsrResponse deleteUsr(CmDeleteUsrRequest request) {
        String url = buildUrl("usrs/trash-can/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_delete_usr);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmDeleteUsrResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmDeleteUsrResponse response = JSON.parseObject(contentBody, CmDeleteUsrResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询用户终端在线状态
     * @param request
     * @return
     */
    public CmGetUsrOnlineStatusResponse getUsrOnlineStatus(CmGetUsrOnlineStatusRequest request) {
        String url = buildUserUrl("usrs/get_usr_online_status/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_usr_online_status);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmGetUsrOnlineStatusResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetUsrOnlineStatusResponse response = JSON.parseObject(contentBody, CmGetUsrOnlineStatusResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询资源模板id
     * @return
     */
    public CmQueryResourceTmplResponse queryResourceTmpl() {
        String url = buildUrl("resource_tmpls/resource_tmpl_id/search/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setCmdid(CMD_ID_search_resource_tmpl);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(commonRequest);
        GenericValue<CmQueryResourceTmplResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryResourceTmplResponse response = JSON.parseObject(contentBody, CmQueryResourceTmplResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 订阅资源模板
     * @param request
     * @return
     */
    public CmQueryResourceTmplInfoResponse queryResourceTmplInfo(CmQueryResourceTmplInfoRequest request) {
        String url = buildUrl("resource_tmpls/resource_tmpl_ids/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_subscribe_resource_tmpl);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryResourceTmplInfoResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryResourceTmplInfoResponse response = JSON.parseObject(contentBody, CmQueryResourceTmplInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 创建资源模板
     * @param request
     * @return
     */
    public CmAddResourceTmplResponse addResourceTmpl(CmAddResourceRequest request) {
        String url = buildUrl("resource_tmpls/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_add_resource_tmpl);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmAddResourceTmplResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmAddResourceTmplResponse response = JSON.parseObject(contentBody, CmAddResourceTmplResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除资源模板
     * @param request
     * @return
     */
    public CmDeleteResourceTmplResponse deleteResourceTmpl(CmDeleteResourceRequest request) {
        String url = buildUrl("resource_tmpls/trash-can/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_delete_resource_tmpl);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmDeleteResourceTmplResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmDeleteResourceTmplResponse response = JSON.parseObject(contentBody, CmDeleteResourceTmplResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 筛选录播
     * @param request
     * @return
     */
    public CmQueryRecordsResponse queryRecords(CmQueryRecordsRequest request) {
        String url = buildUrl("vods/vod_uuids/search/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_search_vod);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryRecordsResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryRecordsResponse response = JSON.parseObject(contentBody, CmQueryRecordsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 订阅录播
     * @param request
     * @return
     */
    public CmQueryRecordsInfoResponse queryRecordsInfo(CmQueryRecordsInfoRequest request) {
        String url = buildUrl("vods/vod_uuids/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_subscribe_vod);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryRecordsInfoResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryRecordsInfoResponse response = JSON.parseObject(contentBody, CmQueryRecordsInfoResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 删除录制文件
     * @param request
     * @return
     */
    public CmDeleteRecordsFilesResponse deleteRecordsFiles(CmDeleteRecordsFilesRequest request) {
        String url = buildUrl("vods/del_or_restore_vods/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_del_or_restore_vod);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmDeleteRecordsFilesResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmDeleteRecordsFilesResponse response = JSON.parseObject(contentBody, CmDeleteRecordsFilesResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取分享录播或观看录播链接
     * @param request
     * @return
     */
    public CmQueryRecordsUrlResponse queryRecordsUrl(CmQueryRecordsUrlRequest request) {
        String url = buildUrl("vods/share_or_watch_vod/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_share_or_watch_vod_info);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryRecordsUrlResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryRecordsUrlResponse response = JSON.parseObject(contentBody, CmQueryRecordsUrlResponse.class);
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
    public CmQuerySysAllResourceStatisticsResponse querySysAllResourceStatistics(CmQuerySysAllResourceStatisticsRequest request) {
        String url = buildUrl("get_many_show_infos/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_many_show_infos);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQuerySysAllResourceStatisticsResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQuerySysAllResourceStatisticsResponse response = JSON.parseObject(contentBody, CmQuerySysAllResourceStatisticsResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取系统资源数
     * @return
     */
    public CmQuerySysResourceStatisticsResponse querySysResourceStatistics() {
        String url = buildUrl("get_system_resource_statistics/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Map<String, String> headers = buildHeader(url);
        GenericValue<CmQuerySysResourceStatisticsResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().get(url, headers, new HttpResponseProcessorAdapter() {
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
     * 获取资源模板预计占用资源数
     * @param request
     * @return
     */
    public CmQueryResourceEvaluationResponse queryResourceEvaluation(CmQueryResourceEvaluationRequest request) {
        String url = buildUrl("common/get_resource_evaluation/");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_resource_evaluation);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmQueryResourceEvaluationResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmQueryResourceEvaluationResponse response = JSON.parseObject(contentBody, CmQueryResourceEvaluationResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 获取一段时间内的历史会议列表
     * @param request
     * @return
     */
    public CmGetRoomHistoryListResponse getRoomHistoryList(CmGetRoomHistoryListRequest request) {
        String url = mcuZjBridge.getBaseUrl() + "/rest/v1/cloud/statistics";
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        request.setCmdid(CMD_ID_get_room_history_req);
        Map<String, String> headers = buildHeader(url);
        StringEntity params = buildParams(request);
        GenericValue<CmGetRoomHistoryListResponse> genericValue = new GenericValue<>();
        mcuZjBridge.getHttpRequester().post(url, headers, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CmGetRoomHistoryListResponse response = JSON.parseObject(contentBody, CmGetRoomHistoryListResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }
}
