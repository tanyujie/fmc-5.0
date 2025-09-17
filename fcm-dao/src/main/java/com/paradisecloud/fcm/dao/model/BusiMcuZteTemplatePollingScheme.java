package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 中兴MCU轮询方案对象 busi_mcu_zte_template_polling_scheme
 * 
 * @author lilinhai
 * @date 2024-04-09
 */
@Schema(description = "中兴MCU轮询方案")
public class BusiMcuZteTemplatePollingScheme extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    @Schema(description = "自增ID")
    private Long id;

    /** 会议模板ID */
    @Schema(description = "会议模板ID")
    @Excel(name = "会议模板ID")
    private Long templateConferenceId;

    /** 轮询时间间隔 */
    @Schema(description = "轮询时间间隔")
    @Excel(name = "轮询时间间隔")
    private Integer pollingInterval;

    /** 轮询策略：1全局轮询，2选定范围，3全局轮询+组织架构优先，4选定范围+组织架构优先 */
    @Schema(description = "轮询策略：1全局轮询，2选定范围，3全局轮询+组织架构优先，4选定范围+组织架构优先")
    @Excel(name = "轮询策略：1全局轮询，2选定范围，3全局轮询+组织架构优先，4选定范围+组织架构优先")
    private Integer pollingStrategy;

    /** 轮询方案名 */
    @Schema(description = "轮询方案名")
    @Excel(name = "轮询方案名")
    private String schemeName;

    /** 启用状态：1启用，2禁用 */
    @Schema(description = "启用状态：1启用，2禁用")
    @Excel(name = "启用状态：1启用，2禁用")
    private Integer enableStatus;

    /** 轮询方案顺序，越大越靠前 */
    @Schema(description = "轮询方案顺序，越大越靠前")
    @Excel(name = "轮询方案顺序，越大越靠前")
    private Integer weight;

    /** 多分频轮询支持 */
    @Schema(description = "多分频轮询支持")
    @Excel(name = "多分频轮询支持")
    private String layout;

    /** 是否广播(1是，2否) */
    @Schema(description = "是否广播(1是，2否)")
    @Excel(name = "是否广播(1是，2否)")
    private Integer isBroadcast;

    /** 是否显示自己(1是，2否) */
    @Schema(description = "是否显示自己(1是，2否)")
    @Excel(name = "是否显示自己(1是，2否)")
    private Integer isDisplaySelf;

    /** 是否补位(1是，2否) */
    @Schema(description = "是否补位(1是，2否)")
    @Excel(name = "是否补位(1是，2否)")
    private Integer isFill;

    /** 是否固定主会场(1是，2否) */
    @Schema(description = "是否固定主会场(1是，2否)")
    @Excel(name = "是否固定主会场(1是，2否)")
    private Integer isFixSelf;

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
    public void setPollingInterval(Integer pollingInterval) 
    {
        this.pollingInterval = pollingInterval;
    }

    public Integer getPollingInterval() 
    {
        return pollingInterval;
    }
    public void setPollingStrategy(Integer pollingStrategy) 
    {
        this.pollingStrategy = pollingStrategy;
    }

    public Integer getPollingStrategy() 
    {
        return pollingStrategy;
    }
    public void setSchemeName(String schemeName) 
    {
        this.schemeName = schemeName;
    }

    public String getSchemeName() 
    {
        return schemeName;
    }
    public void setEnableStatus(Integer enableStatus) 
    {
        this.enableStatus = enableStatus;
    }

    public Integer getEnableStatus() 
    {
        return enableStatus;
    }
    public void setWeight(Integer weight) 
    {
        this.weight = weight;
    }

    public Integer getWeight() 
    {
        return weight;
    }
    public void setLayout(String layout) 
    {
        this.layout = layout;
    }

    public String getLayout() 
    {
        return layout;
    }
    public void setIsBroadcast(Integer isBroadcast) 
    {
        this.isBroadcast = isBroadcast;
    }

    public Integer getIsBroadcast() 
    {
        return isBroadcast;
    }
    public void setIsDisplaySelf(Integer isDisplaySelf) 
    {
        this.isDisplaySelf = isDisplaySelf;
    }

    public Integer getIsDisplaySelf() 
    {
        return isDisplaySelf;
    }
    public void setIsFill(Integer isFill) 
    {
        this.isFill = isFill;
    }

    public Integer getIsFill() 
    {
        return isFill;
    }
    public void setIsFixSelf(Integer isFixSelf) 
    {
        this.isFixSelf = isFixSelf;
    }

    public Integer getIsFixSelf() 
    {
        return isFixSelf;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("templateConferenceId", getTemplateConferenceId())
            .append("pollingInterval", getPollingInterval())
            .append("pollingStrategy", getPollingStrategy())
            .append("schemeName", getSchemeName())
            .append("enableStatus", getEnableStatus())
            .append("weight", getWeight())
            .append("layout", getLayout())
            .append("isBroadcast", getIsBroadcast())
            .append("isDisplaySelf", getIsDisplaySelf())
            .append("isFill", getIsFill())
            .append("isFixSelf", getIsFixSelf())
            .toString();
    }
}
