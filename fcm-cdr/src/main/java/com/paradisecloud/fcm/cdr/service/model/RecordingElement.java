package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 22:55
 **/
@Getter
@Setter
@ToString
public class RecordingElement
{
    
    // @JSONField(name = "@id")
    private String id;
    
    private String path;
    
    private String recorderUri;
    
    private String call;
    
    private String callLeg;
}
