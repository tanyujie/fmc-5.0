package com.paradisecloud.fcm.fme.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 请求类
 *
 * @author zt1994 2019/8/27 9:42
 */
@Getter
@Setter
@ToString
public class CallLegRequest
{
    
    /**
     * 仅用于POST 指定调用分支的地址;这可以是一个SIP URI、一个电话号码或一个用户JID来邀请该用户进行调用
     */
    private String remoteParty;
    
    /**
     * 仅用于POST，如果提供，则设置调用段的带宽，单位为比特/秒(例如2Mbit/s为2000000)。如果没有提供，将使用call bridge配置的值
     */
    private Integer bandwidth;
    
    /**
     * 仅对POST(如果提供)，这将覆盖是否需要远程方确认才能加入call的自动选择。 true—始终需要远程方的确认;这通常采用语音提示的形式，要求用户按下一个键才能加入。
     * false—永远不需要远程方的确认;远程方在接受incoming call时将被加入到coSpace中
     */
    private Boolean confirmation;
    
    /**
     * 如果提供，则必须是会议服务器的ID，以便与此调用分支(call leg)相关联。 当稍后查询调用分支时，会议服务器将返回这个值，因此它应该是一个对请求者有意义的值
     */
    private String ownerId;
    
    /**
     * 此参数覆盖此调用分支的默认布局
     */
    private String chosenLayout;
    
    /**
     * 一串DTMF键按下命令发送到远端，无论是在调用段初始连接时，还是在调用期间。 在提供的序列中，可以使用数字0到9、*和#，以及一个或多个逗号字符(“，”)，它们在数字之间添加停顿。
     */
    private String dtmfSequence;
    
    /**
     * 如果提供，则将指定的调用分支配置文件与此调用分支关联。您还可以为调用分支概要文件的所有参数提供单独的值，以覆盖该调用分支概要文件的值。
     */
    private String callLegProfile;
    
    /**
     * 是否需要激活
     */
    private Boolean needsActivation;
    
    /**
     * 默认布局
     */
    private String defaultLayout;
    
    /**
     * 与会者标签
     */
    private Boolean participantLabels;
    
    /**
     * dualStream|singleStream singleStream提供一个单独的合成内容+视频BFCP流，而不是将输出内容放在单独的流中
     */
    private String presentationDisplayMode;
    
    /**
     * 如果为真，则允许使用此调用分支配置文件的调用分支共享内容
     */
    private Boolean presentationContributionAllowed;
    
    /**
     * 如果为真，则允许使用此调用分支配置文件的调用分支查看共享内容
     */
    private Boolean presentationViewingAllowed;
    
    /**
     * 如果为真，则允许使用此调用腿部概要文件的调用腿部结束它们正在参与的调用
     */
    private Boolean endCallAllowed;
    
    /**
     * 允许其他call leg控制是否静音
     */
    private Boolean muteOthersAllowed;
    
    /**
     * 允许其他call leg控制是否开启视频
     */
    private Boolean videoMuteOthersAllowed;
    
    /**
     * 是否允许自己静音
     */
    private Boolean muteSelfAllowed;
    
    /**
     * 是否允许自己控制视频
     */
    private Boolean videoMuteSelfAllowed;
    
    /**
     * 是否允许修改布局
     */
    private Boolean changeLayoutAllowed;
    
    /**
     * 播放“join tone”的SIP端点的参与者数量;
     */
    private Integer joinToneParticipantThreshold;
    
    /**
     * 人数达到多少，会播放 leave tone
     */
    private Integer leaveTonearticipanthreshold;
    
    /**
     * auto|disabled 不会播放任何主流视频。对于在主视频流中显示内容的设备，在适当的时候，主视频流中将不显示任何参与视频的内容。
     */
    private Boolean videoMode;
    
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
     * 布局 这个参数是为了与早期版本的Acano server向后兼容而提供的——它具有与更改“defaultLayout”相同的功能， 如果两者都提供，那么“defaultLayout”将优先
     */
    private String layout;
    
    /**
     * 如果为真，则允许使用此调用分支配置文件的调用分支断开其他参与者的连接，通常是通过Active控制
     */
    private Boolean disconnectOthersAllowed;
    
    /**
     * 如果为真，则允许使用此调用腿部概要文件的调用腿部添加其他参与者，通常通过Active控制
     */
    private Boolean addParticipantAllowed;
    
    /**
     * 在限制转码资源的基础上，限制此呼叫段的最大协商主视频呼叫质量。使用典型分辨率和帧率指定。 注意，由于端点限制或整体呼叫桥接负载，呼叫分支可能在较低的分辨率或帧率下运行。
     * unrestricted max1080p30 max720p30 max480p30
     */
    private String qualityMain;
    
    /**
     * 在限制转码资源的基础上，限制此呼叫段的最大协商表示视频呼叫质量。使用典型分辨率和帧速率指定。这只影响使用单独表示流的分支。 unrestricted max1080p30 max720p5
     */
    private String qualityPresentation;
    
    /**
     * 控制屏幕上参与者计数器的行为 never auto always
     */
    private String participantCounter;
    
    /**
     * 如果提供，则覆盖此调用分支的名称。设置空字符串将清除该值并恢复原始名称。覆盖参与者的名称及其相关的调用分支是可互换的， 并且两者都有影响;最新的更改优先。它将参与者的姓名更改为:其他会议参与者查看的屏幕名称标签、ActiveControl名册列表、
     * 会议应用程序在呼叫中看到参与者姓名的任何地方、CDR记录，其中姓名出现在web界面中。
     */
    private String nameLabelOverride;
    
}
