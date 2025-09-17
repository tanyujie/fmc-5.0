package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/19 10:47
 */
public class QueryBackGroundsList extends AbstractModel {

    private String meetingId;
    private String userid;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;


    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/backgrounds/";
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
        this.addParams("userid",userid);
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
        this.addParams("instanceid",instanceid.toString());
    }
}
