package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/13 10:56
 */

public class MuteParticipantRequest extends AbstractModel {

    private String meetingId;
    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("mute")
    private Boolean mute;
    @Expose
    @SerializedName("user")
    private UserDTO user;

    @Override
    public String getPath() {
        return "/v1/real-control/meetings/"+meetingId+"/mutes";
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



    public static class UserDTO {
        @Expose
        @SerializedName("to_operator_id")
        private String toOperatorId;
        @Expose
        @SerializedName("to_operator_id_type")
        private Integer toOperatorIdType;
        @Expose
        @SerializedName("instanceid")
        private Integer instanceid;

        @Expose
        @SerializedName("uuid")
        private String uuid;

        public String getToOperatorId() {
            return toOperatorId;
        }

        public void setToOperatorId(String toOperatorId) {
            this.toOperatorId = toOperatorId;
        }

        public Integer getToOperatorIdType() {
            return toOperatorIdType;
        }

        public void setToOperatorIdType(Integer toOperatorIdType) {
            this.toOperatorIdType = toOperatorIdType;
        }

        public Integer getInstanceid() {
            return instanceid;
        }

        public void setInstanceid(Integer instanceid) {
            this.instanceid = instanceid;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
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
    }

    public Integer getOperatorIdType() {
        return operatorIdType;
    }

    public void setOperatorIdType(Integer operatorIdType) {
        this.operatorIdType = operatorIdType;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
