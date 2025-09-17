package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotEmpty;

/**
 * 直播地址配置管理对象 busi_live_setting
 *
 * @author lilinhai
 * @date 2021-04-29
 */
@Schema(description = "直播地址配置管理")
public class BusiLiveSetting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @Schema(description = "$column.columnComment")
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    @Excel(name = "名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    /**
     * 直播地址
     */
    @Schema(description = "直播地址")
    @Excel(name = "直播地址")
    @NotEmpty
    private String url;

    /**
     * 状态{1:启用;0禁用}
     */
    @Schema(description = "状态{1:启用;0:禁用}")
    @Excel(name = "状态{1:启用;0禁用}")
    private Integer status;

    /**
     * 描述
     */
    @Schema(description = "描述")
    @Excel(name = "描述")
    private String description;

    /**
     * 入会方案归属部门
     */
    @Schema(description = "入会方案归属部门")
    @Excel(name = "入会方案归属部门")
    private Long deptId;

    /**
     * 远程参与方地址
     */
    @Schema(description = "远程参与方地址")
    @Excel(name = "远程参与方地址")
    @NotEmpty
    private String remoteParty;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getRemoteParty() {
        return remoteParty;
    }

    public void setRemoteParty(String remoteParty) {
        this.remoteParty = remoteParty;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("url", getUrl())
                .append("status", getStatus())
                .append("description", getDescription())
                .append("remoteParty", getRemoteParty())
                .toString();
    }
}
