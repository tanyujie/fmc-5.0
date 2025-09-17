package com.paradisecloud.fcm.dao.model.vo;

import com.paradisecloud.fcm.dao.model.BusiInfoDisplay;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 信息展示对象 busi_info_display
 * 
 * @author lilinhai
 * @date 2024-05-13
 */
@Schema(description = "信息展示")
public class BusiInfoDisplayVO extends BusiInfoDisplay
{
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
