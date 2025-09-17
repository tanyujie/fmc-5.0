package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 默认视图下指定的多分频单元格对象 busi_template_conference_default_view_cell_screen
 * 
 * @author lilinhai
 * @date 2021-04-08
 */
@Schema(description = "默认视图下指定的多分频单元格")
public class BusiTemplateConferenceDefaultViewCellScreen extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 关联的会议模板ID */
    @Schema(description = "关联的会议模板ID")
    @Excel(name = "关联的会议模板ID")
    private Long templateConferenceId;

    /** 单元格序号 */
    @Schema(description = "单元格序号")
    @Excel(name = "单元格序号")
    private Integer cellSequenceNumber;

    /** 分频单元格对应的操作，默认为选看101，105轮询 */
    @Schema(description = "分频单元格对应的操作，默认为选看101，105轮询")
    @Excel(name = "分频单元格对应的操作，默认为选看101，105轮询")
    private Integer operation;

    /** 分频单元格是否固定 */
    @Schema(description = "分频单元格是否固定")
    @Excel(name = "分频单元格是否固定")
    private Integer isFixed;

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
    public void setCellSequenceNumber(Integer cellSequenceNumber) 
    {
        this.cellSequenceNumber = cellSequenceNumber;
    }

    public Integer getCellSequenceNumber() 
    {
        return cellSequenceNumber;
    }
    public void setOperation(Integer operation) 
    {
        this.operation = operation;
    }

    public Integer getOperation() 
    {
        return operation;
    }
    public void setIsFixed(Integer isFixed) 
    {
        this.isFixed = isFixed;
    }

    public Integer getIsFixed() 
    {
        return isFixed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("templateConferenceId", getTemplateConferenceId())
            .append("cellSequenceNumber", getCellSequenceNumber())
            .append("operation", getOperation())
            .append("isFixed", getIsFixed())
            .toString();
    }
}