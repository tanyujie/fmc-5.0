package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 租户绑定服务器资源对象 busi_free_switch_dept
 * 
 * @author zyz
 * @date 2021-09-02
 */
@Schema(description = "租户绑定服务器资源")
public class BusiFreeSwitchDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Schema(description = "主键id")
    private Long id;

    /** 关联租户 */
    @Schema(description = "关联租户")
    @Excel(name = "关联租户")
    private Long deptId;

    /** 服务器id */
    @Schema(description = "服务器id(当fcm_type为1是，指向busi_free_switch的id字段，为100指向busi_free_switch_cluster的id字段)")
    @Excel(name = "服务器id(当fcm_type为1是，指向busi_free_switch的id字段，为100指向busi_free_switch_cluster的id字段)")
    private Long serverId;

    /** 1单节点，100集群 */
    @Schema(description = "1单节点，100集群")
    @Excel(name = "1单节点，100集群")
    private Integer fcmType;

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
    public void setServerId(Long serverId) 
    {
        this.serverId = serverId;
    }

    public Long getServerId() 
    {
        return serverId;
    }

    public Integer getFcmType() {
        return fcmType;
    }

    public void setFcmType(Integer fcmType) {
        this.fcmType = fcmType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("serverId", getServerId())
            .append("fcmType", getFcmType())
            .toString();
    }
}
