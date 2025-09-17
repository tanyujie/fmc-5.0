package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 操作日志记录对象 busi_operation_log
 *
 * @author lilinhai
 * @date 2023-11-27
 */
@Schema(description = "操作日志记录")
public class BusiOperationLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 操作用户id
     */
    @Schema(description = "操作用户id")
    @Excel(name = "操作用户id")
    private Long userId;

    /**
     * 操作者姓名
     */
    @Schema(description = "操作者姓名")
    @Excel(name = "操作者姓名")
    private String operatorName;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    @Excel(name = "操作时间", width = 30)
    private Date time;

    /**
     * 操作的具体细节
     */
    @Schema(description = "操作的具体细节")
    @Excel(name = "操作的具体细节")
    private String actionDetails;

    /**
     * 操作的结果，1=成功、2=失败
     */
    @Schema(description = "操作的结果，1=成功、2=失败")
    @Excel(name = "操作的结果，1=成功、2=失败")
    private Integer actionResult;

    /**
     * 操作的用户的IP地址
     */
    @Schema(description = "操作的用户的IP地址")
    @Excel(name = "操作的用户的IP地址")
    private String ip;

    /**
     * 操作的用户的设备信息，操作系统、浏览器
     */
    @Schema(description = "操作的用户的设备信息，操作系统、浏览器")
    @Excel(name = "操作的用户的设备信息，操作系统、浏览器")
    private String deviceType;

    /**
     * 操作会议的历史会议id
     */
    @Schema(description = "操作会议的历史会议id")
    @Excel(name = "操作会议的历史会议id")
    private Long historyConferenceId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionResult(Integer actionResult) {
        this.actionResult = actionResult;
    }

    public Integer getActionResult() {
        return actionResult;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setHistoryConferenceId(Long historyConferenceId) {
        this.historyConferenceId = historyConferenceId;
    }

    public Long getHistoryConferenceId() {
        return historyConferenceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("operatorName", getOperatorName())
                .append("time", getTime())
                .append("actionDetails", getActionDetails())
                .append("actionResult", getActionResult())
                .append("ip", getIp())
                .append("deviceType", getDeviceType())
                .append("historyConferenceId", getHistoryConferenceId())
                .toString();
    }
}