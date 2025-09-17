package com.paradisecloud.fcm.smartroom.thirdoa.qywx.model.request;

import java.util.List;

/**
 * @author admin
 */
public class QueryMeetingRoomRequest extends CommonRequest {

    private String city;
    private String building;
    private String floor;
    private List<Integer> equipment;

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
}
