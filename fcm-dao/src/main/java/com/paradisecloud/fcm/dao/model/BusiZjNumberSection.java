package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 【请填写功能名称】对象 busi_zj_number_section
 *
 * @author lilinhai
 * @date 2023-03-27
 */
@Schema(description = "【请填写功能名称】")
public class BusiZjNumberSection extends BaseEntity
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("deptId", getDeptId())
                .append("startValue", getStartValue())
                .append("endValue", getEndValue())
                .toString();
    }
}
