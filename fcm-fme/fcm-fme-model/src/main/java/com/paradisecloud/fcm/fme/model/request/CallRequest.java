package com.paradisecloud.fcm.fme.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call 创建请求类
 *
 * @author zt1994 2019/8/23 15:55
 */
@Getter
@Setter
@ToString
public class CallRequest
{
    
    /**
     * coSpace 唯一id
     */
    @JSONField(name = "@total")
    private String id;
    
    /**
     * 如果调用表示coSpace的实例化，则该值将出现并保存coSpace的id
     */
    private String coSpace;
    
    /**
     * call 可视名称
     */
    private String name;
    
    /**
     * 在此调用的所有分布式实例中具有相同的id。
     */
    private String callCorrelator;
    
    /**
     * 指示调用是否锁定(true)或未锁定(false)。
     */
    private Boolean locked;
    
    /**
     * 如果为true，则为录制
     */
    private Boolean recording;
    
    /**
     * 如果为true，则为直播
     */
    private Boolean streaming;
    
    /**
     * 如果是真的，参与者有权自己静音和不静音
     */
    private Boolean allowAllMuteSelf;
    
    /**
     * 如果是真的，参与者有权发言。如果为false，则此权限取决于callLegProfile中允许的presentationcontribution。默认的是 false
     */
    private Boolean allowAllPresentationContribution;
    
    /**
     * 如果为true，新参与者在加入电话会议时将静音 如果为false，新参与者在加入电话会议时将取消静音 如果未设置，新参与者将使用来自callLegProfile的音频静音值 call
     */
    private Boolean joinAudioMuteOverride;
    
    /**
     * 要显示给call中的每个参与者的文本(仅在配置messageDuration为非零时才显示)
     */
    private String messageText;
    
    /**
     * top|middle|bottom 在屏幕上显示配置的messageText的位置(for SIP终端)
     */
    private String messagePosition;
    
    /**
     * 在屏幕上显示配置的messageText的时间(以秒为单位)。键入字符串permanent将导致该字符串被永久显示，直到重新配置为止。
     */
    private String messageDuration;
    
    /**
     * 如果为真，则当没有参与者时，此调用被认为是“active for load balanced”。 这意味着对空会议的第一个调用优先负载平衡。通过将此参数设置为false，可以优先使用空会议来防止负载平衡。
     * 如果在创建(POST)操作中没有提供此参数，则默认为“true”。
     */
    private Boolean activeWhenEmpty;
    
}
