package com.paradisecloud.fcm.fme.model.response.outdialplan;

import com.paradisecloud.fcm.fme.model.cms.OutboundDialPlanRule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * callProfile 详情响应类
 */
@Getter
@Setter
@ToString
public class OutboundDialPlanRuleInfoResponse
{
    
    /**
     * 单个 callProfile
     */
    private OutboundDialPlanRule outboundDialPlanRule;
}
