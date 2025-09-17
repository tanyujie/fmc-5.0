package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/25 10:49
 */
public class RaiseHandRequest  extends AbstractModel {

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
    @SerializedName("user")
    private UserDTO user;
    @Expose
    @SerializedName("raise_hand")
    private Boolean raise_hand;

    public static class UserDTO {
        @Expose
        @SerializedName("ms_open_id")
        private String msOpenId;
        @Expose
        @SerializedName("instanceid")
        private Integer instanceid;

        public String getMsOpenId() {
            return msOpenId;
        }

        public void setMsOpenId(String msOpenId) {
            this.msOpenId = msOpenId;
        }

        public Integer getInstanceid() {
            return instanceid;
        }

        public void setInstanceid(Integer instanceid) {
            this.instanceid = instanceid;
        }
    }


    @Override
    public String getPath() {
        return "/v1/mra-control/meetings/"+meetingId+"/raise-hand";
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



    public Boolean getRaise_hand() {
        return raise_hand;
    }

    public void setRaise_hand(Boolean raise_hand) {
        this.raise_hand = raise_hand;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
