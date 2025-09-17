package com.paradisecloud.fcm.dao.model;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.paradisecloud.common.annotation.Excel;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 入会设置对象 busi_terminal_meeting_join_settings
 * 
 * @author lilinhai
 * @date 2021-07-09
 */
@Schema(description = "入会设置")
public class BusiTerminalMeetingJoinSettings extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Schema(description = "$column.columnComment")
    private Long id;

    /** 主流分辨率:unrestricted|max1080p30|max720p30|max480p30 */
    @Schema(description = "主流分辨率:unrestricted|max1080p30|max720p30|max480p30")
    @Excel(name = "主流分辨率:unrestricted|max1080p30|max720p30|max480p30")
    private String qualityMain;

    /** 辅流分辨率:unrestricted|max1080p30|max720p5 */
    @Schema(description = "辅流分辨率:unrestricted|max1080p30|max720p5")
    @Excel(name = "辅流分辨率:unrestricted|max1080p30|max720p5")
    private String qualityPresentation;

    /** 入会带宽:512K,1M,1.5M,2M,3M,4M,6M，默认2M */
    @Schema(description = "入会带宽:512K,1M,1.5M,2M,3M,4M,6M，默认2M")
    @Excel(name = "入会带宽:512K,1M,1.5M,2M,3M,4M,6M，默认2M")
    private BigDecimal bandwidth;

    /** 允许双流：值: true-1|false-2  发送双流:终端是否可以发送双流，默认开启。 */
    @Schema(description = "允许双流：值: true-1|false-2  发送双流:终端是否可以发送双流，默认开启。")
    @Excel(name = "允许双流：值: true-1|false-2  发送双流:终端是否可以发送双流，默认开启。")
    private Integer presentationContributionAllowed;

    /** 接收双流    值:true-1|false-2  此终端是否可以观看分享的双流内容 默认开启true */
    @Schema(description = "接收双流    值:true-1|false-2  此终端是否可以观看分享的双流内容 默认开启true")
    @Excel(name = "接收双流    值:true-1|false-2  此终端是否可以观看分享的双流内容 默认开启true")
    private Integer presentationViewingAllowed;

    /** 双流模式 默认:dualStream     值: singleStream|dualStream    参数说明: singleStream单通道。dualStream双通道 */
    @Schema(description = "双流模式 默认:dualStream     值: singleStream|dualStream    参数说明: singleStream单通道。dualStream双通道")
    @Excel(name = "双流模式 默认:dualStream     值: singleStream|dualStream    参数说明: singleStream单通道。dualStream双通道")
    private String presentationDisplayMode;

    /** SIP加密   默认:optional                值: optional|required|prohibited 参数说明: optional可选,required必须,prohibited禁止 */
    @Schema(description = "SIP加密   默认:optional                值: optional|required|prohibited 参数说明: optional可选,required必须,prohibited禁止")
    @Excel(name = "SIP加密   默认:optional                值: optional|required|prohibited 参数说明: optional可选,required必须,prohibited禁止")
    private String sipMediaEncryption;

    /** 入会布局   默认:不设置                    值: allEqual|speakerOnly|telepresence|stacked|allEqualQuarters|allEqualNinths|allEqualSixteenths| allEqualTwentyFifths|onePlusFive|onePlusSeven|onePlusNine| automatic|onePlusN */
    @Schema(description = "入会布局   默认:不设置                    值: allEqual|speakerOnly|telepresence|stacked|allEqualQuarters|allEqualNinths|allEqualSixteenths| allEqualTwentyFifths|onePlusFive|onePlusSeven|onePlusNine| automatic|onePlusN")
    @Excel(name = "入会布局   默认:不设置                    值: allEqual|speakerOnly|telepresence|stacked|allEqualQuarters|allEqualNinths|allEqualSixteenths| allEqualTwentyFifths|onePlusFive|onePlusSeven|onePlusNine| automatic|onePlusN")
    private String defaultLayout;

    /** BFCP模式      默认:serverOnly  值:serverOnly|serverAndClient 模式:客户端模式，服务器模式 */
    @Schema(description = "BFCP模式      默认:serverOnly  值:serverOnly|serverAndClient 模式:客户端模式，服务器模式")
    @Excel(name = "BFCP模式      默认:serverOnly  值:serverOnly|serverAndClient 模式:客户端模式，服务器模式")
    private String bfcpMode;

    /** 关闭扬声器   默认:false         值: true|false */
    @Schema(description = "关闭扬声器   默认:false         值: true|false")
    @Excel(name = "关闭扬声器   默认:false         值: true|false")
    private Integer rxAudioMute;

    /** 入会静音      默认不设置         值: true|false */
    @Schema(description = "入会静音      默认不设置         值: true|false")
    @Excel(name = "入会静音      默认不设置         值: true|false")
    private Integer txAudioMute;

    /** 发送视频      默认:不设置          值: true|false */
    @Schema(description = "发送视频      默认:不设置          值: true|false")
    @Excel(name = "发送视频      默认:不设置          值: true|false")
    private Integer rxVideoMute;

    /** 接收视频      默认:不设置          值: true|false */
    @Schema(description = "接收视频      默认:不设置          值: true|false")
    @Excel(name = "接收视频      默认:不设置          值: true|false")
    private Integer txVideoMute;

    /** 自动音频增益    默认：不设置      值: unset|disabled|agc */
    @Schema(description = "自动音频增益    默认：不设置      值: unset|disabled|agc")
    @Excel(name = "自动音频增益    默认：不设置      值: unset|disabled|agc")
    private String audioGainMode;

    /** 显示入会名称  默认:false     值: true|false */
    @Schema(description = "显示入会名称  默认:false     值: true|false")
    @Excel(name = "显示入会名称  默认:false     值: true|false")
    private Integer participantLabels;

    /** DTMF字符 0至9,*和#   默认:不设置 值: 无 */
    @Schema(description = "DTMF字符 0至9,*和#   默认:不设置 值: 无")
    @Excel(name = "DTMF字符 0至9,*和#   默认:不设置 值: 无")
    private String dtmfSequence;

    /** 开/关语音应答     默认:不设置  值: true|false */
    @Schema(description = "开/关语音应答     默认:不设置  值: true|false")
    @Excel(name = "开/关语音应答     默认:不设置  值: true|false")
    private Integer deactivated;

    /** 挂断呼叫    默认:不设置       值: true|false */
    @Schema(description = "挂断呼叫    默认:不设置       值: true|false")
    @Excel(name = "挂断呼叫    默认:不设置       值: true|false")
    private Integer endCallAllowed;

    /** 挂断其他参会者 默认:不设置   值: true|false */
    @Schema(description = "挂断其他参会者 默认:不设置   值: true|false")
    @Excel(name = "挂断其他参会者 默认:不设置   值: true|false")
    private Integer disconnectOthersAllowed;

    /** 邀请其他参会者 默认:不设置  值: true|false */
    @Schema(description = "邀请其他参会者 默认:不设置  值: true|false")
    @Excel(name = "邀请其他参会者 默认:不设置  值: true|false")
    private Integer addParticipantAllowed;

    /** 静音其他参会者 默认:不设置  值: true|false */
    @Schema(description = "静音其他参会者 默认:不设置  值: true|false")
    @Excel(name = "静音其他参会者 默认:不设置  值: true|false")
    private Integer muteOthersAllowed;

    /** 关闭其他参会者视频 默认:不设置  值: true|false */
    @Schema(description = "关闭其他参会者视频 默认:不设置  值: true|false")
    @Excel(name = "关闭其他参会者视频 默认:不设置  值: true|false")
    private Integer videoMuteOthersAllowed;

    /** 静音自己 默认:不设置  值: true|false */
    @Schema(description = "静音自己 默认:不设置  值: true|false")
    @Excel(name = "静音自己 默认:不设置  值: true|false")
    private Integer muteSelfAllowed;

    /** 关闭自己视频 默认:不设置  值: true|false */
    @Schema(description = "关闭自己视频 默认:不设置  值: true|false")
    @Excel(name = "关闭自己视频 默认:不设置  值: true|false")
    private Integer videoMuteSelfAllowed;

    /** 更改布局 默认:不设置  值: true|false */
    @Schema(description = "更改布局 默认:不设置  值: true|false")
    @Excel(name = "更改布局 默认:不设置  值: true|false")
    private Integer changeLayoutAllowed;

    /** 允许静音自己。默认:不设置  值: true|false */
    @Schema(description = "允许静音自己。默认:不设置  值: true|false")
    @Excel(name = "允许静音自己。默认:不设置  值: true|false")
    private Integer allowAllMuteSelfAllowed;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setQualityMain(String qualityMain) 
    {
        this.qualityMain = qualityMain;
    }

    public String getQualityMain() 
    {
        return qualityMain;
    }
    public void setQualityPresentation(String qualityPresentation) 
    {
        this.qualityPresentation = qualityPresentation;
    }

    public String getQualityPresentation() 
    {
        return qualityPresentation;
    }
    public void setBandwidth(BigDecimal bandwidth) 
    {
        this.bandwidth = bandwidth;
    }

    public BigDecimal getBandwidth() 
    {
        return bandwidth;
    }
    public void setPresentationContributionAllowed(Integer presentationContributionAllowed) 
    {
        this.presentationContributionAllowed = presentationContributionAllowed;
    }

    public Integer getPresentationContributionAllowed() 
    {
        return presentationContributionAllowed;
    }
    public void setPresentationViewingAllowed(Integer presentationViewingAllowed) 
    {
        this.presentationViewingAllowed = presentationViewingAllowed;
    }

    public Integer getPresentationViewingAllowed() 
    {
        return presentationViewingAllowed;
    }
    public void setPresentationDisplayMode(String presentationDisplayMode) 
    {
        this.presentationDisplayMode = presentationDisplayMode;
    }

    public String getPresentationDisplayMode() 
    {
        return presentationDisplayMode;
    }
    public void setSipMediaEncryption(String sipMediaEncryption) 
    {
        this.sipMediaEncryption = sipMediaEncryption;
    }

    public String getSipMediaEncryption() 
    {
        return sipMediaEncryption;
    }
    public void setDefaultLayout(String defaultLayout) 
    {
        this.defaultLayout = defaultLayout;
    }

    public String getDefaultLayout() 
    {
        return defaultLayout;
    }
    public void setBfcpMode(String bfcpMode) 
    {
        this.bfcpMode = bfcpMode;
    }

    public String getBfcpMode() 
    {
        return bfcpMode;
    }
    public void setRxAudioMute(Integer rxAudioMute) 
    {
        this.rxAudioMute = rxAudioMute;
    }

    public Integer getRxAudioMute() 
    {
        return rxAudioMute;
    }
    public void setTxAudioMute(Integer txAudioMute) 
    {
        this.txAudioMute = txAudioMute;
    }

    public Integer getTxAudioMute() 
    {
        return txAudioMute;
    }
    public void setRxVideoMute(Integer rxVideoMute) 
    {
        this.rxVideoMute = rxVideoMute;
    }

    public Integer getRxVideoMute() 
    {
        return rxVideoMute;
    }
    public void setTxVideoMute(Integer txVideoMute) 
    {
        this.txVideoMute = txVideoMute;
    }

    public Integer getTxVideoMute() 
    {
        return txVideoMute;
    }
    public void setAudioGainMode(String audioGainMode) 
    {
        this.audioGainMode = audioGainMode;
    }

    public String getAudioGainMode() 
    {
        return audioGainMode;
    }
    public void setParticipantLabels(Integer participantLabels) 
    {
        this.participantLabels = participantLabels;
    }

    public Integer getParticipantLabels() 
    {
        return participantLabels;
    }
    public void setDtmfSequence(String dtmfSequence) 
    {
        this.dtmfSequence = dtmfSequence;
    }

    public String getDtmfSequence() 
    {
        return dtmfSequence;
    }
    public void setDeactivated(Integer deactivated) 
    {
        this.deactivated = deactivated;
    }

    public Integer getDeactivated() 
    {
        return deactivated;
    }
    public void setEndCallAllowed(Integer endCallAllowed) 
    {
        this.endCallAllowed = endCallAllowed;
    }

    public Integer getEndCallAllowed() 
    {
        return endCallAllowed;
    }
    public void setDisconnectOthersAllowed(Integer disconnectOthersAllowed) 
    {
        this.disconnectOthersAllowed = disconnectOthersAllowed;
    }

    public Integer getDisconnectOthersAllowed() 
    {
        return disconnectOthersAllowed;
    }
    public void setAddParticipantAllowed(Integer addParticipantAllowed) 
    {
        this.addParticipantAllowed = addParticipantAllowed;
    }

    public Integer getAddParticipantAllowed() 
    {
        return addParticipantAllowed;
    }
    public void setMuteOthersAllowed(Integer muteOthersAllowed) 
    {
        this.muteOthersAllowed = muteOthersAllowed;
    }

    public Integer getMuteOthersAllowed() 
    {
        return muteOthersAllowed;
    }
    public void setVideoMuteOthersAllowed(Integer videoMuteOthersAllowed) 
    {
        this.videoMuteOthersAllowed = videoMuteOthersAllowed;
    }

    public Integer getVideoMuteOthersAllowed() 
    {
        return videoMuteOthersAllowed;
    }
    public void setMuteSelfAllowed(Integer muteSelfAllowed) 
    {
        this.muteSelfAllowed = muteSelfAllowed;
    }

    public Integer getMuteSelfAllowed() 
    {
        return muteSelfAllowed;
    }
    public void setVideoMuteSelfAllowed(Integer videoMuteSelfAllowed) 
    {
        this.videoMuteSelfAllowed = videoMuteSelfAllowed;
    }

    public Integer getVideoMuteSelfAllowed() 
    {
        return videoMuteSelfAllowed;
    }
    public void setChangeLayoutAllowed(Integer changeLayoutAllowed) 
    {
        this.changeLayoutAllowed = changeLayoutAllowed;
    }

    public Integer getChangeLayoutAllowed() 
    {
        return changeLayoutAllowed;
    }
    public void setAllowAllMuteSelfAllowed(Integer allowAllMuteSelfAllowed) 
    {
        this.allowAllMuteSelfAllowed = allowAllMuteSelfAllowed;
    }

    public Integer getAllowAllMuteSelfAllowed() 
    {
        return allowAllMuteSelfAllowed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("qualityMain", getQualityMain())
            .append("qualityPresentation", getQualityPresentation())
            .append("bandwidth", getBandwidth())
            .append("presentationContributionAllowed", getPresentationContributionAllowed())
            .append("presentationViewingAllowed", getPresentationViewingAllowed())
            .append("presentationDisplayMode", getPresentationDisplayMode())
            .append("sipMediaEncryption", getSipMediaEncryption())
            .append("defaultLayout", getDefaultLayout())
            .append("bfcpMode", getBfcpMode())
            .append("rxAudioMute", getRxAudioMute())
            .append("txAudioMute", getTxAudioMute())
            .append("rxVideoMute", getRxVideoMute())
            .append("txVideoMute", getTxVideoMute())
            .append("audioGainMode", getAudioGainMode())
            .append("participantLabels", getParticipantLabels())
            .append("dtmfSequence", getDtmfSequence())
            .append("deactivated", getDeactivated())
            .append("endCallAllowed", getEndCallAllowed())
            .append("disconnectOthersAllowed", getDisconnectOthersAllowed())
            .append("addParticipantAllowed", getAddParticipantAllowed())
            .append("muteOthersAllowed", getMuteOthersAllowed())
            .append("videoMuteOthersAllowed", getVideoMuteOthersAllowed())
            .append("muteSelfAllowed", getMuteSelfAllowed())
            .append("videoMuteSelfAllowed", getVideoMuteSelfAllowed())
            .append("changeLayoutAllowed", getChangeLayoutAllowed())
            .append("allowAllMuteSelfAllowed", getAllowAllMuteSelfAllowed())
            .toString();
    }
}