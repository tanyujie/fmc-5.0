package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 22:38
 **/
@Getter
@Setter
@ToString
public class PacketInfo
{
    
    private BigDecimal duration;
    
    private BigDecimal density;
}
