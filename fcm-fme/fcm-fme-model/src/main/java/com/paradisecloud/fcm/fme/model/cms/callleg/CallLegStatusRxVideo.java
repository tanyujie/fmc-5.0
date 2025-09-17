package com.paradisecloud.fcm.fme.model.cms.callleg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * call leg 状态 上行视频
 *
 * @author zt1994 2019/8/26 13:54
 */
@Getter
@Setter
@ToString
public class CallLegStatusRxVideo
{
    
    /**
     * 视频流的类型:主视频流或辅流
     */
    private String role;
    
    /**
     * 编码格式
     */
    private String codec;
    
    /**
     * 分辨率 width * height
     */
    private Integer width;
    
    private Integer height;
    
    /**
     * 帧率
     */
    private Integer frameRate;
    
    /**
     * 通话延迟
     */
    private Integer jitter;
    
    /**
     * 回环时间
     */
    private Integer roundTripTime;
    
    /**
     * 带宽
     */
    private Integer bitRate;
    
    /**
     * 丢包率
     */
    private Integer packetLossPercentage;
    
}
