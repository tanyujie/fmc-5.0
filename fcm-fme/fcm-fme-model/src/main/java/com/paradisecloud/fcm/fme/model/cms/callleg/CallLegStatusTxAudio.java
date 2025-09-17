package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 状态 下行音频
 *
 * @author zt1994 2019/8/26 13:50
 */
@Getter
@Setter
@ToString
public class CallLegStatusTxAudio
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
     * 比特率
     */
    private Integer codecBitRate;
    
    /**
     * 增益
     */
    private String gainApplied;
    
    /**
     * 带宽
     */
    private Integer bitRate;
    
    /**
     * 丢包率
     */
    private Integer packetLossPercentage;
}
