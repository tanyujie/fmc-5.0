/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FixedSettings.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.attendee
 * @author sinhy 
 * @since 2021-09-02 17:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.model.busi.attendee;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**  
 * <pre>参会者固定设置</pre>
 * @author sinhy
 * @since 2021-09-02 17:29
 * @version V1.0  
 */
public class FixedSettings
{
    private Map<String, FixedParam> fixedParamMap = new HashMap<>();
    private FixedParam chosenLayout = new FixedParam("chosenLayout", fixedParamMap);
    private FixedParam defaultLayout = new FixedParam("defaultLayout", fixedParamMap);
    private FixedParam layout = new FixedParam("layout", fixedParamMap);
    private FixedParam needsActivation = new FixedParam("needsActivation", fixedParamMap);
    private FixedParam participantLabels = new FixedParam("participantLabels", fixedParamMap);
    private FixedParam presentationDisplayMode = new FixedParam("presentationDisplayMode", fixedParamMap);
    private FixedParam presentationContributionAllowed = new FixedParam("presentationContributionAllowed", fixedParamMap);
    private FixedParam presentationViewingAllowed = new FixedParam("presentationViewingAllowed", fixedParamMap);
    private FixedParam endCallAllowed = new FixedParam("endCallAllowed", fixedParamMap);
    private FixedParam muteOthersAllowed = new FixedParam("muteOthersAllowed", fixedParamMap);
    private FixedParam videoMuteOthersAllowed = new FixedParam("videoMuteOthersAllowed", fixedParamMap);
    private FixedParam muteSelfAllowed = new FixedParam("muteSelfAllowed", fixedParamMap);
    private FixedParam videoMuteSelfAllowed = new FixedParam("videoMuteSelfAllowed", fixedParamMap);
    private FixedParam changeLayoutAllowed = new FixedParam("changeLayoutAllowed", fixedParamMap);
    private FixedParam callLockAllowed = new FixedParam("callLockAllowed", fixedParamMap);
    private FixedParam videoMode = new FixedParam("videoMode", fixedParamMap);
    private FixedParam rxAudioMute = new FixedParam("rxAudioMute", fixedParamMap);
    private FixedParam txAudioMute = new FixedParam("txAudioMute", fixedParamMap);
    private FixedParam rxVideoMute = new FixedParam("rxVideoMute", fixedParamMap);
    private FixedParam txVideoMute = new FixedParam("txVideoMute", fixedParamMap);
    private FixedParam sipMediaEncryption = new FixedParam("sipMediaEncryption", fixedParamMap);
    private FixedParam deactivationMode = new FixedParam("deactivationMode", fixedParamMap);
    private FixedParam deactivationModeTime = new FixedParam("deactivationModeTime", fixedParamMap);
    private FixedParam telepresenceCallsAllowed = new FixedParam("telepresenceCallsAllowed", fixedParamMap);
    private FixedParam sipPresentationChannelEnabled = new FixedParam("sipPresentationChannelEnabled", fixedParamMap);
    private FixedParam bfcpMode = new FixedParam("bfcpMode", fixedParamMap);
    private FixedParam controlRemoteCameraAllowed = new FixedParam("controlRemoteCameraAllowed", fixedParamMap);
    private FixedParam disconnectOthersAllowed = new FixedParam("disconnectOthersAllowed", fixedParamMap);
    private FixedParam addParticipantAllowed = new FixedParam("addParticipantAllowed", fixedParamMap);
    private FixedParam qualityMain = new FixedParam("qualityMain", fixedParamMap);
    private FixedParam qualityPresentation = new FixedParam("qualityPresentation", fixedParamMap);
    private FixedParam participantCounter = new FixedParam("participantCounter", fixedParamMap);
    private FixedParam nameLabelOverride = new FixedParam("nameLabelOverride", fixedParamMap);
    private FixedParam layoutTemplate = new FixedParam("layoutTemplate", fixedParamMap);
    private FixedParam audioGainMode = new FixedParam("audioGainMode", fixedParamMap);
    private FixedParam meetingTitlePosition = new FixedParam("meetingTitlePosition", fixedParamMap);
    private FixedParam handStatus = new FixedParam("handStatus", fixedParamMap);
    private FixedParam chatContributionAllowed = new FixedParam("chatContributionAllowed", fixedParamMap);
    
    public FixedParam getByName(String name)
    {
        FixedParam fp = fixedParamMap.get(name);
        Assert.notNull(fp, "不识别的参数：" + name);
        return fp;
    }
    
    /**
     * <p>Get Method   :   chosenLayout FixedParam</p>
     * @return chosenLayout
     */
    public FixedParam getChosenLayout()
    {
        return chosenLayout;
    }
    /**
     * <p>Get Method   :   defaultLayout FixedParam</p>
     * @return defaultLayout
     */
    public FixedParam getDefaultLayout()
    {
        return defaultLayout;
    }
    /**
     * <p>Get Method   :   layout FixedParam</p>
     * @return layout
     */
    public FixedParam getLayout()
    {
        return layout;
    }
    /**
     * <p>Get Method   :   needsActivation FixedParam</p>
     * @return needsActivation
     */
    public FixedParam getNeedsActivation()
    {
        return needsActivation;
    }
    /**
     * <p>Get Method   :   participantLabels FixedParam</p>
     * @return participantLabels
     */
    public FixedParam getParticipantLabels()
    {
        return participantLabels;
    }
    /**
     * <p>Get Method   :   presentationDisplayMode FixedParam</p>
     * @return presentationDisplayMode
     */
    public FixedParam getPresentationDisplayMode()
    {
        return presentationDisplayMode;
    }
    /**
     * <p>Get Method   :   presentationContributionAllowed FixedParam</p>
     * @return presentationContributionAllowed
     */
    public FixedParam getPresentationContributionAllowed()
    {
        return presentationContributionAllowed;
    }
    /**
     * <p>Get Method   :   presentationViewingAllowed FixedParam</p>
     * @return presentationViewingAllowed
     */
    public FixedParam getPresentationViewingAllowed()
    {
        return presentationViewingAllowed;
    }
    /**
     * <p>Get Method   :   endCallAllowed FixedParam</p>
     * @return endCallAllowed
     */
    public FixedParam getEndCallAllowed()
    {
        return endCallAllowed;
    }
    /**
     * <p>Get Method   :   muteOthersAllowed FixedParam</p>
     * @return muteOthersAllowed
     */
    public FixedParam getMuteOthersAllowed()
    {
        return muteOthersAllowed;
    }
    /**
     * <p>Get Method   :   videoMuteOthersAllowed FixedParam</p>
     * @return videoMuteOthersAllowed
     */
    public FixedParam getVideoMuteOthersAllowed()
    {
        return videoMuteOthersAllowed;
    }
    /**
     * <p>Get Method   :   muteSelfAllowed FixedParam</p>
     * @return muteSelfAllowed
     */
    public FixedParam getMuteSelfAllowed()
    {
        return muteSelfAllowed;
    }
    /**
     * <p>Get Method   :   videoMuteSelfAllowed FixedParam</p>
     * @return videoMuteSelfAllowed
     */
    public FixedParam getVideoMuteSelfAllowed()
    {
        return videoMuteSelfAllowed;
    }
    /**
     * <p>Get Method   :   changeLayoutAllowed FixedParam</p>
     * @return changeLayoutAllowed
     */
    public FixedParam getChangeLayoutAllowed()
    {
        return changeLayoutAllowed;
    }
    /**
     * <p>Get Method   :   callLockAllowed FixedParam</p>
     * @return callLockAllowed
     */
    public FixedParam getCallLockAllowed()
    {
        return callLockAllowed;
    }
    /**
     * <p>Get Method   :   videoMode FixedParam</p>
     * @return videoMode
     */
    public FixedParam getVideoMode()
    {
        return videoMode;
    }
    /**
     * <p>Get Method   :   rxAudioMute FixedParam</p>
     * @return rxAudioMute
     */
    public FixedParam getRxAudioMute()
    {
        return rxAudioMute;
    }
    /**
     * <p>Get Method   :   txAudioMute FixedParam</p>
     * @return txAudioMute
     */
    public FixedParam getTxAudioMute()
    {
        return txAudioMute;
    }
    /**
     * <p>Get Method   :   rxVideoMute FixedParam</p>
     * @return rxVideoMute
     */
    public FixedParam getRxVideoMute()
    {
        return rxVideoMute;
    }
    /**
     * <p>Get Method   :   txVideoMute FixedParam</p>
     * @return txVideoMute
     */
    public FixedParam getTxVideoMute()
    {
        return txVideoMute;
    }
    /**
     * <p>Get Method   :   sipMediaEncryption FixedParam</p>
     * @return sipMediaEncryption
     */
    public FixedParam getSipMediaEncryption()
    {
        return sipMediaEncryption;
    }
    /**
     * <p>Get Method   :   deactivationMode FixedParam</p>
     * @return deactivationMode
     */
    public FixedParam getDeactivationMode()
    {
        return deactivationMode;
    }
    /**
     * <p>Get Method   :   deactivationModeTime FixedParam</p>
     * @return deactivationModeTime
     */
    public FixedParam getDeactivationModeTime()
    {
        return deactivationModeTime;
    }
    /**
     * <p>Get Method   :   telepresenceCallsAllowed FixedParam</p>
     * @return telepresenceCallsAllowed
     */
    public FixedParam getTelepresenceCallsAllowed()
    {
        return telepresenceCallsAllowed;
    }
    /**
     * <p>Get Method   :   sipPresentationChannelEnabled FixedParam</p>
     * @return sipPresentationChannelEnabled
     */
    public FixedParam getSipPresentationChannelEnabled()
    {
        return sipPresentationChannelEnabled;
    }
    /**
     * <p>Get Method   :   bfcpMode FixedParam</p>
     * @return bfcpMode
     */
    public FixedParam getBfcpMode()
    {
        return bfcpMode;
    }
    /**
     * <p>Get Method   :   controlRemoteCameraAllowed FixedParam</p>
     * @return controlRemoteCameraAllowed
     */
    public FixedParam getControlRemoteCameraAllowed()
    {
        return controlRemoteCameraAllowed;
    }
    /**
     * <p>Get Method   :   disconnectOthersAllowed FixedParam</p>
     * @return disconnectOthersAllowed
     */
    public FixedParam getDisconnectOthersAllowed()
    {
        return disconnectOthersAllowed;
    }
    /**
     * <p>Get Method   :   addParticipantAllowed FixedParam</p>
     * @return addParticipantAllowed
     */
    public FixedParam getAddParticipantAllowed()
    {
        return addParticipantAllowed;
    }
    /**
     * <p>Get Method   :   qualityMain FixedParam</p>
     * @return qualityMain
     */
    public FixedParam getQualityMain()
    {
        return qualityMain;
    }
    /**
     * <p>Get Method   :   qualityPresentation FixedParam</p>
     * @return qualityPresentation
     */
    public FixedParam getQualityPresentation()
    {
        return qualityPresentation;
    }
    /**
     * <p>Get Method   :   participantCounter FixedParam</p>
     * @return participantCounter
     */
    public FixedParam getParticipantCounter()
    {
        return participantCounter;
    }
    /**
     * <p>Get Method   :   nameLabelOverride FixedParam</p>
     * @return nameLabelOverride
     */
    public FixedParam getNameLabelOverride()
    {
        return nameLabelOverride;
    }
    /**
     * <p>Get Method   :   layoutTemplate FixedParam</p>
     * @return layoutTemplate
     */
    public FixedParam getLayoutTemplate()
    {
        return layoutTemplate;
    }
    /**
     * <p>Get Method   :   audioGainMode FixedParam</p>
     * @return audioGainMode
     */
    public FixedParam getAudioGainMode()
    {
        return audioGainMode;
    }
    /**
     * <p>Get Method   :   meetingTitlePosition FixedParam</p>
     * @return meetingTitlePosition
     */
    public FixedParam getMeetingTitlePosition()
    {
        return meetingTitlePosition;
    }
    /**
     * <p>Get Method   :   handStatus FixedParam</p>
     * @return handStatus
     */
    public FixedParam getHandStatus()
    {
        return handStatus;
    }
    /**
     * <p>Get Method   :   chatContributionAllowed FixedParam</p>
     * @return chatContributionAllowed
     */
    public FixedParam getChatContributionAllowed()
    {
        return chatContributionAllowed;
    }
}
