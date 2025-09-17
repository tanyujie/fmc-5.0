package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * FSBC服务器-部门映射对象 busi_fsbc_server_dept
 * 
 * @author lilinhai
 * @date 2021-04-21
 */
@Schema(description = "FSBC服务器-部门映射")
public class BusiFsbcServerDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** FSBC服务器的id */
    @Schema(description = "FSBC服务器的id")
    @Excel(name = "FSBC服务器的id")
    private Long fsbcServerId;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setFsbcServerId(Long fsbcServerId) 
    {
        this.fsbcServerId = fsbcServerId;
    }

    public Long getFsbcServerId() 
    {
        return fsbcServerId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("fsbcServerId", getFsbcServerId())
            .append("deptId", getDeptId())
            .toString();
    }
}
