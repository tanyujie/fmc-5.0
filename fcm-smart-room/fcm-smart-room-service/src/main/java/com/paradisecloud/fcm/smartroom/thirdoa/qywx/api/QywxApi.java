package com.paradisecloud.fcm.smartroom.thirdoa.qywx.api;

import com.alibaba.fastjson.JSON;
import com.paradisecloud.common.utils.StringUtils;
import com.paradisecloud.fcm.smartroom.thirdoa.qywx.cache.QywxCache;
import com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request.*;
import com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.response.*;
import com.sinhy.http.HttpResponseProcessorAdapter;
import com.sinhy.model.GenericValue;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

public class QywxApi {

    private String rootUrl = "https://qyapi.weixin.qq.com/cgi-bin/";
    private String corpId;
    private String corpSecret;

    public QywxApi(String corpId, String corpSecret) {
        this.corpId = corpId;
        this.corpSecret = corpSecret;
    }

    private String buildUrlGetToken(String myPath) {
        if (StringUtils.isNotEmpty(QywxCache.getInstance().getCorpId()) && StringUtils.isNotEmpty(QywxCache.getInstance().getCorpSecret())) {
            return rootUrl + myPath + "?corpid=" + QywxCache.getInstance().getCorpId() + "&corpsecret" + QywxCache.getInstance().getCorpSecret();
        } else {
            return null;
        }
    }

    private String buildUrl(String myPath) {
        if (StringUtils.isNotEmpty(QywxCache.getInstance().getAccessToken())) {
            return rootUrl + myPath + "?access_token=" + QywxCache.getInstance().getAccessToken();
        } else {
            return null;
        }
    }

    private StringEntity buildParams(Object params) {
        StringEntity entity = new StringEntity(JSON.toJSONString(params), ContentType.APPLICATION_JSON);
        return entity;
    }

    /**
     * 获取access_token
     */
    public GetTokenResponse getToken() {
        String url = buildUrlGetToken("gettoken");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        GenericValue<GetTokenResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().get(url, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                GetTokenResponse response = JSON.parseObject(contentBody, GetTokenResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 添加会议室
     */
    public AddMeetingRoomResponse addMeetingRoom(AddMeetingRoomRequest request) {
        String url = buildUrl("oa/meetingroom/add");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<AddMeetingRoomResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                AddMeetingRoomResponse response = JSON.parseObject(contentBody, AddMeetingRoomResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询会议室
     * @param request
     * @return
     */
    public QueryMeetingRoomResponse queryMeetingRoom(QueryMeetingRoomRequest request) {
        String url = buildUrl("oa/meetingroom/list");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<QueryMeetingRoomResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                QueryMeetingRoomResponse response = JSON.parseObject(contentBody, QueryMeetingRoomResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 编辑会议室
     * @param request
     * @return
     */
    public EditMeetingRoomResponse editMeetingRoom(EditMeetingRoomRequest request) {
        String url = buildUrl("oa/meetingroom/edit");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<EditMeetingRoomResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                EditMeetingRoomResponse response = JSON.parseObject(contentBody, EditMeetingRoomResponse.class);
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
    public DelMeetingRoomResponse delMeetingRoom(DelMeetingRoomRequest request) {
        String url = buildUrl("oa/meetingroom/del");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<DelMeetingRoomResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                DelMeetingRoomResponse response = JSON.parseObject(contentBody, DelMeetingRoomResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 查询会议室的预定信息
     * @param request
     * @return
     */
    public GetMeetingRoomBookResponse getMeetingRoomBook(GetMeetingRoomBookRequest request) {
        String url = buildUrl("oa/meetingroom/get_booking_info");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<GetMeetingRoomBookResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                GetMeetingRoomBookResponse response = JSON.parseObject(contentBody, GetMeetingRoomBookResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 新增预定会议室
     * @param request
     * @return
     */
    public AddMeetingRoomBookResponse addMeetingRoomBook(AddMeetingRoomBookRequest request) {
        String url = buildUrl("oa/meetingroom/book");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<AddMeetingRoomBookResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                AddMeetingRoomBookResponse response = JSON.parseObject(contentBody, AddMeetingRoomBookResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 通过日程预定会议室
     * @param request
     * @return
     */
    public AddMeetingRoomBookByScheduleResponse addMeetingRoomBookBySchedule(AddMeetingRoomBookByScheduleRequest request) {
        String url = buildUrl("oa/meetingroom/book_by_schedule");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<AddMeetingRoomBookByScheduleResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                AddMeetingRoomBookByScheduleResponse response = JSON.parseObject(contentBody, AddMeetingRoomBookByScheduleResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 通过会议预定会议室
     * @param request
     * @return
     */
    public AddMeetingRoomBookByMeetingResponse addMeetingRoomBookByMeeting(AddMeetingRoomBookByMeetingRequest request) {
        String url = buildUrl("oa/meetingroom/book_by_meeting");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<AddMeetingRoomBookByMeetingResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                AddMeetingRoomBookByMeetingResponse response = JSON.parseObject(contentBody, AddMeetingRoomBookByMeetingResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 取消预定会议室
     * @param request
     * @return
     */
    public CancelMeetingRoomBookResponse cancelMeetingRoomBook(CancelMeetingRoomBookRequest request) {
        String url = buildUrl("oa/meetingroom/cancel_book");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<CancelMeetingRoomBookResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                CancelMeetingRoomBookResponse response = JSON.parseObject(contentBody, CancelMeetingRoomBookResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }

    /**
     * 根据会议室预定ID查询预定详情
     * @param request
     * @return
     */
    public GetMeetingRoomBookResponse getMeetingRoomBookById(GetMeetingRoomBookByIdRequest request) {
        String url = buildUrl("oa/meetingroom/bookinfo/get");
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        StringEntity params = buildParams(request);
        GenericValue<GetMeetingRoomBookResponse> genericValue = new GenericValue<>();
        QywxCache.getInstance().getHttpRequester().post(url, params, new HttpResponseProcessorAdapter() {
            @Override
            public void success(HttpResponse httpResponse, ContentType contentType) throws IOException {
                String contentBody = getBodyContent(httpResponse);
                GetMeetingRoomBookResponse response = JSON.parseObject(contentBody, GetMeetingRoomBookResponse.class);
                genericValue.setValue(response);
            }
        });
        return genericValue.getValue();
    }
}
