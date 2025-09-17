package com.paradisecloud.fcm.tencent.model.request.layout;

import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/19 10:37
 */
public class DeleteBackgrounds extends AbstractModel {
    private String meetingId;
    private String background_id;

    private String userid;

    private Integer instanceid;

    @Override
    public String getPath() {
        return "/v1/meetings/meetings/"+meetingId+"/backgrounds/"+background_id;
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
        return HttpMethodEnum.DELETE;
    }


    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getBackground_id() {
        return background_id;
    }

    public void setBackground_id(String background_id) {
        this.background_id = background_id;
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
