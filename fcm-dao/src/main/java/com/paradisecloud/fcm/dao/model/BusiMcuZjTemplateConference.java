package com.paradisecloud.fcm.dao.model;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 紫荆MCU会议模板对象 busi_mcu_zj_template_conference
 * 
 * @author lilinhai
 * @date 2021-10-25
 */
@Schema(description = "紫荆MCU会议模板")
public class BusiMcuZjTemplateConference extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Schema(description = "主键ID")
    private Long id;

    /** 模板会议名 */
    @Schema(description = "模板会议名")
    @Excel(name = "模板会议名")
    private String name;

    /** 创建者id */
    @Schema(description = "创建者id")
    @Excel(name = "创建者id")
    private Long createUserId;

    /** 创建者用户名 */
    @Schema(description = "创建者用户名")
    @Excel(name = "创建者用户名")
    private String createUserName;

    /** 部门ID */
    @Schema(description = "部门ID")
    @Excel(name = "部门ID")
    private Long deptId;

    /** 入会方案配置ID（关联MCU里面的入会方案记录ID，会控端不存） */
    @Schema(description = "入会方案配置ID（关联MCU里面的入会方案记录ID，会控端不存）")
    @Excel(name = "入会方案配置ID", readConverterExp = "关=联MCU里面的入会方案记录ID，会控端不存")
    private String callLegProfileId;

    /** 带宽1,2,3,4,5,6M */
    @Schema(description = "带宽1,2,3,4,5,6M")
    @Excel(name = "带宽1,2,3,4,5,6M")
    private Integer bandwidth;

    /** 是否自动呼叫与会者：1是，2否 */
    @Schema(description = "是否自动呼叫与会者：1是，2否")
    @Excel(name = "是否自动呼叫与会者：1是，2否")
    private Integer isAutoCall;

    /** 模板绑定的会议号 */
    @Schema(description = "模板绑定的会议号")
    @Excel(name = "模板绑定的会议号")
    private Long conferenceNumber;

    /** 虚拟会议室参数 */
    @Schema(description = "虚拟会议室参数")
    @Excel(name = "虚拟会议室参数")
    private String callProfileId;

    /** 呼入标识参数 */
    @Schema(description = "呼入标识参数")
    @Excel(name = "呼入标识参数")
    private String callBrandingProfileId;

    /** 模板会议类型：1级联，2普通 */
    @Schema(description = "模板会议类型：1级联，2普通")
    @Excel(name = "模板会议类型：1级联，2普通")
    private Integer type;
    
    /** 视图类型（1卡片，2列表） */
    @Schema(description = "视图类型（1卡片，2列表）")
    @Excel(name = "视图类型", readConverterExp = "1=卡片，2列表")
    private Integer viewType;

    /** 是否自动监听会议：1是，2否 */
    @Schema(description = "是否自动监听会议：1是，2否")
    @Excel(name = "是否自动监听会议：1是，2否")
    private Integer isAutoMonitor;

    /** 直播地址 */
    @Schema(description = "直播地址")
    @Excel(name = "直播地址")
    private String streamUrl;

    /** 是否自动创建会议号 */
    @Schema(description = "是否自动创建会议号")
    @Excel(name = "是否自动创建会议号")
    private Integer isAutoCreateConferenceNumber;

    /** 是否启用录制 */
    @Schema(description = "是否启用录制")
    @Excel(name = "是否启用录制")
    private Integer recordingEnabled;

    /** 创建类型：1自动，2手动 */
    @Schema(description = "创建类型：1自动，2手动")
    @Excel(name = "创建类型：1自动，2手动")
    private Integer createType;

    /** 开启直播：1是，2否 */
    @Schema(description = "开启直播：1是，2否")
    @Excel(name = "开启直播：1是，2否")
    private Integer streamingEnabled;

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

    /** 默认视图是否显示自己 */
    @Schema(description = "默认视图是否显示自己")
    @Excel(name = "默认视图是否显示自己")
    private Integer defaultViewIsDisplaySelf;

    /** 默认视图是否补位 */
    @Schema(description = "默认视图是否补位")
    @Excel(name = "默认视图是否补位")
    private Integer defaultViewIsFill;

    /** 轮询时间间隔 */
    @Schema(description = "轮询时间间隔")
    @Excel(name = "轮询时间间隔")
    private Integer pollingInterval;

    /** 会议密码 */
    @Schema(description = "会议密码")
    @Excel(name = "会议密码")
    private String conferencePassword;

    /** 业务领域类型 */
    @Schema(description = "业务领域类型")
    @Excel(name = "业务领域类型")
    private Integer businessFieldType;

    /** 会议备注 */
    @Schema(description = "会议备注")
    @Excel(name = "会议备注")
    private String remarks;

    /** 业务属性 */
    @Schema(description = "业务属性")
    @Excel(name = "业务属性")
    private Map<String, Object> businessProperties;

    /** 会议封面 */
    @Schema(description = "会议封面")
    @Excel(name = "会议封面")
    private String cover;

    /** 是否启用会议时长 */
    @Schema(description = "是否启用会议时长")
    @Excel(name = "是否启用会议时长")
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    @Schema(description = "会议时长单位分钟")
    @Excel(name = "会议时长单位分钟")
    private Integer durationTime;

    /** 模板绑定的会议控制密码 */
    @Schema(description = "模板绑定的会议控制密码")
    @Excel(name = "模板绑定的会议控制密码")
    private String conferenceCtrlPassword;

    /** 最后会议id */
    @Schema(description = "最后会议id")
    @Excel(name = "最后会议id")
    private Long lastConferenceId;

    /** 资源模板ID */
    @Schema(description = "资源模板ID")
    @Excel(name = "资源模板ID")
    private Integer resourceTemplateId;

    /** 租户局号 */
    @Schema(description = "租户局号")
    @Excel(name = "租户局号")
    private String tenantId;

    /** 静音类型 */
    @Schema(description = "静音类型")
    @Excel(name = "静音类型")
    private Integer muteType;

    /** 是否自动创建直播URL 1自动，2手动 */
    private Integer isAutoCreateStreamUrl;

    /** 观众默认视图布局类型 */
    @Schema(description = "观众默认视图布局类型")
    @Excel(name = "观众默认视图布局类型")
    private String defaultViewLayoutGuest;

    /** 观众默认视图是否补位 */
    @Schema(description = "观众默认视图是否补位")
    @Excel(name = "观众默认视图是否补位")
    private Integer defaultViewIsFillGuest;

    /** 观众轮询时间间隔 */
    @Schema(description = "观众轮询时间间隔")
    @Excel(name = "观众轮询时间间隔")
    private Integer pollingIntervalGuest;

    /** 主持人终端id */
    @Schema(description = "主持人终端id")
    @Excel(name = "主持人终端id")
    private Long presenter;

    /** 上级ID */
    @Schema(description = "上级ID")
    @Excel(name = "上级ID")
    private Long upCascadeId;

    /** 上级MCU类型 */
    @Schema(description = "上级MCU类型")
    @Excel(name = "上级MCU类型")
    private String upCascadeMcuType;

    /** 级联类型 */
    @Schema(description = "级联类型")
    @Excel(name = "级联类型")
    private Integer upCascadeType;

    /** 级联索引 */
    @Schema(description = "级联索引")
    @Excel(name = "级联索引")
    private Integer upCascadeIndex;

    public Integer getDurationEnabled() {
        return durationEnabled;
    }

    public void setDurationEnabled(Integer durationEnabled) {
        this.durationEnabled = durationEnabled;
    }

    public Integer getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(Integer durationTime) {
        this.durationTime = durationTime;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
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
    public void setConferenceNumber(Long conferenceNumber) 
    {
        this.conferenceNumber = conferenceNumber;
    }

    public Long getConferenceNumber() 
    {
        return conferenceNumber;
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
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    
    /**
     * <p>Get Method   :   viewType Integer</p>
     * @return viewType
     */
    public Integer getViewType()
    {
        return viewType;
    }

    /**
     * <p>Set Method   :   viewType Integer</p>
     * @param viewType
     */
    public void setViewType(Integer viewType)
    {
        this.viewType = viewType;
    }

    public void setIsAutoMonitor(Integer isAutoMonitor) 
    {
        this.isAutoMonitor = isAutoMonitor;
    }

    public Integer getIsAutoMonitor() 
    {
        return isAutoMonitor;
    }
    public void setStreamUrl(String streamUrl) 
    {
        this.streamUrl = streamUrl;
    }

    public String getStreamUrl() 
    {
        return streamUrl;
    }
    public void setIsAutoCreateConferenceNumber(Integer isAutoCreateConferenceNumber) 
    {
        this.isAutoCreateConferenceNumber = isAutoCreateConferenceNumber;
    }

    public Integer getIsAutoCreateConferenceNumber() 
    {
        return isAutoCreateConferenceNumber;
    }
    public void setRecordingEnabled(Integer recordingEnabled) 
    {
        this.recordingEnabled = recordingEnabled;
    }

    public Integer getRecordingEnabled() 
    {
        return recordingEnabled;
    }
    public void setCreateType(Integer createType) 
    {
        this.createType = createType;
    }

    public Integer getCreateType() 
    {
        return createType;
    }
    public void setStreamingEnabled(Integer streamingEnabled) 
    {
        this.streamingEnabled = streamingEnabled;
    }

    public Integer getStreamingEnabled() 
    {
        return streamingEnabled;
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
    public void setBusinessFieldType(Integer businessFieldType) 
    {
        this.businessFieldType = businessFieldType;
    }

    public Integer getBusinessFieldType() 
    {
        return businessFieldType;
    }
    public void setRemarks(String remarks) 
    {
        this.remarks = remarks;
    }

    public String getRemarks() 
    {
        return remarks;
    }

    /**
     * <p>Get Method   :   businessProperties Map<String,Object></p>
     * @return businessProperties
     */
    public Map<String, Object> getBusinessProperties()
    {
        return businessProperties;
    }

    /**
     * <p>Set Method   :   businessProperties Map<String,Object></p>
     * @param businessProperties
     */
    public void setBusinessProperties(Map<String, Object> businessProperties)
    {
        this.businessProperties = businessProperties;
    }

    public void setCover(String cover) 
    {
        this.cover = cover;
    }

    public String getCover() 
    {
        return cover;
    }

    public String getConferenceCtrlPassword() {
        return conferenceCtrlPassword;
    }

    public void setConferenceCtrlPassword(String conferenceCtrlPassword) {
        this.conferenceCtrlPassword = conferenceCtrlPassword;
    }

    public Long getLastConferenceId() {
        return lastConferenceId;
    }

    public void setLastConferenceId(Long lastConferenceId) {
        this.lastConferenceId = lastConferenceId;
    }

    public Integer getResourceTemplateId() {
        return resourceTemplateId;
    }

    public void setResourceTemplateId(Integer resourceTemplateId) {
        this.resourceTemplateId = resourceTemplateId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getMuteType() {
        return muteType;
    }

    public void setMuteType(Integer muteType) {
        this.muteType = muteType;
    }

    public Integer getIsAutoCreateStreamUrl() {
        return isAutoCreateStreamUrl;
    }

    public void setIsAutoCreateStreamUrl(Integer isAutoCreateStreamUrl) {
        this.isAutoCreateStreamUrl = isAutoCreateStreamUrl;
    }

    public String getDefaultViewLayoutGuest() {
        return defaultViewLayoutGuest;
    }

    public void setDefaultViewLayoutGuest(String defaultViewLayoutGuest) {
        this.defaultViewLayoutGuest = defaultViewLayoutGuest;
    }

    public Integer getDefaultViewIsFillGuest() {
        return defaultViewIsFillGuest;
    }

    public void setDefaultViewIsFillGuest(Integer defaultViewIsFillGuest) {
        this.defaultViewIsFillGuest = defaultViewIsFillGuest;
    }

    public Integer getPollingIntervalGuest() {
        return pollingIntervalGuest;
    }

    public void setPollingIntervalGuest(Integer pollingIntervalGuest) {
        this.pollingIntervalGuest = pollingIntervalGuest;
    }

    public Long getPresenter() {
        return presenter;
    }

    public void setPresenter(Long presenter) {
        this.presenter = presenter;
    }

    public Long getUpCascadeId() {
        return upCascadeId;
    }

    public void setUpCascadeId(Long upCascadeId) {
        this.upCascadeId = upCascadeId;
    }

    public String getUpCascadeMcuType() {
        return upCascadeMcuType;
    }

    public void setUpCascadeMcuType(String upCascadeMcuType) {
        this.upCascadeMcuType = upCascadeMcuType;
    }

    public Integer getUpCascadeType() {
        return upCascadeType;
    }

    public void setUpCascadeType(Integer upCascadeType) {
        this.upCascadeType = upCascadeType;
    }

    public Integer getUpCascadeIndex() {
        return upCascadeIndex;
    }

    public void setUpCascadeIndex(Integer upCascadeIndex) {
        this.upCascadeIndex = upCascadeIndex;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("name", getName())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("deptId", getDeptId())
            .append("callLegProfileId", getCallLegProfileId())
            .append("bandwidth", getBandwidth())
            .append("isAutoCall", getIsAutoCall())
            .append("conferenceNumber", getConferenceNumber())
            .append("callProfileId", getCallProfileId())
            .append("callBrandingProfileId", getCallBrandingProfileId())
            .append("type", getType())
            .append("viewType", getViewType())
            .append("isAutoMonitor", getIsAutoMonitor())
            .append("streamUrl", getStreamUrl())
            .append("isAutoCreateConferenceNumber", getIsAutoCreateConferenceNumber())
            .append("recordingEnabled", getRecordingEnabled())
            .append("createType", getCreateType())
            .append("streamingEnabled", getStreamingEnabled())
            .append("masterParticipantId", getMasterParticipantId())
            .append("defaultViewLayout", getDefaultViewLayout())
            .append("defaultViewIsBroadcast", getDefaultViewIsBroadcast())
            .append("defaultViewIsDisplaySelf", getDefaultViewIsDisplaySelf())
            .append("defaultViewIsFill", getDefaultViewIsFill())
            .append("pollingInterval", getPollingInterval())
            .append("conferencePassword", getConferencePassword())
            .append("businessFieldType", getBusinessFieldType())
            .append("remarks", getRemarks())
            .append("businessProperties", getBusinessProperties())
            .append("cover", getCover())
            .append("durationEnabled", getDurationTime())
            .append("durationTime", getDurationTime())
            .append("conferenceCtrlPassword", getConferenceCtrlPassword())
            .append("lastConferenceId", getLastConferenceId())
            .append("resourceTemplateId", getResourceTemplateId())
            .append("tenantId", getTenantId())
            .append("muteType", getMuteType())
            .append("isAutoCreateStreamUrl", getIsAutoCreateStreamUrl())
            .append("defaultViewLayoutGuest", getDefaultViewLayoutGuest())
            .append("defaultViewIsFillGuest", getDefaultViewIsFillGuest())
            .append("pollingIntervalGuest", getPollingIntervalGuest())
            .append("presenter", getPresenter())
            .append("upCascadeId", getUpCascadeId())
            .append("upCascadeMcuType", getUpCascadeMcuType())
            .append("upCascadeType", getUpCascadeType())
            .append("upCascadeIndex", getUpCascadeIndex())
            .toString();
    }
}