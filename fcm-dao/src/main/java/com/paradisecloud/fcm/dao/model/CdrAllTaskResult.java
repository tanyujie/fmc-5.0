package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * cdr全定时任务结果对象 cdr_task_result
 *
 * @author johnson liu
 * @date 2021-06-08
 */
@Schema(description = "cdr全定时任务结果")
public class CdrAllTaskResult extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 报表类型
     */
    @Schema(description = "报表类型")
    @Excel(name = "报表类型")
    private Integer reportType;

    /**
     * 时长或数量
     */
    @Schema(description = "时长或数量")
    @Excel(name = "时长或数量")
    private Long durationOrNum;

    /**
     * 统计结果所属日期
     */
    @Schema(description = "统计结果所属日期")
    @Excel(name = "统计结果所属日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date date;

    /**
     * 统计维度分组类型
     */
    @Schema(description = "统计维度分组类型")
    @Excel(name = "统计维度分组类型")
    private Integer groupType;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setDurationOrNum(Long durationOrNum) {
        this.durationOrNum = durationOrNum;
    }

    public Long getDurationOrNum() {
        return durationOrNum;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Integer getGroupType() {
        return groupType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("reportType", getReportType())
                .append("durationOrNum", getDurationOrNum())
                .append("date", getDate())
                .append("groupType", getGroupType())
                .append("createTime", getCreateTime())
                .toString();
    }
}
