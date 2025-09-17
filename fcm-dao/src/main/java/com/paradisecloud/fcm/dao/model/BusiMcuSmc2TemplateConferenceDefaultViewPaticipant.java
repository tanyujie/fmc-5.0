package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMC3.0MCU默认视图的参会者对象 busi_mcu_smc3_template_conference_default_view_paticipant
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0MCU默认视图的参会者")
public class BusiMcuSmc2TemplateConferenceDefaultViewPaticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 关联的会议模板ID */
    @Schema(description = "关联的会议模板ID")
    @Excel(name = "关联的会议模板ID")
    private Long templateConferenceId;

    /** 参会终端ID，关联busi_template_participant的ID */
    @Schema(description = "参会终端ID，关联busi_template_participant的ID")
    @Excel(name = "参会终端ID，关联busi_template_participant的ID")
    private Long templateParticipantId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    @Excel(name = "参会者顺序", readConverterExp = "权=重倒叙排列")
    private Integer weight;

    /** 多分频单元格序号 */
    @Schema(description = "多分频单元格序号")
    @Excel(name = "多分频单元格序号")
    private Integer cellSequenceNumber;

    /** 与会者类型：1:主会场 2:观众 */
    @Schema(description = "与会者类型：1:主会场 2:观众")
    @Excel(name = "与会者类型：1:主会场 2:观众")
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
    public void setTemplateParticipantId(Long templateParticipantId) 
    {
        this.templateParticipantId = templateParticipantId;
    }

    public Long getTemplateParticipantId() 
    {
        return templateParticipantId;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }
    public void setCellSequenceNumber(Integer cellSequenceNumber) 
    {
        this.cellSequenceNumber = cellSequenceNumber;
    }

    public Integer getCellSequenceNumber() 
    {
        return cellSequenceNumber;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("templateConferenceId", getTemplateConferenceId())
            .append("templateParticipantId", getTemplateParticipantId())
            .append("weight", getWeight())
            .append("cellSequenceNumber", getCellSequenceNumber())
            .append("type", getType())
            .toString();
    }
}
