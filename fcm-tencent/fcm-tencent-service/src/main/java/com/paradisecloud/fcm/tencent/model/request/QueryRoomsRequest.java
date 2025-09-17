package com.paradisecloud.fcm.tencent.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.AbstractModel;

/**
 * @author nj
 * @date 2023/8/10 17:10
 */
public class QueryRoomsRequest extends AbstractModel {


    /**
     * 当前页，页码起始值为1。
     */
    @Expose
    @SerializedName("page")
    private Integer page;
    /**
     * 每页数据条数。默认值20，最大值50。
     */
    @Expose
    @SerializedName("page_size")
    private Integer pageSize;


    @Expose
    @SerializedName("meeting_room_name")
    private String meeting_room_name;

    @Override
    public String getPath() {
        return "/v1/meeting-rooms";
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/json");
    }

    @Override
    public HttpMethodEnum getMethod() {
        return HttpMethodEnum.GET;
    }


    public String getMeeting_room_name() {
        return meeting_room_name;
    }

    public void setMeeting_room_name(String meeting_room_name) {
        this.meeting_room_name = meeting_room_name;
        this.addParams("meeting_room_name",meeting_room_name);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if(page!=null){
            this.page = page;
            this.addParams("page",page.toString());
        }

    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if(pageSize==null){
            return;
        }
        this.pageSize = pageSize;
        this.addParams("page_size",pageSize.toString());
    }
}
