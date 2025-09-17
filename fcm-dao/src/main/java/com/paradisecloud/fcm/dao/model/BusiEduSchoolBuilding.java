package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学校建筑，用于存放教室，教室肯定是归属某个建筑对象 busi_edu_school_building
 * 
 * @author lilinhai
 * @date 2021-10-15
 */
@Schema(description = "学校建筑，用于存放教室，教室肯定是归属某个建筑")
public class BusiEduSchoolBuilding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属部门，如果下级部门没有填写，则下级继承该部门的数据信息 */
    @Schema(description = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    @Excel(name = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    private Long deptId;

    /** 建筑名字 */
    @Schema(description = "建筑名字")
    @Excel(name = "建筑名字")
    private String name;

    /** 建筑描述 */
    @Schema(description = "建筑描述")
    @Excel(name = "建筑描述")
    private String remarks;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("name", getName())
            .append("remarks", getRemarks())
            .toString();
    }
}