package com.paradisecloud.fcm.tencent.model.request.layout;

import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

import java.util.List;

/**
 * @author nj
 * @date 2023/7/19 10:41
 */
public class DeleteBackgroundsBatch extends AbstractModel {

    private String meeting_id;
    private String userid;
    private Integer instanceid;
    private List<String> background_id_list;

    @Override
    public String getPath() {
        return "/v1/meetings/"+meeting_id+"/delete-backgrounds";
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
    }

    public Integer getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(Integer instanceid) {
        this.instanceid = instanceid;
    }

    public List<String> getBackground_id_list() {
        return background_id_list;
    }

    public void setBackground_id_list(List<String> background_id_list) {
        this.background_id_list = background_id_list;
    }
}
