package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 10:24
 */
public class SetingDefaultBackgrounds extends AbstractModel {
    @Expose
    @SerializedName("meetingId")
    private String meetingId;
    @Expose
    @SerializedName("userid")
    private String userid;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("selected_background_id")
    private String selected_background_id;


    @Override
    public String getPath() {
        return "/v1/meetings/"+meetingId+"/default-backgrounds";
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public String getSelected_background_id() {
        return selected_background_id;
    }

    public void setSelected_background_id(String selected_background_id) {
        this.selected_background_id = selected_background_id;
    }
}
