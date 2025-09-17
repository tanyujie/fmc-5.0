package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 班级信息对象 busi_edu_class
 * 
 * @author lilinhai
 * @date 2021-10-19
 */
@Schema(description = "班级信息")
public class BusiEduClass extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 归属部门，如果下级部门没有填写，则下级继承该部门的数据信息 */
    @Schema(description = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    @Excel(name = "归属部门，如果下级部门没有填写，则下级继承该部门的数据信息")
    private Long deptId;

    /** 班级号 */
    @Schema(description = "班级号")
    @Excel(name = "班级号")
    private String number;

    /** 班级名字 */
    @Schema(description = "班级名字")
    @Excel(name = "班级名字")
    private String name;

    /** 班长 */
    @Schema(description = "班长")
    @Excel(name = "班长")
    private String monitor;

    /** 班主任 */
    @Schema(description = "班主任")
    @Excel(name = "班主任")
    private String classTeacher;

    /** 荣誉称号 */
    @Schema(description = "荣誉称号")
    @Excel(name = "荣誉称号")
    private String honoraryTitle;

    /** 毕业时间，如2026年毕业 */
    @Schema(description = "毕业时间，如2026年毕业")
    @Excel(name = "毕业时间，如2026年毕业")
    private String graduationTime;

    /** 关联学段 */
    @Schema(description = "关联学段")
    @Excel(name = "关联学段")
    private Long learningStage;

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
    public void setNumber(String number) 
    {
        this.number = number;
    }

    public String getNumber() 
    {
        return number;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setMonitor(String monitor) 
    {
        this.monitor = monitor;
    }

    public String getMonitor() 
    {
        return monitor;
    }
    public void setClassTeacher(String classTeacher) 
    {
        this.classTeacher = classTeacher;
    }

    public String getClassTeacher() 
    {
        return classTeacher;
    }
    public void setHonoraryTitle(String honoraryTitle) 
    {
        this.honoraryTitle = honoraryTitle;
    }

    public String getHonoraryTitle() 
    {
        return honoraryTitle;
    }
    public void setGraduationTime(String graduationTime) 
    {
        this.graduationTime = graduationTime;
    }

    public String getGraduationTime() 
    {
        return graduationTime;
    }
    public void setLearningStage(Long learningStage) 
    {
        this.learningStage = learningStage;
    }

    public Long getLearningStage() 
    {
        return learningStage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .append("number", getNumber())
            .append("name", getName())
            .append("monitor", getMonitor())
            .append("classTeacher", getClassTeacher())
            .append("honoraryTitle", getHonoraryTitle())
            .append("graduationTime", getGraduationTime())
            .append("learningStage", getLearningStage())
            .toString();
    }
}