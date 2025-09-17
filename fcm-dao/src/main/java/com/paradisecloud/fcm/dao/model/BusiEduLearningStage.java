package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学段信息，小学，初中，高中，大学等对象 busi_edu_learning_stage
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Schema(description = "学段信息，小学，初中，高中，大学等")
public class BusiEduLearningStage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属部门，如果下级部门没有填写，则下级继承该部门的数据信息 */
    @Schema(description = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    @Excel(name = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    private Long deptId;

    /** 学段名字 */
    @Schema(description = "学段名字")
    @Excel(name = "学段名字")
    private String name;

    /** 学制：6六年制，4四年制，3三年制 */
    @Schema(description = "学制：6六年制，4四年制，3三年制")
    @Excel(name = "学制：6六年制，4四年制，3三年制")
    private Integer length;

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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setLength(Integer length) 
    {
        this.length = length;
    }

    public Integer getLength() 
    {
        return length;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("name", getName())
            .append("length", getLength())
            .toString();
    }
}
