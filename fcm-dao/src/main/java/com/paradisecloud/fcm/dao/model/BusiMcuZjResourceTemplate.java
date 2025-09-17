package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 紫荆MCU资源模板对象 busi_mcu_zj_resource_template
 * 
 * @author lilinhai
 * @date 2023-03-17
 */
@Schema(description = "紫荆MCU资源模板")
public class BusiMcuZjResourceTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 服务器ID */
    @Schema(description = "服务器ID")
    @Excel(name = "服务器ID")
    private Long mcuZjServerId;

    /** 名称(描述) */
    @Schema(description = "名称(描述)")
    @Excel(name = "名称(描述)")
    private String name;

    /** 是否支持录制 0:不支持 1:支持 */
    @Schema(description = "是否支持录制 0:不支持 1:支持")
    @Excel(name = "是否支持录制 0:不支持 1:支持")
    private Integer hasRecord;

    /** 是否支持传统分屏 0:不支持 1:支持 */
    @Schema(description = "是否支持传统分屏 0:不支持 1:支持")
    @Excel(name = "是否支持传统分屏 0:不支持 1:支持")
    private Integer hasMosic;

    /** 最大分屏 */
    @Schema(description = "最大分屏")
    @Excel(name = "最大分屏")
    private Integer maxMosic;

    /** 最大主会场分屏数 */
    @Schema(description = "最大主会场分屏数")
    @Excel(name = "最大主会场分屏数")
    private Integer maxSpkMosic;

    /** 最大观众分屏数 */
    @Schema(description = "最大观众分屏数")
    @Excel(name = "最大观众分屏数")
    private Integer maxGuestMosic;

    /** 最大嘉宾分屏数 */
    @Schema(description = "最大嘉宾分屏数")
    @Excel(name = "最大嘉宾分屏数")
    private Integer maxChairMosic;

    /** 嘉宾同屏 */
    @Schema(description = "嘉宾同屏")
    @Excel(name = "嘉宾同屏")
    private String chairCopy;

    /** 带宽 */
    @Schema(description = "带宽")
    @Excel(name = "带宽")
    private String resBw;

    /** 单视角 */
    @Schema(description = "单视角")
    @Excel(name = "单视角")
    private Integer singleView;

    /** 是否默认会议参数 */
    @Schema(description = "是否默认会议参数")
    @Excel(name = "是否默认会议参数")
    private Integer isDefault;

    /** 创建者id */
    @Schema(description = "创建者id")
    @Excel(name = "创建者id")
    private Long createUserId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setMcuZjServerId(Long mcuZjServerId) 
    {
        this.mcuZjServerId = mcuZjServerId;
    }

    public Long getMcuZjServerId() 
    {
        return mcuZjServerId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setHasRecord(Integer hasRecord) 
    {
        this.hasRecord = hasRecord;
    }

    public Integer getHasRecord() 
    {
        return hasRecord;
    }
    public void setHasMosic(Integer hasMosic) 
    {
        this.hasMosic = hasMosic;
    }

    public Integer getHasMosic() 
    {
        return hasMosic;
    }
    public void setMaxMosic(Integer maxMosic) 
    {
        this.maxMosic = maxMosic;
    }

    public Integer getMaxMosic() 
    {
        return maxMosic;
    }
    public void setMaxSpkMosic(Integer maxSpkMosic) 
    {
        this.maxSpkMosic = maxSpkMosic;
    }

    public Integer getMaxSpkMosic() 
    {
        return maxSpkMosic;
    }
    public void setMaxGuestMosic(Integer maxGuestMosic) 
    {
        this.maxGuestMosic = maxGuestMosic;
    }

    public Integer getMaxGuestMosic() 
    {
        return maxGuestMosic;
    }
    public void setMaxChairMosic(Integer maxChairMosic) 
    {
        this.maxChairMosic = maxChairMosic;
    }

    public Integer getMaxChairMosic() 
    {
        return maxChairMosic;
    }
    public void setChairCopy(String chairCopy) 
    {
        this.chairCopy = chairCopy;
    }

    public String getChairCopy() 
    {
        return chairCopy;
    }
    public void setResBw(String resBw) 
    {
        this.resBw = resBw;
    }

    public String getResBw() 
    {
        return resBw;
    }
    public void setSingleView(Integer singleView) 
    {
        this.singleView = singleView;
    }

    public Integer getSingleView() 
    {
        return singleView;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public void setCreateUserId(Long createUserId)
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("mcuZjServerId", getMcuZjServerId())
            .append("name", getName())
            .append("hasRecord", getHasRecord())
            .append("hasMosic", getHasMosic())
            .append("maxMosic", getMaxMosic())
            .append("maxSpkMosic", getMaxSpkMosic())
            .append("maxGuestMosic", getMaxGuestMosic())
            .append("maxChairMosic", getMaxChairMosic())
            .append("chairCopy", getChairCopy())
            .append("resBw", getResBw())
            .append("singleView", getSingleView())
            .append("isDefault", getIsDefault())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .toString();
    }
}
