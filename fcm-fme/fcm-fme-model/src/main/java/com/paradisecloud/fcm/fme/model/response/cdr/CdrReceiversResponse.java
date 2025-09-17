package com.paradisecloud.fcm.fme.model.response.cdr;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 callProfiles 响应类
 */
@Getter
@Setter
@ToString
public class CdrReceiversResponse
{
    
    /**
     * 多个 cdrReceivers
     */
    private ActiveCdrReceiversResponse cdrReceivers;
    
}
