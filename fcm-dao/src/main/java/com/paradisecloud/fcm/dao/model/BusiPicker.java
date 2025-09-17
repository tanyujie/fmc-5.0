package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_picker
 * 
 * @author lilinhai
 * @date 2024-06-13
 */
@Schema(description = "【请填写功能名称】")
public class BusiPicker extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** 部门id */
    @Schema(description = "部门id")
    @Excel(name = "部门id")
    private Integer deptId;

    /** 用户id */
    @Schema(description = "用户id")
    @Excel(name = "用户id")
    private Integer userId;

    private String deptType;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setDeptId(Integer deptId) 
    {
        this.deptId = deptId;
    }

    public Integer getDeptId() 
    {
        return deptId;
    }
    public void setUserId(Integer userId) 
    {
        this.userId = userId;
    }

    public Integer getUserId() 
    {
        return userId;
    }


    public String getDeptType() {
        return deptType;
    }

    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("userId", getUserId())
            .toString();
    }
}
