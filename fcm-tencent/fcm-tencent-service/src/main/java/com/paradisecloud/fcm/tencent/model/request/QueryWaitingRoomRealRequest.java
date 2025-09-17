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
public class QueryWaitingRoomRealRequest extends AbstractModel {

    private String meetingId;

    @Expose
    @SerializedName("userid")
    private String userid;

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

    @Override
    public String getPath() {
        return "/v1/meetings/" + this.meetingId+"/waiting-room-participants";
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
        this.addParams("userid",userid.toString());
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
