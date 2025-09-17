package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SMC3.0MCU轮询方案的参会者对象 busi_mcu_smc3_template_polling_paticipant
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0MCU轮询方案的参会者")
public class BusiMcuSmc3TemplatePollingPaticipant extends BaseEntity
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

    /** 该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔） */
    @Schema(description = "该参会者的特定的轮询间隔（如果该值存在，会覆盖轮询方案终端间隔）")
    @Excel(name = "该参会者的特定的轮询间隔", readConverterExp = "如=果该值存在，会覆盖轮询方案终端间隔")
    private Integer pollingInterval;

    /** 会场UUID */
    @Schema(description = "会场UUID")
    @Excel(name = "会场UUID")
    private String attendeeId;

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
    public void setPollingInterval(Integer pollingInterval) 
    {
        this.pollingInterval = pollingInterval;
    }

    public Integer getPollingInterval() 
    {
        return pollingInterval;
    }
    public void setAttendeeId(String attendeeId) 
    {
        this.attendeeId = attendeeId;
    }

    public String getAttendeeId() 
    {
        return attendeeId;
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
            .append("pollingInterval", getPollingInterval())
            .append("attendeeId", getAttendeeId())
            .append("weight", getWeight())
            .toString();
    }
}
