package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议号段对象 busi_conference_number_section
 * 
 * @author lilinhai
 * @date 2021-05-19
 */
@Schema(description = "会议号段")
public class BusiConferenceNumberSection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 关联的部门 */
    @Schema(description = "关联的部门")
    @Excel(name = "关联的部门")
    private Long deptId;

    /** 号段起始值 */
    @Schema(description = "号段起始值")
    @Excel(name = "号段起始值")
    private Long startValue;

    /** 号段结束值 */
    @Schema(description = "号段结束值")
    @Excel(name = "号段结束值")
    private Long endValue;

    /** MCU类型 */
    @Schema(description = "MCU类型")
    @Excel(name = "MCU类型")
    private String mcuType;

    /** 号段类型 */
    @Schema(description = "号段类型")
    @Excel(name = "号段类型")
    private Integer sectionType;

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
    public void setStartValue(Long startValue) 
    {
        this.startValue = startValue;
    }

    public Long getStartValue() 
    {
        return startValue;
    }
    public void setEndValue(Long endValue) 
    {
        this.endValue = endValue;
    }

    public Long getEndValue() 
    {
        return endValue;
    }

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    public Integer getSectionType() {
        return sectionType;
    }

    public void setSectionType(Integer sectionType) {
        this.sectionType = sectionType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("startValue", getStartValue())
            .append("endValue", getEndValue())
            .append("mcuType", getMcuType())
            .append("sectionType", getSectionType())
            .toString();
    }
}
