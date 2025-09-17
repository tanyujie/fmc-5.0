package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * 中兴MCU会议模板的参会者对象 busi_mcu_zte_template_participant
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
@Schema(description = "中兴MCU会议模板的参会者")
public class BusiMcuZteTemplateParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 参会类型：1被叫，2手动主叫，3自动主叫，10直播 */
    @Schema(description = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    @Excel(name = "参会类型：1被叫，2手动主叫，3自动主叫，10直播")
    private Integer attendType;

    /** 模板中的与会者的UUID */
    @Schema(description = "模板中的与会者的UUID")
    @Excel(name = "模板中的与会者的UUID")
    private String uuid;

    /** 会议模板ID */
    @Schema(description = "会议模板ID")
    @Excel(name = "会议模板ID")
    private Long templateConferenceId;

    /** 终端ID */
    @Schema(description = "终端ID")
    @Excel(name = "终端ID")
    private Long terminalId;

    /** 参会者顺序（权重倒叙排列） */
    @Schema(description = "参会者顺序（权重倒叙排列）")
    @Excel(name = "参会者顺序", readConverterExp = "权=重倒叙排列")
    private Integer weight;

    /** 业务属性 */
    @Schema(description = "业务属性")
    @Excel(name = "业务属性")
    private Map<String,Object> businessProperties;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAttendType(Integer attendType) 
    {
        this.attendType = attendType;
    }

    public Integer getAttendType() 
    {
        return attendType;
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
    public void setTerminalId(Long terminalId) 
    {
        this.terminalId = terminalId;
    }

    public Long getTerminalId() 
    {
        return terminalId;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }

    public Map<String, Object> getBusinessProperties() {
        return businessProperties;
    }

    public void setBusinessProperties(Map<String, Object> businessProperties) {
        this.businessProperties = businessProperties;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("attendType", getAttendType())
            .append("uuid", getUuid())
            .append("templateConferenceId", getTemplateConferenceId())
            .append("terminalId", getTerminalId())
            .append("weight", getWeight())
            .append("businessProperties", getBusinessProperties())
            .toString();
    }
}
