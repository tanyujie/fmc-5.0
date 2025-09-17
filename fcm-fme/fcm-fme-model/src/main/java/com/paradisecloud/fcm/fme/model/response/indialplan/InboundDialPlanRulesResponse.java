package com.paradisecloud.fcm.fme.model.response.indialplan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个 outboundDialPlanRules 响应类
 */
@Getter
@Setter
@ToString
public class InboundDialPlanRulesResponse
{
    
    /**
     * 多个 callProfiles
     */
    private ActiveInboundDialPlanRulesResponse inboundDialPlanRules;
    
}
