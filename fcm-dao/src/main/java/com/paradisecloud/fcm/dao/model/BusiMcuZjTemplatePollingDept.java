package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 轮询方案的部门对象 busi_mcu_zj_template_polling_dept
 * 
 * @author lilinhai
 * @date 2021-02-25
 */
@Schema(description = "轮询方案的部门")
public class BusiMcuZjTemplatePollingDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    @Schema(description = "自增ID")
    private Long id;

    /** 会议模板ID */
    @Schema(description = "会议模板ID")
    @Excel(name = "会议模板ID")
    private Long templateConferenceId;

    /** 归属轮询方案ID */
    @Schema(description = "归属轮询方案ID")
    @Excel(name = "归属轮询方案ID")
    private Long pollingSchemeId;

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
    public void setTemplateConferenceId(Long templateConferenceId) 
    {
        this.templateConferenceId = templateConferenceId;
    }

    public Long getTemplateConferenceId() 
    {
        return templateConferenceId;
    }
    public void setPollingSchemeId(Long pollingSchemeId) 
    {
        this.pollingSchemeId = pollingSchemeId;
    }

    public Long getPollingSchemeId() 
    {
        return pollingSchemeId;
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
            .append("templateConferenceId", getTemplateConferenceId())
            .append("pollingSchemeId", getPollingSchemeId())
            .append("deptId", getDeptId())
            .append("weight", getWeight())
            .toString();
    }
}
