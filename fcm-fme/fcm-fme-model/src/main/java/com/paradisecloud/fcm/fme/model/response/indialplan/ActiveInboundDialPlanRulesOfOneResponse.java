package com.paradisecloud.fcm.fme.model.response.indialplan;

import com.paradisecloud.fcm.fme.model.cms.InboundDialPlanRule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多个callProfile
 *
 */
@Getter
@Setter
@ToString
public class ActiveInboundDialPlanRulesOfOneResponse
{
    
    /**
     * inboundDialPlanRule 总数
     */
    private Integer total;
    
    /**
     * 单个inboundDialPlanRule
     */
    private InboundDialPlanRule inboundDialPlanRule;
}
