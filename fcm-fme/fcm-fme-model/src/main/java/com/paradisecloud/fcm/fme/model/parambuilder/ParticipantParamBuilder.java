/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : ParticipantParamBuilder.java
 * Package     : com.paradisecloud.fcm.fme.model.param
 * @author lilinhai 
 * @since 2021-02-19 11:46
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.parambuilder;

/**  
 * <pre>参会者参数构建器</pre>
 * @author lilinhai
 * @since 2021-02-19 11:46
 * @version V1.0  
 */
public class ParticipantParamBuilder extends ParamBuilder<ParticipantParamBuilder>
{
    
    /**
     * 布局，以入会方案为准，如果设置了会议室，则以会议室布局为准
     * @author lilinhai
     * @since 2021-02-19 11:48 
     * @param defaultLayout
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder layout(String... layout)
    {
        return param("layout", layout);
    }
    
    /**
     * 布局，以入会方案为准，如果设置了会议室，则以会议室布局为准
     * @author lilinhai
     * @since 2021-02-19 11:48 
     * @param defaultLayout
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder chosenLayout(String... chosenLayout)
    {
        return param("chosenLayout", chosenLayout);
    }

    /**
     * 布局，以入会方案为准，如果设置了会议室，则以会议室布局为准
     * @author layoutTemplate
     * @since 2022-05-11 11:48
     * @param layoutTemplate
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder layoutTemplate(String... layoutTemplate)
    {
        return param("layoutTemplate", layoutTemplate);
    }
    
    /**
     * 权重值
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param importance
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder importance(Integer importance)
    {
        if (importance == null)
        {
            return param("importance", "");
        }
        return param("importance", importance);
    }
    
    /**
     * 参会者远程地州
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param remoteParty
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder remoteParty(String remoteParty)
    {
        return param("remoteParty", remoteParty);
    }
    
    /**
     * 参会者名称
     * 最多50个字符 如果提供，则覆盖此调用分支的名称。设置空字符串将清除该值并恢复原始名称。覆盖参与者的名称及其相关的调用分支是可互换的，并且两者都有影响;最新的更改优先。
     * 它将参与者的姓名更改为:其他会议参与者查看的屏幕名称标签、ActiveControl名册列表、会议应用程序在呼叫中看到参与者姓名的任何地方、CDR记录，其中姓名出现在web界面中。
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param nameLabelOverride
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder nameLabelOverride(String nameLabelOverride)
    {
        return param("nameLabelOverride", nameLabelOverride);
    }
    
    /**
     * true 主流分辨率
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder qualityMain(String... qualityMain)
    {
        return param("qualityMain", qualityMain);
    }
    
    /**
     * 会议台头位置
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder meetingTitlePosition(String... meetingTitlePosition)
    {
        return param("meetingTitlePosition", meetingTitlePosition);
    }
    
    /**
     * true 辅流分辨率
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder qualityPresentation(String... qualityPresentation)
    {
        return param("qualityPresentation", qualityPresentation);
    }

    /**
     * 入会带宽
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder bandwidth(Double... bandwidth)
    {
        if (bandwidth != null)
        {
            for (Double val : bandwidth)
            {
                if (val != null)
                {
                    return param("bandwidth", (long)(val * 1000000));
                }
            }
        }
        return this;
    }

    /**
     * 入会带宽
     * @author lilinhai
     * @since 2021-02-19 11:49
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder bandwidth(Long... bandwidth)
    {
        if (bandwidth != null)
        {
            for (Long val : bandwidth)
            {
                if (val != null)
                {
                    return param("bandwidth", val);
                }
            }
        }
        return this;
    }
    
    /**
     * 允许双流
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder presentationContributionAllowed(String... presentationContributionAllowed)
    {
        return param("presentationContributionAllowed", presentationContributionAllowed);
    }
    
    /**
     * 接收双流
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder presentationViewingAllowed(String... presentationViewingAllowed)
    {
        return param("presentationViewingAllowed", presentationViewingAllowed);
    }
    
    /**
     * 双流模式
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder presentationDisplayMode(String... presentationDisplayMode)
    {
        return param("presentationDisplayMode", presentationDisplayMode);
    }
    
    /**
     * 分享双流
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder sipPresentationChannelEnabled(String... sipPresentationChannelEnabled)
    {
        return param("sipPresentationChannelEnabled", sipPresentationChannelEnabled);
    }
    
    /**
     * SIP加密
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder sipMediaEncryption(String... sipMediaEncryption)
    {
        return param("sipMediaEncryption", sipMediaEncryption);
    }
    
    /**
     * 默认布局，以入会方案为准，如果设置了会议室，则以会议室布局为准
     * @author lilinhai
     * @since 2021-02-19 11:48 
     * @param defaultLayout
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder defaultLayout(String... defaultLayout)
    {
        return param("defaultLayout", defaultLayout);
    }
    
    /**
     * BFCP模式
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bfcpMode
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder bfcpMode(String... bfcpMode)
    {
        return param("bfcpMode", bfcpMode);
    }
    
    /**
     * 如果为true，则禁用从所有端点接收音频。
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder rxAudioMute(Boolean... rxAudioMute)
    {
        return param("rxAudioMute", rxAudioMute);
    }
    
    /**
     * true 被静音
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder txAudioMute(Boolean... txAudioMute)
    {
        return param("txAudioMute", txAudioMute);
    }
    
    /**
     * true 使用此call leg profile的call leg的(“相机”)视频将不会被其他参与者看到。
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder rxVideoMute(Boolean... rxVideoMute)
    {
        return param("rxVideoMute", rxVideoMute);
    }
    
    /**
     * true 视频流不发送
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder txVideoMute(Boolean... txVideoMute)
    {
        return param("txVideoMute", txVideoMute);
    }
    
    /**
     * true 音频增
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param rxAudioMute
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder audioGainMode(String... audioGainMode)
    {
        return param("audioGainMode", audioGainMode);
    }
    
    /**
     * true 显示入会名称
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param participantLabels
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder participantLabels(String... participantLabels)
    {
        return param("participantLabels", participantLabels);
    }
    
    /**
     * DTMF字符
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param dtmfSequence
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder dtmfSequence(String... dtmfSequence)
    {
        return param("dtmfSequence", dtmfSequence);
    }
    
    /**
     * 开/关语音应答
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param deactivated
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder deactivated(Boolean... deactivated)
    {
        return param("deactivated", deactivated);
    }
    
    /**
     * 挂断呼叫
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param endCallAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder endCallAllowed(Boolean... endCallAllowed)
    {
        return param("endCallAllowed", endCallAllowed);
    }
    
    /**
     * 挂断其他参会者
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param disconnectOthersAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder disconnectOthersAllowed(Boolean... disconnectOthersAllowed)
    {
        return param("disconnectOthersAllowed", disconnectOthersAllowed);
    }
    
    /**
     * 邀请其他参会者
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param addParticipantAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder addParticipantAllowed(Boolean... addParticipantAllowed)
    {
        return param("addParticipantAllowed", addParticipantAllowed);
    }
    
    /**
     * 静音其他参会者
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param muteOthersAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder muteOthersAllowed(Boolean... muteOthersAllowed)
    {
        return param("muteOthersAllowed", muteOthersAllowed);
    }
    
    /**
     * 关闭其他参会者视频
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param videoMuteOthersAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder videoMuteOthersAllowed(Boolean... videoMuteOthersAllowed)
    {
        return param("videoMuteOthersAllowed", videoMuteOthersAllowed);
    }
    
    /**
     * 静音自己
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param muteSelfAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder muteSelfAllowed(Boolean... muteSelfAllowed)
    {
        return param("muteSelfAllowed", muteSelfAllowed);
    }
    
    /**
     * 关闭自己视频
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param videoMuteSelfAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder videoMuteSelfAllowed(Boolean... videoMuteSelfAllowed)
    {
        return param("videoMuteSelfAllowed", videoMuteSelfAllowed);
    }
    
    /**
     * 更改布局
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param changeLayoutAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder changeLayoutAllowed(Boolean... changeLayoutAllowed)
    {
        return param("changeLayoutAllowed", changeLayoutAllowed);
    }
    
    /**
     * 允许静音自己
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param allowAllMuteSelfAllowed
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder allowAllMuteSelfAllowed(Boolean... allowAllMuteSelfAllowed)
    {
        return param("allowAllMuteSelfAllowed", allowAllMuteSelfAllowed);
    }
    
    
}
