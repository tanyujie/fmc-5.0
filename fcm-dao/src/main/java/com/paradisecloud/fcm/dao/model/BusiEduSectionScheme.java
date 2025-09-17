package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程节次方案，每个季节都可能有不同的节次方案对象 busi_edu_section_scheme
 * 
 * @author lilinhai
 * @date 2021-10-30
 */
@Schema(description = "课程节次方案，每个季节都可能有不同的节次方案")
public class BusiEduSectionScheme extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属部门，如果下级部门没有填写，则下级继承该部门的数据信息 */
    @Schema(description = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    @Excel(name = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    private Long deptId;

    /** 节次方案名字 */
    @Schema(description = "节次方案名字")
    @Excel(name = "节次方案名字")
    private String name;

    /** 每节课的进行时间（单位分钟） */
    @Schema(description = "每节课的进行时间（单位分钟）")
    @Excel(name = "每节课的进行时间", readConverterExp = "单=位分钟")
    private Integer classInterval;

    /** 课间休息时长（单位分钟） */
    @Schema(description = "课间休息时长（单位分钟）")
    @Excel(name = "课间休息时长", readConverterExp = "单=位分钟")
    private Integer breakInterval;

    /** 启用状态：1启用，2禁用 */
    @Schema(description = "启用状态：1启用，2禁用")
    @Excel(name = "启用状态：1启用，2禁用")
    private Integer enableStatus;

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
    public void setClassInterval(Integer classInterval) 
    {
        this.classInterval = classInterval;
    }

    public Integer getClassInterval() 
    {
        return classInterval;
    }
    public void setBreakInterval(Integer breakInterval) 
    {
        this.breakInterval = breakInterval;
    }

    public Integer getBreakInterval() 
    {
        return breakInterval;
    }
    public void setEnableStatus(Integer enableStatus) 
    {
        this.enableStatus = enableStatus;
    }

    public Integer getEnableStatus() 
    {
        return enableStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("name", getName())
            .append("classInterval", getClassInterval())
            .append("breakInterval", getBreakInterval())
            .append("enableStatus", getEnableStatus())
            .toString();
    }
}