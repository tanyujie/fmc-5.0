package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）对象 busi_live_dept
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Schema(description = "直播服务器组分配租户的中间（一个FME组可以分配给多个租户，一对多）")
public class BusiLiveDept extends BaseEntity
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
    private Integer liveType;

    /** 当live_type为1是，指向busi_live的id字段，为100指向busi_live_cluster的id字段 */
    @Schema(description = "当live_type为1是，指向busi_live的id字段，为100指向busi_live_cluster的id字段")
    @Excel(name = "当live_type为1是，指向busi_live的id字段，为100指向busi_live_cluster的id字段")
    private Long liveId;

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
    public void setLiveType(Integer liveType) 
    {
        this.liveType = liveType;
    }

    public Integer getLiveType() 
    {
        return liveType;
    }
    public void setLiveId(Long liveId) 
    {
        this.liveId = liveId;
    }

    public Long getLiveId() 
    {
        return liveId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("liveType", getLiveType())
            .append("liveId", getLiveId())
            .toString();
    }
}
