/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : FixedSettings.java
 * Package     : com.paradisecloud.fcm.fme.model.busi.attendee
 * @author sinhy 
 * @since 2021-09-02 17:29
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.service.conference.attendee;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**  
 * <pre>参会者固定设置</pre>
 * @author sinhy
 * @since 2021-09-02 17:29
 * @version V1.0  
 */
public class BaseFixedSettings
{
    private Map<String, BaseFixedParam> fixedParamMap = new HashMap<>();
    private BaseFixedParam chosenLayout = new BaseFixedParam("chosenLayout", fixedParamMap);
    private BaseFixedParam defaultLayout = new BaseFixedParam("defaultLayout", fixedParamMap);
    private BaseFixedParam layout = new BaseFixedParam("layout", fixedParamMap);
    private BaseFixedParam needsActivation = new BaseFixedParam("needsActivation", fixedParamMap);
    private BaseFixedParam participantLabels = new BaseFixedParam("participantLabels", fixedParamMap);
    private BaseFixedParam presentationDisplayMode = new BaseFixedParam("presentationDisplayMode", fixedParamMap);
    private BaseFixedParam presentationContributionAllowed = new BaseFixedParam("presentationContributionAllowed", fixedParamMap);
    private BaseFixedParam presentationViewingAllowed = new BaseFixedParam("presentationViewingAllowed", fixedParamMap);
    private BaseFixedParam endCallAllowed = new BaseFixedParam("endCallAllowed", fixedParamMap);
    private BaseFixedParam muteOthersAllowed = new BaseFixedParam("muteOthersAllowed", fixedParamMap);
    private BaseFixedParam videoMuteOthersAllowed = new BaseFixedParam("videoMuteOthersAllowed", fixedParamMap);
    private BaseFixedParam muteSelfAllowed = new BaseFixedParam("muteSelfAllowed", fixedParamMap);
    private BaseFixedParam videoMuteSelfAllowed = new BaseFixedParam("videoMuteSelfAllowed", fixedParamMap);
    private BaseFixedParam changeLayoutAllowed = new BaseFixedParam("changeLayoutAllowed", fixedParamMap);
    private BaseFixedParam callLockAllowed = new BaseFixedParam("callLockAllowed", fixedParamMap);
    private BaseFixedParam videoMode = new BaseFixedParam("videoMode", fixedParamMap);
    private BaseFixedParam rxAudioMute = new BaseFixedParam("rxAudioMute", fixedParamMap);
    private BaseFixedParam txAudioMute = new BaseFixedParam("txAudioMute", fixedParamMap);
    private BaseFixedParam rxVideoMute = new BaseFixedParam("rxVideoMute", fixedParamMap);
    private BaseFixedParam txVideoMute = new BaseFixedParam("txVideoMute", fixedParamMap);
    private BaseFixedParam sipMediaEncryption = new BaseFixedParam("sipMediaEncryption", fixedParamMap);
    private BaseFixedParam deactivationMode = new BaseFixedParam("deactivationMode", fixedParamMap);
    private BaseFixedParam deactivationModeTime = new BaseFixedParam("deactivationModeTime", fixedParamMap);
    private BaseFixedParam telepresenceCallsAllowed = new BaseFixedParam("telepresenceCallsAllowed", fixedParamMap);
    private BaseFixedParam sipPresentationChannelEnabled = new BaseFixedParam("sipPresentationChannelEnabled", fixedParamMap);
    private BaseFixedParam bfcpMode = new BaseFixedParam("bfcpMode", fixedParamMap);
    private BaseFixedParam controlRemoteCameraAllowed = new BaseFixedParam("controlRemoteCameraAllowed", fixedParamMap);
    private BaseFixedParam disconnectOthersAllowed = new BaseFixedParam("disconnectOthersAllowed", fixedParamMap);
    private BaseFixedParam addParticipantAllowed = new BaseFixedParam("addParticipantAllowed", fixedParamMap);
    private BaseFixedParam qualityMain = new BaseFixedParam("qualityMain", fixedParamMap);
    private BaseFixedParam qualityPresentation = new BaseFixedParam("qualityPresentation", fixedParamMap);
    private BaseFixedParam participantCounter = new BaseFixedParam("participantCounter", fixedParamMap);
    private BaseFixedParam nameLabelOverride = new BaseFixedParam("nameLabelOverride", fixedParamMap);
    private BaseFixedParam layoutTemplate = new BaseFixedParam("layoutTemplate", fixedParamMap);
    private BaseFixedParam audioGainMode = new BaseFixedParam("audioGainMode", fixedParamMap);
    private BaseFixedParam meetingTitlePosition = new BaseFixedParam("meetingTitlePosition", fixedParamMap);
    private BaseFixedParam handStatus = new BaseFixedParam("handStatus", fixedParamMap);
    private BaseFixedParam chatContributionAllowed = new BaseFixedParam("chatContributionAllowed", fixedParamMap);
    
    public BaseFixedParam getByName(String name)
    {
        BaseFixedParam fp = fixedParamMap.get(name);
        Assert.notNull(fp, "不识别的参数：" + name);
        return fp;
    }
    
    /**
     * <p>Get Method   :   chosenLayout BaseFixedParam</p>
     * @return chosenLayout
     */
    public BaseFixedParam getChosenLayout()
    {
        return chosenLayout;
    }
    /**
     * <p>Get Method   :   defaultLayout BaseFixedParam</p>
     * @return defaultLayout
     */
    public BaseFixedParam getDefaultLayout()
    {
        return defaultLayout;
    }
    /**
     * <p>Get Method   :   layout BaseFixedParam</p>
     * @return layout
     */
    public BaseFixedParam getLayout()
    {
        return layout;
    }
    /**
     * <p>Get Method   :   needsActivation BaseFixedParam</p>
     * @return needsActivation
     */
    public BaseFixedParam getNeedsActivation()
    {
        return needsActivation;
    }
    /**
     * <p>Get Method   :   participantLabels BaseFixedParam</p>
     * @return participantLabels
     */
    public BaseFixedParam getParticipantLabels()
    {
        return participantLabels;
    }
    /**
     * <p>Get Method   :   presentationDisplayMode BaseFixedParam</p>
     * @return presentationDisplayMode
     */
    public BaseFixedParam getPresentationDisplayMode()
    {
        return presentationDisplayMode;
    }
    /**
     * <p>Get Method   :   presentationContributionAllowed BaseFixedParam</p>
     * @return presentationContributionAllowed
     */
    public BaseFixedParam getPresentationContributionAllowed()
    {
        return presentationContributionAllowed;
    }
    /**
     * <p>Get Method   :   presentationViewingAllowed BaseFixedParam</p>
     * @return presentationViewingAllowed
     */
    public BaseFixedParam getPresentationViewingAllowed()
    {
        return presentationViewingAllowed;
    }
    /**
     * <p>Get Method   :   endCallAllowed BaseFixedParam</p>
     * @return endCallAllowed
     */
    public BaseFixedParam getEndCallAllowed()
    {
        return endCallAllowed;
    }
    /**
     * <p>Get Method   :   muteOthersAllowed BaseFixedParam</p>
     * @return muteOthersAllowed
     */
    public BaseFixedParam getMuteOthersAllowed()
    {
        return muteOthersAllowed;
    }
    /**
     * <p>Get Method   :   videoMuteOthersAllowed BaseFixedParam</p>
     * @return videoMuteOthersAllowed
     */
    public BaseFixedParam getVideoMuteOthersAllowed()
    {
        return videoMuteOthersAllowed;
    }
    /**
     * <p>Get Method   :   muteSelfAllowed BaseFixedParam</p>
     * @return muteSelfAllowed
     */
    public BaseFixedParam getMuteSelfAllowed()
    {
        return muteSelfAllowed;
    }
    /**
     * <p>Get Method   :   videoMuteSelfAllowed BaseFixedParam</p>
     * @return videoMuteSelfAllowed
     */
    public BaseFixedParam getVideoMuteSelfAllowed()
    {
        return videoMuteSelfAllowed;
    }
    /**
     * <p>Get Method   :   changeLayoutAllowed BaseFixedParam</p>
     * @return changeLayoutAllowed
     */
    public BaseFixedParam getChangeLayoutAllowed()
    {
        return changeLayoutAllowed;
    }
    /**
     * <p>Get Method   :   callLockAllowed BaseFixedParam</p>
     * @return callLockAllowed
     */
    public BaseFixedParam getCallLockAllowed()
    {
        return callLockAllowed;
    }
    /**
     * <p>Get Method   :   videoMode BaseFixedParam</p>
     * @return videoMode
     */
    public BaseFixedParam getVideoMode()
    {
        return videoMode;
    }
    /**
     * <p>Get Method   :   rxAudioMute BaseFixedParam</p>
     * @return rxAudioMute
     */
    public BaseFixedParam getRxAudioMute()
    {
        return rxAudioMute;
    }
    /**
     * <p>Get Method   :   txAudioMute BaseFixedParam</p>
     * @return txAudioMute
     */
    public BaseFixedParam getTxAudioMute()
    {
        return txAudioMute;
    }
    /**
     * <p>Get Method   :   rxVideoMute BaseFixedParam</p>
     * @return rxVideoMute
     */
    public BaseFixedParam getRxVideoMute()
    {
        return rxVideoMute;
    }
    /**
     * <p>Get Method   :   txVideoMute BaseFixedParam</p>
     * @return txVideoMute
     */
    public BaseFixedParam getTxVideoMute()
    {
        return txVideoMute;
    }
    /**
     * <p>Get Method   :   sipMediaEncryption BaseFixedParam</p>
     * @return sipMediaEncryption
     */
    public BaseFixedParam getSipMediaEncryption()
    {
        return sipMediaEncryption;
    }
    /**
     * <p>Get Method   :   deactivationMode BaseFixedParam</p>
     * @return deactivationMode
     */
    public BaseFixedParam getDeactivationMode()
    {
        return deactivationMode;
    }
    /**
     * <p>Get Method   :   deactivationModeTime BaseFixedParam</p>
     * @return deactivationModeTime
     */
    public BaseFixedParam getDeactivationModeTime()
    {
        return deactivationModeTime;
    }
    /**
     * <p>Get Method   :   telepresenceCallsAllowed BaseFixedParam</p>
     * @return telepresenceCallsAllowed
     */
    public BaseFixedParam getTelepresenceCallsAllowed()
    {
        return telepresenceCallsAllowed;
    }
    /**
     * <p>Get Method   :   sipPresentationChannelEnabled BaseFixedParam</p>
     * @return sipPresentationChannelEnabled
     */
    public BaseFixedParam getSipPresentationChannelEnabled()
    {
        return sipPresentationChannelEnabled;
    }
    /**
     * <p>Get Method   :   bfcpMode BaseFixedParam</p>
     * @return bfcpMode
     */
    public BaseFixedParam getBfcpMode()
    {
        return bfcpMode;
    }
    /**
     * <p>Get Method   :   controlRemoteCameraAllowed BaseFixedParam</p>
     * @return controlRemoteCameraAllowed
     */
    public BaseFixedParam getControlRemoteCameraAllowed()
    {
        return controlRemoteCameraAllowed;
    }
    /**
     * <p>Get Method   :   disconnectOthersAllowed BaseFixedParam</p>
     * @return disconnectOthersAllowed
     */
    public BaseFixedParam getDisconnectOthersAllowed()
    {
        return disconnectOthersAllowed;
    }
    /**
     * <p>Get Method   :   addParticipantAllowed BaseFixedParam</p>
     * @return addParticipantAllowed
     */
    public BaseFixedParam getAddParticipantAllowed()
    {
        return addParticipantAllowed;
    }
    /**
     * <p>Get Method   :   qualityMain BaseFixedParam</p>
     * @return qualityMain
     */
    public BaseFixedParam getQualityMain()
    {
        return qualityMain;
    }
    /**
     * <p>Get Method   :   qualityPresentation BaseFixedParam</p>
     * @return qualityPresentation
     */
    public BaseFixedParam getQualityPresentation()
    {
        return qualityPresentation;
    }
    /**
     * <p>Get Method   :   participantCounter BaseFixedParam</p>
     * @return participantCounter
     */
    public BaseFixedParam getParticipantCounter()
    {
        return participantCounter;
    }
    /**
     * <p>Get Method   :   nameLabelOverride BaseFixedParam</p>
     * @return nameLabelOverride
     */
    public BaseFixedParam getNameLabelOverride()
    {
        return nameLabelOverride;
    }
    /**
     * <p>Get Method   :   layoutTemplate BaseFixedParam</p>
     * @return layoutTemplate
     */
    public BaseFixedParam getLayoutTemplate()
    {
        return layoutTemplate;
    }
    /**
     * <p>Get Method   :   audioGainMode BaseFixedParam</p>
     * @return audioGainMode
     */
    public BaseFixedParam getAudioGainMode()
    {
        return audioGainMode;
    }
    /**
     * <p>Get Method   :   meetingTitlePosition BaseFixedParam</p>
     * @return meetingTitlePosition
     */
    public BaseFixedParam getMeetingTitlePosition()
    {
        return meetingTitlePosition;
    }
    /**
     * <p>Get Method   :   handStatus BaseFixedParam</p>
     * @return handStatus
     */
    public BaseFixedParam getHandStatus()
    {
        return handStatus;
    }
    /**
     * <p>Get Method   :   chatContributionAllowed BaseFixedParam</p>
     * @return chatContributionAllowed
     */
    public BaseFixedParam getChatContributionAllowed()
    {
        return chatContributionAllowed;
    }
}
