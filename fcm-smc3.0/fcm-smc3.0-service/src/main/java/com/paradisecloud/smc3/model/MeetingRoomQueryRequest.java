package com.paradisecloud.smc3.model;


import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 11:58
 */

public class MeetingRoomQueryRequest {
    private String areaId;
    private String organizationId;
    private String keyWord;
    private Boolean thirdRoom = false;
    private String searchtree="true";
    private List<?> conditionList=new ArrayList<>();
    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getThirdRoom() {
        return thirdRoom;
    }

    public void setThirdRoom(Boolean thirdRoom) {
        this.thirdRoom = thirdRoom;
    }

    public String getSearchtree() {
        return searchtree;
    }

    public void setSearchtree(String searchtree) {
        this.searchtree = searchtree;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public List<?> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<?> conditionList) {
        this.conditionList = conditionList;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
