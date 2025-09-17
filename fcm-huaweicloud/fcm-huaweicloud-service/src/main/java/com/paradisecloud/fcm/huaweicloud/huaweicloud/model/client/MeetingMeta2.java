package com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client;

import com.huaweicloud.sdk.core.TypeCasts;
import com.huaweicloud.sdk.core.http.FieldExistence;
import com.huaweicloud.sdk.core.http.HttpMethod;
import com.huaweicloud.sdk.core.http.HttpRequestDef;
import com.huaweicloud.sdk.core.http.LocationType;
import com.huaweicloud.sdk.meeting.v1.model.RestParticipantViewReqBody;
import com.huaweicloud.sdk.meeting.v1.model.SetCustomMultiPictureResponse;
import com.huaweicloud.sdk.meeting.v1.model.SetParticipantViewRequest;
import com.huaweicloud.sdk.meeting.v1.model.SetParticipantViewResponse;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.body.RestCustomMultiPictureBody2;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.request.MediaRequest;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.request.SetCustomMultiPictureRequest2;
import com.paradisecloud.fcm.huaweicloud.huaweicloud.model.client.response.MediaResponse;

import java.util.function.BiConsumer;

/**
 * @author nj
 * @date 2024/3/6 16:49
 */
public class MeetingMeta2 {

    public static final HttpRequestDef<SetCustomMultiPictureRequest2, SetCustomMultiPictureResponse> setCustomMultiPicture2 = genForSetCustomMultiPicture2();

    public static final HttpRequestDef<SetParticipantViewRequest, SetParticipantViewResponse> setParticipantView = genForSetParticipantView2();

    public static final HttpRequestDef<MediaRequest, MediaResponse> getMediaInfo = genMedia();

    public MeetingMeta2() {
    }

    private static HttpRequestDef<SetCustomMultiPictureRequest2, SetCustomMultiPictureResponse> genForSetCustomMultiPicture2() {
        HttpRequestDef.Builder<SetCustomMultiPictureRequest2, SetCustomMultiPictureResponse> builder = HttpRequestDef.builder(HttpMethod.PUT, SetCustomMultiPictureRequest2.class, SetCustomMultiPictureResponse.class).withName("SetCustomMultiPicture").withUri("/v1/mmc/control/conferences/display/customMultiPicture").withContentType("application/json");
        builder.withRequestField("conferenceID", LocationType.Query, FieldExistence.NON_NULL_NON_EMPTY, (Class) TypeCasts.uncheckedConversion(String.class), (f) -> {
            BiConsumer<SetCustomMultiPictureRequest2, Object> writer = (request, value) -> request.setConferenceID((String) value);
            f.withMarshaller(SetCustomMultiPictureRequest2::getConferenceID, writer);
        });
        builder.withRequestField("X-Conference-Authorization", LocationType.Header, FieldExistence.NON_NULL_NON_EMPTY, (Class)TypeCasts.uncheckedConversion(String.class), (f) -> {
            BiConsumer<SetCustomMultiPictureRequest2, Object> writer = (request, value) -> request.setXConferenceAuthorization((String) value);
            f.withMarshaller(SetCustomMultiPictureRequest2::getXConferenceAuthorization,writer);
        });
        builder.withRequestField("body", LocationType.Body, FieldExistence.NULL_IGNORE, (Class)TypeCasts.uncheckedConversion(RestCustomMultiPictureBody2.class), (f) -> {
            BiConsumer<SetCustomMultiPictureRequest2, Object> writer = (request, value) -> request.setBody((RestCustomMultiPictureBody2)value);
            f.withMarshaller(SetCustomMultiPictureRequest2::getBody, writer);
        });
        return builder.build();
    }

    private static HttpRequestDef<MediaRequest, MediaResponse> genMedia() {

        return null;
    }

    private static HttpRequestDef<SetParticipantViewRequest, SetParticipantViewResponse> genForSetParticipantView2() {
        HttpRequestDef.Builder<SetParticipantViewRequest, SetParticipantViewResponse> builder = HttpRequestDef.builder(HttpMethod.PUT, SetParticipantViewRequest.class, SetParticipantViewResponse.class).withName("SetParticipantView").withUri("/v1/mmc/control/conferences/setParticipantView").withContentType("application/json");
        builder.withRequestField("conferenceID", LocationType.Query, FieldExistence.NON_NULL_NON_EMPTY, (Class)TypeCasts.uncheckedConversion(String.class), (f) -> {
            BiConsumer<SetParticipantViewRequest, Object> writer = (request, value) -> request.setConferenceID((String) value);
            f.withMarshaller(SetParticipantViewRequest::getConferenceID, writer);
        });
        builder.withRequestField("participantID", LocationType.Query, FieldExistence.NON_NULL_NON_EMPTY, (Class)TypeCasts.uncheckedConversion(String.class), (f) -> {
            BiConsumer<SetParticipantViewRequest, Object> writer = (request, value) -> request.setParticipantID((String) value);
            f.withMarshaller(SetParticipantViewRequest::getParticipantID, writer);
        });
        builder.withRequestField("X-Conference-Authorization", LocationType.Header, FieldExistence.NON_NULL_NON_EMPTY, (Class)TypeCasts.uncheckedConversion(String.class), (f) -> {
            BiConsumer<SetParticipantViewRequest, Object> writer = (request, value) -> request.setXConferenceAuthorization((String) value);
            f.withMarshaller(SetParticipantViewRequest::getXConferenceAuthorization, writer);
        });
        builder.withRequestField("body", LocationType.Body, FieldExistence.NON_NULL_NON_EMPTY, (Class)TypeCasts.uncheckedConversion(RestParticipantViewReqBody.class), (f) -> {
            BiConsumer<SetParticipantViewRequest, Object> writer = (request, value) -> request.setBody((RestParticipantViewReqBody)value);
            f.withMarshaller(SetParticipantViewRequest::getBody, writer);
        });
        return builder.build();
    }

}
