package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 配置参数类
 *
 * @author zt1994 2019/8/26 11:52
 */
@Getter
@Setter
@ToString
public class CallLegConfiguration
{
    
    /**
     * 如果提供，必须是会议的ID，将服务器与此调用段关联。当稍后查询调用分支时，会议服务器将返回这个值，因此它应该是一个对请求者有意义的值。
     */
    private String ownerId;
    
    /**
     * 此参数覆盖此调用分支的默认布局。
     */
    private String chosenLayout;
    
    /**
     * 是否需要激活
     */
    private Boolean needsActivation;
    
    /**
     * 默认布局
     */
    private String defaultLayout;
    
    /**
     * 如果设置为“true”，使用此call leg配置文件的call legs将在其视频窗格上显示参与者窗格标签
     */
    private Boolean participantLabels;
    
    /**
     * dualStream singleStream 提供单一的合成内容+视频BFCP流，而不是将输出内容放在单独的流中
     */
    private String presentationDisplayMode;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile 的 call leg 共享内容
     */
    private Boolean presentationContributionAllowed;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile的 call leg 查看共享内容
     */
    private Boolean presentationViewingAllowed;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile 的 call leg 结束它们正在参与的 call
     */
    private Boolean endCallAllowed;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile 的 call leg 静音或不静音其他参与者的音频
     */
    private Boolean muteOthersAllowed;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile 的 call leg 静音或不静音(开始/停止)其他参与者的视频
     */
    private Boolean videoMuteOthersAllowed;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile 的 call leg 静音或不静音自己的音频。
     */
    private Boolean muteSelfAllowed;
    
    /**
     * 如果为真，则允许使用此 call Leg Profile 的 call leg 静音或不静音(开始/停止)自己的视频
     */
    private Boolean videoMuteSelfAllowed;
    
    /**
     * 是否允许修改布局
     */
    private Boolean changeLayoutAllowed;
    
    private Boolean callLockAllowed;
    
    /**
     * join tone 的最大数量
     */
    private Integer joinToneParticipantThreshold;
    
    /**
     * 人数达到多少，会播放 leave tone
     */
    private Integer leaveToneParticipantThreshold;
    
    /**
     * auto|disabled 不会播放任何主流视频。对于在主视频流中显示内容的设备，在适当的时候，主视频流中将不显示任何参与视频的内容。
     */
    private String videoMode;
    
    /**
     * true 其他参与者将不会听到呼叫腿使用此call leg profile的音频
     */
    private Boolean rxAudioMute;
    
    /**
     * true 被静音
     */
    private Boolean txAudioMute;
    
    /**
     * true 使用此call leg profile的call leg的(“相机”)视频将不会被其他参与者看到。
     */
    private Boolean rxVideoMute;
    
    /**
     * true 视频流不发送
     */
    private Boolean txVideoMute;
    
    /**
     * optional|required|prohibited
     */
    private String sipMediaEncryption;
    
    /**
     * 输出音频流的首选包大小的数值(以毫秒为单位，默认值为20ms)
     */
    private Integer audioPacketSizeMs;
    
    /**
     * needsActivation操作在最后一个activator离开时调用legs
     */
    private String deactivationMode;
    
    /**
     * 最后一秒后的秒数activator在执行失活模式操作之前离开
     */
    private Integer deactivationModeTime;
    
    /**
     * 如果为真，则允许使用此调用腿配置文件的调用腿生成TIP(远程呈现互操作性协议)调用
     */
    private Boolean telepresenceCallsAllowed;
    
    /**
     * 如果为真，则允许使用此调用分支配置文件的调用分支执行表示视频通道操作
     */
    private Boolean sipPresentationChannelEnabled;
    
    /**
     * 如果为SIP调用启用了表示视频通道操作，则此设置确定调用桥的BFCP行为
     */
    private String bfcpMode;
    
    /**
     * 如果为真，则允许使用此调用分支配置文件的调用分支断开其他参与者的连接，通常是通过Active控制
     */
    private Boolean disconnectOthersAllowed;
    
    /**
     * 如果为真，则允许使用此调用腿部概要文件的调用腿部添加其他参与者，通常通过Active控制
     */
    private Boolean addParticipantAllowed;
    
    /**
     * 是否允许使用此call leg profile的call leg更改显示所有call leg的权限
     */
    private Boolean allowAllPresentationContributionAllowed;
    
    /**
     * recording 控制
     */
    private Boolean recordingControlAllowed;
    
    /**
     * streaming 控制
     */
    private Boolean streamingControlAllowed;
    
    /**
     * unrestricted|max1080p30|max720p30|max480p30 主流视频质量
     */
    private String qualityMain;
    
    /**
     * unrestricted|max1080p30|max720p30|max480p30 辅流视频质量
     */
    private String qualityPresentation;
    
    /**
     * 控制屏幕上参与者计数器的行为
     */
    private String participantCounter;
    
    /**
     * 覆盖标签名称
     */
    private String nameLabelOverride;
    
    
    private Boolean controlRemoteCameraAllowed;
    
    private String layoutTemplate;
    
    private String audioGainMode;
    
    private String meetingTitlePosition;
    
    private String handStatus;
    
    private Boolean chatContributionAllowed;
    
}
