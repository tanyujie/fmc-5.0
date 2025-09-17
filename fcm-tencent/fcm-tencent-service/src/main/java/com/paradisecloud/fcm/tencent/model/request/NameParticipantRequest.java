package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/13 10:52
 */
public class NameParticipantRequest extends AbstractModel {
    private String meetingId;

    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;

    @Expose
    @SerializedName("users")
    private List<UsersDTO> users;
    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;

    @Override
    public String getPath() {
        return "/v1/real-control/meetings/"+meetingId+"/names";
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


    public static class UsersDTO {
        @Expose
        @SerializedName("ms_open_id")
        private String msOpenid;
        @Expose
        @SerializedName("instanceid")
        private Integer instanceid;

        @Expose
        @SerializedName("to_operator_id")
        private String toOperatorId;
        @Expose
        @SerializedName("to_operator_id_type")
        private Integer toOperatorIdType;
        @Expose
        @SerializedName("nick_name")
        private String nick_name;

        public String getMsOpenid() {
            return msOpenid;
        }

        public void setMsOpenid(String msOpenid) {
            this.msOpenid = msOpenid;
        }

        public Integer getInstanceid() {
            return instanceid;
        }

        public void setInstanceid(Integer instanceid) {
            this.instanceid = instanceid;
        }

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

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }
    }


    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public List<UsersDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UsersDTO> users) {
        this.users = users;
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
}


