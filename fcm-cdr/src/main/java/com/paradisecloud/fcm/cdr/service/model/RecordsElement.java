package com.paradisecloud.fcm.cdr.service.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description
 * @Author johnson liu
 * @Date 2021/6/30 21:19
 **/
@Getter
@Setter
@ToString
public class RecordsElement
{
    // @JSONField(name = "@session")
    private String session;
    
    // @JSONField(name = "@callBridge")
    private String callBridge;
    
    private List<RecordElement> record;
}
