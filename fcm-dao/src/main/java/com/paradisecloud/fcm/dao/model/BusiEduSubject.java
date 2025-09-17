package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学科信息对象 busi_edu_subject
 * 
 * @author lilinhai
 * @date 2021-10-10
 */
@Schema(description = "学科信息")
public class BusiEduSubject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属部门，如果下级部门没有填写，则下级继承该部门的数据信息 */
    @Schema(description = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    @Excel(name = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    private Long deptId;

    /** 归属父级学科 */
    @Schema(description = "归属父级学科")
    @Excel(name = "归属父级学科")
    private Long parentId;

    /** 学科名字 */
    @Schema(description = "学科名字")
    @Excel(name = "学科名字")
    private String name;

    /** 学科代码 */
    @Schema(description = "学科代码")
    @Excel(name = "学科代码")
    private String code;

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
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("parentId", getParentId())
            .append("name", getName())
            .append("code", getCode())
            .append("remark", getRemark())
            .toString();
    }
}
