package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 历史call；busi_history_conference与该是一对多的关系对象 busi_history_call
 *
 * @author lilinhai
 * @date 2021-06-04
 */
@Schema(description = "历史call；busi_history_conference与该是一对多的关系")
public class BusiHistoryCall extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * callId
     */
    @Schema(description = "callId")
    @Excel(name = "callId")
    private String callId;

    /**
     * coSpaceId
     */
    @Schema(description = "coSpaceId")
    @Excel(name = "coSpaceId")
    private String coSpace;

    /**
     * 关联的会议ID
     */
    @Schema(description = "关联的会议ID")
    @Excel(name = "关联的会议ID")
    private Long historyConferenceId;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public void setCoSpace(String coSpace) {
        this.coSpace = coSpace;
    }

    public String getCoSpace() {
        return coSpace;
    }

    public void setHistoryConferenceId(Long historyConferenceId) {
        this.historyConferenceId = historyConferenceId;
    }

    public Long getHistoryConferenceId() {
        return historyConferenceId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("callId", getCallId())
                .append("coSpace", getCoSpace())
                .append("historyConferenceId", getHistoryConferenceId())
                .append("deptId", getDeptId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
