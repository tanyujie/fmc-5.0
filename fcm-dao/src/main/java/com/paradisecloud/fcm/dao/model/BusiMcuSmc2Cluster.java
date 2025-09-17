package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMC3.0MCU集群对象 busi_mcu_smc3_cluster
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0MCU集群")
public class BusiMcuSmc2Cluster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 集群组名，最长32 */
    @Schema(description = "集群组名，最长32")
    @Excel(name = "集群组名，最长32")
    private String name;

    /** 备用mcu类型，1单节点，100集群 */
    @Schema(description = "备用mcu类型，1单节点，100集群")
    @Excel(name = "备用mcu类型，1单节点，100集群")
    private Integer spareMcuType;

    /** 当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段 */
    @Schema(description = "当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段")
    @Excel(name = "当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段")
    private Long spareMcuId;

    /** 备注信息 */
    @Schema(description = "备注信息")
    @Excel(name = "备注信息")
    private String description;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setSpareMcuType(Integer spareMcuType) 
    {
        this.spareMcuType = spareMcuType;
    }

    public Integer getSpareMcuType() 
    {
        return spareMcuType;
    }
    public void setSpareMcuId(Long spareMcuId) 
    {
        this.spareMcuId = spareMcuId;
    }

    public Long getSpareMcuId() 
    {
        return spareMcuId;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("name", getName())
            .append("spareMcuType", getSpareMcuType())
            .append("spareMcuId", getSpareMcuId())
            .append("description", getDescription())
            .toString();
    }
}
