package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播服务器集群对象 busi_live_cluster
 * 
 * @author lilinhai
 * @date 2022-10-26
 */
@Schema(description = "直播服务器集群")
public class BusiLiveCluster extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 集群组名，最长32 */
    @Schema(description = "集群组名，最长32")
    @Excel(name = "集群组名，最长32")
    private String name;

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
            .append("description", getDescription())
            .toString();
    }
}
