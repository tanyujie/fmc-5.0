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
 * <pre>会议室Call参数构建器</pre>
 * @author lilinhai
 * @since 2021-02-19 11:46
 * @version V1.0  
 */
public class CallParamBuilder extends ParamBuilder<CallParamBuilder>
{
    
    /**
     * 会议室名
     * @author lilinhai
     * @since 2021-02-19 11:48 
     * @param defaultLayout
     * @return ParticipantParamBuilder
     */
    public CallParamBuilder name(String name)
    {
        return param("name", name);
    }
    
    /**
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CallParamBuilder messageText(String messageText)
    {
        return param("messageText", messageText);
    }
    
    /**
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CallParamBuilder messagePosition(String messagePosition)
    {
        return param("messagePosition", messagePosition);
    }
    
    /**
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CallParamBuilder messageDuration(String messageDuration)
    {
        return param("messageDuration", messageDuration);
    }
    
    /**
     * @author lilinhai
     * @since 2021-02-19 11:49 
     * @param bandwidth
     * @return ParticipantParamBuilder
     */
    public CallParamBuilder messageBannerText(String messageBannerText)
    {
        return param("messageBannerText", messageBannerText);
    }
    
    
    /**
     * 窗格最高权重（多分频业务专用）
     * @author lilinhai
     * @since 2021-04-09 17:50 
     * @param panePlacementHighestImportance
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder panePlacementHighestImportance(int panePlacementHighestImportance)
    {
        return param("panePlacementHighestImportance", panePlacementHighestImportance);
    }
    
    /**
     * 窗格模式，是否显示自己
     * @author lilinhai
     * @since 2021-04-09 17:50 
     * @param panePlacementSelfPaneMode
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder panePlacementSelfPaneMode(String panePlacementSelfPaneMode)
    {
        return param("panePlacementSelfPaneMode", panePlacementSelfPaneMode);
    }
    
    /**
     * 窗格模式，是否显示自己
     * @author lilinhai
     * @since 2021-04-09 17:50 
     * @param panePlacementSelfPaneMode
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder locked(Boolean isLock)
    {
        return param("locked", isLock);
    }
    
    /**
     * 允许所有人静音自己
     * @author lilinhai
     * @since 2021-04-28 13:52
     * @param allowAllMuteSelf
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder allowAllMuteSelf(Boolean allowAllMuteSelf)
    {
        return param("allowAllMuteSelf", allowAllMuteSelf);
    }
    
    /**
     * 允许辅流控制
     * @author lilinhai
     * @since 2021-04-28 13:52
     * @param allowAllPresentationContribution
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder allowAllPresentationContribution(Boolean allowAllPresentationContribution)
    {
        return param("allowAllPresentationContribution", allowAllPresentationContribution);
    }
    
    /**
     * 新加入用户静音
     * @author lilinhai
     * @since 2021-04-28 13:52
     * @param joinAudioMuteOverride
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder joinAudioMuteOverride(Boolean joinAudioMuteOverride)
    {
        return param("joinAudioMuteOverride", joinAudioMuteOverride);
    }
    
    /**
     * 是否录制会议
     * @author johnson liu
     * @since 2021-04-28 13:52
     * @param isRecording
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder recording(Boolean isRecording)
    {
        return param("recording", isRecording);
    }
    
    /**
     * 是否直播会议
     * @author johnson liu
     * @since 2021-04-28 13:52
     * @param isRecording
     * @return CoSpaceParamBuilder
     */
    public CallParamBuilder streaming(Boolean streaming)
    {
        return param("streaming", streaming);
    }
}
