package com.paradisecloud.fcm.fme.model.response.outdialplan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 callProfiles
 */
@Getter
@Setter
@ToString
public class OutboundDialPlanRulesOfOneResponse
{
    
    /**
     * 单个 outboundDialPlanRules
     */
    private ActiveOutboundDialPlanRulesOfOneResponse outboundDialPlanRules;
}
