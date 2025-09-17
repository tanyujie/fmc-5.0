package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/7/25 15:19
 */
public class QueryRoomsDeviceRequest extends AbstractModel {

    @Expose
    @SerializedName("page")
    private Integer page;
    @Expose
    @SerializedName("page_size")
    private Integer page_size;
    @Expose
    @SerializedName("meeting_room_name")
    private String meeting_room_name;


    @Override
    public String getPath() {
        return "/v1/devices";
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


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
        this.addParams("page",page.toString());
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
        this.addParams("page_size",page_size.toString());
    }

    public String getMeeting_room_name() {
        return meeting_room_name;
    }

    public void setMeeting_room_name(String meeting_room_name) {
        this.meeting_room_name = meeting_room_name;
        this.addParams("meeting_room_name",meeting_room_name);
    }
}
