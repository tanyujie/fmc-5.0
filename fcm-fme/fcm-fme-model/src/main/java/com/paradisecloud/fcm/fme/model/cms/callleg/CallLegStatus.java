package com.paradisecloud.fcm.fme.model.cms.callleg;

import java.util.ArrayList;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 状态信息
 *
 * @author zt1994 2019/8/26 11:56
 */
@Getter
@Setter
@ToString
public class CallLegStatus
{
    
    /**
     * initial|ringing|connected|onHold
     */
    private String state;
    
    /**
     * 终端入会持续时间 s
     */
    private Integer durationSeconds;
    
    /**
     * incoming | outgoing
     */
    private String direction;
    
    /**
     * sip call id
     */
    private String sipCallId;
    
    /**
     * group id
     */
    private String groupId;
    
    /**
     * true| false
     */
    private Boolean recording;
    
    /**
     * true | false
     */
    private Boolean streaming;
    
    /**
     * 无效的
     */
    private Boolean deactivated;
    
    /**
     * 是否加密
     */
    private Boolean encryptedMedia;
    
    /**
     * 是否不加密
     */
    private Boolean unencryptedMedia;
    
    /**
     * 布局
     */
    private String layout;
    
    /**
     * 活跃布局
     */
    private String activeLayout;
    
    /**
     * 可用的视频流
     */
    private Integer availableVideoStreams;
    
    /**
     * 上行音频
     */
    private CallLegStatusRxAudio rxAudio;
    
    /**
     * 下行音频
     */
    private CallLegStatusTxAudio txAudio;
    
    /**
     * 上行视频
     */
    @JSONField(name = "rxVideo")
    private ArrayList<CallLegStatusRxVideo> rxVideo;
    
    /**
     * 下行视频
     */
    @JSONField(name = "txVideo")
    private ArrayList<CallLegStatusTxVideo> txVideo;
    
}
