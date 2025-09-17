package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 22:34
 **/
@Getter
@Setter
@ToString
public class MediaInfoElement
{
    
    private String codec;
    
    private Integer maxSizeWidth;
    
    private Integer maxSizeHeight;
    
    private PacketStatisticsElement packetStatistics;
}
