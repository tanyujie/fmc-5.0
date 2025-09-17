package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/8/8 17:11
 */
public class QueryMeetingItemActionRequest extends AbstractModel {

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


    @Override
    public String getPath() {
        return "/v1/real-control/meetings/" + this.meetingId+"/status";
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
        return HttpMethodEnum.POST;
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

}
