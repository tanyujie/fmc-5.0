package com.paradisecloud.fcm.fme.model.response.outdialplan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 outboundDialPlanRules 响应类
 */
@Getter
@Setter
@ToString
public class OutboundDialPlanRulesResponse
{
    
    /**
     * 多个 callProfiles
     */
    private ActiveOutboundDialPlanRulesResponse outboundDialPlanRules;
    
}
