package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/20 15:25
 */
public class ApplyingLayoutRequest extends AbstractModel {

    private String meetingId;

    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;

    @Expose
    @SerializedName("operator_id")
    private String operatorId;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;

    @Expose
    @SerializedName("user_list")
    private List<UserListMsopenIdDto> user_list;
    @Expose
    @SerializedName("layout_id")
    private String layout_id;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/applying-layout";
    }

    @Override
    public String getBody() {
        return GSON.toJson(this);
    }

    @Override
    public MediaType contentType() {
        return  MediaType.parse("application/json");
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

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
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

    public static class  UserListMsopenIdDto{
        @Expose
        @SerializedName("instanceid")
        private Integer instanceid;
        @Expose
        @SerializedName("ms_open_id")
        private String ms_open_id;

        public Integer getInstanceid() {
            return instanceid;
        }

        public void setInstanceid(Integer instanceid) {
            this.instanceid = instanceid;
        }

        public String getMs_open_id() {
            return ms_open_id;
        }

        public void setMs_open_id(String ms_open_id) {
            this.ms_open_id = ms_open_id;
        }
    }

    public List<UserListMsopenIdDto> getUser_list() {
        return user_list;
    }

    public void setUser_list(List<UserListMsopenIdDto> user_list) {
        this.user_list = user_list;
    }

    public String getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(String layout_id) {
        this.layout_id = layout_id;
    }
}
