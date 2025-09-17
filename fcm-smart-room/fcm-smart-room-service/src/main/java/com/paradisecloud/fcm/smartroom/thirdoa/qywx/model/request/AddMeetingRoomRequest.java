package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

import java.math.BigDecimal;
import java.util.List;

public class AddMeetingRoomRequest extends CommonRequest {

    private String name;
    private Integer capacity;
    private String city;
    private String building;
    private String floor;
    private List<Long> equipment;
    private Coordinate coordinate;
    private Range range;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
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

    public List<Long> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Long> equipment) {
        this.equipment = equipment;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    private class Coordinate {
        private BigDecimal latitude;
        private BigDecimal longitude;

        public BigDecimal getLatitude() {
            return latitude;
        }

        public void setLatitude(BigDecimal latitude) {
            this.latitude = latitude;
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public void setLongitude(BigDecimal longitude) {
            this.longitude = longitude;
        }
    }

    private class Range {
        private List<String> user_list;
        private List<Long> department_list;

        public List<String> getUser_list() {
            return user_list;
        }

        public void setUser_list(List<String> user_list) {
            this.user_list = user_list;
        }

        public List<Long> getDepartment_list() {
            return department_list;
        }

        public void setDepartment_list(List<Long> department_list) {
            this.department_list = department_list;
        }
    }
}
