package com.paradisecloud.fcm.fme.model.response.cdr;

import com.paradisecloud.fcm.fme.model.cms.CdrReceiver;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * cdrReceiver 详情响应类
 */
@Getter
@Setter
@ToString
public class CdrReceiverInfoResponse
{
    
    /**
     * 单个 cdrReceiver
     */
    private CdrReceiver cdrReceiver;
}
