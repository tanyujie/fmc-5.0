package com.paradisecloud.fcm.fme.model.response.outdialplan;

import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;

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
public class ActiveOutboundDialPlanRulesOfOneResponse
{
    
    /**
     * outboundDialPlanRule 总数
     */
    private Integer total;
    
    /**
     * 单个outboundDialPlanRule
     */
    private OutboundDialPlanRule outboundDialPlanRule;
}
