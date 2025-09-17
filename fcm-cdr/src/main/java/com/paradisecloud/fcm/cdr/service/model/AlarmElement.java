package com.paradisecloud.fcm.cdr.service.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 22:22
 **/
@Getter
@Setter
@ToString
public class AlarmElement
{
    // @JSONField(name = "@type")
    private String type;
    
    // @JSONField(name = "@durationPercentage")
    private BigDecimal durationPercentage;
}
