package com.paradisecloud.fcm.tencent.busi.attende;

/**
 * @author nj
 * @date 2024/3/26 15:07
 */
public class RoomAttendeeTencent extends AttendeeTencent{

    private static final long serialVersionUID = 1L;

    private String meetingRoomId;

    private String meetingRoomName;

    private Integer meetingRoomStatus;

    private Boolean isallowCall;

    private String accountType;


    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }

    public Integer getMeetingRoomStatus() {
        return meetingRoomStatus;
    }

    public void setMeetingRoomStatus(Integer meetingRoomStatus) {
        this.meetingRoomStatus = meetingRoomStatus;
    }

    public Boolean getIsallowCall() {
        return isallowCall;
    }

    public void setIsallowCall(Boolean isallowCall) {
        this.isallowCall = isallowCall;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
