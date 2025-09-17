package com.paradisecloud.fcm.cdr.service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description record节点
 * @Author johnson liu
 * @Date 2021/6/30 20:59
 **/
@Getter
@Setter
@ToString
public class RecordElement
{
    
    // @JSONField(name = "@type")
    private String type;
    
    // @JSONField(name = "@time")
    private String time;
    
    // @JSONField(name = "@recordIndex")
    private Integer recordIndex;
    
    // @JSONField(name = "@correlatorIndex")
    private Integer correlatorIndex;
    
    // @JSONField(name = "@numPreceedingRecordsMissing")
    private Integer numPreceedingRecordsMissing;
    
    private CallElement call;
    
    private CallLegElement callLeg;
    
    private RecordingElement recording;
}
