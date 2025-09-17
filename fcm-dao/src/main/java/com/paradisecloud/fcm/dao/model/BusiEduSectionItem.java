package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程节次条目对象 busi_edu_section_item
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Schema(description = "课程节次条目")
public class BusiEduSectionItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 所属节次方案 */
    @Schema(description = "所属节次方案")
    @Excel(name = "所属节次方案")
    private Long sectionSchemeId;

    /** 节次序号 */
    @Schema(description = "节次序号")
    @Excel(name = "节次序号")
    private Integer serialNumber;

    /** 上课开始时间 */
    @Schema(description = "上课开始时间")
    @Excel(name = "上课开始时间")
    private String beginTime;

    /** 上课结束时间 */
    @Schema(description = "上课结束时间")
    @Excel(name = "上课结束时间")
    private String endTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setSectionSchemeId(Long sectionSchemeId) 
    {
        this.sectionSchemeId = sectionSchemeId;
    }

    public Long getSectionSchemeId() 
    {
        return sectionSchemeId;
    }
    public void setSerialNumber(Integer serialNumber) 
    {
        this.serialNumber = serialNumber;
    }

    public Integer getSerialNumber() 
    {
        return serialNumber;
    }
    public void setBeginTime(String beginTime) 
    {
        this.beginTime = beginTime;
    }

    public String getBeginTime() 
    {
        return beginTime;
    }
    public void setEndTime(String endTime) 
    {
        this.endTime = endTime;
    }

    public String getEndTime() 
    {
        return endTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("sectionSchemeId", getSectionSchemeId())
            .append("serialNumber", getSerialNumber())
            .append("beginTime", getBeginTime())
            .append("endTime", getEndTime())
            .toString();
    }
}
