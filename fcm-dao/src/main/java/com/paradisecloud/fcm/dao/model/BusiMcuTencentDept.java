package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）对象 busi_mcu_smc3_dept
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0组分配租户的中间（一个MCU组可以分配给多个租户，一对多）")
public class BusiMcuTencentDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 分配给的租户 */
    @Schema(description = "分配给的租户")
    @Excel(name = "分配给的租户")
    private Long deptId;

    /** 1单节点，100集群 */
    @Schema(description = "1单节点，100集群")
    @Excel(name = "1单节点，100集群")
    private Integer mcuType;

    /** 当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段 */
    @Schema(description = "当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段")
    @Excel(name = "当mcu_type为1是，指向busi_mcu_smc3的id字段，为100指向busi_mcu_smc3_cluster的id字段")
    private Long mcuId;

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
    public void setMcuType(Integer mcuType) 
    {
        this.mcuType = mcuType;
    }

    public Integer getMcuType() 
    {
        return mcuType;
    }
    public void setMcuId(Long mcuId) 
    {
        this.mcuId = mcuId;
    }

    public Long getMcuId() 
    {
        return mcuId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("mcuType", getMcuType())
            .append("mcuId", getMcuId())
            .toString();
    }
}
