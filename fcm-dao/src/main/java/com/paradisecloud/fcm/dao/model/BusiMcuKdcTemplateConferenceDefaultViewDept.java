package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 默认视图的部门显示顺序对象 busi_mcu_kdc_template_conference_default_view_dept
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@Schema(description = "默认视图的部门显示顺序")
public class BusiMcuKdcTemplateConferenceDefaultViewDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 关联的会议模板ID */
    @Schema(description = "关联的会议模板ID")
    @Excel(name = "关联的会议模板ID")
    private Long templateConferenceId;

    /** 部门ID（部门也是MCU终端，一种与会者） */
    @Schema(description = "部门ID（部门也是MCU终端，一种与会者）")
    @Excel(name = "部门ID", readConverterExp = "部=门也是MCU终端，一种与会者")
    private Long deptId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    @Excel(name = "参会者顺序", readConverterExp = "权=重倒叙排列")
    private Integer weight;

    /** 类型：1主会场 2观众 */
    @Schema(description = "与会者类型：1主会场 2观众")
    @Excel(name = "与会者类型：1主会场 2观众")
    private Integer type;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTemplateConferenceId(Long templateConferenceId) 
    {
        this.templateConferenceId = templateConferenceId;
    }

    public Long getTemplateConferenceId() 
    {
        return templateConferenceId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("templateConferenceId", getTemplateConferenceId())
            .append("deptId", getDeptId())
            .append("weight", getWeight())
            .append("type", getType())
            .toString();
    }
}
