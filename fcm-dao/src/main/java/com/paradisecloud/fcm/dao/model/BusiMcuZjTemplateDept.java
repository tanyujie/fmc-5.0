package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 紫荆MCU会议模板的级联部门对象 busi_mcu_zj_template_dept
 * 
 * @author lilinhai
 * @date 2021-02-03
 */
@Schema(description = "紫荆MCU会议模板的级联部门")
public class BusiMcuZjTemplateDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 模板中的级联与会者终端的UUID */
    @Schema(description = "模板中的级联与会者终端的UUID")
    @Excel(name = "模板中的级联与会者终端的UUID")
    private String uuid;

    /** 会议模板ID */
    @Schema(description = "会议模板ID")
    @Excel(name = "会议模板ID")
    private Long templateConferenceId;

    /** 部门ID（部门也是MCU终端，一种与会者） */
    @Schema(description = "部门ID（部门也是MCU终端，一种与会者）")
    @Excel(name = "部门ID", readConverterExp = "部=门也是MCU终端，一种与会者")
    private Long deptId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    @Excel(name = "参会者顺序", readConverterExp = "权=重倒叙排列")
    private Integer weight;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUuid(String uuid) 
    {
        this.uuid = uuid;
    }

    public String getUuid() 
    {
        return uuid;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("uuid", getUuid())
            .append("templateConferenceId", getTemplateConferenceId())
            .append("deptId", getDeptId())
            .append("weight", getWeight())
            .toString();
    }
}