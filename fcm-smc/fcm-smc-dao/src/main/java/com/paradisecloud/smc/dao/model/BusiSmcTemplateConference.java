package com.paradisecloud.smc.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * smc模板会议关联对象 busi_smc_template_conference
 * 
 * @author nj
 * @date 2022-09-20
 */
@Schema(description = "smc模板会议关联")
public class BusiSmcTemplateConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Integer id;

    /** smc模板ID */
    @Schema(description = "smc模板ID")
    @Excel(name = "smc模板ID")
    private String smcTemplateId;

    /** smc会议ID */
    @Schema(description = "smc会议ID")
    @Excel(name = "smc会议ID")
    private String conferenceId;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setSmcTemplateId(String smcTemplateId) 
    {
        this.smcTemplateId = smcTemplateId;
    }

    public String getSmcTemplateId() 
    {
        return smcTemplateId;
    }
    public void setConferenceId(String conferenceId) 
    {
        this.conferenceId = conferenceId;
    }

    public String getConferenceId() 
    {
        return conferenceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("smcTemplateId", getSmcTemplateId())
            .append("conferenceId", getConferenceId())
            .toString();
    }
}
