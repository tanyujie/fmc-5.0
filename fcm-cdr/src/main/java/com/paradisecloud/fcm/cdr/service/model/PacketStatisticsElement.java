package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 22:37
 **/
@Getter
@Setter
@ToString
public class PacketStatisticsElement
{
    
    private PacketInfo packetLossBursts;
    
    private PacketInfo packetGap;
}
