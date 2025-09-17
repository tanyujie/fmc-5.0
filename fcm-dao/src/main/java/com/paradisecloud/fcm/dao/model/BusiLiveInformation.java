package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 直播资料对象 busi_live_information
 *
 * @author lilinhai
 * @date 2024-05-07
 */
@Schema(description = "直播资料")
public class BusiLiveInformation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 资料url地址 */
    @Schema(description = "资料url地址")
    @Excel(name = "资料url地址")
    private String informationUrl;

    /** 直播id */
    @Schema(description = "直播id")
    @Excel(name = "直播id")
    private Long liveBroadcastId;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setInformationUrl(String informationUrl)
    {
        this.informationUrl = informationUrl;
    }

    public String getInformationUrl()
    {
        return informationUrl;
    }
    public void setLiveBroadcastId(Long liveBroadcastId)
    {
        this.liveBroadcastId = liveBroadcastId;
    }

    public Long getLiveBroadcastId()
    {
        return liveBroadcastId;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .append("informationUrl", getInformationUrl())
                .append("liveBroadcastId", getLiveBroadcastId())
                .append("name", getName())
                .toString();
    }
}
