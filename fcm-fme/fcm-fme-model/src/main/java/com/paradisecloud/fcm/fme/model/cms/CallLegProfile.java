package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callLeg 配置文件
 *
 * @author zt1994 2019/8/16 10:51
 */
@Getter
@Setter
@ToString
public class CallLegProfile
{
    
    private String id;
    
    /**
     * 是否需要激活 默认 false
     */
    private Boolean needsActivation;
    
    /**
     * 默认布局 allEqual| speakerOnly| telepresence| stacked| allEqualQuarters| allEqualNinths|
     * allEqualSixteenths| allEqualTwentyFifths| onePlusFive| onePlusSeven| onePlusNine| automatic|
     * onePlusN
     */
    private String defaultLayout;
    
    /**
     * 如果设置为true，则允许使用此callLegProfile的所有分支更改其在SIP端点上的屏幕布局
     */
    private Boolean changeLayoutAllowed;
    
    /**
     * 如果设置为true，使用此callLegProfile的call legs将在其视频窗格上显示参与者窗格标签
     */
    private Boolean participantLabels;
    
    /**
     * dualStream| singleStream singleStream 提供一个单独的合成内容+视频BFCP流，而不是将输出内容放在单独的流中
     */
    private String presentationDisplayMode;
    
    /**
     * 如果设置为true，则允许使用此callLegProfile的callLeg共享内容
     */
    private Boolean presentationContributionAllowed;
    
    /**
     * 如果设置为true，则允许使用此callLegProfile的callLeg查看共享内容
     */
    private Boolean presentationViewingAllowed;
    
    /**
     * 如果设置为true，则允许使用此callLegProfile的callLeg停止它们正在进行的call
     */
    private Boolean endCallAllowed;
    
    /**
     * 如果设置为true，则允许使用此callLegProfile的callLeg允许断开其他参与者的连接，通常通过via活动控制
     */
    private Boolean disconnectOthersAllowed;
    
    /**
     * 如果设置为true，则允许使用此callLegProfile的callLeg允许添加其他参与者，通常通过via活动控制
     */
    private Boolean addParticipantAllowed;
    
    /**
     * 默认false 如果设置为true，则允许使用此callLegProfile的callLeg允许静音或取消其他参与者的音频
     */
    private Boolean muteOthersAllowed;
    
    /**
     * 默认true 如果设置为true，则允许使用此callLegProfile的callLeg允许静音或不静音(开始/停止)其他参与者的视频
     */
    private Boolean videoMuteOthersAllowed;
    
    /**
     * 默认false 如果设置为true，则允许使用此callLegProfile的callLeg允许静音或不静音自己的音频
     */
    private Boolean muteSelfAllowed;
    
    /**
     * 默认true 如果设置为true，则允许使用此callLegProfile的callLeg允许静音或不静音(开始/停止)自己的视频
     */
    private Boolean videoMuteSelfAllowed;
    
    private Integer joinToneParticipantThreshold;
    
    private Integer leaveToneParticipantThreshold;
    
    /**
     * 增益
     */
    private String audioGainMode;
    
    /**
     * auto | disabled 是否自动播放视频
     */
    private String videoMode;
    
    /**
     * true | false 如果为true，其他callLeg将不会收到该callLeg的音频信息
     */
    private Boolean rxAudioMute;
    
    /**
     * true | false 如果为true，该callLeg被静音
     */
    private Boolean txAudioMute;
    
    /**
     * true | false 如果为true，其他callLeg将不会收到该callLeg的视频信息
     */
    private Boolean rxVideoMute;
    
    /**
     * true | false 如果为true，该callLeg收不到其他callLeg的视频信息
     */
    private Boolean txVideoMute;
    
    /**
     * optional| required| prohibited 与web管理界面设置相同
     */
    private String sipMediaEncryption;
    
    /**
     * 输出音频流的首选包大小的数值
     */
    private Integer audioPacketSizeMs;
    
    /**
     * deactivate| disconnect| remainActivated
     */
    private String deactivationMode;
    
    private Integer deactivationModeTime;
    
    private Boolean telepresenceCallsAllowed;
    
    /**
     * 如果为true，则允许使用此callLegProfile的callLeg执行演示视频通道的操作
     */
    private Boolean sipPresentationChannelEnabled;
    
    /**
     * serverOnly| serverAndClient
     */
    private String bfcpMode;
    
    /**
     * 是否允许锁定call
     */
    private Boolean callLockAllowed;
    
    /**
     * 默认 false 是否允许设置视频权重
     */
    private Boolean setImportanceAllowed;
    
    /**
     * 是否允许将所有callLeg开麦闭麦
     */
    private Boolean allowAllMuteSelfAllowed;
    
    private Boolean allowAllPresentationContributionAllowed;
    
    private Boolean changeJoinAudioMuteOverrideAllowed;
    
    /**
     * 远端镜头控制
     */
    private Boolean controlRemoteCameraAllowed;
    
    /**
     * 是否允许此callLeg开始和停止录制
     */
    private Boolean recordingControlAllowed;
    
    /**
     * 是否允许此callLeg开始和停止直播
     */
    private Boolean streamingControlAllowed;
    
    /**
     * callLegProfile的名称
     */
    private String name;
    
    /**
     * 此callLeg存在最大时间
     */
    private Integer maxCallDurationTime;
    
    /**
     * unrestricted| max1080p30| max720p30| max480p30 在限制转码资源的基础上，限制此callLeg的最大协商视频呼叫质量
     */
    private String qualityMain;
    
    /**
     * unrestricted | max1080p30 | max720p5 在限制转码资源的基础上，限制此callLeg的最大协商演示视频呼叫质量
     */
    private String qualityPresentation;
    
    /**
     * never| auto| always 控制屏幕上参与者计数器的行为，是否显示
     */
    private String participantCounter;
}
