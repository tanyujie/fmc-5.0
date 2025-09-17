package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * @author johnson liu
 * @date 2021/6/16 17:38
 */
@Schema(description = "每天开始会议的数量")
public class CdrCallNumDate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 部门Id
     */
    @Schema(description = "部门Id")
    @Excel(name = "部门Id")
    private Integer deptId;

    /**
     * fmeIP
     */
    @Schema(description = "fmeIP")
    @Excel(name = "fmeIP")
    private String fmeIp;

    /**
     * 会议数量
     */
    @Schema(description = "会议数量")
    @Excel(name = "会议数量")
    private Integer number;

    /**
     * 日期
     */
    @Schema(description = "日期")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date recordDate;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public String getFmeIp() {
        return fmeIp;
    }

    public void setFmeIp(String fmeIp) {
        this.fmeIp = fmeIp;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deptId", getDeptId())
                .append("fmeIp", getFmeIp())
                .append("number", getNumber())
                .append("recordDate", getRecordDate())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
