package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * 批量删除会议布局
 * @author nj
 * @date 2023/7/31 11:05
 */
public class DeleteMeetingLayoutBatchRequest extends AbstractModel {

    @Expose
    @SerializedName("meeting_id")
    private String meeting_id;
    @Expose
    @SerializedName("userid")
    private String userid;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("layout_id_list")
    private List<String> layout_id_list;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meeting_id+"/delete-layouts/";
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

    public List<String> getLayout_id_list() {
        return layout_id_list;
    }

    public void setLayout_id_list(List<String> layout_id_list) {
        this.layout_id_list = layout_id_list;
    }
}
