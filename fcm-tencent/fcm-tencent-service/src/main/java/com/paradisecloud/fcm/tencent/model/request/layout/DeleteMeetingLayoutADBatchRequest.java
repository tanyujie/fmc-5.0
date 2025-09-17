package com.paradisecloud.fcm.tencent.model.request.layout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 9:57
 */
public class DeleteMeetingLayoutADBatchRequest extends AbstractModel {
    @Expose
    @SerializedName("meeting_id")
    private String meeting_id;
    @Expose
    @SerializedName("operator_id")
    private String operator_id;
    @Expose
    @SerializedName("operator_id_type")
    private Integer operator_id_type;
    @Expose
    @SerializedName("instanceid")
    private Integer instanceid;
    @Expose
    @SerializedName("layout_id_list")
    private List<String> layout_id_list;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meeting_id+"/delete-advanced-layouts/";
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



    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
        this.addParams("instanceid",instanceid.toString());
    }

    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public Integer getOperator_id_type() {
        return operator_id_type;
    }

    public void setOperator_id_type(Integer operator_id_type) {
        this.operator_id_type = operator_id_type;
    }

    public List<String> getLayout_id_list() {
        return layout_id_list;
    }

    public void setLayout_id_list(List<String> layout_id_list) {
        this.layout_id_list = layout_id_list;
    }
}
