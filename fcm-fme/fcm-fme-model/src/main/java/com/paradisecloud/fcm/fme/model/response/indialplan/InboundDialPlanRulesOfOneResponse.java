package com.paradisecloud.fcm.fme.model.response.indialplan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 单个 callProfiles
 */
@Getter
@Setter
@ToString
public class InboundDialPlanRulesOfOneResponse
{
    
    /**
     * 单个 outboundDialPlanRules
     */
    private ActiveInboundDialPlanRulesOfOneResponse inboundDialPlanRules;
}
