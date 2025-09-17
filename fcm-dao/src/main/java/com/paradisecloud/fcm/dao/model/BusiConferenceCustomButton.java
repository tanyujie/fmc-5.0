package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议自定义按钮对象 busi_conference_custom_button
 * 
 * @author lilinhai
 * @date 2024-07-05
 */
@Schema(description = "会议自定义按钮")
public class BusiConferenceCustomButton extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Schema(description = "ID")
    private String id;

    /** 名称 */
    @Schema(description = "名称")
    @Excel(name = "名称")
    private String name;

    /** 顺序 */
    @Schema(description = "顺序")
    @Excel(name = "顺序")
    private Integer sort;

    /** MCU类型 */
    @Schema(description = "MCU类型")
    @Excel(name = "MCU类型")
    private String mcuType;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
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
    public void setSort(Integer sort) 
    {
        this.sort = sort;
    }

    public Integer getSort() 
    {
        return sort;
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
            .append("name", getName())
            .append("sort", getSort())
            .append("mcuType", getMcuType())
            .toString();
    }
}
