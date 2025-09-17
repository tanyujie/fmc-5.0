package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 课程节次策略对象 busi_edu_section_strategy
 * 
 * @author lilinhai
 * @date 2021-10-11
 */
@Schema(description = "课程节次策略")
public class BusiEduSectionStrategy extends BaseEntity
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
    private Integer sectionNumber;

    /** 节次策略名 */
    @Schema(description = "节次策略名")
    @Excel(name = "节次策略名")
    private String name;

    /** 上课开始时间 */
    @Schema(description = "上课开始时间")
    @Excel(name = "上课开始时间")
    private String beginTime;

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
    public void setSectionNumber(Integer sectionNumber) 
    {
        this.sectionNumber = sectionNumber;
    }

    public Integer getSectionNumber() 
    {
        return sectionNumber;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setBeginTime(String beginTime) 
    {
        this.beginTime = beginTime;
    }

    public String getBeginTime() 
    {
        return beginTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("sectionSchemeId", getSectionSchemeId())
            .append("sectionNumber", getSectionNumber())
            .append("name", getName())
            .append("beginTime", getBeginTime())
            .toString();
    }
}