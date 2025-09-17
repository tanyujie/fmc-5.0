package com.paradisecloud.fcm.fme.model.cms;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call 配置实体类
 *
 * @author zt1994 2019/8/23 11:46
 */
@Getter
@Setter
@ToString
public class CallProfile
{
    
    /**
     * 唯一id
     */
    private String id;
    
    /**
     * 设置呼叫的最大参与者数量，可以同时激活的最大数量，新参加者不得超过此限额
     */
    private Integer participantLimit;
    
    /**
     * 是否允许使用留言板
     */
    private Boolean messageBoardEnabled;
    
    /**
     * 是否锁定
     */
    private Boolean locked;
    
    /**
     * 会议锁定模式
     */
    private String lockMode;
    
    /**
     * recorder 控制状态 disabled 不能录制 manual 可以控制开启和关闭录制 automatic 自动开始录制，无法控制
     */
    private String recordingMode;
    
    /**
     * stream 控制状态 disabled 不能直播 manual 可以控制开启和关闭直播 automatic 自动开始直播，无法控制
     */
    private String streamingMode;
    
    /**
     * 密码控制 required 需要输入密码，空白密码需要显式输入 timeout 在一段时间没有输入密码之后，将其解释为一个空白密码。超时量由值决定 passcodeTimeout
     */
    private String passcodeMode;
    
    /**
     * numeric 如果指定，这是调用桥接器在将密码解释为空白密码之前等待的时间
     */
    private String passcodeTimeout;
    
    /**
     * true 出站网关call Leg将是音频的，如果入站call leg 只有音频
     */
    private Boolean gatewayAudioCallOptimization;
    
    /**
     * 定义在将参与者连接到 Lync会议时调用桥的行为 dualHomeCluster 所有 call bridges 将共享相同会议 dualHomeCallBridge 每个 call
     * bridges将主办他们自己的会议，每个都将连接到 AVMCU gateway 每个参与者将有专用连接到Lync AVMCU服务器
     */
    private String lyncConferenceMode;
    
    /**
     * 标题栏
     */
    private String messageBannerText;
    
    /**
     * 静音行为
     */
    private String muteBehavior;
    
    /**
     * 录制的URI
     */
    private String sipRecorderUri;
    
    /**
     * 直播的URI
     */
    private String sipStreamerUri;
    
    private Boolean chatAllowed;
    
    private Boolean raiseHandEnabled;
}
