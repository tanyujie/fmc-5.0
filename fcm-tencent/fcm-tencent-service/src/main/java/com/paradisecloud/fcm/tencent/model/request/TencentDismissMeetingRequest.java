package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/12 9:59
 */
public class TencentDismissMeetingRequest extends AbstractModel {
    private String meetingId;
    @Expose
    @SerializedName("userid")
    private String userId;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceId;
    @Expose
    @SerializedName("reason_code")
    private Integer reasonCode;
    @Expose
    @SerializedName("reason_detail")
    private String reasonDetail;
    @Expose
    @SerializedName("force_dismiss_meeting")
    private Integer forceDismissMeeting;
    @Expose
    @SerializedName("retrieve_code")
    private Integer retrieveCode;

    @Expose
    @SerializedName("operator_id_type")
    private Integer operatorIdType;

    @Expose
    @SerializedName("operator_id")
    private String operatorId;

    public TencentDismissMeetingRequest() {
    }

    @Override
    public String getPath() {
        return "/v1/meetings/" + this.meetingId + "/dismiss";
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
        return HttpMethodEnum.POST;
    }

    public String getMeetingId() {
        return this.meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getReasonCode() {
        return this.reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDetail() {
        return this.reasonDetail;
    }

    public void setReasonDetail(String reasonDetail) {
        this.reasonDetail = reasonDetail;
    }

    public Integer getForceDismissMeeting() {
        return this.forceDismissMeeting;
    }

    public void setForceDismissMeeting(Integer forceDismissMeeting) {
        this.forceDismissMeeting = forceDismissMeeting;
    }

    public Integer getRetrieveCode() {
        return this.retrieveCode;
    }

    public void setRetrieveCode(Integer retrieveCode) {
        this.retrieveCode = retrieveCode;
    }

    public Integer getOperatorIdType() {
        return operatorIdType;
    }

    public void setOperatorIdType(Integer operatorIdType) {
        this.operatorIdType = operatorIdType;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
}