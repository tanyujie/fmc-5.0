package com.paradisecloud.fcm.tencent.model.request.layout;

import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/19 9:57
 */
public class DeleteMeetingLayoutRequest extends AbstractModel {

    private String meeting_id;
    private String userid;
    private Integer instanceid;
    private String layout_id;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meeting_id+"/layouts/"+layout_id;
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

    public String getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(String meeting_id) {
        this.meeting_id = meeting_id;
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

    public String getLayout_id() {
        return layout_id;
    }

    public void setLayout_id(String layout_id) {
        this.layout_id = layout_id;
    }
}
