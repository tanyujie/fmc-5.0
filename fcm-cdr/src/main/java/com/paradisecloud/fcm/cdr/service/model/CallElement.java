package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description Call节点
 * @Author johnson liu
 * @Date 2021/6/30 21:03
 **/
@Getter
@Setter
@ToString
public class CallElement
{
    
    // @JSONField(name = "@id")
    private String id;
    
    private String name;
    
    private String coSpace;
    
    private String ownerName;
    
    private String tenant;
    
    private String cdrTag;
    
    private String callType;
    
    private String callCorrelator;
    
    private Integer callLegsCompleted;
    
    private Integer callLegsMaxActive;
    
    private Integer durationSeconds;
}
