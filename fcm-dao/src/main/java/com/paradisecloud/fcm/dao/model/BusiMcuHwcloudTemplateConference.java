package com.paradisecloud.fcm.dao.model;

import com.paradisecloud.common.annotation.Excel;
import com.paradisecloud.common.core.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

/**
 * SMC3.0MCU会议模板对象 busi_mcu_smc3_template_conference
 * 
 * @author lilinhai
 * @date 2023-09-19
 */
@Schema(description = "SMC3.0MCU会议模板")
public class BusiMcuHwcloudTemplateConference extends BaseEntity
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
    private String name;

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
    private Integer type;

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
    private Map<String,Object> businessProperties;

    /** 是否启用会议时长(1是，2否) */
    @Schema(description = "是否启用会议时长(1是，2否)")
    @Excel(name = "是否启用会议时长(1是，2否)")
    private Integer durationEnabled;

    /** 会议时长单位分钟 */
    @Schema(description = "会议时长单位分钟")
    @Excel(name = "会议时长单位分钟")
    private Integer durationTime;

    /** 会议控制密码 */
    @Schema(description = "会议控制密码")
    @Excel(name = "会议控制密码")
    private String conferenceCtrlPassword;

    /** 最后会议id */
    @Schema(description = "最后会议id")
    @Excel(name = "最后会议id")
    private String lastConferenceId;

    /** 静音类型 0:不静音 1:静音 */
    @Schema(description = "静音类型 0:不静音 1:静音")
    @Excel(name = "静音类型 0:不静音 1:静音")
    private Integer muteType;

    /** 是否自动创建直播URL：1是，2否 */
    @Schema(description = "是否自动创建直播URL：1是，2否")
    @Excel(name = "是否自动创建直播URL：1是，2否")
    private Integer isAutoCreateStreamUrl;

    /** 主持人终端id */
    @Schema(description = "主持人终端id")
    @Excel(name = "主持人终端id")
    private Integer presenter;

    /** 视频协议 */
    @Schema(description = "视频协议")
    @Excel(name = "视频协议")
    private String videoProtocol;

    /** 分辨率 */
    @Schema(description = "分辨率")
    @Excel(name = "分辨率")
    private String videoResolution;

    /** 主席密码 */
    @Schema(description = "主席密码")
    @Excel(name = "主席密码")
    private String chairmanPassword;

    /** 嘉宾密码 */
    @Schema(description = "嘉宾密码")
    @Excel(name = "嘉宾密码")
    private String guestPassword;

    /** SMC3模板ID */
    @Schema(description = "SMC3模板ID")
    @Excel(name = "SMC3模板ID")
    private String smcTemplateId;

    /** 会议类型 */
    @Schema(description = "会议类型")
    @Excel(name = "会议类型")
    private String confType;

    /** 会议类型 */
    @Schema(description = "预设画面")
    @Excel(name = "预设画面")
    private String presenceMultiPic;

    /** 最大入会数 */
    @Schema(description = "最大入会数")
    @Excel(name = "最大入会数")
    private Integer maxParticipantNum;

    /** 上级会议ID */
    @Schema(description = "上级会议ID")
    @Excel(name = "上级会议ID")
    private String upCascadeConferenceId;

    /** 级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板 */
    @Schema(description = "级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板")
    @Excel(name = "级联类型 0:自动生成模板 1:手动选择未开会模板 2:手动选择已开会模板")
    private Integer upCascadeType;

    /** smc2会议ID */
    @Schema(description = "smc2会议ID")
    @Excel(name = "smc2会议ID")
    private String confId;

    /** 上级ID */
    @Schema(description = "上级ID")
    @Excel(name = "上级ID")
    private Long upCascadeId;

    /** 上级MCU类型 */
    @Schema(description = "上级MCU类型")
    @Excel(name = "上级MCU类型")
    private String upCascadeMcuType;

    /** 级联索引 */
    @Schema(description = "级联索引")
    @Excel(name = "级联索引")
    private Integer upCascadeIndex;

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
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
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
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
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
    public void setBusinessProperties(Map<String,Object> businessProperties)
    {
        this.businessProperties = businessProperties;
    }

    public Map<String,Object> getBusinessProperties()
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
    public void setConferenceCtrlPassword(String conferenceCtrlPassword) 
    {
        this.conferenceCtrlPassword = conferenceCtrlPassword;
    }

    public String getConferenceCtrlPassword() 
    {
        return conferenceCtrlPassword;
    }
    public void setLastConferenceId(String lastConferenceId)
    {
        this.lastConferenceId = lastConferenceId;
    }

    public String getLastConferenceId()
    {
        return lastConferenceId;
    }
    public void setMuteType(Integer muteType) 
    {
        this.muteType = muteType;
    }

    public Integer getMuteType() 
    {
        return muteType;
    }
    public void setIsAutoCreateStreamUrl(Integer isAutoCreateStreamUrl) 
    {
        this.isAutoCreateStreamUrl = isAutoCreateStreamUrl;
    }

    public Integer getIsAutoCreateStreamUrl() 
    {
        return isAutoCreateStreamUrl;
    }
    public void setPresenter(Integer presenter) 
    {
        this.presenter = presenter;
    }

    public Integer getPresenter() 
    {
        return presenter;
    }
    public void setVideoProtocol(String videoProtocol) 
    {
        this.videoProtocol = videoProtocol;
    }

    public String getVideoProtocol() 
    {
        return videoProtocol;
    }
    public void setVideoResolution(String videoResolution) 
    {
        this.videoResolution = videoResolution;
    }

    public String getVideoResolution() 
    {
        return videoResolution;
    }
    public void setChairmanPassword(String chairmanPassword) 
    {
        this.chairmanPassword = chairmanPassword;
    }

    public String getChairmanPassword() 
    {
        return chairmanPassword;
    }
    public void setGuestPassword(String guestPassword) 
    {
        this.guestPassword = guestPassword;
    }

    public String getGuestPassword() 
    {
        return guestPassword;
    }
    public void setSmcTemplateId(String smcTemplateId) 
    {
        this.smcTemplateId = smcTemplateId;
    }

    public String getSmcTemplateId() 
    {
        return smcTemplateId;
    }
    public void setConfType(String confType) 
    {
        this.confType = confType;
    }

    public String getConfType() 
    {
        return confType;
    }
    public void setMaxParticipantNum(Integer maxParticipantNum) 
    {
        this.maxParticipantNum = maxParticipantNum;
    }

    public Integer getMaxParticipantNum() 
    {
        return maxParticipantNum;
    }
    public void setUpCascadeConferenceId(String upCascadeConferenceId) 
    {
        this.upCascadeConferenceId = upCascadeConferenceId;
    }

    public String getUpCascadeConferenceId() 
    {
        return upCascadeConferenceId;
    }
    public void setUpCascadeType(Integer upCascadeType) 
    {
        this.upCascadeType = upCascadeType;
    }

    public Integer getUpCascadeType() 
    {
        return upCascadeType;
    }

    public String getPresenceMultiPic() {
        return presenceMultiPic;
    }

    public void setPresenceMultiPic(String presenceMultiPic) {
        this.presenceMultiPic = presenceMultiPic;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
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
                .append("createUserId", getCreateUserId())
                .append("createUserName", getCreateUserName())
                .append("name", getName())
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
                .append("type", getType())
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
                .append("conferenceCtrlPassword", getConferenceCtrlPassword())
                .append("lastConferenceId", getLastConferenceId())
                .append("muteType", getMuteType())
                .append("isAutoCreateStreamUrl", getIsAutoCreateStreamUrl())
                .append("presenter", getPresenter())
                .append("videoProtocol", getVideoProtocol())
                .append("videoResolution", getVideoResolution())
                .append("chairmanPassword", getChairmanPassword())
                .append("guestPassword", getGuestPassword())
                .append("smcTemplateId", getSmcTemplateId())
                .append("confType", getConfType())
                .append("maxParticipantNum", getMaxParticipantNum())
                .append("upCascadeConferenceId", getUpCascadeConferenceId())
                .append("upCascadeType", getUpCascadeType())
                .append("presenceMultiPic", getPresenceMultiPic())
                .append("confId", getConfId())
                .append("upCascadeId", getUpCascadeId())
                .append("upCascadeMcuType", getUpCascadeMcuType())
                .toString();
    }
}
