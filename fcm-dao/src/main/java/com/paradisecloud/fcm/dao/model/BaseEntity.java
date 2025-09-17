package com.paradisecloud.fcm.dao.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String searchValue;
    @Schema(description = "创建者")
    private String createBy;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新者")
    private String updateBy;
    @Schema(description = "更新时间")
    private Date updateTime;
    private String remark;
    private Map<String, Object> params;

    public BaseEntity() {
    }

    public String getSearchValue() {
        return this.searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Map<String, Object> getParams() {
        if (this.params == null) {
            this.params = new HashMap();
        }

        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}

