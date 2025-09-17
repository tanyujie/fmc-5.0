package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/8/10 17:10
 */
public class RoomCallRequest extends AbstractModel {
    @Expose
    @SerializedName("meeting_id")
    private String meetingId;
    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;

    @Expose
    @SerializedName("meeting_room_id")
    private String meetingRoomId;
    @Expose
    @SerializedName("mra_address")
    private String mraAddress;

    @Override
    public String getPath() {
        return "/v1/meeting-rooms/room-call";
    }

    @Override
    public String getBody() {
        return GSON.toJson(this);
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json");
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.PUT;
    }


    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
        this.addParams("operator_id",operatorId);
    }

    public Integer getOperatorIdType() {
        return operatorIdType;
    }

    public void setOperatorIdType(Integer operatorIdType) {
        this.operatorIdType = operatorIdType;
        this.addParams("operator_id_type",operatorIdType.toString());
    }

    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
        this.addParams("meeting_room_id",meetingRoomId);
    }

    public String getMraAddress() {
        return mraAddress;
    }

    public void setMraAddress(String mraAddress) {
        this.mraAddress = mraAddress;
        this.addParams("mra_address",mraAddress);
    }
}
