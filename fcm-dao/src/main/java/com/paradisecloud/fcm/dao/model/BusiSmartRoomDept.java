package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）对象 busi_smart_room_dept
 *
 * @author lilinhai
 * @date 2024-02-19
 */
@Schema(description = "智慧办公房间分配租户的中间（一个智慧办公房间可以分配给多个租户，一对多）")
public class BusiSmartRoomDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 房间ID */
    @Schema(description = "房间ID")
    @Excel(name = "房间ID")
    private Long roomId;

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
    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public Long getRoomId()
    {
        return roomId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deptId", getDeptId())
                .append("roomId", getRoomId())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .toString();
    }
}