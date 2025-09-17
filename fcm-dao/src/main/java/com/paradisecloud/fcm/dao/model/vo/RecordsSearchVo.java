package com.paradisecloud.fcm.dao.model.vo;

/**
 * 录制文件搜索类
 */
public class RecordsSearchVo {

    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 当前页面
     */
    private Integer pageNum;
    /**
     * 每页显示条数
     */
    private Integer pageSize;
    /**
     * 检索关键字
     */
    private String searchKey;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

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
}
