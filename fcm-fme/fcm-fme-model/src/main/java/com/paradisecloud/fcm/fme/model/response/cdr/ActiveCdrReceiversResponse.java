package com.paradisecloud.fcm.fme.model.response.cdr;

import java.util.List;

import com.paradisecloud.fcm.fme.model.cms.CdrReceiver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个cdrReceiver
 */
@Getter
@Setter
@ToString
public class ActiveCdrReceiversResponse
{
    
    /**
     * cdrReceiver 总数
     */
    private Integer total;
    
    /**
     * 单个cdrReceivers
     */
    private List<CdrReceiver> cdrReceiver;
}
