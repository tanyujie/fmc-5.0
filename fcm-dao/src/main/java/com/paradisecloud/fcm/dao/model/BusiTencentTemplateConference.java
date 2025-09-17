package com.paradisecloud.fcm.dao.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 华为MCU会议模板对象 busi_tencent_template_conference
 * 
 * @author lilinhai
 * @date 2023-07-05
 */
@Schema(description = "华为MCU会议模板")
public class BusiTencentTemplateConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 创建者id */
    @Schema(description = "创建者id")
    @Excel(name = "创建者id")
    private Long createUserId;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    /** 模板会议名 */
    @Schema(description = "模板会议名")
    @Excel(name = "模板会议名")
    private String subject;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 入会方案配置ID（关联MCU里面的入会方案记录ID，会控端不存） */
    @Schema(description = "入会方案配置ID（关联MCU里面的入会方案记录ID，会控端不存）")
    @Excel(name = "入会方案配置ID", readConverterExp = "关=联MCU里面的入会方案记录ID，会控端不存")
    private String callLegProfileId;

    /** 虚拟会议室参数 */
    @Schema(description = "虚拟会议室参数")
    @Excel(name = "虚拟会议室参数")
    private String callProfileId;

    /** 呼入标识参数 */
    @Schema(description = "呼入标识参数")
    @Excel(name = "呼入标识参数")
    private String callBrandingProfileId;

    /** 直播地址 */
    @Schema(description = "直播地址")
    @Excel(name = "直播地址")
    private String streamUrl;

    /** 是否启用录制(1是，2否) */
    @Schema(description = "是否启用录制(1是，2否)")
    @Excel(name = "是否启用录制(1是，2否)")
    private Integer recordingEnabled;

    /** 开启直播：1是，2否 */
    @Schema(description = "开启直播：1是，2否")
    @Excel(name = "开启直播：1是，2否")
    private Integer streamingEnabled;

    /** 带宽1,2,3,4,5,6M */
    @Schema(description = "带宽1,2,3,4,5,6M")
    @Excel(name = "带宽1,2,3,4,5,6M")
    private Integer bandwidth;

    /** 是否自动呼叫与会者：1是，2否 */
    @Schema(description = "是否自动呼叫与会者：1是，2否")
    @Excel(name = "是否自动呼叫与会者：1是，2否")
    private Integer isAutoCall;

    /** 是否自动监听会议：1是，2否 */
    @Schema(description = "是否自动监听会议：1是，2否")
    @Excel(name = "是否自动监听会议：1是，2否")
    private Integer isAutoMonitor;

    /** 是否自动创建会议号：1是，2否 */
    @Schema(description = "是否自动创建会议号：1是，2否")
    @Excel(name = "是否自动创建会议号：1是，2否")
    private Integer isAutoCreateConferenceNumber;

    /** 创建类型：1自动，2手动 */
    @Schema(description = "创建类型：1自动，2手动")
    @Excel(name = "创建类型：1自动，2手动")
    private Integer createType;

    /** 模板会议是否允许被级联：1允许，2不允许 */
    @Schema(description = "模板会议是否允许被级联：1允许，2不允许")
    @Excel(name = "模板会议是否允许被级联：1允许，2不允许")
    private Integer cascad;

    /** 视图类型（1卡片，2列表） */
    @Schema(description = "视图类型（1卡片，2列表）")
    @Excel(name = "视图类型", readConverterExp = "1=卡片，2列表")
    private Integer viewType;

    /** 模板绑定的会议号 */
    @Schema(description = "模板绑定的会议号")
    @Excel(name = "模板绑定的会议号")
    private Long conferenceNumber;

    /** 主会场ID */
    @Schema(description = "主会场ID")
    @Excel(name = "主会场ID")
    private Long masterParticipantId;

    /** 默认视图布局类型 */
    @Schema(description = "默认视图布局类型")
    @Excel(name = "默认视图布局类型")
    private String defaultViewLayout;

    /** 默认视图是否广播(1是，2否) */
    @Schema(description = "默认视图是否广播(1是，2否)")
    @Excel(name = "默认视图是否广播(1是，2否)")
    private Integer defaultViewIsBroadcast;

    /** 默认视图是否显示自己(1是，2否，3空白)panePlacementSelfPaneMode */
    @Schema(description = "默认视图是否显示自己(1是，2否，3空白)panePlacementSelfPaneMode")
    @Excel(name = "默认视图是否显示自己(1是，2否，3空白)panePlacementSelfPaneMode")
    private Integer defaultViewIsDisplaySelf;

    /** 默认视图是否补位(1是，2否) */
    @Schema(description = "默认视图是否补位(1是，2否)")
    @Excel(name = "默认视图是否补位(1是，2否)")
    private Integer defaultViewIsFill;

    /** 轮询时间间隔 */
    @Schema(description = "轮询时间间隔")
    @Excel(name = "轮询时间间隔")
    private Integer pollingInterval;

    /** 会议密码 */
    @Schema(description = "会议密码")
    @Excel(name = "会议密码")
    private String conferencePassword;

    /** 会议备注 */
    @Schema(description = "会议备注")
    @Excel(name = "会议备注")
    private String remarks;

    /** 会议封面（最大5MB） */
    @Schema(description = "会议封面（最大5MB）")
    @Excel(name = "会议封面", readConverterExp = "最=大5MB")
    private String cover;

    /** 业务领域类型 */
    @Schema(description = "业务领域类型")
    @Excel(name = "业务领域类型")
    private Integer businessFieldType;

    /** 业务属性 */
    @Schema(description = "业务属性")
    @Excel(name = "业务属性")
    private String businessProperties;

    /** 是否启用会议时长(1是，2否) */
    @Schema(description = "是否启用会议时长(1是，2否)")
    @Excel(name = "是否启用会议时长(1是，2否)")
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    @Schema(description = "会议时长单位分钟")
    @Excel(name = "会议时长单位分钟")
    private Integer durationTime;

    /** 最后会议id */
    @Schema(description = "最后会议id")
    @Excel(name = "最后会议id")
    private Long lastConferenceId;

    /** 资源模板id */
    @Schema(description = "资源模板id")
    @Excel(name = "资源模板id")
    private Integer resourceTemplateId;

    /** 企业唯一用户标识 */
    @Schema(description = "企业唯一用户标识")
    @Excel(name = "企业唯一用户标识")
    private String tencentUserid;

    /** 会议类型：
        0：预约会议
        1：快速会议
     */
    private Integer type;

    /** 用户的终端设备类型：
        0：PSTN
        1：PC
        2：Mac
        3：Android
        4：iOS
        5：Web
        6：iPad
        7：Android Pad
        8：小程序
        9：voip、sip 设备
        10：linux
        20：Rooms for Touch Windows
        21：Rooms for Touch MacOS
        22：Rooms for Touch Android
        30：Controller for Touch Windows
        32：Controller for Touch Android
        33：Controller for Touch iOS
         */
    private Integer instanceid;

    /** 会议媒体参数配置对象 */
    @Schema(description = "会议媒体参数配置对象")
    @Excel(name = "会议媒体参数配置对象")
    private String settings;

    /** 直播配置 */
    @Schema(description = "直播配置")
    @Excel(name = "直播配置")
    private String liveConfig;

    /** 会议的唯一标识 */
    @Schema(description = "会议的唯一标识")
    @Excel(name = "会议的唯一标识")
    private String meetingId;

    /** 默认值为0。
0：普通会议
1：周期性会议（周期性会议时 type 不能为快速会议，同一账号同时最多可预定50场周期性会议）
 */
    private Integer meetingType;

    /** 网络研讨会:0:一般会议，1：研讨会 */
    @Schema(description = "网络研讨会:0:一般会议，1：研讨会")
    @Excel(name = "网络研讨会:0:一般会议，1：研讨会")
    private Integer webinarType;

    /** 观众观看限制类型：
        0：公开
        1：报名
        2：密码
     */
    private Integer admissionType;

    /** 默认布局 */
    @Schema(description = "默认布局")
    @Excel(name = "默认布局")
    private String layoutJson;

    /** 会议背景 */
    @Schema(description = "会议背景")
    @Excel(name = "会议背景")
    private String backgrounds;


    private String templateName;

    private Integer duration;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }
    public void setCreateUserName(String createUserName) 
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setCallLegProfileId(String callLegProfileId) 
    {
        this.callLegProfileId = callLegProfileId;
    }

    public String getCallLegProfileId() 
    {
        return callLegProfileId;
    }
    public void setCallProfileId(String callProfileId) 
    {
        this.callProfileId = callProfileId;
    }

    public String getCallProfileId() 
    {
        return callProfileId;
    }
    public void setCallBrandingProfileId(String callBrandingProfileId) 
    {
        this.callBrandingProfileId = callBrandingProfileId;
    }

    public String getCallBrandingProfileId() 
    {
        return callBrandingProfileId;
    }
    public void setStreamUrl(String streamUrl) 
    {
        this.streamUrl = streamUrl;
    }

    public String getStreamUrl() 
    {
        return streamUrl;
    }
    public void setRecordingEnabled(Integer recordingEnabled) 
    {
        this.recordingEnabled = recordingEnabled;
    }

    public Integer getRecordingEnabled() 
    {
        return recordingEnabled;
    }
    public void setStreamingEnabled(Integer streamingEnabled) 
    {
        this.streamingEnabled = streamingEnabled;
    }

    public Integer getStreamingEnabled() 
    {
        return streamingEnabled;
    }
    public void setBandwidth(Integer bandwidth) 
    {
        this.bandwidth = bandwidth;
    }

    public Integer getBandwidth() 
    {
        return bandwidth;
    }
    public void setIsAutoCall(Integer isAutoCall) 
    {
        this.isAutoCall = isAutoCall;
    }

    public Integer getIsAutoCall() 
    {
        return isAutoCall;
    }
    public void setIsAutoMonitor(Integer isAutoMonitor) 
    {
        this.isAutoMonitor = isAutoMonitor;
    }

    public Integer getIsAutoMonitor() 
    {
        return isAutoMonitor;
    }
    public void setIsAutoCreateConferenceNumber(Integer isAutoCreateConferenceNumber) 
    {
        this.isAutoCreateConferenceNumber = isAutoCreateConferenceNumber;
    }

    public Integer getIsAutoCreateConferenceNumber() 
    {
        return isAutoCreateConferenceNumber;
    }
    public void setCreateType(Integer createType) 
    {
        this.createType = createType;
    }

    public Integer getCreateType() 
    {
        return createType;
    }
    public void setCascad(Integer cascad) 
    {
        this.cascad = cascad;
    }

    public Integer getCascad() 
    {
        return cascad;
    }
    public void setViewType(Integer viewType) 
    {
        this.viewType = viewType;
    }

    public Integer getViewType() 
    {
        return viewType;
    }
    public void setConferenceNumber(Long conferenceNumber) 
    {
        this.conferenceNumber = conferenceNumber;
    }

    public Long getConferenceNumber() 
    {
        return conferenceNumber;
    }
    public void setMasterParticipantId(Long masterParticipantId) 
    {
        this.masterParticipantId = masterParticipantId;
    }

    public Long getMasterParticipantId() 
    {
        return masterParticipantId;
    }
    public void setDefaultViewLayout(String defaultViewLayout) 
    {
        this.defaultViewLayout = defaultViewLayout;
    }

    public String getDefaultViewLayout() 
    {
        return defaultViewLayout;
    }
    public void setDefaultViewIsBroadcast(Integer defaultViewIsBroadcast) 
    {
        this.defaultViewIsBroadcast = defaultViewIsBroadcast;
    }

    public Integer getDefaultViewIsBroadcast() 
    {
        return defaultViewIsBroadcast;
    }
    public void setDefaultViewIsDisplaySelf(Integer defaultViewIsDisplaySelf) 
    {
        this.defaultViewIsDisplaySelf = defaultViewIsDisplaySelf;
    }

    public Integer getDefaultViewIsDisplaySelf() 
    {
        return defaultViewIsDisplaySelf;
    }
    public void setDefaultViewIsFill(Integer defaultViewIsFill) 
    {
        this.defaultViewIsFill = defaultViewIsFill;
    }

    public Integer getDefaultViewIsFill() 
    {
        return defaultViewIsFill;
    }
    public void setPollingInterval(Integer pollingInterval) 
    {
        this.pollingInterval = pollingInterval;
    }

    public Integer getPollingInterval() 
    {
        return pollingInterval;
    }
    public void setConferencePassword(String conferencePassword) 
    {
        this.conferencePassword = conferencePassword;
    }

    public String getConferencePassword() 
    {
        return conferencePassword;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }
    public void setCover(String cover) 
    {
        this.cover = cover;
    }

    public String getCover() 
    {
        return cover;
    }
    public void setBusinessFieldType(Integer businessFieldType) 
    {
        this.businessFieldType = businessFieldType;
    }

    public Integer getBusinessFieldType() 
    {
        return businessFieldType;
    }
    public void setBusinessProperties(String businessProperties) 
    {
        this.businessProperties = businessProperties;
    }

    public String getBusinessProperties() 
    {
        return businessProperties;
    }
    public void setDurationEnabled(Integer durationEnabled) 
    {
        this.durationEnabled = durationEnabled;
    }

    public Integer getDurationEnabled() 
    {
        return durationEnabled;
    }
    public void setDurationTime(Integer durationTime) 
    {
        this.durationTime = durationTime;
    }

    public Integer getDurationTime() 
    {
        return durationTime;
    }
    public void setLastConferenceId(Long lastConferenceId) 
    {
        this.lastConferenceId = lastConferenceId;
    }

    public Long getLastConferenceId() 
    {
        return lastConferenceId;
    }
    public void setResourceTemplateId(Integer resourceTemplateId) 
    {
        this.resourceTemplateId = resourceTemplateId;
    }

    public Integer getResourceTemplateId() 
    {
        return resourceTemplateId;
    }
    public void setTencentUserid(String tencentUserid) 
    {
        this.tencentUserid = tencentUserid;
    }

    public String getTencentUserid() 
    {
        return tencentUserid;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setInstanceid(Integer instanceid) 
    {
        this.instanceid = instanceid;
    }

    public Integer getInstanceid() 
    {
        return instanceid;
    }
    public void setSettings(String settings) 
    {
        this.settings = settings;
    }

    public String getSettings() 
    {
        return settings;
    }
    public void setLiveConfig(String liveConfig) 
    {
        this.liveConfig = liveConfig;
    }

    public String getLiveConfig() 
    {
        return liveConfig;
    }
    public void setMeetingId(String meetingId) 
    {
        this.meetingId = meetingId;
    }

    public String getMeetingId() 
    {
        return meetingId;
    }
    public void setMeetingType(Integer meetingType) 
    {
        this.meetingType = meetingType;
    }

    public Integer getMeetingType() 
    {
        return meetingType;
    }
    public void setWebinarType(Integer webinarType) 
    {
        this.webinarType = webinarType;
    }

    public Integer getWebinarType() 
    {
        return webinarType;
    }
    public void setAdmissionType(Integer admissionType) 
    {
        this.admissionType = admissionType;
    }

    public Integer getAdmissionType() 
    {
        return admissionType;
    }
    public void setLayoutJson(String layoutJson) 
    {
        this.layoutJson = layoutJson;
    }

    public String getLayoutJson() 
    {
        return layoutJson;
    }
    public void setBackgrounds(String backgrounds) 
    {
        this.backgrounds = backgrounds;
    }

    public String getBackgrounds() 
    {
        return backgrounds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("subject", getSubject())
            .append("deptId", getDeptId())
            .append("callLegProfileId", getCallLegProfileId())
            .append("callProfileId", getCallProfileId())
            .append("callBrandingProfileId", getCallBrandingProfileId())
            .append("streamUrl", getStreamUrl())
            .append("recordingEnabled", getRecordingEnabled())
            .append("streamingEnabled", getStreamingEnabled())
            .append("bandwidth", getBandwidth())
            .append("isAutoCall", getIsAutoCall())
            .append("isAutoMonitor", getIsAutoMonitor())
            .append("isAutoCreateConferenceNumber", getIsAutoCreateConferenceNumber())
            .append("createType", getCreateType())
            .append("cascad", getCascad())
            .append("viewType", getViewType())
            .append("conferenceNumber", getConferenceNumber())
            .append("masterParticipantId", getMasterParticipantId())
            .append("defaultViewLayout", getDefaultViewLayout())
            .append("defaultViewIsBroadcast", getDefaultViewIsBroadcast())
            .append("defaultViewIsDisplaySelf", getDefaultViewIsDisplaySelf())
            .append("defaultViewIsFill", getDefaultViewIsFill())
            .append("pollingInterval", getPollingInterval())
            .append("conferencePassword", getConferencePassword())
            .append("remarks", getRemarks())
            .append("cover", getCover())
            .append("businessFieldType", getBusinessFieldType())
            .append("businessProperties", getBusinessProperties())
            .append("durationEnabled", getDurationEnabled())
            .append("durationTime", getDurationTime())
            .append("lastConferenceId", getLastConferenceId())
            .append("resourceTemplateId", getResourceTemplateId())
            .append("tencentUserid", getTencentUserid())
            .append("type", getType())
            .append("instanceid", getInstanceid())
            .append("settings", getSettings())
            .append("liveConfig", getLiveConfig())
            .append("meetingId", getMeetingId())
            .append("meetingType", getMeetingType())
            .append("webinarType", getWebinarType())
            .append("admissionType", getAdmissionType())
            .append("layoutJson", getLayoutJson())
            .append("backgrounds", getBackgrounds())
            .toString();
    }
}
