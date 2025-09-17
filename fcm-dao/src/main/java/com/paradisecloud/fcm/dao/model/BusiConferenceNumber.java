package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议号码记录对象 busi_conference_number
 * 
 * @author lilinhai
 * @date 2021-05-19
 */
@Schema(description = "会议号码记录")
public class BusiConferenceNumber extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    @Excel(name = "创建者ID")
    private Long createUserId;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    /** 归属公司ID */
    @Schema(description = "归属公司ID")
    @Excel(name = "归属公司ID")
    private Long deptId;

    /** 会议号类型：1默认，2普通 */
    @Schema(description = "会议号类型：1默认，2普通")
    @Excel(name = "会议号类型：1默认，2普通")
    private Integer type;

    /** 号码状态：1闲置，10已预约，100会议中 */
    @Schema(description = "号码状态：1闲置，10已预约，100会议中")
    @Excel(name = "号码状态：1闲置，10已预约，100会议中")
    private Integer status;

    /** 创建类型：1手动，2自动 */
    @Schema(description = "创建类型：1手动，2自动")
    @Excel(name = "创建类型：1手动，2自动")
    private Integer createType;

    /** 备注信息 */
    @Schema(description = "备注信息")
    @Excel(name = "备注信息")
    private String remarks;

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
    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }
    public void setCreateUserName(String createUserName) 
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setCreateType(Integer createType) 
    {
        this.createType = createType;
    }

    public Integer getCreateType() 
    {
        return createType;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
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
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("deptId", getDeptId())
            .append("type", getType())
            .append("status", getStatus())
            .append("createType", getCreateType())
            .append("remarks", getRemarks())
            .append("mcuType", getMcuType())
            .toString();
    }
}