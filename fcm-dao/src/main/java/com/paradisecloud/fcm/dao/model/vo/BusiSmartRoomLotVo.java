package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiSmartRoomLot;

public class BusiSmartRoomLotVo extends BusiSmartRoomLot {
    /**
     * 当前页面
     */
    private Integer pageNum;
    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 搜索关键字
     */
    private String searchKey;

    private Long deptId;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
