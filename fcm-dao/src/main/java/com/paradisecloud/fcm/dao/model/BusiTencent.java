package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * smc2信息对象 busi_tencent
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
@Schema(description = "smc2信息")
public class BusiTencent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 企业id */
    @Schema(description = "企业id")
    @Excel(name = "企业id")
    private String appId;

    /** 应用ID */
    @Schema(description = "应用ID")
    @Excel(name = "应用ID")
    private String sdkId;

    /** 安全凭证密钥ID */
    @Schema(description = "安全凭证密钥ID")
    @Excel(name = "安全凭证密钥ID")
    private String secretId;

    /** 安全凭证密钥KEY */
    @Schema(description = "安全凭证密钥KEY")
    @Excel(name = "安全凭证密钥KEY")
    private String secretKey;

    /** tencent显示名字 */
    @Schema(description = "tencent显示名字")
    @Excel(name = "tencent显示名字")
    private String name;

    /** smc在线状态：1在线，2离线，3删除 */
    @Schema(description = "smc在线状态：1在线，2离线，3删除")
    @Excel(name = "smc在线状态：1在线，2离线，3删除")
    private Integer status;

    /** tencent会议容量 */
    @Schema(description = "tencent会议容量")
    @Excel(name = "tencent会议容量")
    private Integer capacity;

    /** 备用（本节点宕机后指向的备用节点） */
    @Schema(description = "备用（本节点宕机后指向的备用节点）")
    @Excel(name = "备用", readConverterExp = "本=节点宕机后指向的备用节点")
    private Long spareSmcId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setAppId(String appId) 
    {
        this.appId = appId;
    }

    public String getAppId() 
    {
        return appId;
    }
    public void setSdkId(String sdkId) 
    {
        this.sdkId = sdkId;
    }

    public String getSdkId() 
    {
        return sdkId;
    }
    public void setSecretId(String secretId) 
    {
        this.secretId = secretId;
    }

    public String getSecretId() 
    {
        return secretId;
    }
    public void setSecretKey(String secretKey) 
    {
        this.secretKey = secretKey;
    }

    public String getSecretKey() 
    {
        return secretKey;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setCapacity(Integer capacity) 
    {
        this.capacity = capacity;
    }

    public Integer getCapacity() 
    {
        return capacity;
    }
    public void setSpareSmcId(Long spareSmcId) 
    {
        this.spareSmcId = spareSmcId;
    }

    public Long getSpareSmcId() 
    {
        return spareSmcId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("appId", getAppId())
            .append("sdkId", getSdkId())
            .append("secretId", getSecretId())
            .append("secretKey", getSecretKey())
            .append("name", getName())
            .append("status", getStatus())
            .append("capacity", getCapacity())
            .append("spareSmcId", getSpareSmcId())
            .toString();
    }
}
