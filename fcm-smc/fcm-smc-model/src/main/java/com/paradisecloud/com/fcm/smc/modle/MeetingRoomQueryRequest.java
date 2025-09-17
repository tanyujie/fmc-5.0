package com.paradisecloud.com.fcm.smc.modle;


import java.util.ArrayList;
import java.util.List;

/**
 * @author nj
 * @date 2022/8/23 11:58
 */

public class MeetingRoomQueryRequest {

    private String organizationId;
    private String keyWord;
    private Boolean thirdRoom = false;
    private String searchtree="false";
    private int page;
    private int size;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
}
