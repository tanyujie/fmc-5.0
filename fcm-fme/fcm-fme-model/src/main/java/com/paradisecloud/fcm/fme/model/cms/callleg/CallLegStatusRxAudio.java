package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 状态 上行音频
 *
 * @author zt1994 2019/8/26 13:46
 */
@Getter
@Setter
@ToString
public class CallLegStatusRxAudio
{
    
    /**
     * 音频编码
     */
    private String codec;
    
    /**
     * 延迟
     */
    private Integer jitter;
    
    /**
     * 回环时间
     */
    private Integer roundTripTime;
    
    /**
     * 增益
     */
    private String gainApplied;
    
    /**
     * 带宽
     */
    private Integer bitRate;
    
    /**
     * 此值用于音频编解码器类型，其变体只能通过比特率来区分
     */
    private Integer codecBitRate;
    
    /**
     * 丢包率
     */
    private Integer packetLossPercentage;
    
}
