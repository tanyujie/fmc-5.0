package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiOperationLog;

import java.util.Date;

public class OperationLogSearchVo extends BusiOperationLog {

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

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

    /**
     * 年
     */
    private String year;

    /**
     * 排序
     */
    private String sort = "desc";

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSort() {
        if (sort.equals("asc")) {
            return "asc";
        } else {
            return "desc";
        }
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
