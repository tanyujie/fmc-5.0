package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

import java.util.List;

/**
 * @author admin
 */
public class EditMeetingRoomRequest extends CommonRequest {

    private int meetingroom_id;
    private String name;
    private int capacity;
    private String city;
    private String building;
    private String floor;
    private List<Integer> equipment;
    private CoordinateDTO coordinate;
    private RangeDTO range;

    public int getMeetingroom_id() {
        return meetingroom_id;
    }

    public void setMeetingroom_id(int meetingroom_id) {
        this.meetingroom_id = meetingroom_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public List<Integer> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Integer> equipment) {
        this.equipment = equipment;
    }

    public CoordinateDTO getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(CoordinateDTO coordinate) {
        this.coordinate = coordinate;
    }

    public RangeDTO getRange() {
        return range;
    }

    public void setRange(RangeDTO range) {
        this.range = range;
    }

    public static class CoordinateDTO {
        private String latitude;
        private String longitude;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }

    public static class RangeDTO {
        private List<String> user_list;
        private List<Integer> department_list;

        public List<String> getUser_list() {
            return user_list;
        }

        public void setUser_list(List<String> user_list) {
            this.user_list = user_list;
        }

        public List<Integer> getDepartment_list() {
            return department_list;
        }

        public void setDepartment_list(List<Integer> department_list) {
            this.department_list = department_list;
        }
    }
}
