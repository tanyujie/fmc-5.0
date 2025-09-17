package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 教室对象 busi_edu_classroom
 * 
 * @author lilinhai
 * @date 2021-10-19
 */
@Schema(description = "教室")
public class BusiEduClassroom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属部门，如果下级部门没有填写，则下级继承该部门的数据信息 */
    @Schema(description = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    @Excel(name = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    private Long deptId;

    /** 教室名字 */
    @Schema(description = "教室名字")
    @Excel(name = "教室名字")
    private String name;

    /** 关联的建筑 */
    @Schema(description = "关联的建筑")
    @Excel(name = "关联的建筑")
    private Long scoolBuildingId;

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
    public void setScoolBuildingId(Long scoolBuildingId) 
    {
        this.scoolBuildingId = scoolBuildingId;
    }

    public Long getScoolBuildingId() 
    {
        return scoolBuildingId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("name", getName())
            .append("scoolBuildingId", getScoolBuildingId())
            .toString();
    }
}