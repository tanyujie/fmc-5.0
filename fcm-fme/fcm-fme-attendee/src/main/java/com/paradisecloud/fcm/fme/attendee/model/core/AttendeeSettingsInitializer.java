/* 
 * Copyright   : LinHai Technologies Co., Ltd. Copyright 2015-2021, All right reserved.
 * Description : <pre>TODO(用一句话描述该文件做什么)</pre>
 * FileName    : AttendeeSettingsInitializer.java
 * Package     : com.paradisecloud.fcm.fme.attendee.model.core
 * @author sinhy 
 * @since 2021-09-08 15:02
 * @version  V1.0
 */ 
package com.paradisecloud.fcm.fme.attendee.model.core;

import com.paradisecloud.fcm.common.cache.ExternalConfigCache;
import com.paradisecloud.fcm.common.enumer.TerminalType;
import com.paradisecloud.fcm.common.enumer.YesOrNo;
import com.paradisecloud.fcm.dao.model.BusiTerminal;
import com.paradisecloud.fcm.dao.model.BusiTerminalMeetingJoinSettings;
import com.paradisecloud.fcm.fme.attendee.model.enumer.AttendeeImportance;
import com.paradisecloud.fcm.fme.model.busi.ConferenceContext;
import com.paradisecloud.fcm.fme.model.busi.attendee.Attendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.FmeAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.InvitedAttendee;
import com.paradisecloud.fcm.fme.model.busi.attendee.TerminalAttendee;
import com.paradisecloud.fcm.fme.model.busi.layout.splitscreen.InheritSplitScreen;
import com.paradisecloud.fcm.fme.model.cms.CallLegProfile;
import com.paradisecloud.fcm.fme.model.parambuilder.ParticipantParamBuilder;
import com.paradisecloud.fcm.terminal.cache.TerminalCache;
import com.paradisecloud.fcm.terminal.fs.cache.FcmBridgeCache;
import com.paradisecloud.fcm.terminal.fs.model.FcmBridge;

/**  
 * <pre>参会者初始设置</pre>
 * @author sinhy
 * @since 2021-09-08 15:02
 * @version V1.0  
 */
public class AttendeeSettingsInitializer
{
    
    private ConferenceContext conferenceContext;
    private Attendee attendee;
    private BusiTerminalMeetingJoinSettings busiTerminalMeetingJoinSettings;
    private CallLegProfile callLegProfile;
    
    /**
     * <pre>构造方法</pre>
     * @author sinhy 
     * @since 2021-09-08 15:04 
     * @param conferenceContext
     * @param attendee
     */
    public AttendeeSettingsInitializer(ConferenceContext conferenceContext, Attendee attendee)
    {
        this.conferenceContext = conferenceContext;
        this.attendee = attendee;
        if (attendee instanceof TerminalAttendee)
        {
            TerminalAttendee ta = (TerminalAttendee) attendee;
            busiTerminalMeetingJoinSettings = TerminalCache.getInstance().getById(ta.getTerminalId());
        }
        
        if (busiTerminalMeetingJoinSettings == null)
        {
            busiTerminalMeetingJoinSettings = new BusiTerminalMeetingJoinSettings();
        }
        
        setFixedParam();
        this.callLegProfile = conferenceContext.getCallLegProfile();
    }
    
    /**
     * 获取参数构建器
     * @author sinhy
     * @since 2021-09-08 15:13 
     * @return ParticipantParamBuilder
     */
    public ParticipantParamBuilder getParticipantParamBuilder()
    {
        ParticipantParamBuilder participantParamBuilder = new ParticipantParamBuilder();
        participantParamBuilder.qualityMain(busiTerminalMeetingJoinSettings.getQualityMain(), callLegProfile.getQualityMain());
        participantParamBuilder.qualityPresentation(busiTerminalMeetingJoinSettings.getQualityPresentation(), callLegProfile.getQualityPresentation());
        Long bandwidth = 51200l;
        if (busiTerminalMeetingJoinSettings.getBandwidth() != null) {
            bandwidth = busiTerminalMeetingJoinSettings.getBandwidth().longValue() * 1000000;
        } else {
            if (conferenceContext.getBandwidth().intValue() > 90000000) {
                bandwidth = (conferenceContext.getBandwidth().longValue() - 90000000) * 1000;
            } else {
                bandwidth = conferenceContext.getBandwidth().longValue() * 1000000;
            }
        }
        participantParamBuilder.bandwidth(bandwidth);
        participantParamBuilder.presentationContributionAllowed(busiTerminalMeetingJoinSettings.getPresentationContributionAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getPresentationContributionAllowed()).getBooleanString() : null);
        participantParamBuilder.presentationViewingAllowed(busiTerminalMeetingJoinSettings.getPresentationViewingAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getPresentationViewingAllowed()).getBooleanString() : null);
        participantParamBuilder.presentationDisplayMode(busiTerminalMeetingJoinSettings.getPresentationDisplayMode(), callLegProfile.getPresentationDisplayMode());
        participantParamBuilder.sipMediaEncryption(busiTerminalMeetingJoinSettings.getSipMediaEncryption(), callLegProfile.getSipMediaEncryption());
        
        // 布局设置，按优先级先后：终端-->coSpace-->callLegProfile
        participantParamBuilder.defaultLayout(busiTerminalMeetingJoinSettings.getDefaultLayout(), InheritSplitScreen.LAYOUT);
        participantParamBuilder.chosenLayout(busiTerminalMeetingJoinSettings.getDefaultLayout(), InheritSplitScreen.LAYOUT);
        participantParamBuilder.bfcpMode(busiTerminalMeetingJoinSettings.getBfcpMode(), callLegProfile.getBfcpMode());
        participantParamBuilder.rxAudioMute(busiTerminalMeetingJoinSettings.getRxAudioMute() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getRxAudioMute()).getBoolean() : null
                , (attendee instanceof FmeAttendee || attendee == conferenceContext.getMasterAttendee()) ? Boolean.FALSE : callLegProfile.getRxAudioMute(), Boolean.TRUE);
        participantParamBuilder.txAudioMute(busiTerminalMeetingJoinSettings.getTxAudioMute() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getTxAudioMute()).getBoolean() : null
                , (attendee instanceof FmeAttendee || attendee == conferenceContext.getMasterAttendee()) ? Boolean.FALSE : callLegProfile.getTxAudioMute(), Boolean.FALSE);
        participantParamBuilder.rxVideoMute(busiTerminalMeetingJoinSettings.getRxVideoMute() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getRxVideoMute()).getBoolean() : null, callLegProfile.getRxVideoMute(), Boolean.FALSE);
        participantParamBuilder.txVideoMute(busiTerminalMeetingJoinSettings.getTxVideoMute() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getTxVideoMute()).getBoolean() : null, callLegProfile.getTxVideoMute(), Boolean.FALSE);
        participantParamBuilder.audioGainMode(busiTerminalMeetingJoinSettings.getAudioGainMode(), callLegProfile.getAudioGainMode());
        participantParamBuilder.participantLabels(busiTerminalMeetingJoinSettings.getParticipantLabels() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getParticipantLabels()).getBooleanString() : null);
        participantParamBuilder.importance(getImportance().getStartValue());
        participantParamBuilder.dtmfSequence(busiTerminalMeetingJoinSettings.getDtmfSequence());
        participantParamBuilder.deactivated(busiTerminalMeetingJoinSettings.getDeactivated() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getDeactivated()).getBoolean() : null);
        participantParamBuilder.endCallAllowed(busiTerminalMeetingJoinSettings.getEndCallAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getEndCallAllowed()).getBoolean() : null, callLegProfile.getEndCallAllowed());
        participantParamBuilder.disconnectOthersAllowed(busiTerminalMeetingJoinSettings.getDisconnectOthersAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getDisconnectOthersAllowed()).getBoolean() : null, callLegProfile.getDisconnectOthersAllowed());
        participantParamBuilder.addParticipantAllowed(busiTerminalMeetingJoinSettings.getAddParticipantAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getAddParticipantAllowed()).getBoolean() : null, callLegProfile.getAddParticipantAllowed());
        participantParamBuilder.muteOthersAllowed(busiTerminalMeetingJoinSettings.getMuteOthersAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getMuteOthersAllowed()).getBoolean() : null, callLegProfile.getMuteOthersAllowed());
        participantParamBuilder.videoMuteOthersAllowed(busiTerminalMeetingJoinSettings.getVideoMuteOthersAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getVideoMuteOthersAllowed()).getBoolean() : null, callLegProfile.getVideoMuteOthersAllowed());
        participantParamBuilder.muteSelfAllowed(busiTerminalMeetingJoinSettings.getMuteSelfAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getMuteSelfAllowed()).getBoolean() : null, callLegProfile.getMuteSelfAllowed());
        participantParamBuilder.videoMuteSelfAllowed(busiTerminalMeetingJoinSettings.getVideoMuteSelfAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getVideoMuteSelfAllowed()).getBoolean() : null, callLegProfile.getVideoMuteSelfAllowed());
        participantParamBuilder.changeLayoutAllowed(busiTerminalMeetingJoinSettings.getChangeLayoutAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getChangeLayoutAllowed()).getBoolean() : null, callLegProfile.getChangeLayoutAllowed());
        participantParamBuilder.allowAllMuteSelfAllowed(busiTerminalMeetingJoinSettings.getAllowAllMuteSelfAllowed() != null ? YesOrNo.convert(busiTerminalMeetingJoinSettings.getAllowAllMuteSelfAllowed()).getBoolean() : null, callLegProfile.getAllowAllMuteSelfAllowed());
        String remoteParty = attendee.getRemoteParty();
        if (attendee instanceof TerminalAttendee) {
            TerminalAttendee terminalAttendee = (TerminalAttendee) attendee;
            if (terminalAttendee.getTerminalId() != null) {
                BusiTerminal busiTerminal = TerminalCache.getInstance().get(terminalAttendee.getTerminalId());
                if (TerminalType.isFCMSIP(busiTerminal.getType())) {
                    FcmBridge fcmBridge = FcmBridgeCache.getInstance().get(busiTerminal.getFsServerId());
                    if (fcmBridge != null) {
                        Integer callPort = fcmBridge.getBusiFreeSwitch().getCallPort();
                        if (callPort != null) {
                            remoteParty += ":" + callPort;
                        }
                    }
                }
                else
                {
                    if (TerminalType.isIp(busiTerminal.getType())) {
                        if ("h323".equalsIgnoreCase(busiTerminal.getProtocol())) {
                            remoteParty += "@h323";
                        }
                    }
                    if (TerminalType.isOnlyIP(busiTerminal.getType())) {
                        if (busiTerminal.getPort() != null) {
                            remoteParty += ":" + busiTerminal.getPort();
                        }
                    }
//                    if ("ops".equals(ExternalConfigCache.getInstance().getRegion())) {
//                        if (attendee.getCallRequestSentTime() != null) {
//                            if (remoteParty.endsWith(":5070")) {
//                                remoteParty = remoteParty.replace(":5070", "");
//                                attendee.setRemoteParty(remoteParty);
//                            } else if (!remoteParty.contains(":") || remoteParty.endsWith(":5060")) {
//                                remoteParty += ":5070";
//                                attendee.setRemoteParty(remoteParty);
//                            }
//                        }
//                    }
                }
            }
        }
        participantParamBuilder.remoteParty(remoteParty);
        participantParamBuilder.nameLabelOverride(attendee.getName());
        return participantParamBuilder;
    }
    
    private void setFixedParam()
    {
        if (busiTerminalMeetingJoinSettings != null)
        {
            attendee.getFixedSettings().getQualityMain().setFixed(busiTerminalMeetingJoinSettings.getQualityMain() != null);
            attendee.getFixedSettings().getQualityPresentation().setFixed(busiTerminalMeetingJoinSettings.getQualityPresentation() != null);
            attendee.getFixedSettings().getPresentationContributionAllowed().setFixed(busiTerminalMeetingJoinSettings.getPresentationContributionAllowed() != null);
            attendee.getFixedSettings().getPresentationViewingAllowed().setFixed(busiTerminalMeetingJoinSettings.getPresentationViewingAllowed() != null);
            attendee.getFixedSettings().getPresentationDisplayMode().setFixed(busiTerminalMeetingJoinSettings.getPresentationDisplayMode() != null);
            attendee.getFixedSettings().getSipMediaEncryption().setFixed(busiTerminalMeetingJoinSettings.getSipMediaEncryption() != null);
            attendee.getFixedSettings().getDefaultLayout().setFixed(busiTerminalMeetingJoinSettings.getDefaultLayout() != null);
            attendee.getFixedSettings().getChosenLayout().setFixed(busiTerminalMeetingJoinSettings.getDefaultLayout() != null);
            attendee.getFixedSettings().getBfcpMode().setFixed(busiTerminalMeetingJoinSettings.getBfcpMode() != null);
            attendee.getFixedSettings().getRxAudioMute().setFixed(busiTerminalMeetingJoinSettings.getRxAudioMute() != null);
            attendee.getFixedSettings().getTxAudioMute().setFixed(busiTerminalMeetingJoinSettings.getTxAudioMute() != null);
            attendee.getFixedSettings().getRxVideoMute().setFixed(busiTerminalMeetingJoinSettings.getRxVideoMute() != null);
            attendee.getFixedSettings().getTxVideoMute().setFixed(busiTerminalMeetingJoinSettings.getTxVideoMute() != null);
            attendee.getFixedSettings().getAudioGainMode().setFixed(busiTerminalMeetingJoinSettings.getAudioGainMode() != null);
            attendee.getFixedSettings().getParticipantLabels().setFixed(busiTerminalMeetingJoinSettings.getParticipantLabels() != null);
            attendee.getFixedSettings().getEndCallAllowed().setFixed(busiTerminalMeetingJoinSettings.getEndCallAllowed() != null);
            attendee.getFixedSettings().getDisconnectOthersAllowed().setFixed(busiTerminalMeetingJoinSettings.getDisconnectOthersAllowed() != null);
            attendee.getFixedSettings().getAddParticipantAllowed().setFixed(busiTerminalMeetingJoinSettings.getAddParticipantAllowed() != null);
            attendee.getFixedSettings().getMuteOthersAllowed().setFixed(busiTerminalMeetingJoinSettings.getMuteOthersAllowed() != null);
            attendee.getFixedSettings().getVideoMuteOthersAllowed().setFixed(busiTerminalMeetingJoinSettings.getVideoMuteOthersAllowed() != null);
            attendee.getFixedSettings().getMuteSelfAllowed().setFixed(busiTerminalMeetingJoinSettings.getMuteSelfAllowed() != null);
            attendee.getFixedSettings().getVideoMuteSelfAllowed().setFixed(busiTerminalMeetingJoinSettings.getVideoMuteSelfAllowed() != null);
            attendee.getFixedSettings().getChangeLayoutAllowed().setFixed(busiTerminalMeetingJoinSettings.getChangeLayoutAllowed() != null);
        }
    }
    
    /**
     * 获取会场权重
     * @author lilinhai
     * @since 2021-02-09 13:38 
     * @return AttendeeImportance
     */
    private AttendeeImportance getImportance()
    {
        if (attendee == conferenceContext.getMasterAttendee())
        {
            return AttendeeImportance.MASTER;
        }
        return AttendeeImportance.COMMON;
    }
}
