package com.paradisecloud.fcm.fme.model.response.cdr;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 callProfiles
 */
@Getter
@Setter
@ToString
public class CdrReceiversOfOneResponse
{
    
    /**
     * 单个 cdrReceivers
     */
    private ActiveCdrReceiversOfOneResponse cdrReceivers;
}
