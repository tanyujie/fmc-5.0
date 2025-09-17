package com.paradisecloud.smc.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 部门smc模板关联对象 busi_smc_dept_template
 * 
 * @author lilinhai
 * @date 2023-03-17
 */
@Schema(description = "部门smc模板关联")
public class BusiSmcDeptTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 部门id */
    @Schema(description = "部门id")
    @Excel(name = "部门id")
    private Long deptId;

    /** 模板ID */
    @Schema(description = "模板ID")
    @Excel(name = "模板ID")
    private String smcTemplateId;

    /** 模板名称 */
    @Schema(description = "模板名称")
    @Excel(name = "模板名称")
    private String templateName;

    /** 会议时长分钟数 */
    @Schema(description = "会议时长分钟数")
    @Excel(name = "会议时长分钟数")
    private Integer duration;

    /** 会议类型 */
    @Schema(description = "会议类型")
    @Excel(name = "会议类型")
    private String type;

    /** 带宽 */
    @Schema(description = "带宽")
    @Excel(name = "带宽")
    private Integer rate;

    /** 直播 */
    @Schema(description = "直播")
    @Excel(name = "直播")
    private Integer supportLive;

    /** 录制 */
    @Schema(description = "录制")
    @Excel(name = "录制")
    private Integer supportRecord;

    /** 数据会议 */
    @Schema(description = "数据会议")
    @Excel(name = "数据会议")
    private Integer amcRecord;

    /** 自动静音 */
    @Schema(description = "自动静音")
    @Excel(name = "自动静音")
    private Integer autoMute;

    /** 主席密码 */
    @Schema(description = "主席密码")
    @Excel(name = "主席密码")
    private String chairmanPassword;

    /** 嘉宾密码 */
    @Schema(description = "嘉宾密码")
    @Excel(name = "嘉宾密码")
    private String guestPassword;

    /** 虚拟号码 */
    @Schema(description = "虚拟号码")
    @Excel(name = "虚拟号码")
    private String vmrNumber;

    /** 最大入会数 */
    @Schema(description = "最大入会数")
    @Excel(name = "最大入会数")
    private Integer maxParticipantNum;

    private Integer enableDataConf;

    public Integer getEnableDataConf() {
        return enableDataConf;
    }

    public void setEnableDataConf(Integer enableDataConf) {
        this.enableDataConf = enableDataConf;
    }

    private long masterTerminalId;

    public long getMasterTerminalId() {
        return masterTerminalId;
    }

    public void setMasterTerminalId(long masterTerminalId) {
        this.masterTerminalId = masterTerminalId;
    }

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
    public void setSmcTemplateId(String smcTemplateId) 
    {
        this.smcTemplateId = smcTemplateId;
    }

    public String getSmcTemplateId() 
    {
        return smcTemplateId;
    }
    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
    }
    public void setDuration(Integer duration) 
    {
        this.duration = duration;
    }

    public Integer getDuration() 
    {
        return duration;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setRate(Integer rate) 
    {
        this.rate = rate;
    }

    public Integer getRate() 
    {
        return rate;
    }
    public void setSupportLive(Integer supportLive) 
    {
        this.supportLive = supportLive;
    }

    public Integer getSupportLive() 
    {
        return supportLive;
    }
    public void setSupportRecord(Integer supportRecord) 
    {
        this.supportRecord = supportRecord;
    }

    public Integer getSupportRecord() 
    {
        return supportRecord;
    }
    public void setAmcRecord(Integer amcRecord) 
    {
        this.amcRecord = amcRecord;
    }

    public Integer getAmcRecord() 
    {
        return amcRecord;
    }
    public void setAutoMute(Integer autoMute) 
    {
        this.autoMute = autoMute;
    }

    public Integer getAutoMute() 
    {
        return autoMute;
    }
    public void setChairmanPassword(String chairmanPassword) 
    {
        this.chairmanPassword = chairmanPassword;
    }

    public String getChairmanPassword() 
    {
        return chairmanPassword;
    }
    public void setGuestPassword(String guestPassword) 
    {
        this.guestPassword = guestPassword;
    }

    public String getGuestPassword() 
    {
        return guestPassword;
    }
    public void setVmrNumber(String vmrNumber) 
    {
        this.vmrNumber = vmrNumber;
    }

    public String getVmrNumber() 
    {
        return vmrNumber;
    }
    public void setMaxParticipantNum(Integer maxParticipantNum) 
    {
        this.maxParticipantNum = maxParticipantNum;
    }

    public Integer getMaxParticipantNum() 
    {
        return maxParticipantNum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("smcTemplateId", getSmcTemplateId())
            .append("createTime", getCreateTime())
            .append("templateName", getTemplateName())
            .append("duration", getDuration())
            .append("type", getType())
            .append("rate", getRate())
            .append("supportLive", getSupportLive())
            .append("supportRecord", getSupportRecord())
            .append("amcRecord", getAmcRecord())
            .append("autoMute", getAutoMute())
            .append("chairmanPassword", getChairmanPassword())
            .append("guestPassword", getGuestPassword())
            .append("vmrNumber", getVmrNumber())
            .append("maxParticipantNum", getMaxParticipantNum())
            .toString();
    }
}
