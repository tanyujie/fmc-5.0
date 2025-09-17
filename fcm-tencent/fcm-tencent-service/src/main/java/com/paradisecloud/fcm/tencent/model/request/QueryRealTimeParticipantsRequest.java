package com.paradisecloud.fcm.tencent.model.request;

import com.squareup.okhttp.MediaType;
import com.tencentcloudapi.wemeet.common.constants.HttpMethodEnum;
import com.tencentcloudapi.wemeet.models.meeting.QueryMeetingByIdRequest;

/**
 * @author nj
 * @date 2023/7/11 10:15
 */
public class QueryRealTimeParticipantsRequest extends QueryMeetingByIdRequest {
    private String meetingId;
    private String operatorId;
    private Integer operatorIdType;
    private Integer page;
    private Integer pageSize;


    @Override
    public String getPath() {
        return "/v1/meetings/" + this.meetingId+"/real-time-participants";
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


    @Override
    public String getMeetingId() {
        return meetingId;
    }

    @Override
    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }


    public String getOperatorId() {
        return operatorId;
    }

    public Integer getOperatorIdType() {
        return operatorIdType;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }


    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
        this.addParams("operatorId", operatorId.toString());
    }

    public void setOperatorIdType(Integer operatorIdType) {
        this.operatorIdType = operatorIdType;
        this.addParams("operatorIdType", operatorIdType.toString());
    }

    public void setPage(Integer page) {
        this.page = page;
        this.addParams("page", page.toString());
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.addParams("pageSize", pageSize.toString());
    }
}
