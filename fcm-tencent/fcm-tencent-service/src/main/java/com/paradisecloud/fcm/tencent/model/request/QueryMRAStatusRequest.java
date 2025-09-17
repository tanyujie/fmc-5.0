package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/17 10:28
 */
public class QueryMRAStatusRequest extends AbstractModel {

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
    @SerializedName("user_instance_id")
    private Integer user_instance_id;
    @Expose
    @SerializedName("user_ms_open_id")
    private String user_ms_open_id;

    @Override
    public String getPath() {
        return "/v1/meetings/" + this.meetingId+"/query-participant";
    }

    @Override
    public String getBody() {
         return null;
    }

    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.GET;
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

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
        this.addParams("instanceid",instanceid.toString());
    }

    public Integer getUser_instance_id() {
        return user_instance_id;
    }

    public void setUser_instance_id(Integer user_instance_id) {
        this.user_instance_id = user_instance_id;
        this.addParams("user_instance_id",user_instance_id.toString());
    }

    public String getUser_ms_open_id() {
        return user_ms_open_id;
    }

    public void setUser_ms_open_id(String user_ms_open_id) {
        this.user_ms_open_id = user_ms_open_id;
        this.addParams("user_ms_open_id",user_ms_open_id);
    }
}
