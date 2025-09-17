package com.paradisecloud.fcm.tencent.model.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paradisecloud.fcm.tencent.model.MeetingInfo;
import com.tencentcloudapi.wemeet.models.BaseResponse;

import java.io.Serializable;
import java.util.List;

/**
 * @author nj
 * @date 2023/8/8 14:06
 */
public class QueryMeetingInfoResponse extends BaseResponse implements Serializable {

    @SerializedName("meeting_number")
    @Expose
    private Integer meetingNumber;
    @SerializedName("meeting_info_list")
    @Expose
    private List<MeetingInfo> meetingInfoList;

    public QueryMeetingInfoResponse() {
    }

    public Integer getMeetingNumber() {
        return this.meetingNumber;
    }

    public void setMeetingNumber(Integer meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public List<MeetingInfo> getMeetingInfoList() {
        return this.meetingInfoList;
    }

    public void setMeetingInfoList(List<MeetingInfo> meetingInfoList) {
        this.meetingInfoList = meetingInfoList;
    }
}
