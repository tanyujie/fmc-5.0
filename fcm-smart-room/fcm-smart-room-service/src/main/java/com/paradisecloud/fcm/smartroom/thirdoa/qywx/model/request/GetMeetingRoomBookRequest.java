package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

public class GetMeetingRoomBookRequest extends CommonRequest {

    private int meetingroom_id;
    private int start_time;
    private int end_time;
    private String city;
    private String building;
    private String floor;

    public int getMeetingroom_id() {
        return meetingroom_id;
    }

    public void setMeetingroom_id(int meetingroom_id) {
        this.meetingroom_id = meetingroom_id;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }
}
