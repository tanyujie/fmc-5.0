package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 22:19
 **/
@Getter
@Setter
@ToString
public class MediaUsagePercentagesElement
{
    
    private BigDecimal mainVideoViewer;
    
    private BigDecimal mainVideoContributor;

    private BigDecimal presentationViewer;

    private BigDecimal presentationContributor;
}
