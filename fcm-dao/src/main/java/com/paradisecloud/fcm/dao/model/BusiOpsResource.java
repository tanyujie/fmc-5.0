package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ops资源对象 busi_ops_resource
 * 
 * @author lilinhai
 * @date 2024-07-31
 */
@Schema(description = "ops资源")
public class BusiOpsResource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 序列号 */
    @Schema(description = "序列号")
    @Excel(name = "序列号")
    private String sn;

    /** 免费时长 */
    @Schema(description = "免费时长")
    @Excel(name = "免费时长")
    private Integer freeMinutes;

    /** 会议个数 */
    @Schema(description = "会议个数")
    @Excel(name = "会议个数")
    private Integer conferenceNumber;

    /** 购买时长 */
    @Schema(description = "购买时长")
    @Excel(name = "购买时长")
    private Integer purchaseDuration;

    /** 购买个数 */
    @Schema(description = "购买个数")
    @Excel(name = "购买个数")
    private Integer purchaseQuantity;

    /** 正在使用会议个数 */
    @Schema(description = "正在使用会议个数")
    @Excel(name = "正在使用会议个数")
    private Integer usingNumber;

    /** 用户ID */
    @Schema(description = "用户ID")
    @Excel(name = "用户ID")
    private Long userId;

    /** 购买类型 */
    @Schema(description = "购买类型")
    @Excel(name = "购买类型")
    private String purchaseType;

    /** 启用类型TIME,NUMBER */
    @Schema(description = "启用类型TIME,NUMBER")
    @Excel(name = "启用类型TIME,NUMBER")
    private String enableType;

    /** 已使用时间 */
    @Schema(description = "已使用时间")
    @Excel(name = "已使用时间")
    private Integer usedTime;

    /** MCU类型 */
    @Schema(description = "MCU类型")
    @Excel(name = "MCU类型")
    private String mcuType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
    }
    public void setFreeMinutes(Integer freeMinutes) 
    {
        this.freeMinutes = freeMinutes;
    }

    public Integer getFreeMinutes() 
    {
        return freeMinutes;
    }
    public void setConferenceNumber(Integer conferenceNumber) 
    {
        this.conferenceNumber = conferenceNumber;
    }

    public Integer getConferenceNumber() 
    {
        return conferenceNumber;
    }
    public void setPurchaseDuration(Integer purchaseDuration) 
    {
        this.purchaseDuration = purchaseDuration;
    }

    public Integer getPurchaseDuration() 
    {
        return purchaseDuration;
    }
    public void setPurchaseQuantity(Integer purchaseQuantity) 
    {
        this.purchaseQuantity = purchaseQuantity;
    }

    public Integer getPurchaseQuantity() 
    {
        return purchaseQuantity;
    }
    public void setUsingNumber(Integer usingNumber) 
    {
        this.usingNumber = usingNumber;
    }

    public Integer getUsingNumber() 
    {
        return usingNumber;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setPurchaseType(String purchaseType) 
    {
        this.purchaseType = purchaseType;
    }

    public String getPurchaseType() 
    {
        return purchaseType;
    }
    public void setEnableType(String enableType) 
    {
        this.enableType = enableType;
    }

    public String getEnableType() 
    {
        return enableType;
    }

    public Integer getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Integer usedTime) {
        this.usedTime = usedTime;
    }

    public String getMcuType() {
        return mcuType;
    }

    public void setMcuType(String mcuType) {
        this.mcuType = mcuType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sn", getSn())
            .append("freeMinutes", getFreeMinutes())
            .append("conferenceNumber", getConferenceNumber())
            .append("purchaseDuration", getPurchaseDuration())
            .append("purchaseQuantity", getPurchaseQuantity())
            .append("usingNumber", getUsingNumber())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("purchaseType", getPurchaseType())
            .append("enableType", getEnableType())
            .append("usedTime",getUsedTime())
            .append("mcuType",getMcuType())
            .toString();
    }
}
